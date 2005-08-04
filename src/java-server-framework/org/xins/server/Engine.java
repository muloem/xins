/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.NDC;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.Utils;
import org.xins.common.collections.InvalidPropertyValueException;
import org.xins.common.collections.MissingRequiredPropertyException;
import org.xins.common.collections.PropertyReader;
import org.xins.common.manageable.InitializationException;
import org.xins.common.servlet.ServletConfigPropertyReader;
import org.xins.common.text.TextUtils;
import org.xins.logdoc.ExceptionUtils;

/**
 * XINS server engine. An <code>Engine</code> is a delegate of the
 * {@link APIServlet} that is responsible for initialization and request
 * handling.
 *
 * <p>When an <code>Engine</code> instance is constructed, it gathers
 * initialization information from different sources:
 *
 * <dl>
 *    <dt><strong>1. Build-time settings</strong></dt>
 *    <dd>The application package contains a <code>web.xml</code> file with
 *    build-time settings. Some of these settings are required in order for
 *    the XINS/Java Server Framework to start up, while others are optional.
 *    These build-time settings are passed to the servlet by the application
 *    server as a {@link ServletConfig} object. See
 *    {@link APIServlet#init(ServletConfig)}.
 *    <br>The servlet configuration is the responsibility of the
 *    <em>assembler</em>.</dd>
 *
 *    <dt><strong>2. System properties</strong></dt>
 *    <dd>The location of the configuration file must be passed to the Java VM
 *    at startup, as a system property.
 *    <br>System properties are the responsibility of the
 *    <em>system administrator</em>.
 *    <br>Example:
 *    <br><code>java -Dorg.xins.server.config=`pwd`/config/xins.properties
 *    -jar orion.jar</code></dd>
 *
 *    <dt><strong>3. Configuration file</strong></dt>
 *    <dd>The configuration file should contain runtime configuration
 *    settings, like the settings for the logging subsystem.
 *    <br>Runtime properties are the responsibility of the
 *    <em>system administrator</em>.
 *    <br>Example contents for a configuration file:
 *    <blockquote><code>log4j.rootLogger=DEBUG, console
 *    <br>log4j.appender.console=org.apache.log4j.ConsoleAppender
 *    <br>log4j.appender.console.layout=org.apache.log4j.PatternLayout
 *    <br>log4j.appender.console.layout.ConversionPattern=%d %-5p [%c]
 *    %m%n</code></blockquote>
 * </dl>
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 * @author Mees Witteman (<a href="mailto:mees.witteman@nl.wanadoo.com">mees.witteman@nl.wanadoo.com</a>)
 */
