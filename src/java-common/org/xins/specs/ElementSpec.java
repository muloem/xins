/*
 * $Id$
 */
package org.xins.specs;

import org.xins.util.MandatoryArgumentChecker;

/**
 * Specification for an element in a function data section.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public class ElementSpec
extends Spec {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The type for element components.
    */
   public static final SpecType TYPE = new Type();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>ElementSpec</code> for an element with the
    * specified name.
    *
    * @param parent
    *    the function this element is part of, not <code>null</code>.
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
   public ElementSpec(FunctionSpec parent, String name)
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
    * The type for an element component.
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
         super(FunctionSpec.TYPE, "element", "^[a-zA-Z0-9_-]+$");
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------
   }
}
