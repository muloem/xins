/*
 * $Id$
 */
package org.xins.client;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpRecoverableException;
import org.apache.commons.httpclient.methods.PostMethod;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.TimeOutException;

import org.xins.common.collections.PropertyReader;
import org.xins.common.collections.PropertyReaderUtils;

import org.xins.common.service.CallRequest;
import org.xins.common.service.Descriptor;
import org.xins.common.service.GenericCallException;
import org.xins.common.service.HTTPCallException;
import org.xins.common.service.ServiceCaller;
import org.xins.common.service.TargetDescriptor;

import org.xins.common.service.http.HTTPCallException;

import org.xins.common.text.ParseException;

import org.xins.logdoc.LogdocSerializable;

/**
 * XINS service caller.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 0.146
 */
public final class XINSServiceCaller extends ServiceCaller {

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

/*
      method.addParameter("_function", functionName);

      // If a diagnostic context is available, pass it on
      String contextID = NDC.peek();
      if (contextID != null && contextID.length() > 0) {
         method.addParameter("_context", contextID);
      }

      // If there are parameters, then process them
      int paramCount = (parameters == null) ? 0 : parameters.size();
      if (paramCount > 0) {

         // Loop through them all
         Iterator names = parameters.getNames();
         for (int i = 0; i < paramCount; i++) {

            // Get the parameter key
            String key = (String) names.next();

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
                     method.addParameter(key, valueString);
                  }
               }
            }
         }
      }
*/


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>XINSServiceCaller</code> with the specified
    * descriptor.
    *
    * @param descriptor
    *    the descriptor of the service, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>descriptor == null</code>.
    */
   public XINSServiceCaller(Descriptor descriptor)
   throws IllegalArgumentException {
      super(descriptor);

      _parser     = new XINSCallResultParser();
      _httpCaller = new HTTPServiceCaller();
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The result parser. This field cannot be <code>null</code>.
    */
   private final XINSCallResultParser _parser;

   /**
    * An HTTP service caller instance. This is used to actually perform the
    * request towards a XINS API using HTTP. This field cannot be
    * <code>null</code>.
    */
   private final HTTPServiceCaller _httpCaller;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   protected Object doCallImpl(TargetDescriptor target,
                               CallRequest      request)
   throws Throwable {

      // Delegate to method with more specialized interface
      return call(target, (XINSCallRequest) request);
   }

   /**
    * Performs the specified request towards the XINS service. If the call
    * succeeds with one of the targets, then a {@link XINSCallResult} object
    * is returned. Otherwise, if none of the targets could successfully be
    * called, a {@link XINSCallException} is thrown.
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
    *    if the first call attempt failed due to a generic reason and all the
    *    other call attempts failed as well.
    *
    * @throws HTTPCallException
    *    if the first call attempt failed due to an HTTP-related reason and
    *    all the other call attempts failed as well.
    *
    * @throws XINSCallException
    *    if the first call attempt failed due to a XINS-related reason and
    *    all the other call attempts failed as well.
    */
   public XINSCallResult call(XINSCallRequest request)
   throws GenericCallException, HTTPCallException, XINSCallException {

      CallResult callResult;
      try {
         callResult = doCall(request);

      // Allow GenericCallException, HTTPCallException, XINSCallException and
      // Error to proceed, but block other kinds of exceptions and throw an
      // Error instead.
      } catch (GenericCallException exception) {
         throw exception;
      } catch (HTTPCallException exception) {
         throw exception;
      } catch (XINSCallException exception) {
         throw exception;
      } catch (Exception exception) {
         throw new Error(getClass().getName() + ".doCall(" + request.getClass().getName() + ") threw " + exception.getClass().getName() + '.');
      }

      return (Result) callResult.getResult();
   }

   /**
    * Executes the specified call request on the specified XINS API. If the
    * call fails in any way or if the result is unsuccessful, then a
    * {@link XINSCallException} is thrown.
    *
    * @param target
    *    the service target on which to execute the request, cannot be
    *    <code>null</code>.
    *
    * @param request
    *    the call request to execute, cannot be <code>null</code>.
    *
    * @return
    *    the call result, never <code>null</code> and always successful.
    *
    * @throws IllegalArgumentException
    *    if <code>target == null || request == null</code>.
    *
    * @throws XINSCallException
    *    if the call to the specified target failed.
    *
    * @since XINS 0.198
    */
   public XINSCallResult call(TargetDescriptor target,
                              XINSCallRequest  request)
   throws IllegalArgumentException,
          GenericCallException,
          HTTPCallException,
          XINSCallException {

      // Check preconditions
      MandatoryArgumentChecker.check("target", target, "request", request);

      // Log that we are about to call the API
      // TODO: Either uncomment or remove the following line
      // Log.log_2011(url, functionName, serParams, totalTimeOut, connectionTimeOut, socketTimeOut);

      // Delegate the actual HTTP call to the HTTPServiceCaller. This may
      // cause a CallException
      _httpCaller.call(target, request.getHTTPCallRequest());

      // Read response body (mandatory operation)
      byte[] xml = method.getResponseBody();

      // Check for exceptions
      Throwable exception = executor.getException();
      if (exception != null) {

         // Connection refusal
         if (exception instanceof ConnectException) {
            Log.log_2012(duration, url, functionName, serParams);
            throw new ConnectionRefusedException(request, target, duration);

         // Connection time-out
         } else if (exception instanceof HttpConnection.ConnectionTimeoutException) {
            Log.log_2013(duration, url, functionName, serParams, connectionTimeOut);
            throw new ConnectionTimeOutException(request, target, duration);

         // Socket time-out
         } else if (exception instanceof HttpRecoverableException) {

            // XXX: This is an ugly way to detect a socket time-out, but there
            //      does not seem to be a better way in HttpClient 2.0. This
            //      will, however, be fixed in HttpClient 3.0. See:
            //      http://issues.apache.org/bugzilla/show_bug.cgi?id=19868

            String exMessage = exception.getMessage();
            if (exMessage != null && exMessage.startsWith("java.net.SocketTimeoutException")) {
               Log.log_2014(duration, url, functionName, serParams, socketTimeOut);
               throw new SocketTimeOutException(request, target, duration);

            // Unspecific I/O error
            } else {
               Log.log_2017(exception, duration, url, functionName, serParams);
               throw new CallIOException(request, target, duration, (IOException) exception);
            }

         // Unspecific I/O error
         } else if (exception instanceof IOException) {
            Log.log_2017(exception, duration, url, functionName, serParams);
            throw new CallIOException(request, target, duration, (IOException) exception);

         /* TODO: (1/2) add the lines below
         } else {
            Log.log_2018(exception, duration, url, functionName, serParams);
            throw new UnexpectedExceptionException(request, target, duration, exception);
         }

         // TODO: (2/2) remove the lines below
         */

         } else if (exception instanceof RuntimeException) {
            Log.log_2018(exception, duration, url, functionName, serParams);
            throw (RuntimeException) exception;

         } else if (exception instanceof Error) {
            Log.log_2018(exception, duration, url, functionName, serParams);
            throw (Error) exception;
         }

         // Unknown kind of exception caught
         throw new Error(exception);
      }

      // Check the code
      int code = executor.getStatusCode();

      Log.log_2016(System.currentTimeMillis() - start, url, functionName, serParams, code);

      // If HTTP status code is not in 2xx range, abort
      if (code < 200 || code > 299) {
         Log.log_2008(url, functionName, serParams, code);
         throw new UnexpectedHTTPStatusCodeException(request, target, duration, code);
      }

      // If the stream is null, then there was an error
      if (xml == null) {
         Log.log_2009(duration, url, functionName, serParams);
         throw new InvalidCallResultException(request, target, duration, "Failed to read the response body.", null);
      }

      // Parse the result
      XINSCallResult result;
      try {
         result = _parser.parse(request, target, duration, xml);
      } catch (ParseException parseException) {
         throw new InvalidCallResultException(request, target, duration, "Failed to parse result.", parseException);
      }

      // On failure, throw UnsuccessfulXINSCallException, otherwise return result
      if (result.getErrorCode() != null) {
         throw new UnsuccessfulXINSCallException(result);
      } else {
         return result;
      }
   }

   /**
    * Executes the specified request. If possible, multiple targets will be
    * called if a target fails and fail-over is considered allowable.
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
    * @throws XINSCallException
    *    if the call failed.
    *
    * @since XINS 0.198
    */
   public XINSCallResult execute(CallRequest request)
   throws IllegalArgumentException, XINSCallException {

      // Check preconditions
      MandatoryArgumentChecker.check("request", request);

      try {
         return (Result) doCall(request).getResult();

      // Link the XINSCallException instances together and throw the first one
      } catch (CallFailedException cfe) {

         // Retrieve all XINSCallExceptions
         List exceptions = cfe.getExceptions();

         // Remember the first one, since this one should be thrown
         XINSCallException first = (CallException) exceptions.get(0);

         // Loop through all following XINSCallExceptions to link them to the
         // previous one
         int count = exceptions.size();
         XINSCallException previous = first;
         for (int i = 1; i < count; i++) {
            XINSCallException current = (CallException) exceptions.get(i);
            previous.setNext(current);
            previous = current;
         }

         // Throw the first XINSCallException
         throw first;
      }
   }

   /**
    * Determines whether a call should fail-over to the next selected target.
    *
    * @param request
    *    the request for the call, as passed to {@link #doCall(CallRequest)},
    *    should not be <code>null</code>.
    *
    * @param exception
    *    the exception caught while calling the most recently called target,
    *    should not be <code>null</code>.
    *
    * @return
    *    <code>true</code> if the call should fail-over to the next target, or
    *    <code>false</code> if it should not.
    *
    * @since XINS 0.207
    */
   protected boolean shouldFailOver(CallRequest request,
                                    Throwable   exception) {

      // First let the superclass do it's job
      if (super.shouldFailOver(request, exception)) {
         return true;
      }

      // The request must be a XINS call request
      XINSCallRequest xinsRequest = (XINSCallRequest) request;

      // If fail-over is allowed even if request is already sent, then
      // short-circuit and allow fail-over
      //
      // XXX: Note that fail-over will even be allowed if there was an
      //      internal error that does not have anything to do with the
      //      service being called, e.g. an OutOfMemoryError or an
      //      InterruptedException. This could be improved by checking the
      //      type of exception and only allowingt fail-over if the exception
      //      indicates an I/O error.
      if (request.isFailOverAllowed()) {
         return true;
      }

      // Get the HTTP request underlying the XINS request
      HTTPCallRequest httpRequest = request.getHTTPCallRequest();

      // Check if the request may fail-over from HTTP point-of-view
      //
      // XXX: Note that this will again call ServiceCaller.shouldFailOver,
      //      like done above.
      if (_httpCaller.shouldFailOver(httpRequest)) {
         return true;

      // Some XINS error codes indicate the request was not accepted
      } else if (exception instanceof UnsuccessfulCallException) {
         String code = ((UnsuccessfulCallException) exception).getErrorCode();
         return ("_InvalidRequest".equals(code)
              || "_DisabledFunction".equals(code));

      // Otherwise do not fail over
      } else {
         return false;
      }
   }


   //-------------------------------------------------------------------------
   // Inner classes
   //-------------------------------------------------------------------------

   // TODO: In the executing thread:

         // Execute the call to the XINS API
         try {
            _statusCode = _httpClient.executeMethod(_call);

         // If an exception is thrown, store it for processing at later stage
         } catch (Throwable exception) {
            _exception = exception;
         }

         _call.releaseConnection();
      }

      /**
       * Gets the returned HTTP code of the call.
       *
       * @return
       *    the returned HTTP status code, or -1 if the call has not be performed.
       */
      public int getStatusCode() {
         return _statusCode;
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
   }
}
