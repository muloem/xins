/*
 * $Id$
 *
 * Copyright 2004 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.collections;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.text.FastStringBuffer;

/**
 * Exception thrown to indicate the property of a value is invalid.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
public final class InvalidPropertyValueException
extends Exception {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Creates message based on the specified constructor arguments.
    *
    * @param propertyName
    *    the name of the property, cannot be <code>null</code>.
    *
    * @param propertyValue
    *    the (invalid) value set for the property, cannot be
    *    <code>null</code>.
    *
    * @param reason
    *    additional description of the problem, or <code>null</code>.
    *
    * @return
    *    the message, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>propertyName == null || propertyValue == null</code>.
    */
   private static final String createMessage(String propertyName,
                                             String propertyValue,
                                             String reason)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("propertyName",  propertyName,
                                     "propertyValue", propertyValue);

      // Construct the message
      FastStringBuffer buffer = new FastStringBuffer(150);
      buffer.append("The value \"");
      buffer.append(propertyValue);
      buffer.append("\" is invalid for property \"");
      buffer.append(propertyName);
      if (reason == null) {
         buffer.append("\".");
      } else {
         buffer.append("\": ");
         buffer.append(reason);
      }

      return buffer.toString();
   }


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>InvalidPropertyValueException</code>.
    *
    * @param propertyName
    *    the name of the property, cannot be <code>null</code>.
    *
    * @param propertyValue
    *    the (invalid) value set for the property, cannot be
    *    <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>propertyName == null || propertyValue == null</code>.
    */
   public InvalidPropertyValueException(String propertyName,
                                        String propertyValue)
   throws IllegalArgumentException {

      this(propertyName, propertyValue, null);
   }

   /**
    * Constructs a new <code>InvalidPropertyValueException</code> with the
    * specified reason.
    *
    * @param propertyName
    *    the name of the property, cannot be <code>null</code>.
    *
    * @param propertyValue
    *    the (invalid) value set for the property, cannot be
    *    <code>null</code>.
    *
    * @param reason
    *    additional description of the problem, or <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>propertyName == null || propertyValue == null</code>.
    *
    * @since XINS 0.150
    */
   public InvalidPropertyValueException(String propertyName,
                                        String propertyValue,
                                        String reason)
   throws IllegalArgumentException {

      // Construct message and call superclass constructor
      super(createMessage(propertyName, propertyValue, reason));

      // Store data
      _propertyName  = propertyName;
      _propertyValue = propertyValue;
      _reason        = reason;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The name of the property. Cannot be <code>null</code>.
    */
   private final String _propertyName;

   /**
    * The (invalid) value of the property. Cannot be <code>null</code>.
    */
   private final String _propertyValue;

   /**
    * The detailed reason. Can be <code>null</code>.
    */
   private final String _reason;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the name of the property.
    *
    * @return
    *    the name of the property, never <code>null</code>.
    */
   public String getPropertyName() {
      return _propertyName;
   }

   /**
    * Returns the (invalid) value of the property.
    *
    * @return
    *    the value of the property, never <code>null</code>.
    */
   public String getPropertyValue() {
      return _propertyValue;
   }

   /**
    * Returns the description of the reason.
    *
    * @return
    *    the reason, or <code>null</code>.
    *
    * @since XINS 0.150
    */
   public String getReason() {
      return _reason;
   }
}
