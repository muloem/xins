/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.constraint;

import org.xins.common.MandatoryArgumentChecker;

/**
 * A single constraint violation.
 *
 * <p><em>This class should not be used directly. It may be moved or removed
 * in an upcoming minor XINS release.</em>
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.2.0
 */
public final class ConstraintViolation
extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Fully-qualified name of this class.
    */
   private static final String CLASSNAME = ConstraintViolation.class.getName();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Creates a new <code>ConstraintViolation</code> object.
    *
    * @param constraint
    *    the violated constraint, cannot be <code>null</code>.
    *
    * @param description
    *     a description of the violation, never <code>null</code>.
    */
   ConstraintViolation(Constraint constraint, String description)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("constraint",  constraint,
                                     "description", description);

      // Store information
      _constraint  = constraint;
      _description = description;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The violated constraint. Never <code>null</code>.
    */
   private final Constraint _constraint;

   /**
    * Description of the violation. Can be <code>null</code>.
    */
   private final String _description;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the violated constraint.
    *
    * @return
    *    the constraint, never <code>null</code>.
    */
   public Constraint getConstraint() {
      return _constraint;
   }

   /**
    * Returns the description of the violation.
    *
    * @return
    *    the description, never <code>null</code>.
    */
   public String getDescription() {
      return _description;
   }
}
