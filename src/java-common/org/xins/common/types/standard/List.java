/*
 * $Id$
 *
 * Copyright 2003-2006 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.types.standard;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.types.ItemList;

/**
 * Standard type <em>_list</em>.
 *
 * @version $Revision$
 * @author <a href="mailto:anthony.goubard@orange-ft.com">Anthony Goubard</a>
 *
 * @since XINS 1.5.0.
 */
public final class List extends org.xins.common.types.List {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The only instance of this class. This field is never <code>null</code>.
    */
   public final static List SINGLETON = new List();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>List</code>.
    * This constructor is private, the field {@link #SINGLETON} should be
    * used.
    */
   private List() {
      super("_list", Text.SINGLETON);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public ItemList createList() {
      return new Value();
   }

   //-------------------------------------------------------------------------
   // Inner classes
   //-------------------------------------------------------------------------

	/**
	 * Inner class that represents a list of java.lang.String.
	 */
   public static final class Value extends ItemList {

      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------

      /**
       * Add a new element in the list.
       *
       * @param value
       *    the new value to add, cannot be <code>null</code>.
       *
       * @throws java.lang.IllegalArgumentException
       *    if <code>value == null</code>.
       */
      public void add(String value) {
         MandatoryArgumentChecker.check("value", value);
         addItem(value);
      }

      /**
       * Get an element from the list.
       *
       * @param index
       *    The position of the required element.
       *
       * @return
       *    The element at the specified position, cannot be <code>null</code>.
       */
      public String get(int index) {
         return (String) getItem(index);
      }
   }
}