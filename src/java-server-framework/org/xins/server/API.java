/*
 * $Id$
 */
package org.xins.server;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import javax.servlet.ServletRequest;
import org.apache.log4j.Logger;
import org.xins.types.Type;
import org.xins.types.TypeValueException;
import org.xins.types.standard.Text;
import org.xins.util.MandatoryArgumentChecker;
import org.xins.util.collections.BasicPropertyReader;
import org.xins.util.collections.PropertyReader;
import org.xins.util.collections.PropertiesPropertyReader;
import org.xins.util.collections.expiry.ExpiryFolder;
import org.xins.util.collections.expiry.ExpiryStrategy;
import org.xins.util.io.FastStringWriter;
import org.xins.util.text.DateConverter;
import org.znerd.xmlenc.XMLOutputter;

/**
 * Base class for API implementation classes.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public abstract class API
extends Object
implements DefaultResultCodes {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The logging category used by this class. This class field is never
    * <code>null</code>.
    */
   private static final Logger LOG = Logger.getLogger(API.class.getName());

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
    * Constant indicating the <em>initialized</em> state. See
    * {@link #_state}.
    */
   private static final int INITIALIZED = 2;

   /**
    * String returned by the function <code>_GetStatistics</code> when certain
    * information is not available.
    */
   private static final String NOT_AVAILABLE = "N/A";


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>API</code> object.
    *
    * @param name
    *    the name of the API, cannot be <code>null</code> nor can it be an
    *    empty string.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null || name.length() &lt; 1</code>.
    */
   protected API(String name)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("name", name);
      if (name.length() < 1) {
         throw new IllegalArgumentException("name.length() (" + name.length() + " < 1");
      }

      // Initialize fields
      _name              = name;
      _stateLock         = new Object();
      _startupTimestamp  = System.currentTimeMillis();
      _instances         = new ArrayList();
      _functionsByName   = new HashMap();
      _functionList      = new ArrayList();
      _resultCodesByName = new HashMap();
      _resultCodeList    = new ArrayList();
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The name of this API. Cannot be <code>null</code> and cannot be an empty
    * string.
    */
   private final String _name;

   /**
    * The current state. Either {@link #UNINITIALIZED}, {@link #INITIALIZING}
    * or {@link #INITIALIZED}.
    */
   private int _state;

   /**
    * Lock object for the current state.
    */
   private final Object _stateLock;

   /**
    * Flag that indicates if this API is session-based.
    */
   private boolean _sessionBased;

   /**
    * Flag that indicates if response validations should be enabled for the
    * functions in this API.
    */
   private boolean _responseValidationEnabled;

   /**
    * List of registered instances. See {@link #addInstance(Object)}.
    *
    * <p />This field is initialized to a non-<code>null</code> value by the
    * constructor.
    */
   private final List _instances;

   /**
    * Expiry strategy for <code>_sessionsByID</code>.
    *
    * <p />This field is initialized to a non-<code>null</code> value by the
    * initialization method {@link #init(Properties)}.
    */
   private ExpiryStrategy _sessionExpiryStrategy;

   /**
    * Collection that maps session identifiers to <code>Session</code>
    * instances. Contains all sessions associated with this API.
    *
    * <p />This field is initialized to a non-<code>null</code> value by the
    * initialization method {@link #init(Properties)}.
    */
   private ExpiryFolder _sessionsByID;

   /**
    * Map that maps function names to <code>Function</code> instances.
    * Contains all functions associated with this API.
    *
    * <p />This field is initialized to a non-<code>null</code> value by the
    * constructor.
    */
   private final Map _functionsByName;

   /**
    * List of all functions. This field cannot be <code>null</code>.
    */
   private final List _functionList;

   /**
    * Map that maps result code names to <code>ResultCode</code> instances.
    * Contains all result codes associated with this API.
    *
    * <p />This field is initialized to a non-<code>null</code> value by the
    * constructor.
    */
   private final Map _resultCodesByName;

   /**
    * List of all result codes. This field cannot be <code>null</code>.
    */
   private final List _resultCodeList;

   /**
    * The initialization settings. This field is initialized by
    * {@link #init(Properties)}. It can be <code>null</code> before that.
    */
   private Properties _initSettings;

   /**
    * A reader for the initialization settings. This field is initialized by
    * {@link #init(Properties)}. It can be <code>null</code> before that.
    */
   private PropertyReader _initSettingsReader;

   /**
    * The name of the default function. Is <code>null</code> if there is no
    * default function.
    */
   private String _defaultFunction;

   /**
    * The type that applies for session identifiers. Will be set in
    * {@link #init(Properties)}.
    */
   private SessionID _sessionIDType;

   /**
    * The session ID generator. Will be set in {@link #init(Properties)}.
    */
   private SessionID.Generator _sessionIDGenerator;

   /**
    * Flag that indicates if the shutdown sequence has been initiated.
    */
   private boolean _shutDown;

   /**
    * Timestamp indicating when this API instance was created.
    */
   private final long _startupTimestamp;

   /**
    * Deployment identifier.
    */
   private String _deployment;

   /**
    * Host name for the machine that was used for this build.
    */
   private String _buildHost;

   /**
    * Time stamp that indicates when this build was done.
    */
   private String _buildTime;

   /**
    * The time zone used when generating dates for output.
    */
   private TimeZone _timeZone;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Gets the specified property and converts it to a <code>boolean</code>.
    *
    * @param properties
    *    the set of properties to read from, cannot be <code>null</code>.
    *
    * @param propertyName
    *    the name of the property to read, cannot be <code>null</code>.
    *
    * @return
    *    the value of the property.
    *
    * @throws IllegalArgumentException
    *    if <code>properties == null || propertyName == null</code>.
    */
   private final boolean getBooleanProperty(Properties properties, String propertyName)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("properties", properties, "propertyName", propertyName);

      String value = properties.getProperty(propertyName);
      return "true".equals(value);
   }

   /**
    * Gets the specified property and converts it to an <code>int</code>.
    *
    * @param properties
    *    the set of properties to read from, cannot be <code>null</code>.
    *
    * @param propertyName
    *    the name of the property to read, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>properties == null || propertyName == null</code>.
    *
    * @throws NumberFormatException
    *    if the conversion to an <code>int</code> failed.
    */
   private final int getIntProperty(Properties properties, String propertyName)
   throws IllegalArgumentException, NumberFormatException {

      // Check preconditions
      MandatoryArgumentChecker.check("properties", properties, "propertyName", propertyName);

      String value = properties.getProperty(propertyName);
      return Integer.parseInt(value);
   }

   /**
    * Gets the name of this API.
    *
    * @return
    *    the name of this API, never <code>null</code> and never an empty
    *    string.
    */
   public final String getName() {
      return _name;
   }

   /**
    * Gets the timestamp that indicates when this <code>API</code> instance
    * was created.
    *
    * @return
    *    the time this instance was constructed, as a number of milliseconds
    *    since midnight January 1, 1970.
    */
   public final long getStartupTimestamp() {
      return _startupTimestamp;
   }

   /**
    * Returns the applicable time zone.
    *
    * @return
    *    the time zone, not <code>null</code>.
    *
    * @since XINS 0.95
    */
   public final TimeZone getTimeZone() {
      return _timeZone;
   }

   /**
    * Returns the current number of sessions.
    *
    * @return
    *    the current number of sessions, always &gt;= 0.
    *
    * @throws IllegalStateException
    *    if this API is not session-based.
    *
    * @since XINS 0.95
    */
   public final int getCurrentSessions()
   throws IllegalStateException {

      // Check preconditions
      if (! _sessionBased) {
         throw new IllegalStateException("This API is not session-based.");
      }

      return _sessionsByID.size();
   }

   /**
    * Checks if response validation is enabled.
    *
    * @return
    *    <code>true</code> if response validation is enabled,
    *    <code>false</code> otherwise.
    *
    * @since XINS 0.98
    */
   public final boolean isResponseValidationEnabled() {
      return _responseValidationEnabled;
   }

   /**
    * Initializes this API. The properties are stored internally and then
    * {@link #initImpl(Properties)} is called.
    *
    * @param properties
    *    the properties, can be <code>null</code>.
    *
    * @throws IllegalStateException
    *    if this API is already initialized.
    *
    * @throws Throwable
    *    if the initialization fails (in {@link #initImpl(Properties)}).
    */
   public final void init(Properties properties)
   throws IllegalStateException, Throwable {

      // Check and set state
      synchronized (_stateLock) {
         if (_state != UNINITIALIZED) {
            throw new IllegalStateException("This API is not uninitialized anymore.");
         }
         _state = INITIALIZING;
      }

      // Set the time zone
      _timeZone = TimeZone.getDefault();
      String tzLongName  = _timeZone.getDisplayName(false, TimeZone.LONG);
      String tzShortName = _timeZone.getDisplayName(false, TimeZone.SHORT);
      if (tzLongName.equals(tzShortName)) {
         LOG.info("Local time zone is " + tzLongName + '.');
      } else {
         LOG.info("Local time zone is " + tzShortName + " (" + tzLongName + ").");
      }

      // Store the settings
      if (properties == null) {
         _initSettings = new Properties();
      } else {
         _initSettings = (Properties) properties.clone();
      }
      _initSettingsReader = new PropertiesPropertyReader(_initSettings);

      // Check if a default function is set
      _defaultFunction = properties.getProperty("org.xins.api.defaultFunction");
      if (_defaultFunction != null) {
         LOG.debug("Default function set to \"" + _defaultFunction + "\".");
      }

      // Check if response validation is enabled
      _responseValidationEnabled = getBooleanProperty(properties, "org.xins.api.responseValidation");
      LOG.info("Response validation is " + (_responseValidationEnabled ? "enabled." : "disabled."));

      // Check if this API is session-based
      _sessionBased = getBooleanProperty(properties, "org.xins.api.sessionBased");

      // XXX: Allow configuration of session ID type ?

      // Initialize session-based API
      if (_sessionBased) {
         LOG.debug("Performing session-related initialization.");

         // Initialize session ID type
         _sessionIDType      = new BasicSessionID(this);
         _sessionIDGenerator = _sessionIDType.getGenerator();

         // Determine session time-out duration and precision
         final long MINUTE_IN_MS = 60000L;
         long timeOut   = MINUTE_IN_MS * (long) getIntProperty(properties, "org.xins.api.sessionTimeOut");
         long precision = MINUTE_IN_MS * (long) getIntProperty(properties, "org.xins.api.sessionTimeOutPrecision");

         // Create expiry strategy and folder
         _sessionExpiryStrategy = new ExpiryStrategy(timeOut, precision);
         _sessionsByID          = new ExpiryFolder("sessionsByID",         // name of folder (for logging)
                                                   _sessionExpiryStrategy, // expiry strategy
                                                   false,                  // strict thread sync checking? (TODO)
                                                   5000L);                 // max queue wait time in ms    (TODO)
      }

      // Get build-time properties
      _deployment = properties.getProperty("org.xins.api.deployment");
      _buildHost  = properties.getProperty("org.xins.api.build.host");
      _buildTime  = properties.getProperty("org.xins.api.build.time");
      if (_buildHost == null) {
         LOG.warn("Build host name is not set.");
         _buildHost = "<unknown>";
      } else if (_buildTime == null) {
         LOG.warn("Build time stamp is not set.");
         _buildTime = "<unknown>";
      }

      // Log build-time properties
      if (_deployment == null) {
         LOG.info("Built on " + _buildHost + " (" + _buildTime + ").");
      } else {
         LOG.info("Built on " + _buildHost + " (" + _buildTime + "), for deployment \"" + _deployment + "\".");
      }

      // Let the subclass perform initialization
      boolean succeeded = false;
      try {
         initImpl(properties);
         succeeded = true;

      // Set the state
      } finally {
         synchronized (_stateLock) {
            if (!succeeded) {
               _state = UNINITIALIZED;
            } else {
               _state = INITIALIZED;
            }
         }
      }

      // XXX: Initialize all instances here somewhere ?
   }

   /**
    * Actually initializes this API.
    *
    * <p />The implementation of this method in class {@link API} is empty.
    *
    * @param properties
    *    the properties, can be <code>null</code>.
    *
    * @throws Throwable
    *    if the initialization fails.
    */
   protected void initImpl(Properties properties)
   throws Throwable {
      // empty
   }

   /**
    * Adds the specified instance as an object to initialize at startup and
    * deinitialize at shutdown. The object will immediately be initialized. If
    * the initialization fails, then an {@link InitializationException} will
    * be thrown.
    *
    * <p>The initialization will be performed by calling
    * {@link Singleton#init(PropertyReader)}.
    *
    * <p>At shutdown time {@link Singleton#destroy()} will be called.
    *
    * @param instance
    *    the instance to initialize now and deinitialize at shutdown time, not
    *    <code>null</code>.
    *
    * @throws IllegalStateException
    *    if this API is currently not in the initializing state.
    *
    * @throws IllegalArgumentException
    *    if <code>instance == null</code>.
    *
    * @throws InitializationException
    *    if the initialization of the instance failed.
    *
    * @since XINS 0.55
    */
   protected final void addInstance(Singleton instance)
   throws IllegalStateException,
          IllegalArgumentException,
          InitializationException {

      // Check state
      if (_state != INITIALIZING) {
         throw new IllegalStateException("Currently not initializing.");
      }

      // Check preconditions
      MandatoryArgumentChecker.check("instance", instance);

      _instances.add(instance);

      boolean succeeded = false;
      String className = instance.getClass().getName();
      LOG.debug("Initializing instance of class \"" + className + "\".");
      try {
         instance.init(_initSettingsReader);
         succeeded = true;
      } finally {
         if (succeeded) {
            LOG.info("Initialized instance of class \"" + className + "\".");
         } else {
            String message = "Failed to initialize instance of \"" + className + "\".";
            LOG.error(message);
         }
      }
   }

   /**
    * Adds the specified instance as an object to initialize at startup and
    * deinitialize at shutdown. The object will immediately be initialized. If
    * the initialization fails, then an {@link InitializationException} will
    * be thrown.
    *
    * <p>The initialization will be performed by calling a method
    * <code>init(</code>{@link Properties}<code>)</code> in the specified
    * instance with the following characteristics:
    *
    * <ul>
    *    <li>Must be <em>public</em>
    *    <li>Cannot be <em>static</em>
    *    <li>Cannot be <em>abstract</em>
    * </ul>
    *
    * <p>At shutdown time, a method <code>destroy()</code> will be
    * called using the same approach. The conditions for the <code>init</code>
    * method also apply to this method.
    *
    * @param instance
    *    the instance to initialize now and deinitialize at shutdown time, not
    *    <code>null</code>.
    *
    * @throws IllegalStateException
    *    if this API is currently not in the initializing state.
    *
    * @throws IllegalArgumentException
    *    if <code>instance == null</code>.
    *
    * @throws InitializationException
    *    if the initialization of the instance failed.
    *
    * @deprecated
    *    Deprecated since XINS 0.55. Use {@link #addInstance(Singleton)}
    *    instead.
    */
   protected final void addInstance(Object instance)
   throws IllegalStateException,
          IllegalArgumentException,
          InitializationException {

      // Forward call to non-deprecated method, if possible
      if (instance instanceof Singleton) {
         addInstance((Singleton) instance);
      }


      // Check state
      if (_state != INITIALIZING) {
         throw new IllegalStateException("Currently not initializing.");
      }

      // Check preconditions
      MandatoryArgumentChecker.check("instance", instance);

      if ((instance instanceof Singleton) == false) {
         LOG.warn("Registering API singleton of class " + instance.getClass().getName() + ", which does not implement the interface " + Singleton.class.getName() + '.');
      }

      _instances.add(instance);

      boolean succeeded = callMethod(instance, "init", new Class[] { Properties.class }, new Object[] { _initSettings.clone() });
     
      String className = instance.getClass().getName();
      if (succeeded) {
         LOG.info("Initialized instance of " + className + '.');
      } else {
         String message = "Failed to initialize instance of " + className + '.';
         LOG.error(message);
         throw new InitializationException(message);
      }
   }

   /**
    * Calls the specified method with the specified arguments.
    *
    * @param instance
    *    the instance on which to call the method, should not be
    *    <code>null</code>.
    *
    * @param methodName
    *    the name of the method to call.
    *
    * @param parameterTypes
    *    the parameter types for the method.
    *
    * @param arguments
    *    the arguments to pass to the method.
    *
    * @return
    *    the value returned by the call, can be <code>null</code>.
    */
   private final boolean callMethod(Object   instance,
                                    String   methodName,
                                    Class[]  parameterTypes,
                                    Object[] arguments) {

      Class clazz      = instance.getClass();
      String className = clazz.getName();

      // Determine the signature
      StringBuffer sb = new StringBuffer(128);
      sb.append(className);
      sb.append('.');
      sb.append(methodName);
      sb.append('(');
      for (int i = 0; i < parameterTypes.length; i++) {
         if (i > 0) {
            sb.append(", ");
         }
         sb.append(parameterTypes[i].getClass().getName());
      }
      sb.append(')');
      String signature = sb.toString();

      // Get the method
      Method method;
      try {
         method = clazz.getDeclaredMethod(methodName, parameterTypes);
      } catch (NoSuchMethodException exception) {
         LOG.warn("Unable to find method " + signature + '.');
         return false;
      } catch (SecurityException exception) {
         LOG.warn("Access denied while attempting to lookup method " + signature + '.');
         return false;
      }

      // The method must be public, non-abstract and non-static
      int modifiers = method.getModifiers();
      if (Modifier.isAbstract(modifiers)) {
         LOG.warn("Unable to call abstract method " + signature + '.');
         return false;
      } else if (Modifier.isStatic(modifiers)) {
         LOG.warn("Unable to call abstract method " + signature + '.');
         return false;
      } else if (Modifier.isPublic(modifiers) == false) {
         LOG.warn("Unable to call non-public method " + signature + '.');
         return false;
      }

      // Attempt the call
      try {
         method.invoke(instance, arguments);
      } catch (Throwable exception) {
         LOG.error("Unable to call " + signature + " due to unexpected exception.", exception);
         return false;
      }

      return true;
   }

   /**
    * Performs shutdown of this XINS API.
    */
   final void destroy() {
      _shutDown = true;

      // Stop expiry strategy
      _sessionExpiryStrategy.stop();

      // Destroy all sessions
      LOG.info("Closing " + _sessionsByID.size() + " open sessions.");
      _sessionsByID = null;

      // Deinitialize instances
      for (int i = 0; i < _instances.size(); i++) {
         Object instance = _instances.get(i);

         boolean succeeded = callMethod(instance, "destroy", new Class[] {}, null);
     
         String className = instance.getClass().getName();
         if (succeeded) {
            LOG.info("Deinitialized instance of " + className + '.');
         } else {
            LOG.error("Failed to deinitialize instance of " + className + '.');
         }
      }
   }

   /**
    * Returns the name of the default function, if any.
    *
    * @return
    *    the name of the default function, or <code>null</code> if there is
    *    none.
    */
   public String getDefaultFunctionName() {
      // TODO: Check state
      return _defaultFunction;
   }

   /**
    * Returns if this API is session-based.
    *
    * @return
    *    <code>true</code> if this API is session-based, or <code>false</code>
    *    if it is not.
    *
    * @throws IllegalStateException
    *    if this API is not in the <em>initialized</em> state.
    */
   public boolean isSessionBased()
   throws IllegalStateException {

      if (_state != INITIALIZED) {
         throw new IllegalStateException("This API is not in the 'initialized' state.");
      }

      return _sessionBased;
   }

   /**
    * Gets the session ID type.
    *
    * @return
    *    the type for session IDs in this API, unless otherwise defined this
    *    is {@link Text}.
    *
    * @throws IllegalStateException
    *    if this API is not in the <em>initialized</em> state or if this API is not session-based.
    */
   public final SessionID getSessionIDType()
   throws IllegalStateException {

      // Check preconditions
      if (_state != INITIALIZED) {
         throw new IllegalStateException("This API is not in the 'initialized' state.");
      } else if (! _sessionBased) {
         throw new IllegalStateException("This API is not session-based.");
      }

      return _sessionIDType;
   }

   /**
    * Creates a new session for this API.
    *
    * @return
    *    the newly constructed session, never <code>null</code>.
    *
    * @throws IllegalStateException
    *    if this API is not in the <em>initialized</em> state or if this API is not session-based.
    */
   final Session createSession() throws IllegalStateException {

      // Check preconditions
      if (_state != INITIALIZED) {
         throw new IllegalStateException("This API is not in the 'initialized' state.");
      } else if (! _sessionBased) {
         throw new IllegalStateException("This API is not session-based.");
      }

      // Generate a session ID
      Object sessionID = _sessionIDGenerator.generateSessionID();

      // Construct a Session object...
      Session session = new Session(this, sessionID);

      // ...store it...
      _sessionsByID.put(sessionID, session);

      // ...and then return it
      return session;
   }

   /**
    * Gets the session with the specified identifier.
    *
    * @param id
    *    the identifier for the session, can be <code>null</code>.
    *
    * @return
    *    the session with the specified identifier, or <code>null</code> if
    *    there is no match; if <code>id == null</code>, then <code>null</code>
    *    is returned.
    *
    * @throws IllegalStateException
    *    if this API is not in the <em>initialized</em> state or if this API is not session-based.
    */
   final Session getSession(Object id) throws IllegalStateException {

      // Check preconditions
      if (_state != INITIALIZED) {
         throw new IllegalStateException("This API is not in the 'initialized' state.");
      } else if (! _sessionBased) {
         throw new IllegalStateException("This API is not session-based.");
      }

      return (Session) _sessionsByID.get(id);
   }

   /**
    * Gets the session with the specified identifier as a string.
    *
    * @param idString
    *    the string representation of the identifier for the session, can be <code>null</code>.
    *
    * @return
    *    the session with the specified identifier, or <code>null</code> if
    *    there is no match; if <code>idString == null</code>, then
    *    <code>null</code> is returned.
    *
    * @throws IllegalStateException
    *    if this API is not in the <em>initialized</em> state or if this API is not session-based.
    *
    * @throws TypeValueException
    *    if the specified string is not a valid representation for a value for
    *    the specified type.
    */
   final Session getSessionByString(String idString)
   throws IllegalStateException, TypeValueException {

      // Check preconditions
      if (_state != INITIALIZED) {
         throw new IllegalStateException("This API is not in the 'initialized' state.");
      } else if (! _sessionBased) {
         throw new IllegalStateException("This API is not session-based.");
      }

      return getSession(_sessionIDType.fromString(idString));
   }

   /**
    * Callback method invoked when a function is constructed.
    *
    * @param function
    *    the function that is added, not <code>null</code>.
    *
    * @throws NullPointerException
    *    if <code>function == null</code>.
    */
   final void functionAdded(Function function)
   throws NullPointerException {

      // TODO: Check the state here?

      _functionsByName.put(function.getName(), function);
      _functionList.add(function);

      // TODO: After all functions are added, check that the default function
      //       is set.
   }

   /**
    * Callback method invoked when a result code is constructed.
    *
    * @param resultCode
    *    the result code that is added, not <code>null</code>.
    *
    * @throws NullPointerException
    *    if <code>resultCode == null</code>.
    */
   final void resultCodeAdded(ResultCode resultCode)
   throws NullPointerException {
      _resultCodesByName.put(resultCode.getName(), resultCode);
      _resultCodeList.add(resultCode);
   }

   /**
    * Returns the function with the specified name.
    *
    * @param name
    *    the name of the function, will not be checked if it is
    *    <code>null</code>.
    *
    * @return
    *    the function with the specified name, or <code>null</code> if there
    *    is no match.
    */
   final Function getFunction(String name) {
      return (Function) _functionsByName.get(name);
   }

   /**
    * Forwards a call to a function. The call will actually be handled by
    * {@link Function#handleCall(ServletRequest)}.
    *
    * @param start
    *    the start time of the request, in milliseconds since midnight January
    *    1, 1970.
    *
    * @param request
    *    the original servlet request, not <code>null</code>.
    *
    * @return
    *    the result of the call, never <code>null</code>.
    *
    * @throws NullPointerException
    *    if <code>request == null</code>.
    */
   final CallResult handleCall(long start, ServletRequest request)
   throws NullPointerException {

      // Determine the function name
      String functionName = request.getParameter("_function");
      if (functionName == null || functionName.length() == 0) {
         functionName = request.getParameter("function");
      }
      if (functionName == null || functionName.length() == 0) {
         functionName = getDefaultFunctionName();
      }

      // The function name is required
      if (functionName == null || functionName.length() == 0) {
         // TODO: return new BasicCallResult(MISSING_FUNCTION_NAME);
         return new BasicCallResult(false, "MissingFunctionName", null, null);
      }

      // Detect special functions
      if (functionName.charAt(0) == '_') {
         if ("_NoOp".equals(functionName)) {
            // empty
         } else if ("_PerformGC".equals(functionName)) {
            // TODO: return doPerformGC();
            System.gc();
            // TODO: Cache this CallResult
            return new BasicCallResult(true, null, null, null);
         } else if ("_GetFunctionList".equals(functionName)) {
            return doGetFunctionList();
         } else if ("_GetStatistics".equals(functionName)) {
            return doGetStatistics();
         } else if ("_GetVersion".equals(functionName)) {
            return doGetVersion();
         } else if ("_GetSettings".equals(functionName)) {
            return doGetSettings();
         } else {
            // TODO: Cache this CallResult
            // TODO: new BasicCallResult(NO_SUCH_FUNCTION);
            return new BasicCallResult(false, "NoSuchFunction", null, null);
         }
      }

      // Short-circuit if we are shutting down
      if (_shutDown) {
         return new BasicCallResult(false, "InternalError", null, null);
      }

      // Get the function object
      Function function  = getFunction(functionName);
      if (function == null)  {
         // TODO: Cache this CallResult
         // TODO: new BasicCallResult(NO_SUCH_FUNCTION);
         return new BasicCallResult(false, "NoSuchFunction", null, null);
      }

      // Forward the call to the function
      return function.handleCall(start, request);
   }

   /**
    * Returns a list of all functions in this API. Per function the name and
    * the version are returned.
    *
    * @return
    *    the call result, never <code>null</code>.
    */
   private final CallResult doGetFunctionList() {

      // Initialize a builder
      CallResultBuilder builder = new CallResultBuilder();

      int count = _functionList.size();
      for (int i = 0; i < count; i++) {
         Function function = (Function) _functionList.get(i);
         builder.startTag("function");
         builder.attribute("name",    function.getName());
         builder.attribute("version", function.getVersion());
         builder.endTag();
      }

      return builder;
   }

   /**
    * Returns the call statistics for all functions in this API.
    *
    * @return
    *    the call result, never <code>null</code>.
    */
   private final CallResult doGetStatistics() {

      // Initialize a builder
      CallResultBuilder builder = new CallResultBuilder();

      builder.param("startup", DateConverter.toDateString(_timeZone, _startupTimestamp));
      builder.param("now",     DateConverter.toDateString(_timeZone, System.currentTimeMillis()));

      // Currently available processors
      Runtime rt = Runtime.getRuntime();
      try {
         builder.param("availableProcessors", String.valueOf(rt.availableProcessors()));
      } catch (NoSuchMethodError error) {
         // ignore: Runtime.availableProcessors() is not available in Java 1.3
      }

      // Heap memory statistics
      builder.startTag("heap");
      long free  = rt.freeMemory();
      long total = rt.totalMemory();
      builder.attribute("used",  String.valueOf(total - free));
      builder.attribute("free",  String.valueOf(free));
      builder.attribute("total", String.valueOf(total));
      try {
         builder.attribute("max", String.valueOf(rt.maxMemory()));
      } catch (NoSuchMethodError error) {
         // ignore: Runtime.maxMemory() is not available in Java 1.3
      }
      builder.endTag(); // heap

      // Function-specific statistics
      int count = _functionList.size();
      for (int i = 0; i < count; i++) {
         Function function = (Function) _functionList.get(i);
         Function.Statistics stats = function.getStatistics();

         long successfulCalls      = stats.getSuccessfulCalls();
         long unsuccessfulCalls    = stats.getUnsuccessfulCalls();
         long successfulDuration   = stats.getSuccessfulDuration();
         long unsuccessfulDuration = stats.getUnsuccessfulDuration();

         String successfulAverage;
         String successfulMin;
         String successfulMinStart;
         String successfulMax;
         String successfulMaxStart;
         String lastSuccessfulStart;
         String lastSuccessfulDuration;
         if (successfulCalls == 0) {
            successfulAverage      = NOT_AVAILABLE;
            successfulMin          = NOT_AVAILABLE;
            successfulMinStart     = NOT_AVAILABLE;
            successfulMax          = NOT_AVAILABLE;
            successfulMaxStart     = NOT_AVAILABLE;
            lastSuccessfulStart    = NOT_AVAILABLE;
            lastSuccessfulDuration = NOT_AVAILABLE;
         } else if (successfulDuration == 0) {
            successfulAverage      = "0";
            successfulMin          = String.valueOf(stats.getSuccessfulMin());
            successfulMinStart     = DateConverter.toDateString(_timeZone, stats.getSuccessfulMinStart());
            successfulMax          = String.valueOf(stats.getSuccessfulMax());
            successfulMaxStart     = DateConverter.toDateString(_timeZone, stats.getSuccessfulMaxStart());
            lastSuccessfulStart    = DateConverter.toDateString(_timeZone, stats.getLastSuccessfulStart());
            lastSuccessfulDuration = String.valueOf(stats.getLastSuccessfulDuration());
         } else {
            successfulAverage      = String.valueOf(successfulDuration / successfulCalls);
            successfulMin          = String.valueOf(stats.getSuccessfulMin());
            successfulMinStart     = DateConverter.toDateString(_timeZone, stats.getSuccessfulMinStart());
            successfulMax          = String.valueOf(stats.getSuccessfulMax());
            successfulMaxStart     = DateConverter.toDateString(_timeZone, stats.getSuccessfulMaxStart());
            lastSuccessfulStart    = DateConverter.toDateString(_timeZone, stats.getLastSuccessfulStart());
            lastSuccessfulDuration = String.valueOf(stats.getLastSuccessfulDuration());
         }

         String unsuccessfulAverage;
         String unsuccessfulMin;
         String unsuccessfulMinStart;
         String unsuccessfulMax;
         String unsuccessfulMaxStart;
         String lastUnsuccessfulStart;
         String lastUnsuccessfulDuration;
         if (unsuccessfulCalls == 0) {
            unsuccessfulAverage      = NOT_AVAILABLE;
            unsuccessfulMin          = NOT_AVAILABLE;
            unsuccessfulMinStart     = NOT_AVAILABLE;
            unsuccessfulMax          = NOT_AVAILABLE;
            unsuccessfulMaxStart     = NOT_AVAILABLE;
            lastUnsuccessfulStart    = NOT_AVAILABLE;
            lastUnsuccessfulDuration = NOT_AVAILABLE;
         } else if (unsuccessfulDuration == 0) {
            unsuccessfulAverage      = "0";
            unsuccessfulMin          = String.valueOf(stats.getUnsuccessfulMin());
            unsuccessfulMinStart     = DateConverter.toDateString(_timeZone, stats.getUnsuccessfulMinStart());
            unsuccessfulMax          = String.valueOf(stats.getUnsuccessfulMax());
            unsuccessfulMaxStart     = DateConverter.toDateString(_timeZone, stats.getUnsuccessfulMaxStart());
            lastUnsuccessfulStart    = DateConverter.toDateString(_timeZone, stats.getLastUnsuccessfulStart());
            lastUnsuccessfulDuration = String.valueOf(stats.getLastUnsuccessfulDuration());
         } else {
            unsuccessfulAverage      = String.valueOf(unsuccessfulDuration / unsuccessfulCalls);
            unsuccessfulMin          = String.valueOf(stats.getUnsuccessfulMin());
            unsuccessfulMinStart     = DateConverter.toDateString(_timeZone, stats.getUnsuccessfulMinStart());
            unsuccessfulMax          = String.valueOf(stats.getUnsuccessfulMax());
            unsuccessfulMaxStart     = DateConverter.toDateString(_timeZone, stats.getUnsuccessfulMaxStart());
            lastUnsuccessfulStart    = DateConverter.toDateString(_timeZone, stats.getLastUnsuccessfulStart());
            lastUnsuccessfulDuration = String.valueOf(stats.getLastUnsuccessfulDuration());
         }

         builder.startTag("function");
         builder.attribute("name", function.getName());

         // Successful
         builder.startTag("successful");
         builder.attribute("count",    String.valueOf(successfulCalls));
         builder.attribute("average",  successfulAverage);
         builder.startTag("min");
         builder.attribute("start",    successfulMinStart);
         builder.attribute("duration", successfulMin);
         builder.endTag(); // min
         builder.startTag("max");
         builder.attribute("start",    successfulMaxStart);
         builder.attribute("duration", successfulMax);
         builder.endTag(); // max
         builder.startTag("last");
         builder.attribute("start",    lastSuccessfulStart);
         builder.attribute("duration", lastSuccessfulDuration);
         builder.endTag(); // last
         builder.endTag(); // successful

         // Unsuccessful
         builder.startTag("unsuccessful");
         builder.attribute("count",    String.valueOf(unsuccessfulCalls));
         builder.attribute("average",  unsuccessfulAverage);
         builder.startTag("min");
         builder.attribute("start",    unsuccessfulMinStart);
         builder.attribute("duration", unsuccessfulMin);
         builder.endTag(); // min
         builder.startTag("max");
         builder.attribute("start",    unsuccessfulMaxStart);
         builder.attribute("duration", unsuccessfulMax);
         builder.endTag(); // max
         builder.startTag("last");
         builder.attribute("start",    lastUnsuccessfulStart);
         builder.attribute("duration", lastUnsuccessfulDuration);
         builder.endTag(); // last
         builder.endTag(); // unsuccessful

         builder.endTag(); // function
      }

      return builder;
   }

   /**
    * Returns the XINS version.
    *
    * @return
    *    the call result, never <code>null</code>.
    */
   private final CallResult doGetVersion() {

      CallResultBuilder builder = new CallResultBuilder();

      builder.param("java.version",   System.getProperty("java.version"));
      builder.param("xmlenc.version", org.znerd.xmlenc.Library.getVersion());
      builder.param("xins.version",   Library.getVersion());

      return builder;
   }

   /**
    * Returns the settings.
    *
    * @return
    *    the call result, never <code>null</code>.
    */
   private final CallResult doGetSettings() {

      CallResultBuilder builder = new CallResultBuilder();

      // Initialization settings
      Enumeration names = _initSettings.propertyNames();
      builder.startTag("initialization");
      while (names.hasMoreElements()) {
         String key   = (String) names.nextElement();
         String value = _initSettings.getProperty(key);

         builder.startTag("property");
         builder.attribute("name", key);
         builder.pcdata(value);
         builder.endTag();
      }
      builder.endTag();

      // System properties
      names = System.getProperties().propertyNames();
      builder.startTag("runtime");
      while (names.hasMoreElements()) {
         String key   = (String) names.nextElement();
         String value = System.getProperty(key);

         if (key != null && value != null && key.length() > 0 && value.length() > 0) {
            builder.startTag("property");
            builder.attribute("name", key);
            builder.pcdata(value);
            builder.endTag();
         }
      }
      builder.endTag();

      return builder;
   }
}
