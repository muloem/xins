/*
 * $Id$
 */
package org.xins.logdoc;

/**
 * Statistics for a <em>logdoc</em> <code>Log</code> class. It contains the
 * number of occurrences per log entry.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
public final class LogStatistics
extends Object {

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
    * Constructs a new <code>LogStatistics</code> instance containing the
    * specified entries.
    *
    * @param entries
    *    the list of entries, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>entries == null</code>.
    */
   public LogStatistics(Entry[] entries)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("entries", entries);

      _entries = new Entry[entries.length];
      System.arraycopy(entries, 0, _entries, 0, entries.length);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * All entries. This field cannot be <code>null</code>.
    */
   private final Entry[] _entries;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns all entries. A fresh copy of the internal array will be
    * returned.
    *
    * @return
    *    an new array with all entries, never <code>null</code>.
    */
   public Entry[] getEntries() {
      Entry[] copy = new Entry[_entries.length];
      System.arraycopy(_entries, 0, copy, 0, copy.length);
      return copy;
   }


   //-------------------------------------------------------------------------
   // Inner classes
   //-------------------------------------------------------------------------

   /**
    * Log statistics entry. Combination of a unique log entry ID and the count
    * for the log entry.
    *
    * @version $Revision$ $Date$
    * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
    */
   public final static class Entry extends Object {

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      /**
       * Constructs a new entry with the specified ID and count.
       *
       * @param id
       *    the unique identifier of the log entry, cannot be
       *    <code>null</code>.
       *
       * @param count
       *    the count for the specified log entry, must be &gt;= 0.
       *
       * @throws IllegalArgumentException
       *    if <code>id == null || count &lt; 0</code>.
       */
      public Entry(String id, int count)
      throws IllegalArgumentException {

         // Check preconditions
         MandatoryArgumentChecker.check("id", id);
         if (count < 0) {
            throw new IllegalArgumentException("count (" + count + ") < 0");
         }

         // Store information
         _id    = id;
         _count = count;
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      /**
       * The unique identifier of the log entry. Cannot be <code>null</code>.
       */
      private final String _id;

      /**
       * The number of occurrences of the log entry. Always &gt;= 0.
       */
      private final int _count;


      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------

      /**
       * Returns the unique identifier of the log entry.
       *
       * @return
       *    the ID, never <code>null</code>.
       */
      public String getID() {
         return _id;
      }

      /**
       * Returns the count for the log entry.
       *
       * @return
       *    the count, always &gt;= 0.
       */
      public int getCount() {
         return _count;
      }
   }
}
