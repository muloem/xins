/*
 * $Id$
 */
package org.xins.specs;

import org.xins.util.MandatoryArgumentChecker;

/**
 * Specification for an element attribute in a function data section.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public class ElementAttributeSpec
extends Spec {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The type for element attribute components.
    */
   public static final SpecType TYPE = new Type();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>ElementAttributeSpec</code> for an element
    * attribute with the specified name.
    *
    * @param parent
    *    the element this attribute is defined for, not <code>null</code>.
    *
    * @param name
    *    the name for the component, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>parent == null || name == null</code>.
    *
    * @throws InvalidNameException
    *    if {@link #TYPE}<code>.</code>{@link SpecType#isValidName(String) isValidName}<code>(name) == false</code>.
    */
   public ElementAttributeSpec(ElementSpec parent, String name)
   throws IllegalArgumentException, InvalidNameException {
      super(TYPE, parent, name);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Inner classes
   //-------------------------------------------------------------------------

   /**
    * The type for an element attribute component.
    *
    * @version $Revision$ $Date$
    * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
    */
   private static class Type extends SpecType {

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      /**
       * Constructs a new <code>Type</code> object.
       */
      private Type() {
         super(ElementSpec.TYPE, "element attribute", "^[a-zA-Z0-9_-]+$");
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------
   }
}
