/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common;

import org.xins.common.MandatoryArgumentChecker;

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

   /**
    * The Java 1.1 version.
    */
   public static final JavaVersion JAVA_1_1 = new JavaVersion("1.1", 1);

   /**
    * The Java 1.2 version.
    */
   public static final JavaVersion JAVA_1_2 = new JavaVersion("1.2", 2);

   /**
    * The Java 1.3 version.
    */
   public static final JavaVersion JAVA_1_3 = new JavaVersion("1.3", 3);

   /**
    * The Java 1.4 version.
    */
   public static final JavaVersion JAVA_1_4 = new JavaVersion("1.4", 4);

   /**
    * The Java 1.5 version. This version is also known as Java 5.0.
    */
   public static final JavaVersion JAVA_1_5 = new JavaVersion("1.5", 5);

   /**
    * The Java 1.6 version. This version is also known as Java 6.0.
    */
   public static final JavaVersion JAVA_1_6 = new JavaVersion("1.6", 6);

   /**
    * The current Java version. Never <code>null</code>.
    */
   private static final JavaVersion JAVA_VERSION;


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Initializes this class.
    */
   static {

      // Get the system property that contains the Java VM version
      final String PROPERTY_KEY = "java.vm.version";
      String s = System.getProperty(PROPERTY_KEY);
      if (s == null) {
         final String DETAIL = "Unable to determine Java version. Value of property \""
                             + PROPERTY_KEY
                             + "\" is null.";
         throw new RuntimeException(DETAIL);
      }

      // Forward to the version number in the string
      String s2 = s;
      while (s2.length() >= 3 && !s2.startsWith("1.")) {
         s2 = s2.substring(1);
      }

      // Determine the major Java version
      if (s2.startsWith("1.1")) {
         JAVA_VERSION = JAVA_1_1;
      } else if (s2.startsWith("1.2")) {
         JAVA_VERSION = JAVA_1_2;
      } else if (s2.startsWith("1.3")) {
         JAVA_VERSION = JAVA_1_3;
      } else if (s2.startsWith("1.4")) {
         JAVA_VERSION = JAVA_1_4;
      } else if (s2.startsWith("1.5")) {
         JAVA_VERSION = JAVA_1_5;
      } else if (s2.startsWith("1.6")) {
         JAVA_VERSION = JAVA_1_6;

      // Unable to determine major Java version
      } else {
         final String DETAIL = "Unable to determine Java version. Value of property \""
                             + PROPERTY_KEY
                             + "\" is \""
                             + s
                             + "\".";
         throw new RuntimeException(DETAIL);
      }
   }

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Retrieves the actual (major) Java version.
    *
    * @return
    *    the actual Java version, never <code>null</code>.
    *
    * @since XINS 1.2.0
    */
   public static final JavaVersion getJavaVersion() {
      return JAVA_VERSION;
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
      if (! JAVA_VERSION.olderThan(JAVA_1_4)) {
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
      if (! JAVA_VERSION.olderThan(JAVA_1_4)) {
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

   //-------------------------------------------------------------------------
   // Inner classes
   //-------------------------------------------------------------------------

   /**
    * Abstraction of a major Java release.
    *
    * @version $Revision$ $Date$
    * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
    *
    * @since XINS 1.2.0
    */
   public static final class JavaVersion extends Object {

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      /**
       * Constructs a new <code>JavaVersion</code> object with the specified
       * version string.
       *
       * @param version
       *    the version string, should not be <code>null</code>.
       *
       * @param value
       *    value indicating whether the version is newer or older.
       */
      private JavaVersion(String version, int value) {
         _version = version;
         _value   = value;
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      /**
       * The version string. Should not be <code>null</code>.
       */
      private final String _version;

      /**
       * Value indicating whether the version is newer or older.
       */
      private final int _value;


      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------

      /**
       * Checks if this version is older than the specified one.
       *
       * @param other
       *    the version to compare against, cannot be <code>null</code>.
       *
       * @throws IllegalArgumentException
       *    if <code>other == null</code>.
       */
      public boolean olderThan(JavaVersion other)
      throws IllegalArgumentException {
         MandatoryArgumentChecker.check("other", other);
         return _value < other._value;
      }

      /**
       * Checks if this version is newer than or equal to the specified one.
       *
       * @param other
       *    the version to compare against, cannot be <code>null</code>.
       *
       * @throws IllegalArgumentException
       *    if <code>other == null</code>.
       */
      public boolean newerOrEqual(JavaVersion other)
      throws IllegalArgumentException {
         MandatoryArgumentChecker.check("other", other);
         return _value >= other._value;
      }
   }
}
