/*
 * $Id$
 *
 * Copyright 2003-2008 Online Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client;

import java.util.Iterator;
import java.util.List;

import org.xins.common.xml.Element;
import org.xins.common.xml.ElementBuilder;

/**
 * Exception that indicates that a request for an API call is considered
 * unacceptable on the application-level. For example, a mandatory input
 * parameter may be missing.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 2.0
 */
public class UnacceptableMessageException extends XINSCallException {

   // TODO: Support XINSCallRequest objects?
   // TODO: Is the name UnacceptableRequestException okay?

   /**
    * The DataElement containing the errors.
    */
   private Element _errors = new Element("data");

   /**
    * The error message.
    */
   private String _message;

   /**
    * Constructs a new <code>UnacceptableMessageException</code> using the
    * specified <code>AbstractCAPICallRequest</code>.
    *
    * @param request
    *    the {@link AbstractCAPICallRequest} that is considered unacceptable,
    *    cannot be <code>null</code>.
    */
   UnacceptableMessageException(XINSCallRequest request) {
      super("Invalid request", request, null, 0L, null, null);
   }

   /**
    * Constructs a new <code>UnacceptableMessageException</code> using the
    * specified <code>XINSCallResult</code>.
    * This constructor is used by the generated CAPI.
    *
    * @param result
    *    the {@link XINSCallResult} that is considered unacceptable,
    *    cannot be <code>null</code>.
    */
   public UnacceptableMessageException(XINSCallResult result) {
      super("Invalid result", result, null, null);
   }

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
    * Adds to the response that a paramater that is missing.
    *
    * @param parameter
    *    the missing parameter.
    */
   public void addMissingParameter(String parameter) {
      ElementBuilder missingParam = new ElementBuilder("missing-param");
      missingParam.setAttribute("param", parameter);
      _errors.addChild(missingParam.createElement());
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
      _errors.addChild(missingParam.createElement());
   }

   /**
    * Adds an invalid value for a specified type.
    *
    * @param parameter
    *    the name of the parameter passed by the user.
    *
    * @param type
    *    the type which this parameter should be compliant with.
    *
    * @deprecated since XINS 2.0, use {@link #addInvalidValueForType(String, String, String)}.
    */
   public void addInvalidValueForType(String parameter, String type) {
      ElementBuilder invalidValue = new ElementBuilder("invalid-value-for-type");
      invalidValue.setAttribute("param", parameter);
      invalidValue.setAttribute("type", type);
      _errors.addChild(invalidValue.createElement());
   }

   /**
    * Adds an invalid value for a specified type.
    *
    * @param parameter
    *    the name of the parameter passed by the user.
    *
    * @param value
    *    the value of the parameter passed by the user.
    *
    * @param type
    *    the type which this parameter should be compliant with.
    *
    * @since XINS 2.0
    */
   public void addInvalidValueForType(String parameter, String value, String type) {
      ElementBuilder invalidValue = new ElementBuilder("invalid-value-for-type");
      invalidValue.setAttribute("param", parameter);
      invalidValue.setAttribute("value", value);
      invalidValue.setAttribute("type", type);
      _errors.addChild(invalidValue.createElement());
   }

   /**
    * Adds an invalid value for a specified type.
    *
    * @param parameter
    *    the parameter passed by the user.
    *
    * @param value
    *    the value of the parameter passed by the user.
    *
    * @param type
    *    the type which this parameter should be compliant with.
    *
    * @param element
    *    the element in which the parameter is missing.
    *
    * @since XINS 2.0
    */
   public void addInvalidValueForType(String parameter, String value, String type, String element) {
      ElementBuilder invalidValue = new ElementBuilder("invalid-value-for-type");
      invalidValue.setAttribute("param", parameter);
      invalidValue.setAttribute("value", value);
      invalidValue.setAttribute("type", type);
      invalidValue.setAttribute("element", element);
      _errors.addChild(invalidValue.createElement());
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

      _errors.addChild(paramCombo.createElement());
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

      _errors.addChild(attributeCombo.createElement());
   }
}
