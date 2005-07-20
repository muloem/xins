/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import org.xins.common.collections.InvalidPropertyValueException;
import org.xins.common.collections.MissingRequiredPropertyException;

/**
 * Base class to get the runtime properties.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 * @author Tauseef Rehman (<a href="mailto:tauseef.rehman@nl.wanadoo.com">tauseef.rehman@nl.wanadoo.com</a>)
 *
 * @since XINS 1.2.0
 */
public class RuntimeProperties {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Initialize the runtime properties. This method should be overwritten
    * by a generated class if any runtime properties is declared in the
    * impl.xml file.
    *
    * @param runtimeSettings
    *    the initialization properties, not <code>null<code>.
    *
    * @throws MissingRequiredPropertyException
    *    if a required property is not given.
    *
    * @throws InvalidPropertyValueException
    *    if the value of a certain property is invalid.
    */
   protected void init(org.xins.common.collections.PropertyReader runtimeSettings)
   throws MissingRequiredPropertyException, InvalidPropertyValueException {
   }

   /**
    * Gets the descriptor list. The list is created by getting all the 
    * properties which are marked as <i>_descriptor</i> in the run time
    * properties file.
    *
    * @return
    *    the list of all descriptors, may not be <code>null</code>.
    */
   protected java.util.List descriptors() {
      return new java.util.ArrayList();
   }
}
