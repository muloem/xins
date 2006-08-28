/*
 * $Id$
 *
 * Copyright 2003-2006 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.collections;

import java.util.Iterator;
import java.util.Map;

import org.xins.common.MandatoryArgumentChecker;

/**
 * Base for <code>PropertyReader</code> implementations that use an underlying
 * <code>Map</code> instance.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst.dehaan@orange-ft.com">Ernst de Haan</a>
 * @author <a href="mailto:anthony.goubard@orange-ft.com">Anthony Goubard</a>
 *
 * @since XINS 1.0.0
 */
public abstract class AbstractPropertyReader
implements PropertyReader {

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>AbstractPropertyReader</code>.
    *
    * @param map
    *    the map containing the data of this <code>PropertyReader</code>,
    *    cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>map == null</code>.
    *
    * @since XINS 1.4.0
    */
   protected AbstractPropertyReader(Map map)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("map", map);

      // Initialize fields
      _properties = map;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The mappings from property keys to values. Never <code>null</code>.
    */
   private final Map _properties;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Gets the value of the property with the specified name.
    *
    * @param name
    *    the name of the property, cannot be <code>null</code>.
    *
    * @return
    *    the value of the property, or <code>null</code> if it is not set.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null</code>.
    */
   public String get(String name) throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("name", name);

      // Retrieve the value
      Object value = _properties.get(name);
      return (String) value;
   }

   /**
    * Gets an iterator that iterates over all the property names. The
    * {@link Iterator} will return only {@link String} instances.
    *
    * @return
    *    the {@link Iterator} that will iterate over all the names, never
    *    <code>null</code>.
    */
   public Iterator getNames() {
      return _properties.keySet().iterator();
   }

   /**
    * Returns the number of entries.
    *
    * @return
    *    the size, always &gt;= 0.
    */
   public int size() {
      return _properties.size();
   }

   /**
    * Returns the <code>Map</code> that contains the properties.
    *
    * @return
    *    the {@link Map} used to store the properties in, cannot be
    *    <code>null</code>.
    *
    * @since XINS 1.4.0
    */
   protected Map getPropertiesMap() {
      return _properties;
   }
}
