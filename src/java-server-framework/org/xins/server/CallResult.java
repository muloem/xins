/*
 * $Id$
 */
package org.xins.server;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.collections.PropertyReader;

/**
 * A call result.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 0.119
 */
abstract class CallResult {

   //------------------------------------------------------------------------
   // Class fields
   //------------------------------------------------------------------------

   //------------------------------------------------------------------------
   // Class functions
   //------------------------------------------------------------------------

   //------------------------------------------------------------------------
   // Constructors
   //------------------------------------------------------------------------

   /**
    * Constructs a new <code>CallResult</code> object.
    *
    * @param functionName
    *    the name of the called function, cannot be <code>null</code>.
    */
   CallResult(String functionName) throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("functionName", functionName);

      // Store function name
      _functionName = functionName;
   }

   //------------------------------------------------------------------------
   // Fields
   //------------------------------------------------------------------------

   /**
    * The function name.
    */
   private String _functionName;

   /**
    * The function call ID.
    */
   private int _callID;

   /**
    * The function call duration, in milliseconds.
    */
   private long _duration;


   //------------------------------------------------------------------------
   // Methods
   //------------------------------------------------------------------------

   /**
    * Returns the success indication.
    *
    * @return
    *    success indication, <code>true</code> or <code>false</code>.
    */
   public abstract boolean isSuccess();

   /**
    * Returns the result code.
    *
    * @return
    *    the result code or <code>null</code> if no code was returned.
    */
   public abstract String getErrorCode();

   /**
    * Gets all parameters.
    *
    * @return
    *    a {@link PropertyReader} containing all parameters, or
    *    <code>null</code> if no parameters are set; the keys will be the
    *    names of the parameters ({@link String} objects, cannot be
    *    <code>null</code>), the values will be the parameter values
    *    ({@link String} objects as well, cannot be <code>null</code>).
    */
   public abstract PropertyReader getParameters();

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
   public abstract String getParameter(String name) throws IllegalArgumentException;

   /**
    * Returns the optional extra data. The data is an XML {@link Element}, or
    * <code>null</code>.
    *
    * @return
    *    the extra data as an XML {@link Element}, can be <code>null</code>;
    *    if it is not <code>null</code>, then
    *    <code><em>return</em>.{@link Element#getType() getType()}.equals("data")</code>.
    */
   public abstract Element getDataElement();

   /**
    * Returns the name of the called function.
    *
    * @return
    *    the function name, cannot be <code>null</code>.
    */
   final String getFunctionName() {
      return _functionName;
   }

   /**
    * Stores the call ID.
    *
    * @param callID
    *    the function call ID.
    */
   final void setCallID(int callID) {
      _callID = callID;
   } 

   /**
    * Returns the call ID.
    *
    * @return
    *    the call ID.
    */
   final int getCallID() {
      return _callID;
   }

   /**
    * Stores the call duration.
    *
    * @param duration
    *    the duration of the function call, in milliseconds.
    */
   final void setDuration(long duration) {
      _duration = duration;
   } 

   /**
    * Returns the call duration.
    *
    * @return
    *    the duration of the function call, in milliseconds.
    */
   final long getDuration() {
      return _duration;
   }
}
