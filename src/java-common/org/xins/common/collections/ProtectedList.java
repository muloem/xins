/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.collections;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.xins.common.MandatoryArgumentChecker;

/**
 * Implementation of a list that can only be modified using the secret key
 * passed to the constructor.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.1.0
 */
public final class ProtectedList extends AbstractList implements Cloneable {

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
    * Constructs an empty <code>ProtectedList</code> with the specified
    * initial capacity.
    *
    * @param secretKey
    *    the secret key that must be passed to the modification methods in
    *    order to be authorized to modify this collection.
    *
    * @param initialCapacity
    *    the initial capacity, cannot be a negative number.
    *
    * @throws IllegalArgumentException
    *    if <code>secretKey == null || initialCapacity &lt; 0</code>.
    *
    * @since XINS 1.2.0
    */
   public ProtectedList(Object secretKey, int initialCapacity)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("secretKey", secretKey);

      _secretKey = secretKey;
      _list      = new ArrayList(initialCapacity);
   }

   /**
    * Constructs an empty <code>ProtectedList</code>.
    *
    * @param secretKey
    *    the secret key that must be passed to the modification methods in
    *    order to be authorized to modify this collection, cannot be
    *    <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>secretKey == null</code>.
    */
   public ProtectedList(Object secretKey)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("secretKey", secretKey);

      _secretKey = secretKey;
      _list      = new ArrayList();
   }

   /**
    * Constructs a new <code>ProtectedList</code> containing the elements of
    * the specified collection, in the order they are returned by the
    * collection's iterator.
    *
    * @param secretKey
    *    the secret key that must be passed to the modification methods in
    *    order to be authorized to modify this collection, cannot be
    *    <code>null</code>.
    *
    * @param c
    *    the collection whose elements are to be placed into this list, cannot
    *    be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>secretKey == null || c == null</code>.
    *
    * @since XINS 1.2.0
    */
   public ProtectedList(Object secretKey, Collection c)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("secretKey", secretKey, "c", c);

      _secretKey = secretKey;
      _list      = new ArrayList(c);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The secret key.
    */
   private final Object _secretKey;

   /**
    * The list containing the objects.
    */
   private ArrayList _list;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public Object get(int index) {
      return _list.get(index);
   }

   public int size() {
      return _list.size();
   }

   /**
    * Adds the specified element to the list.
    *
    * <p>The secret key must be passed. If it is incorrect, then an
    * {@link IllegalArgumentException} is thrown. Note that an identity check
    * is done, <em>not</em> an equality check. So
    * {@link Object#equals(Object)} is not used, but the <code>==</code>
    * operator is.
    *
    * @param secretKey
    *    the secret key, must be the same as the key specified with the
    *    constructor, cannot be <code>null</code>.
    *
    * @param element
    *    the element to add to the list, can be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if the secretKey is incorrect.
    */
   public void add(Object secretKey, Object element)
   throws IllegalArgumentException {

      // Check preconditions
      if (secretKey != _secretKey) {
         throw new IllegalArgumentException("Invalid key.");
      }

      // Store the value
      _list.add(element);
   }

   /**
    * Removes the specified element.
    *
    * <p>The key must be passed. If it is incorrect, then an
    * {@link IllegalArgumentException} is thrown. Note that an identity check
    * is done, <em>not</em> an equality check. So
    * {@link Object#equals(Object)} is not used, but the <code>==</code>
    * operator is.
    *
    * @param secretKey
    *    the secret key, must be the same as the key specified with the
    *    constructor, cannot be <code>null</code>.
    *
    * @param index
    *    the position of the element to remove.
    *
    * @throws IllegalArgumentException
    *    if the key is incorrect.
    */
   public void remove(Object secretKey, int index)
   throws IllegalArgumentException {

      // Check preconditions
      if (secretKey != _secretKey) {
         throw new IllegalArgumentException("Invalid key.");
      }

      // Remove the element
      _list.remove(index);
   }

   /**
    * Clones this list. The cloned list will only be ediatable by using the
    * same secret key.
    *
    * @return
    *    a new clone of this object, never <code>null</code>.
    */
   public Object clone() {
      ProtectedList clone = new ProtectedList(_secretKey);
      clone._list = (ArrayList)_list.clone();
      return clone;
   }
}
