/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.util.Date;

import java.text.SimpleDateFormat;
import java.text.DateFormat;

import org.xins.logdoc.AbstractLogdocSerializable;

/**
 * Logdoc-serializable for a date.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.0.0
 */
final class FormattedDate
extends AbstractLogdocSerializable {

   //----------------------------------------------------------------------------
   // Class fields
   //----------------------------------------------------------------------------

   /**
    * The format for a date, as a character string.
    */
   private static final String DATE_FORMAT = "yyyyMMdd-HHmmssSSS";

   /**
    * The date formatter.
    */
   private static final DateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT);


   //------------------------------------------------------------------------
   // Class functions
   //------------------------------------------------------------------------

   //------------------------------------------------------------------------
   // Constructors
   //------------------------------------------------------------------------

   /**
    * Constructs a new <code>FormattedDate</code> object.
    *
    * @param date
    *    the date, as a number of milliseconds since January 1, 1970.
    */
   public FormattedDate(long date) {
      _epochDate = date;
   }


   //------------------------------------------------------------------------
   // Fields
   //------------------------------------------------------------------------

   /**
    * The date, as a number of milliseconds since January 1, 1970.
    */
   private final long _epochDate;


   //------------------------------------------------------------------------
   // Methods
   //------------------------------------------------------------------------

   /**
    * Initializes this <code>AbstractLogdocSerializable</code>.
    *
    * @return
    *    the serialized form of this <code>FormattedDate</code>, never
    *    <code>null</code>.
    */
   protected String initialize() {
      return DATE_FORMATTER.format(new Date(_epochDate));
   }
}
