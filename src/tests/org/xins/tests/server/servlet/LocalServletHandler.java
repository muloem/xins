/*
 * $Id$
 */
package org.xins.tests.server.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import org.xins.server.APIServlet;

/**
 * This class allows to invoke a XINS API without using HTTP.
 *
 * Example:
 * <code>
 * LocalServletHandler handler = LocalServletHandler.getInstance("c:\\test\\myproject.war");
 * String xmlResult = handler.query("http://127.0.0.1:8080/myproject/?_function=MyFunction&gender=f&personLastName=Lee");
 * </code>
 *
 * @version $Revision$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public class LocalServletHandler {
   
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
    * Creates a Servlet handler that allow to invoke a Servlet without starting
    * a HTTP server.
    *
    * @param warFile
    *    the location of the war file containing the Servlet, cannot be 
    *    <code>null</code>.
    */
   private LocalServletHandler(String warFile) {
      try {
         initServlet(warFile);
      } catch (ServletException ex) {
         ex.printStackTrace();
      }
   }
   
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
    * Gets the instance of the Servlet handler.
    */
   public final static LocalServletHandler getInstance(String warFile) {
      if (_servlet == null) {
         _servlet = new LocalServletHandler(warFile);
      }
      return _servlet;
   }
   
   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * This Servlet handler.
    */
   private static LocalServletHandler _servlet;
   
   /**
    * The Servlet started by this Servlet handler.
    */
   private APIServlet _apiServlet;

   
   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
   
   /**
    * Queries the Servlet with the specified URL.
    *
    * @param url
    *    the url query for the request.
    */
   public String query(String url) throws IOException {
      LocalHTTPServletRequest request = new LocalHTTPServletRequest(url.replace('&', ','));
      LocalHTTPServletResponse response = new LocalHTTPServletResponse();
      _apiServlet.service(request, response);
      return response.getResult();
   }
   
   /**
    * Disposes the Servlet and closes this Servlet handler.
    */
   public void close() {
      _apiServlet.destroy();
      _servlet = null;
   }
}
