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

import org.xins.common.Utils;
import org.xins.common.collections.InvalidPropertyValueException;
import org.xins.common.collections.MissingRequiredPropertyException;
import org.xins.common.collections.PropertyReader;
import org.xins.common.manageable.InitializationException;
import org.xins.common.servlet.ServletConfigPropertyReader;
import org.xins.common.text.TextUtils;
import org.xins.logdoc.ExceptionUtils;



/**
 * XINS server engine.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
final class Engine extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>ServletException</code> with the specified cause.
    *
    * @param t
    *    the cause for the {@link ServletException}, can be <code>null</code>.
    *
    * @return
    *    the new {@link ServletException}, that has <code>t</code> registered
    *    as the cause for it, never <code>null</code>.
    *
    * @see ExceptionUtils#setCause(Throwable,Throwable)
    */
   static ServletException servletExceptionFor(Throwable t) {

      ServletException servletException = new ServletException();
      if (t != null) {
         ExceptionUtils.setCause(servletException, t);
      }

      return servletException;
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>Engine</code> object.
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
    *        {@link APIServlet#init(ServletConfig)}.
    *        <br>The servlet configuration is the responsibility of the
    *        <em>assembler</em>.</dd>
    *
    *    <dt><strong>2. System properties</strong></dt>
    *    <dd>The location of the configuration file must be passed to the
    *        Java VM at startup, as a system property.
    *        <br>System properties are the responsibility of the
    *        <em>system administrator</em>.
    *        <br>Example:
    *        <br><code>java
    *        -Dorg.xins.server.config=`pwd`/conf/xins.properties
    *        orion.jar</code></dd>
    *
    *    <dt><strong>3. Configuration file</strong></dt>
    *    <dd>The configuration file should contain runtime configuration
    *        settings, like the settings for the logging subsystem.
    *        <br>Runtime properties are the responsibility of the
    *        <em>system administrator</em>.
    *        <br>Example contents for a configuration file:
    *        <blockquote><code>log4j.rootLogger=DEBUG, console
    *        <br>log4j.appender.console=org.apache.log4j.ConsoleAppender
    *        <br>log4j.appender.console.layout=org.apache.log4j.PatternLayout
    *        <br>log4j.appender.console.layout.ConversionPattern=%d
    *        %-5p [%c] %m%n</code></blockquote>
    * </dl>
    *
    * @param config
    *    the {@link ServletConfig} object which contains build properties for
    *    this servlet, as specified by the <em>assembler</em>, cannot be
    *    <code>null</code>.
    *
    * @throws ServletException
    *    if the engine could not be constructed.
    */
   Engine(ServletConfig config) throws ServletException {
      // TODO: TRACE: Enter constructor

      EngineStarter starter = new EngineStarter(this, config, _state);
      starter.logBootMessages();

      _configManager = new ConfigManager(this, config);
      _configManager.checkServletConfig();
      setServletConfig(config);

      // Proceed to first actual stage
      _state.setState(EngineState.BOOTSTRAPPING_FRAMEWORK);

      _configManager.determineConfigFile();
      _configManager.readRuntimeProperties();

      starter.checkAndLogVersionNumber();

      _api = starter.constructAPI();
      starter.bootstrapAPI(_api);

      _configManager.init();

      // TODO: TRACE: Leave constructor ?
   }

   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The stored servlet configuration object.
    */
   private ServletConfig _servletConfig;

   /**
    * The state machine for this engine. Never <code>null</code>.
    */
   private final EngineStateMachine _state = new EngineStateMachine();

   /**
    * The default calling convention stored in the Servlet.
    */
   private String _defaultCallingConvention;

   /**
    * The cache for the calling convention other than the default one.
    * The key is the name of the calling convention, the value is the
    * calling convention object.
    */
   private Map _callingConventionCache = new HashMap();

   /**
    * The name of the runtime configuration file.
    */
   private String _configFile;

   /**
    * The manager of the config file.
    */
   private ConfigManager _configManager;

   /**
    * The properties read from the runtime configuration file.
    */
   private PropertyReader _runtimeProperties;

   /**
    * The name of the API.
    */
   private String _apiName;

   /**
    * The API that this engine forwards requests to.
    */
   private API _api;

   /**
    * The default calling convention for the API. Can be overridden by the
    * input parameter {@link APIServlet#CALLING_CONVENTION_PARAMETER}.
    *
    * <p>This field can never be <code>null</code> so there will always be a
    * calling convention to fall back to.
    */
   private CallingConvention _callingConvention;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Initializes the API using the current runtime settings.
    */
   void initAPI() {

      _state.setState(EngineState.INITIALIZING_API);

      synchronized (ConfigManager.RUNTIME_PROPERTIES_LOCK) {

         boolean succeeded = false;

         _configManager.determineLogLocale();

         try {

            _api.init(_runtimeProperties);

            // Initialize the default calling convention for this API
            if (_callingConvention != null) {
               _callingConvention.init(_runtimeProperties);
            }

            // Clear the cache for the other calling convention
            _callingConventionCache.clear();

            succeeded = true;
         } catch (MissingRequiredPropertyException exception) {
            Log.log_3411(exception.getPropertyName());
         } catch (InvalidPropertyValueException exception) {
            Log.log_3412(exception.getPropertyName(),
                         exception.getPropertyValue(),
                         exception.getReason());
         } catch (InitializationException exception) {
            Log.log_3413(exception.getMessage());
         } catch (Throwable exception) {
            Log.log_3414(exception);
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
   void service(HttpServletRequest  request,
                HttpServletResponse response)
   throws IOException {

      String contextID = determineContextID(request);

      // Associate the context ID with this thread
      NDC.push(contextID);

      // Handle the request
      try {
         doService(request, response);

      // Catch and log all exceptions
      } catch (Throwable exception) {
         Log.log_3003(exception);

      // And disassociate the context ID from this thread
      } finally {
         NDC.pop();
         NDC.remove();
      }
   }

   /**
    * Will gets the contextID from the request or let a new ID generate if it is
    * not yet available.
    *
    * @param request
    *    The request
    *
    * @return
    *    The context ID
    */
   private String determineContextID(HttpServletRequest request) {
      String contextID = request.getParameter("_context");

      // If there is no diagnostic context ID, then generate one.
      if ((contextID == null) || (contextID.length() < 1)) {
         contextID = ContextIDGenerator.generate(_apiName);
      }
      return contextID;
   }

   /**
    * Handles a request to this servlet (implementation method). If any of the
    * arguments is <code>null</code>, then the behaviour of this method is
    * undefined.
    *
    * <p>This method is called from
    * {@link #service(HttpServletRequest,HttpServletResponse)}. The latter
    * first determines the <em>nested diagnostic context</em> and then
    * forwards the call to this method.
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

      final String THIS_METHOD = "doService("
                               + HttpServletRequest.class.getName()
                               + ','
                               + HttpServletResponse.class.getName()
                               + ')';

      // TODO: TRACE: Enter method

      // Determine current time
      long start = System.currentTimeMillis();

      // Determine the remote IP address and the query string
      String ip          = request.getRemoteAddr();
      String queryString = request.getQueryString();

      // Check the HTTP request method
      String method = request.getMethod();
      boolean sendOutput = "GET".equals(method) || "POST".equals(method);
      if (!sendOutput) {
         if ("OPTIONS".equals(method)) {
            Log.log_3521(ip, method, queryString);
            response.setContentLength(0);
            response.setHeader("Accept", "GET, HEAD, POST");
            response.setStatus(HttpServletResponse.SC_OK);
            return;
         } else if ("HEAD".equals(method)) {
            response.setContentLength(0);

         // If the method is not recognized, return '405 Method Not Allowed'
         } else {
            Log.log_3520(ip, method, queryString);
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
         }
      }
      Log.log_3521(ip, method, queryString);

      // Determine the calling convention. If an existing calling convention
      // is specified in the request, then use that, otherwise use the calling
      // convention stored in the field.
      String ccParam = (String) request.getParameter(
         APIServlet.CALLING_CONVENTION_PARAMETER);
      CallingConvention callingConvention = null;
      if (ccParam != null && !ccParam.equals(_defaultCallingConvention)) {
         callingConvention = (CallingConvention) _callingConventionCache.get(ccParam);
         if (callingConvention == null) {
            try {
               callingConvention = CallingConventionFactory.createCallingConvention(
                  ccParam, _servletConfig, _api);
               if (callingConvention != null) {
                  callingConvention.init(_runtimeProperties);
               }
               _callingConventionCache.put(ccParam, callingConvention);
            } catch (Exception ex) {

               // The calling convention could not be created or initialized
               Log.log_3560(ex, ccParam);
               response.sendError(HttpServletResponse.SC_BAD_REQUEST);
               return;
            }
         }
      }

      if (callingConvention == null) {
         callingConvention = _callingConvention;
         // TODO: Log that no calling convention was specified
      }

      // Call the API if the state is READY
      FunctionResult result;
      EngineState state = _state.getState();
      if (state == EngineState.READY) {

         String subjectClass  = callingConvention.getClass().getName();
         String subjectMethod = "convertRequest("
                              + HttpServletRequest.class.getName()
                              + ')';
         try {

            // Convert the HTTP request to an incoming XINS request
            FunctionRequest xinsRequest =
               callingConvention.convertRequest(request);

            // Call the function
            subjectClass  = _api.getClass().getName();
            subjectMethod = "handleCall(long,"
                          + FunctionRequest.class.getName()
                          + ",java.lang.String)";
            result = _api.handleCall(start, xinsRequest, ip);

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

            // Otherwise an unexpected exception is thrown
            } else {
               error = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
               Utils.logProgrammingError(Engine.class.getName(),
                                         THIS_METHOD,
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

      // Send the output only if GET or POST
      if (sendOutput) {
         callingConvention.convertResult(result, response, request);
      }
   }



   /**
    * Determines the default calling convention name from the config object and
    * uses this to create a calling convention. If this doesn't work out, a
    * default xins standard calling convention is made.
    *
    * @param config
    *    the {@link ServletConfig} object which contains build properties for
    *    this servlet, as specified by the <em>assembler</em>, cannot be
    *    <code>null</code>.
    *
    * @throws ServletException if the calling convention can not be created.
    */
   void initCallingConvention(ServletConfig config)
   throws ServletException {

      try {
         // Determine the default calling convention
         _defaultCallingConvention = config.getInitParameter(
            APIServlet.API_CALLING_CONVENTION_PROPERTY);
         if (! TextUtils.isEmpty(_defaultCallingConvention)) {
            _callingConvention = CallingConventionFactory.createCallingConvention(
               _defaultCallingConvention, _servletConfig, _api);
            if (_callingConvention == null) {
               Log.log_3210(APIServlet.API_CALLING_CONVENTION_PROPERTY,
                            _defaultCallingConvention,
                            "No such calling convention.");
               _state.setState(EngineState.API_BOOTSTRAP_FAILED);
               throw new ServletException();
            }
            // TODO: Log that we use the specified calling convention
         } else {
            // TODO: Use shared StandardCallingConvention instance
            _defaultCallingConvention = "_xins-std";
            _callingConvention = new StandardCallingConvention();
            _callingConvention.bootstrap(new ServletConfigPropertyReader(_servletConfig));
            // TODO: Log that we use the default calling convention
         }
      } catch (Throwable t) {
         _state.setState(EngineState.API_BOOTSTRAP_FAILED);
         ServletException se = new ServletException("API bootstrap failed.");
         ExceptionUtils.setCause(se, t);
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
    * object is the one passed to the {@link APIServlet#init(ServletConfig)} method.
    *
    * @return
    *    the {@link ServletConfig} object that was used to initialize this
    *    servlet, not <code>null</code> if this servlet is indeed already
    *    initialized.
    */
   ServletConfig getServletConfig() {
      return _servletConfig;
   }

   /**
    * Store the ServletConfig object, per the Servlet API Spec, see:
    * http://java.sun.com/products/servlet/2.3/javadoc/javax/servlet/Servlet.html#getServletConfig()
    * TODO: Store this only if the initialization completely succeeded
    * and no exception was thrown. Maybe this is solved aleady..?
    *
    * @param config the config object
    */
   private void setServletConfig(ServletConfig config) {
      _servletConfig = config;
   }

   /**
    * @param properties The runtime properties to set.
    */
   void setRuntimeProperties(PropertyReader properties) {
      _runtimeProperties = properties;
   }

   /**
    * @param convention The calling convention to set.
    */
   void setCallingConvention(CallingConvention convention) {
      _callingConvention = convention;
   }

   /**
    * @param callingConvention The default calling convention to set.
    */
   void setDefaultCallingConvention(String callingConvention) {
      _defaultCallingConvention = callingConvention;
   }

   /**
    * @param name The api name to set.
    */
   void setApiName(String name) {
      _apiName = name;
   }

}