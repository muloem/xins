/*
 * $Id$
 */
package org.xins.specs;

import org.xins.util.MandatoryArgumentChecker;

/**
 * Specification for an input parameter for a function.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public final class InputParamSpec
extends ParamSpec {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The type for input parameter components.
    */
   public static final SpecType TYPE = new Type();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>InputParamSpec</code> for an input parameter with
    * the specified name.
    *
    * @param parent
    *    the function this input parameter is part of, not <code>null</code>.
    *
    * @param name
    *    the name for the component, not <code>null</code>.
    *
    * @param paramType
    *    the type for the parameter, not <code>null</code>.
    *
    * @param required
    *    flag that indicates if this parameter is required or not.
    *
    * @throws IllegalArgumentException
    *    if <code>parent == null || name == null || paramType == null</code>.
    *
    * @throws InvalidNameException
    *    if {@link #TYPE}<code>.</code>{@link SpecType#isValidName(String) isValidName}<code>(name) == false</code>.
    */
   public InputParamSpec(FunctionSpec parent,
                         String       name,
                         TypeSpec     paramType,
                         boolean      required)
   throws IllegalArgumentException, InvalidNameException {
      super((Type) TYPE, parent, name, paramType, required);
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
    * The type for an input parameter component.
    *
    * @version $Revision$ $Date$
    * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
    */
   private static class Type extends ParamSpec.Type {

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      /**
       * Constructs a new <code>Type</code> object.
       */
      private Type() {
         super("function input parameter");
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------
   }
}
