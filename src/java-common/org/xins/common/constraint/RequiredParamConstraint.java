/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.constraint;

import org.xins.common.MandatoryArgumentChecker;

/**
 * Constraint that mandates that a parameter value is not <em>null</em>.
 *
 * <p><em>This class should not be used directly. It may be moved or removed
 * in an upcoming minor XINS release.</em>
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.2.0
 */
public final class RequiredParamConstraint
extends ParamConstraint {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Fully-qualified name of this class.
    */
   private static final String CLASSNAME = RequiredParamConstraint.class.getName();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Creates a new <code>RequiredParamConstraint</code> for a parameter with
    * the specified name.
    *
    * <p>If the value of the parameter is found to be <code>null</code> then
    * that violates this constraint.
    *
    * @param parameterName
    *    the parameter name, cannot be <code>null</code> and cannot be an
    *    empty string.
    *
    * @throws IllegalArgumentException
    *    if <code>parameterName == null
    *          || parameterName.length() &lt; 1</code>.
    */
   public RequiredParamConstraint(String parameterName)
   throws IllegalArgumentException {
      super(parameterName);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Checks if the specified value is allowable. If this constraint is
    * violated, then <code>false</code> is returned, otherwise
    * <code>true</code> is returned.
    *
    * <p>This method should only ever be called from
    * {@link #checkImpl(ConstraintContext)}.
    *
    * <p>The implementation of this method returns <code>true</code> if and
    * only if <code>value != null</code>.
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
      if (value == null) {
         return "Parameter \""
              + getParameterName()
              + "\" is required, but the value is null.";
      } else {
         return null;
      }
   }
}
