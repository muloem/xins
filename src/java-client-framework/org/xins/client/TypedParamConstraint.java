/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client;

import org.xins.common.MandatoryArgumentChecker;

import org.xins.common.types.Type;

/**
 * Constraint that mandates that a parameter value matches a specified type.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
abstract class TypedParamConstraint
extends ParamConstraint {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Fully-qualified name of this class.
    */
   private static final String CLASSNAME = TypedParamConstraint.class.getName();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Creates a new <code>TypedParamConstraint</code> for a parameter with the
    * specified name and type.
    *
    * <p>If the value of the parameter is found to be not match the type, then
    * that violates this constraint.
    *
    * @param name
    *    the parameter name, cannot be <code>null</code> and cannot be an
    *    empty string.
    *
    * @param type
    *    the parameter type, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null
    *          || name.length() &lt; 1
    *          || type == null</code>.
    */
   TypedParamConstraint(String name, Type type)
   throws IllegalArgumentException {
      super(name);

      // Check additional preconditions
      MandatoryArgumentChecker.check("type", type);

      // Store information
      _type = type;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The type of the parameter. This value of this field can never be
    * <code>null</code>.
    */
   private final Type _type;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Retrieves the type.
    *
    * @return
    *    the associated type, never <code>null</code>.
    */
   Type getType() {
      return _type;
   }

   /**
    * Checks if the specified value is allowable. If this constraint is
    * violated, then <code>false</code> is returned, otherwise
    * <code>true</code> is returned.
    *
    * <p>This method should only ever be called from
    * {@link #checkImpl(ConstraintContext)}.
    *
    * <p>The implementation of this method returns <code>true</code> if and
    * only if
    * <code>{@link #getType()}.{@link Type#isValidValue(String) isValidValue}(value)</code>
    * returns <code>true</code>.
    *
    * @param value
    *    the value for the parameter, possibly <code>null</code>.
    *
    * @return
    *    flag that indicates if this constraint was violated,
    *    <code>true</code> if it was not, and <code>false</code> if it was.
    */
   boolean checkParameterValue(String value) {
      return _type.isValidValue(value);
   }
}
