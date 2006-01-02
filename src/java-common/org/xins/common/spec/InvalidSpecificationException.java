/*
 * $Id$
 *
 * Copyright 2003-2006 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.spec;

import org.xins.logdoc.ExceptionUtils;

/**
 * Thrown when the specification of the API is incorrect or cannot be found.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 *
 * @since XINS 1.3.0
 */
public class InvalidSpecificationException extends Exception {

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Creates a new <code>InvalidSpecificationException</code> with the reason
    * of the problem.
    *
    * @param message
    *    the reason why this exception has been thrown, can be <code>null</code>.
    */
   InvalidSpecificationException(String message) {
      this(message, null);
   }

   /**
    * Creates a new <code>InvalidSpecificationException</code> with the reason
    * of the problem.
    *
    * @param message
    *    the reason why this exception has been thrown, can be <code>null</code>.
    *
    * @param cause
    *    the cause of the exception, can be <code>null</code>.
    */
   InvalidSpecificationException(String message, Throwable cause) {
      super(message);
      if (cause != null) {
         ExceptionUtils.setCause(this, cause);
      }
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

}
