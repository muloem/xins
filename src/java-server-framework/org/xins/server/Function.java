/*
 * $Id$
 */
package org.xins.server;

import java.io.PrintWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import javax.servlet.ServletRequest;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.collections.BasicPropertyReader;
import org.xins.common.collections.PropertyReader;
import org.xins.common.io.FastStringWriter;
import org.xins.common.manageable.Manageable;
import org.xins.common.servlet.ServletRequestPropertyReader;
import org.xins.common.text.FastStringBuffer;

import org.xins.logdoc.AbstractLogdocSerializable;
import org.xins.logdoc.LogdocSerializable;
import org.xins.logdoc.LogdocStringBuffer;

/**
 * Base class for function implementation classes.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
public abstract class Function
extends Manageable
implements DefaultResultCodes {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Call result to be returned when a function is currently disabled. See
    * {@link #isEnabled()}.
    */
   private static final CallResult DISABLED_FUNCTION_RESULT = new BasicCallResult("_DisabledFunction", null, null);


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>Function</code>.
    *
    * @param api
    *    the API to which this function belongs, not <code>null</code>.
    *
    * @param name
    *    the name, not <code>null</code>.
    *
    * @param version
    *    the version of the specification this function implements, not
    *    <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>api == null || name == null || version == null</code>.
    */
   protected Function(API api, String name, String version)
   throws IllegalArgumentException {

      // Check arguments
      MandatoryArgumentChecker.check("api", api, "name", name, "version", version);

      _api          = api;
      _name         = name;
      _version      = version;
      _enabled      = true;

      _api.functionAdded(this);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The API implementation this function is part of.
    */
   protected final API _api;

   /**
    * The name of this function.
    */
   private final String _name;

   /**
    * The version of the specification this function implements.
    */
   private final String _version;

   /**
    * Flag that indicates if this function is currently accessible.
    */
   private boolean _enabled;

   /**
    * Lock object for <code>_callCount</code>.
    */
   private final Object _callCountLock = new Object();

   /**
    * The total number of calls executed up until now.
    */
   private int _callCount;

   /**
    * Statistics object linked to this function.
    */
   private final FunctionStatistics _statistics = new FunctionStatistics();


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the API that contains this function.
    *
    * @return
    *    the {@link API}, not <code>null</code>.
    */
   public final API getAPI() {
      return _api;
   }

   /**
    * Returns the name of this function.
    *
    * @return
    *    the name, not <code>null</code>.
    */
   final String getName() {
      return _name;
   }

   /**
    * Returns the specification version for this function.
    *
    * @return
    *    the version, not <code>null</code>.
    */
   final String getVersion() {
      return _version;
   }

   /**
    * Checks if this function is currently accessible.
    *
    * @return
    *    <code>true</code> if this function is currently accessible,
    *    <code>false</code> otherwise.
    *
    * @since XINS 0.139
    */
   public final boolean isEnabled() {
      return _enabled;
   }

   /**
    * Sets if this function is currently accessible.
    *
    * @param enabled
    *    <code>true</code> if this function should be accessible,
    *    <code>false</code> if not.
    *
    * @since XINS 0.139
    */
   public final void setEnabled(boolean enabled) {
      _enabled = enabled;
   }

   /**
    * Returns the call statistics for this function.
    *
    * @return
    *    the statistics, never <code>null</code>.
    */
   final FunctionStatistics getStatistics() {
      return _statistics;
   }

   /**
    * Assigns a new call ID for the caller. Every call to this method will
    * return an increasing number.
    *
    * @return
    *    the assigned call ID, &gt;= 0.
    */
   final int assignCallID() {
      synchronized (_callCountLock) {
         return _callCount++;
      }
   }

   /**
    * Handles a call to this function (wrapper method). This method will call
    * {@link #handleCall(CallContext context)}.
    *
    * @param start
    *    the start time of the call, as milliseconds since midnight January 1,
    *    1970.
    *
    * @param request
    *    the original servlet request for this call, never <code>null</code>.
    *
    * @return
    *    the call result, never <code>null</code>.
    *
    * @throws IllegalStateException
    *    if this object is currently not initialized.
    */
   CallResult handleCall(long start, ServletRequest request)
   throws IllegalStateException {

      // TODO: Know nothing about servlets, so do not accept the
      //       ServletRequest argument

      // Check state first
      assertUsable();

      // Assign a call ID
      int callID = assignCallID();

      // Check if this function is enabled
      if (!_enabled) {
         performedCall(request, start, callID, DISABLED_FUNCTION_RESULT);
         return DISABLED_FUNCTION_RESULT;
      }

      // Construct a CallContext object
      CallContext context = new CallContext(request, start, this, callID);

      CallResult result;
      try {

         FunctionResult functionResult = handleCall(context);
         result = functionResult.getCallResult();

      } catch (Throwable exception) {

         // TODO: Allow customization of what exceptions are logged?
         Log.log_1513(exception, _name, callID);

         // Create a set of parameters for the result
         BasicPropertyReader parameters = new BasicPropertyReader();

         // Add the exception class
         parameters.set("_exception.class", exception.getClass().getName());

         // Add the exception message, if any
         String exceptionMessage = exception.getMessage();
         if (exceptionMessage != null && exceptionMessage.length() > 0) {
            parameters.set("_exception.message", exceptionMessage);
         }

         // Add the stack trace, if any
         FastStringWriter stWriter = new FastStringWriter();
         PrintWriter printWriter = new PrintWriter(stWriter);
         exception.printStackTrace(printWriter);
         String stackTrace = stWriter.toString();
         if (stackTrace != null && stackTrace.length() > 0) {
            parameters.set("_exception.stacktrace", stackTrace);
         }

         result = new BasicCallResult("_InternalError", parameters, null);
      }

      // Update function statistics
      performedCall(request, start, callID, result);

      return result;
   }

   /**
    * Handles a call to this function.
    *
    * @param context
    *    the context for this call, never <code>null</code>.
    *
    * @return
    *    the result of the call, never <code>null</code>.
    *
    * @throws Throwable
    *    if anything goes wrong.
    */
   protected abstract FunctionResult handleCall(CallContext context)
   throws Throwable;

   /**
    * Callback method that may be called after a call to this function. This
    * method will store statistics-related information.
    *
    * <p />This method does not <em>have</em> to be called. If statistics
    * gathering is disabled, then this method should not be called.
    *
    * @param request
    *    the servlet request, should not be <code>null</code>.
    *
    * @param start
    *    the start time, in milliseconds since January 1, 1970, not
    *    <code>null</code>.
    *
    * @param callID
    *    the assigned call ID.
    *
    * @param result
    *    the function result code, cannot be <code>null</code>.
    */
   private final void performedCall(ServletRequest request,
                                    long           start,
                                    int            callID,
                                    CallResult     result) {

      String ip = request.getRemoteAddr();

      // XXX: Accept input parameters

      // XXX: If the Logging is moved somewhere else then
      //      the method invoking this method (performedCall) can directly
      //      invoke recordCall and this method can be removed.

      long duration = _statistics.recordCall(start, result.isSuccess());

      String code = result.getErrorCode();

      LogdocSerializable serStart = new FormattedDate(start);
      // TODO: LogdocSerializable inParams = new FormattedInputParameters(request);
      // TODO: LogdocSerializable inParams = new FormattedOutputParameters(result);
      LogdocSerializable inParams  = new ServletRequestPropertyReader(request);
      LogdocSerializable outParams = result.getParameters();

      Log.log_1540(serStart, ip, _name, callID, duration, code, inParams, outParams);
      Log.log_1541(serStart, ip, _name, callID, duration, code);
   }

   /**
    * Logdoc-serializable for a date.
    *
    * @version $Revision$ $Date$
    * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
    */
   private static final class FormattedDate
   extends AbstractLogdocSerializable {

      //---------------------------------------------------------------------
      // Constructor
      //---------------------------------------------------------------------

      /**
       * Constructs a new <code>FormattedDate</code> object.
       *
       * @param date
       *    the date, as a number of milliseconds since January 1, 1970.
       */
      private FormattedDate(long date) {
         _epochDate = date;
      }


      //---------------------------------------------------------------------
      // Fields
      //---------------------------------------------------------------------

      /**
       * The date, as a number of milliseconds since January 1, 1970.
       */
      private final long _epochDate;

      /**
       * Lazily initialized string that represents the date.
       */
      private String _asString;


      //---------------------------------------------------------------------
      // Methods
      //---------------------------------------------------------------------

      public void serializeImpl(LogdocStringBuffer buffer)
      throws NullPointerException {
         buffer.append(_asString);
      }

      public void initialize() {

         // Create a FastStringBuffer with an initial size as follows:
         //  2 for the century     (e.g.  20)
         //  2 for the year        (e.g.  04)
         //  2 for the month       (e.g.  07)
         //  1 for a hyphen
         //  2 for the day         (e.g.  30)
         //  2 for the hour        (e.g.  13)
         //  2 for the minute      (e.g.  33)
         //  2 for the second      (e.g.  09)
         //  3 for the millisecond (e.g. 231)
         // ---
         // 18 in total
         final int BUFFER_SIZE = 18;
         FastStringBuffer buffer = new FastStringBuffer(18);

         // XXX: It seems stupid that it is not possible to create a Calendar
         //      instance directly using the current date as a number of
         //      milliseconds since the Epoch
         Calendar calendar = new GregorianCalendar(1970, Calendar.JANUARY, 1);
         calendar.setTimeInMillis(_epochDate);

         int year    = calendar.get(Calendar.YEAR);
         int month   = calendar.get(Calendar.MONTH);
         int day     = calendar.get(Calendar.DATE);
         int hours   = calendar.get(Calendar.HOUR);
         int minutes = calendar.get(Calendar.MINUTE);
         int seconds = calendar.get(Calendar.SECOND);
         int millis  = calendar.get(Calendar.MILLISECOND);

         // Append year
         buffer.append(year);

         // Append month
         if (month < 10) {
            buffer.append('0');
         }
         buffer.append(month);

         // Append day
         if (day < 10) {
            buffer.append('0');
         }
         buffer.append(day);

         // Append hyphen separator
         buffer.append('-');

         // Append hours
         if (hours < 10) {
            buffer.append('0');
         }
         buffer.append(hours);

         // Append minutes
         if (minutes < 10) {
            buffer.append('0');
         }
         buffer.append(minutes);

         // Append seconds
         if (seconds < 10) {
            buffer.append('0');
         }
         buffer.append(seconds);

         // Append milliseconds
         if (millis < 10) {
            buffer.append("00");
         } else if (millis < 100) {
            buffer.append('0');
         }
         buffer.append(millis);

         assert buffer.getLength() == BUFFER_SIZE : "buffer.getLength() (" + buffer.getLength() + ") == BUFFER_SIZE (" + BUFFER_SIZE + ')';

         _asString = buffer.toString();
      }
   }
}
