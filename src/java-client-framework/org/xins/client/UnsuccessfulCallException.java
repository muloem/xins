/*
 * $Id$
 */
package org.xins.client;

import org.jdom.Element;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.collections.PropertyReader;
import org.xins.common.service.TargetDescriptor;
import org.xins.common.text.FastStringBuffer;

/**
 * Exception that indicates that data was not received on a socket within a
 * designated time-out period.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 0.36
 */
public final class UnsuccessfulCallException extends CallException {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Checks the arguments for the constructor and then returns the short
    * reason.
    *
    * @param request
    *    the original request, cannot be <code>null</code>.
    *
    * @param target
    *    descriptor for the target that was attempted to be called, cannot be
    *    <code>null</code>.
    *
    * @param duration
    *    the call duration in milliseconds, must be &gt;= 0.
    *
    * @param result
    *    the call result that is unsuccessful, cannot be <code>null</code>,
    *    and
    *    <code>result.</code>{@link XINSServiceCaller.Result#isSuccess() isSuccess()}
    *    should be <code>false</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>request     == null
    *          || target      == null
    *          || result      == null
    *          || result.{@link XINSServiceCaller.Result#getErrorCode() getErrorCode()} == null
    *          || duration  &lt; 0</code>
    *
    * @since XINS 0.202
    */
   private static final String createDetailMessage(CallRequest              request,
                                                   TargetDescriptor         target,
                                                   long                     duration,
                                                   XINSServiceCaller.Result result)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("request", request,
                                     "target",  target,
                                     "result",  result);
      String code = result.getErrorCode();
      if (code == null) {
         throw new IllegalArgumentException("result.getErrorCode() == null");
      } else if (duration < 0) {
         throw new IllegalArgumentException("duration (" + duration + ") < 0");
      }

      // Construct and return the detail message
      FastStringBuffer buffer = new FastStringBuffer(35, "Error code is \"");
      buffer.append(code);
      buffer.append("\".");
      return buffer.toString();
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>UnsuccessfulCallException</code>.
    *
    * @param result
    *    the result, cannot be <code>null</code> and must be unsuccessful.
    *
    * @throws IllegalArgumentException
    *    if <code>result == null || result.getErrorCode() == null</code>.
    *
    * @since XINS 0.203
    */
   UnsuccessfulCallException(XINSServiceCaller.Result result)
   throws IllegalArgumentException {
      super("Unsuccessful result", result, null, null);

      _result = result;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The call result. The value of this field cannot be <code>null</code>.
    */
   private final XINSServiceCaller.Result _result;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the error code.
    *
    * @return
    *    the error code or <code>null</code> if the call was successful and no
    *    error code was returned.
    *
    * @since XINS 0.181
    */
   public String getErrorCode() {
      return _result.getErrorCode();
   }

   /**
    * Gets all returned parameters.
    *
    * @return
    *    a {@link PropertyReader} containing all parameters, or
    *    <code>null</code> if there are none.
    *
    * @since XINS 0.202
    */
   public PropertyReader getParameters() {
      return _result.getParameters();
   }

   /**
    * Gets the value of the specified returned parameter.
    *
    * @param name
    *    the parameter name, not <code>null</code>.
    *
    * @return
    *    the value of the parameter, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null</code>.
    *
    * @since XINS 0.136
    */
   public String getParameter(String name)
   throws IllegalArgumentException {
      return _result.getParameter(name);
   }

   /**
    * Returns the optional extra data. The data is an XML {@link Element}, or
    * <code>null</code>.
    *
    * @return
    *    the extra data as an XML {@link Element}, can be <code>null</code>;
    *    if it is not <code>null</code>, then
    *    <code><em>return</em>.{@link Element#getName() getName()}.equals("data") &amp;&amp; <em>return</em>.{@link Element#getNamespace() getNamespace()}.equals({@link org.jdom.Namespace#NO_NAMESPACE NO_NAMESPACE})</code>.
    *
    * @since XINS 0.136
    */
   public Element getDataElement() {
      // TODO: Do not return a JDOM Element
      return _result.getDataElement();
   }
}
