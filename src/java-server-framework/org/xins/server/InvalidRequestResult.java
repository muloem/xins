/*
 * $Id$
 */
package org.xins.server;

import java.util.Iterator;
import java.util.List;

/**
 * Result code that indicates that an input parameter is either missing or invalid.
 *
 * @version $Revision$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public class InvalidRequestResult extends FunctionResult {

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   public InvalidRequestResult() {
      super(false, DefaultResultCodes._INVALID_REQUEST.getValue());
   }

   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Adds to the response a paramater that is missing.
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

   /**
    * Adds an invalid combination of parameters.
    *
    * @param type
    *    the type of the combination.
    * @param elements
    *    list of the elements in the combination passed as a list of <code>String</code>.
    */
   public void addParamCombo(String type, List elements) {
      getResultBuilder().startTag("param-combo");
      getResultBuilder().attribute("type", type);
      Iterator itElements = elements.iterator();
      while(itElements.hasNext()) {
         getResultBuilder().startTag("param");
         getResultBuilder().attribute("name", (String)itElements.next());
         getResultBuilder().endTag();
      }
      getResultBuilder().endTag();
   }
}
