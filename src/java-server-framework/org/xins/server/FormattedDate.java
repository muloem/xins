/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import org.xins.common.text.DateConverter;
import org.xins.logdoc.AbstractLogdocSerializable;

/**
 * Logdoc-serializable for a date.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 * @deprecated since XINS 2.0, use DateConverter.format()
 */
final class FormattedDate
extends AbstractLogdocSerializable {

   /**
    * Shared <code>DateConverter</code> instance. Used by all
    * <code>FormattedDate</code> instances. Needs to be locked first.
    */
   private static final DateConverter DATE_CONVERTER =
      new DateConverter(true);

   /**
    * The date, as a number of milliseconds since the UNIX Epoch.
    */
   private final long _epochDate;

   /**
    * Constructs a new <code>FormattedDate</code> object.
    *
    * @param date
    *    the date, as a number of milliseconds since the
    *    <a href="http://en.wikipedia.org/wiki/Unix_Epoch">UNIX Epoch</a>.
    */
   public FormattedDate(long date) {
      _epochDate = date;
   }

   /**
    * Initializes this <code>AbstractLogdocSerializable</code>.
    *
    * @return
    *    the serialized form of this <code>FormattedDate</code>, never
    *    <code>null</code>.
    */
   protected String initialize() {
      synchronized (DATE_CONVERTER) {
         return DATE_CONVERTER.format(_epochDate);
      }
   }
}
