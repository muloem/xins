/*
 * $Id$
 */
package org.xins.specs;

import org.xins.util.MandatoryArgumentChecker;

/**
 * Specification for a component of a XINS API specification. Each of these
 * components have at least a type and a name.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public abstract class Spec
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
    * Constructs a new <code>Spec</code> object with the specified type and
    * name. This constructor can only be called by subclasses in the
    * same package.
    *
    * @param type
    *    the type of the component, not <code>null</code>.
    *
    * @param name
    *    the name for the component, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>type == null || name == null</code>.
    */
   Spec(SpecType type, String name)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("type", type, "name", name);

      _type    = type;
      _name    = name;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The type of the component. Cannot be <code>null</code>.
    */
   private final SpecType _type;

   /**
    * The name of the component. Cannot be <code>null</code>.
    */
   private final String _name;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the type of the component.
    *
    * @return
    *    the type, cannot be <code>null</code>.
    */
   public final SpecType getType() {
      return _type;
   }

   /**
    * Returns the name of the component.
    *
    * @return
    *    the name, cannot be <code>null</code>.
    */
   public final String getName() {
      return _name;
   }
}
