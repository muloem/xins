/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.servlet.container;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.xins.common.Log;

/**
 * This class allows to invoke a XINS API without using HTTP.
 *
 * Example:
 * <code>
 * LocalServletHandler handler = LocalServletHandler.getInstance("c:\\test\\myproject.war");
 * String xmlResult = handler.query("http://127.0.0.1:8080/myproject/?_function=MyFunction&gender=f&personLastName=Lee");
 * </code>
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public class LocalServletHandler {

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Gets the instance of the Servlet handler for the specified WAR file.
    * All classes needed should be in the classpath.
    *
    * @param warFile
    *    The WAR file containing the Servlet to load.
    *
    * @return the servlet handler.
    *
    * @throws ServletException
    *    if the Servlet cannot be created.
    */
   public final static LocalServletHandler getInstance(File warFile) throws ServletException {
      return getInstance(warFile, USE_CURRENT_CLASSPATH);
   }

   /**
    * Gets the instance of the Servlet handler for the specified WAR file.
    * Note that the mode parameter will be only used the first time that 
    * the WAR file is loaded.
    *
    * @param warFile
    *    The WAR file containing the Servlet to load.
    *
    * @param mode
    *    The way that the classes will be loaded. The possible values are
    *    <code>USE_CURRENT_CLASSPATH</code>, <code>USE_CLASSPATH_LIB</code>,
    *    <code>USE_XINS_LIB</code>, <code>USE_WAR_LIB</code>,
    *    <code>USE_WAR_EXTERNAL_LIB</code>.
    *
    * @return the servlet handler.
    *
    * @throws ServletException
    *    if the Servlet cannot be created.
    */
   public final static LocalServletHandler getInstance(File warFile, int mode) throws ServletException {
      if (SERVLET_MAP.get(warFile.getPath()) == null) {
         LocalServletHandler servlet = new LocalServletHandler(warFile, mode);
         SERVLET_MAP.put(warFile.getPath(), servlet);
      }
      return (LocalServletHandler) SERVLET_MAP.get(warFile.getPath());
   }


   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Use the current class loader to load the servlet and the libraries.
    */
   public final static int USE_CURRENT_CLASSPATH = 1;
   
   /**
    * Load the Servlet code from the WAR file and use the current
    * classpath for the libraries.
    */
   public final static int USE_CLASSPATH_LIB = 2;

   /**
    * Load the servlet code from the WAR file and try to find the libraries
    * in the same directory as this xins-common.jar or &lt:parent&gt;/lib
    * directory.
    */
   public final static int USE_XINS_LIB = 3;

   /**
    * Load the servlet code and the libraries from the WAR file.
    * This may take some time as the libraries need to be extracted from the 
    * WAR file.
    */
   public final static int USE_WAR_LIB = 4;

   /**
    * Load the servlet code and the standard libraries from the CLASSPATH.
    * Load the included external libraries from the WAR file.
    */
   public final static int USE_WAR_EXTERNAL_LIB = 5;
   
   /**
    * The name of the standard libraries.
    */
   private final static String[] STANDARD_LIBS = {"xins-server.jar", "xins-common.jar",
      "xins-client.jar", "logdoc.jar", "commons-codec.jar", "commons-httpclient.jar", 
      "commons-logging.jar", "jakarta-oro.jar", "log4j.jar", "xmlenc.jar"};

   /**
    * The Map&lt;String, LocalServletHandler&gt; containing the Servlets.
    */
   private final static Map SERVLET_MAP = new HashMap();


   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Creates a Servlet handler that allow to invoke a Servlet without starting
    * a HTTP server.
    *
    * @param warFile
    *    the location of the war file containing the Servlet, cannot be
    *    <code>null</code>.
    *
    * @param mode
    *    the mode in which the servlet should be loaded. The possible values are
    *    <code>USE_CURRENT_CLASSPATH</code>, <code>USE_CLASSPATH_LIB</code>,
    *    <code>USE_XINS_LIB</code>, <code>USE_WAR_LIB</code>,
    *    <code>USE_WAR_EXTERNAL_LIB</code>.
    *
    * @throws ServletException
    *    if the Servlet cannot be created.
    */
   private LocalServletHandler(File warFile, int mode) throws ServletException {
      initServlet(warFile, mode);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The Servlet started by this Servlet handler.
    */
   private HttpServlet _apiServlet;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Initializes the Servlet.
    *
    * @param warFile
    *    the location of the war file, cannot be <code>null</code>.
    *
    * @param mode
    *    the mode in which the servlet should be loaded. The possible values are
    *    <code>USE_CURRENT_CLASSPATH</code>, <code>USE_CLASSPATH_LIB</code>,
    *    <code>USE_XINS_LIB</code>, <code>USE_WAR_LIB</code>,
    *    <code>USE_WAR_EXTERNAL_LIB</code>.
    *
    * @throws ServletException
    *    if the Servlet cannot be loaded.
    */
   public void initServlet(File warFile, int mode) throws ServletException {
      // create and initiliaze the Servlet
      Log.log_1503(warFile.getPath(), mode);
      try {
         ClassLoader servletClassLoader = getServletClassLoader(warFile, mode);
         LocalServletConfig servletConfig = new LocalServletConfig(warFile);
         _apiServlet = (HttpServlet) servletClassLoader.loadClass(servletConfig.getServletClass()).newInstance();
         _apiServlet.init(servletConfig);
      } catch (ServletException exception) {
         Log.log_1508(exception);
         throw exception;
      } catch (Exception exception) {
         exception.printStackTrace();
         Log.log_1509(exception);
         throw new ServletException(exception);
      }
   }

   /**
    * Get the class loader that will loader the servlet.
    *
    * @param warFile
    *    The WAR file containing the Servlet.
    *
    * @param mode
    *    the mode in which the servlet should be loaded. The possible values are
    *    <code>USE_CURRENT_CLASSPATH</code>, <code>USE_CLASSPATH_LIB</code>,
    *    <code>USE_XINS_LIB</code>, <code>USE_WAR_LIB</code>,
    *    <code>USE_WAR_EXTERNAL_LIB</code>.
    *
    * @return
    *    The Class loader to use to load the Servlet.
    *
    * @throws IOException
    *    if the file cannot be read or is incorrect.
    */
   private ClassLoader getServletClassLoader(File warFile, int mode) throws IOException {
      if (mode == USE_CURRENT_CLASSPATH) {
         return getClass().getClassLoader();
      }
      List urlList = new ArrayList();
      if (mode != USE_WAR_EXTERNAL_LIB) {
         URL classesURL = new URL("jar:file:" + warFile.getAbsolutePath().replace(File.separatorChar, '/') + "!/WEB-INF/classes/");
         urlList.add(classesURL);
      }
      
      if (mode == USE_XINS_LIB) {
         String classLocation = getClass().getProtectionDomain().getCodeSource().getLocation().toString();
         String commonJar = classLocation.substring(6).replace('/', File.separatorChar);
         File baseDir = new File(commonJar).getParentFile();
         File[] xinsFiles = baseDir.listFiles(); 
         for (int i = 0; i < xinsFiles.length; i++) {
            if (xinsFiles[i].getName().endsWith(".jar")) {
               urlList.add(xinsFiles[i].toURL());
            }
         }
         File libDir = new File(baseDir, ".." + File.separator + "lib");
         File[] libFiles = libDir.listFiles(); 
         for (int i = 0; i < libFiles.length; i++) {
            if (libFiles[i].getName().endsWith(".jar")) {
               urlList.add(libFiles[i].toURL());
            }
         }
      } else if (mode == USE_WAR_LIB || mode == USE_WAR_EXTERNAL_LIB) {
         List standardLibs = new ArrayList();
         if (mode == USE_WAR_EXTERNAL_LIB) {
            for (int i = 0; i < STANDARD_LIBS.length; i++) {
               standardLibs.add(STANDARD_LIBS[i]);
            }
         }
         JarInputStream jarStream = new JarInputStream(new FileInputStream(warFile));
         JarEntry entry = jarStream.getNextJarEntry();
         while(entry != null) {
            String entryName = entry.getName();
            if (entryName.startsWith("WEB-INF/lib/") && entryName.endsWith(".jar") && !standardLibs.contains(entryName.substring(12))) {
               File tempJarFile = unpack(jarStream, entryName);
               urlList.add(tempJarFile.toURL());
            }
            entry = jarStream.getNextJarEntry();
         }
         jarStream.close();
      }
      URL[] urls = new URL[urlList.size()];
      for (int i=0; i<urlList.size(); i++) {
         urls[i] = (URL) urlList.get(i);
      }
      ClassLoader loader = new URLClassLoader(urls, getClass().getClassLoader());
      return loader;
   }

   /**
    * Unpack the specified entry from the JAR file.
    *
    * @param jarStream
    *    The input stream of the JAR file positioned at the entry.
    * @param entryName
    *    The name of the entry to extract.
    *
    * @return
    *    The extracted file. The created file is a temporary file in the 
    *    temporary directory.
    *
    * @throws IOException
    *    if the JAR file cannot be read or is incorrect.
    */
   private File unpack(JarInputStream jarStream, String entryName) throws IOException {
      String libName = entryName.substring(entryName.lastIndexOf('/') + 1, entryName.length() - 4);
      File tempJarFile = File.createTempFile(libName, ".jar");
      FileOutputStream out = new FileOutputStream(tempJarFile);
    
      // Transfer bytes from the JAR file to the output file
      byte[] buf = new byte[8192];
      int len;
      while ((len = jarStream.read(buf)) > 0) {
         out.write(buf, 0, len);
      }
      out.close();
      return tempJarFile;
   }
   
   /**
    * Gets the Servlet.
    *
    * @return
    *    the created Servlet or <code>null</code> if no Servlet was created.
    */
   public HttpServlet getServlet() {
      return _apiServlet;
   }
   
   /**
    * Queries the Servlet with the specified URL.
    *
    * @param url
    *    the url query for the request.
    *
    * @return
    *    the servlet response.
    *
    * @throws IOException
    *    If the query is not handled correctly by the servlet.
    */
   public XINSServletResponse query(String url) throws IOException {
      Log.log_1504(url);
      XINSServletRequest request = new XINSServletRequest(url);
      XINSServletResponse response = new XINSServletResponse();
      try {
         _apiServlet.service(request, response);
      } catch (ServletException ex) {
         Log.log_1505(ex);
         throw new IOException(ex.getMessage());
      }
      Log.log_1506(response.getResult(), response.getStatus());
      return response;
   }

   /**
    * Disposes the Servlet and closes this Servlet handler.
    */
   public void close() {
      Log.log_1507();
      _apiServlet.destroy();
      SERVLET_MAP.clear();
   }
}
