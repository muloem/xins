/*
 * $Id$
 */
package org.xins.common.service.http;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.net.ConnectException;

import java.util.Iterator;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpRecoverableException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import org.xins.common.Log;
import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.TimeOutException;

import org.xins.common.collections.PropertyReader;
import org.xins.common.collections.PropertyReaderUtils;

import org.xins.common.net.URLEncoding;

import org.xins.common.service.CallException;
import org.xins.common.service.CallRequest;
import org.xins.common.service.CallResult;
import org.xins.common.service.ConnectionRefusedCallException;
import org.xins.common.service.ConnectionTimeOutCallException;
import org.xins.common.service.Descriptor;
import org.xins.common.service.GenericCallException;
import org.xins.common.service.IOCallException;
import org.xins.common.service.ServiceCaller;
import org.xins.common.service.SocketTimeOutCallException;
import org.xins.common.service.TargetDescriptor;
import org.xins.common.service.TotalTimeOutCallException;
import org.xins.common.service.UnexpectedExceptionCallException;

import org.xins.common.text.FastStringBuffer;

/**
 * HTTP service caller. This class can be used to perform a call to an HTTP
 * server and fail-over to other HTTP servers if the first one fails.
 *
 * <p>The following example code snippet constructs an
 * <code>HTTPServiceCaller</code> instance:
 *
 * <blockquote><code>{@link Descriptor Descriptor} descriptor = {@link org.xins.common.service.DescriptorBuilder DescriptorBuilder}.{@link org.xins.common.service.DescriptorBuilder#build(PropertyReader,String) build}(properties, PROPERTY_NAME);
 * <br />HTTPServiceCaller caller = new {@link #HTTPServiceCaller(Descriptor,HTTPServiceCaller.Method) HTTPServiceCaller}(descriptor, HTTPServiceCaller.{@link #GET GET});</code></blockquote>
 *
 * <p>A call can be executed as follows, using this <code>HTTPServiceCaller</code>:
 *
 * <blockquote><code>{@link org.xins.common.collections.PropertyReader PropertyReader} params = new {@link org.xins.common.collections.BasicPropertyReader BasicPropertyReader}();
 * <br />params.set("street",      "Broadband Avenue");
 * <br />params.set("houseNumber", "12");
 * <br />{@link HTTPServiceCaller.Result HTTPServiceCaller.Result} result = caller.{@link #call(PropertyReader) call}(params);</code></blockquote>
 *
 * <p>TODO: Fix the example code for XINS 0.207.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 0.115
 */
public final class HTTPServiceCaller extends ServiceCaller {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   // TODO: Decide whether Method should be an inner class in this class or not
   // TODO: Decide whether these constants Method should be in this class or elsewhere

   /**
    * Constant representing the HTTP GET method.
    */
   public static final Method GET = new Method("GET");

   /**
    * Constant representing the HTTP POST method.
    */
   public static final Method POST = new Method("POST");


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Creates an appropriate <code>HttpMethod</code> object for the specified
    * URL.
    *
    * @param url
    *    the URL for which to create an {@link HttpMethod} object, should not
    *    be <code>null</code>.
    *
    * @param request
    *    the HTTP call request, should not be <code>null</code>.
    *    be sent.
    *
    * @return
    *    the constructed {@link HttpMethod} object, never <code>null</code>.
    *
    * @throws NullPointerException
    *    if <code>request == null</code>.
    */
   private static HttpMethod createMethod(String          url,
                                          HTTPCallRequest request) {

      Method method = request.getMethod();
      PropertyReader parameters = request.getParameters();

      // HTTP POST request
      if (method == POST) {
         PostMethod postMethod = new PostMethod(url);

         // Loop through the parameters
         Iterator keys = parameters.getNames();
         while (keys.hasNext()) {

            // Get the parameter key
            String key = (String) keys.next();

            // Get the value
            Object value = parameters.get(key);

            // Add this parameter key/value combination
            if (key != null && value != null) {
               postMethod.addParameter(key, value.toString());
            }
         }
         return postMethod;

      // HTTP GET request
      } else if (method == GET) {
         GetMethod getMethod = new GetMethod(url);

         // Loop through the parameters
         FastStringBuffer query = new FastStringBuffer(255);
         Iterator keys = parameters.getNames();
         while (keys.hasNext()) {

            // Get the parameter key
            String key = (String) keys.next();

            // Get the value
            Object value = parameters.get(key);

            // Add this parameter key/value combination
            if (key != null && value != null) {

               if (query.getLength() > 0) {
                  query.append(",");
               }
               query.append(URLEncoding.encode(key));
               query.append("=");
               query.append(URLEncoding.encode(value.toString()));
            }
         }
         if (query.getLength() > 0) {
            getMethod.setQueryString(query.toString());
         }
         return getMethod;

      // Unrecognized HTTP method (only GET and POST are supported)
      } else {
         throw new Error("Unrecognized method \"" + method + "\".");
      }
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>HTTPServiceCaller</code> object.
    *
    * @param descriptor
    *    the descriptor of the service, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>descriptor == null</code>.
    *
    * @since XINS 0.207
    */
   public HTTPServiceCaller(Descriptor descriptor)
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
                               CallRequest      request)
   throws Throwable {

      // Delegate to method with more specialized interface
      return call(target, (HTTPCallRequest) request);
   }

