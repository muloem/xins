/*
 * $Id$
 *
 * Copyright 2004 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.xml;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.io.FastStringWriter;
import org.znerd.xmlenc.XMLOutputter;

/**
 * Convert the Element to XML.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 *
 * @since XINS 1.1.0
 */
public class ElementSerializer {
   
   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Fully-qualified name of this class.
    */
   private static final String CLASSNAME = ElementSerializer.class.getName();

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Serializes the element to XML.
    *
    * @param element
    *    the element to serialize, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>element == null</code>.
    *
    * @throws IOException
    *    if there is an I/O error.
    */
   public static String serialize(Element element)
   throws IllegalArgumentException, IOException {

      // Check preconditions
      MandatoryArgumentChecker.check("element", element);

      // Create an XMLOutputter
      FastStringWriter out = new FastStringWriter();
      XMLOutputter xmlout = new XMLOutputter(out, "UTF-8");

      // Output the declaration
      // XXX: Make it configurable whether the declaration is output or not?
      xmlout.declaration();

      output(xmlout, element);

      out.close();

      return out.toString();
   }

   /**
    * Generates XML for the specified element.
    *
    * @param xmlout
    *    the {@link XMLOutputter} to use, cannot be <code>null</code>.
    *
    * @param element
    *    the {@link Element} object to convert to XML, cannot be
    *    <code>null</code>.
    *
    * @throws NullPointerException
    *    if <code>xmlout == null || element == null</code>.
    *
    * @throws IOException
    *    if there is an I/O error.
    */
   private static final void output(XMLOutputter xmlout, Element element)
   throws NullPointerException, IOException {

      // Start the tag
      xmlout.startTag(element.getLocalName());

      // Write the attributes
      Map attributes = element.getAttributeMap();
      Iterator names = attributes.keySet().iterator();
      while (names.hasNext()) {
         Element.QualifiedName qname  = (Element.QualifiedName) names.next();
         String name = qname.getLocalName();
         String value = (String) attributes.get(qname);
         xmlout.attribute(name, value);
      }

      // Process all contained elements and PCDATA sections
      List content = element.getChildElements();
      int count = content == null ? 0 : content.size();
      for (int i = 0; i < count; i++) {
         Object o = content.get(i);
         output(xmlout, (Element) o);
      }
      if (element.getText() != null) {
         xmlout.pcdata(element.getText());
      }

      // End the tag
      xmlout.endTag();
   }

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * This class only contains static method.
    */
   private ElementSerializer() {
   }
}
