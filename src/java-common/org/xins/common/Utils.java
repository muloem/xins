/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common;

import org.xins.common.MandatoryArgumentChecker;

import org.xins.common.text.FastStringBuffer;
import org.xins.common.text.TextUtils;

/**
 * General utility functions.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.1.0
 */
public final class Utils extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Retrieves the actual (major) Java version.
    *
    * @return
    *    the actual Java version.
    *
    * @since XINS 1.2.0
    */
   public static final double getJavaVersion() {
      return Double.parseDouble(System.getProperty("java.version").substring(0, 3));
   }

   /**
    * Retrieves the name of the calling class. If it cannot be determined,
    * then a special string (e.g. <code>"&lt;unknown&gt;"</code>) is returned.
    *
    * @return
    *    the class name of the caller of the caller of this method, never an
    *    empty string and never <code>null</code>.
    */
   public static final String getCallingClass() {
      if (getJavaVersion() >= 1.4) {
         Throwable exception = new Throwable();
         StackTraceElement[] trace = exception.getStackTrace();
         if (trace != null && trace.length >= 3) {
            StackTraceElement caller = trace[2];
            if (caller != null) {
               String callingClass = caller.getClassName();
               if (! TextUtils.isEmpty(callingClass)) {
                  return callingClass;
               }
            }
         }
      }

      // Fallback
      return "<unknown>";
   }

   /**
    * Retrieves the name of the calling method. If it cannot be determined,
    * then a special string (e.g. <code>"&lt;unknown&gt;"</code>) is returned.
    *
    * @return
    *    the method name of the caller of the caller of this method, never an
    *    empty string and never <code>null</code>.
    */
   public static final String getCallingMethod() {
      if (getJavaVersion() >= 1.4) {
         Throwable exception = new Throwable();
         StackTraceElement[] trace = exception.getStackTrace();
         if (trace != null && trace.length >= 3) {
            StackTraceElement caller = trace[2];
            if (caller != null) {
               String callingMethod = caller.getMethodName();
               if (! TextUtils.isEmpty(callingMethod)) {
                  return callingMethod;
               }
            }
         }
      }

      // Fallback
      return "<unknown>";
   }

   /**
    * Logs a programming error with an optional cause exception, and returns a
    * <code>ProgrammingException</code> object for it.
    *
    * @param detail
    *    the detail message, can be <code>null</code>.
    *
    * @return
    *    an appropriate {@link ProgrammingException} that can be thrown by the
    *    calling method, never <code>null</code>.
    */
   public static final ProgrammingException
   logProgrammingError(String details) {
      return logProgrammingError(getCallingClass(), getCallingMethod(), getCallingClass(), getCallingMethod(), details);
   }

   /**
    * Logs a programming error with an optional cause exception, and returns a
    * <code>ProgrammingException</code> object for it.
    *
    * @param cause
    *    the cause exception, cannot be <code>null</code>.
    *
    * @return
    *    an appropriate {@link ProgrammingException} that can be thrown by the
    *    calling method, never <code>null</code>.
    */
   public static final ProgrammingException
   logProgrammingError(Throwable cause) {
      String sourceClass = "<unknown>";
      String sourceMethod = "<unknown>";
      try {
         StackTraceElement[] trace = cause.getStackTrace();
         StackTraceElement source = trace[trace.length - 1];
         sourceClass = source.getClassName();
         sourceMethod = source.getMethodName();
      } catch (Throwable t) {
      }
      return logProgrammingError(getCallingClass(), getCallingMethod(), sourceClass, sourceMethod, cause.getMessage(), cause);
   }

   /**
    * Logs a programming error with an optional cause exception, and returns a
    * <code>ProgrammingException</code> object for it.
    *
    * @param detectingClass
    *    the name of the class that detected the problem, or
    *    <code>null</code> if unknown.
    *
    * @param detectingMethod
    *    the name of the method within the <code>detectingClass</code> that
    *    detected the problem, or <code>null</code> if unknown.
    *
    * @param subjectClass
    *    the name of the class which exposes the programming error, or
    *    <code>null</code> if unknown.
    *
    * @param subjectMethod
    *    the name of the method (within the <code>subjectClass</code>) which
    *    exposes the programming error, or <code>null</code> if unknown.
    *
    * @param detail
    *    the detail message, can be <code>null</code>.
    *
    * @param cause
    *    the cause exception, can be <code>null</code>.
    *
    * @return
    *    an appropriate {@link ProgrammingException} that can be thrown by the
    *    calling method, never <code>null</code>.
    */
   public static final ProgrammingException
   logProgrammingError(String    detectingClass,
                       String    detectingMethod,
                       String    subjectClass,
                       String    subjectMethod,
                       String    detail,
                       Throwable cause) {

      // Log programming error (not due to exception)
      if (cause == null) {
         Log.log_1050(detectingClass, detectingMethod,
                      subjectClass,   subjectMethod,
                      detail);

      // Log programming error (due to exception)
      } else {
         Log.log_1052(cause,
                      detectingClass, detectingMethod,
                      subjectClass,   subjectMethod,
                      detail);
      }

      // Construct and return ProgrammingException object
      return new ProgrammingException(detectingClass, detectingMethod,
                                      subjectClass,   subjectMethod,
                                      detail,         cause);

   }

   /**
    * Logs a programming error with no cause exception, and returns a
    * <code>ProgrammingException</code> object for it.
    *
    * @param detectingClass
    *    the name of the class that detected the problem, or
    *    <code>null</code> if unknown.
    *
    * @param detectingMethod
    *    the name of the method within the <code>detectingClass</code> that
    *    detected the problem, or <code>null</code> if unknown.
    *
    * @param subjectClass
    *    the name of the class which exposes the programming error, or
    *    <code>null</code> if unknown.
    *
    * @param subjectMethod
    *    the name of the method (within the <code>subjectClass</code>) which
    *    exposes the programming error, or <code>null</code> if unknown.
    *
    * @param detail
    *    the detail message, can be <code>null</code>.
    *
    * @return
    *    an appropriate {@link ProgrammingException} that can be thrown by the
    *    calling method, never <code>null</code>.
    */
   public static final ProgrammingException
   logProgrammingError(String    detectingClass,
                       String    detectingMethod,
                       String    subjectClass,
                       String    subjectMethod,
                       String    detail) {

      return logProgrammingError(detectingClass, detectingMethod,
                                 subjectClass,   subjectMethod,
                                 detail,         null);
   }

   /**
    * Determines the name of the specified class.
    *
    * @param c
    *    the class to determine the name for, not <code>null</code>.
    *
    * @return
    *    the name of the class, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>c == null</code>.
    *
    * @since XINS 1.2.0
    */
   public static final String getNameOfClass(Class c)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("c", c);

      // TODO: if (c.isPrimitive()) {

      if (c.isArray()) {
         FastStringBuffer buffer = new FastStringBuffer(137);
         Class comp = c.getComponentType();
         buffer.append(getNameOfClass(comp));
         if (c.getName().charAt(0) == '[') {
            buffer.append("[]");
         }
         return buffer.toString();
      } else {
         return c.getName();
      }
   }

   /**
    * Determines the name of the class of the specified object.
    *
    * @param object
    *    the object to determine the name of the class for, not
    *    <code>null</code>.
    *
    * @return
    *    the name of the class, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>object == null</code>.
    *
    * @since XINS 1.2.0
    */
   public static final String getClassName(Object object)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("object", object);

      return getNameOfClass(object.getClass());
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>Utils</code> object.
    */
   private Utils() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

}
