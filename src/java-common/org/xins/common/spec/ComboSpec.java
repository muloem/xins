/*
 * $Id$
 *
 * Copyright 2003-2006 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.spec;

import java.util.Map;
import org.xins.common.MandatoryArgumentChecker;

/**
 * Specification of a combo.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 *
 * @since XINS 1.4.0
 */
class ComboSpec {

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Creates a new <code>ComboSpec</code>.
    *
    * @param type
    *    the type of the combo, cannot be <code>null</code>.
    *
    * @param parameters
    *    the parameters this param-combo or attribute-combo refers to, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>type == null || parameters == null</code>.
    */
   ComboSpec(String type, Map parameters) throws IllegalArgumentException {
      MandatoryArgumentChecker.check("type", type, "parameters", parameters);
      _type = type;
      _parameters = parameters;
   }

   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The type of the combo, never <code>null</code>.
    */
   private final String _type;

   /**
    * The parameters of this combo, never <code>null</code>.
    */
   private final Map _parameters;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns whether the combo is a all-or-none type.
    *
    * @return
    *    <code>true</code> if the type is <i>all-or-none</i>, <code>false</code> otherwise.
    */
   public boolean isAllOrNone() {

      return _type.equals("all-or-none");
   }

   /**
    * Returns whether the combo is a not-all type.
    *
    * @return
    *    <code>true</code> if the type is <i>not-all</i>, <code>false</code> otherwise.
    */
   public boolean isNotAll() {

      return _type.equals("not-all");
   }

   /**
    * Returns whether the combo is a exclusive-or type.
    *
    * @return
    *    <code>true</code> if the type is <i>exclusive-or</i>, <code>false</code> otherwise.
    */
   public boolean isExclusiveOr() {

      return _type.equals("exclusive-or");
   }

   /**
    * Returns whether the combo is a inclusive-or type.
    *
    * @return
    *    <code>true</code> if the type is <i>inclusive-or</i>, <code>false</code> otherwise.
    */
   public boolean isInclusiveOr() {

      return _type.equals("inclusive-or");
   }

   /**
    * Gets the parameters or attributes defined in the combo.
    * The key is the name of the parameter or of the attributes,
    * the value is the {@link ParameterSpec} object.
    *
    * @return
    *    the specification of the parameters defined in the combo, never <code>null</code>.
    */
   protected Map getReferences() {

      return _parameters;
   }
}
