/*
 * $Id$
 */
package org.xins.server;

import java.util.ArrayList;
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
final class Element extends Object {

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

      // TODO: Check that name is valid

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
    * Content of this element. May contain PCDATA (as {@link String} objects)
    * and other elements (as {@link Element} objects).
    *
    * <p>This field is lazily initialized, so it is initially
    * <code>null</code>.
    */
   private List _content;


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

      // TODO: Check attribute is not yet set

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
    * Adds the specified PCDATA as content for this element.
    *
    * @param pcdata
    *    the text to add, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>pcdata == null</code>.
    */
   public void add(String pcdata) throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("pcdata", pcdata);

      // Initialize the content list, if necessary
      if (_content == null) {
         _content = new ArrayList(7);
      }

      // Add the PCDATA
      _content.add(pcdata);
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
      if (_content == null) {
         _content = new ArrayList(7);
      }

      // Add the child element
      _content.add(child);

      // Set the parent of the child
      child._parent = this;
   }

   public List getContent() {
      return _content;
   }
}
