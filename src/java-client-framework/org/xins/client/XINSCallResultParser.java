/*
 * $Id$
 */
package org.xins.client;

import java.io.ByteArrayInputStream;
import java.util.Hashtable;
import java.util.Properties;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.collections.PropertyReader;
import org.xins.common.collections.PropertiesPropertyReader;
import org.xins.common.service.TargetDescriptor;
import org.xins.common.text.FastStringBuffer;
import org.xins.common.text.ParseException;

/**
 * XINS call result parser. XML is parsed to produce a {@link XINSCallResult}
 * object.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 *
 * @since XINS 0.207
 */
public class XINSCallResultParser
extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Parses the given XML string to create a
    * <code>Result</code> object with the specified
    * <code>TargetDescriptor</code>.
    *
    * @param request
    *    the original {@link XINSCallRequest} that was used to perform the
    *    call, cannot be <code>null</code>.
    *
    * @param target
    *    the {@link TargetDescriptor} that was used to get the XML, cannot be
    *    <code>null</code>.
    *
    * @param duration
    *    the call duration, should be &gt;= 0.
    *
    * @param xml
    *    the XML to be parsed, not <code>null</code>.
    *
    * @return
    *    the parsed result of the call, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>request   == null
    *          || target    == null
    *          || xmlStream == null
    *          || duration &lt; 0</code>
    *
    * @throws ParseException
    *    if the specified string is not valid XML or if it is not a valid XINS
    *    API function call result.
    */
   public XINSCallResult parse(XINSCallRequest  request,
                               TargetDescriptor target,
                               long             duration,
                               byte[]           xml)
   throws IllegalArgumentException, ParseException {

      // Check preconditions
      MandatoryArgumentChecker.check("request",   request,
                                     "target",    target,
                                     "xml",       xml);
      if (duration < 0) {
         throw new IllegalArgumentException("duration (" + duration + ") < 0");
      }

      Handler handler = new Handler();
      try {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        ByteArrayInputStream bais = new ByteArrayInputStream(xml);
        saxParser.parse(bais, handler);
        bais.close();
      } catch (Throwable exception) {
         String detail = exception.getMessage();
         FastStringBuffer buffer = new FastStringBuffer(250);
         buffer.append("Unable to convert the specified character string to XML");
         if (detail != null && detail.length() > 0) {
            buffer.append(": ");
            buffer.append(detail);
         } else {
            buffer.append('.');
         }
         String message = buffer.toString();
         Log.log_2005(exception, detail);
         throw new ParseException(message);
      }

      return new XINSCallResult(request, target, duration, handler.getErrorCode(), handler.getParameters(), handler.getDataElement());
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
   private class Handler extends DefaultHandler {

      //-------------------------------------------------------------------------
      // Class fields
      //-------------------------------------------------------------------------

      //-------------------------------------------------------------------------
      // Class functions
      //-------------------------------------------------------------------------

      //-------------------------------------------------------------------------
      // Constructors
      //-------------------------------------------------------------------------

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
       */
      private Properties _parameters = new Properties();

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


      //-------------------------------------------------------------------------
      // Methods
      //-------------------------------------------------------------------------

      public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
         if (_level >= 0) {
            _level++;
            DataElement element = new DataElement(qName);

            for (int i = 0; i < attributes.getLength(); i++) {
               String key = attributes.getQName(i);
               String value = attributes.getValue(i);
               element.addAttribute(key, value);
            }
            _elements.put(new Integer(_level), element);
         } else if (qName.equals("result")) {
            _errorCode = attributes.getValue("errorcode");
            if (_errorCode == null) {
               _errorCode = attributes.getValue("code");
            }
         } else if (qName.equals("param")) {
            _parameterKey = attributes.getValue("name");
            _pcdata = new FastStringBuffer(20);
         } else if (qName.equals("data")) {
            _elements = new Hashtable();
            _elements.put(new Integer(0), new DataElement("data"));
            _level = 0;
         } else {
            throw new SAXException("Starting to parse an unknown element \"" + qName + "\".");
         }
      }

      public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
         if (_level > 0) {
            DataElement child = (DataElement)_elements.get(new Integer(_level));
            if (_pcdata != null) {
               child.setPCData(_pcdata.toString());
            }
            _level--;
            DataElement parent = (DataElement)_elements.get(new Integer(_level));
            parent.addChild(child);
         } if (qName.equals("param")) {
            final String ELEMENT_NAME  = "param";
            final String KEY_ATTRIBUTE = "name";
            String value = _pcdata.toString();
            boolean noKey   = (_parameterKey == null || _parameterKey.length() < 1);
            boolean noValue = (value == null || value.length() < 1);
            if (noKey && noValue) {
               Log.log_2001(ELEMENT_NAME);
            } else if (noKey) {
               Log.log_2002(ELEMENT_NAME, KEY_ATTRIBUTE);
            } else if (noValue) {
               Log.log_2003(ELEMENT_NAME, KEY_ATTRIBUTE, _parameterKey);
            } else {

               Log.log_2004(ELEMENT_NAME, "name", _parameterKey, value);
               if (_parameters.get(_parameterKey) != null) {
                  throw new SAXException("The returned XML is invalid. Found <" + ELEMENT_NAME + "/> with duplicate " + KEY_ATTRIBUTE + " \"" + _parameterKey + "\" attribute.");
               }
               _parameters.put(_parameterKey, value);
            }
            _parameterKey = null;
            _pcdata = null;
         } else if (!qName.equals("result")) {
            throw new SAXException("Ending to parse an unknown element \"" + qName + "\".");
         }
      }

      public void characters(char[] ch, int start, int length) {
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
         return _errorCode;
      }

      /**
       * Get the parameters returned by the function.
       *
       * @return
       *    the parameters (name/value), cannot be <code>null</code>.
       */
      public PropertyReader getParameters() {
         return new PropertiesPropertyReader(_parameters);
      }

      /**
       * Get the data element returned by the function if any.
       *
       * @return
       *    the data element, or <code>null</code> if the function did not
       *    return any data element.
       */
      public DataElement getDataElement() {
         return (DataElement) _elements.get(new Integer(0));
      }
   }
}
