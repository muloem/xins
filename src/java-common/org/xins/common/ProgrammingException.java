/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
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
      super(createMessage(detectingClass,detectingMethod,
                          subjectClass,
                          subjectMethod,
                          detail,
                          cause));
      ExceptionUtils.setCause(this, cause);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
