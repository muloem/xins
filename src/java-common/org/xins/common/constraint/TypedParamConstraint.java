/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.constraint;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.MandatoryArgumentChecker;

import org.xins.common.types.Type;
import org.xins.common.types.TypeValueException;

/**
 * Constraint that mandates that a parameter value matches a specified type.
 *
 * <p><em>This class should not be used directly. It may be moved or removed
 * in an upcoming minor XINS release.</em>
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.2.0
 */
public final class TypedParamConstraint
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
    * @param parameterName
    *    the parameter name, cannot be <code>null</code> and cannot be an
    *    empty string.
    *
    * @param type
    *    the parameter type, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>parameterName == null
    *          || parameterName.length() &lt; 1
    *          || type == null</code>.
    */
   public TypedParamConstraint(String parameterName, Type type)
   throws IllegalArgumentException {
      super(parameterName);

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
    * Retrieves the type for the parameter value.
    *
    * @return
    *    the associated type, never <code>null</code>.
    */
   public Type getType() {
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
   boolean checkParameterValue(Object value) {
      if (value == null) {
         return true;
      }
      
      try {
         String string = _type.toString(value);
         return _type.isValidValue(string);
      } catch (ClassCastException exception) {
         return false;
      } catch (TypeValueException exception) {
         return false;
      }
   }

   /**
    * Describes a violation of this constraint.
    *
    * @return
    *    a description of a violation of this constraint, never
    *    <code>null</code> and never an empty string.
    */
   public String describeViolation() {
      return "Value for parameter \""
           + getParameterName()
           + "\" does not match type "
           + _type.getName()
           + '.';
   }
}