final class Engine extends Object {

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
    * Constructs a new <code>Engine</code> object.
    *
    * @param config
    *    the {@link ServletConfig} object which contains build properties for
    *    this servlet, as specified by the <em>assembler</em>, cannot be
    *    <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>config == null</code>.
    *
    * @throws ServletException
    *    if the engine could not be constructed.
    */
   Engine(final ServletConfig config)
   throws IllegalArgumentException, ServletException {

      // Check preconditions
      MandatoryArgumentChecker.check("config", config);

      // Prepare the calling conventions cache
      _conventionCache = new HashMap();

      // Construct the EngineStarter
      EngineStarter starter = new EngineStarter(this, config, _state);

      // Log some initial boot messages
      starter.logBootMessages();

      // Construct a configuration manager and store the servlet configuration
      _configManager = new ConfigManager(this, config);
      _servletConfig = config;

      // Proceed to first actual stage
      _state.setState(EngineState.BOOTSTRAPPING_FRAMEWORK);

      // Read configuration details
      _configManager.determineConfigFile();
      _configManager.readRuntimeProperties();

      // Log version of XINS/Java Server Framework
      starter.checkAndLogVersionNumber();

      // Construct and bootstrap the API
      _api = starter.constructAPI();
      starter.bootstrapAPI(_api);

      // Initialize the configuration manager
      _configManager.init();

      // TODO: Make sure _apiName               is not null
      // TODO: Make sure _runtimeProperties     is not null
      // TODO: Make sure _defaultConventionName is not null
      // TODO: Make sure _defaultConvention     is not null
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The state machine for this engine. Never <code>null</code>.
    */
   private final EngineStateMachine _state = new EngineStateMachine();

   /**
    * The stored servlet configuration object. Never <code>null</code>.
    */
   private final ServletConfig _servletConfig;

   /**
    * The API that this engine forwards requests to. Never <code>null</code>.
    */
   private final API _api;

   /**
    * The name of the API. Never <code>null</code>.
    */
   private String _apiName;

   /**
    * Manager for the runtime configuration file. Never <code>null</code>.
    */
   private final ConfigManager _configManager;

   /**
    * The set of properties read from the runtime configuration file. Never
    * <code>null</code>.
    */
   private PropertyReader _runtimeProperties;

   /**
    * The name of the default calling convention for this engine. This field
    * can never be <code>null</code> and must always be in sync with
    * {@link #_defaultConvention}.
    *
    * <p>If no calling convention is specified in a request, then the default
    * calling convention is used.
    */
   private String _defaultConventionName;

   /**
    * The default calling convention for this engine. <p>This field can never
    * be <code>null</code> and must always be in sync with
    * {@link #_defaultConventionName}.
    *
    * <p>If no calling convention is specified in a request, then the default
    * calling convention is used.
    */
   private CallingConvention _defaultConvention;

   /**
    * The cache for the calling conventions other than the default one.
    * The key is the name of the calling convention, the value is the calling
    * convention object. This field is never <code>null</code>.
    */
   private final Map _conventionCache;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Initializes or re-initializes the runtime properties. This is a callback
    * method for the {@link ConfigManager}.
    *
    * @param newProperties
    *    the new runtime properties, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>properties == null</code>.
    */
   void setRuntimeProperties(final PropertyReader newProperties)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("newProperties", newProperties);

      // Store the runtime properties
      _runtimeProperties = newProperties;
   }

   /**
    * Initializes the API name. This is a callback method for the
    * {@link EngineStarter}.
    *
    * @param name
    *    the name for the API, cannot be <code>null</code>.
    *
    * @throws IllegalStateException
    *    if the API name is already set.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null</code>.
    */
   void initAPIName(final String name)
   throws IllegalStateException, IllegalArgumentException {

      // Check preconditions
      if (_apiName != null) {
         throw new IllegalStateException("API name is already initialized.");
      }
      MandatoryArgumentChecker.check("name", name);

      // Store the API name
      _apiName = name;
   }

   /**
    * Initializes the API using the current runtime settings. This method
    * should be called whenever the runtime properties changed.
    */
   void initAPI() {

      _state.setState(EngineState.INITIALIZING_API);

      synchronized (ConfigManager.RUNTIME_PROPERTIES_LOCK) {

         boolean succeeded = false;

         _configManager.determineLogLocale();

         try {

            // Initialize the API
            _api.init(_runtimeProperties);

            // Initialize the default calling convention for this API
            if (_defaultConvention != null) {
               _defaultConvention.init(_runtimeProperties);
            }

            // Clear the cache for the other calling convention
            _conventionCache.clear();

            succeeded = true;

         // Missing required property
         } catch (MissingRequiredPropertyException exception) {
            Log.log_3411(exception.getPropertyName());

         // Invalid property value
         } catch (InvalidPropertyValueException exception) {
            Log.log_3412(exception.getPropertyName(),
                         exception.getPropertyValue(),
                         exception.getReason());

         // Initialization of API failed for some other reason
         } catch (InitializationException exception) {
            Log.log_3413(exception.getMessage());

         // Unexpected error.
         //
         // XXX: According to the documentation of the Manageable class, this
         //      cannot happen.
         } catch (Throwable exception) {
            Log.log_3414(exception);

         // Always leave the object in a well-known state
         } finally {
            if (succeeded) {
               _state.setState(EngineState.READY);
               Log.log_3415();
            } else {
               _state.setState(EngineState.API_INITIALIZATION_FAILED);
            }
         }
      }
   }

