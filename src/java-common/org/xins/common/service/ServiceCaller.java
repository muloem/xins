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

      // Check preconditions
      MandatoryArgumentChecker.check("descriptor", descriptor);

      // Set fields
      _descriptor = descriptor;
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
    * Performs a call using the specified subject. Target
    * {@link TargetDescriptor descriptors} will be picked and passed
    * to {@link #doCallImpl(TargetDescriptor,Object)} until there is one that
    * succeeds, as long as fail-over can be done (according to
    * {@link #shouldFailOver(Object,Throwable)}.
    *
    * <p>If one of the calls succeeds, then the result is returned. If
    * none succeeds or if fail-over should not be done, then a {@link
    * CallFailedException} is thrown.
    *
    * <p>Each attempt consists of a call to
    * {@link #doCallImpl(TargetDescriptor,Object)}.
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
    * @throws CallFailedException
    *    if all calls failed.
    *
    * @since XINS 0.207
    */
   protected final CallResult doCall(CallRequest request)
   throws CallException {

      // Check preconditions
      MandatoryArgumentChecker.check("request", request);

      // Keep a reference to the most recent CallException since
      // setNext(CallException) needs to be called on it to make it link to
      // the next one, if there is one
      CallException lastException = null;

      // Keep a reference to the very first CallException since that is the
      // exception that will be thrown, if there will be any CallException
      CallException firstException = null;

      // Maintain the list of failed targets and the corresponding exceptions;
      // This is needed if a successful result (a CallResult object) is
      // returned, since it will contain references to the failures as well;
      // Note that these lists are lazily initialized because this code is
      // performance- and memory-optimized for the successful case
      ArrayList failedTargets = null;
      ArrayList exceptions    = null;

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
            result = doCallImpl(target, request);
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
            // initialize the list of failed targets and the list of
            // exceptions and keep a reference to the first exception
            if (failedTargets == null) {
               failedTargets  = new ArrayList();
               exceptions     = new ArrayList();
               firstException = currentException;
            }

            // Store the failed target and the corresponding exception
            failedTargets.add(target);
            exceptions.add(exception);

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
            return new CallResult(failedTargets, exceptions, target, result);
         }
      }

      // Loop ended, call failed completely
      Log.log_3314();
      throw firstException;
   }

   /**
    * Calls the specified target using the specified subject. This method must
    * be implemented by subclasses. It is called as soon as a target is
    * selected to be called. If the call fails, then an exception should be
    * thrown. If the call succeeds, then the call result should be returned
    * from this method.
    *
    * @param target
    *    the target to call, cannot be <code>null</code>.
    *
    * @param subject
    *    the subject passed, could possibly be <code>null</code>.
    *
    * @return
    *    the result, if and only if the call succeeded, could be
    *    <code>null</code>.
    *
    * @throws Throwable
    *    if the call to the specified target failed.
    *
    * @since XINS 0.207
    */
   protected abstract Object doCallImpl(TargetDescriptor target,
                                        CallRequest      request)
   throws Throwable;

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
    * This method should be overridden by subclasses. The implementation in
    * class {@link ServiceCaller} always returns <code>false</code>.
    *
    * @param request
    *    the request for the call, as passed to {@link #doCall(CallRequest)},
    *    can be <code>null</code>.
    *
    * @param exception
    *    the exception caught while calling the most recently called target,
    *    never <code>null</code>.
    *
    * @return
    *    <code>true</code> if the call should fail-over to the next target, or
    *    <code>false</code> if it should not.
    *
    * @since XINS 0.207
    */
   protected boolean shouldFailOver(CallRequest request, Throwable exception) {
      return false;
   }
}
