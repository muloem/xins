/*
 * $Id$
 */
package org.xins.util.math;

import java.security.SecureRandom;
import java.util.Random;

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
    * Constructs a new <code>SafeRandom</code> object.
    */
   public SafeRandom() {
      _secureRandom = new SecureRandom();
      _random       = new Random();
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The <code>SecureRandom</code> object, the preferred random generator to
    * use.
    */
   private SecureRandom _secureRandom;

   /**
    * The <code>Random</code> object, the least preferred random generator to
    * use.
    */
   private Random _random;


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
         return _random.nextLong();
      }
   }
}
