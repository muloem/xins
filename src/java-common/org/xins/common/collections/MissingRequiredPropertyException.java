/*
 * $Id$
 */
package org.xins.common.collections;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.text.FastStringBuffer;

/**
 * Exception thrown to indicate a required property has no value set for it.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
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
    * @return
    *    the message, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>propertyName == null</code>.
    */
   private static final String createMessage(String propertyName)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("propertyName", propertyName);

      // Construct the message
      FastStringBuffer buffer = new FastStringBuffer(120);
      buffer.append("No value is set for the required property \"");
      buffer.append(propertyName);
      buffer.append("\".");

      return buffer.toString();
   }


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

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

      // Construct message and call superclass constructor
      super(createMessage(propertyName));

      // Store data
      _propertyName = propertyName;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The name of the property.
    */
   private final String _propertyName;


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
}
