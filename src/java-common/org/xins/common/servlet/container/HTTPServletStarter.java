/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.servlet.container;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * HTTP Server used to invoke the XINS Servlet.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:anthony.goubard@orange-ftgroup.com">Anthony Goubard</a>
 */
public class HTTPServletStarter {

   /**
    * The default port number.
    */
   public static final int DEFAULT_PORT_NUMBER = 8080;

   /**
    * Creates a new <code>HTTPServletStarter</code> for the specified WAR
    * file, on the default port, as a daemon thread.
    *
    * <p>A listener is started on the port immediately.
    *
    * @param warFile
    *    the WAR file of the application to deploy, cannot be
    *    <code>null</code>.
    *
    * @throws ServletException
    *    if the servlet cannot be initialized.
    *
    * @throws IOException
    *    if the servlet container cannot be started.
    */
   public HTTPServletStarter(File warFile)
   throws Exception {
      this(warFile, DEFAULT_PORT_NUMBER, true);
   }

   /**
    * Creates a new <code>HTTPServletStarter</code> for the specified WAR
    * file, on the specified port, as a daemon thread.
    *
    * <p>A listener is started on the port immediately.
    *
    * @param warFile
    *    the WAR file of the application to deploy, cannot be
    *    <code>null</code>.
    *
    * @param port
    *    the port to run the web server on.
    *
    * @throws ServletException
    *    if the servlet cannot be initialized.
    *
    * @throws IOException
    *    if the servlet container cannot be started.
    */
   public HTTPServletStarter(File warFile, int port)
   throws Exception {
      this(warFile, port, true);
   }

   /**
    * Creates a new <code>HTTPServletStarter</code> for the specified WAR
    * file, on the specified port, optionally as a daemon thread.
    *
    * <p>A listener is started on the port immediately.
    *
    * @param warFile
    *    The war file of the application to deploy, cannot be <code>null</code>.
    *
    * @param port
    *    The port of the web server, cannot be <code>null</code>.
    *
    * @param deamon
    *    <code>true</code> if the thread listening to connection should be a
    *    deamon thread, <code>false</code> otherwise.
    *
    * @throws ServletException
    *    if the servlet cannot be initialized.
    *
    * @throws IOException
    *    if the servlet container cannot be started.
    */
   public HTTPServletStarter(File warFile, int port, boolean deamon)
   throws Exception {
      this(warFile, port, deamon, ServletClassLoader.USE_WAR_EXTERNAL_LIB);
   }

