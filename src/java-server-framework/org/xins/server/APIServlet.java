/*
 * $Id$
 */
package org.xins.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.xins.util.servlet.ServletUtils;

/**
 * Servlet that forwards request to an <code>API</code>.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public final class APIServlet
extends HttpServlet {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>APIServlet</code> object.
    */
   public APIServlet() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The API that this servlet forwards requests to.
    */
   private API _api;

   /**
    * The logger used by this servlet. This field is initialized by
    * {@link #init(ServletConfig)} and set to a non-<code>null</code> value.
    */
   private Logger _log;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public void init(ServletConfig config)
   throws ServletException {

      // Call the superclass, in accordance with the Servlet API spec, see:
      // http://java.sun.com/products/servlet/2.3/javadoc/javax/servlet/GenericServlet.html#init(javax.servlet.ServletConfig)
      super.init(config);

      String apiClass = config.getInitParameter("org.xins.api.class");
      if (apiClass == null || apiClass.equals("")) {
         throw new ServletException("Unable to initialize servlet \"" + config.getServletName() + "\", API class should be set in init parameter \"api.class\".");
      }

      Properties settings = ServletUtils.settingsAsProperties(config);

      // Initialize Log4J
      configureLogger(settings);
      _log = Logger.getLogger(getClass().getName());
      if (_log == null) {
         throw new ServletException("Unable to initialize logger. Logger.getLogger(String) returned null.");
      }

      // Create an API instance
      try {
         _log.debug("Attempting to create API instance of class: " + apiClass);
         _api = (API) Class.forName(apiClass).newInstance();
         _log.info("Created API instance of class: " + apiClass);
      } catch (Exception e) {
         String message = "Failed to create API instance of class: " + apiClass;
         _log.error(message, e);
         throw new ServletException(message);
      }

      // Initialize the API
      try {
         _log.debug("Initializing API.");
         _api.init(settings);
         _log.info("Initialized API.");
      } catch (Throwable e) {
         String message = "Failed to initialize API.";
         _log.error(message, e);
         throw new ServletException(message);
      }
   }

   /**
    * Configures the logger using the specified settings. This method is
    * called from {@link #init(ServletConfig)}.
    *
    * @param settings
    *    the initialization settings, not <code>null</code>.
    */
   private void configureLogger(Properties settings) {

      String value = settings.getProperty("log4j.rootCategory");
      if (value == null || "".equals(value)) {
         settings.setProperty("log4j.rootCategory",                              "DEBUG, console");
         settings.setProperty("log4j.appender.console",                          "org.apache.log4j.ConsoleAppender");
         settings.setProperty("log4j.appender.console.layout",                   "org.apache.log4j.PatternLayout");
         settings.setProperty("log4j.appender.console.layout.ConversionPattern", "%d %-5p - %m%n");
      }

      PropertyConfigurator.configure(settings);
   }

   protected void doGet(HttpServletRequest req, HttpServletResponse resp)
   throws IOException {
      handleRequest(req, resp);
   }

   protected void doPost(HttpServletRequest req, HttpServletResponse resp)
   throws IOException {
      handleRequest(req, resp);
   }

   /**
    * Handles all HTTP GET and POST requests.
    *
    * @param request
    *    the HTTP request, not <code>null</code>.
    *
    * @param response
    *    the HTTP response, not <code>null</code>.
    *
    * @throws IOException
    *    there was an I/O problem.
    */
   private void handleRequest(HttpServletRequest request,
                              HttpServletResponse response)
   throws IOException {

      // Set the content output type to XML
      response.setContentType("text/xml");

      // Call the API
      PrintWriter out = response.getWriter(); 
      _api.handleCall(request, out);

      // Flush
      out.flush();
   }
}
