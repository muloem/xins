/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.collections.PropertyReader;
import org.xins.common.io.FastStringWriter;
import org.xins.common.text.TextUtils;
import org.xins.common.xml.Element;
import org.xins.common.xml.ElementSerializer;

import org.znerd.xmlenc.XMLOutputter;

/**
 * Transformer that is able to externalize a <code>FunctionResult</code> object to
 * XML.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
final class CallResultOutputter extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Generates XML for the specified call result. The XML is sent to the
    * specified output stream.
    *
    * @param out
    *    the output stream to send the XML to, cannot be <code>null</code>.
    *
    * @param encoding
    *    the encoding format for the XML, cannot be <code>null</code>.
    *
    * @param result
    *    the call result to convert to XML, cannot be <code>null</code>.
    *
    * @param oldStyle
    *    flag that indicates if old-style output should be generated or not.
    *
    * @throws IllegalArgumentException
    *    if <code>out      == null
    *          || encoding == null
    *          || result   == null</code>.
    *
    * @throws IOException
    *    if there was an I/O error while writing to the output stream.
    */
   public static void output(Writer         out,
                             String         encoding,
                             FunctionResult result,
                             boolean        oldStyle)
   throws IllegalArgumentException, IOException {

      // TODO: Support OutputStream instead of Writer

      // Check preconditions
      MandatoryArgumentChecker.check("out",      out,
                                     "encoding", encoding,
                                     "result",   result);

      // Store the result in a StringWriter before sending it.
      Writer buffer = new FastStringWriter();

      // Create an XMLOutputter
      XMLOutputter xmlout = new XMLOutputter(buffer, encoding);

      // Output the declaration
      // XXX: Make it configurable whether the declaration is output or not?
      xmlout.declaration();

      // Write the result start tag
      xmlout.startTag("result");

      // Write the error code
      String code = result.getErrorCode();
      if (oldStyle) {
         xmlout.attribute("success", code == null ? "true" : "false");
         if (code != null) {
            xmlout.attribute("code", code);
         }
      }

      // TODO: Only print the 'errorcode' attribute if not oldStyle?
      if (code != null) {
         xmlout.attribute("errorcode", code);
      }

      // Write the output parameters
      PropertyReader params = result.getParameters();
      if (params != null) {
         Iterator names = params.getNames();
         while (names.hasNext()) {
            String name  = (String) names.next();
            if (! TextUtils.isEmpty(name)) {
               String value = params.get(name);

               if (! TextUtils.isEmpty(value)) {
                  xmlout.startTag("param");
                  xmlout.attribute("name", name);
                  xmlout.pcdata(value);
                  xmlout.endTag(); // param
               }
            }
         }
      }

      // Write the data element
      Element dataElement = result.getDataElement();
      if (dataElement != null) {
         ElementSerializer serializer = new ElementSerializer();
         serializer.output(xmlout, dataElement);
      }

      xmlout.endTag(); // result

      // Write the result to the servlet response
      out.write(buffer.toString());
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
