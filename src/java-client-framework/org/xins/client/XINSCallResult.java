/*
 * $Id$
 *
 * Copyright 2004 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client;

import org.xins.common.MandatoryArgumentChecker;

import org.xins.common.service.CallExceptionList;
import org.xins.common.service.CallResult;
import org.xins.common.service.TargetDescriptor;

import org.xins.common.collections.PropertyReader;

/**
 * Result of a call to a XINS service.
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

      super(request, succeededTarget, duration, exceptions);

      // Check preconditions
      MandatoryArgumentChecker.check("request",         request,
                                     "succeededTarget", succeededTarget,
                                     "data",            data);
      // TODO: Check all arguments at once, before calling superconstructor

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
    * Returns the error code. If the result was successful, then no error code
    * is returned. In this case this method will return <code>null</code>.
    *
    * @return
    *    the error code or <code>null</code> if no code was returned.
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
    * Returns the optional extra data. The data is an XML {@link DataElement}, or
    * <code>null</code>.
    *
    * @return
    *    the extra data as an XML {@link DataElement}, can be <code>null</code>;
    */
   public DataElement getDataElement() {

      return _data.getDataElement();
   }
}
