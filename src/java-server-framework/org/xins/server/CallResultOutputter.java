/*
 * $Id$
 *
 * Copyright 2004 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.collections.PropertyReader;
import org.xins.common.xml.Element;
import org.xins.common.xml.ElementSerializer;

import org.znerd.xmlenc.XMLOutputter;

/**
 * Transformer that is able to externalize a <code>FunctionResult</code> object to
 * XML.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.0.0
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
    * @throws IllegalArgumentException
    *    if <code>out      == null
    *          || encoding == null
    *          || result   == null</code>.
    *
    * @throws IOException
    *    if there was an I/O error while writing to the output stream.
    */
   public static void output(PrintWriter    out,
                             String         encoding,
                             FunctionResult result)
   throws IllegalArgumentException, IOException {

      // Check preconditions
      MandatoryArgumentChecker.check("out",      out,
                                     "encoding", encoding,
                                     "result",   result);

      // Create an XMLOutputter
      XMLOutputter xmlout = new XMLOutputter(out, encoding);

      // Output the declaration
      // XXX: Make it configurable whether the declaration is output or not?
      xmlout.declaration();

      // Write the result start tag
      xmlout.startTag("result");

      // Write the error code
      String code = result.getErrorCode();
      if (code != null) {
         xmlout.attribute("errorcode", code);
      }

      // Write the output parameters
      PropertyReader params = result.getParameters();
      if (params != null) {
         Iterator names = params.getNames();
         while (names.hasNext()) {
            String name  = (String) names.next();
            String value = params.get(name);

            xmlout.startTag("param");
            xmlout.attribute("name", name);
            xmlout.pcdata(value);
            xmlout.endTag(); // param
         }
      }

      // Write the data element
      Element dataElement = result.getDataElement();
      if (dataElement != null) {
         ElementSerializer serializer = new ElementSerializer();
         serializer.output(xmlout, dataElement);
      }

      xmlout.endTag(); // result
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
