/*
 * $Id$
 */
package org.xins.util.ant.sourceforge;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;


/**
 * Apache Ant task that creates a new XINS release at the SourceForge site.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.127
 */
public class AddReleaseTask extends Task {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   private static final String FTP_SERVER = "upload.sourceforge.net";
   private static final String FTP_DIR = "incoming";


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>AddReleaseTask</code>.
    */
   public AddReleaseTask() {
      _httpClient = new HttpClient();
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The SourceForge user to log in as.
    */
   private String _user;

   /**
    * The password for the SourceForge user to log in as.
    */
   private String _password;

   /**
    * Flag that indicates if upload failures should be ignored.
    */
   private boolean _ignoreUploadFailures;

   /**
    * The file to upload.
    */
   private String _file;

   /**
    * The SourceForge group ID for the package.
    */
   private String _groupID;

   /**
    * The SourceForge ID for the package.
    */
   private String _packageID;

   /**
    * The SourceForge release name.
    */
   private String _releaseName;

   /**
    * The location of the keystore file.
    */
   private String _keystore;

   /**
    * The <code>HttpClient</code> object to use.
    */
   private HttpClient _httpClient;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Indicates if upload failures should be ignored.
    *
    * @param cond
    *    condition that indicates if upload failures should be ignored.
    */
   public void setIgnoreUploadFailures(boolean cond) {
      _ignoreUploadFailures = cond;
   }

   /**
    * Sets the SourceForge user to log in as. Neither <code>null</code> nor
    * <code>""</code> are valid.
    *
    * @param user
    *    the SourceForge user.
    */
   public void setUser(String user) {
      if (user == null || user.length() < 1) {
         _user = null;
      } else {
         _user = user;
      }
   }

   /**
    * Sets the password for the SourceForge user to log in as. Neither
    * <code>null</code> nor <code>""</code> are valid.
    *
    * @param password
    *    the password for the SourceForge user.
    */
   public void setPassword(String password) {
      if (password == null || password.length() < 1) {
         _password = null;
      } else {
         _password = password;
      }
   }

   /**
    * Sets the file to upload. Neither <code>null</code> nor <code>""</code>
    * are valid.
    *
    * @param file
    *    the path to the file to upload.
    */
   public void setFile(String file) {
      if (file == null || file.length() < 1) {
         _file = null;
      } else {
         _file = file;
      }
   }

   /**
    * Sets the SourceForge group ID. Neither <code>null</code> nor
    * <code>""</code> are valid.
    *
    * @param groupID
    *    the SourceForge group ID.
    */
   public void setGroup(String groupID) {
      if (groupID == null || groupID.length() < 1) {
         _groupID = null;
      } else {
         _groupID = groupID;
      }
   }

   /**
    * Sets the SourceForge package ID. Neither <code>null</code> nor
    * <code>""</code> are valid.
    *
    * @param groupID
    *    the SourceForge package ID.
    */
   public void setPackage(String packageID) {
      if (packageID == null || packageID.length() < 1) {
         _packageID = null;
      } else {
         _packageID = packageID;
      }
   }

   /**
    * Sets the SourceForge release name. Neither <code>null</code> nor
    * <code>""</code> are valid. Normally something like <code>"0.15"</code>.
    *
    * @param releaseName
    *    the name for this release.
    */
   public void setRelease(String releaseName) {
      if (releaseName == null || releaseName.length() < 1) {
         _releaseName = null;
      } else {
         _releaseName = releaseName;
      }
   }

   /**
    * Sets the location of the keystore file. Neither <code>null</code> nor
    * <code>""</code> are valid.
    *
    * @param keystore
    *    the location of the keystore file.
    */
   public void setKeystore(String keystore) {
      if (keystore == null || keystore.length() < 1) {
         _keystore = null;
      } else {
         _keystore = keystore;
      }
   }

   /**
    * Called by the project to let the task do its work.
    *
    * @throws BuildException
    *    if something goes wrong with the build.
    */
   public void execute() throws BuildException {

      // Check preconditions
      if (_file == null) {
         throw new BuildException("The file to upload must be set, but it is not.");
      } else if (_user == null) {
         throw new BuildException("The SourceForge user must be set, but it is not.");
      } else if (_password == null) {
         throw new BuildException("The password for the SourceForge user must be set, but it is not.");
      } else if (_groupID == null) {
         throw new BuildException("The SourceForge group ID must be set, but it is not.");
      } else if (_releaseName == null) {
         throw new BuildException("The SourceForge release name must be set, but it is not.");
      } else if (_packageID == null) {
         throw new BuildException("The SourceForge package ID must be set, but it is not.");
      } else if (_keystore == null) {
         throw new BuildException("The keystore location must be set, but it is not.");
      }

      // Upload the file
      if (_ignoreUploadFailures) {
         try {
            uploadFile();
         } catch (BuildException e) {
            log("Ignoring build error: " + e.getMessage());
         }
      } else {
         uploadFile();
      }

      // Use our own keystore
      log("Using keystore file \"" + _keystore + "\".");
      System.setProperty("javax.net.ssl.trustStore", _keystore);

      // Login to SourceForge site
      disableCertificateValidation();
      login();

      // Create a new release
      createRelease();
   }

   private void disableCertificateValidation() {

      // Create a trust manager that does not validate certificate chains
      TrustManager[] trustAllCerts = new TrustManager[] {
         new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
               return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
               // empty
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
               // empty
            }
         }
      };
    
