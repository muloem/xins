/*
 * $Id$
 */
package org.xins.util.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Implentation of a Map that only contains 1 entry.
 *
 * @version $Revision$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public class SingleEntryMap implements Map {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>SingleEntryMap</code>.
    * 
    * @param key
    *    the key for the single entry, cannot be
    *    <code>null</code>.
    * @param value
    *    the value for the single entry, can be
    *    <code>null</code>.
    * 
    * @throw NullPointerException
    *    if the key is <code>null</code>.
    */
   public SingleEntryMap(Object key, Object value) {
      if (key == null) {
         throw new NullPointerException("The key cannot be null."); 
      }
      _key = key;
      _value = value;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The key of the single entry in the Map.
    */
   private Object _key;

   /**
    * The key of the single entry in the Map.
    */
   private Object _value;
   
   
   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public int size() {
      return 1;
   }
   
   public boolean isEmpty() {
      return false;
   }
   
   public boolean containsKey(Object key) {
      if (key == null) {
         throw new NullPointerException("The key cannot be null."); 
      }
      return key.equals(_key);
   }
   
   public boolean containsValue(Object value) {
      if (value == null) {
         return (_value == null);
      }
      return  (_value != null && _value.equals(value));
   }
   
   public Object get(Object key) {
      if (key == null) {
         throw new NullPointerException("The key cannot be null."); 
      }
      if (key.equals(_key)) {
         return _value; 
      } else {
         return null; 
      }
   }
   
   public Object put(Object key, Object value) {
      if (key == null) {
         throw new NullPointerException("The key cannot be null."); 
      }
      Object oldKey = _key;
      Object oldValue = _value;
      _key = key;
      _value = value;
      if (oldKey.equals(key)) {
         return oldValue;
      } else {
         return null; 
      }
   }
    
   public Object remove(Object key) {
      throw new UnsupportedOperationException("This map must contain one and only one entry.");
   }
    
   public void putAll(Map t) {
      throw new UnsupportedOperationException("This map must contain one and only one entry.");
   }
   
   public void clear() {
      throw new UnsupportedOperationException("This map must contain one and only one entry.");
   }
   
   public Set keySet() {
    
      // XXX This could be optimized by creating a SingleEntrySet
      Set keys = new TreeSet();
      keys.add(_key);
      return keys;
   }
   
   public Collection values() {
    
   	// XXX This could be optimized by creating a SingleEntryList
      Collection values = new ArrayList(1);
      values.add(_value);
      return values;
   }
   
   public Set entrySet() {
      Set entries = new TreeSet();
      entries.add(this);
      return entries;
   }
   
   public int hashCode() {
      return (_key == null ? 0 : _key.hashCode()) ^ 
             (_value == null ? 0 : _value.hashCode());   
   }
   
   public boolean equals(Object o) {
      if (!(o instanceof SingleEntryMap)) {
         return false; 
      }
      Map e2 = (SingleEntryMap)o;
      return entrySet().equals(e2.entrySet());
   }
   
   /**
    * The Map.Entry for this SingleEntryMap.
    */ 
   private class SingleEntry implements Map.Entry {
   
       public Object getKey() {
          return _key;
       }
       
       public Object getValue() {
          return _value;
       }
       
       public Object setValue(Object value) {
          Object oldValue = _value;
          _value = value;
          return oldValue;
       }
       
       public int hashCode() {
          return (_key == null ? 0 : _key.hashCode()) ^ 
                 (_value == null ? 0 : _value.hashCode());   
       }
       
       public boolean equals(Object o) {
          if (!(o instanceof Map.Entry)) {
             return false; 
          }
          Map.Entry e2 = (Map.Entry)o;
          return (_key.equals(e2.getKey()))  &&
                 (_value == null ? e2.getValue() == null : _value.equals(e2.getValue()));
       }
   }
}
