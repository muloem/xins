/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.collections;

import java.util.HashMap;
import org.xins.common.MandatoryArgumentChecker;

/**
 * Modifiable implementation of a property reader, can be protected from
 * unauthorized changes.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.0.0
 */
public final class ProtectedPropertyReader
extends AbstractPropertyReader {

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
    * Constructs a new <code>ProtectedPropertyReader</code>.
    *
    * @param secretKey
    *    the secret key that must be passed to {@link #set(Object,String,String)}
    *    in order to be authorized to modify this set of properties.
    *
    * @throws IllegalArgumentException
    *    if <code>secretKey == null</code>.
    */
   public ProtectedPropertyReader(Object secretKey)
   throws IllegalArgumentException {
      super(new HashMap(89));

      // Check preconditions
      MandatoryArgumentChecker.check("secretKey", secretKey);

      _secretKey = secretKey;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The secret key.
    */
   private final Object _secretKey;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Verifies that the specified object matches the secret key. If not, an
    * exception is thrown.
    *
    * @param secretKey
    *    the secret key, must be identity-equal to the secret key passed to
    *    the constructor, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>secretKey</code> does not match the secret key passed to the
    *    constructor.
    */
   private void checkSecretKey(Object secretKey)
   throws IllegalArgumentException {
      if (secretKey != _secretKey) {
         throw new IllegalArgumentException("Incorrect secret key.");
      }
   }

   /**
    * Sets the specified property to the specified value.
    *
    * <p>The correct secret key must be passed. If it is incorrect, then an
    * {@link IllegalArgumentException} is thrown. Note that an identity check
    * is done, <em>not</em> an equality check. So
    * {@link Object#equals(Object)} is not used, but the <code>==</code>
    * operator is.
    *
    * @param secretKey
    *    the secret key, must be identity-equal to the secret key passed to
    *    the constructor, cannot be <code>null</code>.
    *
    * @param name
    *    the name of the property to set or reset, cannot be
    *    <code>null</code>.
    *
    * @param value
    *    the value for the property, can be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null</code> or if <code>secretKey</code> does not
    *    match the secret key passed to the constructor.
    */
   public void set(Object secretKey, String name, String value)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("name", name);
      checkSecretKey(secretKey);

      // Store the value
      getPropertiesMap().put(name, value);
   }

   /**
    * Removes the specified property.
    *
    * <p>The correct secret key must be passed. If it is incorrect, then an
    * {@link IllegalArgumentException} is thrown. Note that an identity check
    * is done, <em>not</em> an equality check. So
    * {@link Object#equals(Object)} is not used, but the <code>==</code>
    * operator is.
    *
    * @param secretKey
    *    the secret key, must be identity-equal to the secret key passed to
    *    the constructor, cannot be <code>null</code>.
    *
    * @param name
    *    the name of the property to remove, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null</code> or if <code>secretKey</code> does not
    *    match the secret key passed to the constructor.
    */
   public void remove(Object secretKey, String name)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("name", name);
      checkSecretKey(secretKey);

      // Remove the property
      getPropertiesMap().remove(name);
   }

   /**
    * Removes all properties.
    *
    * <p>The correct secret key must be passed. If it is incorrect, then an
    * {@link IllegalArgumentException} is thrown. Note that an identity check
    * is done, <em>not</em> an equality check. So
    * {@link Object#equals(Object)} is not used, but the <code>==</code>
    * operator is.
    *
    * @param secretKey
    *    the secret key, must be identity-equal to the secret key passed to
    *    the constructor, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>secretKey</code> does not match the secret key passed to the
    *    constructor.
    *
    * @since XINS 1.2.0
    */
   public void clear(Object secretKey)
   throws IllegalArgumentException {
      checkSecretKey(secretKey);
      getPropertiesMap().clear();
   }
}
