/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client;

import java.util.List;

import org.xins.common.MandatoryArgumentChecker;

import org.xins.common.service.TargetDescriptor;

import org.xins.common.text.FastStringBuffer;

/**
 * Exception thrown to indicate a standard error code was received that
 * indicates the request from the client-side is considered invalid by the
 * server-side.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.2.0
 */
public class InvalidRequestException
extends StandardErrorCodeException {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Delegate for the constructor that determines the detail message based on
    * a <code>XINSCallResultData</code> object.
    *
    * @param result
    *    the {@link XINSCallResultData} instance, should not be
    *    <code>null</code>.
    *
    * @return
    *    the detail message for the constructor to use, never
    *    <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>result == null
    *          || result.{@link XINSCallResultData#getErrorCode() getErrorCode()} == null</code>.
    */
   private static final String determineDetail(XINSCallResultData result)
   throws IllegalArgumentException {

      // Argument cannot be null
      MandatoryArgumentChecker.check("result", result);

      // Result must be unsuccessful
      String errorCode = result.getErrorCode();
      if (errorCode == null) {
         throw new IllegalArgumentException("result.getErrorCode() == null");
      }

      // Parse the data element
      DataElement element = result.getDataElement();
      return createMessage(element);
   }

   /**
    * Creates the message containing the details of the error.
    *
    * @param element
    *    the {@link DataElement} containing the details of the error, can be
    *    <code>null</code>.
    *
    * @return
    *    the message or <code>null</code> if <code>element == null</code>
    *    or empty.
    */
   static String createMessage(DataElement element) {
      
      // Parse the data element
      if (element == null) {
         return null;
      }

      FastStringBuffer detail = new FastStringBuffer(250);

      // Handle all missing parameters
      List missingParamElements = element.getChildElements("missing-param");
      if (missingParamElements != null) {
         int size = missingParamElements.size();
         for (int i = 0; i < size; i++) {
            DataElement e = (DataElement) missingParamElements.get(i);
            String paramName = e.getAttribute("param");
            String elementName = e.getAttribute("element");
            if (elementName == null && paramName != null && paramName.length() >= 1) {
               detail.append("No value given for required parameter \""
                           + paramName
                           + "\". ");
            } else if (elementName != null &&  elementName.length() >= 1 && paramName != null && paramName.length() >= 1) {
               detail.append("No value given for required attribute \""
                           + paramName
                           + "\" in the element \""
                           + elementName
                           + "\". ");
            }
         }
      }

      // Handle all invalid parameter values
      List invalidValueElements = element.getChildElements("invalid-value-for-type");
      if (invalidValueElements != null) {
         int size = invalidValueElements.size();
         for (int i = 0; i < size; i++) {
            DataElement e = (DataElement) invalidValueElements.get(i);
            String paramName = e.getAttribute("param");
            String typeName      = e.getAttribute("type");
            String elementName   = e.getAttribute("element");
            if (paramName != null && paramName.length() >= 1) {
               detail.append("The value for parameter \""
                           + paramName
                           + "\" is considered invalid for the type \""
                           + typeName
                           + "\". ");
            } else if (elementName != null &&  elementName.length() >= 1 && paramName != null && paramName.length() >= 1) {
               detail.append("The value for attribute \""
                           + paramName
                           + "\" in the element \""
                           + elementName
                           + "\" is considered invalid for the type \""
                           + typeName
                           + "\". ");
            }
            // XXX: Actual value is not specified in the message
         }
      }

      // Handle all param-combo values
      List paramComboElements = element.getChildElements("param-combo");
      if (paramComboElements != null) {
         int size = paramComboElements.size();

         // Loop through all param-combo elements
         for (int i = 0; i < size; i++) {
            DataElement e = (DataElement) paramComboElements.get(i);

            // There should be a 'type' attribute
            String typeName = e.getAttribute("type");
            if (typeName == null || typeName.trim().length() < 1) {
               // TODO: Log?
               continue;
            }

            // There should be at least 2 'param' elements
            List paramList = e.getChildElements("param");
            if (paramList == null || paramList.size() < 2) {
               // TODO: Log?
               continue;
            }

            // Create detail message
            detail.append("Violated param-combo constraint of type \"");
            detail.append(typeName);
            detail.append("\" on parameters ");
            int paramCount = paramList.size();
            for (int j = 0; j < paramCount; j++) {
               DataElement e2 = (DataElement) paramList.get(j);
               String paramName = e2.getAttribute("name");
               if (paramName == null || paramName.trim().length() < 1) {
                  // TODO: Log?
                  continue;
               }

               detail.append("\"");
               detail.append(paramName);
               detail.append("\"");

               if (j == (paramCount - 1)) {
                  detail.append(". ");
               } else if (j == (paramCount - 2)) {
                  detail.append(" and ");
               } else {
                  detail.append(", ");
               }
            }
         }
      }
      
      // Remove the last space from the string, if there is any
      if (detail.getLength() >= 1) {
         detail.crop(detail.getLength() - 1);
         return detail.toString();
      } else {
         return null;
      }
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>InvalidRequestException</code>.
    *
    * @param request
    *    the original request, guaranteed not to be <code>null</code>.
    *
    * @param target
    *    the target on which the request was executed, guaranteed not to be
    *    <code>null</code>.
    *
    * @param duration
    *    the call duration, guaranteed to be &gt;= <code>0L</code>.
    *
    * @param resultData
    *    the data returned from the call, guaranteed to be <code>null</code>
    *    and must have an error code set.
    *
    * @throws IllegalArgumentException
    *    if <code>result == null
    *          || result.{@link XINSCallResultData#getErrorCode() getErrorCode()} == null</code>.
    */
   InvalidRequestException(XINSCallRequest    request,
                           TargetDescriptor   target,
                           long               duration,
                           XINSCallResultData resultData)
   throws IllegalArgumentException {
      super(request, target, duration, resultData,
            determineDetail(resultData));

      // TODO for XINS 1.3: Parse details
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
