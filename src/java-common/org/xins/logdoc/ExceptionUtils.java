/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.logdoc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.WeakHashMap;

/**
 * Utility functions related to exceptions.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.2.0
 */
public final class ExceptionUtils extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Reference to the <code>getCause()</code> method in class
    * <code>Throwable</code>. This reference will be <code>null</code> on Java
    * 1.3.
    */
   private static Method GET_CAUSE;

   /**
    * Reference to the <code>initCause()</code> method in class
    * <code>Throwable</code>. This reference will be <code>null</code> on Java
    * 1.3.
    */
   private static Method SET_CAUSE;

   /**
    * Table that maps from exception to cause. This table will only be used on
    * Java 1.3. On Java 1.4 and up it will be <code>null</code>.
    */
   private static WeakHashMap CAUSE_TABLE;

   /**
    * Placeholder for the <code>null</code> object. This object will be stored
    * in the {@link #CAUSE_TABLE} on Java 1.3 if the cause for an exception is
    * set to <code>null</code>.
    */
   private static final Object NULL = new Object();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Initializes this class.
    */
   static {

      Class[] args = new Class[] { Throwable.class };

      try {
         GET_CAUSE = Throwable.class.getDeclaredMethod("getCause", null);
         SET_CAUSE = Throwable.class.getDeclaredMethod("initCause", args);
         CAUSE_TABLE = null;

      // Method does not exist, this is not Java 1.4
      } catch (NoSuchMethodException exception) {
         GET_CAUSE   = null;
         SET_CAUSE   = null;
         CAUSE_TABLE = new WeakHashMap();

      // Access denied
      } catch (SecurityException exception) {
         throw new RuntimeException("Unable to get getCause() method of class java.lang.Throwable: Access denied by security manager.");
      }
   }

   /**
    * Determines the root cause for the specified exception.
    *
    * @param exception
    *    the exception to determine the root cause for, cannot be
    *    <code>null</code>.
    *
    * @return
    *    the root cause exception, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>exception == null</code>.
    */
   public static final Throwable getRootCause(Throwable exception)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("exception", exception);

      // Get the root cause of the exception
      Throwable cause = getCause(exception);
      while (cause != null) {
         exception = cause;
         cause = getCause(exception);
      }

      return exception;
   }

   /**
    * Determines the cause for the specified exception.
    *
    * @param exception
    *    the exception to determine the cause for, cannot be
    *    <code>null</code>.
    *
    * @return
    *    the cause exception, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>exception == null</code>.
    */
   private static final Throwable getCause(Throwable exception)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("exception", exception);

      // On Java 1.4 (and up) use the Throwable.getCause() method
      if (GET_CAUSE != null) {
         try {
            return (Throwable) GET_CAUSE.invoke(exception, null);
         } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to invoke Throwable.getCause() method. Caught IllegalAccessException.");
         } catch (IllegalArgumentException e) {
            throw new RuntimeException("Unable to invoke Throwable.getCause() method. Caught IllegalArgumentException");
         } catch (InvocationTargetException e) {
            throw new RuntimeException("Unable to invoke Throwable.getCause() method. Caught InvocationTargetException");
         }

      // On Java 1.3 use the static table
      } else {
         Object cause = CAUSE_TABLE.get(exception);
         return (cause == NULL) ? null : (Throwable) cause;
      }
   }

   /**
    * Sets the cause for the specified exception.
    *
    * @param exception
    *    the exception to set the cause for, cannot be <code>null</code>.
    *
    * @param cause
    *    the cause exception, can be <code>null</code> but cannot be the
    *    same as <code>exception</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>exception == null || exception == cause</code>.
    *
    * @throws IllegalStateException
    *    if the cause exception was already set.
    */
   public static final void setCause(Throwable exception, Throwable cause)
   throws IllegalArgumentException, IllegalStateException {

      // TODO: Add unit test for IllegalStateException

      // Check preconditions
      MandatoryArgumentChecker.check("exception", exception);
      if (exception == cause) {
         throw new IllegalArgumentException("exception == cause");
      }

      // On Java 1.4 (and up) use the Throwable.initCause() method
      if (SET_CAUSE != null) {
         try {
            Object args[] = new Object[] { cause };
            SET_CAUSE.invoke(exception, args);
         } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to invoke Throwable.initCause() method. Caught IllegalAccessException.");
         } catch (IllegalArgumentException e) {
            throw new RuntimeException("Unable to invoke Throwable.initCause() method. Caught IllegalArgumentException");
         } catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();
            if (targetException instanceof RuntimeException) {
               throw (RuntimeException) targetException;
            } else if (targetException instanceof Error) {
               throw (Error) targetException;
            } else {
               throw new RuntimeException("Unable to invoke Throwable.initCause() method. Throwable.initCause() has thrown an unexpected exception. Exception class is " + targetException.getClass().getName() + ".  Message is: " + targetException.getMessage() + '.');
            }
         }

      // On Java 1.3 use the static table
      } else {
         if (CAUSE_TABLE.get(exception) != null) {
            throw new IllegalStateException("Cause for exception already set.");
         }

         Object value = (cause == null) ? NULL : cause;
         CAUSE_TABLE.put(exception, value);
      }
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>ExceptionUtils</code> object.
    */
   private ExceptionUtils() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
