/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.constraint;

import java.util.List;

import org.xins.common.MandatoryArgumentChecker;

import org.xins.common.collections.CollectionUtils;

import org.xins.common.text.FastStringBuffer;

/**
 * Constraint on a combination of parameters that mandates that either all
 * these parameters have a value set or none of them.
 *
 * <p><em>This class should not be used directly. It may be moved or removed
 * in an upcoming minor XINS release.</em>
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.2.0
 */
public final class ExclusiveOrParamComboConstraint
extends ParamComboConstraint {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Fully-qualified name of this class.
    */
   private static final String CLASSNAME = ExclusiveOrParamComboConstraint.class.getName();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Creates a new <code>ExclusiveOrParamComboConstraint</code> for the
    * specified set of parameters.
    *
    * @param names
    *    the set of parameter names, cannot be <code>null</code> and must
    *    contain at least 2 elements; all elements must be neither
    *    <code>null</code> nor an empty string.
    *
    * @throws IllegalArgumentException
    *    if <code>names                     ==   null
    *          || names.length              &lt; 2
    *          || names[<em>i</em>          ==   null
    *          || names[<em>i</em>.length() &lt; 1</code>
    *    or if <code>names</code> constains any duplicates.
    */
   public ExclusiveOrParamComboConstraint(String[] names)
   throws IllegalArgumentException {
      super(names);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

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
   protected String checkImpl(ConstraintContext context) {

      // Get all parameter names, at least 2
      List names = getParameterNames();

      // Loop through all the parameters
      int nonNull = 0;
      for (int i = 0; i < names.size(); i++) {

         // Get the parameter value
         String name = (String) names.get(i);
         Object value = context.getParameter(name);

         // If the value is specified, then increase the counter
         if (value != null) {
            nonNull++;
         }
      }

      // There should be exactly 1 non-null value
      if (nonNull != 1) {
         FastStringBuffer buffer = new FastStringBuffer(171);
         buffer.append("Exactly one of the parameters ");

         buffer.append('"');
         buffer.append((String) names.get(0));
         buffer.append('"');

         for (int i = 1; i < names.size(); i++) {
            buffer.append(", \"");
            buffer.append((String) names.get(i));
            buffer.append('"');
         }
         buffer.append(" should have been set, but ");
         if (nonNull < 1) {
            buffer.append("none");
         } else {
            buffer.append(nonNull);
         }
         buffer.append(" were.");
         return buffer.toString();
         // TODO: Improve description
      }

      // If control comes here, then there is no issue
      return null;
   }
}