   /**
    * Handles a request to this servlet (wrapper method). If any of the
    * arguments is <code>null</code>, then the behaviour of this method is
    * undefined.
    *
    * @param request
    *    the servlet request, should not be <code>null</code>.
    *
    * @param response
    *    the servlet response, should not be <code>null</code>.
    *
    * @throws IOException
    *    if there is an error error writing to the response output stream.
    */
   void service(final HttpServletRequest  request,
                final HttpServletResponse response)
   throws IOException {

      // Associate the current diagnostic context identifier with this thread
      String contextID = determineContextID(request);
      NDC.push(contextID);

      // Handle the request
      try {
         doService(request, response);

      // Catch and log all exceptions
      } catch (Throwable exception) {
         Log.log_3003(exception);

      // Finally always disassociate the diagnostic context identifier from
      // this thread
      } finally {
         NDC.pop();
         NDC.remove();
      }
   }

   /**
    * Returns an applicable diagnostic context identifier. If the request
    * already specifies a diagnostic context identifier, then that will be
    * used. Otherwise a new one will be generated.
    *
    * @param request
    *    the HTTP servlet request, should not be <code>null</code>.
    *
    * @return
    *    the diagnostic context identifier, never <code>null</code> and never
    *    an empty string.
    */
   private String determineContextID(final HttpServletRequest request) {

      // See if the request already specifies a diagnostic context identifier
      // TODO: Use constant for that request parameter name
      String contextID = request.getParameter("_context");

      // If it does not, then generate a new one
      if (TextUtils.isEmpty(contextID)) {
         // TODO: Construct a ContextIDGenerator with the API name?
         // TODO: Support custom format (SF.net RFE #1078846)
         contextID = ContextIDGenerator.generate(_apiName);
      }

      // TODO: If not empty, check validity (SF.net RFE #1078843)

      return contextID;
   }

