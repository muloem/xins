/*
 * $Id$
 */
package org.xins.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.xins.util.MandatoryArgumentChecker;
import org.xins.util.collections.PropertyReader;
import org.xins.util.collections.ProtectedPropertyReader;

/**
 * Simple representation of an XML element.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.119
 */
final class Element extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The logging category used by this class. This class field is never
    * <code>null</code>.
    */
   private static final Logger LOG = Logger.getLogger(Element.class.getName());


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
      _attributes = new ProtectedPropertyReader(LOG);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The type of this element. Cannot be <code>null</code>.
    */
   private String _type;

   /**
    * The set of attributes. Cannot be <code>null</code>.
    */
   private ProtectedPropertyReader _attributes;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

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

      _attributes.set(LOG, name, value);
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
}
