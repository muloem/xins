/*
 * $Id$
 */
package org.xins.util.ant.sourceforge;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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

   private static final String REMOTE_HOST = "upload.sourceforge.net";
   private static final String REMOTE_DIR = "incoming";


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
      // empty
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The file to upload.
    */
   private String _file;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

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
    * Called by the project to let the task do its work.
    *
    * @throws BuildException
    *    if something goes wrong with the build.
    */
   public void execute() throws BuildException {

      // Check preconditions
      if (_file == null) {
         throw new BuildException("The file to upload must be set, but it is not.");
      }

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
         log("Connecting to FTP server \"" + REMOTE_HOST + "\".", Project.MSG_DEBUG);
         ftp.connect(REMOTE_HOST);
         log("Connected to FTP server \"" + REMOTE_HOST + "\".", Project.MSG_VERBOSE);
         log("FTP server \"" + REMOTE_HOST + "\" returned reply string \"" + ftp.getReplyString() + "\".", Project.MSG_DEBUG);

         // Check reply code to verify success
         int reply = ftp.getReplyCode();
         if(!FTPReply.isPositiveCompletion(reply)) {
            throw new BuildException("FTP server \"" + REMOTE_HOST + "\" refused connection.");
         }

         // Login
         log("Logging in as user \"anonymous\".", Project.MSG_DEBUG);
         ftp.login("anonymous", "anonymous@anonymous.org");
         log("Logged in as user \"anonymous\".", Project.MSG_DEBUG);

         // Set file type
         log("Setting file type to binary.", Project.MSG_DEBUG);
         ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
         log("Set file type to binary.", Project.MSG_DEBUG);

         // Change to correct directory
         log("Changing working directory to \"" + REMOTE_DIR + "\".", Project.MSG_DEBUG);
         ftp.changeWorkingDirectory(REMOTE_DIR);
         log("Changed working directory to \"" + REMOTE_DIR + "\".", Project.MSG_DEBUG);

         // Upload file
         log("Uploading \"" + _file + "\" to ftp://" + REMOTE_HOST + '/' + REMOTE_DIR + '/' + fileName, Project.MSG_VERBOSE);
         if (!ftp.storeFile(fileName, in)) {
            throw new BuildException("Failed to upload \"" + _file + "\" to ftp://" + REMOTE_HOST + '/' + REMOTE_DIR + '/' + fileName);
         }
         log("Uploaded \"" + _file + "\" to ftp://" + REMOTE_HOST + '/' + REMOTE_DIR + '/' + fileName, Project.MSG_DEBUG);

      } catch(IOException e) {
         throw new BuildException("I/O error. Caught " + e.getClass().getName() + ", message is: " + e.getMessage());
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
}
