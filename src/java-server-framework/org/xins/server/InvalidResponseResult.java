/*
 * $Id$
 *
 * Copyright 2003-2006 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.util.Iterator;
import java.util.List;
import org.xins.common.xml.ElementBuilder;

/**
 * Result code that indicates that an output parameter is either missing or
 * invalid.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 *
 * @since XINS 1.0.0
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

   /**
    * Constructs a new <code>InvalidResponseResult</code> object.
    */
   public InvalidResponseResult() {
      super(DefaultResultCodes._INVALID_RESPONSE.getValue());
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
      ElementBuilder missingParam = new ElementBuilder("missing-param");
      missingParam.setAttribute("param", parameter);
      add(missingParam.createElement());
   }

   /**
    * Adds to the response a parameter that is missing in an element.
    *
    * @param parameter
    *    the missing parameter.
    *
    * @param element
    *    the element in which the parameter is missing.
    */
   public void addMissingParameter(String parameter, String element) {
      ElementBuilder missingParam = new ElementBuilder("missing-param");
      missingParam.setAttribute("param", parameter);
      missingParam.setAttribute("element", element);
      add(missingParam.createElement());
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
      ElementBuilder invalidValue = new ElementBuilder("invalid-value-for-type");
      invalidValue.setAttribute("param", parameter);
      invalidValue.setAttribute("type", type);
      add(invalidValue.createElement());
   }

   /**
    * Adds an invalid value for a specified type.
    *
    * @param parameter
    *    the parameter passed by the user.
    *
    * @param type
    *    the type which this parameter should be compliant with.
    *
    * @param element
    *    the element in which the parameter is missing.
    */
   public void addInvalidValueForType(String parameter, String type, String element) {
      ElementBuilder invalidValue = new ElementBuilder("invalid-value-for-type");
      invalidValue.setAttribute("param", parameter);
      invalidValue.setAttribute("type", type);
      invalidValue.setAttribute("element", element);
      add(invalidValue.createElement());
   }

   /**
    * Adds an invalid combination of parameters.
    *
    * @param type
    *    the type of the combination.
    *
    * @param parameters
    *    list of the parameters in the combination passed as a list of
    *    {@link String} objects.
    */
   public void addParamCombo(String type, List parameters) {

      ElementBuilder paramCombo = new ElementBuilder("param-combo");
      paramCombo.setAttribute("type", type);

      // Iterate over all parameters
      Iterator itParameters = parameters.iterator();
      while(itParameters.hasNext()) {
         ElementBuilder param = new ElementBuilder("param");
         param.setAttribute("name", (String) itParameters.next());
         paramCombo.addChild(param.createElement());
      }

      add(paramCombo.createElement());
   }

   /**
    * Adds an invalid combination of attributes.
    *
    * @param type
    *    the type of the combination.
    *
    * @param attributes
    *    list of the attributes in the combination passed as a list of
    *    {@link String} objects.
    *
    * @param elementName
    *    the name of the element to which these attributes belong.
    *
    * @since XINS 1.4.0
    */
   public void addAttributeCombo(String type, List attributes, String elementName) {

      ElementBuilder attributeCombo = new ElementBuilder("attribute-combo");
      attributeCombo.setAttribute("type", type);

      // Iterate over all attributes
      Iterator itAttributes = attributes.iterator();
      while(itAttributes.hasNext()) {
         ElementBuilder attribute = new ElementBuilder("attribute");
         attribute.setAttribute("name", (String) itAttributes.next());
         attributeCombo.addChild(attribute.createElement());
      }

      add(attributeCombo.createElement());
   }
}
