/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.logdoc;

/**
 * Abstract base class for <code>LogdocSerializable</code> implementations,
 * with support for lazy initialization.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 * @deprecated since XINS 2.0, replace by the toString() method of the class.
 */
public abstract class AbstractLogdocSerializable implements LogdocSerializable {

   /**
    * Constructs a new <code>AbstractLogdocSerializable</code> instance. This
    * constructor is only available for subclasses.
    */
   protected AbstractLogdocSerializable() {
      // empty
   }

   /**
    * The serialized version of this object. The value is <code>null</code> as
    * long as it has not been initialized yet.
    */
   private String _asString;

   /**
    * Serializes this object for logging to the specified string buffer
    * (wrapper method). This method first checks if this object is already
    * initialized. If it is, then {@link #serialize(LogdocStringBuffer)}
    * is immediately called, otherwise {@link #initialize()} is called first,
    * to initialize this object.
    *
    * @param buffer
    *    the {@link LogdocStringBuffer} to serialize to, cannot be
    *    <code>null</code>.
    *
    * @throws NullPointerException
    *    if <code>buffer == null</code> (this is checked only <em>after</em>
    *    the optional initialization is performed).
    */
   public final void serialize(LogdocStringBuffer buffer)
   throws NullPointerException {

      // Initialize this object if that is not done yet
      if (_asString == null) {
         _asString = initialize();
         if (_asString == null) {
            _asString = "";
         }
      }

      buffer.append(_asString);
   }

   /**
    * Serializes this object for logging to the specified string buffer
    * (wrapper method). This method first checks if this object is already
    * initialized. If it is, then {@link #serialize(LogdocStringBuffer)}
    * is immediately called, otherwise {@link #initialize()} is called first,
    * to initialize this object.
    *
    * @param buffer
    *    the {@link StringBuffer} to serialize to, cannot be
    *    <code>null</code>.
    *
    * @throws NullPointerException
    *    if <code>buffer == null</code>.
    */
   public final void serialize(StringBuffer buffer) throws NullPointerException {

      // Initialize this object if that is not done yet
      if (_asString == null) {
         _asString = initialize();
         if (_asString == null) {
            _asString = "";
         }
      }

      buffer.append(_asString);
   }

   /**
    * Initializes this object. This method will be called the first time
    * {@link #serialize(LogdocStringBuffer)} is called. It should return the
    * serialized form of this object which will from then on be returned from
    * {@link #serialize(LogdocStringBuffer)}.
    *
    * @return
    *    the serialized form of this object which will from then on be
    *    returned from {@link #serialize(LogdocStringBuffer)}, a
    *    <code>null</code> will be interpreted as an empty string.
    */
   protected abstract String initialize();
}
