/*
 * $Id$
 */
package org.xins.common.math;

import java.security.SecureRandom;
import java.util.Random;
import org.xins.common.Log;
import org.xins.common.MandatoryArgumentChecker;

/**
 * Random generator facade that uses the securest available random generator.
 * The implementation creates a {@link SecureRandom} and a {@link Random}
 * object. A request for a random number will first attempt to use the
 * {@link SecureRandom} object. If that fails, then the {@link Random} object
 * will be used.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public final class SafeRandom extends Object {

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
    * Constructs a new <code>SafeRandom</code> object with the specified name.
    * The name is used in log messages.
    *
    * @param name
    *    the name, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null</code>.
    */
   public SafeRandom(String name)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("name", name);

      // Initialize instance fields
      _name         = name;
      _asString     = "SafeRandom \"" + name + '"';
      _secureRandom = new SecureRandom();
      _random       = new Random();
      _intWarning   = _asString + ": Failed to generate secure random int, falling back to pseudo-random.";
      _longWarning  = _asString + ": Failed to generate secure random long, falling back to pseudo-random.";
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The name. Cannot be <code>null</code>.
    */
   private final String _name;

   /**
    * Textual representation of this object. Cannot be <code>null</code>.
    */
   private final String _asString;

   /**
    * The <code>SecureRandom</code> object, the preferred random generator to
    * use. Cannot be <code>null</code>.
    */
   private final SecureRandom _secureRandom;

   /**
    * The <code>Random</code> object, the least preferred random generator to
    * use. Cannot be <code>null</code>.
    */
   private final Random _random;

   /**
    * The warning logged if an <code>int</code> could not be generated using
    * the <code>SecureRandom</code>.
    */
   private final String _intWarning;

   /**
    * The warning logged if an <code>long</code> could not be generated using
    * the <code>SecureRandom</code>.
    */
   private final String _longWarning;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Generates the next (pseudo-)random <code>int</code> value.
    *
    * @return
    *    the next (pseudo)random number, can be any <code>int</code> value.
    */
   public int nextInt() {

      try {
         return _secureRandom.nextInt();
      } catch (Throwable exception) {
         Log.log_3203(_asString);
         return _random.nextInt();
      }
   }

   /**
    * Generates the next (pseudo)random <code>long</code> value.
    *
    * @return
    *    the next (pseudo)random number, can be any <code>long</code> value.
    */
   public long nextLong() {
      try {
         return _secureRandom.nextLong();
      } catch (Throwable exception) {
         Log.log_3204(_asString);
         return _random.nextLong();
      }
   }

   /**
    * Returns a textual representation of this object.
    *
    * @return
    *    textual representation of this object, never <code>null</code>.
    */
   public String toString() {
      return _asString;
   }
}