   /**
    * Handles a request to this servlet (implementation method). If any of the
    * arguments is <code>null</code>, then the behaviour of this method is
    * undefined.
    *
    * <p>This method is called from the corresponding wrapper method,
    * {@link #service(HttpServletRequest,HttpServletResponse)}.
    *
    * @param request
    *    the servlet request, should not be <code>null</code>.
    *
    * @param response
    *    the servlet response, should not be <code>null</code>.
    *
    * @throws IOException
    *    if there is an error error writing to the response output stream.
    */
   private void doService(final HttpServletRequest request,
                          final HttpServletResponse response)
   throws IOException {

      // Determine current time
      long start = System.currentTimeMillis();

      // Determine the remote IP address and the query string
      String remoteIP    = request.getRemoteAddr();
      String queryString = request.getQueryString();

      // TODO: Return 'Server' header

      // Check the HTTP request method
      String  method = request.getMethod();
      boolean sendOutput;

      // Support HTTP GET
      if ("GET".equals(method)) {
         sendOutput = true;

      // Support HTTP POST
      } else if ("POST".equals(method)) {
         sendOutput = true;

      // Support HTTP HEAD (returns no output)
      } else if ("HEAD".equals(method)) {
         sendOutput = false;
         response.setContentLength(0);

      // Support HTTP OPTIONS
      } else if ("OPTIONS".equals(method)) {
         Log.log_3521(remoteIP, method, queryString);
         response.setContentLength(0);
         response.setHeader("Accept", "GET, HEAD, POST");
         response.setStatus(HttpServletResponse.SC_OK);
         return;

      // Otherwise the HTTP method is unrecognized, so return
      // '405 Method Not Allowed'
      } else {
         Log.log_3520(remoteIP, method, queryString);
         response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
         return;
      }

      // Log that we have received an HTTP request
      Log.log_3521(remoteIP, method, queryString);

      // Determine the calling convention; if an existing calling convention
      // is specified in the request, then use that, otherwise use the default
      // calling convention for this engine
      String ccParam = (String) request.getParameter(
         APIServlet.CALLING_CONVENTION_PARAMETER);
      CallingConvention cc = null;

      // A convention is indeed specified in the request
      if (! (TextUtils.isEmpty(ccParam)
             || ccParam.equals(_defaultConventionName))) {

         // Get the calling convention by name
         cc = (CallingConvention) _conventionCache.get(ccParam);

         // If not found, try to create it
         if (cc == null) {
            try {

               // Have the factory create a CallingConvention instance...
               cc = CallingConventionFactory.create(ccParam,
                                                    _servletConfig,
                                                    _api);

               // ...initialize it...
               if (cc != null) {
                  cc.init(_runtimeProperties);
               }

               // ...and finally store it in our cache
               _conventionCache.put(ccParam, cc);

            // The calling convention could not be created or initialized
            } catch (Exception ex) {

               Log.log_3560(ex, ccParam);
               // TODO: Is this behaviour described?
               response.sendError(HttpServletResponse.SC_BAD_REQUEST);
               return;
            }
         }
      }

      // No convention is specified in the request, so use the default calling
      // convention for this engine
      if (cc == null) {
         cc = _defaultConvention;
         // TODO: Log that no calling convention was specified in the request?
      }

      // Call the API if the state is READY
      FunctionResult result;
      EngineState state = _state.getState();
      if (state == EngineState.READY) {

         String subjectClass  = cc.getClass().getName();
         String subjectMethod = "convertRequest(javax.servlet.http.HttpServletRequest)";
         try {

            // Convert the HTTP request to a XINS request
            FunctionRequest xinsRequest = cc.convertRequest(request);

            // Call the function
            subjectClass  = _api.getClass().getName();
            subjectMethod = "handleCall(long,"
                          + FunctionRequest.class.getName()
                          + ",java.lang.String)";
            result = _api.handleCall(start, xinsRequest, remoteIP);

         } catch (Throwable exception) {

            int error;

            // If the function is not specified, then return '404 Not Found'
            if (exception instanceof FunctionNotSpecifiedException) {
               error = HttpServletResponse.SC_NOT_FOUND;

            // If the request is invalid, then return '400 Bad Request'
            } else if (exception instanceof InvalidRequestException) {
               error = HttpServletResponse.SC_BAD_REQUEST;

            // If access is denied, return '403 Forbidden'
            } else if (exception instanceof AccessDeniedException) {
               error = HttpServletResponse.SC_FORBIDDEN;

            // If no matching function is found, return '404 Not Found'
            } else if (exception instanceof NoSuchFunctionException) {
               error = HttpServletResponse.SC_NOT_FOUND;

            // Otherwise an unexpected exception is thrown, return
            // '500 Internal Server Error'
            } else {
               error = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

               Utils.logProgrammingError(
                  Engine.class.getName(),
                  "doService(javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse)",
                  subjectClass,
                  subjectMethod,
                  null,
                  exception);
            }

            Log.log_3522(exception, error);

            response.sendError(error);
            return;
         }

      // Otherwise return an appropriate 50x HTTP response code
      } else if (state == EngineState.INITIAL
              || state == EngineState.BOOTSTRAPPING_FRAMEWORK
              || state == EngineState.CONSTRUCTING_API
              || state == EngineState.BOOTSTRAPPING_API
              || state == EngineState.INITIALIZING_API) {
         response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
         return;
      } else {
         response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
         return;
      }

      // Send the output only if GET or POST; convert it from a XINS function
      // result to an HTTP response
      if (sendOutput) {
         cc.convertResult(result, response, request);
      }
   }

