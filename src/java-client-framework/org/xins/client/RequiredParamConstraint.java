/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client;

import org.xins.common.MandatoryArgumentChecker;

/**
 * Constraint that mandates that a parameter value is not <em>null</em>.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
final class RequiredParamConstraint
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
    * @param name
    *    the parameter name, cannot be <code>null</code> and cannot be an
    *    empty string.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null || name.length() &lt; 1</code>.
    */
   RequiredParamConstraint(String name)
   throws IllegalArgumentException {
      super(name);
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
    *    flag that indicates if this constraint was violated,
    *    <code>true</code> if it was not, and <code>false</code> if it was.
    */
   boolean checkParameterValue(String value) {
      return (value != null);
   }
}
