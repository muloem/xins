/*
 * $Id$
 */
package org.xins.logdoc;

/**
 * Abstract base class for <em>logdoc</em> <code>Log</code> classes.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public abstract class AbstractLog
extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The <em>debug</em> log level.
    */
   private static final Level DEBUG;

   /**
    * The <em>info</em> log level.
    */
   private static final Level INFO;

   /**
    * The <em>notice</em> log level.
    */
   private static final Level NOTICE;

   /**
    * The <em>warning</em> log level.
    */
   private static final Level WARNING;

   /**
    * The <em>error</em> log level.
    */
   private static final Level ERROR;

   /**
    * The <em>fatal</em> log level.
    */
   private static final Level FATAL;


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Initializes this class.
    */
   static {

      // Determine the int value for the NOTICE level
      int noticeInt = (Level.INFO_INT + Level.WARN_INT) / 2;
      if (noticeInt <= Level.INFO_INT || noticeInt >= Level.WARN_INT) {
         throw new Error("Unable to determine int value for NOTICE level between INFO and WARN. Value for INFO level is " + Level.INFO_INT + ". Value for WARN level is " + Level.WARN_INT + '.');
      }

      // Initialize all the log levels
      DEBUG   = Level.DEBUG;
      INFO    = Level.INFO;
      NOTICE  = new CustomLevel(noticeInt, "NOTICE", 5);
      WARNING = Level.WARN;
      ERROR   = Level.ERROR;
      FATAL   = Level.FATAL;
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>AbstractLog</code> instance.
    */
   protected AbstractLog() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
