/*
 * $Id$
 */
package org.xins.util.sd;

import org.xins.util.collections.PropertyReader;

/**
 * Builder that can build a <code>Descriptor</code> object based on a set of
 * properties.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.105
 */
public final class DescriptorBuilder extends Object {

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
    * Constructs a new <code>DescriptorBuilder</code>.
    */
   public DescriptorBuilder() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Builds a <code>Descriptor</code> based on the specified set of
    * properties.
    *
    * @param properties
    *    the properties to read from, cannot be <code>null</code>.
    *
    * @param propertyName
    *    the base for the property names, cannot be <code>null</code>.
    *
    * @return
    *    the {@link Descriptor} that was built, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>properties == null || propertyName == null</code>.
    */
   public Descriptor build(PropertyReader properties, String propertyName)
   throws IllegalArgumentException, DescriptorBuilder.Exception {
      return null; // TODO
   }


   //-------------------------------------------------------------------------
   // Inner classes
   //-------------------------------------------------------------------------

   /**
    * Exception thrown if a descriptor could not be built.
    *
    * @version $Revision$ $Date$
    * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
    *
    * @since XINS 0.105
    */
   public static final class Exception extends java.lang.Exception {

      //----------------------------------------------------------------------
      // Constructor
      //----------------------------------------------------------------------

      /**
       * Constructs a new <code>DescriptorBuilder.Exception</code> with the
       * spedified detail message.
       *
       * @param message
       *    the detail message, can be <code>null</code>.
       */
      Exception(String message) {
         super(message);
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------
   }
}
