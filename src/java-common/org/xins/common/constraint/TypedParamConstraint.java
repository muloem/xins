/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.constraint;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.Utils;

import org.xins.common.text.TextUtils;

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
    *    if this constraint was violated, then a description of the violation
    *    (can be an empty string), otherwise (if this constraint was not
    *    violated) <code>null</code>.
    */
   String checkParameterValue(Object value) {

      // Null is always allowed
      if (value == null) {
         return null;
      }
      
      // Convert to a string and then check it
      String string;
      try {
         string = _type.toString(value);

      // Class mismatch
      } catch (ClassCastException exception) {
         return "Class of value for parameter \""
              + getParameterName()
              + "\" ("
              + value.getClass().getName()
              + " does not match value class for type "
              + _type.getName()
              + " ("
              + _type.getValueClass()
              + ").";
      } catch (TypeValueException exception) {
         String detail = exception.getDetail();

         // Invalid value, detailed description not available
         if (detail == null || detail.length() < 1) {
            return "Value of class \""
                 + Utils.getClassName(value)
                 + "\" for parameter \""
                 + getParameterName()
                 + "\" does not match type \""
                 + _type.getName()
                 + "\".";

         // Invalid value, detailed description indeed available
         } else {
            return "Value of class \""
                 + Utils.getClassName(value)
                 + "\" for parameter \""
                 + getParameterName()
                 + "\" does not match type \""
                 + _type.getName()
                 + "\" (detail: \""
                 + detail
                 + "\").";
         }
      }

      try {
         _type.checkValue(string);
         return null;
      } catch (TypeValueException exception) {
         String detail = exception.getDetail();

         // Invalid value, detailed description not available
         if (detail == null || detail.length() < 1) {
            return "Value of class \""
                 + Utils.getClassName(value)
                 + "\" for parameter \""
                 + getParameterName()
                 + "\" does not match type \""
                 + _type.getName()
                 + "\" when converted to a character string ("
                 + TextUtils.quote(string)
                 + ").";

         // Invalid value, detailed description indeed available
         } else {
            return "Value of class \""
                 + Utils.getClassName(value)
                 + "\" for parameter \""
                 + getParameterName()
                 + "\" does not match type \""
                 + _type.getName()
                 + "\" (detail: \""
                 + detail
                 + "\" when converted to a character string ("
                 + TextUtils.quote(string)
                 + ").";
         }
      }
   }
}
