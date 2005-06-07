/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.spec;

/**
 * Specification of a data section element.
 *
 * @version $Revision$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public abstract class DataSectionElement {
   
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
    * Creates a new instance of DataSectionElement
    */
   public DataSectionElement() {
   }
   
   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------
   
   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Gets the name of the data element.
    *
    * @return
    *    The name of the data element, never <code>null</code>.
    */
   public abstract String getName();
   
   /**
    * Gets the description of the data element.
    *
    * @return
    *    The description of the data element, never <code>null</code>.
    */
   public abstract String getDescription();

   /**
    * Gets the sub elements that are included in this element.
    *
    * @return
    *    The specification of the sub elements, never <code>null</code>.
    */
   public abstract DataSectionElement[] getSubElements();
   
   /**
    * Gets the attributes of the element.
    *
    * @return
    *    The specification of the attributes, never <code>null</code>.
    */
   public abstract Parameter[] getAttributes();
   
   /**
    * Returns whether the element can contain a PCDATA text.
    *
    * @return
    *    <code>true</code> if the element can contain text, <code>false</code> otherwise.
    */
   public abstract boolean hasText();
}
