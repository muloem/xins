/*
 * $Id$
 */
package org.xins.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Properties;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
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
extends Object
implements Servlet {

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
    * The stored servlet configuration object.
    */
   private ServletConfig _config;

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

      // Store the ServletConfig object, per the Servlet API Spec, see:
      // http://java.sun.com/products/servlet/2.3/javadoc/javax/servlet/Servlet.html#getServletConfig()
      _config = config;

      String apiClassName = config.getInitParameter("org.xins.api.class");
      if (apiClassName == null || apiClassName.equals("")) {
         throw new ServletException("Unable to initialize servlet \"" + config.getServletName() + "\", API class should be set in init parameter \"api.class\".");
      }

      Properties settings = ServletUtils.settingsAsProperties(config);

      // Initialize Log4J
      configureLogger(settings);
      _log = Logger.getLogger(getClass().getName());
      if (_log == null) {
         throw new ServletException("Unable to initialize logger. Logger.getLogger(String) returned null.");
      }

      // Get the API class
      _log.debug("Loading API class: " + apiClassName);
      Class apiClass;
      try {
         apiClass = Class.forName(apiClassName);
      } catch (Exception e) {
         String message = "Failed to load API class: " + apiClassName;
         _log.error(message, e);
         throw new ServletException(message);
      }
      _log.debug("Loaded API class.");

      // Get the SINGLETON field
      _log.debug("Looking up declared field SINGLETON of API class.");
      Field singletonField;
      try {
         singletonField = apiClass.getDeclaredField("SINGLETON");
      } catch (Exception e) {
         String message = "Failed to obtain class field SINGLETON of API class.";
         _log.error(message, e);
         throw new ServletException(message);
      }
      _log.debug("Looked up declared field SINGLETON of API class.");

      // Get the value of the SINGLETON field
      _log.debug("Getting value of SINGLETON field of API class.");
      try {
         _api = (API) singletonField.get(null);
      } catch (Exception e) {
         String message = "Failed to get value of SINGLETON field of API class.";
         _log.error(message, e);
         throw new ServletException(message);
      }
      _log.info("Obtained API instance of class: " + apiClassName);

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

   public ServletConfig getServletConfig() {
      return _config;
   }

   public void service(ServletRequest request, ServletResponse response)
   throws IOException {

      // Set the content output type to XML
      response.setContentType("text/xml");

      // Call the API
      PrintWriter out = response.getWriter(); 
      _api.handleCall(request, out);

      // Flush
      out.flush();
   }

   public String getServletInfo() {
      return "XINS " + Library.getVersion() + " API Servlet";
   }

   public void destroy() {
      // empty
   }
}
