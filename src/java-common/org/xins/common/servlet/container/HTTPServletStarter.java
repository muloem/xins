/*
 * $Id$
 *
 * Copyright 2003-2006 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.servlet.container;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;

import javax.servlet.ServletException;

/**
 * HTTP Server used to invoke the XINS Servlet.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:anthony.goubard@orange-ftgroup.com">Anthony Goubard</a>
 */
public class HTTPServletStarter {

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

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
      if (args.length < 1) {
         System.err.println("Please, pass the location of the WAR file as argument.");
         System.exit(-1);
      }
      int port = DEFAULT_PORT_NUMBER;
      if (args.length > 1) {
         try {
            port = Integer.parseInt(args[1]);
         } catch (NumberFormatException nfe) {
            System.err.println("Warning: Incorrect port number \"" + args[1] +
                  "\", using " + DEFAULT_PORT_NUMBER + " as port number.");
         }
      }

      File warFile = new File(args[0]);
      if (!warFile.exists()) {
         System.err.println("WAR file \"" + args[0] + "\" not found.");
         System.exit(-1);
      }
      try {
         // Starts the server and wait for connections
         new HTTPServletStarter(warFile, port, false);
      } catch (Exception ioe) {
         ioe.printStackTrace();
      }
   }

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The default port number.
    */
   public final static int DEFAULT_PORT_NUMBER = 8080;


   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

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
   throws ServletException, IOException {
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
   throws ServletException, IOException {
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
   throws ServletException, IOException {

      // Create the servlet
      ClassLoader loader = ServletClassLoader.getServletClassLoader(warFile, ServletClassLoader.USE_WAR_EXTERNAL_LIB);

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
   throws ServletException, IOException {

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
}
