/*
 * $Id$
 */
package org.xins.specs;

import org.xins.util.MandatoryArgumentChecker;

/**
 * Specification for a component of a XINS API specification. Each of these
 * components have at least a type, a name, and a version.
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
    * Constructs a new <code>Spec</code> object with the specified type, name
    * and version. This constructor can only be called by subclasses in the
    * same package.
    *
    * @param type
    *    the type of the component, not <code>null</code>.
    *
    * @param name
    *    the name for the component, not <code>null</code>.
    *
    * @param version
    *    the version for the component, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>type == null || name == null || version == null</code>.
    */
   Spec(SpecType type, String name, String version)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("type", type, "name", name, "version", version);

      _type    = type;
      _name    = name;
      _version = version;
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

   /**
    * The version of the component. Cannot be <code>null</code>.
    */
   private final String _version;


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

   /**
    * Returns the version of the component.
    *
    * @return
    *    the version, cannot be <code>null</code>.
    */
   public final String getVersion() {
      return _version;
   }
}
