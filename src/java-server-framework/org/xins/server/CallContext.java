/*
 * $Id$
 */
package org.xins.server;

import javax.servlet.ServletRequest;
import org.xins.util.MandatoryArgumentChecker;
import org.xins.util.text.FastStringBuffer;
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
public final class CallContext {

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
    * Constructs a new <code>CallContext</code> and configures it for the
    * specified servlet request.
    *
    * @param request
    *    the servlet request, should not be <code>null</code>.
    *
    * @param start
    *    the start time of the call, as milliseconds since midnight January 1,
    *    1970.
    *
    * @param function
    *    the concerning function, cannot be <code>null</code>.
    *
    * @param callID
    *    the assigned call ID.
    *
    * @param session
    *    the session, if any, or <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>request == null || function == null</code>.
    */
   CallContext(ServletRequest request, long start, Function function, int callID, Session session)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("request",  request, "function", function);

      // Initialize fields
      _request      = request;
      _start        = start;
      _api          = function.getAPI();
      _function     = function;
      _functionName = function.getName();
      _callID       = callID;
      _session      = session;
      _builder      = new CallResultBuilder();
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
    * The original servlet request.
    */
   private final ServletRequest _request;

   /**
    * The call result builder. Cannot be <code>null</code>.
    */
   private final CallResultBuilder _builder;

   /**
    * The start time of the call, as a number of milliseconds since midnight
    * January 1, 1970 UTC.
    */
   private final long _start;

   /**
    * The number of element tags currently open within the data section.
    */
   private int _elementDepth;

   /**
    * The name of the function currently being called. Cannot be
    * <code>null</code>.
    */
   private final String _functionName;

   /**
    * The function currently being called. Cannot be <code>null</code>.
    */
   private final Function _function;

   /**
    * The session for this call.
    */
   private Session _session;

   /**
    * Flag that indicates if the session ID should be added as a parameter to
    * the response.
    */
   private boolean _returnSessionID;

   /**
    * The call ID, unique in the context of the pertaining function.
    */
   private final int _callID;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   // TODO: Document
   // TODO: Probably take a different approach
   CallResult getCallResult() {
      if (_builder.isSuccess() && _returnSessionID) {
         _builder.param("_session", _session.getIDString());
         _returnSessionID = false;
      }
      return _builder;
   }

   /**
    * Returns the start time of the call.
    *
    * @return
    *    the timestamp indicating when the call was started, as a number of
    *    milliseconds since midnight January 1, 1970 UTC.
    */
   public long getStart() {
      return _start;
   }

   /**
    * Returns the stored success indication.
    *
    * @return
    *    the success indication.
    *
    * @since XINS 0.128
    */
   public final boolean isSuccess() {
      return _builder.isSuccess();
   }

   /**
    * Returns the stored return code.
    *
    * @return
    *    the return code, can be <code>null</code>.
    */
   final String getCode() {
      return _builder.getCode();
   }

   /**
    * Returns the session for this call, if any.
    *
    * @return
    *    the session for this call, not <code>null</code>.
    *
    * @throws IllegalStateException
    *    if the current function is not session-based.
    */
   public Session getSession() throws IllegalStateException {

      // Check preconditions
      if (_function.isSessionBased() == false) {
         throw new IllegalStateException("The function " + _functionName + " is not session-based.");
      }

      // Get the session
      return _session;
   }

   /**
    * Creates a session, stores it and remembers that it will have to be
    * returned in the result.
    *
    * @return
    *    the constructed session, cannot be <code>null</code>.
    */
   public Session createSession() {

      // Create the session
      Session session = _api.createSession();

      // Store the session and remember that we have to send it down
      _session         = session;
      _returnSessionID = true;

      return session;
   }

   /**
    * Returns the value of a parameter with the specificied name. Note that
    * reserved parameters, i.e. those starting with an underscore
    * (<code>'_'</code>) cannot be retrieved.
    *
    * @param name
    *    the name of the parameter, not <code>null</code>.
    *
    * @return
    *    the value of the parameter, or <code>null</code> if the parameter is
    *    not set, never an empty string (<code>""</code>) because it will be
    *    returned as being <code>null</code>.
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

      // XXX: In a later version, support a parameter named 'function'

      if (_request != null && name.length() > 0 && !"function".equals(name) && name.charAt(0) != '_') {
         String value = _request.getParameter(name);
         return "".equals(value) ? null : value;
      }
      return null;
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
}
