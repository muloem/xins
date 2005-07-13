/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.servlet.container;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xins.common.text.FastStringBuffer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class is an implementation of the ServletConfig that can be
 * called locally.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public class LocalServletConfig implements ServletConfig {

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Creates a new Servlet configuration.
    *
    * @param warFileLocation
    *    the war file containing the servlet to deploy,
    *    cannot be <code>null</code>.
    */
   public LocalServletConfig(File warFileLocation) {
      _warFile = warFileLocation;
      _initParameters = new Properties();
      _context = new XINSServletContext(this);

      try {
         JarFile warFile = new JarFile(warFileLocation);
         JarEntry webxmlEntry = warFile.getJarEntry("WEB-INF/web.xml");
         InputStream webxmlInputStream = warFile.getInputStream(webxmlEntry);
         parseWebXML(webxmlInputStream);
      } catch (Exception ex) {
         
         // try again without the DTD
         try {
            JarFile warFile = new JarFile(warFileLocation);
            JarEntry webxmlEntry = warFile.getJarEntry("WEB-INF/web.xml");
            InputStream webxmlInputStream = warFile.getInputStream(webxmlEntry);
            byte[] webXMLContent = new byte[webxmlInputStream.available()];
            webxmlInputStream.read(webXMLContent);
            String webXMLContentString = new String(webXMLContent, "UTF-8");
            int beginDTD = webXMLContentString.indexOf("<!DOCTYPE web-app");
            int endDTD = webXMLContentString.indexOf(".dtd\">", beginDTD) + 6;
            String webXMLWithoutDTD = webXMLContentString.substring(0, beginDTD) +
                  webXMLContentString.substring(endDTD);
            ByteArrayInputStream baisWebXML = new ByteArrayInputStream(webXMLWithoutDTD.getBytes("UTF-8"));
            parseWebXML(baisWebXML);
         } catch (Exception ex2) {
            
            // TODO log
            ex2.printStackTrace();
         }
      }
   }

   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The name of the servlet.
    */
   private String _servletName;

   /**
    * The class of the servlet.
    */
   private String _servletClass;

   /**
    * The properties of the servlet.
    */
   private Properties _initParameters;

   /**
    * The servlet context.
    */
   private ServletContext _context;
   
   /**
    * The WAR file.
    */
   private File _warFile;
   

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Parses the web.xml file.
    *
    * @param webxmlInputStream
    *    the web.xml file input stream.
    *
    * @throws Exception
    *    if the file cannot be parsed for any reason.
    */
   private void parseWebXML(InputStream webxmlInputStream) throws Exception {
      DefaultHandler handler = new WebInfoParser();
      SAXParserFactory factory = SAXParserFactory.newInstance();
      factory.setValidating(false);
      SAXParser saxParser = factory.newSAXParser();
      saxParser.parse(webxmlInputStream, handler);
      webxmlInputStream.close();
   }
   
   public String getInitParameter(String param) {
      return _initParameters.getProperty(param);
   }

   public String getServletName() {
      return _servletName;
   }

   /**
    * Gets the class name of the Servlet.
    *
    * @return
    *    the class name of the servlet, cannot be <code>null</code>.
    */
   public String getServletClass() {
      return _servletClass;
   }

   public ServletContext getServletContext() {
      return _context;
   }

   public Enumeration getInitParameterNames() {
      return _initParameters.keys();
   }

   /**
    * Gets the WAR file location.
    *
    * @return
    *    the WAR file, never <code>null</code>
    */
   File getWarFile() {
      return _warFile;
   }
   
   /**
    * Parser for the web.xml containing the information about the Servlet.
    */
   private class WebInfoParser extends DefaultHandler {

      //-------------------------------------------------------------------------
      // Class functions
      //-------------------------------------------------------------------------

      //-------------------------------------------------------------------------
      // Class fields
      //-------------------------------------------------------------------------

      //-------------------------------------------------------------------------
      // Constructor
      //-------------------------------------------------------------------------

      //-------------------------------------------------------------------------
      // Fields
      //-------------------------------------------------------------------------

      /**
       * The PCDATA element of the tag that is actually parsed.
       */
      private FastStringBuffer _pcdata;

      /**
       * The name of the property that is currently parsed.
       */
      private String _paramName;


      //-------------------------------------------------------------------------
      // Methods
      //-------------------------------------------------------------------------

      public void startElement(String     namespaceURI,
                               String     localName,
                               String     qName,
                               Attributes atts)
      throws IllegalArgumentException, SAXException {
         _pcdata = new FastStringBuffer(80);
      }

      public void endElement(String namespaceURI,
                             String localName,
                             String qName)
      throws IllegalArgumentException, SAXException {
         if (qName.equals("param-name")) {
            _paramName = _pcdata.toString();
         } else if (qName.equals("param-value")) {
            _initParameters.setProperty(_paramName, _pcdata.toString());
         } else if (qName.equals("servlet-name")) {
            _servletName = _pcdata.toString();
         } else if (qName.equals("servlet-class")) {
            _servletClass = _pcdata.toString();
         }
         _pcdata = null;
      }

      public void characters(char[] ch, int start, int length)
      throws IndexOutOfBoundsException {

         if (_pcdata != null) {
            _pcdata.append(ch, start, length);
         }
      }
   }
}
