/*
 * $Id$
 *
 * Copyright 2004 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
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

   /**
    * Constructs a new <code>InvalidRequestResult</code> object.
    */
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
      Element missingParam = new Element("missing-param");
      missingParam.addAttribute("param", parameter);
      add(missingParam);
   }

   /**
    * Adds an invalid value for a specified type.
    *
    * @param parameter
    *    the parameter passed by the user.
    *
    * @param type
    *    the type which this parameter should be compliant with.
    */
   public void addInvalidValueForType(String parameter, String type) {
      Element invalidValue = new Element("invalid-value-for-type");
      invalidValue.addAttribute("param", parameter);
      invalidValue.addAttribute("type", type);
      add(invalidValue);
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

      Element paramCombo = new Element("param-combo");
      paramCombo.addAttribute("type", type);

      // Iterate ober all elements
      Iterator itElements = elements.iterator();
      while(itElements.hasNext()) {
         Element param = new Element("param");
         param.addAttribute("name", (String) itElements.next());
         paramCombo.add(param);
      }

      add(paramCombo);
   }
}