   /**
    * Creates a new <code>HTTPServletStarter</code> for the specified servlet
    * class, on the specified port, optionally as a daemon thread.
    *
    * <p>A listener is started on the port immediately.
    *
    * @param servletClassName
    *    The name of the servlet to load, cannot be <code>null</code>.
    *
    * @param port
    *    The port of the web server, cannot be <code>null</code>.
    *
    * @param deamon
    *    <code>true</code> if the thread listening to connection should be a
    *    deamon thread, <code>false</code> otherwise.
    *
    * @param loaderMode
    *    the way the ClassLoader should locate and load the classes.
    *    See {@link ServletClassLoader].
    *
    * @throws ServletException
    *    if the servlet cannot be initialized.
    *
    * @throws IOException
    *    if the servlet container cannot be started.
    *
    * @since XINS 2.1.
    */
   public HTTPServletStarter(File warFile, int port, boolean deamon, int loaderMode)
   throws Exception {

      // Create the servlet
      ClassLoader loader = ServletClassLoader.getServletClassLoader(warFile, loaderMode);

      Class[] constClasses = {File.class, Integer.TYPE, Boolean.TYPE};
      Object[] constArgs = {warFile, new Integer(port), deamon ? Boolean.TRUE : Boolean.FALSE};
      try {
         Class delegate = loader.loadClass("org.xins.common.servlet.container.HTTPServletHandler");
         Constructor constructor  = delegate.getConstructor(constClasses);
         constructor.newInstance(constArgs);
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   /**
    * Creates a new <code>HTTPServletStarter</code> for the specified servlet
    * class, on the specified port, optionally as a daemon thread.
    *
    * <p>A listener is started on the port immediately.
    *
    * @param servletClassName
    *    The name of the servlet to load, cannot be <code>null</code>.
    *
    * @param port
    *    The port of the web server, cannot be <code>null</code>.
    *
    * @param deamon
    *    <code>true</code> if the thread listening to connection should be a
    *    deamon thread, <code>false</code> otherwise.
    *
    * @throws ServletException
    *    if the servlet cannot be initialized.
    *
    * @throws IOException
    *    if the servlet container cannot be started.
    */
   public HTTPServletStarter(String servletClassName, int port, boolean deamon)
   throws Exception {

      // Create the servlet
      Class[] constClasses = {String.class, Integer.TYPE, Boolean.TYPE};
      Object[] constArgs = {servletClassName, new Integer(port), Boolean.valueOf(deamon)};
      try {
         Class delegate = getClass().getClassLoader().loadClass("org.xins.common.servlet.container.HTTPServletHandler");
         Constructor constructor  = delegate.getConstructor(constClasses);
         constructor.newInstance(constArgs);
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   /**
    * Starts the Servlet container for the specific API.
    *
    * @param args
    *    The command line arguments, the first argument should be the location
    *    of the WAR file or the name of the class of the servlet to load,
    *    the optional second argument is the port number.
    *    If no port number is specified, 8080 is used as default.
    */
   public static void main(String[] args) {

      CommandLineArguments cmdArgs = new CommandLineArguments(args);
      if (cmdArgs.showGUI()) {
         ConsoleGUI.display();
      }
      try {
         // Starts the server and wait for connections
         new HTTPServletStarter(cmdArgs.getWARFile(), cmdArgs.getPort(), false, cmdArgs.getLoaderMode());
      } catch (Exception ioe) {
         ioe.printStackTrace();
      }
   }
   
   /**
    * Inner class used to parse the arguments.
    */
   static class CommandLineArguments {

      private int port;

      private File warFile;

      private int loaderMode = -1;

      private boolean showGUI;

      CommandLineArguments(String[] args) {
         port = DEFAULT_PORT_NUMBER;
         showGUI = false;
         if (args.length == 1 && args[0].equals("-help")) {
            System.out.println("Usage: java -jar <api name>.war [-port:<port number>] [-gui] [-war:<war file>] [loader:<classloader mode>]");
            System.exit(0);
         }
         for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("-port:") || arg.startsWith("-port=")) {
               try {
                  port = Integer.parseInt(arg.substring(6));
               } catch (NumberFormatException nfe) {
                  System.err.println("Warning: Incorrect port number \"" + args[1] +
                        "\", using " + DEFAULT_PORT_NUMBER + " as port number.");
               }
            } else if (arg.startsWith("-war:") || arg.startsWith("-war=")) {
               warFile = new File(arg.substring(5));
            } else if (arg.startsWith("-loader:") || arg.startsWith("-loader=")) {
               try {
                  loaderMode = Integer.parseInt(args[2]);
               } catch (NumberFormatException nfe) {
                  System.err.println("Warning: Incorrect ClassLoader \"" + args[2] +
                        "\", using " + ServletClassLoader.USE_WAR_LIB + " as default.");
               }
            } else if (arg.equalsIgnoreCase("-gui")) {
               showGUI = true;

            // for backward compatibility
            } else if (arg.endsWith(".war") && warFile == null) {
               warFile = new File(arg);
            } else if (port == DEFAULT_PORT_NUMBER) {
               try {
                  port = Integer.parseInt(arg);
               } catch (NumberFormatException nfe) {
               }
            }
         }

         // Detect the location of the WAR file if needed.
         if (warFile == null) {
            URL codeLocation = HTTPServletStarter.class.getProtectionDomain().getCodeSource().getLocation();
            System.out.println("No WAR file passed as argument, using: " + codeLocation);
            try {
               warFile = new File(new URI(codeLocation.toString()));
            } catch (URISyntaxException murlex) {
               murlex.printStackTrace();
            }
         }

         if (warFile == null || !warFile.exists()) {
            System.err.println("WAR file \"" + warFile + "\" not found.");
            System.exit(-1);
         }

         // Detect the ClassLoader mode
         if (loaderMode == -1) {
            String classPath = System.getProperty("java.class.path");
            if (classPath.indexOf("xins-common.jar") != -1 && classPath.indexOf("servlet.jar") != -1 &&
                  classPath.indexOf("xins-server.jar") != -1 && classPath.indexOf("xmlenc.jar") != -1) {
               loaderMode = ServletClassLoader.USE_WAR_EXTERNAL_LIB;
            } else {
               loaderMode = ServletClassLoader.USE_WAR_LIB;
            }
         }
      }

      /**
       * Gets the port number specified. If no default port number is specified
       * return the default port number.
       * 
       * @return
       *    the port number.
       */
      int getPort() {
         return port;
      }
      
      /**
       * Gets the location of the WAR file to execute.
       * 
       * @return
       *    the WAR file or <code>null</code> if not found.
       */
      File getWARFile() {
         return warFile;
      }
      
      /**
       * Gets the class loader mode.
       * 
       * @return
       *    the class loader mode to use to load the WAR classes.
       */
      int getLoaderMode() {
         return loaderMode;
      }
      
      /**
       * Indicates whether to run it in console mode or with the Swing user interface.
       * 
       * @return
       *    <code>true</code> for the graphical user interface mode, 
       *    <code>false</code> for the console mode.
       */
      boolean showGUI() {
         return showGUI;
      }
   }
}
