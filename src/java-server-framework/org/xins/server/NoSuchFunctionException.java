/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import org.xins.common.MandatoryArgumentChecker;

/**
 * Exception that indicates that there is no function matching the request.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.0.0
 */
public final class NoSuchFunctionException
extends Exception {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Creates the error message for this exception.
    *
    * @param functionName
    *    the name of the function, or <code>null</code>.
    *
    * @return
    *    the error message, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>functionName == null</code>.
    */
   private static String createMessage(String functionName)
   throws IllegalArgumentException {

      // Function name specified
      MandatoryArgumentChecker.check("functionName", functionName);

      return "The function \"" + functionName + "\" cannot be found.";
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>NoSuchFunctionException</code> for the specified
    * function name.
    *
    * @param functionName
    *    the name, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>functionName == null</code>.
    */
   NoSuchFunctionException(String functionName)
   throws IllegalArgumentException {
      super(createMessage(functionName));
      _functionName = functionName;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The name of the function.
    */
   private final String _functionName;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Gets the name of the function.
    *
    * @return
    *    the name of the function, never <code>null</code>.
    */
   public String getFunctionName() {
      return _functionName;
   }
}