   /**
    * Determines the default calling convention name from the config object
    * and uses this to create a calling convention. If this does not work out,
    * a default XINS standard calling convention is constructed.
    *
    * @param config
    *    the {@link ServletConfig} object which contains build properties for
    *    this servlet, as specified by the <em>assembler</em>, cannot be
    *    <code>null</code>.
    *
    * @throws ServletException
    *    if the calling convention can not be created.
    */
   void initCallingConvention(final ServletConfig config)
   throws ServletException {

      try {
         // Determine the name of the default calling convention, as specified
         // in the build-time propertie
         String ccName = config.getInitParameter(
            APIServlet.API_CALLING_CONVENTION_PROPERTY);

         // If the name is specified, attempt to construct an instance
         CallingConvention cc;
         if (! TextUtils.isEmpty(ccName)) {
            cc = CallingConventionFactory.create(ccName,
                                                 _servletConfig,
                                                 _api);

            // If the factory method returned null, then the specified name
            // does not identify a known calling convention
            if (cc == null) {
               Log.log_3210(APIServlet.API_CALLING_CONVENTION_PROPERTY,
                            ccName,
                            "No such calling convention.");
               _state.setState(EngineState.API_BOOTSTRAP_FAILED);
               throw new ServletException();
            }

            // On success, store the calling convention name and object
            _defaultConventionName = ccName;
            _defaultConvention     = cc;

            // TODO: Log that we use the specified calling convention

         // No calling convention is specified in the build-time properties,
         // so use the standard calling convention
         } else {
            _defaultConventionName = "_xins-std";
            _defaultConvention     = new StandardCallingConvention();
            _defaultConvention.bootstrap(
               new ServletConfigPropertyReader(_servletConfig));

            // TODO: Log that we use the standard calling convention
         }

      } catch (Throwable t) {
         _state.setState(EngineState.API_BOOTSTRAP_FAILED);

         // Throw a ServletException
         ServletException se;
         if (t instanceof ServletException) {
            se = (ServletException) t;
         } else {
            se = new ServletException(
               "Calling convention construction failed.");
            ExceptionUtils.setCause(se, t);
         }
         throw se;
      }
   }

   /**
    * Destroys this servlet. A best attempt will be made to release all
    * resources.
    *
    * <p>After this method has finished, it will set the state to
    * <em>disposed</em>. In that state no more requests will be handled.
    */
   void destroy() {

      _configManager.destroy();

      Log.log_3600();

      // Set the state temporarily to DISPOSING
      _state.setState(EngineState.DISPOSING);

      // Destroy the API
      if (_api != null) {
         try {
            _api.deinit();
         } catch (Throwable exception) {
            Log.log_3601(exception);
         }
      }

      // Set the state to DISPOSED
      _state.setState(EngineState.DISPOSED);

      Log.log_3602();
   }

   /**
    * Returns the config manager.
    *
    * @return
    *    the config manager.
    */
   ConfigManager getConfigManager() {
      return _configManager;
   }

   /**
    * Returns the runtime properties.
    *
    * @return
    *    the property reader containing the runtime properties.
    */
   PropertyReader getRunTimeProperties() {
      return _runtimeProperties;
   }

   /**
    * Returns the state.
    *
    * @return
    *    the state.
    */
   EngineStateMachine getState() {
      return _state;
   }

   /**
    * Returns the <code>ServletConfig</code> object which contains the
    * build properties for this servlet. The returned {@link ServletConfig}
    * object is the one passed to the {@link APIServlet#init(ServletConfig)}
    * method.
    *
    * @return
    *    the {@link ServletConfig} object that was used to initialize this
    *    servlet, not <code>null</code> if this servlet is indeed already
    *    initialized.
    */
   ServletConfig getServletConfig() {
      return _servletConfig;
   }
}
