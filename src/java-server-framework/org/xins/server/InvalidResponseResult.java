/*
 * $Id$
 */
package org.xins.server;

/**
 * Result code that indicates that an output parameter is either missing or invalid.
 *
 * @version $Revision$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
public final class InvalidResponseResult extends FunctionResult {

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>InvalidResponseResult</code> for the specified
    * function.
    *
    * @param functionName
    *    the name of the function, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>functionName == null</code>.
    */
   public InvalidResponseResult(String functionName) {
      super(functionName, DefaultResultCodes._INVALID_RESPONSE.getValue());
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Adds to the response that a paramater that is missing.
    *
    * @param parameter
    *    the missing parameter.
    */
   public void addMissingParameter(String parameter) {
      CallResultBuilder builder = getResultBuilder();
      builder.startTag("missing-param");
      builder.attribute("param", parameter);
      builder.endTag();
   }

   /**
    * Adds an invalid value for a specified type.
    *
    * @param parameter
    *    the parameter passed by the user.
    * @param type
    *    the type which this parameter should be compliant with.
    */
   public void addInvalidValueForType(String parameter, String type) {
      CallResultBuilder builder = getResultBuilder();
      builder.startTag("invalid-value-for-type");
      builder.attribute("param", parameter);
      builder.attribute("type", type);
      builder.endTag();
   }
}
