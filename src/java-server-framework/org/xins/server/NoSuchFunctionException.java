/*
 * $Id$
 *
 * Copyright 2004 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

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
    */
   private static String createMessage(String functionName) {

      // Function name specified
      if (functionName != null) {
         return "The function \"" + functionName + "\" cannot be found.";

      // Function name not specified
      } else {
         return "An unspecified function cannot be found.";
      }
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>NoSuchFunctionException</code> for the specified
    * function name.
    *
    * @param functionName
    *    the name, or <code>null</code>.
    */
   NoSuchFunctionException(String functionName) {
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
    *    the name of the function, or <code>null</code> if no function was provided.
    */
   public String getFunctionName() {
      return _functionName;
   }
}
