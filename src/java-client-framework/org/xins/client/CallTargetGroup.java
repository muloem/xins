/*
 * $Id$
 */
package org.xins.client;

import java.io.IOException;
import java.util.Map;
import org.xins.util.MandatoryArgumentChecker;

/**
 * Grouping of function callers. This grouping is of a certain type (see
 * {@link #getType()}) that sets the algorithm to be used to determine what
 * underlying actual function caller is used to call the remote API
 * implementation. A <code>CallTargetGroup</code> can contain other
 * <code>CallTargetGroup</code> instances.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public abstract class CallTargetGroup extends AbstractFunctionCaller {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The <em>ordered</em> call target group type.
    */
   public static final Type ORDERED_TYPE = new Type();

   /**
    * The <em>random</em> call target group type.
    */
   public static final Type RANDOM_TYPE = new Type();

   /**
    * The <em>round robin</em> call target group type.
    */
   public static final Type ROUND_ROBIN_TYPE = new Type();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>CallTargetGroup</code>.
    *
    * @param type
    *    the type, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>type == null</code>.
    */
   CallTargetGroup(Type type) throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("type", type);

      _type = type;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The type of this group. This field cannot be <code>null</code>.
    */
   private final Type _type;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public final CallResult call(String sessionID,
                                String functionName,
                                Map    parameters)
   throws IllegalArgumentException, IOException, InvalidCallResultException {
      return callImpl(sessionID, functionName, parameters);
   }

   /**
    * Calls the specified API function with the specified parameters,
    * optionally at the specified host only.
    *
    * @param hostName
    *    the name of the host the call the API function on, or
    *    <code>null</code> if any of the underlying actual function callers
    *    can be called.
    *
    * @param sessionID
    *    the session identifier, if any, or <code>null</code> if the function
    *    is session-less.
    *
    * @param functionName
    *    the name of the function to be called, not <code>null</code>.
    *
    * @param parameters
    *    the parameters to be passed to that function, or
    *    <code>null</code>; keys must be {@link String Strings}, values can be
    *    of any class.
    *
    * @return
    *    the call result, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>functionName == null</code>.
    *
    * @throws IOException
    *    if the API could not be contacted due to an I/O error.
    *
    * @throws InvalidCallResultException
    *    if the calling of the function failed or if the result from the
    *    function was invalid.
    */
   public final CallResult call(String hostName, String sessionID, String functionName, Map parameters)
   throws IllegalArgumentException, IOException, InvalidCallResultException {
      return callImpl(sessionID, functionName, parameters);
   }

   /**
    * Calls the specified API function with the specified parameters (actual
    * implementation). The type (see {@link #getType()}) determines what
    * actual function caller will be used.
    *
    * @param sessionID
    *    the session identifier, if any, or <code>null</code> if the function
    *    is session-less.
    *
    * @param functionName
    *    the name of the function to be called, not <code>null</code>.
    *
    * @param parameters
    *    the parameters to be passed to that function, or
    *    <code>null</code>; keys must be {@link String Strings}, values can be
    *    of any class.
    *
    * @return
    *    the call result, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>functionName == null</code>.
    *
    * @throws IOException
    *    if the API could not be contacted due to an I/O error.
    *
    * @throws InvalidCallResultException
    *    if the calling of the function failed or if the result from the
    *    function was invalid.
    */
   abstract CallResult callImpl(String sessionID,
                                String functionName,
                                Map    parameters)
   throws IllegalArgumentException, IOException, InvalidCallResultException;


   //-------------------------------------------------------------------------
   // Inner classes
   //-------------------------------------------------------------------------

   /**
    * The type of a <code>CallTargetGroup</code>. The type determines the
    * algorithm to be used to determine what underlying actual function caller
    * is used to call the remote API implementation.
    *
    * @version $Revision$ $Date$
    * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
    */
   public static final class Type extends Object {

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      /**
       * Constructs a new <code>Type</code>.
       */
      private Type() {
         // empty
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------
   } 
}
