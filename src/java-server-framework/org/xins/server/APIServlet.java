/*
 * $Id$
 */
package org.xins.server;

import java.io.File;
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
import org.apache.log4j.helpers.NullEnumeration;
import org.xins.util.MandatoryArgumentChecker;
import org.xins.util.servlet.ServletUtils;
import org.xins.util.text.Replacer;

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
    * The logger used by this servlet. This field is never <code>null</code>.
    */
   private static final Logger LOG = Logger.getLogger(APIServlet.class.getName());

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

   /**
    * Configures the logger using the specified servlet configuration. This
    * method is called from {@link #init(ServletConfig)}.
    *
    * @param config
    *    the servlet configuration, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>config == null</code>.
    */
   private static void configureLogger(ServletConfig config)
   throws IllegalArgumentException, ServletException {

      // Convert the ServletConfig to a Properties object
      Properties settings = ServletUtils.settingsAsProperties(config);

      // Apply replacements
      try {
         settings = Replacer.replace(settings, '[', ']', System.getProperties());
      } catch (Replacer.Exception exception) {
         configureLoggerFallback();
         String message = "Failed to apply replacements to servlet initialization settings.";
         LOG.error(message, exception);
         throw new ServletException(message, exception);
      }

      // First see if a config file has been specified
      String configFile = settings.getProperty("org.apache.log4j.config");
      boolean doConfigure = true;
      if (configFile != null && configFile.length() > 0) {
         if (new File(configFile).exists()) {
            // TODO: configure delay for configureAndWatch
            PropertyConfigurator.configureAndWatch(configFile);
            doConfigure = false;
            LOG.debug("Using Log4J configuration file \"" + configFile + "\".");
         } else {
            configureLoggerFallback();
            doConfigure = false;
            LOG.error("Log4J configuration file \"" + configFile + "\" does not exist. Using fallback defaults.");
         }

      // If not, perform initialization with init settings
      } else {
         PropertyConfigurator.configure(settings);
      }

      // If Log4J is not initialized at this point, use fallback defaults
      if (doConfigure && LOG.getAllAppenders() instanceof NullEnumeration) {
         configureLoggerFallback();
         LOG.warn("No initialization settings found for Log4J. Using fallback defaults.");
      }
   }

   /**
    * Initializes Log4J with fallback default settings.
    */
   private static final void configureLoggerFallback() {
      Properties settings = new Properties();

      settings.setProperty("log4j.rootLogger",              "ALL, console");
      settings.setProperty("log4j.appender.console",        "org.apache.log4j.ConsoleAppender");
      settings.setProperty("log4j.appender.console.layout", "org.apache.log4j.SimpleLayout");

      PropertyConfigurator.configure(settings);
   }

   /**
    * Initializes an API instance based on the specified servlet
    * configuration.
    *
    * @param config
    *    the servlet configuration, cannot be <code>null</code>.
    *
    * @throws ServletException
    *    if an API instance could not be initialized.
    */
   private static API configureAPI(ServletConfig config)
   throws ServletException { 

      API api;

      // Determine the API class
      String apiClassName = config.getInitParameter("org.xins.api.class");
      if (apiClassName == null || apiClassName.trim().length() < 1) {
         throw new ServletException("Unable to initialize servlet \"" + config.getServletName() + "\", API class should be set in init parameter \"api.class\".");
      }

      // Load the API class
      Class apiClass;
      try {
         apiClass = Class.forName(apiClassName);
      } catch (Exception e) {
         String message = "Failed to load API class: \"" + apiClassName + "\".";
         LOG.error(message, e);
         throw new ServletException(message);
      }

      // Get the SINGLETON field
      Field singletonField;
      try {
         singletonField = apiClass.getDeclaredField("SINGLETON");
      } catch (Exception e) {
         String message = "Failed to lookup class field SINGLETON in API class \"" + apiClassName + "\".";
         LOG.error(message, e);
         throw new ServletException(message);
      }

      // Get the value of the SINGLETON field
      try {
         api = (API) singletonField.get(null);
      } catch (Exception e) {
         String message = "Failed to get value of SINGLETON field of API class \"" + apiClassName + "\".";
         LOG.error(message, e);
         throw new ServletException(message);
      }
      if (LOG.isDebugEnabled()) {
         LOG.debug("Obtained API instance of class: \"" + apiClassName + "\".");
      }

      // Initialize the API
      if (LOG.isDebugEnabled()) {
         LOG.debug("Initializing API.");
      }
      Properties settings = ServletUtils.settingsAsProperties(config);
      try {
         api.init(settings);
      } catch (Throwable e) {
         try {
            api.destroy();
         } catch (Throwable e2) {
            LOG.error("Caught " + e2.getClass().getName() + " while destroying API instance of class " + api.getClass().getName() + ". Ignoring.", e2);
         }

         String message = "Failed to initialize API.";
         LOG.error(message, e);
         throw new ServletException(message);
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("Initialized API.");
      }

      return api;
   }


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


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public void init(ServletConfig config)
   throws ServletException {

      // Check preconditions
      if (config == null) {
         throw new ServletException("No servlet configuration.");
      }

      synchronized (_stateLock) {
         initImpl(config);
      }
   }

   /**
    * Actually initializes this servlet.  This method is called from
    * {@link #init(ServletConfig)}.
    *
    * @param config
    *    the servlet configuration object, guaranteed not to be
    *    <code>null</code>.
    *
    * @throws ServletException
    *    if the initialization fails.
    */
   private void initImpl(ServletConfig config)
   throws ServletException {

      // Check preconditions
      if (_state != UNINITIALIZED) {
         throw new ServletException("Unable to initialize, state is not UNINITIALIZED.");
      } else if (config == null) {
         throw new ServletException("No servlet configuration, unable to initialize.");
      }

      // Set the state
      _state = INITIALIZING;

      // Store the ServletConfig object, per the Servlet API Spec, see:
      // http://java.sun.com/products/servlet/2.3/javadoc/javax/servlet/Servlet.html#getServletConfig()
      _config = config;

      // Initialize Log4J
      configureLogger(config);

      // Initialization starting
      String version = org.xins.server.Library.getVersion();
      if (LOG.isDebugEnabled()) {
         LOG.debug("XINS/Java Server Framework " + version + " is initializing.");
      }

      // Initialize API instance
      _api = configureAPI(config);

      // Initialization done
      if (LOG.isInfoEnabled()) {
         LOG.info("XINS/Java Server Framework " + version + " is initialized.");
      }

      // Finally enter the ready state
      _state = READY;
   }

   public ServletConfig getServletConfig() {
      return _config;
   }

   public void service(ServletRequest request, ServletResponse response)
   throws ServletException, IOException {

      // Check state
      if (_state != READY) {
         if (_state == UNINITIALIZED) {
            throw new ServletException("This servlet is not yet initialized.");
         } else if (_state == DISPOSING) {
            throw new ServletException("This servlet is currently being disposed.");
         } else if (_state == DISPOSED) {
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
      if (LOG.isDebugEnabled()) {
         LOG.debug("XINS/Java Server Framework shutdown initiated.");
      }

      synchronized (_stateLock) {
         _state = DISPOSING;
         _api.destroy();
         if (LOG != null) {
            LOG.info("XINS/Java Server Framework shutdown completed.");
         }
         _state = DISPOSED;
      }
   }
}
