/*
 * $Id$
 */
package org.xins.logdoc;

/**
 * Abstract base class for <code>LogdocSerializable</code> implementations,
 * with support for lazy initialization.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 0.201
 */
public abstract class AbstractLogdocSerializable
extends Object
implements LogdocSerializable {

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
    * Constructs a new <code>AbstractLogdocSerializable</code> instance. This
    * constructor is only available for subclasses.
    */
   protected AbstractLogdocSerializable() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * Flag that indicates whether this object has been successfully
    * initialized. Initially it is <code>false</code>.
    */
   private boolean _initialized;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Serializes this object for logging to the specified string buffer
    * (wrapper method). This method first checks if this object is already
    * initialized. If it is, then {@link #serializeImpl(LogdocStringBuffer)}
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
      if (! _initialized) {
         initialize();
         _initialized = true;
      }

      // Hand control to the implementation method
      serializeImpl(buffer);
   }

   /**
    * Initializes this object. This method will be called the first time
    * {@link #serialize(LogdocStringBuffer)} is called.
    */
   protected abstract void initialize();

   /**
    * Serializes this object for logging to the specified string buffer
    * (implementation method). This method is called from
    * {@link #serialize(LogdocStringBuffer)}, but not before
    * {@link #initialize()} is called exactly once, from that method.
    *
    * @param buffer
    *    the {@link LogdocStringBuffer} to serialize to, cannot be
    *    <code>null</code>.
    *
    * @throws NullPointerException
    *    if <code>buffer == null</code> (this can be accomplished by just
    *    accessing the object by calling one of the <code>append()</code>
    *    methods on it.
    */
   protected abstract void serializeImpl(LogdocStringBuffer buffer)
   throws NullPointerException;
}
