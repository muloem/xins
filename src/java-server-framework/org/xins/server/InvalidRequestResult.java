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
      super(DefaultResultCodes._INVALID_REQUEST.getValue());
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

   /**
    * Adds an invalid combination of parameters.
    *
    * @param type
    *    the type of the combination.
    *
    * @param elements
    *    list of the elements in the combination passed as a list of
    *    {@link String} objects.
    */
   public void addParamCombo(String type, List elements) {

      CallResultBuilder builder = getResultBuilder();

      builder.startTag("param-combo");
      builder.attribute("type", type);

      // Iterate ober all elements
      Iterator itElements = elements.iterator();
      while(itElements.hasNext()) {
         builder.startTag("param");
         builder.attribute("name", (String) itElements.next());
         builder.endTag();
      }

      builder.endTag();
   }
}
