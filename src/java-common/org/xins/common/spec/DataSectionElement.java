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
public class DataSectionElement {

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
    * Creates a new instance of DataSectionElement.
    *
    * @param name
    *    the name of the data section element, cannot be <code>null</code>.
    *
    * @param description
    *    the description of the data section element, cannot be <code>null</code>.
    *
    * @param isPCDataAllowed
    *    <code>true</code> if the element can contain text, <code>false</code> otherwise.
    *
    * @param subElements
    *    the sub elements that can contain this element, cannot be <code>null</code>.
    *
    * @param attributes
    *    the possible attributes for this element, cannot be <code>null</code>.
    */
   DataSectionElement(String name, String description, boolean isPCDataAllowed, DataSectionElement[] subElements, Parameter[] attributes) {
      _name = name;
      _description = description;
      _isPCDataAllowed = isPCDataAllowed;
      _attributes = attributes;
      _subElements = subElements;
   }

   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------
   
   /**
    * Name of the element.
    */
   private final String _name;
   
   /**
    * Description of the element.
    */
   private String _description;
   
   /**
    * The attributes of the element.
    */
   private Parameter[] _attributes;

   /**
    * Flag indicating that the element can have PCDATA.
    */
   private boolean _isPCDataAllowed;
   
   /**
    * The sub elements of the element.
    */
   private DataSectionElement[] _subElements;

   
   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Gets the name of the data element.
    *
    * @return
    *    The name of the data element, never <code>null</code>.
    */
   public String getName() {
      
      return _name;
   }

   /**
    * Gets the description of the data element.
    *
    * @return
    *    The description of the data element, never <code>null</code>.
    */
   public String getDescription() {
      
      return _description;
   }

   /**
    * Gets the sub elements that are included in this element.
    *
    * @return
    *    The specification of the sub elements, never <code>null</code>.
    */
   public DataSectionElement[] getSubElements() {
      
      return _subElements;
   }

   /**
    * Gets the attributes of the element.
    *
    * @return
    *    The specification of the attributes, never <code>null</code>.
    */
   public Parameter[] getAttributes() {
      
      return _attributes;
   }

   /**
    * Returns whether the element can contain a PCDATA text.
    *
    * @return
    *    <code>true</code> if the element can contain text, <code>false</code> otherwise.
    */
   public boolean isPCDataAllowed() {
      
      return _isPCDataAllowed;
   }
}
