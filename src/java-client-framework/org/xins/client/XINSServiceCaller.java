/*
 * $Id$
 */
package org.xins.client;

import java.util.Iterator;
import java.util.Map;
import java.util.Collections;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.Namespace;
import org.xins.util.MandatoryArgumentChecker;
import org.xins.util.collections.CollectionUtils;
import org.xins.util.collections.PropertyReader;
import org.xins.util.net.URLEncoding;
import org.xins.util.service.CallFailedException;
import org.xins.util.service.CallResult;
import org.xins.util.service.Descriptor;
import org.xins.util.service.ServiceCaller;
import org.xins.util.service.TargetDescriptor;
import org.xins.util.text.FastStringBuffer;

/**
 * XINS service caller.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.146
 */
public final class XINSServiceCaller extends ServiceCaller {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Logger for this class.
    */
   public static final Logger LOG = Logger.getLogger(XINSServiceCaller.class.getName());

   /**
    * Initial buffer size for a parameter string. See
    * {@link #createParameterString(String,String,Map)}.
    */
   private static int PARAMETER_STRING_BUFFER_SIZE = 256;


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Creates a parameter string from a session ID, a function name and a set
    * of parameters.
    *
    * @param sessionID
    *    the session identifier, if any, or <code>null</code>.
    *
    * @param functionName
    *    the name of the function to be called, not <code>null</code>.
    *
    * @param parameters
    *    the parameters to be passed, or <code>null</code>; keys must be
    *    either <code>null</code> or otherwise {@link String} instances;
    *    values can be of any class; if
    *    <code>(key == null
    *        || key.</code>{@link String#length() length()} &lt; 1
    *        || value == null
    *        || value.</code>{@link Object#toString() toString()} == null
    *        || value.</code>{@link Object#toString() toString()}<code>.</code>{@link String#length() length()}<code> &lt; 1)</code>,
    *    then this parameter will not be sent down.
    *
    * @throws IllegalArgumentException
    *    if <code>functionName == null</code>.
    *
    * @return
    *    the string that can be used in an HTTP GET call, never
    *    <code>null</code> nor empty.
    */
   private final String createParameterString(String sessionID,
                                              String functionName,
                                              Map    parameters)
   throws IllegalArgumentException {

      // TODO: Consider using an IndexedMap, for improved iteration
      //       performance

      // Check preconditions
      MandatoryArgumentChecker.check("functionName", functionName);

      // TODO: More checks on the function name? It cannot be an empty string,
      //       for example.

      // Initialize a buffer
      FastStringBuffer buffer = new FastStringBuffer(PARAMETER_STRING_BUFFER_SIZE);
      buffer.append("function=");
      buffer.append(functionName);

      // If there is a session identifier, process it
      if (sessionID != null) {
         buffer.append("&_session=");
         buffer.append(sessionID);
      }

      // If there are parameters, then process them
      int paramCount = (parameters == null) ? 0 : parameters.size();
      if (paramCount > 0) {

         // Loop through them all
         Iterator keys = parameters.keySet().iterator();
         for (int i = 0; i < paramCount; i++) {

            // Get the parameter key
            String key = (String) keys.next();

            // Process key only if it is not null and not an empty string
            if (key != null && key.length() > 0) {

               // TODO: Improve checks to make sure the key is properly
               //       formatted, otherwise throw an InvalidKeyException

               // The key cannot start with an underscore
               if (key.charAt(0) == '_') {
                  throw new IllegalArgumentException("The parameter key \"" + key + "\" is invalid, since it cannot start with an underscore.");

               // The key cannot equal 'function'
               } else if ("function".equals(key)) {
                  throw new IllegalArgumentException("The parameter key \"function\" is invalid, since \"function\" is a reserved word.");
               }

               // Get the value
               Object value = parameters.get(key);

               // Add this parameter key/value combination
               if (value != null) {

                  // Convert the value object to a string
                  String valueString = value.toString();

                  // Only add the key/value combo if there is a value string
                  if (valueString != null && valueString.length() > 0) {
                     buffer.append('&');
                     buffer.append(URLEncoding.encode(key));
                     buffer.append('=');
                     buffer.append(URLEncoding.encode(valueString));
                  }
               }
            }
         }
      }

      return buffer.toString();
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>XINSServiceCaller</code> object.
    *
    * @param descriptor
    *    the descriptor of the service, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>descriptor == null || method == null</code>.
    */
   public XINSServiceCaller(Descriptor descriptor)
   throws IllegalArgumentException {
      super(descriptor);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   protected Object doCallImpl(TargetDescriptor target,
                               Object           subject)
   throws Throwable {

      // Convert subject to a CallRequest
      CallRequest request = (CallRequest) subject;

      // Construct new HttpClient and PostMethod objects
      HttpClient client = new HttpClient();
      PostMethod method = new PostMethod(target.getURL());

      // Set the correct time-out
      client.setTimeout(target.getTimeOut());

      boolean succeeded = false;
      byte[] data;
      int    code;

      try {
         // Execute the request
         client.executeMethod(method);

         // Read response body (mandatory operation) and determine status
         data = method.getResponseBody();
         code = method.getStatusCode();

         succeeded = true;
      } finally {

         // Release the connection
         if (succeeded) {
            method.releaseConnection();
         } else {
            try {
               method.releaseConnection();
            } catch (Throwable exception) {
               LOG.error("Caught " + exception.getClass().getName() + " while releasing HTTP connection after request failed. Ignoring this exception so the original exception is not hidden.", exception);
            }
         }
      }

      return new Result(target,
                        true,  // TODO:          success,
                        null,  // TODO:          code,
                        null,  // TODO:          parameters,
                        null); // TODO:          dataElement)
   }

   /**
    * Calls the specified session-less API function with the specified
    * parameters.
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
    * @throws CallFailedException
    *    if the call failed.
    */
   public Result call(String functionName,
                      Map    parameters)
   throws IllegalArgumentException,
          CallFailedException {
      return call(new CallRequest(null, functionName, parameters));
   }

   /**
    * Calls the specified API function with the specified parameters.
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
    * @throws CallFailedException
    *    if the call failed.
    */
   public Result call(String sessionID,
                      String functionName,
                      Map    parameters)
   throws IllegalArgumentException,
          CallFailedException {
      return call(new CallRequest(sessionID, functionName, parameters));
   }

   /**
    * Executes the specified request.
    *
    * @param request
    *    the request to execute, cannot be <code>null</code>.
    *
    * @return
    *    the call result, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>request == null</code>.
    *
    * @throws CallFailedException
    *    if the call failed.
    */
   public Result call(CallRequest request)
   throws IllegalArgumentException,
          CallFailedException {

      // Check preconditions
      MandatoryArgumentChecker.check("request", request);

      // Attempt to perform the call
      CallResult callResult = doCall(request);

      // On success, return the result
      return (Result) callResult.getResult();
   }


   //-------------------------------------------------------------------------
   // Inner classes
   //-------------------------------------------------------------------------

   /**
    * Result of a call to a XINS API function.
    *
    * @version $Revision$ $Date$
    * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
    *
    * @since XINS 0.146
    */
   public static final class Result extends Object {

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      /**
       * Constructs a new <code>Result</code> object.
       *
       * @param target
       *    the {@link TargetDescriptor} that was used to successfully get the
       *    result, cannot be <code>null</code>.
       *
       * @param success
       *    success indication returned by the function.
       *
       * @param code
       *    the return code, if any, can be <code>null</code>.
       *
       * @param parameters
       *    output parameters returned by the function, or <code>null</code>.
       *
       * @param dataElement
       *    the data element returned by the function, or <code>null</code>; if
       *    specified then the name must be <code>"data"</code>, with no
       *    namespace.
       *
       * @throws IllegalArgumentException
       *    if <code>target == null || (dataElement != null &amp;&amp;
       *             !("data".equals(dataElement.</code>{@link Element#getName() getName()}<code>) &amp;&amp;</code>
       *               {@link Namespace#NO_NAMESPACE}<code>.equals(dataElement.</code>{@link Element#getNamespace() getNamespace()}<code>)))</code>
       */
      public Result(TargetDescriptor target,
                    boolean          success,
                    String           code,
                    Map              parameters,
                    Element          dataElement)
      throws IllegalArgumentException {

         // Clone the data element if there is one
         MandatoryArgumentChecker.check("target", target);
         if (dataElement != null) {
            String    dataElementName = dataElement.getName();
            Namespace ns              = dataElement.getNamespace();
            if (!"data".equals(dataElement.getName())) {
               throw new IllegalArgumentException("dataElement.getName() returned \"" + dataElementName + "\", instead of \"data\".");
            } else if (!Namespace.NO_NAMESPACE.equals(ns)) {
               throw new IllegalArgumentException("dataElement.getNamespace() returned a namespace with URI \"" + ns.getURI() + "\", instead of no namespace.");
            }
            dataElement = (Element) dataElement.clone();
         }

         // Store all the information
         _target      = target;
         _success     = success;
         _code        = code;
         _parameters  = parameters == null
                      ? CollectionUtils.EMPTY_MAP
                      : Collections.unmodifiableMap(parameters);
         _dataElement = dataElement;
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      /**
       * The <code>TargetDescriptor</code> that was used to produced this
       * result. Cannot be <code>null</code>.
       */
      private final TargetDescriptor _target;

      /**
       * Success indication.
       */
      private final boolean _success;

      /**
       * The result code. This field is <code>null</code> if no code was
       * returned.
       */
      private final String _code;

      /**
       * The parameters and their values. This field is never <code>null</code>.
       * If there are no parameters, then this field will be set to
       * {@link CollectionUtils#EMPTY_MAP}.
       */
      private final Map _parameters;

      /**
       * The data element. This field is <code>null</code> if there is no data
       * element.
       */
      private final Element _dataElement;


      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------

      /**
       * Returns the <code>TargetDescriptor</code> that was used to generate
       * this result.
       *
       * @return
       *    the {@link TargetDescriptor}, cannot be <code>null</code>.
       */
      public TargetDescriptor getTarget() {
         return _target;
      }

      /**
       * Returns the success indication.
       *
       * @return
       *    success indication, <code>true</code> or <code>false</code>.
       */
      public boolean isSuccess() {
         return _success;
      }

      /**
       * Returns the result code.
       *
       * @return
       *    the result code or <code>null</code> if no code was returned.
       */
      public String getCode() {
         return _code;
      }

      /**
       * Gets all parameters.
       *
       * @return
       *    a <code>Map</code> containing all parameters, never
       *    <code>null</code>; the keys will be the names of the parameters
       *    ({@link String} objects, cannot be <code>null</code>), the values
       *    will be the parameter values ({@link String} objects as well, cannot
       *    be <code>null</code>).
       */
      public Map getParameters() {
         return _parameters;
      }

      /**
       * Gets the value of the specified parameter.
       *
       * @param name
       *    the parameter element name, not <code>null</code>.
       *
       * @return
       *    string containing the value of the parameter element,
       *    not <code>null</code>.
       *
       * @throws IllegalArgumentException
       *    if <code>name == null</code>.
       */
      public String getParameter(String name)
      throws IllegalArgumentException {

         // Check preconditions
         MandatoryArgumentChecker.check("name", name);

         // Short-circuit if there are no parameters at all
         if (_parameters == null) {
            return null;
         }

         // Otherwise return the parameter value
         return (String) _parameters.get(name);
      }

      /**
       * Returns the optional extra data. The data is an XML {@link Element}, or
       * <code>null</code>.
       *
       * @return
       *    the extra data as an XML {@link Element}, can be <code>null</code>;
       *    if it is not <code>null</code>, then
       *    <code><em>return</em>.{@link Element#getName() getName()}.equals("data") &amp;&amp; <em>return</em>.{@link Element#getNamespace() getNamespace()}.equals({@link Namespace#NO_NAMESPACE NO_NAMESPACE})</code>.
       */
      public Element getDataElement() {

         // If there is no data element, return null
         if (_dataElement == null) {
            return null;

         // Otherwise return a clone of the data element
         } else {
            return (Element) _dataElement.clone();
         }
      }
   }
}
