/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client;

import java.util.ArrayList;

import org.xins.common.collections.CollectionUtils;

import org.xins.common.MandatoryArgumentChecker;

/**
 * Constraint on a combination of parameters.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
abstract class ParamComboConstraint
extends Constraint {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Fully-qualified name of this class.
    */
   private static final String CLASSNAME = ParamComboConstraint.class.getName();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Creates a new <code>ParamComboConstraint</code> for a set of parameters
    * with the specified names.
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
   ParamComboConstraint(String[] names)
   throws IllegalArgumentException {

      _names = CollectionUtils.list("names", names, 2);

      // Make sure there are no empty strings in the list
      for (int i = 0; i < names.length; i++) {
         String name = names[i];
         if (name.length() < 1) {
            throw new IllegalArgumentException("names[" + i + "].length() == 0");
         }
      }
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The names of all parameters. This field is never <code>null</code>.
    */
   private final ArrayList _names;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
