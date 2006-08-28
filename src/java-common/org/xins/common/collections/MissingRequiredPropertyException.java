/*
 * $Id$
 *
 * Copyright 2003-2006 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.collections;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.text.FastStringBuffer;
import org.xins.common.text.TextUtils;

/**
 * Exception thrown to indicate a required property has no value set for it.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst.dehaan@orange-ft.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 *
 * @see InvalidPropertyValueException
 */
public final class MissingRequiredPropertyException
extends Exception {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Creates message based on the specified constructor argument.
    *
    * @param propertyName
    *    the name of the property, cannot be <code>null</code>.
    *
    * @param detail
    *    a more detailed description of why this property is required in this
    *    context, can be <code>null</code>.
    *
    * @return
    *    the message, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>propertyName == null</code>.
    */
   private static String createMessage(String propertyName, String detail)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("propertyName", propertyName);

      // Construct the message
      FastStringBuffer buffer = new FastStringBuffer(120);
      buffer.append("No value is set for the required property \"");
      buffer.append(propertyName);

      // Append the detail message, if any
      detail = TextUtils.trim(detail, null);
      if (detail != null) {
         buffer.append("\": ");
         buffer.append(detail);
      } else {
         buffer.append("\".");
      }

      return buffer.toString();
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>MissingRequiredPropertyException</code>, with the
    * specified detail message.
    *
    * @param propertyName
    *    the name of the required property, not <code>null</code>.
    *
    * @param detail
    *    a more detailed description of why this property is required in this
    *    context, can be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>propertyName == null</code>.
    *
    * @since XINS 1.3.0
    */
   public MissingRequiredPropertyException(String propertyName, String detail)
   throws IllegalArgumentException {

      // Construct message and call superclass constructor
      super(createMessage(propertyName, detail));

      // Store data
      _propertyName = propertyName;
      _detail       = TextUtils.trim(detail, null);
   }

   /**
    * Constructs a new <code>MissingRequiredPropertyException</code>.
    *
    * @param propertyName
    *    the name of the required property, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>propertyName == null</code>.
    */
   public MissingRequiredPropertyException(String propertyName)
   throws IllegalArgumentException {
      this(propertyName, null);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The name of the property. Never <code>null</code>.
    */
   private final String _propertyName;

   /**
    * Detailed description of why this property is required in the current
    * context. Can be <code>null</code>.
    */
   private final String _detail;


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
    * Returns the detail message.
    *
    * @return
    *    the trimmed detail message, can be <code>null</code>.
    *
    * @since XINS 1.3.0
    */
   public String getDetail() {
      return _detail;
   }
}
