/*
 * $Id$
 */
package org.xins.specs;

import org.xins.util.MandatoryArgumentChecker;

/**
 * Type of a component of a XINS API specification.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @see Spec
 */
public abstract class SpecType
extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>SpecType</code> with the specified name.
    *
    * @param name
    *    the name for the type, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null</code>.
    */
   SpecType(String name) throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("name", name);

      _typeName = name;
      _nameRE   = null; // TODO
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The name of the type. Cannot be <code>null</code>.
    */
   private final String _typeName;

   /**
    * The regular expression that names for components must match. Is
    * <code>null</code> if there are no restrictions on the name.
    */
   private final String _nameRE;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the name of the component type.
    *
    * @return
    *    the name, cannot be <code>null</code>.
    */
   public final String getTypeName() {
      return _typeName;
   }

   /**
    * Returns the regular expression that names for components of this type
    * must match.
    *
    * @return
    *    the regular expression that names for components must match, or
    *    <code>null</code> if there are no restrictions on the name.
    */
   public final String getNameRE() {
      return _nameRE;
   }
}
