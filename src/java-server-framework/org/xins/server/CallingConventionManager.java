/*
 * $Id$
 *
 * Copyright 2003-2006 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

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
 *
 * @see CallingConvention
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
    * The name of the default calling convention. There is always a default
    * calling convention (at least after bootstrapping).
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
      _defaultConventionName = determineDefaultConvention(properties);

      // Create a list with all known calling convention names
      ArrayList conventions = new ArrayList(CONVENTIONS);
      
      // Append the defined calling conventions
      Iterator itCustomCC = properties.getNames();
      while (itCustomCC.hasNext()) {
         String nextProperty = (String) itCustomCC.next();
         if (nextProperty.startsWith(APIServlet.API_CALLING_CONVENTION_PROPERTY + '.') &&
             !nextProperty.equals(APIServlet.API_CALLING_CONVENTION_CLASS_PROPERTY)) {
            String conventionName = nextProperty.substring(32, nextProperty.length() - 6);
            conventions.add(conventionName);
         }
      }

      // Construct and bootstrap all calling conventions
      Iterator itConventions = conventions.iterator();
      while (itConventions.hasNext()) {

         // Create the calling convention
         String name = (String) itConventions.next();
         boolean defaultConvention = name.equals(_defaultConventionName);
         CallingConvention cc = create(properties, name);

         // If created, store the object and attempt bootstrapping
         if (cc != null) {
            _conventions.put(name, cc);
            bootstrap(name, cc, properties);
            
            if (defaultConvention && cc.getState() != Manageable.BOOTSTRAPPED) {
               throw new BootstrapException("Failed to bootstrap the default calling convention.");
            }
            
         // Otherwise, if it's the default calling convention, fails
         } else if (defaultConvention) {
            throw new BootstrapException("Failed to create the default calling convention.");
            
         // Otherwise remember we know this one, but it failed to create
         } else {
            _conventions.put(name, CREATION_FAILED);
         }
      }
   }

   /**
    * Determines the default calling convention.
    *
    * @param properties
    *    the bootstrap properties, cannot be <code>null</code>.
    *
    * @throws MissingRequiredPropertyException
    *    if a required property is not given.
    *
    * @throws InvalidPropertyValueException
    *    if the value of a certain property is invalid.
    */
   private String determineDefaultConvention(PropertyReader properties)
   throws MissingRequiredPropertyException,
          InvalidPropertyValueException {

      // Name of the default calling convention (if any)
      String name = TextUtils.trim(properties.get(APIServlet.API_CALLING_CONVENTION_PROPERTY), null);

      // No calling convention defined
      if (name == null) {

         // Log: No custom calling convention specified
         Log.log_3246();

         // Fallback to the XINS-specified default calling convention
         name = APIServlet.STANDARD_CALLING_CONVENTION;
      }

      // Log: Determined default calling convention
      Log.log_3245(name);

      // Return the name of the default calling convention
      return name;
   }

   /**
    * Constructs the calling convention with the specified name, using the
    * specified bootstrap properties. This method is called for both
	 * <em>regular</em> and <em>custom</em> calling conventions.
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

      // Determine the name of the CallingConvention class
      String className = null;
      if (name.charAt(0) == '_') {
         className = classNameForRegular(name);
      } else {
         className = properties.get(APIServlet.API_CALLING_CONVENTION_PROPERTY + '.' + name + ".class");
      }

      // If the class could not be determined, then return null
      if (className == null) {
         Log.log_3239(null, name, className);
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
    * Determines the name of the class that represents the regular calling
    * convention with the specified name. A <em>regular</em> calling
    * convention is one that comes with the XINS framework.
    *
    * @param name
    *    the name of the calling convention, should not be <code>null</code>
    *    and should normally starts with an underscore character
    *    (<code>'_'</code>).
    *
    * @return
    *    the name of the {@link CallingConvention} class that matches the
    *    specified calling convention name, or <code>null</code> if unknown.
    */
   private String classNameForRegular(String name) {

      // XINS old-style
      if (name.equals(APIServlet.OLD_STYLE_CALLING_CONVENTION)) {
         return "org.xins.server.OldStyleCallingConvention";

      // XINS standard (new-style)
      } else if (name.equals(APIServlet.STANDARD_CALLING_CONVENTION)) {
         return "org.xins.server.StandardCallingConvention";

      // XINS XML
      } else if (name.equals(APIServlet.XML_CALLING_CONVENTION)) {
         return "org.xins.server.XMLCallingConvention";

      // XSLT
      } else if (name.equals(APIServlet.XSLT_CALLING_CONVENTION)) {
         return "org.xins.server.XSLTCallingConvention";

      // SOAP
      } else if (name.equals(APIServlet.SOAP_CALLING_CONVENTION)) {
         return "org.xins.server.SOAPCallingConvention";

      // XML-RPC
      } else if (name.equals(APIServlet.XML_RPC_CALLING_CONVENTION)) {
         return "org.xins.server.XMLRPCCallingConvention";

      // Unrecognized
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
            
            // Fails if the default calling convention fails to initialize
            if (((CallingConvention) cc).getState() != Manageable.USABLE &&
                  name.equals(_defaultConventionName)) {
               throw new InitializationException("Failed to initialize the default calling convention.");
            }
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
      if (cc.getState() != Manageable.BOOTSTRAPPED &&
          cc.getState() != Manageable.USABLE) {
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
    * Determines the calling convention to use for the specified request.
    *
    * @param request
    *    the incoming request, cannot be <code>null</code>.
    *
    * @return
    *    the calling convention to use, never <code>null</code>.
    *
    * @throws InvalidRequestException
    *    if the request is considered invalid, for example because the calling
    *    convention specified in the request is unknown.
    */
   CallingConvention getCallingConvention(HttpServletRequest request)
   throws InvalidRequestException {

      // Get the value of the input parameter that determines the convention
      String paramName = APIServlet.CALLING_CONVENTION_PARAMETER;
      String ccName    = request.getParameter(paramName);

      // If a calling convention is specified then use that one
      if (! TextUtils.isEmpty(ccName)) {
         return getCallingConvention(ccName);

      // Otherwise try to detect which one is appropriate
      } else {
         return detectCallingConvention(request);
      }
   }


   /**
    * Gets the calling convention for the given name.
    *
    * <p>The returned calling convention is bootstrapped and initialized.
    *
    * @param name
    *    the name of the calling convention to retrieve, should not be
    *    <code>null</code>.
    *
    * @return
    *    the calling convention, never <code>null</code>.
    *
    * @throws InvalidRequestException
    *    if the calling convention name is unknown.
    */
   private CallingConvention getCallingConvention(String name)
   throws InvalidRequestException {

      // Get the CallingConvention object
      Object o = _conventions.get(name);

      // Not found
      if (o == null) {
         String detail = "Calling convention \""
                       + name
                       + "\" is unknown.";
         Log.log_3507(name, detail);
         throw new InvalidRequestException(detail);

      // Creation failed
      } else if (o == CREATION_FAILED) {
         String detail = "Calling convention \""
                       + name
                       + "\" is known, but could not be created.";
         Log.log_3507(name, detail);
         throw new InvalidRequestException(detail);

      // Calling convention is recognized and was created OK
      } else {

         // Not usable (so not bootstrapped and initialized)
         CallingConvention cc = (CallingConvention) o;
         if (! cc.isUsable()) {
            String detail = "Calling convention \""
                          + name
                          + "\" is known, but is uninitialized.";
            Log.log_3507(name, detail);
            throw new InvalidRequestException(detail);
         }

         return cc;
      }
   }

   /**
    * Gets the calling convention for the given name, or <code>null</code> if
    * the calling convention is not found or not usable.
    *
    * <p>The returned calling convention is bootstrapped and initialized.
    *
    * @param name
    *    the name of the calling convention to retrieve, should not be
    *    <code>null</code>.
    *
    * @return
    *    the calling convention, or <code>null</code>.
    */
   private CallingConvention getCallingConvention2(String name) {

      // Get the CallingConvention object
      Object o = _conventions.get(name);

      // Not a CallingConvention instance
      if (! (o instanceof CallingConvention)) {
         return null;

      // Calling convention is recognized and was created OK
      } else {

         // Cast
         CallingConvention cc = (CallingConvention) o;

         // Return null if it's unusable
         if (! cc.isUsable()) {
            return null;

         // Otherwise return the object
         } else {
            return cc;
         }
      }
   }

   /**
    * Attempts to detect which calling convention is the most appropriate for
    * an incoming request. This method is called when the calling convention
    * is not explicitly specified in the request.
    *
    * <p>The {@link CallingConvention#matchesRequest(HttpServletRequest)}
    * method is used to determine which calling conventions match. Then
    * the following algorithm is used to chose one:
    *
    * <ul>
    *    <li>if the default calling convention matches, use that;
    *    <li>otherwise if the {@link XSLTCallingConvention} matches and at
    *        least one of the parameters specific for the this calling
    *        convention is set, then use it;
    *    <li>otherwise if the {@link StandardCallingConvention} matches, use
    *        that;
    *    <li>otherwise if there is exactly one other calling convention that
    *        matches, use that one;
    *    <li>if none of the calling conventions match, throw an
    *        {@link InvalidRequestException}, indicating that no match could
    *        be found;
    *    <li>if multiple calling conventions match, throw an
    *        {@link InvalidRequestException}, indicating that several matches
    *        were found;
    * </ul>
    *
    * @param request
    *    the incoming request, cannot be <code>null</code>.
    *
    * @return
    *    the calling convention to use, never <code>null</code>.
    *
    * @throws InvalidRequestException
    *    if the request is considered invalid, for example because the calling
    *    convention specified in the request is unknown.
    */
   CallingConvention detectCallingConvention(HttpServletRequest request)
   throws InvalidRequestException {

      String noMatches = "Request does not specify a calling convention, it "
                       + "cannot be handled by the default calling "
                       + "convention and it was not possible to find any "
                       + "calling convention that can handle it.";

      String multipleMatches = "Request does not specify a calling "
                             + "convention, it cannot be handled by the "
                             + "default calling convention and multiple "
                             + "calling conventions are able to handle it: "
                             + '"';

      String defaultName = _defaultConventionName;

      // Get some calling convention instances in advance
      CallingConvention defCC = getCallingConvention2(defaultName);
      CallingConvention xslCC = getCallingConvention2("_xins-xslt");
      CallingConvention stdCC = getCallingConvention2("_xins-std");

      // Log: Request does not specify any calling convention
      Log.log_3508();

      // See if the default calling convention matches
      if (defCC != null && defCC.matchesRequest(request)) {
         Log.log_3509(defCC.getClass().getName());
         return defCC;
      }

      // If not, see if XSLT-specific properties are set /and/ _xins-xslt
      // matches
      if (xslCC != null && xslCC != defCC && xslCC.matchesRequest(request)) {

         // Determine if one of the two XSLT-specific parameters is set
         String p1 = request.getParameter(XSLTCallingConvention.TEMPLATE_PARAMETER);
         String p2 = request.getParameter(XSLTCallingConvention.CLEAR_TEMPLATE_CACHE_PARAMETER);

         // Use the XSLT calling convention if and only if at least one of the
         // parameters is actually set
         if (! (TextUtils.isEmpty(p1) && TextUtils.isEmpty(p2))) {
            Log.log_3509(XSLTCallingConvention.class.getName());
            return xslCC;
         }
      }

      // If not, see if _xins-std matches
      if (stdCC != null && stdCC != defCC && stdCC.matchesRequest(request)) {
         Log.log_3509(StandardCallingConvention.class.getName());
         return stdCC;
      }

      // Local variable to hold the first matching calling convention
      CallingConvention matching = null;

      // Determine which calling conventions match
      Set       entrySet  = _conventions.entrySet();
      Iterator  iterator  = entrySet.iterator();
      while (iterator.hasNext()) {
         Map.Entry entry = (Map.Entry) iterator.next();
         Object    value =             entry.getValue();

         // Skip all values that are not CallingConvention instances
         if (! (value instanceof CallingConvention)) {
            continue;
         }

         // Convert the value to a CallingConvention
         CallingConvention cc = (CallingConvention) value;

         // Skip the default and the standard calling conventions, we
         // already established that they cannot handle the request
         if (cc == defCC || cc == stdCC) {
            continue;
         }

         // Determine whether this one can handle it
         if (cc.matchesRequest(request)) {

            // First match
            if (matching == null) {
               matching = cc;

            // Fail: Multiple matches
            } else {
               Log.log_3511();
               String message = multipleMatches
                              + matching.getClass().getName()
                              + "\", \""
                              + cc.getClass().getName()
                              + "\".";
               throw new InvalidRequestException(message);
            }
         }
      }

      // One match
      if (matching != null) {
         return matching;

      // Fail: No matches
      } else {
         Log.log_3510();
         throw new InvalidRequestException(noMatches);
      }
   }
}
