/*
 * $Id$
 */
package org.xins.server;

import java.io.IOException;
import java.util.Map;
import org.znerd.xmlenc.XMLOutputter;

/**
 * Context for a function call. Objects of this kind are passed with a
 * function call.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public final class CallContext
extends Object
implements Responder {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>CallContext</code> object.
    */
   CallContext(XMLOutputter xmlOutputter, Map parameters) {
      _start        = System.currentTimeMillis();
      _xmlOutputter = xmlOutputter;
      _parameters   = parameters;
      _state        = BEFORE_START;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The start time of the call, as a number of milliseconds since midnight
    * January 1, 1970 UTC.
    */
   private final long _start;

   /**
    * The XML outputter.
    */
   private final XMLOutputter _xmlOutputter;

   /**
    * The parameters for this call.
    */
   private final Map _parameters;

   /**
    * The current state.
    */
   private ResponderState _state;

   /**
    * The number of element tags currently open within the data section.
    */
   private int _elementDepth;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

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
    * Returns the name of the function called.
    *
    * @return
    *    the function called, or <code>null</code> if there is no function
    *    specificied.
    */
   public String getFunction() {

      // Check arguments
      if (_parameters != null) {
         return (String) _parameters.get("function");
      } else {
         return null;
      }
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

      if (_parameters != null && !"function".equals(name)) {
         return (String) _parameters.get(name);
      } else {
         return null;
      }
   }

   public final void startResponse(boolean success, String returnCode)
   throws IllegalStateException, IOException {

      // Check state
      if (_state != BEFORE_START) {
         throw new IllegalStateException("The state is " + _state + '.');
      }

      // Temporarily enter the ERROR state
      _state = ERROR;

      _xmlOutputter.startTag("result");

      if (success) {
         _xmlOutputter.attribute("success", "true");
      } else {
         _xmlOutputter.attribute("success", "false");
      }

      if (returnCode != null && returnCode.length() > 0) {
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
}
