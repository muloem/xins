/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.collections;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.xins.common.MandatoryArgumentChecker;

/**
 * Immutable property reader that remembers which properties have not been
 * accessed.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.3.0
 */
public final class StatsPropertyReader implements PropertyReader {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Confidential object used to protect <code>ProtectedPropertyReader</code>
    * instances from unauthorized changes. Not <code>null</code>.
    */
   private static final Object SECRET_KEY = new Object();


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>StatsPropertyReader</code> based on the specified
    * <code>PropertyReader</code>. The properties in <code>source</code> are
    * copied to an internal store.
    *
    * @param source
    *    the source property reader, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>source == null</code>.
    */
   public StatsPropertyReader(PropertyReader source)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("source", source);

      // Prepare collections for storing the name/value combinations
      _properties = new HashMap();
      _unused     = new ProtectedPropertyReader(SECRET_KEY);

      // Copy the property reader to an internal HashMap and to a set of
      // unused properties
      Iterator names = source.getNames();
      while (names.hasNext()) {
         String name = (String) names.next();

         if (name != null) {
            String value = source.get(name);
            if (value != null) {
               _properties.put(name, value);
               _unused.set(SECRET_KEY, name, value);
            }
         }
      }
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The set of properties to retrieve values from. Never <code>null</code>.
    */
   private final Map _properties;

   /**
    * The set of unused properties. Initially contains all properties. Becomes
    * <code>null</code> if there are no more unused properties.
    */
   private ProtectedPropertyReader _unused;


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
      String value = (String) _properties.get(name);

      // If the property is found, then mark it used
      if (value != null && _unused != null) {
         _unused.remove(SECRET_KEY, name);

         // If the size of the ProtectedPropertyReader becomes zero, then
         // remove it to save memory
         if (_unused.size() < 1) {
            _unused = null;
         }
      }

      return value;
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
    * Retrieves the set of unused properties.
    *
    * @return
    *    a {@link PropertyReader} containing which were not queried, never
    *    <code>null</code>.
    */
   public PropertyReader getUnused() {
      if (_unused != null) {
         return _unused;
      } else {
         return PropertyReaderUtils.EMPTY_PROPERTY_READER;
      }
   }
}
