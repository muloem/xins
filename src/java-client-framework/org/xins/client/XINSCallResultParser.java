/*
 * $Id$
 */
package org.xins.client;

import java.io.ByteArrayInputStream;
import java.util.Hashtable;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import org.xins.common.MandatoryArgumentChecker;

import org.xins.common.collections.PropertyReader;
import org.xins.common.collections.ProtectedPropertyReader;

import org.xins.common.service.TargetDescriptor;
import org.xins.common.text.FastStringBuffer;
import org.xins.common.text.ParseException;

/**
 * XINS call result parser. XML is parsed to produce a {@link XINSCallResult}
 * object.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 0.207
 */
public class XINSCallResultParser
extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Fully-qualified name of this class. This field is not <code>null</code>.
    */
   private static final String CLASSNAME = XINSCallResultParser.class.getName();

   /**
    * Constant for an <code>Integer</code> object representing the number
    * zero. This field is not <code>null</code>.
    */
   private static final Integer ZERO = new Integer(0);

   /**
    * The key for the <code>ProtectedPropertyReader</code> instances created
    * by this class.
    */
   private static final Object PROTECTION_KEY = new Object();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>XINSCallResultParser</code>.
    */
   public XINSCallResultParser() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Parses the given XML string to create a <code>XINSCallResultData</code>
    * object.
    *
    * @param xml
    *    the XML to be parsed, not <code>null</code>.
    *
    * @return
    *    the parsed result of the call, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>xml == null</code>.
    *
    * @throws ParseException
    *    if the specified string is not valid XML or if it is not a valid XINS
    *    API function call result.
    */
   public XINSCallResultData parse(byte[] xml)
   throws IllegalArgumentException, ParseException {

      // TRACE: Enter method
      Log.log_2003(CLASSNAME, "parse(byte[])", null);

      // Check preconditions
      MandatoryArgumentChecker.check("xml", xml);

      // Initialize a SAX event handler
      Handler handler = new Handler();

      try {

         // Construct a SAX parser
         SAXParserFactory factory = SAXParserFactory.newInstance();
         SAXParser saxParser      = factory.newSAXParser();

         // Convert the byte array to an input stream
         ByteArrayInputStream bais = new ByteArrayInputStream(xml);

         // Let SAX parse the XML, using our handler
         saxParser.parse(bais, handler);

         // Dispose the constructed input stream
         bais.close();

      } catch (Throwable exception) {

         // Construct a buffer for the error message
         FastStringBuffer buffer = new FastStringBuffer(142, "Unable to convert the specified character string to XML");

         // Include the exception message in our error message, if any
         String detail = exception.getMessage();
         if (detail != null && detail.length() > 0) {
            buffer.append(": ");
            buffer.append(detail);
         } else {
            buffer.append('.');
         }

         Log.log_2206(exception, detail);

         throw new ParseException(buffer.toString());
         // TODO: throw new ParseException(buffer.toString(), exception);
      }

      // TRACE: Leave method
      Log.log_2005(CLASSNAME, "parse(byte[])", null);

      return handler;
   }


   //-------------------------------------------------------------------------
   // Inner classes
   //-------------------------------------------------------------------------

   /**
    * SAX event handler that will parse the result from a call to a XINS
    * service.
    *
    * @version $Revision$ $Date$
    * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
    *
    * @since XINS 0.207
    */
   private class Handler
   extends DefaultHandler
   implements XINSCallResultData {

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
       * Constructs a new <code>Handler</code> instance.
       */
      private Handler() {
         // empty
      }


      //-------------------------------------------------------------------------
      // Fields
      //-------------------------------------------------------------------------

      /**
       * The error code returned by the function or <code>null</code>, if no
       * error code is returned.
       */
      private String _errorCode;

      /**
       * The list of the parameters (name/value) returned by the function.
       * This field is lazily initialized.
       */
      private ProtectedPropertyReader _parameters;

      /**
       * The parameter name of the parameter that is actually parsed.
       */
      private String _parameterKey;

      /**
       * The PCDATA element of the tag that is actually parsed.
       */
      private FastStringBuffer _pcdata;

      /**
       * The content of the data element that is actually parsed.
       */
      private Hashtable _elements = new Hashtable();

      /**
       * The level of the element that is actually parsed in the data element.
       * Initially this field is set to -1, which means that no element is parsed;
       * 0 means that the parser just read the &lt;data&gt; tag;
       * 1 means that the parser entered in an direct sub-element of the
       * &lt;data&gt; tag, etc.
       */
      private int _level = -1;

      /**
       * Indicates if the parsing of the result has started
       */
      private boolean _parsingStarted  = false;


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
       *    the local name (without prefix); the empty string indicates that
       *    Namespace processing is not being performed; can be
       *    <code>null</code>.
       *
       * @param qName
       *    the qualified name (with prefix); the empty string indicates that
       *    qualified names are not available; cannot be <code>null</code>.
       *
       * @param atts
       *    the attributes attached to the element; if there are no
       *    attributes, it shall be an empty {@link Attributes} object; cannot
       *    be <code>null</code>.
       *
       * @throws IllegalArgumentException
       *    if <code>qName == null || atts == null</code>.
       *
       * @throws SAXException
       *    if the element is unknown.
       */
      public void startElement(String     namespaceURI,
                               String     localName,
                               String     qName,
                               Attributes atts)
      throws IllegalArgumentException, SAXException {

         // Check preconditions
         MandatoryArgumentChecker.check("qName", qName, "atts", atts);

         // Root element must be 'result'
         if (!_parsingStarted && !qName.equals("result")) {
            Log.log_2200(qName);
         }

         // Within the data section
         if (_level >= 0) {

            // Increase the depth level
            _level++;

            // Construct a DataElement
            DataElement element = new DataElement(qName);

            // Add all attributes
            for (int i = 0; i < atts.getLength(); i++) {
               String key = atts.getQName(i);
               String value = atts.getValue(i);
               element.addAttribute(key, value);
            }
            _elements.put(new Integer(_level), element);

            // Reserve buffer for PCDATA
            _pcdata = new FastStringBuffer(20);

         // Root element
         } else if (qName.equals("result")) {
            _parsingStarted = true;
            _errorCode = atts.getValue("errorcode");
            if (_errorCode == null) {
               _errorCode = atts.getValue("code");
            }

         // Output parameter
         } else if (qName.equals("param")) {
            _parameterKey = atts.getValue("name");
            _pcdata = new FastStringBuffer(20);

         // Start of data section
         } else if (qName.equals("data")) {
            _elements = new Hashtable();
            _elements.put(ZERO, new DataElement("data"));
            _level = 0;

         // Unknown element
         } else {
            // TODO: Log?
            // TODO: Just ignore this element?
            throw new SAXException("Unknown element \"" + qName + "\".");
         }
      }

      /**
       * Receive notification of the end of an element.
       *
       * @param namespaceURI
       *    the namespace URI, can be <code>null</code>.
       *
       * @param localName
       *    the local name (without prefix); the empty string indicates that
       *    Namespace processing is not being performed; can be
       *    <code>null</code>.
       *
       * @param qName
       *    the qualified name (with prefix); the empty string indicates that
       *    qualified names are not available; cannot be <code>null</code>.
       *
       * @throws IllegalArgumentException
       *    if <code>qName == null</code>.
       *
       * @throws SAXException
       *    if the element is unknown.
       */
      public void endElement(String namespaceURI,
                             String localName,
                             String qName)
      throws IllegalArgumentException, SAXException {

         // Check preconditions
         MandatoryArgumentChecker.check("qName", qName);

         // Within data section
         if (_level > 0) {
            DataElement child = (DataElement)_elements.get(new Integer(_level));
            if (_pcdata != null && _pcdata.getLength() > 0) {
               child.setText(_pcdata.toString());
            }
            _pcdata = null;
            _level--;
            DataElement parent = (DataElement)_elements.get(new Integer(_level));
            parent.addChild(child);

         // Output parameter
         } else if (qName.equals("param")) {

            // Retrieve name and value for output parameter
            String name  = _parameterKey;
            String value = _pcdata.toString();

            // Both name and value should be set
            boolean noName  = (name  == null || name.length()  < 1);
            boolean noValue = (value == null || value.length() < 1);
            if (noName && noValue) {
               Log.log_2201();
            } else if (noName) {
               Log.log_2202(value);
            } else if (noValue) {
               Log.log_2203(name);

            // Name and value are both set, correctly
            } else {
               Log.log_2204(name, value);

               // Previously no parameters, perform (lazy) initialization
               if (_parameters == null) {
                  _parameters = new ProtectedPropertyReader(PROTECTION_KEY);

               // Check if parameter is already set
               } else {
                  String existingValue = _parameters.get(name);
                  if (existingValue != null) {
                     if (existingValue.equals(value)) {
                        // Ignore
                     } else {
                        Log.log_2205(name, existingValue, value);
                        throw new SAXException("Found conflicting duplicate value for output parameter \"" + name + "\". Initial value is \"" + existingValue + "\". New value is \"" + value + "\".");
                     }
                  }
               }

               // Store the name-value combination for the output parameter
               _parameters.set(PROTECTION_KEY, name, value);
            }

            // Reset the state
            _parameterKey = null;
            _pcdata = null;

         // End of data section
         } else if (_level == 0 && qName.equals("data")) {
            _level--;

         // Otherwise we expect to be in the root element (result)
         } else if (!qName.equals("result")) {
            throw new SAXException("Unknown element \"" + qName + "\".");
         }
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
       */
      public void characters(char[] ch, int start, int length)
      throws IndexOutOfBoundsException {

         // TODO: Check state

         // TODO: Ignore, but log, PCDATA outside expected regions

         if (_pcdata != null) {
            _pcdata.append(ch, start, length);
         }
      }

      /**
       * Gets the error code returned by the function if any.
       *
       * @return
       *    the error code returned by the function or <code>null<code>
       *    if no error code has been returned from the function.
       */
      public String getErrorCode() {

         // TODO: Check state

         return _errorCode;
      }

      /**
       * Get the parameters returned by the function.
       *
       * @return
       *    the parameters (name/value) or <code>null</code> if the function
       *    does not have any parameters.
       */
      public PropertyReader getParameters() {

         // TODO: Check state

         return _parameters;
      }

      /**
       * Get the data element returned by the function if any.
       *
       * @return
       *    the data element, or <code>null</code> if the function did not
       *    return any data element.
       */
      public DataElement getDataElement() {
         return (DataElement) _elements.get(ZERO);
      }
   }
}
