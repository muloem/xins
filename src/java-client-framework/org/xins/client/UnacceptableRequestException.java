/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client;

import java.util.Iterator;
import java.util.List;

/**
 * Exception that indicates that a request for an API call is considered
 * unacceptable on the application-level. For example, a mandatory input
 * parameter may be missing.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.2.0
 */
public final class UnacceptableRequestException
extends RuntimeException {

   // TODO: Support XINSCallRequest objects?
   // TODO: Is the name UnacceptableRequestException okay?
   // TODO: Log UnacceptableRequestException! (not in this class though)

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>UnacceptableRequestException</code> using the
    * specified <code>AbstractCAPICallRequest</code>.
    *
    * @param request
    *    the {@link AbstractCAPICallRequest} that is considered unacceptable,
    *    may be <code>null</code>.
    */
   public UnacceptableRequestException(AbstractCAPICallRequest request) {

      // Store the information
      _request    = request;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The request that is considered unacceptable. Never <code>null</code>.
    */
   private final AbstractCAPICallRequest _request;

   /**
    * The DataElement containing the errors.
    */
   private DataElement _errors = new DataElement(null, "data");

   /**
    * The error message.
    */
   private String _message;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the message for this exception.
    *
    * @return
    *    the exception message, can be <code>null</code>.
    */
   public String getMessage() {
      if (_message == null) {
         _message = InvalidRequestException.createMessage(_errors);
      }
      return _message;
   }

   /**
    * Adds to the response a paramater that is missing.
    *
    * @param parameter
    *    the missing parameter.
    */
   public void addMissingParameter(String parameter) {
      DataElement missingParam = new DataElement(null, "missing-param");
      missingParam.setAttribute(null, "param", parameter);
      _errors.addChild(missingParam);
   }

   /**
    * Adds to the response an attribute that is missing in an element.
    *
    * @param attribute
    *    the missing attribute.
    *
    * @param element
    *    the element in which the attribute is missing.
    */
   public void addMissingParameter(String attribute, String element) {
      DataElement missingParam = new DataElement(null, "missing-param");
      missingParam.setAttribute(null, "param", attribute);
      missingParam.setAttribute(null, "element", element);
      _errors.addChild(missingParam);
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
      DataElement invalidValue = new DataElement(null,
                                                 "invalid-value-for-type");
      invalidValue.setAttribute(null, "param", parameter);
      invalidValue.setAttribute(null, "type", type);
      _errors.addChild(invalidValue);
   }

   /**
    * Adds an invalid value for a specified type.
    *
    * @param attribute
    *    the attribute passed by the user.
    *
    * @param type
    *    the type which this parameter should be compliant with.
    *
    * @param element
    *    the element in which the attribute is missing.
    */
   public void addInvalidValueForType(String attribute,
                                      String type,
                                      String element) {

      DataElement invalidValue = new DataElement(null,
                                                 "invalid-value-for-type");
      invalidValue.setAttribute(null, "param", attribute);
      invalidValue.setAttribute(null, "type", type);
      invalidValue.setAttribute(null, "element", element);
      _errors.addChild(invalidValue);
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

      DataElement paramCombo = new DataElement(null, "param-combo");
      paramCombo.setAttribute(null, "type", type);

      // Iterate over all parameters
      Iterator itParameters = parameters.iterator();
      while(itParameters.hasNext()) {
         DataElement param = new DataElement(null, "param");
         param.setAttribute(null, "name", (String) itParameters.next());
         paramCombo.addChild(param);
      }

      _errors.addChild(paramCombo);
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

      DataElement attributeCombo = new DataElement(null, "attribute-combo");
      attributeCombo.setAttribute(null, "type", type);

      // Iterate over all attributes
      Iterator itAttributes = attributes.iterator();
      while(itAttributes.hasNext()) {
         DataElement attribute = new DataElement(null, "attribute");
         attribute.setAttribute(null, "name", (String) itAttributes.next());
         attributeCombo.addChild(attribute);
      }

      _errors.addChild(attributeCombo);
   }
}
