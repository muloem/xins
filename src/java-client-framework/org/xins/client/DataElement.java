/*
 * $Id$
 */
package org.xins.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.xins.common.collections.BasicPropertyReader;

/**
 * The data element received from the server when any.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 *
 * @since XINS 0.203
 */
public class DataElement implements Cloneable {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Creates a new DataElement with the specified qualified name.
    *
    * @param qName
    *    the name of the element, cannot be <code>null</code>.
    */
   DataElement(String qName) {
      _name = qName;
      _children = new ArrayList();
      _attributes = new BasicPropertyReader();
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The name of this element.
    */
   private final String _name;

   /**
    * The sub-elements of this element.
    */
   private final List _children;

   /**
    * The attributes of this elements.
    */
   private final BasicPropertyReader _attributes;

   /**
    * The content of this element.
    */
   private String _pcdata;

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Gets the name of the element.
    *
    * @return element
    *    the name of this element, cannot be <code>null</code>.
    */
   public String getName() {
      return _name;
   }

   /**
    * Adds a new child to this element.
    *
    * @param element
    *    the new child to add to this element, cannot be <code>null</code>.
    */
   void addChild(DataElement element) {
      _children.add(element);
   }

   /**
    * Adds an attribute to this element. If the key already exists, the previous
    * value for this key is replaced by the new one.
    *
    * @param key
    *    the name of the attribute, cannot be <code>null</code>.
    *
    * @param value
    *    the value of the attribute, can be <code>null</code>.
    */
   void addAttribute(String key, String value) {
      _attributes.set(key, value);
   }

   /**
    * Sets the PCDATA content of this element.
    *
    * @param pcdata
    *    the PCDATA content for this element, can be <code>null</code>.
    */
   void setText(String pcdata) {
      _pcdata = pcdata;
   }

   /**
    * Gets the list of the attributes.
    *
    * @return
    *    an {@link Iterator} returning each attribute name as a
    *    {@link String}; <code>null</code> indicates there are no attributes.
    */
   public Iterator getAttributes() {
      return _attributes.getNames();
   }

   /**
    * Gets the value of an attribute.
    *
    * @param name
    *    the name of the attribute, cannot be <code>null</code>.
    *
    * @return
    *    the value of the attribute, or <code>null</code> if the attribute is
    *    either not set or set to <code>null</code>.
    */
   public String get(String name) {
      return _attributes.get(name);
   }

   /**
    * Gets the children of this element.
    *
    * @return
    *    an {@link Iterator} that returns each child of this element as
    *    another <code>DataElement</code> instance; <code>null</code>
    *    indicates there are no child elements.
    */
   public Iterator getChildren() {
      return _children.iterator();
   }

   /**
    * Gets the text of this element.
    *
    * @return
    *    the text of this element or <code>null</code> if no text has been specified
    *    for this element.
    */
   public String getText() {
      return _pcdata;
   }
    
   public Object clone() {
      DataElement clone = new DataElement(getName());
      Iterator itChildren = getChildren();
      while (itChildren.hasNext()) {
         clone.addChild((DataElement) ((DataElement)itChildren.next()).clone());
      }
      Iterator itAttributes = getAttributes();
      while (itAttributes.hasNext()) {
         String nextKey = (String) itAttributes.next();
         clone.addAttribute(nextKey, get(nextKey));
      }
      clone.setText(getText());
      return clone;
   }
}
