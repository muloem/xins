/*
 * $Id$
 */
package org.xins.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.collections.PropertyReader;
import org.xins.common.collections.ProtectedPropertyReader;

/**
 * Simple representation of an XML element.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 0.119
 */
public final class Element implements Cloneable {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Secret key used when accessing <code>ProtectedPropertyReader</code>
    * objects.
    */
   private static final Object SECRET_KEY = new Object();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>Element</code> of the specified type.
    *
    * @param type
    *    the type of the XML element, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>type == null</code>.
    */
   public Element(String type) throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("type", type);

      _type       = type;
      _attributes = new ProtectedPropertyReader(SECRET_KEY);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The parent of this element. Can be <code>null</code>.
    */
   private Element _parent;

   /**
    * The type of this element. Cannot be <code>null</code>.
    */
   private String _type;

   /**
    * The set of attributes. Cannot be <code>null</code>.
    */
   private ProtectedPropertyReader _attributes;

   /**
    * Sub elements for this element. This list is a list of {@link Element} 
    * objects.
    *
    * <p>This field is lazily initialized, so it is initially
    * <code>null</code>.
    */
   private List _children;

   /**
    * PCDATA text for this element. If no text is specified, the value is
    * <code>null</code>.
    */
   private String _pcdata;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the parent element.
    *
    * @return
    *    the parent element, or <code>null</code> if this element currently
    *    has no parent.
    */
   public Element getParent() {
      return _parent;
   }

   /**
    * Returns the type of this element.
    *
    * @return
    *    the type of this element, cannot be <code>null</code>.
    */
   public String getType() {
      return _type;
   }

   /**
    * Adds an attribute.
    * If the attribute was already set the previous value is replaced
    * by the new one.
    *
    * @param name
    *    the name of the attribute, cannot be <code>null</code>.
    *
    * @param value
    *    the value of the attribute, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null || value == null</code>.
    */
   public void addAttribute(String name, String value)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("name", name, "value", value);

      _attributes.set(SECRET_KEY, name, value);
   }

   /**
    * Gets the set of attributes.
    *
    * @return
    *    the set of attributes, never <code>null</code>.
    */
   public PropertyReader getAttributes() {
      return _attributes;
   }

   /**
    * Sets the text content for this element. Any previous value set will be 
    * erased.
    *
    * @param pcdata
    *    the text to add, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>pcdata == null</code>.
    */
   public void setText(String pcdata) throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("pcdata", pcdata);

      // Set the PCDATA
      _pcdata = pcdata;
   }

   /**
    * Adds the specified element as content for this element.
    *
    * @param child
    *    the element to add, cannot be <code>null</code>, and
    *    <code>child.</code>{@link #getParent()} must be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>child == null || child.</code>{@link #getParent()}<code> != null</code>.
    */
   public void add(Element child) throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("child", child);
      if (child._parent != null) {
         throw new IllegalArgumentException("child.getParent() != null");
      }

      // Initialize the content list, if necessary
      if (_children == null) {
         _children = new ArrayList(7);
      }

      // Add the child element
      _children.add(child);

      // Set the parent of the child
      child._parent = this;
   }

   /**
    * Returns the children of this element. This returns a list of 
    * other elements (as {@link Element} objects).
    *
    * <p>Since the content is lazily initialized, <code>null</code> is
    * returned if there is no content.
    *
    * @return
    *    the {@link List} of content items, or <code>null</code>.
    */
   public List getChildren() {
      return _children;
   }
   
   /**
    * Gets the value of the PCDATA element for this element.
    *
    * @return
    *    The text content for this element, or <code>null</code> if no text
    *    content has been specified.
    */
   public String getText() {
      return _pcdata;
   }
   
   /**
    * Clones this object. A new <code>Element</code> instance will be returned
    * with the same type, equivalent child elements, equivalent attributes and
    * the same PCDATA content.
    *
    * @return
    *    a new clone of this object, never <code>null</code>.
    */
   public Object clone() {

      // Construct a new Element with the same type
      Element clone = new Element(getType());

      // Copy all the children
      if (getChildren() != null) {
         Iterator itChildren = getChildren().iterator();
         while (itChildren.hasNext()) {
            clone.add((Element) ((Element)itChildren.next()).clone());
         }
      }

      // Copy all the attributes
      if (getAttributes() != null) {
         Iterator itAttributes = getAttributes().getNames();
         while (itAttributes.hasNext()) {
            String nextKey = (String) itAttributes.next();
            clone.addAttribute(nextKey, getAttributes().get(nextKey));
         }
      }

      // Copy the PCDATA content
      if (getText() != null) {
         clone.setText(getText());
      }

      return clone;
   }
}
