/*
 * $Id$
 */
package org.xins.server;

/**
 * Exception that indicates that you are trying to get the value of a parameter
 * that has not been set.
 *
 * @version $Revision$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 *
 * @since XINS 0.170
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
    *    the parameter that the user tries to get, cannot be <code>null</code>.
    */
   public ParameterNotInitializedException(String paramName) {
      super(paramName);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}