/*
 * $Id$
 */
package org.xins.server;

import org.xins.types.Type;
import org.xins.types.TypeValueException;
import org.xins.util.MandatoryArgumentChecker;
import org.xins.util.math.SafeRandom;
import org.xins.util.text.FastStringBuffer;
import org.xins.util.text.HexConverter;

/**
 * Type for basic session IDs.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.57
 */
public final class BasicSessionIDType extends SessionIDType {

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
    * Constructs a new <code>BasicSessionIDType</code> for the specified API.
    *
    * @param api
    *    the API for which to create the type, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>api == null</code>.
    */
   BasicSessionIDType(API api) throws IllegalArgumentException {
      super("basicSessionID", SessionID.class, api);
      FastStringBuffer buffer = new FastStringBuffer(17, HexConverter.toHexString(api.getStartupTimestamp()));
      buffer.append(':');
      _prefix    = buffer.toString();
      _generator = new Generator();
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The session ID generator for this session ID type.
    */
   private final Generator _generator;

   /**
    * The prefix that all generated session IDs have.
    */
   private final String _prefix;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   protected boolean isValidValueImpl(String value) {

      // Make sure the length of the string is correct
      if (value.length() != 33) {
         return false;
      } else if (_prefix.equals(value.substring(0, 17)) == false) {
         return false;
      }

      try {
         HexConverter.parseHexString(value, 17);
         return true;
      } catch (NumberFormatException nfe) {
         return false;
      }
   }

   protected Object fromStringImpl(String string)
   throws TypeValueException {
      if (string == null) {
         throw new IllegalArgumentException("string == null");
      } else if (string.length() != 33) {
         throw new TypeValueException(this, string);
      }

      if (_prefix.equals(string.substring(0, 17)) == false) {
         throw new TypeValueException(this, string);
      }

      try {
         return new SessionID(HexConverter.parseHexString(string, 17));
      } catch (NumberFormatException nfe) {
         throw new TypeValueException(this, string);
      }
   }

   public String toString(Object value) {
      MandatoryArgumentChecker.check("value", value);
      SessionID sessionID = (SessionID) value;
      FastStringBuffer buffer = new FastStringBuffer(33, _prefix);
      buffer.append(sessionID.toString());
      return buffer.toString();
   }

   public final SessionIDType.Generator getGenerator() {
      return _generator;
   }


   //-------------------------------------------------------------------------
   // Inner classes
   //-------------------------------------------------------------------------

   /**
    * Session ID generator for <code>BasicSessionIDType</code>.
    *
    * @version $Revision$ $Date$
    * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
    *
    * @since XINS 0.57
    */
   private final class Generator
   extends SessionIDType.Generator {

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      /**
       * Constructs a new <code>Generator</code> for the specified API.
       */
      private Generator() {
         _lock = new Object();
         _randomizer = new SafeRandom();
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      /**
       * Object that will be locked on if {@link #_randomizer} needs to be
       * accessed.
       */
      private Object _lock;

      /**
       * The random number generator for the session IDs.
       */
      private final SafeRandom _randomizer;


      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------

      public Object generateSessionID() {
         // TODO: Improve performance ?
         long num;
         synchronized (_lock) {
            num = _randomizer.nextLong();
         }
         return new SessionID(num);
      }
   }

   /**
    * Session ID used in <code>BasicSessionIDType</code>.
    *
    * @version $Revision$ $Date$
    * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
    *
    * @since XINS 0.144
    */
   private final class SessionID
   extends Object {

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      /**
       * Constructs a new <code>SessionID</code> object with the specified ID
       * number.
       */
      private SessionID(long id) {
         _id = id;
         _asString = HexConverter.toHexString(id);
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      /**
       * The ID number.
       */
      private final long _id;

      /**
       * The ID number, converted to a hexadecimal string.
       */
      private final String _asString;


      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------

      public int hashCode() {
         return (int) _id;
      }

      public boolean equals(Object o) {
         return ((o instanceof SessionID) && ((SessionID) o)._id == _id);
      }

      public String toString() {
         return _asString;
      }
   }
}
