/*
 * $Id$
 *
 * Copyright 2003-2006 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client.async;

import java.lang.reflect.Method;
import org.xins.client.AbstractCAPI;
import org.xins.client.AbstractCAPICallRequest;
import org.xins.client.AbstractCAPICallResult;
import org.xins.common.service.CallException;

/**
 * Class used to call an API in a separate thread.
 * To call the API, you will need to invoke the {@link #start()} method.
 * If you want to wait for the result at a certain point in your program, 
 * invoke the {@link #join()} method.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 *
 * @since XINS 1.4.0
 */
public class CallCAPIThread extends Thread {

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Calls a CAPI function on a separate thread.
    *
    * @param capi
    *    the CAPI to use to call the function.
    *
    * @param function
    *    the name of the function to call.
    *
    * @param request
    *    the input parameters for this call.
    */
   CallCAPIThread(AbstractCAPI capi, String function, AbstractCAPICallRequest request) {
      _capi = capi;
      _function = function;
      _request = request;
   }

   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The CAPI.
    */
   private AbstractCAPI _capi;

   /**
    * The name of the function to call.
    */
   private String _function;

   /**
    * The request of the function.
    */
   private AbstractCAPICallRequest _request;

   /**
    * The duration of the call.
    */
   private long _duration = -1L;

   /**
    * The successful result returned by the function.
    */
   private AbstractCAPICallResult _result;

   /**
    * The exception thrown by the call.
    */
   private Exception _exception;

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public void run() {
      long startTime = System.currentTimeMillis();
      try {
         // Execute the function
         String functionName = "call" + _function;
         Class[] callArgumentsClass = {AbstractCAPICallRequest.class};
         Object[] callArguments = {_request};
         Method callMethod = _capi.getClass().getMethod(functionName, callArgumentsClass);
         _result = (AbstractCAPICallResult) callMethod.invoke(_capi, callArguments);

         // Get the result of the call and notify the listeners
         _duration = _result.duration();
      } catch (Exception ex) {

         _exception = ex;
         // Get the exception thrown by the call and notify the listeners
         if (ex instanceof CallException) {
            _duration = ((CallException) ex).getDuration();
         } else {
            _duration = System.currentTimeMillis() - startTime;
         }
      }
   }

   /**
    * Gets the CAPI used to call the function.
    *
    * @return
    *    the CAPI used to call the function.
    */
   public AbstractCAPI getCAPI() {
      return _capi;
   }

   /**
    * Gets the request used to the call the function.
    *
    * @return
    *    the request used to the call the function.
    */
   public AbstractCAPICallRequest getRequest() {
      return _request;
   }

   /**
    * Gets the name of the function called.
    *
    * @return
    *    the name of the function called.
    */
   public String getFunctionName() {
      return _function;
   }

   /**
    * Gets the result returned by the function. You may want then to cast the
    * {@link org.xins.client.AbstractCAPICallResult AbstractCAPICallResult}
    * to the generated result file normally returned by the CAPI call.
    *
    * @return
    *    the successful result returned by the function.
    */
   public AbstractCAPICallResult getResult() {
      return _result;
   }

   /**
    * Gets the exception thrown by the CAPI call.
    *
    * @return
    *    the exception, most probably a sub class of the 
    *    {@link org.xins.common.service.CallException CallException}.
    */
   public Exception getException() {
      return _exception;
   }   

   /**
    * Gets the time it took to call the function.
    *
    * @return
    *    the duration of the call in milliseconds.
    */
   public long getDuration() {
      return _duration;
   }
}
