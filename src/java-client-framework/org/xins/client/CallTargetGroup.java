/*
 * $Id$
 */
package org.xins.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.net.URL;
import java.util.ArrayList;
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
 *
 * @since XINS 0.41
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
    * Creates a new <code>CallTargetGroup</code> of the specified type, using
    * the specified URL. The host name in the URL may resolve to multiple IP
    * addresses. Each one will be added as a member.
    *
    * @param type
    *    the type, either {@link #ORDERED_TYPE}, {@link #RANDOM_TYPE} or
    *    {@link #ROUND_ROBIN_TYPE}, cannot be <code>null</code>.
    *
    * @param url
    *    the {@link URL} that is used to create {@link FunctionCaller}
    *    members, one per resolved IP address, cannot be <code>null</code>.
    *
    * @return
    *    the <code>CallTargetGroup</code>, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>type == null || url == null</code>.
    *
    * @throws SecurityException
    *    if a security manager does not allow the DNS lookup operation for the
    *    host specified in the URL.
    *
    * @throws UnknownHostException
    *    if no IP address could be found for the host specified in the URL.
    *
    * @since XINS 0.44
    */
   public final static CallTargetGroup create(Type type, URL url)
   throws IllegalArgumentException, SecurityException, UnknownHostException {

      // Check preconditions
      MandatoryArgumentChecker.check("type", type, "url", url);

      List members = new ArrayList();

      String hostName = url.getHost();
      InetAddress[] addresses = InetAddress.getAllByName(hostName);
      int addressCount = addresses.length;
      try {
         for (int i = 0; i < addressCount; i++) {
            URL afcURL = new URL(url.getProtocol(),             // protocol
                                 addresses[i].getHostAddress(), // host
                                 url.getPort(),                 // port
                                 url.getFile());                // file
            members.add(new ActualFunctionCaller(afcURL, hostName));
         }
      } catch (MalformedURLException mue) {
         throw new InternalError("Caught MalformedURLException for a protocol that was previously accepted: \"" + url.getProtocol() + "\".");
      } catch (MultipleIPAddressesException miae) {
         throw new InternalError("Caught MultipleIPAddressesException while only using resolved IP addresses.");
      }

      return create(type, members);
   }

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
         return new RandomCallTargetGroup(members);
      } else if (type == ROUND_ROBIN_TYPE) {
         return new RoundRobinCallTargetGroup(members);
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
    *    if <code>type == null || members == null || !(members.get(<em>i</em>)
    *    instanceof FunctionCaller)</code> (where 0 &lt;= <em>i</em> &lt;
    *    <code>members.size()</code>).
    */
   CallTargetGroup(Type type, List members) throws IllegalArgumentException {

      super(members);

      // Check preconditions
      MandatoryArgumentChecker.check("type", type);

      // Initialize fields
      _type                       = type;
      _actualFunctionCallersByURL = new HashMap();

      addActualFunctionCallers(members);
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
    * Stores all actual function callers by URL that are found in the
    * specified list of function callers.
    *
    * @param members
    *    the list of function callers, can be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>!(members.get(<em>i</em>) instanceof FunctionCaller)</code>
    *    (where 0 &lt;= <em>i</em> &lt; <code>members.size()</code>).
    */
   private final void addActualFunctionCallers(List members)
   throws IllegalArgumentException {

      int memberCount = members == null ? 0 : members.size();
      for (int i = 0; i < memberCount; i++) {
         FunctionCaller member;

         // Get the member and make sure it is a FunctionCaller instance
         try {
            member = (FunctionCaller) members.get(i);
            if (member == null) {
               throw new IllegalArgumentException("members.get(" + i + ") == null");
            }
         } catch (ClassCastException cce) {
            throw new IllegalArgumentException("members.get(" + i + ") is an instance of " + members.get(i).getClass().getName() + '.');
         }

         // If the member is an actual function caller, store a reference
         if (member instanceof ActualFunctionCaller) {
            ActualFunctionCaller afc = (ActualFunctionCaller) member;
            _actualFunctionCallersByURL.put(afc.getURL().toString(), afc);

         // If the member is composite, get all its members
         } else if (member instanceof CompositeFunctionCaller) {
            CompositeFunctionCaller cfc = (CompositeFunctionCaller) member;
            addActualFunctionCallers(cfc.getMembers());
         }
      }
   }

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
