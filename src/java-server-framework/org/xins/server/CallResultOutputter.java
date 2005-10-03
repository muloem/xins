/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Iterator;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.collections.PropertyReader;
import org.xins.common.io.FastStringWriter;
import org.xins.common.text.FastStringBuffer;
import org.xins.common.xml.Element;
import org.xins.common.xml.ElementSerializer;
import org.xins.logdoc.ExceptionUtils;

import org.znerd.xmlenc.XMLEncoder;
import org.znerd.xmlenc.XMLOutputter;

/**
 * Transformer that is able to convert a <code>FunctionResult</code> object to
 * XML.
 *
 * <p>The result output is always in the UTF-8 encoding.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
final class CallResultOutputter extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The initial output for each output conversion. Never <code>null</code>.
    */
   private static final char[] PREFACE =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<result".toCharArray();

   /**
    * Per-thread caches for the <code>FastStringWriter</code> instances. Never
    * <code>null</code>.
    */
   private static final ThreadLocal WRITER = new ThreadLocal();

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
   public static void output(Writer         out,
                             FunctionResult result,
                             boolean        oldStyle)
   throws IllegalArgumentException, IOException {

      // TODO: Support OutputStream instead of Writer

      // Check preconditions
      MandatoryArgumentChecker.check("out", out, "result", result);

      // Store the result in a buffer before sending it. Reserve 4 KB.
      FastStringWriter writer;
      Object o = WRITER.get();
      if (o == null) {
         writer = new FastStringWriter(4096);
         WRITER.set(writer);
      } else {
         writer = (FastStringWriter) o;
      }
      FastStringBuffer buffer = writer.getBuffer();

      // Output the declaration
      buffer.append(PREFACE);

      String code = result.getErrorCode();
      if (oldStyle) {
         if (code == null) {
            buffer.append(" success=\"true\">");
         } else {
            buffer.append(" success=\"false\" code=\"");
            buffer.append(code);
            buffer.append("\" errorcode=\"");
            buffer.append(code);
            buffer.append("\">");
         }
      } else {
         if (code == null) {
            buffer.append('>');
         } else {
            buffer.append(" errorcode=\"");
            buffer.append(code);
            buffer.append("\">");
         }
      }

      // Write the output parameters, if any
      PropertyReader params = result.getParameters();
      if (params != null) {
         Iterator names = params.getNames();
         while (names.hasNext()) {
            String n = (String) names.next();
            if (n != null && n.length() > 0 && n.trim().length() > 0) {
               String v = params.get(n);
               if (v != null && (v.length() > 0) && v.trim().length() > 0) {

                  // TODO: Put this in a static char[]
                  buffer.append("\n<param name=\"");

                  // Encode and output the name
                  XML_ENCODER.text(writer, n, true);

                  // TODO: Put this in a static char[]
                  buffer.append("\">");

                  // Encode and output the value
                  XML_ENCODER.text(writer, v, true);

                  // TODO: Put this in a static char[]
                  buffer.append("</param>");
               }
            }
         }
      }

      // Write the data element, if any
      Element dataElement = result.getDataElement();
      if (dataElement != null) {
         ElementSerializer serializer = new ElementSerializer();
         XMLOutputter xmlout = new XMLOutputter(writer, "UTF-8");
         serializer.output(xmlout, dataElement);
      }

      // End the root element <result>
      // TODO: Put this in a static char[]
      buffer.append("</result>");

      // Write the result to the servlet response
      String s = buffer.toString();
      writer.getBuffer().clear();
      out.write(s);
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


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
