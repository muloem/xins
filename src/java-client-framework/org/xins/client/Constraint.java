/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client;

import org.xins.common.MandatoryArgumentChecker;

/**
 * Constraint.
 *
 * <p><em>This class should not be used directly. It may be moved or removed
 * in an upcoming minor XINS release.</em>
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.2.0
 */
public abstract class Constraint
extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Fully-qualified name of this class.
    */
   private static final String CLASSNAME = Constraint.class.getName();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Creates a new <code>Constraint</code> object.
    */
   Constraint() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Validates this constraint in the specified context (wrapper method).
    * If this constraint is violated, then <code>false</code> is returned,
    * otherwise <code>true</code> is returned.
    *
    * <p>If <code>context == null</code> then an exception is thrown,
    * otherwise the result of {@link #checkImpl(ConstraintContext)} is
    * returned.
    *
    * @param context
    *    the context for the validation, cannot be <code>null</code>.
    *
    * @return
    *    flag that indicates if this constraint was violated,
    *    <code>true</code> if it was not, and <code>false</code> if it was.
    *
    * @throws IllegalArgumentException
    *    if <code>context == null</code>.
    */
   final boolean check(ConstraintContext context)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("context", context);

      return checkImpl(context);
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
    *    flag that indicates if this constraint was violated,
    *    <code>true</code> if it was not, and <code>false</code> if it was.
    */
   abstract boolean checkImpl(ConstraintContext context);

   /**
    * Describes a violation of this constraint.
    *
    * @return
    *    a description of a violation of this constraint, never
    *    <code>null</code> and never an empty string.
    */
   abstract String describeViolation();
}
