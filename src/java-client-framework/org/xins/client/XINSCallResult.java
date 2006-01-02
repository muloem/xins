/*
 * $Id$
 *
 * Copyright 2003-2006 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client;

import org.xins.common.MandatoryArgumentChecker;

import org.xins.common.service.CallExceptionList;
import org.xins.common.service.CallResult;
import org.xins.common.service.TargetDescriptor;

import org.xins.common.collections.PropertyReader;

/**
 * Successful result of a call to a XINS service. It may be that some targets
 * failed before a target returned a successful result. All the failures are
 * also stored in this object.
 *
 * <p>When a <code>XINSCallResult</code> instance is created, information must
 * be passed both about the successful call (which target successfully
 * returned a result, how long did it take, what was the result) and about the
 * unsuccessful calls (to which targets were they, what was the error, etc.)
 *
 * <p>While a {@link XINSCallResultData} object describes the result of a call
 * to an single target, a <code>XINSCallResultData</code> also describes all
 * failed calls that happened before.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.0.0
 */
public final class XINSCallResult
extends CallResult
implements XINSCallResultData {

   //----------------------------------------------------------------------
   // Class fields
   //----------------------------------------------------------------------

   //----------------------------------------------------------------------
   // Class functions
   //----------------------------------------------------------------------

   /**
    * Checks the constructor arguments that cannot be <code>null</code>.
    *
    * @param request
    *    the call request that resulted in this result, cannot be
    *    <code>null</code>.
    *
    * @param succeededTarget
    *    the target for which the call succeeded, cannot be <code>null</code>.
    *
    * @param data
    *    the {@link XINSCallResultData} object returned from the call, cannot
    *    be <code>null</code>.
    *
    * @return
    *    the argument <code>request</code>, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>request         ==   null
    *          || succeededTarget ==   null
    *          || data            ==   null</code>.
    */
   private static XINSCallRequest checkArguments(
      XINSCallRequest    request,
      TargetDescriptor   succeededTarget,
      XINSCallResultData data) throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("request",         request,
                                     "succeededTarget", succeededTarget,
                                     "data",            data);

      return request;
   }


   //----------------------------------------------------------------------
   // Constructors
   //----------------------------------------------------------------------

   /**
    * Constructs a new <code>XINSCallResult</code> object.
    *
    * @param request
    *    the original {@link XINSCallRequest} that was used to perform the
    *    call, cannot be <code>null</code>.
    *
    * @param succeededTarget
    *    the {@link TargetDescriptor} that was used to successfully get the
    *    result, cannot be <code>null</code>.
    *
    * @param duration
    *    the call duration, should be &gt;= 0.
    *
    * @param exceptions
    *    the list of {@link org.xins.common.service.CallException}s, collected
    *    in a {@link CallExceptionList} object, or <code>null</code> if the
    *    first call attempt succeeded.
    *
    * @param data
    *    the {@link XINSCallResultData} returned from the call, cannot be
    *    <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>request         ==   null
    *          || succeededTarget ==   null
    *          || data            ==   null
    *          || duration        &lt; 0</code>.
    */
   XINSCallResult(XINSCallRequest    request,
                  TargetDescriptor   succeededTarget,
                  long               duration,
                  CallExceptionList  exceptions,
                  XINSCallResultData data)

   throws IllegalArgumentException {

      super(checkArguments(request, succeededTarget, data),
            succeededTarget, duration, exceptions);

      _data = data;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The <code>XINSCallResultData</code> object that contains all the
    * information returned from the call. This field cannot be
    * <code>null</code>.
    */
   private final XINSCallResultData _data;


   //----------------------------------------------------------------------
   // Methods
   //----------------------------------------------------------------------

   /**
    * Returns the error code. If <code>null</code> is returned the call was
    * successful and thus no error code was returned. Otherwise the call was
    * unsuccessful.
    *
    * <p>This method will never return an empty string, so if the result is
    * not <code>null</code>, then it is safe to assume the length of the
    * string is at least 1 character.
    *
    * @return
    *    the returned error code, or <code>null</code> if the call was
    *    successful.
    */
   public String getErrorCode() {
      return _data.getErrorCode();
   }

   /**
    * Gets all parameters.
    *
    * @return
    *    a {@link PropertyReader} with all parameters, or <code>null</code>
    *    if there are none.
    */
   public PropertyReader getParameters() {
      return _data.getParameters();
   }

   /**
    * Gets the value of the specified parameter.
    *
    * @param name
    *    the parameter element name, not <code>null</code>.
    *
    * @return
    *    string containing the value of the parameter element,
    *    or <code>null</code> if the parameter has no value.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null</code>.
    */
   public String getParameter(String name)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("name", name);

      PropertyReader params = getParameters();

      // Short-circuit if there are no parameters at all
      if (params == null) {
         return null;
      }

      // Otherwise return the parameter value
      return params.get(name);
   }

   /**
    * Returns the optional extra data. The data is an XML {@link DataElement},
    * or <code>null</code>.
    *
    * @return
    *    the extra data as an XML {@link DataElement}, can be
    *    <code>null</code>.
    */
   public DataElement getDataElement() {
      return _data.getDataElement();
   }
}
