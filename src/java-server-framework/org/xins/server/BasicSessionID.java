/*
 * $Id$
 */
package org.xins.server;

import org.xins.types.Type;
import org.xins.types.TypeValueException;
import org.xins.util.LongUtils;

/**
 * Type for basic session IDs.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public final class BasicSessionID extends SessionID {

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
    * Constructs a new <code>BasicSessionID</code> type for the specified API.
    *
    * @param api
    *    the API for which to create the type, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>api == null</code>.
    */
   BasicSessionID(API api) throws IllegalArgumentException {
      super("basicSessionID", java.lang.Long.class, api);
      _prefix = LongUtils.toHexString(api.getStartupTimestamp()) + ':';
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
      if (value.length() != 17) {
         return false;
      } else if (_prefix.equals(value.substring(0, 9)) == false) {
         return false;
      }

      try {
         // TODO: Use parseHexString(value, 9) instead
         LongUtils.parseHexString(value.substring(9));
         return true;
      } catch (NumberFormatException nfe) {
         return false;
      }
   }

   protected Object fromStringImpl(String string)
   throws TypeValueException {
      if (string == null) {
         throw new IllegalArgumentException("string == null");
      } else if (string.length() != 17) {
         throw new TypeValueException(this, string);
      }

      if (_prefix.equals(string.substring(0, 9)) == false) {
         throw new TypeValueException(this, string);
      }

      try {
         // TODO: Use parseHexString(string, 9) instead
         return new Long(LongUtils.parseHexString(string.substring(9)));
      } catch (NumberFormatException nfe) {
         throw new TypeValueException(this, string);
      }
   }

   public final SessionID.Generator getGenerator() {
      return _generator;
   }


   //-------------------------------------------------------------------------
   // Inner classes
   //-------------------------------------------------------------------------

   /**
    * Session ID generator for <code>BasicSessionID</code>. Session IDs
    * generated by this class are constructed as follows:
    *
    * <ul>
    *    <li>Startup timestamp (exactly 8 hex digits)
    *    <li>Colon (<code>':'</code>)
    *    <li>Counter (exactly 8 hex digits)
    * </ul>
    *
    * @version $Revision$ $Date$
    * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
    *
    * @since XINS 0.57
    */
   private final class Generator
   extends SessionID.Generator {

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      /**
       * Constructs a new <code>Generator</code> for the specified API.
       */
      private Generator() {
         _lock = new Object();
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      /**
       * Counter for the session IDs. Will be used to generate the session ID.
       */
      private long _counter;

      /**
       * Object that will be locked on before the value of <code>_counter</code>
       * is get and set.
       */
      private Object _lock;


      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------

      public String generateSessionID() {
         // TODO: Improve performance ?
         long num;
         synchronized (_lock) {
            num = _counter++;
         }
         return _prefix + LongUtils.toHexString(num);
      }
   }
}
