/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;

import javax.xml.transform.URIResolver;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.xins.common.collections.InvalidPropertyValueException;
import org.xins.common.collections.MissingRequiredPropertyException;
import org.xins.common.collections.PropertyReader;
import org.xins.common.io.FastStringWriter;
import org.xins.common.manageable.InitializationException;

/**
 * XSLT calling convention.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
class XSLTCallingConvention extends StandardCallingConvention {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The runtime property name that define the base directory of the XSLT templates.
    */
   public final static String TEMPLATE_LOCATION_PROPERTY = "templates.callingconvention.source";
   
   /**
    * The input parameter that specify the location of the XSLT template tot use.
    */
   public final static String TEMPLATE_PARAMETER = "_template";
   
   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>XSLTCallingConvention</code> object.
    */
   XSLTCallingConvention() {
      
      // Creates the transformer factory
      _factory = TransformerFactory.newInstance();
      _factory.setURIResolver(new XsltURIResolver());
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * Location of the XSLT transformation Style Sheet.
    */
   private String _baseXSLTDir;

   /**
    * The XSLT transformer.
    */
   private final TransformerFactory _factory;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   protected void initImpl(PropertyReader buildSettings, PropertyReader runtimeProperties)
   throws MissingRequiredPropertyException,
          InvalidPropertyValueException,
          InitializationException {
      
      // Get the base directory of the Style Sheet
      // e.g. http://xslt.mycompany.com/myapi/
      // then the XSLT file must have the function names.
      _baseXSLTDir = runtimeProperties.get(TEMPLATE_LOCATION_PROPERTY);
      
      // Relative URLs use the user directory as base dir.
      if (_baseXSLTDir.indexOf("://") == -1) {
         try {
            String userDir = new File(System.getProperty("user.dir")).toURL().toString();
            _baseXSLTDir = userDir + _baseXSLTDir;
         } catch (IOException ioe) {
            // Ignore
         }
      }
   }

   protected void convertResultImpl(FunctionResult      xinsResult,
                                    HttpServletResponse httpResponse,
                                    HttpServletRequest  httpRequest)
   throws IOException {

      // Send the XML output to the stream and flush
      FastStringWriter xmlOutput = new FastStringWriter();
      CallResultOutputter.output(xmlOutput, RESPONSE_ENCODING, xinsResult, false);
      xmlOutput.close();
   
      String xsltLocation = httpRequest.getParameter(TEMPLATE_PARAMETER);
      if (xsltLocation == null) {
         xsltLocation = _baseXSLTDir + httpRequest.getParameter("_function") + ".xslt";
      }
      try {
         Templates template = _factory.newTemplates(new StreamSource(
           new URL(xsltLocation).openStream()));
         Transformer xformer = template.newTransformer();
         Source source = new StreamSource(new StringReader(xmlOutput.toString()));
         Writer buffer = new FastStringWriter();
         Result result = new StreamResult(buffer);
         xformer.transform(source, result);

         PrintWriter out = httpResponse.getWriter();
         
         // Determine the MIME type for the output.
         String mimeType = template.getOutputProperties().getProperty("media-type");
         if (mimeType == null) {
            String method = template.getOutputProperties().getProperty("method");
            if ("xml".equals(method)) {
               mimeType = "text/xml";
            } else if ("html".equals(method)) {
               mimeType.equals("text/html");
            } else if ("text".equals(method)) {
               mimeType.equals("text/plain");
            }
         }
         String encoding = template.getOutputProperties().getProperty("encoding");
         if (mimeType != null && encoding != null) {
            mimeType += ";charset=" + encoding;
         }
         if (mimeType != null) {
            httpResponse.setContentType(mimeType);
         }
         
         httpResponse.setStatus(HttpServletResponse.SC_OK);
         out.print(buffer.toString());
         out.close();
      } catch (Exception ex) {
         ex.printStackTrace();
         throw new IOException(ex.getMessage());
      }
   }

   /**
    * Class used to revolved URL locations when an SLT file refers to another XSLT file using a relative URL.
    */
   class XsltURIResolver implements URIResolver {

      /**
       * Revolve a hyperlink reference.
       *
       * @param href
       *    The hyperlink to resolve.
       * @param base
       *    The base URI in effect when the href attribute was encountered.
       *
       * @return
       *    A Source object, or <code>null</code> if the href cannot be resolved,
       *    and the processor should try to resolve the URI itself.
       *
       * @throws TransformerException
       *    If an error occurs when trying to resolve the URI.
       */
      public Source resolve(String href, String base) throws TransformerException {
         if (base == null) {
            base = _baseXSLTDir;
         }
         if (href.startsWith("../")) {
            int lastSlash = base.lastIndexOf('/');
            int secondLastSlash = base.lastIndexOf('/', lastSlash - 1);
            href = base.substring(0, secondLastSlash) + href.substring(2);
         } else if (!href.startsWith("http://")) {
            int lastSlash = base.lastIndexOf('/');
            href = base.substring(0, lastSlash + 1) + href;
         }
         try {
            return new StreamSource(new URL(href).openStream());
         } catch (IOException ioe) {
            ioe.printStackTrace();
            return _factory.getURIResolver().resolve(href, base);
         }
      }
   }
}
