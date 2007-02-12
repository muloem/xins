/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.io.Writer;
import java.util.Iterator;
import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.collections.PropertyReader;
import org.xins.common.xml.Element;
import org.xins.common.xml.ElementSerializer;
import org.xins.logdoc.ExceptionUtils;
import org.znerd.xmlenc.XMLEncoder;
import org.znerd.xmlenc.XMLOutputter;

/**
 * Converter that can be used by calling conventions to generate responses
 * which are compatible with the XINS standard calling convention.
 *
 * <p>The result output is always in the UTF-8 encoding.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 * @author <a href="mailto:anthony.goubard@orange-ftgroup.com">Anthony Goubard</a>
 *
 * @since XINS 1.5.0
 */
public final class CallResultOutputter {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The first output for each output conversion. Never <code>null</code>.
    */
   private static final char[] DOCUMENT_PREFACE =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?><result".toCharArray();

   /**
    * The output for the old-style calling convention in case success is true.
    * Never <code>null</code>.
    */
   private static final char[] SUCCESS_TRUE =
      " success=\"true\">".toCharArray();

   /**
    * The output for the old-style calling convention in case success is
    * false, just before the name of the first error code.
    * Never <code>null</code>.
    */
   private static final char[] SUCCESS_FALSE_PREFIX =
      " success=\"false\" code=\"".toCharArray();

   /**
    * The output for the old-style calling convention in case success is
    * false, just after the name of the first error code and before the name
    * of the second.
    * Never <code>null</code>.
    */
   private static final char[] SUCCESS_FALSE_MIDDLE =
      "\" errorcode=\"".toCharArray();

   /**
    * The output for the new-style calling convention in case success is
    * false, just before the name of the error code.
    * Never <code>null</code>.
    */
   private static final char[] ERRORCODE_IS =
      " errorcode=\"".toCharArray();

   /**
    * The output just before a parameter name. Never <code>null</code>.
    */
   private static final char[] PARAM_PREFACE = "<param name=\"".toCharArray();

   /**
    * The output right after a parameter value. Never <code>null</code>.
    */
   private static final char[] PARAM_SUFFIX = "</param>".toCharArray();

   /**
    * The final output for each output conversion. Never <code>null</code>.
    */
   private static final char[] DOCUMENT_SUFFIX = "</result>".toCharArray();

   /**
    * An <code>XMLEncoder</code> for the UTF-8 encoding. Initialized by the
    * class initialized and then never <code>null</code>.
    */
   private static final XMLEncoder XML_ENCODER;


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   static {
      try {
         XML_ENCODER = XMLEncoder.getEncoder("UTF-8");
      } catch (UnsupportedEncodingException exception) {
         Error error = new Error();
         ExceptionUtils.setCause(error, exception);
         throw error;
      }
   }

   /**
    * Generates XML for the specified call result. The XML is sent to the
    * specified output stream.
    *
    * @param out
    *    the output stream to send the XML to, cannot be <code>null</code>.
    *
    * @param result
    *    the call result to convert to XML, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>out      == null
    *          || result   == null</code>.
    *
    * @throws IOException
    *    if there was an I/O error while writing to the output stream.
    */
   public static void output(Writer out, FunctionResult result)
   throws IllegalArgumentException, IOException {
      output(out, result, false);
   }

   /**
    * Generates XML for the specified call result. The XML is sent to the
    * specified output stream.
    *
    * @param out
    *    the output stream to send the XML to, cannot be <code>null</code>.
    *
    * @param result
    *    the call result to convert to XML, cannot be <code>null</code>.
    *
    * @param oldStyle
    *    flag that indicates if old-style output should be generated or not.
    *
    * @throws IllegalArgumentException
    *    if <code>out      == null
    *          || result   == null</code>.
    *
    * @throws IOException
    *    if there was an I/O error while writing to the output stream.
    */
   static void output(Writer out, FunctionResult result, boolean oldStyle)
   throws IllegalArgumentException, IOException {

      // Check preconditions
      MandatoryArgumentChecker.check("out", out, "result", result);

      // Output the declaration
      out.write(DOCUMENT_PREFACE);

      // Output the start of the <result> element
      String code = result.getErrorCode();
      if (oldStyle) {
         if (code == null) {
            out.write(SUCCESS_TRUE);
         } else {
            out.write(SUCCESS_FALSE_PREFIX);
            out.write(code);
            out.write(SUCCESS_FALSE_MIDDLE);
            out.write(code);
            out.write('"');
            out.write('>');
         }
      } else {
         if (code == null) {
            out.write('>');
         } else {
            out.write(ERRORCODE_IS);
            out.write(code);
            out.write('"');
            out.write('>');
         }
      }

      // Write the output parameters, if any
      PropertyReader params = result.getParameters();
      if (params != null) {
         Iterator names = params.getNames();
         while (names.hasNext()) {
            String n = (String) names.next();
            if (n != null && n.length() > 0) {
               String v = params.get(n);
               if (v != null && v.length() > 0) {
                  out.write(PARAM_PREFACE);
                  XML_ENCODER.text(out, n, true);
                  out.write('"');
                  out.write('>');
                  XML_ENCODER.text(out, v, true);
                  out.write(PARAM_SUFFIX);
               }
            }
         }
      }

      // Write the data element, if any
      Element dataElement = result.getDataElement();
      if (dataElement != null) {
         ElementSerializer serializer = new ElementSerializer();
         XMLOutputter xmlout = new XMLOutputter(out, "UTF-8");
         serializer.output(xmlout, dataElement);
      }

      // End the root element <result>
      out.write(DOCUMENT_SUFFIX);
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>CallResultOutputter</code> object.
    */
   private CallResultOutputter() {
      // empty
   }
}
