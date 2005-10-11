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
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.URIResolver;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xins.common.Utils;
import org.xins.common.collections.InvalidPropertyValueException;
import org.xins.common.collections.MissingRequiredPropertyException;
import org.xins.common.collections.PropertyReader;
import org.xins.common.io.FastStringWriter;
import org.xins.common.manageable.InitializationException;
import org.xins.common.text.TextUtils;

/**
 * XSLT calling convention.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
class XSLTCallingConvention extends StandardCallingConvention {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The name of the runtime property that defines if the templates should be
    * cached. Should be either <code>"true"</code> or <code>"false"</code>.
    */
   public final static String TEMPLATES_CACHE_PROPERTY = "templates.cache";

   /**
    * The name of the runtime property that defines the location of the XSLT
    * templates. Should indicate a directory, either locally or remotely.
    * Local locations will be interpreted as relative to the user home
    * directory.
    *
    * <p>Examples of valid locations include:
    *
    * <ul>
    * <li><code>projects/dubey/xslt/</code></li>
    * <li><code>/home/john.doe/projects/dubey/xslt/</code></li>
    * <li><code>file:///home/john.doe/projects/dubey/xslt/</code></li>
    * <li><code>http://johndoe/projects/dubey/xslt/</code></li>
    * <li><code>https://xslt.johndoe/</code></li>
    * </ul>
    */
   public final static String TEMPLATES_LOCATION_PROPERTY = "templates.callingconvention.source";

   /**
    * The name of the input parameter that specifies the location of the XSLT
    * template to use.
    */
   public final static String TEMPLATE_PARAMETER = "_template";

   /**
    * The name of the input parameter used to clear the template cache.
    */
   public final static String CLEAR_TEMPLATE_CACHE_PARAMETER = "_cleartemplatecache";

   /**
    * Cache for the XSLT templates. Never <code>null</code>.
    */
   private final static Map TEMPLATE_CACHE = new HashMap(89);
   // FIXME: Make TEMPLATE_CACHE an instance variable


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
    * The XSLT transformer. Never <code>null</code>.
    */
   private final TransformerFactory _factory;

   /**
    * Flag that indicates whether the templates should be cached. This field
    * is set during initialization.
    */
   private boolean _cacheTemplates;

   /**
    * Location of the XSLT templates. This field is initially
    * <code>null</code> and set during initialization.
    */
   private String _location;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   protected void initImpl(PropertyReader runtimeProperties)
   throws MissingRequiredPropertyException,
          InvalidPropertyValueException,
          InitializationException {

      // Determine if the template cache should be enabled
      String propName  = TEMPLATES_CACHE_PROPERTY;
      String propValue = runtimeProperties.get(propName);

      // By default, the template cache is enabled
      if (TextUtils.isEmpty(propValue)) {
         _cacheTemplates = true;
      } else {
         propValue = propValue.trim();
         if ("true".equals(propValue)) {
            _cacheTemplates = true;
         } else if ("false".equals(propValue)) {
            _cacheTemplates = false;
         } else {
            throw new InvalidPropertyValueException(propName, propValue,
               "Expected either \"true\" or \"false\".");
         }
      }

      // Log whether the cache is enabled or not
      if (_cacheTemplates) {
         Log.log_3440();
      } else {
         Log.log_3441();
      }

      // Get the base directory of the Style Sheet
      // e.g. http://xslt.mycompany.com/myapi/
      // then the XSLT file must have the function names.
      _location = runtimeProperties.get(TEMPLATES_LOCATION_PROPERTY);

      // Relative URLs use the user directory as base dir.
      if (TextUtils.isEmpty(_location) || _location.indexOf("://") == -1) {

         // Trim the location and make sure it's never null
         _location = TextUtils.trim(_location);

         // Attempt to convert the home directory to a URL
         String home    = System.getProperty("user.dir");
         String homeURL = "";
         try {
            homeURL = new File(home).toURL().toString();

         // If the conversion to a URL failed, then just use the original
         } catch (IOException exception) {
            Utils.logIgnoredException(
               XSLTCallingConvention.class.getName(), "initImpl",
               "java.io.File",                        "toURL()",
               exception);
         }

         // Prepend the home directory URL
         _location = homeURL + _location;
      }

      // Log the base directory for XSLT templates
      Log.log_3442(_location);
   }

   protected void convertResultImpl(FunctionResult      xinsResult,
                                    HttpServletResponse httpResponse,
                                    HttpServletRequest  httpRequest)
   throws IOException {

      // Send the XML output to the stream and flush
      FastStringWriter xmlOutput = new FastStringWriter();
      CallResultOutputter.output(xmlOutput, xinsResult, false);
      xmlOutput.close();

      String xsltLocation = httpRequest.getParameter(TEMPLATE_PARAMETER);
      if (xsltLocation == null) {
         xsltLocation = _location + httpRequest.getParameter("_function") + ".xslt";
      }
      try {
         Templates t = null;
         if ("true".equals(httpRequest.getParameter(CLEAR_TEMPLATE_CACHE_PARAMETER))) {
            TEMPLATE_CACHE.clear();
            PrintWriter out = httpResponse.getWriter();
            out.write("Done.");
            out.close();
            return;
         }
         if (!_cacheTemplates && TEMPLATE_CACHE.containsKey(xsltLocation)) {
            t = (Templates) TEMPLATE_CACHE.get(xsltLocation);
         } else {
            t = _factory.newTemplates(_factory.getURIResolver().resolve(xsltLocation, _location));
            if (_cacheTemplates) {
               TEMPLATE_CACHE.put(xsltLocation, t);
            }
         }
         Transformer xformer = t.newTransformer();
         Source source = new StreamSource(new StringReader(xmlOutput.toString()));
         Writer buffer = new FastStringWriter(1024);
         Result result = new StreamResult(buffer);
         xformer.transform(source, result);

         PrintWriter out = httpResponse.getWriter();

         // Determine the MIME type for the output.
         String mimeType = t.getOutputProperties().getProperty("media-type");
         if (mimeType == null) {
            String method = t.getOutputProperties().getProperty("method");
            if ("xml".equals(method)) {
               mimeType = "text/xml";
            } else if ("html".equals(method)) {
               mimeType = "text/html";
            } else if ("text".equals(method)) {
               mimeType = "text/plain";
            }
         }
         String encoding = t.getOutputProperties().getProperty("encoding");
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

   //-------------------------------------------------------------------------
   // Inner classes
   //-------------------------------------------------------------------------

   /**
    * Class used to revolved URL locations when an SLT file refers to another
    * XSLT file using a relative URL.
    *
    * @version $Revision$ $Date$
    * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
    */
   class XsltURIResolver implements URIResolver {

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      /**
       * The previous base URL, if any. Can be <code>null</code>.
       */
      private String _base;


      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------

      /**
       * Revolve a hyperlink reference.
       *
       * @param href
       *    the hyperlink to resolve.
       *
       * @param base
       *    the base URI in effect when the href attribute was encountered.
       *
       * @return
       *    a {@link Source} object, or <code>null</code> if the href cannot
       *    be resolved, and the processor should try to resolve the URI
       *    itself.
       *
       * @throws TransformerException
       *    if an error occurs when trying to resolve the URI.
       */
      public Source resolve(String href, String base)
      throws TransformerException {

         if (base == null) {
            base = _base;
         } else if (! base.endsWith("/")) {
            base += '/';
         }
         _base = base;
         String url = null;
         if (href.indexOf(":/") == -1) {
            url = base + href;
         } else {
            url = href;
            _base = href.substring(0, href.lastIndexOf('/') + 1);
         }

         try {
            return new StreamSource(new URL(url).openStream());
         } catch (IOException ioe) {
            ioe.printStackTrace();
            throw new TransformerException(ioe);
         }
      }
   }
}
