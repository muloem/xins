/*
 * $Id$
 */
package org.xins.client;

import java.io.ByteArrayInputStream;
import java.util.Hashtable;
import java.util.Properties;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
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
 * Call result parser. XML is parsed to produce a
 * {@link XINSServiceCaller.Result} object.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 *
 * @since XINS 0.203
 */
public class ResultParser {

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
    * <code>XINSServiceCaller.Result</code> object with the specified
    * <code>TargetDescriptor</code>.
    *
    * @param request
    *    the original {@link CallRequest} that was used to perform the call,
    *    cannot be <code>null</code>.
    *
    * @param target
    *    the {@link TargetDescriptor} that was used to get the XML, cannot be
    *    <code>null</code>.
    *
    * @param duration
    *    the call duration, should be &gt;= 0.
    *
    * @param xmlStream
    *    the input stream of the XML to be parsed, not <code>null</code>.
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
   public XINSServiceCaller.Result parse(CallRequest      request,
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

      ResultHandler handler = new ResultHandler();
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
      } finally {
         //xmlStream.close();
      }

      return new XINSServiceCaller.Result(request, target, duration, handler.getErrorCode(), handler.getParameters(), handler.getDataElement());
   }

   //-------------------------------------------------------------------------
   // Inner classes
   //-------------------------------------------------------------------------

   class ResultHandler extends DefaultHandler {

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

      private String _errorCode;

      private Properties _parameters = new Properties();
      private String _parameterKey;
      private FastStringBuffer _pcdata;

      //private DataElement _dataElement;
      private Hashtable _elements = new Hashtable();
      private int _level = -1;

      //-------------------------------------------------------------------------
      // Methods
      //-------------------------------------------------------------------------

      // todo throw the parse exception
      public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
         if (_level >= 0) {
            _level++;
            DataElement element = new DataElement();

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
            _elements.put(new Integer(0), new DataElement());
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

      public String getErrorCode() {
         return _errorCode;
      }

      public PropertyReader getParameters() {
         return new PropertiesPropertyReader(_parameters);
      }

      public DataElement getDataElement() {
         return (DataElement) _elements.get(new Integer(0));
      }
   }
}
