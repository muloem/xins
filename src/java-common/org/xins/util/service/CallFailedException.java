/*
 * $Id$
 */
package org.xins.util.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.xins.util.MandatoryArgumentChecker;

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
    *    the list of targets for which the call failed, can be
    *    <code>null</code>; all elements in this {@link List} must be
    *    {@link ServiceDescriptor} objects, no <code>null</code> elements are
    *    allowed, but duplicates are.
    *
    * @param exceptions
    *    the list of caught exceptions, matching the list of failed targets,
    *    can be <code>null</code>; all elements in this {@link List} must be
    *    {@link Throwable} objects, no <code>null</code> elements are allowed,
    *    but duplicates are.
    *
    * @throws IllegalArgumentException
    *    if <code>(failedTargets == null &amp;&amp; exceptions != null)
    *         || (failedTargets != null &amp;&amp; (
    *               exceptions == null
    *            || failedTargets.size() != exceptions.size()
    *            || !(exceptions.get(<em>i</em>) instanceof Throwable)
    *            || failedTargets.get(<em>i</em>) == null
    *            || !(failedTargets.get(<em>i</em>) instanceof ServiceDescriptor)
    *            || failedTargets.get(<em>x</em>).equals(failedTargets.get(<em>y</em>))))</code>,
    *    where <code>0 &lt;= <em>i</em> &lt; failedTargets.size()</code>
    *    and   <code>0 &lt;= <em>x</em> &lt; <em>y</em> &lt; failedTargets.size()</code>.
    */
   public CallFailedException(Object subject,
                              List   failedTargets,
                              List   exceptions)
   throws IllegalArgumentException {

      // Check preconditions
      CallResult.checkFailureLists(failedTargets, exceptions);

      _subject       = subject;
      _failedTargets = failedTargets == null ? null : Collections.unmodifiableList(new ArrayList(failedTargets));
      _exceptions    = exceptions    == null ? null : Collections.unmodifiableList(new ArrayList(exceptions));
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
    * All elements in this {@link List} are {@link ServiceDescriptor} objects.
    * The {@link List} contains no <code>null</code> elements, but it may
    * contain duplicates.
    *
    * <p>This is an unmodifiable {@link List}.
    */
   private final List _failedTargets;

   /**
    * The list of caught exceptions, one per failed target. Can be
    * <code>null</code> if and only if <code>_failedTargets == null</code>.
    * All elements in this {@link List} are {@link Throwable} objects.
    * The {@link List} contains no <code>null</code> elements, but it may
    * contain duplicates.
    *
    * <p>This is an unmodifiable {@link List}.
    */
   private final List _exceptions;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
