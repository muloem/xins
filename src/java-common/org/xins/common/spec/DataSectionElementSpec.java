/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.spec;

import java.util.Map;

import org.xins.common.MandatoryArgumentChecker;

/**
 * Specification of a data section element.
 *
 * @version $Revision$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 *
 * @since XINS 1.3.0
 */
public class DataSectionElementSpec {

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
   DataSectionElementSpec(String name, String description, boolean isPCDataAllowed, Map subElements, Map attributes) {
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
   private Map _attributes;

   /**
    * Flag indicating that the element can have PCDATA.
    */
   private boolean _isPCDataAllowed;
   
   /**
    * The sub elements of the element.
    */
   private Map _subElements;

   
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
    * Gets the specified sub element that are included in this element.
    *
    * @param elementName
    *    the name of the element, cannot be <code>null</code>.
    *
    * @return
    *    The specification of the sub element, never <code>null</code>.
    *
    * @throws EntityNotFoundException
    *    if the element does not have any sub element with the specified name.
    *
    * @throws IllegalArgumentException
    *    if <code>elementName == null</code>.
    */
   public DataSectionElementSpec getSubElement(String elementName)
   throws EntityNotFoundException, IllegalArgumentException {
      
      MandatoryArgumentChecker.check("elementName", elementName);
      
      DataSectionElementSpec element = (DataSectionElementSpec) _subElements.get(elementName);
      
      if (element == null) {
         throw new EntityNotFoundException("Sub element \"" + elementName 
                 + "\" not found in the element \"" + _name +"\".");
      }
      
      return element;
   }

   /**
    * Gets the specification of the sub elements that are included in this element.
    * The key is the name of the element, the value is the {@link DataSectionElementSpec} object.
    *
    * @return
    *    The specification of the sub elements, never <code>null</code>.
    */
   public Map getSubElements() {
      
      return _subElements;
   }

   /**
    * Gets the specification of the specified attribute of the element.
    *
    * @param attributeName
    *    the name of the attribute, cannot be <code>null</code>.
    *
    * @return
    *    The specification of the attribute, never <code>null</code>.
    *
    * @throws EntityNotFoundException
    *    if the element does not have any attribute with the specified name.
    *
    * @throws IllegalArgumentException
    *    if <code>attributeName == null</code>.
    */
   public ParameterSpec getAttribute(String attributeName)
   throws EntityNotFoundException, IllegalArgumentException {
      
      MandatoryArgumentChecker.check("attributeName", attributeName);
      
      ParameterSpec attribute = (ParameterSpec) _attributes.get(attributeName);
      
      if (attribute == null) {
         throw new EntityNotFoundException("Attribute \"" + attributeName
                 + "\" not found in the element \"" + _name +"\".");
      }
      
      return attribute;
   }

   /**
    * Gets the attributes of the element.
    * The key is the name of the attribute, the value is the {@link ParameterSpec} object.
    *
    * @return
    *    The specification of the attributes, never <code>null</code>.
    */
   public Map getAttributes() {
      
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
