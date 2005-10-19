/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;

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
    * The fully-qualified name of this class.
    */
   private static final String CLASSNAME =
      CallingConventionManager.class.getName();

   /**
    * The list of the calling conventions currently included in XINS.
    */
   private final static String[] CONVENTIONS = {
      APIServlet.STANDARD_CALLING_CONVENTION,
      APIServlet.OLD_STYLE_CALLING_CONVENTION,
      APIServlet.XML_CALLING_CONVENTION,
      APIServlet.XSLT_CALLING_CONVENTION,
      APIServlet.SOAP_CALLING_CONVENTION,
      APIServlet.XML_RPC_CALLING_CONVENTION,
   };

   /**
    * Array of type <code>Class</code> that is used when constructing a
    * <code>CallingConvention</code> instance via RMI.
    */
   private final static Class[] CONSTRUCTOR_ARG_CLASSES = { API.class };


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
    * calling convention, the value is the calling convention object.
    *
    * <p>This field is initialized during bootstrapping.
    */
   private final HashMap _conventions;


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

      // Determine the name of the default calling convention
      String prop     = APIServlet.API_CALLING_CONVENTION_PROPERTY;
      String fallback = APIServlet.STANDARD_CALLING_CONVENTION;
      String ccName   = TextUtils.trim(properties.get(prop), fallback);

      // Attempt to construct an instance
      CallingConvention cc = create(properties, ccName);

      // If the factory method returned null, then the specified name
      // does not identify a known calling convention
      if (cc == null) {
         String detail = "No such calling convention.";
         Log.log_3240(ccName, prop, ccName, detail);
         throw new InvalidPropertyValueException(prop, ccName, detail);
      }

      // Store the convention
      _conventions.put(ccName, cc);

      // Bootstrap the default calling convention
      bootstrap(ccName, cc, properties);

      // Store the default calling convention
      _defaultConventionName = ccName;

      // TODO: Log that we use the specified calling convention

      // Construct and bootstrap all other calling conventions.
      for (int i = 0; i < CONVENTIONS.length; i++) {
         ccName = CONVENTIONS[i];

         // Skip the default calling convention
         if (ccName.equals(_defaultConventionName)) {
            continue;
         }

         // Create the calling convention
         cc = create(properties, ccName);

         if (cc != null) {

            // Store it in the map with other calling conventions
            _conventions.put(ccName, cc);

            // Bootstrap it
            bootstrap(ccName, cc, properties);
         }
      }
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
    *    a non-bootstrapped {@link CallingConvention} instances that matches
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

      // Determine the name and class of the custom calling convention, if any
      String prop    = APIServlet.API_CALLING_CONVENTION_PROPERTY;
      String cccName = TextUtils.trim(properties.get(prop), null);
      String cccClass = null;
      if (cccName != null) {
         prop     = APIServlet.API_CALLING_CONVENTION_CLASS_PROPERTY;
         cccClass = TextUtils.trim(properties.get(prop), null);
      }

      // Old-style XINS calling convention
      if (APIServlet.OLD_STYLE_CALLING_CONVENTION.equals(name)) {
         return new OldStyleCallingConvention();

      // XINS standard calling convention
      } else if (APIServlet.STANDARD_CALLING_CONVENTION.equals(name)) {
         return new StandardCallingConvention();

      // XINS XML calling convention
      } else if (APIServlet.XML_CALLING_CONVENTION.equals(name)) {
         return new XMLCallingConvention();

      // XINS XSLT calling convention
      } else if (APIServlet.XSLT_CALLING_CONVENTION.equals(name)) {
         return new XSLTCallingConvention();

      // XINS SOAP calling convention
      } else if (APIServlet.SOAP_CALLING_CONVENTION.equals(name)) {
         return new SOAPCallingConvention(_api);

      // XINS XML-RPC calling convention
      } else if (APIServlet.XML_RPC_CALLING_CONVENTION.equals(name)) {
         return new XMLRPCCallingConvention(_api);

      // The custom calling convention
      } else if (cccName.equals(name) && cccClass != null) {
         return construct(name, cccClass);

      // If the name is unrecognized, return null
      } else {
         return null;
      }
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

      String thisMethod = "construct(java.lang.String,java.lang.String)";

      // Check arguments
      MandatoryArgumentChecker.check("name", name, "className", className);

      // Try to load the class
      Class clazz;
      try {
         clazz = Class.forName(className);
      } catch (Throwable exception) {
         Log.log_3560(exception, name, className);
         return null;
      }

      // Get the constructor that accepts an API argument
      Constructor con = null;
      try {
         con = clazz.getConstructor(CONSTRUCTOR_ARG_CLASSES);
      } catch (Throwable exception) {
         // fall through, do not even log
      }

      // If that succeeded, try to invoke the constructor
      if (con != null) {
         Object[] args = { _api };
         try {
            return (CallingConvention) con.newInstance(args);
         } catch (Throwable exception) {
            Utils.logIgnoredException(CLASSNAME,
                                      thisMethod,
                                      clazz.getName(),
                                      "newInstance(java.lang.Object[])",
                                      exception);
         }
      }

      // Secondly try a constructor with no arguments
      try {
         return (CallingConvention) clazz.newInstance();
      } catch (Throwable exception) {
         Log.log_3560(exception, name, className);
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
    *    if <code>cc == null || properties == null</code>.
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
   private void bootstrap(String            name,
                          CallingConvention cc,
                          PropertyReader    properties)
   throws IllegalArgumentException,
          MissingRequiredPropertyException,
          InvalidPropertyValueException,
          BootstrapException {

      // Check arguments
      MandatoryArgumentChecker.check("name",       name,
                                     "cc",         cc,
                                     "properties", properties);

      // Bootstrapping calling convention
      Log.log_3237(name);

      try {
         cc.bootstrap(properties);
         Log.log_3238(name);

      // Missing property
      } catch (MissingRequiredPropertyException exception) {
         Log.log_3239(name, exception.getPropertyName());
         throw exception;

      // Invalid property
      } catch (InvalidPropertyValueException exception) {
         Log.log_3240(name,
                      exception.getPropertyName(),
                      exception.getPropertyValue(),
                      exception.getReason());
         throw exception;

      // Catch BootstrapException and any other exceptions not caught
      // by previous catch statements
      } catch (Throwable exception) {

         // Log event
         Log.log_3241(exception, name);

         // Throw a BootstrapException. If necessary, wrap around the
         // caught exception
         if (exception instanceof BootstrapException) {
            throw (BootstrapException) exception;
         } else {
            throw new BootstrapException(exception);
         }
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
         String            name = (String) iterator.next();
         CallingConvention cc   = (CallingConvention) _conventions.get(name);

         // Attempt the initialization
         Log.log_3435(name);
         try {
            cc.init(properties);
            Log.log_3436(name);

         // Missing property
         } catch (MissingRequiredPropertyException exception) {
            Log.log_3437(name, exception.getPropertyName());
            throw exception;

         // Invalid property
         } catch (InvalidPropertyValueException exception) {
            Log.log_3438(name,
                         exception.getPropertyName(),
                         exception.getPropertyValue(),
                         exception.getReason());
            throw exception;

         // Catch InitializationException and any other exceptions not caught
         // by previous catch statements
         } catch (Throwable exception) {

            // Log event
            Log.log_3439(exception, name);

            // Throw an InitializationException. If necessary, wrap around the
            // caught exception
            if (exception instanceof InitializationException) {
               throw (InitializationException) exception;
            } else {
               throw new InitializationException(exception);
            }
         }
      }
   }

   /**
    * Gets the calling convention for the given name.
    *
    * <p>If the given name is <code>null</code> or empty, the default calling
    * convention is returned.
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
      CallingConvention cc = (CallingConvention) _conventions.get(name);

      // Not found
      if (cc == null) {
         String detail = "Calling convention \""
                       + name
                       + "\" is unknown.";
         throw new InvalidRequestException(detail);

      // Not usable (so not bootstrapped and initialized)
      } else if (! cc.isUsable()) {
         String detail = "Calling convention \""
                       + name
                       + "\" is uninitialized.";
         throw new InvalidRequestException(detail);
      }

      return cc;
   }
}
