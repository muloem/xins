/*
 * $Id$
 */
package org.xins.client;

import org.xins.common.MandatoryArgumentChecker;

import org.xins.common.service.CallException;
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
 * @since XINS 0.207
 */
public final class XINSCallResult extends CallResult {

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
    *    the list of {@link CallException}s, collected in a
    *    {@link CallExceptionList} object, or <code>null</code> if the first
    *    call attempt succeeded.
    *
    * @param data
    *    the {@link Data} returned from the call, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>request         ==   null
    *          || succeededTarget ==   null
    *          || data            ==   null
    *          || duration        &lt; 0</code>.
    */
   XINSCallResult(XINSCallRequest   request,
                  TargetDescriptor  succeededTarget,
                  long              duration,
                  CallExceptionList exceptions,
                  Data              data)

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
    * The <code>Data</code> object that contains the information returned from
    * the call. This field cannot be <code>null</code>.
    */
   private final Data _data;


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
    *    not <code>null</code>.
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


   //----------------------------------------------------------------------
   // Inner classes
   //----------------------------------------------------------------------

   /**
    * Data part of a XINS call result.
    *
    * @version $Revision$ $Date$
    * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
    *
    * @since XINS 0.207
    */
   static final class Data extends Object {

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      /**
       * Constructs a new <code>Data</code> object.
       *
       * @param code
       *    the error code returned, if any, or <code>null</code> if none was
       *    returned.
       *
       * @param parameters
       *    output parameters returned by the function, or <code>null</code>.
       *
       * @param dataElement
       *    the data element returned by the function, or <code>null</code>;
       *    if specified then the name must be <code>"data"</code>, with no
       *    namespace.
       */
      Data(String         code,
           PropertyReader parameters,
           DataElement    dataElement) {

         _code        = code;
         _parameters  = parameters;
         _dataElement = dataElement;
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      /**
       * The error code. This field is <code>null</code> if the call was
       * successful and thus no error code was returned.
       */
      private final String _code;

      /**
       * The parameters and their values. This field is never <code>null</code>.
       */
      private final PropertyReader _parameters;

      /**
       * The data element. This field is <code>null</code> if there is no data
       * element.
       */
      private final DataElement _dataElement;


      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------

      /**
       * Determines if the call was successful or not.
       *
       * @return
       *    <code>true</code> if the call result indicated success,
       *    <code>false</code> otherwise.
       */
      boolean isSuccess() {
         return (_code == null);
      }

      /**
       * Returns the error code. If <code>null</code> is returned the call was
       * successful and thus no error code was returned. Otherwise the call
       * was unsuccessful.
       *
       * @return
       *    the returned error code, or <code>null</code>.
       */
      private String getErrorCode() {
         return _code;
      }

      /**
       * Gets all parameters.
       *
       * @return
       *    a {@link PropertyReader} with all parameters, or <code>null</code>
       *    if there are none.
       */
      public PropertyReader getParameters() {
         return _parameters;
      }

      /**
       * Returns the optional extra data. The data is an XML
       * {@link DataElement}, or <code>null</code>.
       *
       * @return
       *    the extra data as an XML {@link DataElement}, can be
       *    <code>null</code>;
       */
      public DataElement getDataElement() {
         return _dataElement;
      }
   }
}
