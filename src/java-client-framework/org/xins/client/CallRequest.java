/*
 * $Id$
 */
package org.xins.client;

import java.util.Collections;

import org.xins.common.MandatoryArgumentChecker;

import org.xins.common.collections.PropertyReader;

/**
 * Abstraction of a XINS request.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 0.46
 */
public final class CallRequest extends Object {

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
    * Constructs a new <code>CallRequest</code> for the specified function and
    * parameters, disallowing fail-over.
    *
    * @param functionName
    *    the name of the function to call, cannot be <code>null</code>.
    *
    * @param parameters
    *    the input parameters, if any, can be <code>null</code> and should not
    *    be modifiable.
    *
    * @throws IllegalArgumentException
    *    if <code>functionName == null</code>.
    */
   public CallRequest(String functionName, PropertyReader parameters)
   throws IllegalArgumentException {
      this(functionName, parameters, false);
   }

   /**
    * Constructs a new <code>CallRequest</code> for the specified function and
    * parameters, possibly allowing fail-over.
    *
    * @param functionName
    *    the name of the function to call, cannot be <code>null</code>.
    *
    * @param parameters
    *    the input parameters, if any, can be <code>null</code> and should not
    *    be modifiable.
    *
    * @param failOverAllowed
    *    flag that indicates whether fail-over is in principle allowed, even
    *    if the request was already sent to the other end.
    *
    * @throws IllegalArgumentException
    *    if <code>functionName == null</code>.
    */
   public CallRequest(String         functionName,
                      PropertyReader parameters,
                      boolean        failOverAllowed)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("functionName", functionName);

      // Store the function name and copy the parameters
      _functionName    = functionName;
      _parameters      = parameters; // XXX: Make unmodifiable and change @param?
      _failOverAllowed = failOverAllowed;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The name of the function to call. This field cannot be
    * <code>null</code>.
    */
   private final String _functionName;

   /**
    * The parameters to pass in the request, and their respective values. This
    * field can be <code>null</code>.
    */
   private final PropertyReader _parameters;

   /**
    * Flag that indicates whether fail-over is in principle allowed, even if
    * the request was already sent to the other end.
    */
   private final boolean _failOverAllowed;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

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
    *    the parameters, or <code>null</code> if there are none.
    */
   public PropertyReader getParameters() {
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

      return _parameters == null ? null : _parameters.get(name);
   }

   /**
    * Determines whether fail-over is in principle allowed, even if the
    * request was already sent to the other end. 
    *
    * @return
    *    <code>true</code> if fail-over is in principle allowed, even if the
    *    request was already sent to the other end, <code>false</code>
    *    otherwise.
    *
    * @since XINS 0.202
    */
   public boolean isFailOverAllowed() {
      return _failOverAllowed;
   }
}
