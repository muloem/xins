/*
 * $Id$
 */
package org.xins.util.math;

import java.security.SecureRandom;
import java.util.Random;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xins.util.MandatoryArgumentChecker;

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

   /**
    * Logger for this class.
    */
   private static final Logger LOG = Logger.getLogger(SafeRandom.class.getName());


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
      _secureRandom = new SecureRandom();
      _random       = new Random();
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The name. Cannot be <code>null</code>.
    */
   private final String _name;

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
         if (LOG.isEnabledFor(Level.WARN)) {
            LOG.warn("SafeRandom \"" + _name + "\": Failed to generate secure random int, falling back to pseudo-random.");
         }
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
         if (LOG.isEnabledFor(Level.WARN)) {
            LOG.warn("SafeRandom \"" + _name + "\": Failed to generate secure random long, falling back to pseudo-random.");
         }
         return _random.nextLong();
      }
   }
}
