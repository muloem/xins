/*
 * $Id$
 */
package org.xins.server;

import java.util.Iterator;
import java.util.List;

/**
 * Result code that indicates that an output parameter is either missing or invalid.
 *
 * @version $Revision$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public class InvalidResponseResult extends FunctionResult {

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   public InvalidResponseResult() {
      super(false, DefaultResultCodes.INVALID_RESPONSE.getValue());
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
      getResultBuilder().startTag("missing-param");
      getResultBuilder().attribute("param", parameter);
      getResultBuilder().endTag();
   }

   /**
    * Adds an invalid value for a specified type.
    *
    * @param parameter
    *    the parameter passed by the user.
    * @param type
    *    the type which this parameter should be compliant with.
    */
   public void addInvalidTypeForValue(String parameter, String type) {
      getResultBuilder().startTag("invalid-value-for-type");
      getResultBuilder().attribute("param", parameter);
      getResultBuilder().attribute("type", type);
      getResultBuilder().endTag();
   }
}
