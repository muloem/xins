/*
 * $Id$
 *
 * Copyright 2004 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import org.xins.common.MandatoryArgumentChecker;

/**
 * Exception that indicates that it is attempted to get the value of a
 * parameter that has not been set.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 *
 * @since XINS 1.0.0
 */
public class ParameterNotInitializedException extends RuntimeException {

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
    * Constructs a new <code>ParameterNotInitializedException</code> for the
    * specified parameter.
    *
    * @param paramName
    *    the name of the parameter that is attempted to be retrieved, cannot
    *    be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>paramName == null</code>.
    */
   public ParameterNotInitializedException(String paramName)
   throws IllegalArgumentException {
      super(paramName);

      // Check argument
      MandatoryArgumentChecker.check("paramName", paramName);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