   /**
    * Performs the specified request towards the HTTP service. If the call
    * succeeds with one of the targets, then a {@link Result} object is
    * returned, that combines the HTTP status code and the data returned.
    * Otherwise, if none of the targets could successfully be called, a
    * {@link CallException} is thrown.
    *
    * @param request
    *    the call request, not <code>null</code>.
    *
    * @return
    *    the result of the call, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>request == null</code>.
    *
    * @throws GenericCallException
    *    if the (first) call attempt failed due to a generic reason.
    */
   public Result call(HTTPCallRequest request)
   throws GenericCallException, HTTPCallException {

      CallResult callResult;
      try {
         callResult = doCall(request);

      // Allow GenericCallException, HTTPCallException and Error to proceed,
      // but block other kinds of exceptions and throw an Error instead.
      } catch (GenericCallException exception) {
         throw exception;
      } catch (HTTPCallException exception) {
         throw exception;
      } catch (Exception exception) {
         throw new Error(getClass().getName() + ".doCall(" + request.getClass().getName() + ") threw " + exception.getClass().getName() + '.');
      }

      return (Result) callResult.getResult();
   }

   /**
    * Executes the specified HTTP call request on the specified target. If the
    * call fails in any way, then a {@link CallException} is thrown.
    *
    * @param target
    *    the service target on which to execute the request, cannot be
    *    <code>null</code>.
    *
    * @param request
    *    the call request to execute, cannot be <code>null</code>.
    *
    * @return
    *    the call result, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>target == null || request == null</code>.
    *
    * @throws GenericCallException
    *    if the call to the specified target failed due to a generic reason.
    *
    * @throws HTTPCallException
    *    if the call to the specified target failed due to an HTTP-specific
    *    error.
    *
    * @since XINS 0.207
    */
   public Result call(TargetDescriptor target,
                      HTTPCallRequest  request)
   throws IllegalArgumentException,
          GenericCallException,
          HTTPCallException {

      // TODO: Review log message 3304. Perhaps remove it.

      // NOTE: Preconditions are checked by the CallExecutor constructor
      // Prepare a thread for execution of the call
      CallExecutor executor = new CallExecutor(target, request);

      // TODO: Log that we are about to make an HTTP call

      // Perform the HTTP call
      boolean succeeded = false;
      long start = System.currentTimeMillis();
      long duration;
      try {
         controlTimeOut(executor, target);
         succeeded = true;

      // Total time-out exceeded
      } catch (TimeOutException exception) {
         duration = System.currentTimeMillis() - start;
         // TODO: Log the total time-out (2015 ?)
         throw new TotalTimeOutCallException(request, target, duration);

      } finally {

         // Determine the call duration
         duration = System.currentTimeMillis() - start;
      }

      // Check for exceptions
      Throwable exception = executor.getException();
      if (exception != null) {

         // Connection refusal
         if (exception instanceof ConnectException) {
            // TODO: Log connection refusal (2012 ?)
            throw new ConnectionRefusedCallException(request, target, duration);

         // Connection time-out
         } else if (exception instanceof HttpConnection.ConnectionTimeoutException) {
            // TODO: Log connection time-out (2013 ?)
            throw new ConnectionTimeOutCallException(request, target, duration);

         // Socket time-out
         } else if (exception instanceof HttpRecoverableException) {

            // XXX: This is an ugly way to detect a socket time-out, but there
            //      does not seem to be a better way in HttpClient 2.0. This
            //      will, however, be fixed in HttpClient 3.0. See:
            //      http://issues.apache.org/bugzilla/show_bug.cgi?id=19868

            String exMessage = exception.getMessage();
            if (exMessage != null && exMessage.startsWith("java.net.SocketTimeoutException")) {
               // TODO: Log socket time-out (2014 ?)
               throw new SocketTimeOutCallException(request, target, duration);

            // Unspecific I/O error
            } else {
               // TODO: Log unspecific I/O error (2017 ?)
               throw new IOCallException(request, target, duration, (IOException) exception);
            }

         // Unspecific I/O error
         } else if (exception instanceof IOException) {
            // TODO: Log unspecific I/O error (2017 ?)
            throw new IOCallException(request, target, duration, (IOException) exception);

         // Unrecognized kind of exception caught
         } else {
            // TODO: Log unrecognized exception error (2018 ?)
            throw new UnexpectedExceptionCallException(request, target, duration, null, exception);
         }
      }

      // TODO: Log (2016 ?)

      // Grab the result from the HTTP call
      Result result = executor.getResult();

      // Check the status code, if necessary
      HTTPStatusCodeVerifier verifier = request.getStatusCodeVerifier();
      if (verifier != null) {

         int code = result.getStatusCode();

         if (! verifier.isAcceptable(code)) {
            // TODO: Pass down body as well. Perhaps just pass down complete
            //       Result object and add getter for the body to the
            //       StatusCodeHTTPCallException class.
            throw new StatusCodeHTTPCallException(request, target, duration, code);
         }
      }

      return result;
   }


