/*
 * $Id$
 */
package org.xins.common.service;

import java.util.ArrayList;
import java.util.Iterator;

import org.xins.common.Log;
import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.TimeOutController;
import org.xins.common.TimeOutException;

/**
 * Abstraction of a service caller for a TCP-based service. Possible
 * implementations include service callers for HTTP, FTP, JDBC, etc.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 0.115
 */
public abstract class ServiceCaller extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The fully-qualified name of this class.
    */
   private static final String CLASSNAME = ServiceCaller.class.getName();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>ServiceCaller</code> object.
    *
    * @param descriptor
    *    the descriptor of the service, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>descriptor == null</code>.
    */
   protected ServiceCaller(Descriptor descriptor)
   throws IllegalArgumentException {

      // TRACE: Enter constructor
      Log.log_3000(CLASSNAME, null);

      // Check preconditions
      MandatoryArgumentChecker.check("descriptor", descriptor);

      // Set fields
      _descriptor = descriptor;

      // TRACE: Leave constructor
      Log.log_3002(CLASSNAME, null);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The descriptor for this service. Cannot be <code>null</code>.
    */
   private final Descriptor _descriptor;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the descriptor.
    *
    * @return
    *    the descriptor for this service, never <code>null</code>.
    */
   public final Descriptor getDescriptor() {
      return _descriptor;
   }

   /**
    * Performs a call using the specified subject.
    * {@link TargetDescriptor Target descriptors} will be picked and passed
    * to {@link #doCallImpl(CallRequest,TargetDescriptor)} until there is one
    * that succeeds, as long as fail-over can be done (according to
    * {@link #shouldFailOver(CallRequest,Throwable)}).
    *
    * <p>If one of the calls succeeds, then the result is returned. If
    * none succeeds or if fail-over should not be done, then a
    * {@link CallException} is thrown.
    *
    * <p>Each call attempt consists of a call to
    * {@link #doCallImpl(CallRequest,TargetDescriptor)}.
    *
    * @param request
    *    the call request, not <code>null</code>.
    *
    * @return
    *    a combination of the call result and a link to the
    *    {@link TargetDescriptor target} that returned this result, if and
    *    only if one of the calls succeeded, could be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>request == null</code>.
    *
    * @throws CallException
    *    if all call attempts failed.
    *
    * @since XINS 0.207
    */
   protected final CallResult doCall(CallRequest request)
   throws IllegalArgumentException, CallException {

      // Check preconditions
      MandatoryArgumentChecker.check("request", request);

      // Keep a reference to the most recent CallException since
      // setNext(CallException) needs to be called on it to make it link to
      // the next one (if there is one)
      CallException lastException = null;

      // Maintain the list of CallExceptions
      //
      // This is needed if a successful result (a CallResult object) is
      // returned, since it will contain references to the exceptions as well;
      //
      // Note that this object is lazily initialized because this code is
      // performance- and memory-optimized for the successful case
      CallExceptionList exceptions = null;

      // Iterate over all targets
      Iterator iterator = _descriptor.iterateTargets();

      // There should be at least one target
      if (! iterator.hasNext()) {
         throw new Error("Unexpected situation: " + _descriptor.getClass().getName() + " contains no target descriptors.");
      }

      // Loop over all TargetDescriptors
      boolean shouldContinue = true;
      while (shouldContinue) {

         // Get a reference to the next TargetDescriptor
         TargetDescriptor target = (TargetDescriptor) iterator.next();

         // Call using this target
         Log.log_3312(target.getURL());
         Object result = null;
         boolean succeeded = false;
         long start = System.currentTimeMillis();
         try {

            // Attempt the call
            result = doCallImpl(request, target);
            succeeded = true;

         // If the call to the target fails, store the exception and try the next
         } catch (Throwable exception) {

            Log.log_3313(target.getURL());

            long duration = System.currentTimeMillis() - start;

            // If the caught exception is not a CallException, then
            // encapsulate it in one
            CallException currentException;
            if (exception instanceof CallException) {
               currentException = (CallException) exception;
            } else {
               currentException = new UnexpectedExceptionCallException(request, target, duration, null, exception);
            }

            // Link the previous exception (if there is one) to this one
            if (lastException != null) {
               lastException.setNext(currentException);
            }

            // Now set this exception as the most recent CallException
            lastException = currentException;

            // If this is the first exception being caught, then lazily
            // initialize the CallExceptionList and keep a reference to the
            // first exception
            if (exceptions == null) {
               exceptions = new CallExceptionList();
            }

            // Store the failure
            exceptions.add(currentException);

            // Determine whether fail-over is allowed and whether we have
            // another target to fail-over to
            boolean failOver = shouldFailOver(request, exception);
            boolean haveNext = iterator.hasNext();

            // No more targets and no fail-over
            if (!haveNext && !failOver) {
               Log.log_3315();
               shouldContinue = false;

            // No more targets but fail-over would be allowed
            } else if (!haveNext) {
               Log.log_3316();
               shouldContinue = false;

            // More targets available but fail-over is not allowed
            } else if (!failOver) {
               Log.log_3317();
               shouldContinue = false;

            // More targets available and fail-over is allowed
            } else {
               Log.log_3318();
               shouldContinue = true;
            }
         }

         // The call succeeded
         if (succeeded) {
            long duration = System.currentTimeMillis() - start;
            return createCallResult(request, target, duration, exceptions, result);
         }
      }

      // Loop ended, call failed completely
      Log.log_3314();
      throw exceptions.get(0);
   }

   /**
    * Calls the specified target using the specified subject. This method must
    * be implemented by subclasses. It is called as soon as a target is
    * selected to be called. If the call fails, then a {@link CallException}
    * should be thrown. If the call succeeds, then the call result should be
    * returned from this method.
    *
    * @param target
    *    the target to call, cannot be <code>null</code>.
    *
    * @param request
    *    the call request to be executed, cannot be <code>null</code>.
    *
    * @return
    *    the result, if and only if the call succeeded, could be
    *    <code>null</code>.
    *
    * @throws ClassCastException
    *    if the specified <code>request</code> object is not <code>null</code>
    *    and not an instance of an expected subclass of class
    *    {@link CallRequest}.
    *
    * @throws IllegalArgumentException
    *    if <code>target == null || request == null</code>.
    *
    * @throws CallException
    *    if the call to the specified target failed.
    *
    * @since XINS 0.207
    */
   protected abstract Object doCallImpl(CallRequest      request,
                                        TargetDescriptor target)
   throws ClassCastException, IllegalArgumentException, CallException;

   /**
    * Constructs an appropriate <code>CallResult</code> object for a
    * successful call attempt. This method is called from
    * {@link #doCall(CallRequest)}.
    *
    * @param request
    *    the {@link CallRequest} that was to be executed, never
    *    <code>null</code> when called from {@link #doCall(CallRequest)}.
    *
    * @param succeededTarget
    *    the {@link TargetDescriptor} for the service that was successfully
    *    called, never <code>null</code> when called from
    *    {@link #doCall(CallRequest)}.
    *
    * @param duration
    *    the call duration in milliseconds, guaranteed to be a non-negative
    *    number when called from {@link #doCall(CallRequest)}.
    *
    * @param exceptions
    *    the list of {@link CallException} instances, or <code>null</code> if
    *    there were no call failures.
    *
    * @param result
    *    the result from the call, which is the object returned by
    *    {@link #doCallImpl(CallRequest,TargetDescriptor)}, can be
    *    <code>null</code>.
    *
    * @return
    *    a {@link CallResult} instance, never <code>null</code>.
    *
    * @throws ClassCastException
    *    if <code>request</code> and/or <code>result</code> are not of the
    *    correct class.
    */
   protected abstract CallResult createCallResult(CallRequest       request,
                                                  TargetDescriptor  succeededTarget,
                                                  long              duration,
                                                  CallExceptionList exceptions,
                                                  Object            result)
   throws ClassCastException;

   /**
    * Runs the specified task. If the task does not finish within the total
    * time-out period, then the thread executing it is interrupted using the
    * {@link Thread#interrupt()} method and a {@link TimeOutException} is
    * thrown.
    *
    * @param task
    *    the task to run, cannot be <code>null</code>.
    *
    * @param descriptor
    *    the descriptor for the target on which the task is executed, cannot
    *    be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>task == null || descriptor == null</code>.
    *
    * @throws IllegalThreadStateException
    *    if the task is a {@link Thread} which is already started.
    *
    * @throws SecurityException
    *    if the task did not finish within the total time-out period, but the
    *    interruption of the thread was disallowed (see
    *    {@link Thread#interrupt()}).
    *
    * @throws TimeOutException
    *    if the task did not finish within the total time-out period and was
    *    interrupted.
    *
    * @since XINS 0.204
    */
   protected final void controlTimeOut(Runnable         task,
                                       TargetDescriptor descriptor)
   throws IllegalArgumentException,
          IllegalThreadStateException,
          SecurityException,
          TimeOutException {

      // Check preconditions
      MandatoryArgumentChecker.check("task",       task,
                                     "descriptor", descriptor);

      // Determine the total time-out
      int totalTimeOut = descriptor.getTotalTimeOut();

      // If there is no total time-out, then execute the task on this thread
      if (totalTimeOut < 1) {
         task.run();

      // Otherwise a time-out controller will be used
      } else {
         TimeOutController.execute(task, totalTimeOut);
      }
   }

   /**
    * Determines whether a call should fail-over to the next selected target.
    * This method should only be called from {@link #doCall(CallRequest)}.
    *
    * <p>This method is typically overridden by subclasses. Usually, a
    * subclass first calls this method in the superclass, and if that returns
    * <code>false</code> it does some additional checks, otherwise
    * <code>true</code> is immediately returned.
    *
    * <p>The implementation of this method in class {@link ServiceCaller}
    * returns <code>true</code> if and only if <code>exception instanceof
    * {@link ConnectionCallException}</code>.
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
      return (exception instanceof ConnectionCallException);
   }
}