      // Install the all-trusting trust manager
      try {
         log("Disabling SSL certificate validation.", Project.MSG_VERBOSE);
         SSLContext sc = SSLContext.getInstance("SSLv3");
         sc.init(null, trustAllCerts, new SecureRandom());
         HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
         log("Disabled SSL certificate validation.", Project.MSG_DEBUG);
      } catch (Exception e) {
         throw new BuildException("Error while disabling SSL certificate validation.", e);
      }
   }

   private void uploadFile() throws BuildException {

      File f = new File(_file);

      // Create stream to file
      FileInputStream in;
      String fileName;
      try {
         in = new FileInputStream(f);
         fileName = f.getName();
      } catch (FileNotFoundException fnf) {
         throw new BuildException("Unable to open file \"" + _file + "\".");
      }

      FTPClient ftp = new FTPClient();

      try {
         // Connect
         log("Connecting to FTP server \"" + FTP_SERVER + "\".", Project.MSG_VERBOSE);
         ftp.connect(FTP_SERVER);
         log("Connected to FTP server \"" + FTP_SERVER + "\".", Project.MSG_DEBUG);
         log("FTP server \"" + FTP_SERVER + "\" returned reply string \"" + ftp.getReplyString() + "\".", Project.MSG_DEBUG);

         // Check reply code to verify success
         int reply = ftp.getReplyCode();
         if(! FTPReply.isPositiveCompletion(reply)) {
            throw new BuildException("FTP server \"" + FTP_SERVER + "\" refused connection.");
         }

         // Login
         log("Logging in as user \"anonymous\".", Project.MSG_DEBUG);
         ftp.login("anonymous", _user + "@users.sourceforge.net");
         log("Logged in as user \"anonymous\".", Project.MSG_DEBUG);

         // Set file type
         log("Setting file type to binary.", Project.MSG_DEBUG);
         ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
         log("Set file type to binary.", Project.MSG_DEBUG);

         // Change to correct directory
         log("Changing working directory to \"" + FTP_DIR + "\".", Project.MSG_DEBUG);
         ftp.changeWorkingDirectory(FTP_DIR);
         log("Changed working directory to \"" + FTP_DIR + "\".", Project.MSG_DEBUG);

         // Upload file
         log("Uploading \"" + _file + "\" to ftp://" + FTP_SERVER + '/' + FTP_DIR + '/' + fileName, Project.MSG_VERBOSE);
         if (!ftp.storeFile(fileName, in)) {
            throw new BuildException("Failed to upload \"" + _file + "\" to ftp://" + FTP_SERVER + '/' + FTP_DIR + '/' + fileName);
         }
         log("Uploaded \"" + _file + "\" to ftp://" + FTP_SERVER + '/' + FTP_DIR + '/' + fileName, Project.MSG_DEBUG);

      } catch(IOException e) {
         throw new BuildException("I/O error while uploading file.", e);
      } finally {
         if(ftp.isConnected()) {
            try {
               ftp.disconnect();
            } catch(IOException e) {
               // Ignore
            }
         }
      }
   }

   private void login() throws BuildException {

      PostMethod method = new PostMethod("https://sourceforge.net/account/login.php");
      method.addParameter("form_loginname", _user);
      method.addParameter("form_pw",        _password);
      method.addParameter("login",          "Login With SSL");

      log("Logging in to SourceForge site as \"" + _user + "\".", Project.MSG_VERBOSE);
      int code;
      try {
         _httpClient.executeMethod(method);
         code = method.getStatusCode();
      } catch (IOException e) {
         throw new BuildException("I/O error during SourceForge login.", e);
      } finally {
         method.releaseConnection();
      }

      // Expect a HTTP redirect (302)
      if (code != 302) {
         throw new BuildException("HTTP result code " + code + " while logging in. Status line: " + method.getStatusLine());
      }
      log("Logged in to SourceForge site as \"" + _user + "\".", Project.MSG_DEBUG);
   }

   private void createRelease() throws BuildException {
      PostMethod method = new PostMethod("http://sourceforge.net/project/admin/newrelease.php");
      method.addParameter("group_id",     _groupID);
      method.addParameter("release_name", _releaseName);
      method.addParameter("package_id",   _packageID);
      method.addParameter("submit",       "Create This Release");

      log("Creating release \"" + _releaseName + "\" for group " + _groupID + ", package " + _packageID + '.', Project.MSG_VERBOSE);
      int code;
      try {
         _httpClient.executeMethod(method);
         code = method.getStatusCode();
      } catch (IOException e) {
         throw new BuildException("I/O error while creating release.", e);
      } finally {
         method.releaseConnection();
      }

      // Expect a HTTP redirect (302)
      if (code != 302) {
         throw new BuildException("HTTP result code " + code + " while creating release. Status line: " + method.getStatusLine());
      }
      log("Created release \"" + _releaseName + "\" for group " + _groupID + ", package " + _packageID + '.');
   }
}
