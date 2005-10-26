/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.Utils;
import org.xins.common.collections.InvalidPropertyValueException;
import org.xins.common.collections.MissingRequiredPropertyException;
import org.xins.common.collections.PropertyReader;
import org.xins.common.manageable.BootstrapException;
import org.xins.common.manageable.InitializationException;
import org.xins.common.manageable.Manageable;
import org.xins.common.text.TextUtils;

/**
 * Manages the <code>CallingConvention</code> instances for the API.
 *
 * @version $Revision$ $Date$
 * @author Mees Witteman (<a href="mailto:mees.witteman@nl.wanadoo.com">mees.witteman@nl.wanadoo.com</a>)
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
class CallingConventionManager
extends Manageable {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * List of the names of the calling conventions currently included in
    * XINS.
    */
   private final static List CONVENTIONS = Arrays.asList(new String[] {
      APIServlet.STANDARD_CALLING_CONVENTION,
      APIServlet.OLD_STYLE_CALLING_CONVENTION,
      APIServlet.XML_CALLING_CONVENTION,
      APIServlet.XSLT_CALLING_CONVENTION,
      APIServlet.SOAP_CALLING_CONVENTION,
      APIServlet.XML_RPC_CALLING_CONVENTION,
   });

   /**
    * Array of type <code>Class</code> that is used when constructing a
    * <code>CallingConvention</code> instance via RMI.
    */
   private final static Class[] CONSTRUCTOR_ARG_CLASSES = { API.class };

   /**
    * Placeholder object used to indicate that the construction of a calling
    * convention object failed. Never <code>null</code>.
    */
   private final static Object CREATION_FAILED = new Object();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Creates a <code>CallingConventionManager</code> for the specified API.
    *
    * @param api
    *    the API, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>api == null</code>.
    */
   CallingConventionManager(API api)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("api", api);

      // Store the reference to the API
      _api = api;

      // Create a map to store the conventions in
      _conventions = new HashMap(89);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The API. Never <code>null</code>.
    */
   private final API _api;

   /**
    * The name of the default calling convention.
    *
    * <p>This field is initialized during bootstrapping.
    */
   private String _defaultConventionName;

   /**
    * Map containing all calling conventions. The key is the name of the
    * calling convention, the value is the calling convention object, or
    * {@link #CREATION_FAILED} if the calling convention object could not be
    * constructed.
    *
    * <p>This field is initialized during bootstrapping.
    */
   private final HashMap _conventions;

   /**
    * Name of the custom calling convention as specified in the bootstrap
    * properties. This field will only be set together with the field
    * {@link #_classCustomCC}.
    *
    * <p>This field is initialized during bootstrapping.
    */
   private String _nameCustomCC;

   /**
    * Name of the custom calling convention class as specified in the
    * bootstrap properties. This field will only be set together with the
    * field {@link #_nameCustomCC}.
    *
    * <p>This field is initialized during bootstrapping.
    */
   private String _classCustomCC;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Performs the bootstrap procedure (actual implementation).
    *
    * @param properties
    *    the bootstrap properties, not <code>null</code>.
    *
    * @throws MissingRequiredPropertyException
    *    if a required property is not given.
    *
    * @throws InvalidPropertyValueException
    *    if the value of a certain property is invalid.
    *
    * @throws BootstrapException
    *    if the bootstrapping failed for any other reason.
    */
   protected void bootstrapImpl(PropertyReader properties)
   throws MissingRequiredPropertyException,
          InvalidPropertyValueException,
          BootstrapException {

      // Determine the name and class of the custom calling convention
      determineCustomConvention(properties);

      // Determine the name of the default calling convention
      determineDefaultConvention(properties);

      // Create a list with all known calling convention names
      ArrayList conventions = new ArrayList(CONVENTIONS);
      if (_nameCustomCC != null) {
         conventions.add(_nameCustomCC);
      }

      // Construct and bootstrap all calling conventions
      for (int i = 0, size = conventions.size(); i < size; i++) {

         // Create the calling convention
         String            name = (String) conventions.get(i);
         CallingConvention cc   = create(properties, name);

         // If created, store the object and attempt bootstrapping
         if (cc != null) {
            _conventions.put(name, cc);
            bootstrap(name, cc, properties);

         // Otherwise remember we know this one, but it failed to create
         } else {
            _conventions.put(name, CREATION_FAILED);
         }
      }
   }

   /**
    * Determines the name and class name for the custom calling convention
    * defined in the specified bootstrap properties.
    *
    * @param properties
    *    the bootstrap properties, cannot be <code>null</code>.
    *
    * @throws NullPointerException
    *    if <code>properties == null</code>.
    *
    * @throws MissingRequiredPropertyException
    *    if a required property is not given.
    *
    * @throws InvalidPropertyValueException
    *    if the value of a certain property is invalid.
    */
   private void determineCustomConvention(PropertyReader properties)
   throws NullPointerException,
          MissingRequiredPropertyException,
          InvalidPropertyValueException {

      // Get bootstrap property names
      String nameProp  = APIServlet.API_CALLING_CONVENTION_PROPERTY;
      String classProp = APIServlet.API_CALLING_CONVENTION_CLASS_PROPERTY;

      // Determine the name of the custom calling convention, if any
      String name = TextUtils.trim(properties.get(nameProp), null);

      // No custom calling convention is specified
      if (name == null) {
         Log.log_3246(nameProp);
         return;
      }

      // Get the name of the class, default to null
      String className = TextUtils.trim(properties.get(classProp), null);

      // Custom calling convention class not specified
      if (className == null) {
         throw new MissingRequiredPropertyException(classProp);
      }

      // Try to load the class
      try {
         Class.forName(className);
      } catch (Throwable exception) {
         throw new InvalidPropertyValueException(classProp, className,
                                                 "Unable to load class.");
      }

      // Store the custom calling convention name and class
      _nameCustomCC  = name;
      _classCustomCC = className;

      Log.log_3247(nameProp, name, className);
   }

   /**
    * Determines the name of the default calling convention and stores it in a
    * field. The field it is stored in is {@link #_defaultConventionName}.
    *
    * @param properties
    *    the bootstrap properties, cannot be <code>null</code>.
    *
    * @throws NullPointerException
    *    if <code>properties == null</code>.
    */
   private void determineDefaultConvention(PropertyReader properties)
   throws NullPointerException {

      // Get the value of the bootstrap property, fallback to default
      String prop            = APIServlet.API_CALLING_CONVENTION_PROPERTY;
      String fallback        = APIServlet.STANDARD_CALLING_CONVENTION;
      _defaultConventionName = TextUtils.trim(properties.get(prop), fallback);

      // Log: Determined the default calling convention
      Log.log_3245(_defaultConventionName);
   }

   /**
    * Constructs the calling convention with the specified name, using the
    * specified bootstrap properties.
    *
    * <p>If the name does not identify a recognized calling convention, then
    * <code>null</code> is returned.
    *
    * @param properties
    *    the bootstrap properties, cannot be <code>null</code>.
    *
    * @param name
    *    the name of the calling convention to construct, cannot be
    *    <code>null</code>.
    *
    * @return
    *    a non-bootstrapped {@link CallingConvention} instance that matches
    *    the specified name, or <code>null</code> if no match is found.
    *
    * @throws IllegalArgumentException
    *    if <code>properties == null || name == null</code>.
    */
   private CallingConvention create(PropertyReader properties,
                                    String         name)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("properties", properties, "name", name);

      String className = null;

      // Regular calling conventions
      if (APIServlet.OLD_STYLE_CALLING_CONVENTION.equals(name)) {
         className = "org.xins.server.OldStyleCallingConvention";
      } else if (APIServlet.STANDARD_CALLING_CONVENTION.equals(name)) {
         className = "org.xins.server.StandardCallingConvention";
      } else if (APIServlet.XML_CALLING_CONVENTION.equals(name)) {
         className = "org.xins.server.XMLCallingConvention";
      } else if (APIServlet.XSLT_CALLING_CONVENTION.equals(name)) {
         className = "org.xins.server.XSLTCallingConvention";
      } else if (APIServlet.SOAP_CALLING_CONVENTION.equals(name)) {
         className = "org.xins.server.SOAPCallingConvention";
      } else if (APIServlet.XML_RPC_CALLING_CONVENTION.equals(name)) {
         className = "org.xins.server.XMLRPCCallingConvention";

      // Custom calling convention
      } else if (name.equals(_nameCustomCC)) {
         className = _classCustomCC;
      }

      // If the class could not be determined, then return null
      if (className == null) {
         // TODO: Log warning: No calling convention named <name>.
         //       (should probably be logged one level up)
         return null;
      }

      Log.log_3237(name, className);

      // Construct a CallingConvention instance
      CallingConvention cc = construct(name, className);

      // NOTE: Logging of construction failures is done in construct(...)

      // Constructed successfully
      if (cc != null) {
         Log.log_3238(name, className);
      }

      return cc;
   }

   /**
    * Constructs a new <code>CustomCallingConvention</code> instance by class
    * name.
    *
    * @param name
    *    the name of the calling convention, cannot be <code>null</code>.
    *
    * @param className
    *    the name of the class, cannot be <code>null</code>.
    *
    * @return
    *    the constructed {@link CallingConvention} instance, or
    *    <code>null</code> if the construction failed.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null || className == null</code>.
    */
   private CallingConvention construct(String name, String className)
   throws IllegalArgumentException {

      // Check arguments
      MandatoryArgumentChecker.check("name", name, "className", className);

      // Try to load the class
      Class clazz;
      try {
         clazz = Class.forName(className);
      } catch (Throwable exception) {
         Log.log_3239(exception, name, className);
         return null;
      }

      // Get the constructor that accepts an API argument
      Constructor con = null;
      try {
         con = clazz.getConstructor(CONSTRUCTOR_ARG_CLASSES);
      } catch (NoSuchMethodException exception) {
         // fall through, do not even log
      }

      // If there is such a constructor, invoke it
      if (con != null) {

         // Invoke it
         Object[] args = { _api };
         try {
            return (CallingConvention) con.newInstance(args);

         // If the constructor exists but failed, then construction failed
         } catch (Throwable exception) {
            String thisClass  = CallingConventionManager.class.getName();
            String thisMethod = "construct(java.lang.String,"
                              + "java.lang.String)";
            Utils.logIgnoredException(thisClass,
                                      thisMethod,
                                      con.getClass().getName(),
                                      "newInstance(java.lang.Object[])",
                                      exception);
            return null;
         }
      }

      // Secondly try a constructor with no arguments
      try {
         return (CallingConvention) clazz.newInstance();
      } catch (Throwable exception) {
         Log.log_3239(exception, name, className);
         return null;
      }
   }

   /**
    * Bootstraps the specified calling convention.
    *
    * @param name
    *    the name of the calling convention, cannot be <code>null</code>.
    *
    * @param cc
    *    the {@link CallingConvention} object to bootstrap, cannot be
    *    <code>null</code>.
    *
    * @param properties
    *    the bootstrap properties, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null || cc == null || properties == null</code>.
    */
   private void bootstrap(String            name,
                          CallingConvention cc,
                          PropertyReader    properties)
   throws IllegalArgumentException {

      // Check arguments
      MandatoryArgumentChecker.check("name",       name,
                                     "cc",         cc,
                                     "properties", properties);

      // Bootstrapping calling convention
      Log.log_3240(name);

      try {
         cc.bootstrap(properties);
         Log.log_3241(name);

      // Missing property
      } catch (MissingRequiredPropertyException exception) {
         Log.log_3242(name, exception.getPropertyName(),
                      exception.getDetail());

      // Invalid property
      } catch (InvalidPropertyValueException exception) {
         Log.log_3243(name,
                      exception.getPropertyName(),
                      exception.getPropertyValue(),
                      exception.getReason());

      // Catch BootstrapException and any other exceptions not caught
      // by previous catch statements
      } catch (Throwable exception) {
         Log.log_3244(exception, name);
      }
   }

   /**
    * Performs the initialization procedure (actual implementation).
    *
    * @param properties
    *    the initialization properties, not null.
    *
    * @throws MissingRequiredPropertyException
    *    if a required property is not given.
    *
    * @throws InvalidPropertyValueException
    *    if the value of a certain property is invalid.
    *
    * @throws InitializationException
    *    if the initialization failed, for any other reason.
    */
   protected void initImpl(PropertyReader properties)
   throws MissingRequiredPropertyException,
          InvalidPropertyValueException,
          InitializationException {

      // Loop through all CallingConvention instances
      Iterator iterator = _conventions.keySet().iterator();
      while (iterator.hasNext()) {

         // Determine the name and get the CallingConvention instance
         String name = (String) iterator.next();
         Object cc   = _conventions.get(name);

         // If creation of CallingConvention succeeded, then initialize it
         if (cc != CREATION_FAILED) {
            init(name, (CallingConvention) cc, properties);
         }
      }
   }

   /**
    * Initializes the specified calling convention.
    *
    * <p>If the specified calling convention is not even bootstrapped, the
    * initialization is not even attempted.
    *
    * @param name
    *    the name of the calling convention, cannot be <code>null</code>.
    *
    * @param cc
    *    the {@link CallingConvention} object to initialize, cannot be
    *    <code>null</code>.
    *
    * @param properties
    *    the initialization properties, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null || cc == null || properties == null</code>.
    */
   private void init(String            name,
                     CallingConvention cc,
                     PropertyReader    properties)
   throws IllegalArgumentException {

      // Check arguments
      MandatoryArgumentChecker.check("name",       name,
                                     "cc",         cc,
                                     "properties", properties);

      // If the CallingConvention is not even bootstrapped, then do not even
      // attempt to initialize it
      if (cc.getState() == Manageable.UNUSABLE) {
         return;
      }

      // Initialize calling convention
      Log.log_3435(name);

      try {
         cc.init(properties);
         Log.log_3436(name);

      // Missing property
      } catch (MissingRequiredPropertyException exception) {
         Log.log_3437(name, exception.getPropertyName(),
                      exception.getDetail());

      // Invalid property
      } catch (InvalidPropertyValueException exception) {
         Log.log_3438(name,
                      exception.getPropertyName(),
                      exception.getPropertyValue(),
                      exception.getReason());

      // Catch InitializationException and any other exceptions not caught
      // by previous catch statements
      } catch (Throwable exception) {
         Log.log_3439(exception, name);
      }
   }

   /**
    * Gets the calling convention for the given name.
    *
    * <p>If the given name is <code>null</code> or empty (after trimming),
    * then the default calling convention is returned.
    *
    * <p>The returned calling convention is bootstrapped and initialized.
    *
    * @param name
    *    the name of the calling convention to retrieve, can be
    *    <code>null</code>.
    *
    * @return
    *    the calling convention, never <code>null</code>.
    *
    * @throws InvalidRequestException
    *    if the calling convention name is unknown.
    */
   CallingConvention getCallingConvention(String name)
   throws InvalidRequestException {

      // Default to the default calling convention
      if (TextUtils.isEmpty(name)) {
         name = _defaultConventionName;
      }

      // Get the CallingConvention object
      Object o = _conventions.get(name);

      // Not found
      if (o == null) {
         String detail = "Calling convention \""
                       + name
                       + "\" is unknown.";
         throw new InvalidRequestException(detail);

      // Creation failed
      } else if (o == CREATION_FAILED) {
         String detail = "Calling convention \""
                       + name
                       + "\" is recognized, but could not be created.";
         throw new InvalidRequestException(detail);

      // Calling convention is recognized and was created OK
      } else {

         // Not usable (so not bootstrapped and initialized)
         CallingConvention cc = (CallingConvention) o;
         if (! cc.isUsable()) {
            String detail = "Calling convention \""
                          + name
                          + "\" is uninitialized.";
            throw new InvalidRequestException(detail);
         }

         return cc;
      }
   }
}
