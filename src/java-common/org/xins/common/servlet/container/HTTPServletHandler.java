/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.servlet.container;


import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import javax.servlet.ServletException;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.PropertyConfigurator;

import org.xins.common.Log;
import org.xins.common.Utils;

/**
 * HTTP Server used to invoke the XINS Servlet.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public class HTTPServletHandler {

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Initializes the logging subsystem with fallback default settings.
    */
   private static final void configureLoggerFallback() {
      Properties settings = new Properties();
      settings.setProperty("log4j.rootLogger",                                "ALL, console");
      settings.setProperty("log4j.appender.console",                          "org.apache.log4j.ConsoleAppender");
      settings.setProperty("log4j.appender.console.layout",                   "org.apache.log4j.PatternLayout");
      settings.setProperty("log4j.appender.console.layout.ConversionPattern", "%16x %6c{1} %-6p %m%n");
      PropertyConfigurator.configure(settings);
   }

   
   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The default port number is 8080.
    */
   public final static int DEFAULT_PORT_NUMBER = 8080;

   
   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Creates a new HTTPSevletHandler with no Servlet. Use the addServlet 
    * methods to add the WAR files or the Servlets.
    *
    * @param port
    *    The port of the servlet server.
    *
    * @param deamon
    *    <code>true</code> if the thread listening to connection should be a 
    *    deamon thread, <code>false</code> otherwise.
    *
    * @throws IOException
    *    if the servlet container cannot be started.
    */
   public HTTPServletHandler(int port, boolean deamon) throws IOException {
      // Configure log4j
      configureLoggerFallback();
      
      // Start the HTTP server.
      startServer(port, deamon);
   }

   /**
    * Creates a new HTTPSevletHandler. This Servlet handler starts a web server
    * on port 8080 and wait for calls from the XINSServiceCaller.
    * Note that all the libraries used by this WAR file should already be in
    * the classpath.
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
   public HTTPServletHandler(File warFile)
   throws ServletException, IOException {
      this(DEFAULT_PORT_NUMBER, true);
      addWAR(warFile, "/");
   }

   /**
    * Creates a new HTTPSevletHandler. This Servlet handler starts a web server
    * on port 8080 and wait for calls from the XINSServiceCaller.
    * Note that all the libraries used by this WAR file should already be in
    * the classpath.
    *
    * @param warFile
    *    the war file of the application to deploy, cannot be <code>null</code>.
    *
    * @param port
    *    The port of the servlet server.
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
   public HTTPServletHandler(File warFile, int port, boolean deamon)
   throws ServletException, IOException {
      this(port, deamon);
      addWAR(warFile, "/");
   }

   /**
    * Creates a new HTTPSevletHandler. This Servlet handler starts a web server
    * and wait for calls from the XINSServiceCaller.
    *
    * @param servletClassName
    *    The name of the servlet's class to load, cannot be <code>null</code>.
    *
    * @throws ServletException
    *    if the servlet cannot be initialized.
    *
    * @throws IOException
    *    if the servlet container cannot be started.
    */
   public HTTPServletHandler(String servletClassName) throws ServletException, IOException {
      this(DEFAULT_PORT_NUMBER, true);
      addServlet(servletClassName, "/");
   }

   /**
    * Creates a new HTTPSevletHandler. This Servlet handler starts a web server
    * and wait for calls from the XINSServiceCaller.
    *
    * @param servletClassName
    *    The name of the servlet's class to load, cannot be <code>null</code>.
    *
    * @param port
    *    The port of the servlet server.
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
   public HTTPServletHandler(String servletClassName, int port, boolean deamon) throws ServletException, IOException {
      this(port, deamon);
      addServlet(servletClassName, "/");
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The web server.
    */
   private ServerSocket _serverSocket;

   /**
    * The thread that waits for connections from the client.
    */
   private SocketAcceptor _acceptor;

   /**
    * Flag indicating if the server should wait for other connections or stop.
    */
   private boolean _running;

   /**
    * Mapping between the path and the servlet.
    */
   private Map _servlets = new HashMap();
   
   
   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Adds a WAR file to the server.
    * The servlet with the virtual path "/" will be the default one.
    * Note that all the libraries used by this WAR file should already be in
    * the classpath.
    *
    * @param warFile
    *    The war file of the application to deploy, cannot be <code>null</code>.
    *
    * @param virtualPath
    *    The virtual path of the HTTP server that links to this WAR file, cannot be <code>null</code>.
    *
    * @throws ServletException
    *    if the servlet cannot be initialized.
    */
   public void addWAR(File warFile, String virtualPath) throws ServletException {
      LocalServletHandler servlet = new LocalServletHandler(warFile);
      _servlets.put(virtualPath, servlet);
   }

   /**
    * Adds a new servlet.
    * The servlet with the virtual path "/" will be the default one.
    *
    * @param servletClassName
    *    The name of the servlet's class to load, cannot be <code>null</code>.
    *
    * @param virtualPath
    *    The virtual path of the HTTP server that links to this WAR file, cannot be <code>null</code>.
    *
    * @throws ServletException
    *    if the servlet cannot be initialized.
    */
   public void addServlet(String servletClassName, String virtualPath) throws ServletException{
      LocalServletHandler servlet = new LocalServletHandler(servletClassName);
      _servlets.put(virtualPath, servlet);
   }
   
   /**
    * Remove a servlet from the server.
    *
    * @param virtualPath
    *    The virtual path of the servlet to remove, cannot be <code>null</code>.
    */
   public void removeServlet(String virtualPath) {
      LocalServletHandler servlet = (LocalServletHandler) _servlets.get(virtualPath);
      servlet.close();
      _servlets.remove(virtualPath);
   }
   
   /**
    * Starts the web server.
    *
    * @param port
    *    The port of the servlet server.
    *
    * @param deamon
    *    <code>true</code> if the thread listening to connection should be a 
    *    deamon thread, <code>false</code> otherwise.
    *
    * @throws IOException
    *    If the web server cannot be started.
    */
   public void startServer(int port, boolean deamon) throws IOException {
      // Create the server socket
      _serverSocket = new ServerSocket(port, 5);
      _running = true;

      _acceptor = new SocketAcceptor(deamon);
      _acceptor.start();
   }

   /**
    * Dispose the servlet and stops the web server.
    */
   public void close() {
      _running = false;
      Iterator itServlets = _servlets.values().iterator();
      while (itServlets.hasNext()) {
         LocalServletHandler servlet = (LocalServletHandler) itServlets.next();
         servlet.close();
      }
      try {
         _serverSocket.close();
      } catch (IOException ioe) {
         Log.log_1502(ioe);
      }
   }

   /**
    * This method is invoked when a client connects to the server.
    *
    * @param client
    *    the connection with the client.
    *
    * @throws IOException
    *    If the query is not handled correctly.
    */
   public void serviceClient(Socket client) throws IOException {
      BufferedOutputStream outbound = null;
      try {
         // Acquire the streams for IO
         BufferedReader inbound = new BufferedReader(new InputStreamReader(client.getInputStream()));
         outbound = new BufferedOutputStream(client.getOutputStream());

         // Get the output
         String httpResult = httpQuery(inbound);

         outbound.write(httpResult.getBytes("ASCII"), 0, httpResult.length());

      } finally{
         // Clean up
         outbound.close();

         // The following close statements doesn't work on Unix.
         // inbound.close();
         // outbound.close();
         // client.close();
      }
   }

   /**
    * This method parses the data sent from the client to get the input
    * parameters and format the result as a compatible HTTP result.
    * This method will used the servlet associated with the passed virtual
    * path. If no servlet is associated with the virtual path, the servlet with 
    * the virtual path "/" is used as default. If there is no servlet then with
    * the virtual path "/" is found then HTTP 404 is returned.
    *
    * @param input
    *    the input stream that contains the data send by the client.
    *
    * @return
    *    the HTTP result to send back to the client.
    *
    * @throws IOException
    *    If the query is not handled correctly.
    */
   public String httpQuery(BufferedReader input) throws IOException {
      String inputLine;
      String query = null;
      String virtualPath = "/";

      while (query == null && (inputLine = input.readLine()) != null) {
         if (inputLine.startsWith("GET ")) {
            int questionPos = inputLine.indexOf('?');
            if (questionPos !=-1) {
               int lastSpace = inputLine.indexOf(' ', questionPos);
               if (lastSpace != -1) {
                  query = inputLine.substring(questionPos + 1, lastSpace);
               } else {
                  query = inputLine.substring(questionPos + 1);
               }
               virtualPath = inputLine.substring(4, questionPos);
            } else {
               query = "";
               int lastSpace = inputLine.indexOf(' ', 4);
               if (lastSpace != -1) {
                  virtualPath = inputLine.substring(4, lastSpace);
               } else {
                  virtualPath = inputLine.substring(4);
               }
            }
            query = query.replace(',', '&');
            if (virtualPath.length() > 1 && virtualPath.endsWith("/")) {
               virtualPath = virtualPath.substring(0, virtualPath.length() - 1);
            }
         }

         // POST method
         if (inputLine.startsWith("POST ")) {
            virtualPath = inputLine.substring(5, inputLine.indexOf(' ', 5));
         }
         if (inputLine.startsWith("Content-Length: ")) {
            int postLength = Integer.parseInt(inputLine.substring(16));
            input.readLine();
            input.readLine();
            char[] data = new char[postLength];
            input.read(data);
            query = new String(data);
         }
      }
      if (query != null) {
         LocalServletHandler servlet = (LocalServletHandler) _servlets.get(virtualPath);
         if (servlet == null) {
            servlet = (LocalServletHandler) _servlets.get("/");
         }
         if (servlet == null) {
            return "HTTP/1.1 404 " + HttpStatus.getStatusText(404).replace(' ', '_') + "\n\n";
         }
         XINSServletResponse response = servlet.query(query);
         String result = response.getResult();
         if (result == null) {
            return "HTTP/1.1 " + response.getStatus() + " " + HttpStatus.getStatusText(response.getStatus()).replace(' ', '_') + "\n\n";
         }
         String httpResult = "HTTP/1.1 " + response.getStatus() + " " + HttpStatus.getStatusText(response.getStatus()) + "\n";
         httpResult += "Content-type: " + response.getContentType() + "\n";
         int length = result.length() + 1;
         httpResult += "Content-Length: " + length + "\n";
         httpResult += "Connection: close\n";
         httpResult += "\n";
         httpResult += result + "\n";
         httpResult += "\n";
         return httpResult;
      }
      return "HTTP/1.1 400 BAD_REQUEST\n\n";
   }

   /**
    * Thread waiting for connection from the client.
    */
   private class SocketAcceptor extends Thread {

      /**
       * Create the thread.
       * 
       * @param deamon
       *    <code>true</code> if the server should be a deamon thread,$
       *    <code>false</code> otherwise.
       */
      public SocketAcceptor(boolean deamon) {
         setDaemon(deamon);
      }

      /**
       * Executes the thread.
       */
      public void run() {
         Log.log_1500(_serverSocket.getLocalPort());
         try {
            while (_running) {
               // Wait for a connection
               Socket clientSocket = _serverSocket.accept();

               try {
                  // Service the connection
                  serviceClient(clientSocket);
               } catch (Exception ex) {
                  // If anything goes wrong still continue to listen to the port.
                  Utils.logProgrammingError(getClass().getName(), "run", "", "", "", ex);
               }
            }
         } catch (SocketException ie) {
            // fall through
         } catch (IOException ioe) {
            Log.log_1501(ioe);
         }
      }
   }
}
