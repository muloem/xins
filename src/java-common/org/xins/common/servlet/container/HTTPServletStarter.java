/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
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
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
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
      boolean isWar = args[0].toLowerCase().endsWith(".war");
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
    * Creates a new HTTPSevletHandler. This Servlet handler starts a web server
    * on port 8080 and wait for calls from the XINSServiceCaller.
    *
    * @param warFile
    *    the war file of the application to deploy, cannot be <code>null</code>.
    *
    * @throws ServletException
    *    if the servlet cannot be initialized.
    *
    * @throws IOException
    *    if the servlet container cannot be started.
    */
   public HTTPServletStarter(File warFile)
   throws ServletException, IOException {
      this(warFile, DEFAULT_PORT_NUMBER);
   }

   /**
    * Creates a new HTTPSevletHandler. This Servlet handler starts a web server
    * and wait for calls from the XINSServiceCaller.
    *
    * @param warFile
    *    the war file of the application to deploy, cannot be <code>null</code>.
    *
    * @param port
    *    the port of the web server, cannot be <code>null</code>.
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
    * Creates a new HTTPSevletHandler. This Servlet handler starts a web server
    * and wait for calls from the XINSServiceCaller.
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
      Object[] constArgs = {warFile, new Integer(port), new Boolean(deamon)};
      try {
         Class delegate = loader.loadClass("org.xins.common.servlet.container.HTTPServletHandler");
         Constructor constructor  = delegate.getConstructor(constClasses);
         constructor.newInstance(constArgs);
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   /**
    * Creates a new HTTPSevletHandler. This Servlet handler starts a web server
    * and wait for calls from the XINSServiceCaller.
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
      Object[] constArgs = {servletClassName, new Integer(port), new Boolean(deamon)};
      try {
         Class delegate = getClass().getClassLoader().loadClass("org.xins.common.servlet.container.HTTPServletHandler");
         Constructor constructor  = delegate.getConstructor(constClasses);
         constructor.newInstance(constArgs);
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

}
