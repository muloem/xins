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
 * Exception thrown to indicate an <code>_InvalidRequest</code> error code was
 * received.
 *
 * <p>Although this class is currently package-private, it is expected to be
 * marked as public at some point.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
class InvalidRequestException
extends UnsuccessfulXINSCallException {

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
      if (element == null) {
         return null;
      }

      FastStringBuffer detail = new FastStringBuffer(50);

      // Handle all missing parameters
      List missingParamElements = element.getChildElements("missing-param");
      if (missingParamElements != null && missingParamElements.size() >= 1) {
         int size = missingParamElements.size();
         for (int i = 0; i < size; i++) {
            DataElement e = (DataElement) missingParamElements.get(i);
            String parameterName = e.getAttribute("param");
            if (parameterName != null && parameterName.length() >= 1) {
               detail.append("No value given for required parameter \""
                           + parameterName
                           + "\". ");
            }
         }
      }

      // TODO: Handle all invalid parameter values

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

      // TODO: Parse details
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   // TODO: Add methods for retrieval of details

   //-------------------------------------------------------------------------
   // Inner classes
   //-------------------------------------------------------------------------

   /**
    * Issue as returned in the call result.
    *
    * @version $Revision$ $Date$
    * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
    */
   private static abstract class Issue
   extends Object {

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      /**
       * Constructs a new <code>Issue</code> instance.
       *
       * @param description
       *    a textual description of this issue, cannot be <code>null</code>.
       *
       * @throws IllegalArgumentException
       *    if <code>description == null</code>.
       */
      private Issue(String description)
      throws IllegalArgumentException {
         _description = description;
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      /**
       * A textual description of this issue. Cannot be <code>null</code>.
       */
      private final String _description;


      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------

      /**
       * Returns a description of this issue.
       *
       * @return
       *    a description of this issue, never <code>null</code>. Always
       *    a sentence that starts with a capital and ends with a
       *    <code>'.'</code (full stop).
       */
      private final String describe() {
         return _description;
      }
   }

   /**
    * Issue indicating a required parameter is missing.
    *
    * @version $Revision$ $Date$
    * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
    */
   public static final class MissingParameterIssue
   extends Issue {

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      /**
       * Constructs a new <code>MissingParameterIssue</code> instance.
       *
       * @param parameterName
       *    the name of the missing parameter, cannot be <code>null</code>.
       *
       * @throws IllegalArgumentException
       *    if <code>parameterName == null</code>.
       */
      private MissingParameterIssue(String parameterName)
      throws IllegalArgumentException {

         // Call superconstructor
         super("No value given for required parameter \""
             + parameterName
             + "\".");

         // Check precondition
         MandatoryArgumentChecker.check("parameterName", parameterName);
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------
   }
}
