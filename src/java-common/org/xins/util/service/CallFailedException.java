/*
 * $Id$
 */
package org.xins.util.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.xins.util.MandatoryArgumentChecker;
import org.xins.util.text.FastStringBuffer;

/**
 * Exception that indicates that a call failed after all possible target
 * services were tried.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.115
 */
public final class CallFailedException extends Exception {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Creates a message for the constructor.
    *
    * @param subject
    *    the subject to be passed to the service, could possibly be
    *    <code>null</code>.
    *
    * @param failedTargets
    *    the list of targets for which the call failed, cannot be
    *    <code>null</code>; must contain at least 1 element; all elements in
    *    this {@link List} must be {@link TargetDescriptor} objects, no
    *    <code>null</code> elements are allowed, but duplicates are.
    *
    * @param exceptions
    *    the list of caught exceptions, matching the list of failed targets,
    *    cannot be <code>null</code>; must contain at least 1 element; all
    *    elements in this {@link List} must be {@link Throwable} objects, no
    *    <code>null</code> elements are allowed, but duplicates are.
    *
    * @throws IllegalArgumentException
    *    if <code>failedTargets == null || exceptions == null)
    *         || failedTargets.size() != exceptions.size()
    *         || failedTargets.size() &lt; 1
    *         || failedTargets.get(<em>i</em>) == null
    *         || !(failedTargets.get(<em>i</em>) instanceof TargetDescriptor)
    *         || !(exceptions.get(<em>i</em>) instanceof Throwable)</code>
    *    where <code>0 &lt;= <em>i</em> &lt; failedTargets.size()</code>.
    */
   private static final String createMessage(Object subject,
                                             List   failedTargets,
                                             List   exceptions)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("failedTargets", failedTargets,
                                     "exceptions",    exceptions);
      CallResult.checkFailureLists(failedTargets, exceptions);

      int count = exceptions.size();

      // Construct the message
      FastStringBuffer buffer = new FastStringBuffer(50);
      buffer.append("Failed to call service. Tried ");
      if (count == 1) {
         buffer.append("1 target.");
      } else {
         buffer.append(count);
         buffer.append(" targets.");
      }

      // XXX: We could possibly improve the message by including more
      //      information.

      // Returns the message to the constructor
      return buffer.toString();
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>CallFailedException</code>.
    *
    * @param subject
    *    the subject to be passed to the service, could possibly be
    *    <code>null</code>.
    *
    * @param failedTargets
    *    the list of targets for which the call failed, cannot be
    *    <code>null</code>; must contain at least 1 element; all elements in
    *    this {@link List} must be {@link TargetDescriptor} objects, no
    *    <code>null</code> elements are allowed, but duplicates are.
    *
    * @param exceptions
    *    the list of caught exceptions, matching the list of failed targets,
    *    cannot be <code>null</code>; must contain at least 1 element; all
    *    elements in this {@link List} must be {@link Throwable} objects, no
    *    <code>null</code> elements are allowed, but duplicates are.
    *
    * @throws IllegalArgumentException
    *    if <code>failedTargets == null || exceptions == null)
    *         || failedTargets.size() != exceptions.size()
    *         || failedTargets.size() &lt; 1
    *         || failedTargets.get(<em>i</em>) == null
    *         || !(failedTargets.get(<em>i</em>) instanceof TargetDescriptor)
    *         || !(exceptions.get(<em>i</em>) instanceof Throwable)</code>
    *    where <code>0 &lt;= <em>i</em> &lt; failedTargets.size()</code>.
    */
   public CallFailedException(Object subject,
                              List   failedTargets,
                              List   exceptions)
   throws IllegalArgumentException {

      // Check preconditions, create message and pass it to the
      // superconstructor
      super(createMessage(subject, failedTargets, exceptions));

      // Store the information
      _subject       = subject;
      _failedTargets = Collections.unmodifiableList(new ArrayList(failedTargets));
      _exceptions    = Collections.unmodifiableList(new ArrayList(exceptions));
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The original subject for the call. Could be <code>null</code>.
    */
   private final Object _subject;

   /**
    * The list of targets for which the call failed. Can be <code>null</code>.
    * All elements in this {@link List} are {@link TargetDescriptor} objects.
    * The {@link List} contains no <code>null</code> elements, but it may
    * contain duplicates.
    *
    * <p>This is an unmodifiable {@link List}. It contains at least 1 element.
    */
   private final List _failedTargets;

   /**
    * The list of caught exceptions, one per failed target. Can be
    * <code>null</code> if and only if <code>_failedTargets == null</code>.
    * All elements in this {@link List} are {@link Throwable} objects.
    * The {@link List} contains no <code>null</code> elements, but it may
    * contain duplicates.
    *
    * <p>This is an unmodifiable {@link List}. It contains at least 1 element.
    */
   private final List _exceptions;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the subject for the call.
    *
    * @return
    *    the subject for the call, can be <code>null</code>.
    */
   public Object getSubject() {
      return _subject;
   }

   /**
    * Returns the list of targets for which the call failed. The returned
    * {@link List} cannot be <code>null</code>. All elements in the
    * {@link List} are {@link TargetDescriptor} objects, and it contains no
    * <code>null</code> elements. It may contain duplicates, though.
    *
    * <p>The returned {@link List} is unmodifiable and contains at least 1
    * element.
    *
    * @return
    *    the {@link List} of failed targets, not <code>null</code>.
    */
   public List getFailedTargets() {
      return _failedTargets;
   }

   /**
    * The list of caught exceptions, one per failed target. The returned
    * {@link List} cannot be <code>null</code>, but if it is not then all elements in
    * the {@link List} are {@link Throwable} objects, and it contains no
    * <code>null</code> elements. It may contain duplicates, though.
    *
    * <p>The returned {@link List} is unmodifiable and contains at least 1
    * element.
    *
    * @return
    *    the {@link List} of exceptions, not <code>null</code>.
    */
   public List getExceptions() {
      return _exceptions;
   }
}