   //-------------------------------------------------------------------------
   // Inner classes
   //-------------------------------------------------------------------------

   /**
    * Result returned from an HTTP request.
    *
    * @version $Revision$ $Date$
    * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
    *
    * @since XINS 0.115
    */
   public static final class Result extends Object {

      //----------------------------------------------------------------------
      // Constructor
      //----------------------------------------------------------------------

      /**
       * Constructs a new <code>Result</code> object.
       *
       * @param code
       *    the HTTP return code, must be &gt;= 0.
       *
       * @param data
       *    the retrieved data, not <code>null</code>.
       *
       * @throws IllegalArgumentException
       *    if <code>code &lt; 0 || data == null</code>.
       */
      public Result(int code, byte[] data)
      throws IllegalArgumentException {

         // Check preconditions
         MandatoryArgumentChecker.check("data", data);
         if (code < 0) {
            throw new IllegalArgumentException("code (" + code + ") < 0");
         }

         // Just store the arguments in fields
         _code = code;
         _data = data;
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      /**
       * The HTTP return code.
       */
      private final int _code;

      /**
       * The data returned.
       */
      private final byte[] _data;


      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------

      /**
       * Returns the HTTP status code.
       *
       * @return
       *    the HTTP status code.
       *
       * @since XINS 0.207
       */
      public int getStatusCode() {
         return _code;
      }

      /**
       * Returns the result data as a byte array. Note that this is not a copy
       * or clone of the internal data structure, but it is a link to the
       * actual data structure itself.
       *
       * @return
       *    a byte array of the result data, not <code>null</code>.
       */
      public byte[] getData() {
         return _data;
      }

      /**
       * Returns the returned data as a <code>String</code>. The encoding
       * <code>US-ASCII</code> is assumed.
       *
       * @return
       *    the result data as a text string, not <code>null</code>.
       */
      public String getString() {
         final String ENCODING = "US-ASCII";
         try {
            return getString(ENCODING);
         } catch (UnsupportedEncodingException exception) {
            throw new Error("Encoding \"" + ENCODING + "\" is unsupported.");
         }
      }

      /**
       * Returns the returned data as a <code>String</code> in the specified
       * encoding.
       *
       * @param encoding
       *    the encoding to use in the conversion from bytes to a text string,
       *    not <code>null</code>.
       *
       * @return
       *    the result data as a text string, not <code>null</code>.
       *
       * @throws UnsupportedEncodingException
       *    if the specified encoding is not supported.
       */
      public String getString(String encoding)
      throws UnsupportedEncodingException {
         byte[] bytes = getData();
         return new String(bytes, encoding);
      }

      /**
       * Returns the returned data as an <code>InputStream</code>. The input
       * stream is based directly on the underlying byte array.
       *
       * @return
       *    an {@link InputStream} that returns the returned data, never
       *    <code>null</code>.
       *
       * @since XINS 0.194
       */
      public InputStream getStream() {
         return new ByteArrayInputStream(_data);
      }
   }

   /**
    * HTTP method. Possible values for variable of this class:
    *
    * <ul>
    *    <li>{@link #GET}
    *    <li>{@link #POST}
    * </ul>
    *
    * @version $Revision$ $Date$
    * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
    *
    * @since XINS 0.115
    */
   public static final class Method extends Object {

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      /**
       * Constructs a new <code>Method</code> object with the specified name.
       *
       * @param name
       *    the name of the method, for example <code>"GET"</code> or
       *    <code>"POST"</code>; should not be <code>null</code>.
       */
      Method(String name) {
         _name = name;
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      /**
       * The name of this method. For example <code>"GET"</code> or
       * <code>"POST"</code>. This field should never be <code>null</code>.
       */
      private final String _name;


      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------

      /**
       * Returns a textual representation of this object. The implementation
       * of this method returns the name of this HTTP method, like
       * <code>"GET"</code> or <code>"POST"</code>.
       *
       * @return
       *    the name of this method, e.g. <code>"GET"</code> or
       *    <code>"POST"</code>; never <code>null</code>.
       */
      public String toString() {
         return _name;
      }
   }

