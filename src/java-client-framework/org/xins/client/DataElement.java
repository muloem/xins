/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.Utils;

import org.xins.common.text.TextUtils;
import org.xins.common.xml.Element;
import org.xins.common.xml.ElementBuilder;

/**
 * Element in a XINS result data section.
 *
 * <p>Note that this class is not thread-safe. It should not be used from
 * different threads at the same time. This applies even to read operations.
 *
 * <p>Note that the namespace URIs and local names are not checked for
 * validity in this class.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:anthony.goubard@orange-ftgroup.com">Anthony Goubard</a>
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public class DataElement implements Cloneable {

   // NOTE: The behavior of this class has been slightly redefined in XINS
   //       1.1. In XINS 1.0, the name for a DataElement was a combination of
   //       the namespace prefix and the local name. In XINS 1.1, the name is
   //       just the local name. Since XINS 1.0 did not support XML Namespaces
   //       yet, this is not considered an incompatibility.

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Fully-qualified name of this class.
    */
   private static final String CLASSNAME = DataElement.class.getName();


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Creates a new <code>DataElement</code>.
    *
    * @param namespaceURI
    *    the namespace URI for the element, can be <code>null</code>; an empty
    *    string is equivalent to <code>null</code>.
    *
    * @param localName
    *    the local name of the element, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>localName == null</code>.
    */
   DataElement(String namespaceURI, String localName)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("localName", localName);

      // An empty namespace URI is equivalent to null
      if (namespaceURI != null && namespaceURI.length() < 1) {
         namespaceURI = null;
      }

      // Store namespace URI and local name
      _namespaceURI = namespaceURI;
      _localName    = localName;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The namespace URI. This field can be <code>null</code>, but it can never
    * be an empty string.
    */
   private final String _namespaceURI;

   /**
    * The local name. This field is never <code>null</code>.
    */
   private final String _localName;

   /**
    * The child elements. This field is lazily initialized is initially
    * <code>null</code>.
    */
   private ArrayList _children;

   /**
    * The attributes. This field is lazily initialized and is initially
    * <code>null</code>.
    */
   private HashMap _attributes;

   /**
    * The character content for this element. Can be <code>null</code>.
    */
   private String _text;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Gets the namespace URI.
    *
    * @return
    *    the namespace URI for this element, or <code>null</code> if there is
    *    none, but never an empty string.
    *
    * @since XINS 1.1.0
    */
   public String getNamespaceURI() {
      return _namespaceURI;
   }

   /**
    * Gets the local name.
    *
    * @return
    *    the local name of this element, cannot be <code>null</code>.
    *
    * @since XINS 1.1.0
    */
   public String getLocalName() {
      return _localName;
   }

   /**
    * Sets the specified attribute. If the value for the specified
    * attribute is already set, then the previous value is replaced.
    *
    * @param namespaceURI
    *    the namespace URI for the attribute, can be <code>null</code>; an
    *    empty string is equivalent to <code>null</code>.
    *
    * @param localName
    *    the local name for the attribute, cannot be <code>null</code>.
    *
    * @param value
    *    the value for the attribute, can be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>localName == null</code>.
    *
    * @since XINS 1.1.0
    */
   void setAttribute(String namespaceURI, String localName, String value)
   throws IllegalArgumentException {

      // Construct a QualifiedName object. This will check the preconditions.
      QualifiedName qn = new QualifiedName(namespaceURI, localName);

      if (_attributes == null) {
         if (value == null) {
            return;
         }

         // Lazily initialize
         _attributes = new HashMap();
      }

      // Set or reset the attribute
      _attributes.put(qn, value);
   }

   /**
    * Gets an unmodifiable view of all attributes.
    *
    * @return
    *    an unmodifiable {@link Map} (never <code>null</code>) which is a view
    *    on all the attributes; each key in the <code>Map</code> is a
    *    {@link QualifiedName} instance (not <code>null</code>) and each value
    *    in it is a <code>String</code> instance (not <code>null</code>).
    *
    * @since XINS 1.1.0
    */
   public Map getAttributeMap() {
      if (_attributes == null) {
         return Collections.EMPTY_MAP;
      } else {
         return Collections.unmodifiableMap(_attributes);
      }
   }

   /**
    * Gets the value of the attribute with the qualified name. If the
    * qualified name does not specify a namespace, then only an attribute that
    * does not have a namespace will match.
    *
    * @param qn
    *    a combination of an optional namespace and a mandatory local name, or
    *    <code>null</code>.
    *
    * @return
    *    the value of the attribute that matches the specified namespace and
    *    local name, or <code>null</code> if such an attribute is either not
    *    set or set to <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>qn == null</code>.
    *
    * @since XINS 1.1.0
    */
   public String getAttribute(QualifiedName qn)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("qn", qn);

      if (_attributes == null) {
         return null;
      } else {
         return (String) _attributes.get(qn);
      }
   }

   /**
    * Gets the value of the attribute with the specified namespace and local
    * name. The namespace is optional. If the namespace is not given, then only
    * an attribute that does not have a namespace will match.
    *
    * @param namespaceURI
    *    the namespace URI for the attribute, can be <code>null</code>; an
    *    empty string is equivalent to <code>null</code>; if specified this
    *    string must be a valid namespace URI.
    *
    * @param localName
    *    the local name of the attribute, cannot be <code>null</code>.
    *
    * @return
    *    the value of the attribute that matches the specified namespace and
    *    local name, or <code>null</code> if such an attribute is either not
    *    set or set to <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>localName == null</code>.
    *
    * @since XINS 1.1.0
    */
   public String getAttribute(String namespaceURI, String localName)
   throws IllegalArgumentException {
      QualifiedName qn = new QualifiedName(namespaceURI, localName);
      return getAttribute(qn);
   }

   /**
    * Gets the value of an attribute that has no namespace.
    *
    * @param localName
    *    the local name of the attribute, cannot be <code>null</code>.
    *
    * @return
    *    the value of the attribute that matches the specified local name and
    *    has no namespace defined, or <code>null</code> if the attribute is
    *    either not set or set to <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>localName == null</code>.
    *
    * @since XINS 1.1.0
    */
   public String getAttribute(String localName)
   throws IllegalArgumentException {
      return getAttribute(null, localName);
   }

   /**
    * Adds a new child element.
    *
    * @param child
    *    the new child to add to this element, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>child == null || child == <em>this</em></code>.
    */
   void addChild(DataElement child) throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("child", child);
      if (child == this) {
         throw new IllegalArgumentException("child == this");
      }

      // Lazily initialize
      if (_children == null) {
         _children = new ArrayList();
      }

      _children.add(child);
   }

   /**
    * Gets the list of all child elements.
    *
    * @return
    *    an unmodifiable {@link List} containing all child elements; each
    *    element in the list is another <code>DataElement</code> instance;
    *    never <code>null</code>.
    */
   public List getChildElements() {

      List children;

      // If there are no children, then return an immutable empty List
      if (_children == null || _children.size() == 0) {
         children = Collections.EMPTY_LIST;

      // Otherwise return an immutable view of the list of children
      } else {
         children = Collections.unmodifiableList(_children);
      }

      return children;
   }

   /**
    * Gets the list of child elements that match the specified name.
    *
    * @param name
    *    the name for the child elements to match, cannot be
    *    <code>null</code>.
    *
    * @return
    *    a {@link List} containing each child element that matches the
    *    specified name as another <code>DataElement</code> instance;
    *    never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null</code>.
    */
   public List getChildElements(String name)
   throws IllegalArgumentException {

      // TODO: Support namespaces

      // Check preconditions
      MandatoryArgumentChecker.check("name", name);

      List matches;

      // If there are no children, then return null
      if (_children.size() == 0) {
         matches = Collections.EMPTY_LIST;

      // There are children, find all matching ones
      } else {
         matches = new ArrayList();
         Iterator it = _children.iterator();
         while (it.hasNext()) {
            DataElement child = (DataElement) it.next();
            if (name.equals(child.getLocalName())) {
               matches.add(child);
            }
         }

         // If there are no matching children, then return null
         if (matches.size() == 0) {
            matches = Collections.EMPTY_LIST;

         // Otherwise return an immutable list with all matches
         } else {
            matches = Collections.unmodifiableList(matches);
         }
      }

      return matches;
   }

   /**
    * Sets the character content. The existing character content, if any, is
    * replaced
    *
    * @param text
    *    the character content for this element, or <code>null</code>.
    */
   void setText(String text) {
      _text = text;
   }

   /**
    * Gets the character content, if any.
    *
    * @return
    *    the character content of this element, or <code>null</code> if no
    *    text has been specified for this element.
    */
   public String getText() {
      return _text;
   }

   /**
    * Converts this DataElement to a {@link org.xins.common.xml.Element} object.
    *
    * @return
    *    the converted object, never <code>null</code>.
    *
    * @since XINS 1.3.0
    */
   public Element toXMLElement() {
      return toXMLElement(this);
   }

   /**
    * Converts the given DataElement to a
    * {@link org.xins.common.xml.Element} object.
    *
    * @param dataElement
    *    the input element to convert, cannot be <code>null</code>
    *
    * @return
    *    the converted object, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>dataElement == null</code>.
    */
   private Element toXMLElement(DataElement dataElement)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("dataElement", dataElement);

      String elementName = dataElement.getLocalName();
      String elementNameSpaceURI = dataElement.getNamespaceURI();
      Map elementAttributes = dataElement.getAttributeMap();
      String elementText = dataElement.getText();
      List elementChildren = dataElement.getChildElements();

      ElementBuilder builder = new ElementBuilder(elementNameSpaceURI, elementName);

      builder.setText(elementText);

      // Go through the attributes
      Iterator itAttributes = elementAttributes.entrySet().iterator();
      while (itAttributes.hasNext()) {
         Map.Entry                 entry          = (Map.Entry) itAttributes.next();
         DataElement.QualifiedName attributeName  = (DataElement.QualifiedName) entry.getKey();
         String                    attributeValue = (String) entry.getValue();
         builder.setAttribute(attributeName.getNamespaceURI(), attributeName.getLocalName(), attributeValue);
      }

      // Add the children of this element
      Iterator itChildren = elementChildren.iterator();
      while (itChildren.hasNext()) {
         DataElement nextChild = (DataElement) itChildren.next();
         Element transformedChild = toXMLElement(nextChild);
         builder.addChild(transformedChild);
      }

      return builder.createElement();
   }

   /**
    * Clones this object. The clone will have the same namespace URI and local
    * name and equivalent attributes, children and character content.
    *
    * @return
    *    a new clone of this object, never <code>null</code>.
    */
   public Object clone() {

      // Construct a new DataElement, copy all field values (shallow copy)
      DataElement clone;
      try {
         clone = (DataElement) super.clone();
      } catch (CloneNotSupportedException exception) {
         String detail = null;
         throw Utils.logProgrammingError(DataElement.class.getName(),
                                         "clone()",
                                         "Object",
                                         "clone()",
                                         detail,
                                         exception);
      }

      // Deep copy the children
      if (_children != null) {
         clone._children = (ArrayList) _children.clone();
      }

      // Deep copy the attributes
      if (_attributes != null) {
         clone._attributes = (HashMap) _attributes.clone();
      }

      return clone;
   }


   //-------------------------------------------------------------------------
   // Inner classes
   //-------------------------------------------------------------------------

   /**
    * Qualified name for an element or attribute. This is a combination of an
    * optional namespace URI and a mandatory local name.
    *
    * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
    *
    * @since XINS 1.1.0
    */
   public static final class QualifiedName extends Object {

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      /**
       * Constructs a new <code>QualifiedName</code> with the specified
       * namespace and local name.
       *
       * @param namespaceURI
       *    the namespace URI for the element, can be <code>null</code>; an
       *    empty string is equivalent to <code>null</code>.
       *
       * @param localName
       *    the local name of the element, cannot be <code>null</code>.
       *
       * @throws IllegalArgumentException
       *    if <code>localName == null</code>.
       */
      public QualifiedName(String namespaceURI, String localName)
      throws IllegalArgumentException {

         // Check preconditions
         MandatoryArgumentChecker.check("localName", localName);

         // An empty namespace URI is equivalent to null
         if (namespaceURI != null && namespaceURI.length() < 1) {
            namespaceURI = null;
         }

         // Initialize fields
         _hashCode     = localName.hashCode();
         _namespaceURI = namespaceURI;
         _localName    = localName;
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      /**
       * The hash code for this object.
       */
      private final int _hashCode;

      /**
       * The namespace URI. Can be <code>null</code>.
       */
      private final String _namespaceURI;

      /**
       * The local name. Cannot be <code>null</code>.
       */
      private final String _localName;


      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------

      /**
       * Returns the hash code value for this object.
       *
       * @return
       *    the hash code value.
       */
      public int hashCode() {
         return _hashCode;
      }

      /**
       * Compares this object with the specified object for equality.
       *
       * @param obj
       *    the object to compare with, or <code>null</code>.
       *
       * @return
       *    <code>true</code> if this object and the argument are considered
       *    equal, <code>false</code> otherwise.
       */
      public boolean equals(Object obj) {

         if (! (obj instanceof QualifiedName)) {
            return false;
         }

         QualifiedName qn = (QualifiedName) obj;
         return ((_namespaceURI == null && qn._namespaceURI == null) ||
               (_namespaceURI != null && _namespaceURI.equals(qn._namespaceURI))) &&
               _localName.equals(qn._localName);
      }

      /**
       * Gets the namespace URI.
       *
       * @return
       *    the namespace URI, can be <code>null</code>.
       */
      public String getNamespaceURI() {
         return _namespaceURI;
      }

      /**
       * Gets the local name.
       *
       * @return
       *    the local name, never <code>null</code>.
       */
      public String getLocalName() {
         return _localName;
      }
   }
}
