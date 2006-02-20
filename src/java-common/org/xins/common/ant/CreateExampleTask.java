/*
 * $Id$
 *
 * Copyright 2003-2006 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.ant;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.Writer;
import java.net.URL;
import java.net.URLDecoder;
import java.util.StringTokenizer;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import org.xins.common.io.FastStringWriter;
import org.xins.common.xml.Element;
import org.xins.common.xml.ElementBuilder;
import org.xins.common.xml.ElementParser;

/**
 * Apache Ant task that generates the specification code of an example based
 * on the request URL.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 *
 * @since XINS 1.4.0
 */
public class CreateExampleTask extends Task {

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
    * The URL to use to call the function.
    */
   private String _requestURL;

   /**
    * The name of the property in which the result should be stored.
    */
   private String _exampleProperty;

   /**
    * The name of the property in which the name of the function should be stored.
    */
   private String _functionProperty;

   /**
    * The location of the XSL style sheet to use to create the example.
    */
   private String _xslLocation;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Sets the URL to call the function.
    *
    * @param requestURL
    *    the name of the property to store the uppercase value.
    */
   public void setRequestURL(String requestURL) {
      _requestURL = requestURL;
   }

   /**
    * Sets the name of the property in which the result should be stored.
    *
    * @param exampleProperty
    *    the name of the property in which the result should be stored.
    */
   public void setExampleProperty(String exampleProperty) {
      _exampleProperty = exampleProperty;
   }

   /**
    * Sets the name of the property in which the name of the function should be stored.
    *
    * @param functionProperty
    *    the name of the property in which the result should be stored.
    */
   public void setFunctionProperty(String functionProperty) {
      _functionProperty = functionProperty;
   }

   /**
    * Sets the location of the XSL style sheet to use to create the example.
    *
    * @param xslLocation
    *    the name of the property in which the result should be stored.
    */
   public void setXslLocation(String xslLocation) {
      _xslLocation = xslLocation;
   }

   /**
    * Called by the project to let the task do its work.
    *
    * @throws BuildException
    *    if something goes wrong with the build.
    */
   public void execute() throws BuildException {

      checkAttributes();

      try {
         // Transform the URL request to XML
         Element requestXML = getRequestAsXML();
         log("request: " + requestXML.toString(), Project.MSG_VERBOSE);

         Element resultXML = getResultAsXML();
         log("result: " + resultXML.toString(), Project.MSG_VERBOSE);

         ElementBuilder builder = new ElementBuilder("combined");
         builder.addChild(requestXML);
         builder.addChild(resultXML);
         Element combined = builder.createElement();

         String example = transformElement(combined);
         getProject().setUserProperty(_exampleProperty, example);
      } catch (Exception ex) {
         throw new BuildException("Error: " + ex.getMessage());
      }
   }

   /**
    * Checks the attributes of the task.
    *
    * @throws BuildException
    *    if a required attribute is missing.
    */
   private void checkAttributes() throws BuildException {

      if (_requestURL == null) {
         throw new BuildException("The \"requestUrl\" attribute needs to be specified.");
      }

      if (_exampleProperty == null) {
         throw new BuildException("An \"exampleProperty\" attribute needs to be specified.");
      }

      if (_xslLocation == null) {
         throw new BuildException("An \"xslLocation\" attribute needs to be specified.");
      }

      if (getProject().getUserProperty(_exampleProperty) != null) {
         String message = "Override ignored for property \""
                        + _exampleProperty
                        + "\".";
         log(message, Project.MSG_VERBOSE);
      }

      if (_functionProperty != null && getProject().getUserProperty(_functionProperty) != null) {
         String message = "Override ignored for property \""
                        + _functionProperty
                        + "\".";
         log(message, Project.MSG_VERBOSE);
      }
   }

   /**
    * Transforms the request URL to an XML element.
    *
    * @return
    *    the query URL as XML.
    *
    * @throws Exception
    *    if the query is not a correct URL.
    */
   private Element getRequestAsXML() throws Exception {

      String queryString = _requestURL.substring(_requestURL.indexOf('?') + 1);
      ElementBuilder requestBuilder = new ElementBuilder("request");
      StringTokenizer stQuery = new StringTokenizer(queryString, "&");
      while (stQuery.hasMoreTokens()) {
         String nextParam = stQuery.nextToken();
         int equalPos = nextParam.indexOf('=');
         String paramName = nextParam.substring(0, equalPos);
         String paramValue = (equalPos == nextParam.length() - 1) ? "" : nextParam.substring(equalPos + 1);

         // Handle the _function parameter
         if (paramName.equals("_function")) {
            requestBuilder.setAttribute("function", paramValue);
            if (_functionProperty != null) {
               getProject().setUserProperty(_functionProperty, paramValue);
            }

         // Handle the _data parameter
         } else if (paramName.equals("_data")) {
            String dataSectionXML = URLDecoder.decode(paramValue, "UTF-8");
            requestBuilder.addXMLChild(dataSectionXML);

         // Handle the input parameters of the function
         } else if (paramName.charAt(0) != '_') {
            String paramXML = "<param name=\"" + paramName + "\">" + 
                  URLDecoder.decode(paramValue, "UTF-8") + "</param>";
            requestBuilder.addXMLChild(paramXML);
         }
      }
      return requestBuilder.createElement();
   }

   /**
    * Gets the result from the API.
    *
    * @return
    *    the result returned by the API when the request is performed.
    *
    * @throws Exception
    *    if the communication with the API fails or does not return an XMl element.
    */
   private Element getResultAsXML() throws Exception {
      // Get the result of the request
      URL url = new URL(_requestURL);
    
      // Read all the text returned by the server
      BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
      String result = "";
      String line;
      while ((line = in.readLine()) != null) {
         result += line;
      }
      in.close();

      // Combine the XML request with the result
      ElementParser parser = new ElementParser();
      Element resultXML = parser.parse(new StringReader(result));
      return resultXML;
   }

   /**
    * Transforms the given element using the stylesheet given to the task.
    *
    * @param combined
    *    the element resulting of the combination of the request and the result.
    *
    * @return
    *    the result of the transformation.
    *
    * @throws Exception
    *    if the XSLT cannot be read or the transformation fails.
    */
   private String transformElement(Element combined) throws Exception {

      // Creates the XSLT Transformer
      TransformerFactory factory = TransformerFactory.newInstance();
      Templates templates = factory.newTemplates(new StreamSource(
            new FileInputStream(_xslLocation)));
      Transformer xformer = templates.newTransformer();

      // Read the source and process it with the XSLT file
      Source source = new StreamSource(new StringReader(combined.toString()));
      Writer buffer = new FastStringWriter(1024);
      Result resultExample = new StreamResult(buffer);
      xformer.transform(source, resultExample);
      return buffer.toString();
   }
}