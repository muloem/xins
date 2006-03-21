/*
 * $Id$
 *
 * Copyright 2003-2006 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common;

/**
 * Utility functions related to exceptions.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.0.0
 *
 * @deprecated
 *    Deprecated since XINS 1.2.0.
 *    Use class {@link org.xins.logdoc.ExceptionUtils} instead
 */
public final class ExceptionUtils extends Object {

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

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
   public static Throwable getRootCause(Throwable exception)
   throws IllegalArgumentException {
      return org.xins.logdoc.ExceptionUtils.getRootCause(exception);
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
   // Methods
   //-------------------------------------------------------------------------
}
