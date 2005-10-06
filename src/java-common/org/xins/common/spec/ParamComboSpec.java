/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.spec;

import java.util.Map;
import org.xins.common.MandatoryArgumentChecker;

/**
 * Specification of a param combo.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 *
 * @since XINS 1.3.0
 */
public final class ParamComboSpec {

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Creates a new <code>ParamComboSpec</code>.
    *
    * @param type
    *    the type of the param-combo, cannot be <code>null</code>.
    *
    * @param parameters
    *    the parameters this param-combo refers to, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>type == null || parameters == null</code>.
    */
   ParamComboSpec(String type, Map parameters) throws IllegalArgumentException {
      MandatoryArgumentChecker.check("type", type, "parameters", parameters);
      _type = type;
      _parameters = parameters;
   }

   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The type of the param-combo, never <code>null</code>.
    */
   private final String _type;

   /**
    * The parameters of this param-combo, never <code>null</code>.
    */
   private final Map _parameters;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns whether the param combo is a all-or-none type.
    *
    * @return
    *    <code>true</code> if the type is <i>all-or-none</i>, <code>false</code> otherwise.
    */
   public boolean isAllOrNone() {

      return _type.equals("all-or-none");
   }

   /**
    * Returns whether the param combo is a not-all type.
    *
    * @return
    *    <code>true</code> if the type is <i>not-all</i>, <code>false</code> otherwise.
    */
   public boolean isNotAll() {

      return _type.equals("not-all");
   }

   /**
    * Returns whether the param combo is a exclusive-or type.
    *
    * @return
    *    <code>true</code> if the type is <i>exclusive-or</i>, <code>false</code> otherwise.
    */
   public boolean isExclusiveOr() {

      return _type.equals("exclusive-or");
   }

   /**
    * Returns whether the param combo is a inclusive-or type.
    *
    * @return
    *    <code>true</code> if the type is <i>inclusive-or</i>, <code>false</code> otherwise.
    */
   public boolean isInclusiveOr() {

      return _type.equals("inclusive-or");
   }

   /**
    * Gets the parameters defined in the param combo.
    * The key is the name of the parameter, the value is the {@link ParameterSpec} object.
    *
    * @return
    *    The specification of the parameters defined in the param combo, never <code>null</code>.
    */
   public Map getParameters() {

      return _parameters;
   }
}
