/*
 * $Id$
 *
 * Copyright 2003-2006 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.service;

import java.io.Serializable;

import org.xins.common.text.FastStringBuffer;

/**
 * Configuration for a service call. Objects of this type specify certain
 * aspects of <em>how</em> a call is executed. For example, for an HTTP
 * service caller, a <code>CallConfig</code> object could specify what HTTP
 * method (GET, POST, etc.) to use.
 *
 * <p>This base class only specifies the property <em>failOverAllowed</em>,
 * which indicates whether fail-over is unconditionally allowed, even if the
 * request was already received or even processed by the other end.
 *
 * <h2>Thread-safety</h2>
 *
 * <p>This class is thread-safe, and subclasses <em>must</em> be thread-safe
 * as well. When reading or writing a field, the code should synchronize on
 * the lock object returned by {@link #getLock()}. For example, the
 * <em>failOverAllowed</em> getter and setter methods in this class could be
 * implemented as follows:
 *
 * <blockquote><pre>public final boolean isFailOverAllowed() {
 *   synchronized (getLock()) {
 *      return _failOverAllowed;
 *   }
 *}
 *
 *public final void setFailOverAllowed(boolean allowed) {
 *   synchronized (getLock()) {
 *      _failOverAllowed = allowed;
 *   }
 *}</pre></blockquote>
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.1.0
 *
 * @see ServiceCaller
 * @see CallRequest
 */
public class CallConfig implements Serializable {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The number of instances of this class. Initially zero.
    */
   private static int INSTANCE_COUNT;

   /**
    * Lock object for field <code>INSTANCE_COUNT</code>.
    */
   private static Object INSTANCE_COUNT_LOCK = new Object();


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>CallConfig</code> object.
    */
   public CallConfig() {

      // First determine instance number
      synchronized (INSTANCE_COUNT_LOCK) {
         _instanceNumber = ++INSTANCE_COUNT;
      }

      // Create lock object
      _lock = new Object();
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The 1-based sequence number of this instance. Since this number is
    * 1-based, the first instance of this class will have instance number 1
    * assigned to it.
    */
   private final int _instanceNumber;

   /**
    * Access controller for the fields in this object. Field reading or
    * writing code should synchronize on this object.
    */
   private transient final Object _lock;

   /**
    * Flag that indicates whether fail-over is unconditionally allowed.
    */
   private boolean _failOverAllowed;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the access controller for the fields in this object. Field
    * reading or writing code should synchronize on this object.
    *
    * @return
    *    the lock, never <code>null</code>.
    */
   protected final Object getLock() {
      return _lock;
   }

   /**
    * Describes this configuration. The description should be trimmed and
    * should fit in a sentence. Good examples include
    * <code>"HTTP call config #1592 [failOverAllowed=true,
    * method=\"POST\"]"</code> and <code>"HTTP call config #12
    * [failOverAllowed=false, method=(null)]"</code>
    *
    * <p>The implementation of this method in class {@link CallConfig} returns
    * a descriptive string that contains an instance number and the
    * <em>failOverAllowed</em> setting.
    *
    * @return
    *    the description of this configuration, should never be
    *    <code>null</code>, should never be empty and should never start or
    *    end with whitespace characters.
    */
   public String describe() {
      boolean failOverAllowed;
      synchronized (_lock) {
         failOverAllowed = _failOverAllowed;
      }

      FastStringBuffer buffer = new FastStringBuffer(55);
      buffer.append("call config #");
      buffer.append(_instanceNumber);
      buffer.append(" [failOverAllowed=");
      buffer.append(failOverAllowed);
      buffer.append(']');

      return buffer.toString();
   }

   /**
    * Returns a textual presentation of this object.
    *
    * <p>The implementation of this method in class {@link CallRequest}
    * returns {@link #describe()}.
    *
    * @return
    *    a textual presentation of this object, should never be
    *    <code>null</code>.
    */
   public final String toString() {
      return describe();
   }

   /**
    * Determines whether fail-over is unconditionally allowed.
    *
    * @return
    *    <code>true</code> if fail-over is unconditionally allowed, even if
    *    the request was already received or even processed by the other end,
    *    <code>false</code> otherwise.
    */
   public final boolean isFailOverAllowed() {
      synchronized (_lock) {
         return _failOverAllowed;
      }
   }

   /**
    * Configures whether fail-over is unconditionally allowed.
    *
    * @param allowed
    *    <code>true</code> if fail-over is unconditionally allowed, even if
    *    the request was already received or even processed by the other end,
    *    <code>false</code> otherwise.
    */
   public final void setFailOverAllowed(boolean allowed) {
      synchronized (_lock) {
         _failOverAllowed = allowed;
      }
   }
}
