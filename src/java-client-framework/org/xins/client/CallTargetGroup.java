/*
 * $Id$
 */
package org.xins.client;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;
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
public abstract class CallTargetGroup
extends AbstractCompositeFunctionCaller {

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

   /**
    * Creates a new <code>CallTargetGroup</code> of the specified type, with
    * the specified members.
    *
    * @param type
    *    the type, either {@link #ORDERED_TYPE}, {@link #RANDOM_TYPE} or
    *    {@link #ROUND_ROBIN_TYPE}, cannot be <code>null</code>.
    *
    * @param members
    *    the {@link List} of {@link FunctionCaller} members, cannot be
    *    <code>null</code>.
    *
    * @return
    *    the <code>CallTargetGroup</code>, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>type == null || members == null</code>.
    */
   public final static CallTargetGroup create(Type type, List members)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("type", type, "members", members);

      // Return an instance of a CallTargetGroup subclass
      if (type == ORDERED_TYPE) {
         return new OrderedCallTargetGroup(members);
      } else if (type == RANDOM_TYPE) {
         return null; // TODO: new RandomCallTargetGroup(members);
      } else if (type == ROUND_ROBIN_TYPE) {
         return null; // TODO: new RoundRobinCallTargetGroup(members);
      } else {
         throw new InternalError("Type not recognized.");
      }
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>CallTargetGroup</code>.
    *
    * @param type
    *    the type, cannot be <code>null</code>.
    *
    * @param members
    *    the members for this group, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>type == null || members == null</code>.
    */
   CallTargetGroup(Type type, List members) throws IllegalArgumentException {

      super(members);

      // Check preconditions
      MandatoryArgumentChecker.check("type", type);

      // Initialize fields
      _type                            = type;
      _actualFunctionCallersByURL = new HashMap();
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The type of this group. This field cannot be <code>null</code>.
    */
   private final Type _type;

   /**
    * Mappings from URLs to <code>ActualFunctionCaller</code>. The URLs are
    * stored as {@link String} instances. This {@link Map} cannot be
    * <code>null</code>.
    */
   private final Map _actualFunctionCallersByURL;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the type of this group.
    *
    * @return
    *    the type of this group, either {@link #ORDERED_TYPE},
    *     {@link #RANDOM_TYPE} or {@link #ROUND_ROBIN_TYPE}.
    */
   public final Type getType() {
      return _type;
   }

   /**
    * Gets the actual function caller for the specified URL.
    *
    * @param url
    *    the URL of the API to get the actual function caller for, not
    *    <code>null</code>.
    *
    * @return
    *    the actual function caller for the specified URL, not
    *    <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>url == null</code>.
    *
    * @throws NoSuchActualFunctionCallerException
    *    if there is no {@link ActualFunctionCaller} for the specified URL in
    *    this group or any of the contained groups (if any).
    */
   public final ActualFunctionCaller getActualFunctionCaller(String url)
   throws IllegalArgumentException, NoSuchActualFunctionCallerException {

      // Check preconditions
      MandatoryArgumentChecker.check("url", url);

      Object o = _actualFunctionCallersByURL.get(url);
      if (o == null) {
         throw new NoSuchActualFunctionCallerException(url);
      }

      return (ActualFunctionCaller) o;
   }

   public final CallResult call(String sessionID,
                                String functionName,
                                Map    parameters)
   throws IllegalArgumentException, IOException, InvalidCallResultException {
      return callImpl(sessionID, functionName, parameters);
   }

   /**
    * Calls the specified API function with the specified parameters,
    * optionally at the specified URL only.
    *
    * @param url
    *    the URL that identifies the API to call the function on, or
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
    * @throws NoSuchActualFunctionCallerException
    *    if there is no {@link ActualFunctionCaller} for the specified URL in
    *    this group or any of the contained groups (if any).
    *
    * @throws IOException
    *    if the API could not be contacted due to an I/O error.
    *
    * @throws InvalidCallResultException
    *    if the calling of the function failed or if the result from the
    *    function was invalid.
    */
   public final CallResult call(String url, String sessionID, String functionName, Map parameters)
   throws IllegalArgumentException, NoSuchActualFunctionCallerException, IOException, InvalidCallResultException {
      if (url == null) {
         return callImpl(sessionID, functionName, parameters);
      } else {
         return getActualFunctionCaller(url).call(sessionID, functionName, parameters);
      }
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
