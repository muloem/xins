/*
 * $Id$
 */
package org.xins.tests.server.servlet;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import javax.servlet.ServletException;

import org.apache.commons.httpclient.HttpStatus;
import org.xins.server.APIServlet;

/**
 * HTTP Server used to invoke the XINS servlet.
 *
 * @version $Revision$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public class HTTPServletHandler {
   
   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Creates a new HTTPSevletHandler. This Servlet handler starts a web server
    * on port 8080 and wait for calls from the XINSServiceCaller.
    *
    * @param warFile
    *    the war file of the application to deploy, cannot be <code>null</code>.
    */
   public HTTPServletHandler(String warFile) {
      try {
         // Initialize the servlet
         initServlet(warFile);

         // Start the HTTP server.
         startServer();
      } catch (Exception ex) {
         ex.printStackTrace();
         close();
      }
   }

   
   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The servlet.
    */
   private APIServlet _apiServlet;
   
   /**
    * The web server.
    */
   private ServerSocket _serverSocket;
   
   /**
    * The thread that waits for connections from the client.
    */
   private SocketAcceptor _acceptor;
   
   /**
    * flag indicating if the server should wait for other connections or stop.
    */
   private boolean _running;
   
   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
   
   /**
    * Initializes the Servlet.
    *
    * @param warFile
    *    the location of the war file, cannot be <code>null</code>.
    */
   public void initServlet(String warFile) throws ServletException {
      _apiServlet = new APIServlet();
      LocalServletConfig servletConfig = new LocalServletConfig(warFile);
      _apiServlet.init(servletConfig);
   }

   /**
    * Starts the web server.
    *
    * @throw IOException
    *    If the web server cannot be created.
    */
   public void startServer() throws IOException {
      // Create the server socket
      _serverSocket = new ServerSocket(8080, 5);
      _running = true;
      
      _acceptor = new SocketAcceptor();
      _acceptor.start();
   }

   /**
    * Dispose the servlet and stops the web server.
    */
   public void close() {
      _running = false;
      _apiServlet.destroy();
      try {
         _serverSocket.close();
      } catch (IOException ioe) {
         ioe.printStackTrace();
      }
   }

   /**
    * This method is invoked when a client connects to the server.
    *
    * @param client
    *    the connection with the client.
    */
   public void serviceClient(Socket client) throws IOException {
      DataInputStream inbound = null;
      DataOutputStream outbound = null;
      try {
         // Acquire the streams for IO
         inbound = new DataInputStream(client.getInputStream());
         outbound = new DataOutputStream(client.getOutputStream());

         // Get the output
         String httpResult = httpQuery(inbound);
         // System.out.println("+++ Result " + httpResult);

         outbound.writeBytes(httpResult);
         
      } finally{
         // Clean up
         // System.out.println("Cleaning up connection: " + client);
         outbound.flush();
         
         // The following close statements doesn't work on Unix.
         // inbound.close();
         // outbound.close();
         // client.close();
      }
   }
   
   /**
    * This method parses the data sent from the client to get the input 
    * parameters and format the result as a compatible HTTP result.
    *
    * @param input
    *    the input stream that contains the data send by the client.
    *
    * @return
    *    the HTTP result to send back to the client.
    */
   public String httpQuery(DataInputStream input) throws IOException {
      String inputLine;
    
      while ((inputLine = input.readLine()) != null) {
         // System.out.println("*** input: " + inputLine);
         if (inputLine.startsWith("GET ")) {
            int questionPos = inputLine.indexOf('?');
            if (questionPos !=-1) {
               int lastSpace = inputLine.indexOf(' ', questionPos);
               if (lastSpace != -1) {
                  inputLine = inputLine.substring(questionPos + 1, lastSpace);
               } else {
                  inputLine = inputLine.substring(questionPos + 1);
               }
            }
            LocalHTTPServletResponse response = query(inputLine);
            String result = response.getResult();
            String httpResult = "HTTP/1.1 " + response.getStatus() + " " + HttpStatus.getStatusText(response.getStatus()) + "\n";
            httpResult += "Content-type: " + response.getContentType() + "\n";
            int length = result.length() + 1;
            httpResult += "Content-Length: " + length + "\n";
            httpResult += "\n";
            httpResult += result + "\n";
            httpResult += "\n";
            return httpResult;
         }
      }
      return "HTTP/1.1 400 BAD_REQUEST\n\n";
   }
   
   /**
    * Executes the servlet
    *
    * @param url
    *    the requested URL or a common separated list of the parameters 
    *    passed to the URL (e.g. _function=GetVestion,param1=value1)
    */
   public LocalHTTPServletResponse query(String url) throws IOException {
      LocalHTTPServletRequest request = new LocalHTTPServletRequest(url);
      LocalHTTPServletResponse response = new LocalHTTPServletResponse();
      _apiServlet.service(request, response);
      return response;
   }
   
   /**
    * Thread waiting for connection from the client.
    */
   private class SocketAcceptor extends Thread {
      
      /**
       * Create the thread.
       */
      public SocketAcceptor() {
         setDaemon(true);
      }
      
      /**
       * Executes the thread.
       */
      public void run() {
         // System.out.println("Server started.");
         try {
            while (_running) {
               // Wait for a connection
               Socket clientSocket = _serverSocket.accept();
               // System.out.println("Server contacted.");

               // Service the connection
               serviceClient(clientSocket);
            }
         } catch (SocketException ie) {
            // fall through
         } catch (IOException ioe) {
            ioe.printStackTrace();
         }
      }
   }
}
