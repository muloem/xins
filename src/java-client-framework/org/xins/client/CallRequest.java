/*
 * $Id$
 */
package org.xins.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Collections;
import org.apache.log4j.Logger;
import org.xins.util.MandatoryArgumentChecker;
import org.xins.util.collections.CollectionUtils;

/**
 * Abstraction of a XINS request.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.46
 */
public final class CallRequest extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The logging category used by this class. This class field is never
    * <code>null</code>.
    */
   private final static Logger LOG = Logger.getLogger(CallRequest.class.getName());


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>CallRequest</code> object.
    *
    * @param sessionID
    *    the session identifier, or <code>null</code> if the no session
    *    identifier should be passed with the call.
    *
    * @param functionName
    *    the name of the function to call, cannot be <code>null</code>.
    *
    * @param parameters
    *    the input parameters, if any, can be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>functionName == null</code>.
    */
   public CallRequest(String sessionID, String functionName, Map parameters)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("functionName", functionName);

      // Store the function name and copy the Map
      _sessionID    = sessionID;
      _functionName = functionName;
      _parameters   = (parameters != null)
                    ? Collections.unmodifiableMap(new HashMap(parameters))
                    : CollectionUtils.EMPTY_MAP;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The session identifier. This field can be <code>null</code>.
    */
   private final String _sessionID;

   /**
    * The name of the function to call. This field cannot be
    * <code>null</code>.
    */
   private final String _functionName;

   /**
    * The parameters to pass in the request, and their respective values. This
    * field is never <code>null</code>. If there are no parameters, then this
    * field will be set to {@link CollectionUtils#EMPTY_MAP}.
    */
   private final Map _parameters;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the session identifier, if any.
    *
    * @return
    *    the session identifier, or <code>null</code> if no session identifier
    *    should be passed in the request.
    */
   public String getSessionID() {
      return _sessionID;
   }

   /**
    * Returns the name of the function to call.
    *
    * @return
    *    the name of the function to call, never <code>null</code>.
    */
   public String getFunctionName() {
      return _functionName;
   }

   /**
    * Gets all parameters to pass with the call, with their respective values.
    *
    * @return
    *    an unmodifiable <code>Map</code> containing all parameters, never
    *    <code>null</code>; the keys will be the names of the parameters
    *    ({@link String} objects, cannot be <code>null</code>), the values will be the parameter values
    *    ({@link String} objects as well, cannot be <code>null</code>).
    */
   public Map getParameters() {
      return _parameters;
   }

   /**
    * Gets the value of the specified parameter.
    *
    * @param name
    *    the parameter name, not <code>null</code>.
    *
    * @return
    *    string containing the value of the parameter, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null</code>.
    */
   public String getParameter(String name)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("name", name);

      // The map can never be null, so call get(key) directly
      return (String) _parameters.get(name);
   }
}
