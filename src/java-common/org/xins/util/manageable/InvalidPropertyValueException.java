/*
 * $Id$
 */
package org.xins.util.manageable;

import org.xins.util.MandatoryArgumentChecker;
import org.xins.util.text.FastStringBuffer;

/**
 * Exception thrown to indicate the property of a value is invalid. This
 * exception applies to both bootstrapping
 * ({@link Manageable#bootstrap(PropertyReader)}) and initialization
 * ({@link Manageable#init(PropertyReader)}).
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
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
    * @return
    *    the message, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>propertyName == null || propertyValue == null</code>.
    */
   private static final String createMessage(String propertyName, String propertyValue)
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
   public InvalidPropertyValueException(String propertyName, String propertyValue)
   throws IllegalArgumentException {

      // Construct message and call superclass constructor
      super(createMessage(propertyName, propertyValue));

      // Store data
      _propertyName  = propertyName;
      _propertyValue = propertyValue;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The name of the property.
    */
   private final String _propertyName;

   /**
    * The (invalid) value of the property.
    */
   private final String _propertyValue;


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
}
