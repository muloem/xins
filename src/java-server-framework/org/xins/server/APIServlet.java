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

   /**
    * Constant indicating the <em>uninitialized</em> state. See
    * {@link #_state}.
    */
   private static final int UNINITIALIZED = 0;

   /**
    * Constant indicating the <em>initializing</em> state. See
    * {@link #_state}.
    */
   private static final int INITIALIZING = 1;

   /**
    * Constant indicating the <em>ready</em> state. See
    * {@link #_state}.
    */
   private static final int READY = 2;

   /**
    * Constant indicating the <em>disposing</em> state. See
    * {@link #_state}.
    */
   private static final int DISPOSING = 3;

   /**
    * Constant indicating the <em>disposed</em> state. See
    * {@link #_state}.
    */
   private static final int DISPOSED = 4;


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
      _stateLock = new Object();
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The current state.
    */
   private int _state;

   /**
    * The object to synchronize on when the state is changed.
    */
   private Object _stateLock;

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
      synchronized (_stateLock) {
         initImpl(config);
      }
   }

   private void initImpl(ServletConfig config)
   throws ServletException {

      _state = INITIALIZING;

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

      _log.debug("XINS/Java Server Framework is initializing.");

      // Get the API class
      Class apiClass;
      try {
         apiClass = Class.forName(apiClassName);
      } catch (Exception e) {
         String message = "Failed to load API class: \"" + apiClassName + "\".";
         _log.error(message, e);
         throw new ServletException(message);
      }

      // Get the SINGLETON field
      Field singletonField;
      try {
         singletonField = apiClass.getDeclaredField("SINGLETON");
      } catch (Exception e) {
         String message = "Failed to lookup class field SINGLETON in API class \"" + apiClassName + "\".";
         _log.error(message, e);
         throw new ServletException(message);
      }

      // Get the value of the SINGLETON field
      try {
         _api = (API) singletonField.get(null);
      } catch (Exception e) {
         String message = "Failed to get value of SINGLETON field of API class \"" + apiClassName + "\".";
         _log.error(message, e);
         throw new ServletException(message);
      }
      _log.debug("Obtained API instance of class: \"" + apiClassName + "\".");

      // Initialize the API
      try {
         _log.debug("Initializing API.");
         _api.init(settings);
      } catch (Throwable e) {
         String message = "Failed to initialize API.";
         _log.error(message, e);
         throw new ServletException(message);
      }

      _log.info("XINS/Java Server Framework is initialized.");

      _state = READY;
   }

   /**
    * Configures the logger using the specified settings. This method is
    * called from {@link #init(ServletConfig)}.
    *
    * @param settings
    *    the initialization settings, not <code>null</code>.
    */
   private void configureLogger(Properties settings) {

      // TODO: Take a better approach to checking if Log4J is initialized
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
   throws ServletException, IOException {

      int state = _state;

      // Check state
      if (state != READY) {
         if (state == UNINITIALIZED) {
            throw new ServletException("This servlet is not yet initialized.");
         } else if (state == DISPOSING) {
            throw new ServletException("This servlet is currently being disposed.");
         } else if (state == DISPOSED) {
            throw new ServletException("This servlet is disposed.");
         } else {
            throw new InternalError("This servlet is not ready, the state is unknown.");
         }
      }

      // TODO: Only set the content type to XML if there is no uncaught exception

      // Set the content output type to XML
      response.setContentType("text/xml");

      // Call the API
      PrintWriter out = response.getWriter(); 
      _api.handleCall(request, out);

      // TODO: Support and use OutputStream instead of Writer, for improved
      //       performance

      // Flush
      out.flush();
   }

   public String getServletInfo() {
      return "XINS " + Library.getVersion() + " API Servlet";
   }

   public void destroy() {
      if (_log != null) {
         _log.debug("XINS/Java Server Framework shutdown initiated.");
      }

      synchronized (_stateLock) {
         _state = DISPOSING;
         _api.destroy();
         if (_log != null) {
            _log.info("XINS/Java Server Framework shutdown completed.");
         }
         _state = DISPOSED;
      }
   }
}
