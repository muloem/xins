/*
 * $Id$
 */
package org.xins.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Properties;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.NullEnumeration;
import org.xins.util.MandatoryArgumentChecker;
import org.xins.util.collections.PropertiesPropertyReader;
import org.xins.util.io.FileWatcher;
import org.xins.util.servlet.ServletConfigPropertyReader;
import org.xins.util.text.Replacer;

/**
 * Servlet that forwards requests to an <code>API</code>.
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
    * The <em>INITIAL</em> state.
    */
   private static final State INITIAL = new State("INITIAL");

   /**
    * The <em>BOOTSTRAPPING_FRAMEWORK</em> state.
    */
   private static final State BOOTSTRAPPING_FRAMEWORK = new State("BOOTSTRAPPING_FRAMEWORK");

   /**
    * The <em>FRAMEWORK_BOOTSTRAP_FAILED</em> state.
    */
   private static final State FRAMEWORK_BOOTSTRAP_FAILED = new State("FRAMEWORK_BOOTSTRAP_FAILED");

   /**
    * The <em>CONSTRUCTING_API</em> state.
    */
   private static final State CONSTRUCTING_API = new State("CONSTRUCTING_API");

   /**
    * The <em>API_CONSTRUCTION_FAILED</em> state.
    */
   private static final State API_CONSTRUCTION_FAILED = new State("API_CONSTRUCTION_FAILED");

   /**
    * The <em>BOOTSTRAPPING_API</em> state.
    */
   private static final State BOOTSTRAPPING_API = new State("BOOTSTRAPPING_API");

   /**
    * The <em>API_BOOTSTRAP_FAILED</em> state.
    */
   private static final State API_BOOTSTRAP_FAILED = new State("API_BOOTSTRAP_FAILED");

   /**
    * The <em>INITIALIZING_API</em> state.
    */
   private static final State INITIALIZING_API = new State("INITIALIZING_API");

   /**
    * The <em>API_INITIALIZATION_FAILED</em> state.
    */
   private static final State API_INITIALIZATION_FAILED = new State("API_INITIALIZATION_FAILED");

   /**
    * The <em>READY</em> state.
    */
   private static final State READY = new State("READY");

   /**
    * The <em>DISPOSING</em> state.
    */
   private static final State DISPOSING = new State("DISPOSING");

   /**
    * The <em>DISPOSED</em> state.
    */
   private static final State DISPOSED = new State("DISPOSED");

   /**
    * The expected version of the Java Servlet Specification, major part.
    */
   private static final int EXPECTED_SERVLET_VERSION_MAJOR = 2;

   /**
    * The expected version of the Java Servlet Specification, minor part.
    */
   private static final int EXPECTED_SERVLET_VERSION_MINOR = 3;

   /**
    * The name of the system property that specifies the location of the
    * configuration file.
    */
   public static final String CONFIG_FILE_SYSTEM_PROPERTY = "org.xins.server.config";

   /**
    * The name of the build property that specifies the name of the
    * API class to load.
    */
   public static final String API_CLASS_PROPERTY = "org.xins.api.class";

   /**
    * The name of the configuration property that specifies the interval
    * for the configuration file modification checks, in seconds.
    */
   public static final String CONFIG_RELOAD_INTERVAL_PROPERTY = "org.xins.server.config.reload";

   /**
    * The default configuration file modification check interval, in seconds.
    */
   public static final int DEFAULT_CONFIG_RELOAD_INTERVAL = 60;


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
      _state     = INITIAL;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The current state.
    */
   private State _state;

   /**
    * Lock for <code>_state</code>
    */
   private Object _stateLock;

   /**
    * The stored servlet configuration object.
    */
   private ServletConfig _servletConfig;

   /**
    * The name of the configuration file.
    */
   private String _configFile;

   /**
    * The API that this servlet forwards requests to.
    */
   private API _api;

   /**
    * Description of the current error, if any. Will be returned by
    * {@link #service(ServletRequest,ServletResponse)} if and only if the
    * state is not {@link #READY}.
    */
   private String _error;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Initializes this servlet using the specified configuration. The
    * (required) {@link ServletConfig} argument is stored internally and is
    * returned from {@link #getServletConfig()}.
    *
    * <p>The initialization procedure will take required information from 3
    * sources, initially:
    *
    * <dl>
    *    <dt><strong>1. Build-time settings</strong></dt>
    *    <dd>The application package contains a <code>web.xml</code> file with
    *        build-time settings. Some of these settings are required in order
    *        for the XINS/Java Server Framework to start up, while others are
    *        optional. These build-time settings are passed to the servlet by
    *        the application server as a {@link ServletConfig} object. See
    *        {@link #init(ServletConfig)}.
    *        <br />The servlet configuration is the responsibility of the
    *        <em>assembler</em>.</dd>
    *
    *    <dt><strong>2. System properties</strong></dt>
    *    <dd>The location of the configuration file must be passed to the
    *        Java VM at startup, as a system property.
    *        <br />System properties are the responsibility of the
    *        <em>system administrator</em>.
    *        <br />Example:
    *        <br /><code>java -Dorg.xins.server.config=`pwd`/conf/xins.properties orion.jar</code></dd>
    *
    *    <dt><strong>3. Configuration file</strong></dt>
    *    <dd>The configuration file should contain runtime configuration
    *        settings, like the settings for the logging subsystem.
    *        <br />System properties are the responsibility of the
    *        <em>system administrator</em>.
    *        <br />Example contents for a configuration file:
    *        <blockquote><code>log4j.rootLogger=DEBUG, console
    *        <br />log4j.appender.console=org.apache.log4j.ConsoleAppender
    *        <br />log4j.appender.console.layout=org.apache.log4j.PatternLayout
    *        <br />log4j.appender.console.layout.ConversionPattern=%d %-5p [%c] %m%n</code></blockquote>
    * </dl>
    *
    * @param config
    *    the {@link ServletConfig} object which contains build properties for
    *    this servlet, as specified by the <em>assembler</em>, cannot be
    *    <code>null</code>.
    *
    * @throws ServletException
    *    if <code>config == null</code>, if this servlet is not uninitialized
    *    or if the initialization failed for some other reason.
    */
   public void init(ServletConfig config) {

      //-------------------------------------------------------------------//
      //                     Checks and preparations                       //
      //-------------------------------------------------------------------//

      // Make sure the Library class is initialized
      String version = Library.getVersion();

      // Get a reference to the appropriate logger
      Logger log = Library.BOOTSTRAP_LOG;

      // Check preconditions
      synchronized (_stateLock) {
         if (_state != INITIAL) {
            String message = "Application server malfunction detected. Cannot initialize servlet. State is " + _state + " instead of " + INITIAL + '.';
            // This is not fatal, but an error, since the framework is already
            // initialized.
            log.error(message);
            throw new Error(message);
         } else if (config == null) {
            String message = "Application server malfunction detected. Cannot initialize servlet. No servlet configuration object passed.";
            log.fatal(message);
            throw new Error(message);
         }

         // Get the ServletContext
         ServletContext context = config.getServletContext();
         if (context == null) {
            String message = "Application server malfunction detected. Cannot initialize servlet. No servlet context available.";
            log.fatal(message);
            throw new Error(message);
         }

         // Check the expected vs implemented Java Servlet API version
         int major = context.getMajorVersion();
         int minor = context.getMinorVersion();
         if (major != EXPECTED_SERVLET_VERSION_MAJOR || minor != EXPECTED_SERVLET_VERSION_MINOR) {
            log.warn("Application server implements Java Servlet API version " + major + '.' + minor + " instead of the expected version " + EXPECTED_SERVLET_VERSION_MAJOR + '.' + EXPECTED_SERVLET_VERSION_MINOR + ". The application may or may not work correctly.");
         }

         // Store the ServletConfig object, per the Servlet API Spec, see:
         // http://java.sun.com/products/servlet/2.3/javadoc/javax/servlet/Servlet.html#getServletConfig()
         _servletConfig = config;


         //----------------------------------------------------------------//
         //                     Bootstrap framework                        //
         //----------------------------------------------------------------//

         // Proceed to first actual stage
         _state = BOOTSTRAPPING_FRAMEWORK;
         log.debug("Bootstrapping XINS/Java Server Framework.");

         // Determine configuration file location
         try {
            _configFile = System.getProperty(CONFIG_FILE_SYSTEM_PROPERTY);
         } catch (SecurityException exception) {
            _state = FRAMEWORK_BOOTSTRAP_FAILED;
            _error = "System administration issue detected. Unable to get system property \"" + CONFIG_FILE_SYSTEM_PROPERTY + "\" due to a security restriction.";
            log.error(_error, exception);
            return;
         }
         
         // Property value must be set
         // NOTE: Don't trim the configuration file name, since it may start
         //       with a space or other whitespace character.
         if (_configFile == null || _configFile.length() < 1) {
            _state = FRAMEWORK_BOOTSTRAP_FAILED;
            _error = "System administration issue detected. System property \"" + CONFIG_FILE_SYSTEM_PROPERTY + "\" is not set.";
            log.error(_error);
            return;
         }

         // Apply the properties to the framework
         Properties runtimeProperties = applyConfigFile(log);


         //----------------------------------------------------------------//
         //                        Construct API                           //
         //----------------------------------------------------------------//

         // Proceed to next stage
         _state = CONSTRUCTING_API;
         log.debug("Constructing API.");

         // Determine the API class
         String apiClassName = config.getInitParameter(API_CLASS_PROPERTY);
         if (apiClassName == null || apiClassName.trim().length() < 1) {
            _state = API_CONSTRUCTION_FAILED;
            _error = "Invalid application package. API class name not set in build property \"" + API_CLASS_PROPERTY + "\".";
            log.fatal(_error);
            return;
         }

         // Load the API class
         Class apiClass;
         try {
            apiClass = Class.forName(apiClassName);
         } catch (Throwable exception) {
            _state = API_CONSTRUCTION_FAILED;
            _error = "Invalid application package. Failed to load API class \"" + apiClassName + "\", as set in build property \"" + API_CLASS_PROPERTY + "\" due to unexpected " + exception.getClass().getName() + '.';
            log.fatal(_error);
            return;
         }

         // Check that the loaded API class is derived from the API base class
         if (! API.class.isAssignableFrom(apiClass)) {
            _state = API_CONSTRUCTION_FAILED;
            _error = "Invalid application package. The \"" + apiClassName + "\" is not derived from class " + API.class.getName() + '.';
            log.fatal(_error);
            return;
         }

         // Get the SINGLETON field
         Field singletonField;
         try {
            singletonField = apiClass.getDeclaredField("SINGLETON");
         } catch (Exception exception) {
            _state = API_CONSTRUCTION_FAILED;
            _error = "Invalid application package. Failed to lookup class field SINGLETON in API class \"" + apiClassName + "\" due to unexpected " + exception.getClass().getName() + '.';
            log.fatal(_error, exception);
            return;
         }

         // Get the value of the SINGLETON field
         try {
            _api = (API) singletonField.get(null);
         } catch (Exception exception) {
            _state = API_CONSTRUCTION_FAILED;
            _error = "Invalid application package. Failed to get value of the SINGLETON field of API class \"" + apiClassName + "\". Caught unexpected " + exception.getClass().getName() + '.';
            log.fatal(_error, exception);
            return;
         }

         // Make sure that the field is an instance of that same class
         if (_api == null) {
            _state = API_CONSTRUCTION_FAILED;
            _error = "Invalid application package. The value of the SINGLETON field of API class \"" + apiClassName + "\" is null.";
            log.fatal(_error);
            return;
         } else if (_api.getClass() != apiClass) {
            _state = API_CONSTRUCTION_FAILED;
            _error = "Invalid application package. The value of the SINGLETON field of API class \"" + apiClassName + "\" is not an instance of that class.";
            log.fatal(_error);
            return;
         }

         // Get the name of the API
         String apiName = _api.getName();
         log.debug("Constructed \"" + apiName + "\" API.");


         //----------------------------------------------------------------//
         //                        Bootstrap API                           //
         //----------------------------------------------------------------//

         // Proceed to next stage
         _state = BOOTSTRAPPING_API;
         log.debug("Bootstrapping \"" + apiName + "\" API.");

         try {
            _api.bootstrap(new ServletConfigPropertyReader(config));
         } catch (Throwable exception) {
            _state = API_BOOTSTRAP_FAILED;
            _error = "Application package may be invalid. Unable to bootstrap \"" + apiName + "\" API due to unexpected " + exception.getClass().getName() + '.';
            log.fatal(_error, exception);
            return;
         }

         log.info("Bootstrapped \"" + apiName + "\" API.");

         // Watch the configuration file
         // TODO: Do this somewhere else?
         FileWatcher.Listener listener = new ConfigurationFileListener();
         int interval = 10; // TODO: Read from config file
         FileWatcher watcher = new FileWatcher(_configFile, interval, listener);
         watcher.start(); // XXX: Start after API is initialized ?
         log.info("Using config file \"" + _configFile + "\". Checking for changes every " + interval + " seconds.");



         //----------------------------------------------------------------//
         //                      Initialize the API                        //
         //----------------------------------------------------------------//

         // Proceed to next stage
         log = Library.INIT_LOG;
         _state = INITIALIZING_API;
         log.debug("Initializing \"" + apiName + "\" API.");

         try {
            _api.init(new PropertiesPropertyReader(runtimeProperties));
         } catch (Throwable e) {
            _state = API_INITIALIZATION_FAILED;
            _error = "Failed to initialize \"" + apiName + "\" API.";
            log.error(_error);
            return;
         }

         // Finished!
         _state = READY;
         log.debug("Initialized \"" + apiName + "\" API.");
      }
   }

   /**
    * Reads the configuration file and applies the settings in it to this
    * library. If this fails, then an error is logged on the specified logger.
    * Still, a {@link Properties} object is always returned.
    *
    * <p>Note that the settings are <em>not</em> applied to the API.
    *
    * @param log
    *    the logger to log messages to, should not be <code>null</code>.
    *
    * @return
    *    the properties read from the config file, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>log = null</code>.
    */
   private Properties applyConfigFile(Logger log)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("log", log);

      Properties properties = new Properties();

      try {
         FileInputStream in = new FileInputStream(_configFile);
         properties.load(in);

         Library.configure(log, properties);
      } catch (FileNotFoundException exception) {
         log.error("System administration issue detected. Configuration file \"" + _configFile + "\" cannot be opened.");
      } catch (SecurityException exception) {
         log.error("System administration issue detected. Access denied while loading configuration file \"" + _configFile + "\".");
      } catch (IOException exception) {
         log.error("System administration issue detected. Unable to read configuration file \"" + _configFile + "\".");
      }

      return properties;
   }

   /**
    * Returns the <code>ServletConfig</code> object which contains the
    * build properties for this servlet. The returned {@link ServletConfig}
    * object is the one passed to the {@link #init(ServletConfig)} method.
    *
    * @return
    *    the {@link ServletConfig} object that was used to initialize this
    *    servlet, not <code>null</code> if this servlet is indeed already
    *    initialized.
    */
   public ServletConfig getServletConfig() {
      return _servletConfig;
   }

   /**
    * Handles a request to this servlet.
    *
    * @param request
    *    the servlet request, should not be <code>null</code>.
    *
    * @param response
    *    the servlet response, should not be <code>null</code>.
    *
    * @throws ServletException
    *    if the state of this servlet is not <em>ready</em>, or
    *    if <code>request == null || response == null</code>.
    *
    * @throws IOException
    *    if there is an error error writing to the response output stream.
    */
   public void service(ServletRequest request, ServletResponse response)
   throws ServletException, IOException {

      // Determine current time
      long start = System.currentTimeMillis();

      // Check state
      if (_state != READY) {
         // TODO: This is not an application server malfunction.
         // TODO: Return current state and _error
         String message = "Application server malfunction detected. State is " + _state + " instead of " + READY + '.';
         Library.RUNTIME_LOG.error(message);
         throw new ServletException(message);
      }

      // Check arguments
      if (request == null || response == null) {
         String message = "Application server malfunction detected. ";
         if (request == null && response == null) {
            message += "Both request and response are null.";
         } else if (request == null) {
            message += "Request is null.";
         } else {
            message += "Response is null.";
         }
         Library.RUNTIME_LOG.error(message);
         throw new ServletException(message);
      }

      // TODO: Support and use OutputStream instead of Writer, for improved
      //       performance

      // Call the API
      CallResult result = _api.handleCall(start, request);

      // Determine the XSLT to link to
      String xslt = request.getParameter("_xslt");

      // Send the XML output to the stream and flush
      PrintWriter out = response.getWriter(); 
      response.setContentType("text/xml");
      CallResultOutputter.output(out, result, xslt);
      out.flush();
   }

   /**
    * Returns information about this servlet, as plain text.
    *
    * @return
    *    textual description of this servlet, not <code>null</code> and not an
    *    empty character string.
    */
   public String getServletInfo() {
      return "XINS/Java Server Framework " + Library.getVersion();
   }

   /**
    * Destroys this servlet. A best attempt will be made to release all
    * resources.
    *
    * <p>After this method has finished, it will set the state to
    * <em>disposed</em>. In that state no more requests will be handled.
    */
   public void destroy() {

      Library.SHUTDOWN_LOG.debug("Shutting down XINS/Java Server Framework.");

      synchronized (_stateLock) {
         // Set the state temporarily to DISPOSING
         _state = DISPOSING;

         // Destroy the API
         if (_api != null) {
            _api.destroy();
         }

         // Set the state to DISPOSED
         _state = DISPOSED;
      }

      Library.SHUTDOWN_LOG.info("XINS/Java Server Framework shutdown completed.");
   }


   //-------------------------------------------------------------------------
   // Inner classes
   //-------------------------------------------------------------------------

   /**
    * State of an <code>APIServlet</code>.
    *
    * @version $Revision$ $Date$
    * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
    *
    * @since XINS 0.121
    */
   private static final class State extends Object {

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      /**
       * Constructs a new <code>State</code> object.
       *
       * @param name
       *    the name of this state, cannot be <code>null</code>.
       *
       * @throws IllegalArgumentException
       *    if <code>name == null</code>.
       */
      private State(String name) throws IllegalArgumentException {

         // Check preconditions
         MandatoryArgumentChecker.check("name", name);

         _name = name;
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      /**
       * The name of this state. Cannot be <code>null</code>.
       */
      private final String _name; 


      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------

      /**
       * Returns the name of this state.
       *
       * @return
       *    the name of this state, cannot be <code>null</code>.
       */
      String getName() {
         return _name;
      }

      /**
       * Returns a textual representation of this object.
       *
       * @return
       *    the name of this state, never <code>null</code>.
       */
      public String toString() {
         return _name;
      }
   }

   /**
    * Listener that reloads the configuration file if it changes.
    *
    * @version $Revision$ $Date$
    * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
    *
    * @since XINS 0.121
    */
   private final class ConfigurationFileListener
   extends Object
   implements FileWatcher.Listener {

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      /**
       * Constructs a new <code>ConfigurationFileListener</code> object.
       */
      private ConfigurationFileListener() {
         // empty
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------

      public void fileModified() {
         Library.INIT_LOG.info("Configuration file \"" + _configFile + "\" changed. Re-initializing XINS/Java Server Framework.");
         applyConfigFile(Library.INIT_LOG);
         // TODO: reinit API
         Library.INIT_LOG.info("XINS/Java Server Framework re-initialized.");
      }

      public void fileNotFound() {
         Library.INIT_LOG.error("System administration issue detected. Configuration file \"" + _configFile + "\" cannot be opened.");
      }

      public void fileNotModified() {
         Library.INIT_LOG.debug("Configuration file \"" + _configFile + "\" is not modified.");
      }

      public void securityException(SecurityException exception) {
         Library.INIT_LOG.error("System administration issue detected. Access denied while reading file \"" + _configFile + "\".");
      }
   }
}
