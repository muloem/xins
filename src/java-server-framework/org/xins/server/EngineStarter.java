/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.lang.reflect.Field;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.Utils;
import org.xins.common.collections.InvalidPropertyValueException;
import org.xins.common.collections.MissingRequiredPropertyException;
import org.xins.common.servlet.ServletConfigPropertyReader;
import org.xins.common.text.TextUtils;
import org.xins.logdoc.AbstractLog;
import org.xins.logdoc.ExceptionUtils;
import org.xins.logdoc.UnsupportedLocaleError;

/**
 * XINS engine starter.
 *
 * @version $Revision$ $Date$
 * @author Mees Witteman (<a href="mailto:mees.witteman@nl.wanadoo.com">mees.witteman@nl.wanadoo.com</a>)
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
final class EngineStarter extends Object {

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

      ServletException servletException;

      // If the cause is already a ServletException, use it unchanged
      if (t instanceof ServletException) {
         servletException = (ServletException) t;

      // If a cause has been specified, then use that
      } else if (t != null) {
         servletException = new ServletException();
         ExceptionUtils.setCause(servletException, t);

      // Otherwise just create a vanilla ServletException
      } else {
         servletException = new ServletException();
      }

      return servletException;
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructor for the <code>EngineStarter</code> class.
    *
    * @param engine
    *    a XINS server engine, cannot be <code>null</code>.
    *
    * @param config
    *    servlet configuration, cannot be <code>null</code>.
    *
    * @param state
    *    the state machine for the engine, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>engine == null || config == null || state == null</code>.
    */
   EngineStarter(Engine             engine,
                 ServletConfig      config,
                 EngineStateMachine state) {

      // Check preconditions
      MandatoryArgumentChecker.check("engine", engine,
                                     "config", config,
                                     "state",  state);

      // Store data
      _engine = engine;
      _config = config;
      _state  = state;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The associated XINS server engine. Never <code>null</code>.
    */
   private Engine _engine;

   /**
    * The state machine for the engine. Never <code>null</code>.
    */
   private EngineStateMachine _state;

   /**
    * The servlet config. Never <code>null</code>.
    */
   private ServletConfig _config;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Logs server version, warns if server version differs from common version
    * and warns if the server version is not a production release.
    */
   void logBootMessages() {

      // Log: Bootstrapping XINS/Java Server Framework
      String serverVersion = Library.getVersion();
      Log.log_3200(serverVersion);

      // Warn if Server version differs from Common version
      String commonVersion = org.xins.common.Library.getVersion();
      if (! serverVersion.equals(commonVersion)) {
         Log.log_3226(serverVersion, commonVersion);
      }

      // Warn if the current XINS version is not a production version
      if (! Library.isProductionRelease(serverVersion)) {
         Log.log_3227(serverVersion);
      }
   }

   /**
    * Performs some logging with regards to the version of the XINS/Java
    * Server Framework.
    */
   void checkAndLogVersionNumber() {

      // Log XINS version
      String serverVersion = Library.getVersion();
      Log.log_3225(serverVersion);

      // Warn if API build version is more recent than running version
      if (Library.isProductionRelease(serverVersion)) {
         String buildVersion = _config.getInitParameter(
            APIServlet.API_BUILD_VERSION_PROPERTY);
         if (buildVersion == null
               || (Library.isProductionRelease(buildVersion)
                     && Library.isMoreRecent(buildVersion))) {
            Log.log_3229(buildVersion, serverVersion);
         }
      }
   }

   /**
    * Constructs the API.
    *
    * @return The constructed API.
    *
    * @throws ServletException
    *    if the API can not be constructed from the values in the config object
    */
   API constructAPI()
   throws ServletException {

      // Proceed to next stage
      _state.setState(EngineState.CONSTRUCTING_API);

      String apiClassName = determineAPIClassName();
      Class  apiClass     = loadAPIClass(apiClassName);
      API    api          = getAPIFromSingletonField(apiClassName, apiClass);

      checkAPIConstruction(apiClassName, apiClass, api);

      return api;
   }

   /**
    * Checks the construction of the API.
    *
    * @param apiClassName
    *    the name of the API class, cannot be <code>null</code>.
    *
    * @param apiClass
    *    The API class, cannot be <code>null</code>.
    *
    * @param api
    *    The API instance self, cannot be <code>null</code>.
    *
    * @throws ServletException
    *    if the API is <code>null</code> or if the API class is not equal to
    *    <code>apiClass</code>.
    */
   private void checkAPIConstruction(String apiClassName,
                                     Class  apiClass,
                                     API    api)
   throws ServletException {

      // Make sure that the value of the field is not null
      if (api == null) {
         String detail = "Value of static field SINGLETON in class "
            + apiClassName
            + " is null.";
         Log.log_3208(APIServlet.API_CLASS_PROPERTY, apiClassName, detail);
         _state.setState(EngineState.API_CONSTRUCTION_FAILED);
         throw new ServletException();
      }

      // Make sure that the value of the field is an instance of that class
      if (api.getClass() != apiClass) {
         String detail = "Value of static field SINGLETON in class "
            + apiClassName
            + " is not an instance of that class.";
         Log.log_3208(APIServlet.API_CLASS_PROPERTY, apiClassName, detail);
         _state.setState(EngineState.API_CONSTRUCTION_FAILED);
         throw new ServletException();
      }
   }

   /**
    * Gets the API from the singleton field that is available on all API's.
    *
    * @param apiClassName The api class name
    * @param apiClass The api
    *
    * @return
    *    The api.
    *
    * @throws ServletException If the apiClass doesn't have a singleton field or
    *    if the value of the field can noet be cast to the API class.
    */
   private API getAPIFromSingletonField(String apiClassName,
                                        Class apiClass)
   throws ServletException {

      // Get the SINGLETON field and the value of it
      Field singletonField;
      API api;
      try {
         singletonField = apiClass.getDeclaredField("SINGLETON");
         api = (API) singletonField.get(null);
      } catch (Throwable exception) {
         String detail = "Caught unexpected "
            + exception.getClass().getName()
            + " while retrieving the value of the static field"
            + " SINGLETON in class "
            + apiClassName
            + '.';
         final String THIS_METHOD = "<init>(javax.servlet.ServletConfig)";
         Utils.logProgrammingError(Engine.class.getName(),
                                   THIS_METHOD,
                                   apiClassName,
                                   "SINGLETON",
                                   detail,
                                   exception);
         Log.log_3208(APIServlet.API_CLASS_PROPERTY, apiClassName, detail);
         _state.setState(EngineState.API_CONSTRUCTION_FAILED);
         throw servletExceptionFor(exception);
      }
      return api;
   }

   /**
    * Loads the class with the given name and performs checks on the loaded
    * class.
    *
    * @param apiClassName
    *
    * @return the class that is loaded
    *
    * @throws ServletException
    */
   private Class loadAPIClass(String apiClassName)
   throws ServletException {

      // Load the API class
      Class apiClass;
      try {
         apiClass = Class.forName(apiClassName);
      } catch (Throwable exception) {
         String detail = "Caught unexpected "
            + exception.getClass().getName()
            + " while loading class "
            + apiClassName
            + '.';
         Log.log_3207(exception, APIServlet.API_CLASS_PROPERTY, apiClassName);
         _state.setState(EngineState.API_CONSTRUCTION_FAILED);
         throw servletExceptionFor(exception);
      }

      // Check that the loaded API class is derived from the API base class
      if (! API.class.isAssignableFrom(apiClass)) {
         String detail = "Class "
            + apiClassName
            + " is not derived from "
            + API.class.getName()
            + '.';
         Log.log_3208(APIServlet.API_CLASS_PROPERTY, apiClassName, detail);
         _state.setState(EngineState.API_CONSTRUCTION_FAILED);
         throw new ServletException();
      }
      return apiClass;
   }

   /**
    * Determines the API class name from the config file.
    *
    * @return The API class name
    *
    * @throws ServletException if the class name could not be determined from
    *    the init parameters.
    */
   private String determineAPIClassName()
   throws ServletException {
      String apiClassName = _config.getInitParameter(APIServlet.API_CLASS_PROPERTY);
      apiClassName = TextUtils.isEmpty(apiClassName)
            ? null
            : apiClassName.trim();
      if (apiClassName == null) {
         Log.log_3206(APIServlet.API_CLASS_PROPERTY);
         _state.setState(EngineState.API_CONSTRUCTION_FAILED);
         throw new ServletException();
      }
      return apiClassName;
   }

   /**
    * Bootstraps the API. The following steps will be performed:
    * <ul>
    *   <li>Determine API name
    *   <li>Load the Logdoc if available
    *   <li>Bootstrap the API itself
    *   <li>Determine the calling convention
    *   <li>Link the engine to the API
    * </ul>
    *
    * @param api The API to bootstrap.
    *
    * @throws ServletException
    *    if bootstrap fails
    */
   void bootstrapAPI(API api)
   throws ServletException {

      // Proceed to next stage
      _state.setState(EngineState.BOOTSTRAPPING_API);

      // Determine the name of the API
      _engine.initAPIName(determineAPIName());

      // Load the Logdoc if available
      String logdocClassName = determineLogDocName();
      loadLogDoc(logdocClassName, _state);

      // Actually bootstrap the API
      bootstrap(api);

      // Configure the calling convention
      _engine.initCallingConvention(_config);

      // Make the API have a link to this Engine
      api.setEngine(_engine);
   }


   /**
    * Calls the bootstrap on the API and logs exceptions in case of an error.
    *
    * @param api The API to bootstrap.
    *
    * @throws ServletException if the bootstrap of the api fails.
    */
   private void bootstrap(API api)
   throws ServletException {

      // Bootstrap the API self
      Throwable caught;
      try {
         api.bootstrap(new ServletConfigPropertyReader(_config));
         caught = null;

      // Missing required property
      } catch (MissingRequiredPropertyException exception) {
         Log.log_3209(exception.getPropertyName());
         caught = exception;

      // Invalid property value
      } catch (InvalidPropertyValueException exception) {
         Log.log_3210(exception.getPropertyName(),
                      exception.getPropertyValue(),
                      exception.getReason());
         caught = exception;

      // Other bootstrap error
      } catch (Throwable exception) {
         Log.log_3211(exception);
         caught = exception;
      }

      // Throw a ServletException if the bootstrap failed
      if (caught != null) {
         _state.setState(EngineState.API_BOOTSTRAP_FAILED);
         ServletException se = new ServletException("API bootstrap failed.");
         ExceptionUtils.setCause(se, caught);
         throw se;
      }
   }

   /**
    * Attempts to load the logdoc class and performs checks on the class.
    *
    * @param logdocClassName The Log doc class to load.
    *
    * @param state The state of the engine.
    *
    * @throws ServletException If the log doc class can not be loaded.
    *
    */
   private void loadLogDoc(String logdocClassName, EngineStateMachine state)
   throws ServletException {


      try {
         // Attempt to load the Logdoc 'Log' class. This should execute the
         // static initializer, which is what we want.
         Class logdocClass = Class.forName(logdocClassName);

         // Is the loaded class really a Logdoc 'Log' class or just some
         // other class that is coincedentally called 'Log' ?
         // If it is, then the API indeed uses Logdoc logging
         if (AbstractLog.class.isAssignableFrom(logdocClass)) {
            Log.log_3233();

         // The API does not use Logdoc logging
         } else {
            Log.log_3234();
         }

      // There is no 'Log' class in the API package
      } catch (ClassNotFoundException cnfe) {
         Log.log_3234();

      // The locale is not supported
      } catch (UnsupportedLocaleError exception) {
         Log.log_3309(exception.getLocale());
         state.setState(EngineState.API_BOOTSTRAP_FAILED);
         throw servletExceptionFor(exception);

      // Other unexpected exception
      } catch (Throwable exception) {
         Utils.logProgrammingError(
            Engine.class.getName(),
            "<init>(javax.servlet.ServletConfig)",
            "java.lang.Class",
            "forName(java.lang.String)",
            "Unexpected exception while loading Logdoc Log class for API.",
            exception);
      }
   }

   /**
    * Determines the name of the Logdoc Log class for the API. If there is a
    * Logdoc Log class for the API, then it should match the returned
    * fully-qualified class name.
    *
    * @return
    *    the fully-qualified name of the Logdoc Log class, never
    *    <code>null</code>.
    */
   private String determineLogDocName() {

      // Determine the name of the API class
      String apiClassName = _config.getInitParameter(
         APIServlet.API_CLASS_PROPERTY);

      // Determine the class prefix, which is everything before the
      // unqualified class name
      String classPrefix;
      int lastDot = apiClassName.lastIndexOf('.');
      if (lastDot < 0) {
         classPrefix = "";
      } else {
         classPrefix = apiClassName.substring(0, lastDot + 1);
      }

      // The name of the Logdoc Log class is always "Log"
      String logdocClassName = classPrefix + "Log";

      return logdocClassName;
   }

   /**
    * Determines the API name.
    *
    * @return
    *    the API name, or <code>"-"</code> if unknown, never
    *    <code>null</code>.
    */
   private String determineAPIName() {

      // Determine the name of the API
      String apiName = _config.getInitParameter(APIServlet.API_NAME_PROPERTY);
      if (apiName != null) {
         apiName = apiName.trim();
      }

      // If the name is not set, then return a hyphen instead
      if (TextUtils.isEmpty(apiName)) {
         Log.log_3232(APIServlet.API_NAME_PROPERTY);
         apiName = "-";
         /* TODO for XINS 2.0.0: Fail if API name is not set.
          Log.log_3209(API_NAME_PROPERTY);
          _state.setState(EngineState.API_BOOTSTRAP_FAILED);
          throw new ServletException();
          */
      } else {
         apiName = apiName.trim();
      }

      // Log the API name
      Log.log_3235(apiName);

      // TODO: Should we not _either_ log 3232 _or_ 3235 ?
      //       We now log both.

      return apiName;
   }
}
