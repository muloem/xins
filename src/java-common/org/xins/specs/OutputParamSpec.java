/*
 * $Id$
 */
package org.xins.specs;

import org.xins.util.MandatoryArgumentChecker;

/**
 * Specification for an output parameter for a function.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public class OutputParamSpec
extends Spec {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The type for output parameter components.
    */
   public static final SpecType TYPE = new Type();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>OutputParamSpec</code> for an output parameter
    * with the specified name.
    *
    * @param parent
    *    the function this output parameter is part of, not <code>null</code>.
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
   public OutputParamSpec(FunctionSpec parent, String name)
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
    * The type for an output parameter component.
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
         super(FunctionSpec.TYPE, "function output parameter", "^[a-z]+$");
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------
   }
}
