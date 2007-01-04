/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.spec;

import java.util.Map;

/**
 * Specification of a param combo.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:anthony.goubard@orange-ftgroup.com">Anthony Goubard</a>
 *
 * @since XINS 1.3.0
 */
public final class ParamComboSpec extends ComboSpec {

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
      super(type, parameters);
   }


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Gets the parameters defined in the param combo.
    * The key is the name of the parameter, the value is the {@link ParameterSpec} object.
    *
    * @return
    *    The specification of the parameters defined in the param combo, never <code>null</code>.
    */
   public Map getParameters() {

      return getReferences();
   }
}
