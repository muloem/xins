/*
 * $Id$
 */
package org.xins.server;

import java.io.IOException;
import javax.servlet.ServletRequest;
import org.xins.util.MandatoryArgumentChecker;
import org.xins.util.io.FastStringWriter;
import org.znerd.xmlenc.XMLOutputter;
import org.apache.commons.logging.Log;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 * Context for a function call. Objects of this kind are passed with a
 * function call.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public final class CallContext
extends Object
implements Responder, Log {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The fully-qualified name of this class.
    */
   private static final String FQCN = CallContext.class.getName();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>CallContext</code>. The state will be set
    * to {@link #UNINITIALIZED}.
    *
    * <p />Before this object can be used, {@link #reset(ServletRequest)} must
    * be called.
    *
    * @param api
    *    the API for which this <code>CallContext</code> will be used, cannot
    *    be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>api == null</code>.
    */
   CallContext(API api) throws IllegalArgumentException {

      MandatoryArgumentChecker.check("api", api);

      _api          = api;
      _state        = UNINITIALIZED;
      _success      = true;
      _code         = null;
      _stringWriter = new FastStringWriter();
      _xmlOutputter = new XMLOutputter();
      _callID       = -1;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The API for which this CallContext is used. This field is initialized by
    * the constructor and can never be <code>null</code>.
    */
   private final API _api;

   /**
    * The start time of the call, as a number of milliseconds since midnight
    * January 1, 1970 UTC.
    */
   private long _start;

   /**
    * The original servlet request.
    */
   private ServletRequest _request;

   /**
    * The character stream to send the output to. This field is initialized by
    * the constructor and can never be <code>null</code>.
    */
   private final FastStringWriter _stringWriter;

   /**
    * The XML outputter. It is initialized by the constructor and sends its
    * output to {@link #_stringWriter}.
    */
   private final XMLOutputter _xmlOutputter;

   /**
    * The current state.
    */
   private ResponderState _state;

   /**
    * The number of element tags currently open within the data section.
    */
   private int _elementDepth;

   /**
    * The name of the function currently being called. This field is
    * initialized by {@link #reset(ServletRequest)} and can be set to
    * <code>null</code>.
    */
   private String _functionName;

   /**
    * The function currently being called. This field is initialized by
    * {@link #reset(ServletRequest)} and can be set to <code>null</code>.
    */
   private Function _function;

   /**
    * The logger associated with the function. This field is set if and only
    * if {@link #_function} is set.
    */
   private Logger _logger;

   /**
    * The log prefix for log messages.
    */
   private String _logPrefix;

   /**
    * Success indication. Defaults to <code>true</code> and will <em>only</em>
    * be set to <code>false</code> if and only if
    * {@link #startResponse(boolean,String)} is called with the first
    * parameter (<em>success</em>) set to <code>false</code>.
    */
   private boolean _success;

   /**
    * Return code. The default is <code>null</code> and will <em>only</em> be
    * set to something else if and only if
    * {@link #startResponse(boolean,String)} is called with the second
    * parameter (<em>code</em>) set to a non-<code>null</code>, non-empty
    * value.
    */
   private String _code;

   /**
    * The call ID, unique in the context of the pertaining function.
    */
   private int _callID;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Resets this <code>CallContext</code>. The servlet request will be set to
    * <code>null</code> and the state will be set to {@link #UNINITIALIZED}.
    *
    * <p />Before this object can be used again,
    * {@link #reset(ServletRequest)} must be called.
    */
   void reset() {
      _request      = null;
      _state        = UNINITIALIZED;
      _success      = true;
      _code         = null;
      _functionName = null;
      _function     = null;
      _logger       = null;
      _callID       = -1;
      _logPrefix    = null;
   }

   /**
    * Resets this <code>CallContext</code> and configures it for the specified
    * servlet request. This resets the state to {@link #BEFORE_START}.
    *
    * @param request
    *    the servlet request, should not be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>request == null</code>.
    *
    * @throws IOException
    *    if an I/O error occurs.
    */
   void reset(ServletRequest request)
   throws IllegalArgumentException, IOException {

      // Check preconditions
      MandatoryArgumentChecker.check("request", request);

      _start   = System.currentTimeMillis();
      _request = request;
      _state   = BEFORE_START;
      _success = true;
      _code    = null;

      _stringWriter.getBuffer().clear();
      _xmlOutputter.reset(_stringWriter, "UTF-8");

      // Determine the function name
      String functionName = _request.getParameter("_function");
      if (functionName == null) {
         functionName = _request.getParameter("function");
      }
      _functionName = functionName;

      // Determine the function object, logger, call ID, log prefix
      _function  = (functionName == null) ? null : _api.getFunction(functionName);
      _logger    = (_function    == null) ? null : _function.getLogger();
      _callID    = (_function    == null) ? -1   : _function.assignCallID();
      _logPrefix = (_function    == null) ? ""   : "Call " + _functionName + ':' + _callID + ": ";
   }

   /**
    * Returns the start time of the call.
    *
    * @return
    *    the timestamp indicating when the call was started, as a number of
    *    milliseconds since midnight January 1, 1970 UTC.
    */
   long getStart() {
      return _start;
   }

   /**
    * Returns the character stream the XML output is sent to.
    *
    * @return
    *    the underlying {@link FastStringWriter}, not <code>null</code>.
    */
   FastStringWriter getStringWriter() {
      return _stringWriter;
   }

   /**
    * Returns the <code>XMLOutputter</code> that is used to generate XML.
    *
    * @return
    *    the underlying {@link XMLOutputter} that sends its output to the
    *    {@link FastStringWriter}.
    */
   XMLOutputter getXMLOutputter() {
      return _xmlOutputter;
   }

   /**
    * Returns the stored success indication. The default is <code>true</code>
    * and it will <em>only</em> be set to <code>false</code> if and only if
    * {@link #startResponse(boolean,String)} is called with the first
    * parameter (<em>success</em>) set to <code>false</code>.
    *
    * @return
    *    the success indication.
    */
   final boolean getSuccess() {
      return _success;
   }

   /**
    * Returns the stored return code. The default is <code>null</code>
    * and it will <em>only</em> be set to something else if and only if
    * {@link #startResponse(boolean,String)} is called with the second
    * parameter (<em>code</em>) set to a non-<code>null</code>, non-empty
    * value.
    *
    * @return
    *    the return code, can be <code>null</code>.
    */
   final String getCode() {
      return _code;
   }

   /**
    * Returns the name of the function called.
    *
    * @return
    *    the name of the function called, or <code>null</code> if there is no
    *    function specificied.
    */
   public String getFunctionName() {
      return _functionName;
   }

   /**
    * Returns the function that is being called.
    *
    * @return
    *    the function called, or <code>null</code> if there is no function
    *    specificied or if there was no function in the API with the specified
    *    name (see {@link #getFunctionName()}).
    */
   public Function getFunction() {
      return _function;
   }

   /**
    * Returns the value of a parameter with the specificied name.
    *
    * @param name
    *    the name of the parameter, not <code>null</code>.
    *
    * @return
    *    the value of the parameter, or <code>null</code> if the parameter is
    *    not set.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null</code>.
    */
   public String getParameter(String name)
   throws IllegalArgumentException {

      // Check arguments
      if (name == null) {
         throw new IllegalArgumentException("name == null");
      }

      if (_request != null && !"function".equals(name)) {
         return _request.getParameter(name);
      } else {
         return null;
      }
   }

   /**
    * Returns the assigned call ID. This ID is unique within the context of
    * the pertaining function. If no call ID is assigned, then <code>-1</code>
    * is returned.
    *
    * @return
    *    the assigned call ID for the function, or <code>-1</code> if none is
    *    assigned.
    */
   public int getCallID() {
      return _callID;
   }

   public final void startResponse(ResultCode resultCode)
   throws IllegalStateException, IOException {
      if (resultCode == null) {
         startResponse(true, null);
      } else {
         startResponse(resultCode.getSuccess(), resultCode.getValue());
      }
   }

   public final void startResponse(boolean success)
   throws IllegalStateException, IOException {
      startResponse(success, null);
   }

   public final void startResponse(boolean success, String returnCode)
   throws IllegalStateException, IOException {

      // Check state
      if (_state != BEFORE_START) {
         throw new IllegalStateException("The state is " + _state + '.');
      }

      // Temporarily enter the ERROR state
      _state = ERROR;

      _xmlOutputter.declaration();
      _xmlOutputter.startTag("result");

      if (success) {
         _xmlOutputter.attribute("success", "true");
      } else {
         _success = false;
         _xmlOutputter.attribute("success", "false");
      }

      if (returnCode != null && returnCode.length() > 0) {
         _code = returnCode;
         _xmlOutputter.attribute("code", returnCode);
      }

      // Reset the state
      _state = WITHIN_PARAMS;
   }

   public final void param(String name, String value)
   throws IllegalStateException, IllegalArgumentException, IOException {

      // Check state
      if (_state != BEFORE_START && _state != WITHIN_PARAMS) {
         throw new IllegalStateException("The state is " + _state + '.');
      }

      // Check arguments
      if (name == null || value == null) {
         if (name == null && value == null) {
            throw new IllegalArgumentException("name == null && value == null");
         } else if (name == null) {
            throw new IllegalArgumentException("name == null");
         } else {
            throw new IllegalArgumentException("value == null");
         }
      }

      // Start the response if necesary
      if (_state == BEFORE_START) {
         startResponse(true, null);
      }

      // Temporarily enter the ERROR state
      _state = ERROR;

      // Write <param name="name">value</param>
      _xmlOutputter.startTag("param");
      _xmlOutputter.attribute("name", name);
      _xmlOutputter.pcdata(value);
      _xmlOutputter.endTag();

      // Reset the state
      _state = WITHIN_PARAMS;
   }

   private final void startDataSection()
   throws IOException {
      _state = ERROR;
      _xmlOutputter.startTag("data");
      _state = WITHIN_ELEMENT;

      _elementDepth = 0;
   }

   public final void startTag(String type)
   throws IllegalStateException, IllegalArgumentException, IOException {

      // Check state
      if (_state == AFTER_END) {
         throw new IllegalStateException("The state is " + _state + '.');
      }

      // Check argument
      if (type == null) {
         throw new IllegalArgumentException("type == null");
      } else if (type.length() == 0) {
         throw new IllegalArgumentException("type.equals(\"\")");
      }

      // Start the response if necesary
      if (_state == BEFORE_START) {
         startResponse(true, null);
      }

      // Enter the <data/> section if necessary
      if (_state == WITHIN_PARAMS) {
         startDataSection();
      }

      // Temporarily enter the ERROR state
      _state = ERROR;

      // Write the start tag
      _xmlOutputter.startTag(type);
      _elementDepth++;

      // Reset the state
      _state = START_TAG_OPEN;
   }

   public final void attribute(String name, String value)
   throws IllegalStateException, IllegalArgumentException, IOException {

      // Check state
      if (_state != START_TAG_OPEN) {
         throw new IllegalStateException("The state is " + _state + '.');
      }

      // Temporarily enter the ERROR state
      _state = ERROR;

      // Write the attribute
      _xmlOutputter.attribute(name, value);

      // Reset the state
      _state = START_TAG_OPEN;
   }

   public final void pcdata(String text)
   throws IllegalStateException, IllegalArgumentException, IOException {

      // Check state
      if (_state != START_TAG_OPEN && _state != WITHIN_ELEMENT) {
         throw new IllegalStateException("The state is " + _state + '.');
      }

      // Temporarily enter the ERROR state
      _state = ERROR;

      // Write the PCDATA
      _xmlOutputter.pcdata(text);

      // Reset the state
      _state = WITHIN_ELEMENT;
   }

   public final void endTag()
   throws IllegalStateException, IOException {

      // Check state
      if (_state != START_TAG_OPEN && _state != WITHIN_ELEMENT) {
         throw new IllegalStateException("The state is " + _state + '.');
      }
      if (_elementDepth == 0) {
         throw new IllegalStateException("There are no more elements in the data section to close.");
      }

      // Temporarily enter the ERROR state
      _state = ERROR;

      // End the tag
      _xmlOutputter.endTag();
      _elementDepth--;

      // Reset the state
      _state = WITHIN_ELEMENT;
   }

   public final void endResponse() throws IOException {

      // Short-circuit if the response is already ended
      if (_state == AFTER_END) {
         return;
      }

      // Start the response if necesary
      if (_state == BEFORE_START) {
         startResponse(true, null);
      }

      // Temporarily enter the ERROR state
      _state = ERROR;

      // Close all open elements
      _xmlOutputter.close();

      // Flush the output stream
      _xmlOutputter.getWriter().flush();
   }

   public void trace(Object message) {
      _logger.log(FQCN, Priority.DEBUG, _logPrefix + message, null);
   }

   public void trace(Object message, Throwable t) {
      _logger.log(FQCN, Priority.DEBUG, _logPrefix + message, t);
   }

   public void debug(Object message) {
      _logger.log(FQCN, Priority.DEBUG, _logPrefix + message, null);
   }

   public void debug(Object message, Throwable t) {
      _logger.log(FQCN, Priority.DEBUG, _logPrefix + message, t);
   }

   public void info(Object message) {
      _logger.log(FQCN, Priority.INFO, _logPrefix + message, null);
   }

   public void info(Object message, Throwable t) {
      _logger.log(FQCN, Priority.INFO, _logPrefix + message, t);
   }

   public void warn(Object message) {
      _logger.log(FQCN, Priority.WARN, _logPrefix + message, null);
   }

   public void warn(Object message, Throwable t) {
      _logger.log(FQCN, Priority.WARN, _logPrefix + message, t);
   }

   public void error(Object message) {
      _logger.log(FQCN, Priority.ERROR, _logPrefix + message, null);
   }

   public void error(Object message, Throwable t) {
      _logger.log(FQCN, Priority.ERROR, _logPrefix + message, t);
   }

   public void fatal(Object message) {
      _logger.log(FQCN, Priority.FATAL, _logPrefix + message, null);
   }

   public void fatal(Object message, Throwable t) {
      _logger.log(FQCN, Priority.FATAL, _logPrefix + message, t);
   }

   public boolean isDebugEnabled() {
      return _logger.isDebugEnabled();
   }

   public boolean isErrorEnabled() {
      return _logger.isEnabledFor(Priority.ERROR);
   }

   public boolean isFatalEnabled() {
      return _logger.isEnabledFor(Priority.FATAL);
   }

   public boolean isInfoEnabled() {
      return _logger.isInfoEnabled();
   }

   public boolean isTraceEnabled() {
      return _logger.isDebugEnabled();
   }

   public boolean isWarnEnabled() {
      return _logger.isEnabledFor(Priority.WARN);
   }
}
