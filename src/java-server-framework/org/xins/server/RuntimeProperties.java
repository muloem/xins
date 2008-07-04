/*
 * $Id$
 *
 * Copyright 2003-2008 Online Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.util.ArrayList;
import java.util.List;

import org.xins.common.collections.InvalidPropertyValueException;
import org.xins.common.collections.MissingRequiredPropertyException;
import org.xins.common.collections.PropertyReader;

/**
 * Base class to get the runtime properties.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 * @author <a href="mailto:tauseef.rehman@orange-ftgroup.com">Tauseef Rehman</a>
 *
 * @since XINS 1.2.0
 */
public class RuntimeProperties {

   private PropertyReader _runtimeSettings;

   /**
    * Initialize the runtime properties. This method should be overwritten
    * by a generated class if any runtime properties is declared in the
    * impl.xml file.
    *
    * @param runtimeSettings
    *    the runtime properties, not <code>null</code>.
    *
    * @throws MissingRequiredPropertyException
    *    if a required property is not given.
    *
    * @throws InvalidPropertyValueException
    *    if the value of a certain property is invalid.
    */
   protected void init(PropertyReader runtimeSettings)
   throws MissingRequiredPropertyException, InvalidPropertyValueException {
      _runtimeSettings = runtimeSettings;
   }

   /**
    * Gets the descriptor list. The list is created by getting all the
    * properties which are marked as <i>_descriptor</i> in the run time
    * properties file.
    *
    * @return
    *    the list of all descriptors, may not be <code>null</code>.
    *
    * @since XINS 1.3.0
    */
   protected List descriptors() {
      return new ArrayList();
   }

   /**
    * Gets all the runtime properties.
    *
    * @return
    *    the runtime properties, can be <code>null</code> before the initialization.
    *
    * @since XINS 2.1
    */
   public PropertyReader properties() {
      return _runtimeSettings;
   }
}
