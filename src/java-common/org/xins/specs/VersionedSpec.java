/*
 * $Id$
 */
package org.xins.specs;

import org.xins.util.MandatoryArgumentChecker;

/**
 * Specification for a versioned component of a XINS API specification. Each
 * of these components have at least a type, a name, and a version.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public abstract class VersionedSpec
extends Spec {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Checks the arguments to the constructor and returns the first one.
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
    * @return
    *    the first argument, the type.
    *
    * @throws IllegalArgumentException
    *    if <code>type == null || name == null || version == null</code>.
    *
    * @throws InvalidVersionException
    *    if <code>version</code> is not a well-formed version number string.
    */
   private static final SpecType checkArguments(SpecType type, String name, String version)
   throws IllegalArgumentException, InvalidVersionException {

      // Check conditions
      MandatoryArgumentChecker.check("type", type, "name", name, "version", version);

      // Return type
      return type;
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>VersionedSpec</code> object with the specified
    * type, name and version. This constructor can only be called by
    * subclasses in the same package.
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
    *
    * @throws InvalidNameException
    *    if <code>type.</code>{@link SpecType#isValidName(String) isValidName}<code>(name) == false</code>.
    *
    * @throws InvalidVersionException
    *    if <code>version</code> is not a well-formed version number string.
    */
   VersionedSpec(SpecType type, String name, String version)
   throws IllegalArgumentException,
          InvalidNameException,
          InvalidVersionException {

      // Check preconditions and call superconstructor
      super(checkArguments(type, name, version), name);

      _version = version;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The version of the component. Cannot be <code>null</code>.
    */
   private final String _version;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

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