   /**
    * Executor of calls to an API.
    *
    * @version $Revision$ $Date$
    * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
    *
    * @since XINS 0.207
    */
   private static final class CallExecutor extends Thread {

      //-------------------------------------------------------------------------
      // Class fields
      //-------------------------------------------------------------------------

      /**
       * The number of constructed call executors.
       */
      private static int CALL_EXECUTOR_COUNT;


      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      /**
       * Constructs a new <code>CallExecutor</code> for the specified call to
       * a XINS API.
       *
       * @param target
       *    the service target on which to execute the request, cannot be
       *    <code>null</code>.
       *
       * @param request
       *    the call request to execute, cannot be <code>null</code>.
       *
       * @throws IllegalArgumentException
       *    if <code>target == null || request == null</code>.
       */
      private CallExecutor(TargetDescriptor target,
                           HTTPCallRequest  request)
      throws IllegalArgumentException {

         // Create textual representation of this object
         _asString = "HTTP call executor #" + (++CALL_EXECUTOR_COUNT);

         // Check preconditions
         MandatoryArgumentChecker.check("target", target, "request", request);

         // Store target and request for later use in the run() method
         _target  = target;
         _request = request;
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      /**
       * Textual representation of this object. Never <code>null</code>.
       */
      private final String _asString;

      /**
       * The service target on which to execute the request. Never
       * <code>null</code>.
       */
      private final TargetDescriptor _target;

      /**
       * The call request to execute. Never <code>null</code>.
       */
      private final HTTPCallRequest _request;

      /**
       * The exception caught while executing the call. If there was no
       * exception, then this field is <code>null</code>.
       */
      private Throwable _exception;

      /**
       * The result from the call. The value of this field is
       * <code>null</code> if the call was unsuccessful or if it was not
       * executed yet..
       */
      private Result _result;


      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------

      /**
       * Runs this thread. It will call the XINS API. If that call was
       * successful, then the status code is stored in this object. Otherwise
       * there is an exception. In that case the exception is stored in this
       * object.
       */
      public void run() {

         // TODO: Check if this request was already executed, since this is a
         //       stateful object. If not, mark it as executing within a
         //       synchronized section, so it may no 2 threads may execute
         //       this request at the same time.

         // NOTE: Performance could be improved by using local variables for
         //       _target and _request

         // Get the input parameters
         PropertyReader params = _request.getParameters();

         // TODO: Uncomment or remove the following line:
         // LogdocSerializable serParams = PropertyReaderUtils.serialize(params, "-");

         // Construct new HttpClient object
         HttpClient client = new HttpClient();

         // Determine URL and time-outs
         String url               = _target.getURL();
         int    totalTimeOut      = _target.getTotalTimeOut();
         int    connectionTimeOut = _target.getConnectionTimeOut();
         int    socketTimeOut     = _target.getSocketTimeOut();

         // Configure connection time-out and socket time-out
         client.setConnectionTimeout(connectionTimeOut);
         client.setTimeout          (socketTimeOut);

         // Construct the method object
         HttpMethod method = createMethod(url, _request);

         // Log that we are about to make the HTTP call
         // TODO: Uncomment or remove the following line:
         // Log.log_2011(url, functionName, serParams, totalTimeOut, connectionTimeOut, socketTimeOut);

         // Perform the HTTP call
         try {
            int    statusCode = client.executeMethod(method);
            byte[] body       = method.getResponseBody();

            // Store the result immediately
            _result = new Result(statusCode, body);

         // If an exception is thrown, store it for processing at later stage
         } catch (Throwable exception) {
            _exception = exception;
         }

         // Release the HTTP connection immediately
         try {
            method.releaseConnection();
         } catch (Throwable exception) {
            // TODO: Log
         }

         // TODO: Mark this CallExecutor object as executed, so it may not be
         //       run again
      }

      /**
       * Gets the exception if any generated when calling the method.
       *
       * @return
       *    the invocation exception or <code>null</code> if the call
       *    performed successfully.
       */
      public Throwable getException() {
         return _exception;
      }

      /**
       * Returns the result if the call was successful. If the call was
       * unsuccessful, then <code>null</code> is returned.
       *
       * @return
       *    the result from the call, or <code>null</code> if it was
       *    unsuccessful.
       */
      public Result getResult() {
         return _result;
      }
   }
}
