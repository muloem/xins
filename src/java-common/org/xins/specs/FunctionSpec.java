/*
 * $Id$
 */
package org.xins.specs;

import org.xins.util.MandatoryArgumentChecker;

/**
 * Specification for a function within an API.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public class FunctionSpec
extends VersionedSpec {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The type for function components.
    */
   public static final SpecType TYPE = new Type();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>FunctionSpec</code> for a function with the
    * specified name and version.
    *
    * @param parent
    *    the API the function is part of, not <code>null</code>.
    *
    * @param name
    *    the name for the function, not <code>null</code>.
    *
    * @param version
    *    the version for the function, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>parent == null || name == null || version == null</code>.
    *
    * @throws InvalidNameException
    *    if {@link #TYPE}<code>.</code>{@link SpecType#isValidName(String) isValidName}<code>(name) == false</code>.
    *
    * @throws InvalidVersionException
    *    if <code>version</code> is not a well-formed version number string.
    */
   public FunctionSpec(APISpec parent, String name, String version)
   throws IllegalArgumentException,
          InvalidNameException,
          InvalidVersionException {
      super(TYPE, name, version);
      // TODO: super(TYPE, parent, name, version);

      // TODO: Remove
      MandatoryArgumentChecker.check("parent", parent);
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
    * The type for a function component.
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
         super(APISpec.TYPE, "function", "^([A-Z][a-z0-9]+)+$");
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------
   }
}
