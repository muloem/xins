/*
 * $Id$
 */
package org.xins.client;

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
import org.xins.util.text.HexConverter;

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
    * The <em>ordered</em> call target group type. The name of this type is:
    * <code>"ordered"</code>.
    */
   public static final Type ORDERED_TYPE = new Type("ordered");

   /**
    * The <em>random</em> call target group type. The name of this type is:
    * <code>"random"</code>.
    */
   public static final Type RANDOM_TYPE = new Type("random");


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Gets the type with the specified name.
    *
    * @param name
    *    the name of the type, cannot be <code>null</code>.
    *
    * @return
    *    the type with the specified name, or <code>null</code> if there is no
    *    type with the specified name.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null</code>.
    *
    * @since XINS 0.45
    */
   public static final Type getTypeByName(String name)
   throws IllegalArgumentException {

      // Recognize existing types
      if (ORDERED_TYPE.getName().equals(name)) {
         return ORDERED_TYPE;
      } else if (RANDOM_TYPE.getName().equals(name)) {
         return RANDOM_TYPE;

      // Fail if name is null
      } else if (name == null) {
         throw new IllegalArgumentException("name == null");

      // Otherwise: not found, return null
      } else {
         return null;
      }
   }

   /**
    * Creates a new <code>CallTargetGroup</code> of the specified type, using
    * the specified URL. The host name in the URL may resolve to multiple IP
    * addresses. Each one will be added as a member.
    *
    * @param type
    *    the type, either {@link #ORDERED_TYPE}, or
    *    {@link #RANDOM_TYPE}, cannot be <code>null</code>.
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

      // TODO: Make sure there are no ActualFunctionCaller instances with duplicate CRC-32 values

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
         throw new Error("Caught MalformedURLException for a protocol that was previously accepted: \"" + url.getProtocol() + "\".");
      } catch (MultipleIPAddressesException miae) {
         throw new Error("Caught MultipleIPAddressesException while only using resolved IP addresses.");
      }

      return create(type, members);
   }

   /**
    * Creates a new <code>CallTargetGroup</code> of the specified type, with
    * the specified members.
    *
    * @param type
    *    the type, either {@link #ORDERED_TYPE}, or {@link #RANDOM_TYPE},
    *    cannot be <code>null</code>.
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
      } else {
         throw new Error("Type not recognized.");
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
      _type                                     = type;
      _actualFunctionCallers                    = new ArrayList();
      _actualFunctionCallersByURL               = new HashMap();
      _actualFunctionCallersByURLChecksum       = new HashMap();
      _actualFunctionCallersByURLChecksumString = new HashMap();

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
    * List of <code>ActualFunctionCaller</code> instances. This {@link List}
    * cannot be <code>null</code>.
    */
   private final List _actualFunctionCallers;

   /**
    * Mappings from URLs to <code>ActualFunctionCaller</code>. The URLs are
    * stored as {@link String} instances. This {@link Map} cannot be
    * <code>null</code>.
    */
   private final Map _actualFunctionCallersByURL;

   /**
    * Mappings from URL checksums to <code>ActualFunctionCaller</code>. The
    * checksums are stored as {@link Long} instances. This {@link Map} cannot be
    * <code>null</code>.
    */
   private final Map _actualFunctionCallersByURLChecksum;

   /**
    * Mappings from URL checksum strings to <code>ActualFunctionCaller</code>.
    * The checksums are stored as {@link String} instances. This {@link Map}
    * cannot be <code>null</code>.
    */
   private final Map _actualFunctionCallersByURLChecksumString;


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

            // Store the ActualFunctionCaller self
            _actualFunctionCallers.add(afc);

            // Store the ActualFunctionCaller by URL
            String url = afc.getURL().toString();
            _actualFunctionCallersByURL.put(url, afc);

            // Store the ActualFunctionCaller by URL checksum
            long checksum = afc.getCRC32();
            Long l = new Long(checksum);
            ActualFunctionCaller afc0 = (ActualFunctionCaller) _actualFunctionCallersByURLChecksum.get(l);
            if (afc0 != null) {
               throw new IllegalArgumentException("List contains two ActualFunctionCaller instances that have the same CRC-32 checksum. URL of first is \"" + afc0.getURL().toString() + "\", URL of second is \"" + afc.getURL().toString() + '.');
            }
            _actualFunctionCallersByURLChecksum.put(l, afc);

            // Store the ActualFunctionCaller by URL checksum string
            _actualFunctionCallersByURLChecksumString.put(HexConverter.toHexString(checksum), afc);

         // If the member is composite, get all its members
         } else if (member instanceof CompositeFunctionCaller) {
            CompositeFunctionCaller cfc = (CompositeFunctionCaller) member;
            addActualFunctionCallers(cfc.getMembers());
         }
      }
   }

   public List getActualFunctionCallers() {
      return Collections.unmodifiableList(_actualFunctionCallers);
   }

   /**
    * Returns the type of this group.
    *
    * @return
    *    the type of this group, either {@link #ORDERED_TYPE}, or
    *     {@link #RANDOM_TYPE}.
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
    *    the actual function caller for the specified URL, or
    *    <code>null</code> if there is no {@link ActualFunctionCaller} for the
    *    specified URL in this group or any of the contained groups (if any).
    *
    * @throws IllegalArgumentException
    *    if <code>url == null</code>.
    */
   public final ActualFunctionCaller getActualFunctionCaller(String url)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("url", url);

      return (ActualFunctionCaller) _actualFunctionCallersByURL.get(url);
   }

   /**
    * Gets the actual function caller for the specified URL checksum.
    *
    * @param checksum
    *    the CRC-32 checksum of the API URL.
    *
    * @return
    *    the actual function caller for the specified checksum, or
    *    <code>null</code> if there is no {@link ActualFunctionCaller} for the
    *    specified URL checksum in this group or any of the contained groups
    *    (if any).
    */
   public final ActualFunctionCaller getActualFunctionCaller(long checksum) {
      return (ActualFunctionCaller) _actualFunctionCallersByURLChecksum.get(new Long(checksum));
   }

   public final ActualFunctionCaller getActualFunctionCallerByCRC32(String crc32)
   throws IllegalArgumentException {
      return (ActualFunctionCaller) _actualFunctionCallersByURLChecksumString.get(crc32);
   }

   public final CallResult call(String sessionID,
                                String functionName,
                                Map    parameters)
   throws IllegalArgumentException,
          CallIOException,
          InvalidCallResultException {

      // Check preconditions
      MandatoryArgumentChecker.check("functionName", functionName);

      // Pass control to callImpl(...) method
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
    *    the name of the function to be called, guaranteed not to be
    *    <code>null</code>.
    *
    * @param parameters
    *    the parameters to be passed to that function, or
    *    <code>null</code>; keys must be {@link String Strings}, values can be
    *    of any class.
    *
    * @return
    *    the call result, never <code>null</code>.
    *
    * @throws CallIOException
    *    if the API could not be contacted due to an I/O error.
    *
    * @throws InvalidCallResultException
    *    if the calling of the function failed or if the result from the
    *    function was invalid.
    */
   abstract CallResult callImpl(String sessionID,
                                String functionName,
                                Map    parameters)
   throws CallIOException, InvalidCallResultException;

   /**
    * Attempts to call the specified <code>FunctionCaller</code>. If the call
    * succeeds, then the {@link CallResult} will be returned. If it fails,
    * then the {@link Throwable} exception will be returned.
    *
    * @param caller
    *    the {@link FunctionCaller} to call, not <code>null</code>.
    *
    * @param sessionID
    *    the session identifier, or <code>null</code>.
    *
    * @param functionName
    *    the name of the function to call, not <code>null</code>.
    *
    * @param parameters
    *    the parameters to be passed to the function, or <code>null</code>;
    *    keys must be {@link String Strings}, values can be of any class.
    *
    * @return
    *    a {@link Throwable} if
    *    <code>caller.</code>{@link FunctionCaller#call(String,String,Map)}
    *    throws an exception, otherwise the return value of that call, but
    *    never <code>null</code>.
    *
    * @throws Error
    *    if <code>caller.</code>{@link FunctionCaller#call(String,String,Map)}
    *    returned <code>null</code>.
    */
   final Object tryCall(FunctionCaller caller,
                        String         sessionID,
                        String         functionName,
                        Map            parameters)
   throws Error {

      // Perform the call
      CallResult result;
      try {
         result = caller.call(sessionID, functionName, parameters);

      // If there was an exception, return it...
      } catch (Throwable exception) {
         return exception;
      }


      // otherwise if the result was null, then throw an error...
      if (result == null) {
         throw new Error(caller.getClass().getName() + ".call(String,String,Map) returned null.");
      }

      // otherwise return the CallResult object
      return result;
   }

   /**
    * Returns the result of <code>tryCall()</code>. This utility method can be
    * called from {@link #callImpl(String,String,Map)} after
    * {@link #tryCall(FunctionCaller,String,String,Map)} has been called as
    * many times as necessary.
    *
    * <p>If the specified object is a <code>CallResult</code> object, then it
    * will be <em>returned</em>. If it is an exception, then that exception
    * will be <em>thrown</em>.
    *
    * @param result
    *    the result from a call to
    *    {@link #tryCall(FunctionCaller,String,String,Map)}, should not be
    *    <code>null</code>.
    *
    * @return
    *    the {@link CallResult}, if <code>result instanceof CallResult</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>result == null</code>.
    *
    * @throws CallIOException
    *    if <code>result instanceof CallIOException</code>.
    *
    * @throws InvalidCallResultException
    *    if <code>result instanceof InvalidCallResultException</code>.
    */
   final CallResult callImplResult(Object result)
   throws IllegalArgumentException,
          CallIOException,
          InvalidCallResultException {

      // Check preconditions
      MandatoryArgumentChecker.check("result", result);

      // Determine behaviour based on result object
      if (result instanceof CallResult) {
         return (CallResult) result;
      } else if (result instanceof CallIOException) {
         throw (CallIOException) result;
      } else if (result instanceof InvalidCallResultException) {
         throw (InvalidCallResultException) result;
      } else if (result instanceof Error) {
         throw (Error) result;
      } else if (result instanceof RuntimeException) {
         throw (RuntimeException) result;
      } else {
         throw new Error("CallTargetGroup.tryCall() returned an instance of class " + result.getClass().getName() + ", which is unsupported.");
      }
   }


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
       *
       * @param name
       *    the name of this type, not <code>null</code>.
       *
       * @throws IllegalArgumentException
       *    if <code>name == null</code>.
       */
      private Type(String name) throws IllegalArgumentException {
         MandatoryArgumentChecker.check("name", name);
         _name = name;
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      /**
       * The name of this type.
       */
      private final String _name;


      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------

      /**
       * Returns the name of this type. The name uniquely identifies this
       * type.
       *
       * @return
       *    the name of this type, not <code>null</code>.
       *
       * @since XINS 0.45
       */
      public String getName() {
         return _name;
      }

      public String toString() {
         return _name;
      }
   } 
}
