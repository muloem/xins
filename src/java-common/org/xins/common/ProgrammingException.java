/*
 * $Id$
 *
 * Copyright 2003-2006 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common;

import org.xins.logdoc.ExceptionUtils;
import org.xins.common.text.FastStringBuffer;

/**
 * Exception thrown when a programming error is detected.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.1.0
 */
public final class ProgrammingException
extends RuntimeException {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Creates the exception message for the specified details.
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
    *    the exception message, can be <code>null</code>.
    */
   private static String createMessage(String    detectingClass,
                                       String    detectingMethod,
                                       String    subjectClass,
                                       String    subjectMethod,
                                       String    detail,
                                       Throwable cause) {

      FastStringBuffer message = new FastStringBuffer(271);

      message.append("Programming error suspected");

      if (subjectClass != null) {
         message.append(" in class ");
         message.append(subjectClass);
         if (subjectMethod != null) {
            message.append(", method ");
            message.append(subjectMethod);
         }
      }

      if (detectingClass != null) {
         message.append(". Detected by class ");
         message.append(detectingClass);
         if (detectingMethod != null) {
            message.append(", method ");
            message.append(detectingMethod);
         }
      }

      if (detail != null) {
         message.append(". Detail: \"");
         message.append(detail);
         message.append("\"");
      }

      message.append('.');

      return message.toString();
   }


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>ProgrammingException</code> for the specified
    * class and method, indicating which class and method detected the
    * problem.
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
    */
   public ProgrammingException(String    detectingClass,
                               String    detectingMethod,
                               String    subjectClass,
                               String    subjectMethod,
                               String    detail,
                               Throwable cause) {

      // Call superconstructor with a constructed message
      super(createMessage(detectingClass, detectingMethod,
                          subjectClass,   subjectMethod,
                          detail,
                          cause));

      // Register the cause for this exception
      ExceptionUtils.setCause(this, cause);

      // Store all the information in fields
      _detectingClass  = detectingClass;
      _detectingMethod = detectingMethod;
      _subjectClass    = subjectClass;
      _subjectMethod   = subjectMethod;
      _detail          = detail;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The name of the class that detected the problem. Is <code>null</code> if
    * unknown.
    */
   private final String _detectingClass;

   /**
    * The name of the method where the problem was detected. Is
    * <code>null</code> if unknown.
    */
   private final String _detectingMethod;

   /**
    * The name of the class which exposes the programming error. Is
    * <code>null</code> if unknown.
    */
   private final String _subjectClass;

   /**
    * The name of the method which exposes the programming error, or
    * <code>null</code> if unknown.
    */
   private final String _subjectMethod;

   /**
    * The detail message. Can be <code>null</code>.
    */
   private final String _detail;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the name of the class that detected the problem.
    *
    * @return
    *    the name of the class that detected the problem, or <code>null</code>
    *    if unknown.
    *
    * @since XINS 1.3.0
    */
   public String getDetectingClass() {
      return _detectingClass;
   }

   /**
    * Returns the name of the method where the problem was detected.
    *
    * @return
    *    the name of the method that detected the problem, or
    *    <code>null</code> if unknown.
    *
    * @since XINS 1.3.0
    */
   public String getDetectingMethod() {
      return _detectingMethod;
   }

   /**
    * Returns the name of the class which exposes the programming error.
    *
    * @return
    *    the name of the class that exposed the problem, or
    *    <code>null</code> if unknown.
    *
    * @since XINS 1.3.0
    */
   public String getSubjectClass() {
      return _subjectClass;
   }

   /**
    * Returns the name of the method which exposes the programming error.
    *
    * @return
    *    the name of the method that exposed the problem, or
    *    <code>null</code> if unknown.
    *
    * @since XINS 1.3.0
    */
   public String getSubjectMethod() {
      return _subjectMethod;
   }

   /**
    * Returns the detail message.
    *
    * @return
    *    the detail message, can be <code>null</code>.
    *
    * @since XINS 1.3.0
    */
   public String getDetail() {
      return _detail;
   }
}
