/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.constraint;

import org.xins.common.MandatoryArgumentChecker;

/**
 * Constraint on a single parameter.
 *
 * <p><em>This class should not be used directly. It may be moved or removed
 * in an upcoming minor XINS release.</em>
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.2.0
 */
public abstract class ParamConstraint
extends Constraint {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Fully-qualified name of this class.
    */
   private static final String CLASSNAME = ParamConstraint.class.getName();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Creates a new <code>ParamConstraint</code> for a parameter with the
    * specified name.
    *
    * @param parameterName
    *    the parameter name, cannot be <code>null</code> and cannot be an
    *    empty string.
    *
    * @throws IllegalArgumentException
    *    if <code>parameterName == null
    *          || parameterName.length() &lt; 1</code>.
    */
   ParamConstraint(String parameterName)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("parameterName", parameterName);
      if (parameterName.length() < 1) {
         throw new IllegalArgumentException("parameterName.length() == 0");
      }

      // Store information
      _parameterName = parameterName;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The name of the parameter. This value of this field can neither be
    * <code>null</code> nor an empty string.
    */
   private final String _parameterName;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the parameter name.
    *
    * @return
    *    the name of the parameter, never <code>null</code>.
    */
   public final String getParameterName() {
      return _parameterName;
   }

   /**
    * Validates this constraint in the specified context (implementation
    * method). If this constraint is violated, then <code>false</code> is
    * returned, otherwise <code>true</code> is returned.
    *
    * <p>This method should only ever be called from
    * {@link #check(ConstraintContext)}.
    *
    * @param context
    *    the context for the validation, guaranteed not to be
    *    <code>null</code>.
    *
    * @return
    *    if this constraint was violated, then a description of the violation
    *    (can be an empty string), otherwise (if this constraint was not
    *    violated) <code>null</code>.
    */
   final String checkImpl(ConstraintContext context) {
      return checkParameterValue(context.getParameter(_parameterName));
   }

   /**
    * Checks if the specified value is allowable. If this constraint is
    * violated, then <code>false</code> is returned, otherwise
    * <code>true</code> is returned.
    *
    * <p>This method should only ever be called from
    * {@link #checkImpl(ConstraintContext)}.
    *
    * @param value
    *    the value for the parameter, possibly <code>null</code>.
    *
    * @return
    *    if this constraint was violated, then a description of the violation
    *    (can be an empty string), otherwise (if this constraint was not
    *    violated) <code>null</code>.
    */
   abstract String checkParameterValue(Object value);
}
