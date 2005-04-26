/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.servlet.container;

import java.io.File;
import java.io.IOException;
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
    *
    * @throws ServletException
    *    if the Servlet cannot be created.
    */
   public LocalServletHandler(File warFile) throws ServletException {
      initServlet(warFile);
   }

   /**
    * Creates a Servlet handler that allow to invoke a Servlet without starting
    * a HTTP server.
    *
    * @param servletClassName
    *    The name of the servlet's class to load, cannot be <code>null</code>.
    *
    * @throws ServletException
    *    if the Servlet cannot be created.
    */
   public LocalServletHandler(String servletClassName) throws ServletException {
      initServlet(servletClassName);
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
    * @throws ServletException
    *    if the Servlet cannot be loaded.
    */
   public void initServlet(File warFile) throws ServletException {
      // create and initiliaze the Servlet
      Log.log_1503(warFile.getPath());
      try {
         LocalServletConfig servletConfig = new LocalServletConfig(warFile);
         _apiServlet = (HttpServlet) Class.forName(servletConfig.getServletClass()).newInstance();
         _apiServlet.init(servletConfig);
      } catch (ServletException exception) {
         Log.log_1508(exception);
         throw (ServletException)exception;
      } catch (Exception exception) {
         exception.printStackTrace();
         Log.log_1509(exception);
         throw new ServletException(exception);
      }
   }

   /**
    * Initializes the Servlet.
    *
    * @param servletClassName
    *    The name of the servlet's class to load, cannot be <code>null</code>.
    *
    * @throws ServletException
    *    if the Servlet cannot be loaded.
    */
   public void initServlet(String servletClassName) throws ServletException {
      // create and initiliaze the Servlet
      //Log.log_1503(warFile.getPath());
      try {
         _apiServlet = (HttpServlet) Class.forName(servletClassName).newInstance();
         _apiServlet.init();
      } catch (ServletException exception) {
         Log.log_1508(exception);
         throw (ServletException)exception;
      } catch (Exception exception) {
         exception.printStackTrace();
         Log.log_1509(exception);
         throw new ServletException(exception);
      }
   }

   /**
    * Gets the Servlet.
    *
    * @return
    *    the created Servlet or <code>null</code> if no Servlet was created.
    */
   public Object getServlet() {
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
   }
}
