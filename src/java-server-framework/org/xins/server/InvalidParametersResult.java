/*
 * $Id$
 */
package org.xins.server;

import java.util.Iterator;
import java.util.List;

/**
 * State of a <code>Responder</code>.
 *
 * @version $Revision$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public class InvalidParametersResult extends FunctionResult {

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   public InvalidParametersResult() {
      super(false, DefaultResultCodes.INVALID_PARAMETERS.getValue());
   }

   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Set the arguments if the <code>InvalidParametersResult</code> is due to an
    * invalid value for a specified type.
    *
    * @param parameter
    *    the parameter passed by the user.
    * @param type
    *    the type which this parameter should be compliant with.
    */
   public void setInvalidTypeForValue(String parameter, String type) {
      getResultBuilder().startTag("invalid-value-for-type");
      getResultBuilder().attribute("param", parameter);
      getResultBuilder().attribute("type", type);
      getResultBuilder().endTag();
   }

   /**
    * Set the arguments if the <code>InvalidParametersResult</code> is due to an
    * invalid combination of parameters.
    *
    * @param type
    *    the type of the combination.
    * @param elements
    *    list of the elements in the combination passed as a list of <code>String</code>.
    */
   public void setParamCombo(String type, List elements) {
      getResultBuilder().startTag("param-combo");
      getResultBuilder().attribute("type", "inclusive-or");
      Iterator itElements = elements.iterator();
      while(itElements.hasNext()) {
         getResultBuilder().startTag("param");
         getResultBuilder().attribute("name", (String)itElements.next());
         getResultBuilder().endTag();
      }
      getResultBuilder().endTag();
   }
}
