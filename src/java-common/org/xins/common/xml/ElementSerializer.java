/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.xml;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.Utils;
import org.znerd.xmlenc.XMLOutputter;

/**
 * Serializer that takes an <code>Element</code> and converts it to an XML
 * string.
 *
 * <p>This class is not thread-safe. It should only be used on one thread at a
 * time.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:anthony.goubard@orange-ftgroup.com">Anthony Goubard</a>
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.1.0
 */
public final class ElementSerializer {

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>ElementSerializer</code>.
    */
   public ElementSerializer() {
      // TODO: Set _instanceNumber
      _lock       = new Object();
      _namespaces = new HashMap();
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * Lock object that is synchronized on when reading or writing
    * <code>_inUse</code>.
    */
   private final Object _lock;

   /**
    * Flag that indicates whether this serializer is currently in use. It may
    * only be used by one thread at a time.
    */
   private boolean _inUse;

   /**
    * Map that links namespaces to their prefixes. Each key is a namespace and
    * the corresponding value is the associated prefix.
    */
   private HashMap _namespaces;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Serializes the element to XML. This method is not reentrant. Hence, it
    * should only be used from a single thread.
    *
    * @param element
    *    the element to serialize, cannot be <code>null</code>.
    *
    * @return
    *    an XML document that represents <code>element</code>, never
    *    <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>element == null</code>.
    */
   public String serialize(Element element)
   throws IllegalArgumentException {

      synchronized (_lock) {

         // Make sure this serializer is not yet in use
         if (_inUse) {
            // TODO: Use _instanceNumber in message
            String detail = "ElementSerializer instance already in use.";
            throw Utils.logProgrammingError(detail);
         }

         // Lock this serializer
         _inUse = true;
      }

      // Check argument
      MandatoryArgumentChecker.check("element", element);

      // Create an XMLOutputter
      Writer fsw = new StringWriter(512);
      XMLOutputter out;
      final String ENCODING = "UTF-8";
      try {
         out = new XMLOutputter(fsw, ENCODING);
      } catch (UnsupportedEncodingException uee) {
         String message = "Expected XMLOutputter to support encoding \"" + ENCODING + "\".";
         throw Utils.logProgrammingError(message, uee);
      }

      // XXX: Allow output of declaration to be configured?

      // Output the XML that represents the Element
      try {
         output(out, element);

      // I/O errors should not happen on a StringWriter
      } catch (IOException exception) {
         throw Utils.logProgrammingError(exception);

      } finally {
         _namespaces.clear();
         _inUse = false;
      }

      String xml = fsw.toString();

      return xml;
   }

   /**
    * Generates XML for the specified <code>Element</code>.
    *
    * @param out
    *    the {@link XMLOutputter} to use, cannot be <code>null</code>.
    *
    * @param element
    *    the {@link Element} object to convert to XML, cannot be
    *    <code>null</code>.
    *
    * @throws NullPointerException
    *    if <code>out == null || element == null</code>.
    *
    * @throws IOException
    *    if there is an I/O error.
    */
   public void output(XMLOutputter out, Element element)
   throws NullPointerException, IOException {

      String namespaceURI = element.getNamespaceURI();
      String localName    = element.getLocalName();

      // Write an element with namespace
      if (namespaceURI != null) {

         // Determine if the namespace already has an assigned prefix
         String prefix = (String) _namespaces.get(namespaceURI);
         boolean newNamespace = (prefix == null);
         if (newNamespace) {

            // Generate a namespace prefix
            prefix = "ns" + _namespaces.size();

            // Remember the association of the namespace with the prefix
            _namespaces.put(namespaceURI, prefix);
         }

         // Write the start tag with prefix
         out.startTag(prefix + ':' + localName);

         // Associate the namespace with the prefix in the result XML
         if (newNamespace) {
            out.attribute("xmlns:" + prefix, namespaceURI);
         }

      // Write an element without namespace
      } else {
         out.startTag(localName);
      }

      // Loop through all attributes
      Map attributes = element.getAttributeMap();
      Iterator entries = attributes.entrySet().iterator();
      while (entries.hasNext()) {

         // Get the next Map.Entry from the iterator
         Map.Entry entry = (Map.Entry) entries.next();

         // Get the namespace, local name and value
         Element.QualifiedName qn = (Element.QualifiedName) entry.getKey();
         String attrNamespaceURI  = qn.getNamespaceURI();
         String attrLocalName     = qn.getLocalName();
         String attrValue         = (String) entry.getValue();

         // Write an attribute with namespace
         if (attrNamespaceURI != null) {

            // Determine if the namespace already has an assigned prefix
            String prefix = (String) _namespaces.get(attrNamespaceURI);
            boolean newNamespace = (prefix == null);
            if (newNamespace) {

               // Generate a namespace prefix
               prefix = "ns" + _namespaces.size();

               // Remember the association of the namespace with the prefix
               _namespaces.put(namespaceURI, prefix);
            }

            // Write the attribute with prefix
            out.attribute(prefix + ':' + attrLocalName, attrValue);

            // Associate the namespace with the prefix in the result XML
            if (newNamespace) {
               out.attribute("xmlns:" + prefix, attrNamespaceURI);
            }

         // Write an attribute without namespace
         } else {
            out.attribute(attrLocalName, attrValue);
         }
      }

      // Process all contained elements
      List content = element.getChildElements();
      int count = content.size();
      for (int i = 0; i < count; i++) {
         Object o = content.get(i);
         output(out, (Element) o);
      }

      // Output contained PCDATA
      if (element.getText() != null) {
         out.pcdata(element.getText());
      }

      // End the tag
      out.endTag();
   }
}
