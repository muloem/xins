/*
 * $Id$
 */
package org.xins.common.specs;

import org.xins.common.MandatoryArgumentChecker;

/**
 * Specification for an API.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.2.0
 */
public final class APISpec
extends VersionedSpec {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The type for API components.
    */
   public static final SpecType TYPE = new Type();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>APISpec</code> for an API with the specified
    * name and version.
    *
    * @param name
    *    the name for the component, not <code>null</code>.
    *
    * @param version
    *    the version for the component, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null || version == null</code>.
    *
    * @throws InvalidNameException
    *    if {@link #TYPE}<code>.</code>{@link SpecType#isValidName(String) isValidName}<code>(name) == false</code>.
    *
    * @throws InvalidVersionException
    *    if <code>version</code> is not a well-formed version number string.
    */
   public APISpec(String name, String version)
   throws IllegalArgumentException,
          InvalidNameException,
          InvalidVersionException {
      super(TYPE, null, name, version);
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
    * The type for an API component.
    *
    * @version $Revision$ $Date$
    * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
    */
   private static class Type extends SpecType {

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      /**
       * Constructs a new <code>Type</code> object.
       */
      private Type() {
         super(null, "api", "^[a-z][a-z0-9]*(-[a-z][a-z0-9]*)*$");
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------
   }
}