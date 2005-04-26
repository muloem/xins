/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.xml;

import java.io.IOException;
import java.io.Reader;

import java.util.Stack;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import org.xins.common.Log;
import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.Utils;

import org.xins.common.text.FastStringBuffer;
import org.xins.common.text.ParseException;
import org.xins.common.text.TextUtils;

/**
 * XML element parser. XML is parsed to produce {@link Element} objects.
 *
 * <p>Note: This parser is
 * <a href="http://www.w3.org/TR/REC-xml-names/">XML Namespaces</a>-aware.
 *
 * @version $Revision$ $Date$
 *
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.1.0
 */
public class ElementParser
extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Fully-qualified name of this class. This field is not <code>null</code>.
    */
   private static final String CLASSNAME = ElementParser.class.getName();

   /**
    * Fully-qualified name of the inner class <code>Handler</code>. This field
    * is not <code>null</code>.
    */
   private static final String HANDLER_CLASSNAME = ElementParser.Handler.class.getName();

   /**
    * Error state for the SAX event handler.
    */
   private static final State ERROR = new State("ERROR");

   /**
    * State for the SAX event handler in the data section (at any depth within
    * the <code>data</code> element).
    */
   private static final State PARSING = new State("PARSING");

   /**
    * State for the SAX event handler for the final state, when parsing is
    * finished.
    */
   private static final State FINISHED = new State("FINISHED");

   /**
    * The factory for SAX parsers. This field is never <code>null</code>, it
    * is initialized by a class initializer.
    */
   private static final SAXParserFactory SAX_PARSER_FACTORY;


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Initializes this class.
    */
   static {
      SAX_PARSER_FACTORY = SAXParserFactory.newInstance();
      SAX_PARSER_FACTORY.setValidating(true);
      SAX_PARSER_FACTORY.setNamespaceAware(true);
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>ElementParser</code>.
    */
   public ElementParser() {

      // TRACE: Enter constructor
      Log.log_1000(CLASSNAME, null);

      // empty

      // TRACE: Leave constructor
      Log.log_1002(CLASSNAME, null);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Parses content of a character stream to create an XML
    * <code>Element</code> object.
    *
    * @param in
    *    the character stream that is supposed to contain XML to be parsed,
    *    not <code>null</code>.
    *
    * @return
    *    the parsed result, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>in == null</code>.
    *
    * @throws IOException
    *    if there is an I/O error.
    *
    * @throws ParseException
    *    if the content of the character stream is not considered to be valid
    *    XML.
    */
   public Element parse(Reader in)
   throws IllegalArgumentException,
          IOException,
          ParseException {

      // TODO: Consider using an XMLReader instead of a SAXParser

      final String THIS_METHOD = "parse(java.io.Reader)";

      // TRACE: Enter method
      Log.log_1003(CLASSNAME, THIS_METHOD, null);

      // Check preconditions
      MandatoryArgumentChecker.check("in", in);

      // Initialize our SAX event handler
      Handler handler = new Handler();

      // Construct a SAX parser
      SAXParser saxParser;
      try {
         saxParser = SAX_PARSER_FACTORY.newSAXParser();

      // Factory method may fail with an exception
      } catch (Exception exception) {
         final String SUBJECT_CLASS  = SAX_PARSER_FACTORY.getClass().getName();
         final String SUBJECT_METHOD = "newSAXParser()";
         final String DETAIL         = null;
         throw Utils.logProgrammingError(CLASSNAME,     THIS_METHOD,
                                         SUBJECT_CLASS, SUBJECT_METHOD,
                                         DETAIL,        exception);
      }

      // Make sure the returned reference is not null
      if (saxParser == null) {
         final String SUBJECT_CLASS  = SAX_PARSER_FACTORY.getClass().getName();
         final String SUBJECT_METHOD = "newSAXParser()";
         final String DETAIL         = "Method returned null";
         throw Utils.logProgrammingError(CLASSNAME,     THIS_METHOD,
                                         SUBJECT_CLASS, SUBJECT_METHOD,
                                         DETAIL,        null);
      }

      // Make sure the parser validates XML documents
      if (! saxParser.isValidating()) {
         final String SUBJECT_CLASS  = SAX_PARSER_FACTORY.getClass().getName();
         final String SUBJECT_METHOD = "newSAXParser()";
         final String DETAIL         = "Returned parser does not validate XML documents.";
         throw Utils.logProgrammingError(CLASSNAME,     THIS_METHOD,
                                         SUBJECT_CLASS, SUBJECT_METHOD,
                                         DETAIL,        null);
      }

      // Make sure the parser supports XML Namespaces
      if (! saxParser.isNamespaceAware()) {
         final String SUBJECT_CLASS  = SAX_PARSER_FACTORY.getClass().getName();
         final String SUBJECT_METHOD = "newSAXParser()";
         final String DETAIL         = "Returned parser does not support XML Namespaces.";
         throw Utils.logProgrammingError(CLASSNAME,     THIS_METHOD,
                                         SUBJECT_CLASS, SUBJECT_METHOD,
                                         DETAIL,        null);
      }

      // Wrap the Reader in a SAX InputSource object
      InputSource source = new InputSource(in);

      try {
         // Let SAX parse the XML, using our handler
         saxParser.parse(source, handler);

      } catch (SAXException exception) {

         // TODO: Log: Parsing failed
         String exMessage = exception.getMessage();

         // Construct complete message
         String message = "Failed to parse XML";
         if (TextUtils.isEmpty(exMessage)) {
            message += '.';
         } else {
            message += ": " + exMessage;
         }

         // Throw exception with message, and register cause exception
         throw new ParseException(message, exception, exMessage);
      }

      Element element = handler.getElement();

      // TRACE: Leave method
      Log.log_1005(CLASSNAME, THIS_METHOD, null);

      return element;
   }


   //-------------------------------------------------------------------------
   // Inner classes
   //-------------------------------------------------------------------------

   /**
    * SAX event handler that will parse XML.
    *
    * @version $Revision$ $Date$
    * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
    * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
    *
    * @since XINS 1.1.0
    */
   private class Handler extends DefaultHandler {

      //-------------------------------------------------------------------------
      // Constructors
      //-------------------------------------------------------------------------

      /**
       * Constructs a new <code>Handler</code> instance.
       */
      private Handler() {

         // TRACE: Enter constructor
         Log.log_1000(HANDLER_CLASSNAME, null);

         _state            = PARSING;
         _level            = -1;
         _characters       = new FastStringBuffer(45);
         _dataElementStack = new Stack();

         // TRACE: Leave constructor
         Log.log_1002(HANDLER_CLASSNAME, null);
      }


      //-------------------------------------------------------------------------
      // Fields
      //-------------------------------------------------------------------------

      /**
       * The current state. Never <code>null</code>.
       */
      private State _state;

      /**
       * The element resulting of the parsing.
       */
      private Element _element;

      /**
       * The character content (CDATA or PCDATA) of the element currently
       * being parsed.
       */
      private final FastStringBuffer _characters;

      /**
       * The stack of child elements within the data section. The top element
       * is always <code>&lt;data/&gt;</code>.
       */
      private Stack _dataElementStack;

      /**
       * The level for the element pointer within the XML document. Initially
       * this field is <code>-1</code>, which indicates the current element
       * pointer is outside the document. The value <code>0</code> is for the
       * root element (<code>result</code>), etc.
       */
      private int _level;


      //-------------------------------------------------------------------------
      // Methods
      //-------------------------------------------------------------------------

      /**
       * Receive notification of the beginning of an element.
       *
       * @param namespaceURI
       *    the namespace URI, can be <code>null</code>.
       *
       * @param localName
       *    the local name (without prefix); cannot be <code>null</code>.
       *
       * @param qName
       *    the qualified name (with prefix), can be <code>null</code> since
       *    <code>namespaceURI</code> and <code>localName</code> are always
       *    used instead.
       *
       * @param atts
       *    the attributes attached to the element; if there are no
       *    attributes, it shall be an empty {@link Attributes} object; cannot
       *    be <code>null</code>.
       *
       * @throws IllegalArgumentException
       *    if <code>localName == null || atts == null</code>.
       *
       * @throws SAXException
       *    if the parsing failed.
       */
      public void startElement(String     namespaceURI,
                               String     localName,
                               String     qName,
                               Attributes atts)
      throws IllegalArgumentException, SAXException {

         final String THIS_METHOD = "startElement(java.lang.String,java.lang.String,java.lang.String,org.xml.sax.Attributes)";

         // Temporarily enter ERROR state, on success this state is left
         State currentState = _state;
         _state = ERROR;

         // Make sure namespaceURI is either null or non-empty
         namespaceURI = "".equals(namespaceURI) ? null : namespaceURI;

         // Cache quoted version of namespaceURI
         String quotedNamespaceURI = TextUtils.quote(namespaceURI);

         // TRACE: Enter method
         Log.log_1003(HANDLER_CLASSNAME, THIS_METHOD,
                        "_state="       + currentState
                    + "; _level="       + _level
                    + "; namespaceURI=" + quotedNamespaceURI
                    + "; localName="    + TextUtils.quote(localName)
                    + "; qName="        + TextUtils.quote(qName));

         // Check preconditions
         MandatoryArgumentChecker.check("localName", localName, "atts", atts);

         // Increase the element depth level
         _level++;

         if (currentState == ERROR) {
            String detail = "Unexpected state " + currentState + " (level=" + _level + ')';
            throw Utils.logProgrammingError(HANDLER_CLASSNAME, THIS_METHOD, HANDLER_CLASSNAME, THIS_METHOD, detail);

         } else {

            // Construct a Element
            Element element = new Element(namespaceURI, localName);

            // Add all attributes
            for (int i = 0; i < atts.getLength(); i++) {
               String attrNamespaceURI = atts.getURI(i);
               String attrLocalName    = atts.getLocalName(i);
               String attrValue        = atts.getValue(i);

               element.setAttribute(attrNamespaceURI, attrLocalName, attrValue);
            }

            // Push the element on the stack
            _dataElementStack.push(element);

            // Reserve buffer for PCDATA
            _characters.clear();

            // Reset the state from ERROR back to PARSING
            _state = PARSING;
         }

         Log.log_1005(HANDLER_CLASSNAME, THIS_METHOD,
                        "_state="       + _state
                    + "; _level="       + _level
                    + "; namespaceURI=" + TextUtils.quote(namespaceURI)
                    + "; localName="    + TextUtils.quote(localName)
                    + "; qName="        + TextUtils.quote(qName));
      }

      /**
       * Receive notification of the end of an element.
       *
       * @param namespaceURI
       *    the namespace URI, can be <code>null</code>.
       *
       * @param localName
       *    the local name (without prefix); cannot be <code>null</code>.
       *
       * @param qName
       *    the qualified name (with prefix), can be <code>null</code> since
       *    <code>namespaceURI</code> and <code>localName</code> are only
       *    used.
       *
       * @throws IllegalArgumentException
       *    if <code>localName == null</code>.
       */
      public void endElement(String namespaceURI,
                             String localName,
                             String qName)
      throws IllegalArgumentException {

         final String THIS_METHOD = "endElement(java.lang.String,java.lang.String,java.lang.String)";

         // Temporarily enter ERROR state, on success this state is left
         State currentState = _state;
         _state = ERROR;

         // Make sure namespaceURI is either null or non-empty
         namespaceURI = "".equals(namespaceURI) ? null : namespaceURI;

         // Cache quoted version of namespaceURI
         String quotedNamespaceURI = TextUtils.quote(namespaceURI);

         // TRACE: Enter method
         Log.log_1003(HANDLER_CLASSNAME, THIS_METHOD,
                        "_state="       + currentState
                    + "; _level="       + _level
                    + "; namespaceURI=" + TextUtils.quote(namespaceURI)
                    + "; localName="    + TextUtils.quote(localName)
                    + "; qName="        + TextUtils.quote(qName));

         // Check preconditions
         MandatoryArgumentChecker.check("localName", localName);

         if (currentState == ERROR) {
            String detail = "Unexpected state " + currentState + " (level=" + _level + ')';
            throw Utils.logProgrammingError(HANDLER_CLASSNAME, THIS_METHOD, HANDLER_CLASSNAME, THIS_METHOD, detail);

         // Within data section
         } else {

            // Get the Element for which we process the end tag
            Element child = (Element) _dataElementStack.pop();

            // Set the PCDATA content on the element
            if (_characters != null && _characters.getLength() > 0) {
               child.setText(_characters.toString());
            }

            // Add the child to the parent
            if (_dataElementStack.size() > 0) {
               Element parent = (Element) _dataElementStack.peek();
               parent.addChild(child);

               // Reset the state back from ERROR to PARSING
               _state = PARSING;
            } else {
               _element = child;
               _state = FINISHED;
            }

         }

         _level--;
         _characters.clear();

         // TRACE: Leave method
         Log.log_1005(HANDLER_CLASSNAME, THIS_METHOD,
                        "_state="       + _state
                    + "; _level="       + _level
                    + "; namespaceURI=" + TextUtils.quote(namespaceURI)
                    + "; localName="    + TextUtils.quote(localName)
                    + "; qName="        + TextUtils.quote(qName));
      }

      /**
       * Receive notification of character data.
       *
       * @param ch
       *    the <code>char</code> array that contains the characters from the
       *    XML document, cannot be <code>null</code>.
       *
       * @param start
       *    the start index within <code>ch</code>.
       *
       * @param length
       *    the number of characters to take from <code>ch</code>.
       *
       * @throws IndexOutOfBoundsException
       *    if characters outside the allowed range are specified.
       *
       * @throws SAXException
       *    if the parsing failed.
       */
      public void characters(char[] ch, int start, int length)
      throws IndexOutOfBoundsException, SAXException {

         final String THIS_METHOD = "characters(char[],int,int)";

         // Temporarily enter ERROR state, on success this state is left
         State currentState = _state;
         _state = ERROR;

         // TRACE: Enter method
         Log.log_1003(HANDLER_CLASSNAME, THIS_METHOD, null);

         if (_characters != null) {
            _characters.append(ch, start, length);
         }

         // Reset _state
         _state = currentState;
      }

      /**
       * Gets the parsed element.
       *
       * @return
       *    the element resulting of the parsing of the XML.
       */
      Element getElement() {

         final String THIS_METHOD = "getElement()";

         // Check state
         if (_state != FINISHED) {
            final String DETAIL = "State is "
                                + _state
                                + " instead of "
                                + FINISHED;
            throw Utils.logProgrammingError(HANDLER_CLASSNAME, THIS_METHOD,
                                            HANDLER_CLASSNAME, THIS_METHOD,
                                            DETAIL);
         }

         return _element;
      }
   }

   /**
    * State of the event handler.
    *
    * @version $Revision$ $Date$
    * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
    *
    * @since XINS 1.0.0
    */
   private static final class State extends Object {

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      /**
       * Constructs a new <code>State</code> object.
       *
       * @param name
       *    the name of this state, cannot be <code>null</code>.
       *
       * @throws IllegalArgumentException
       *    if <code>name == null</code>.
       */
      State(String name) throws IllegalArgumentException {

         // Check preconditions
         MandatoryArgumentChecker.check("name", name);

         _name = name;
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      /**
       * The name of this state. Cannot be <code>null</code>.
       */
      private final String _name;


      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------

      /**
       * Returns the name of this state.
       *
       * @return
       *    the name of this state, cannot be <code>null</code>.
       */
      public String getName() {
         return _name;
      }

      /**
       * Returns a textual representation of this object.
       *
       * @return
       *    the name of this state, never <code>null</code>.
       */
      public String toString() {
         return _name;
      }
   }
}
