/*
 * $Id$
 *
 * Copyright 2003-2006 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import org.xins.common.Utils;

import org.xins.common.collections.PropertyReader;
import org.xins.common.text.FastStringBuffer;
import org.xins.common.text.URLEncoding;

import org.xins.logdoc.AbstractLogdocSerializable;

/**
 * Logdoc-serializable for parameters.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.0.0
 */
final class FormattedParameters
extends AbstractLogdocSerializable {

   //----------------------------------------------------------------------------
   // Class fields
   //----------------------------------------------------------------------------

   //------------------------------------------------------------------------
   // Class functions
   //------------------------------------------------------------------------

   //------------------------------------------------------------------------
   // Constructors
   //------------------------------------------------------------------------

   /**
    * Constructs a new <code>FormattedParameters</code> object.
    *
    * @param parameters
    *    the parameters, can be <code>null</code>.
    */
   public FormattedParameters(PropertyReader parameters) {
      _parameters = parameters;
   }


   //------------------------------------------------------------------------
   // Fields
   //------------------------------------------------------------------------

   /**
    * The parameters to serialize. This field can be <code>null</code>.
    */
   private final PropertyReader _parameters;


   //------------------------------------------------------------------------
   // Methods
   //------------------------------------------------------------------------

   /**
    * Initializes this <code>AbstractLogdocSerializable</code>.
    *
    * @return
    *    the serialized form of this <code>FormattedParameters</code>, never
    *    <code>null</code>.
    */
   protected String initialize() {

      Iterator names = (_parameters == null) ? null : _parameters.getNames();

      // If there are no parameters, then just return a hyphen
      if (names == null || ! names.hasNext()) {
         return "-";
      }

      FastStringBuffer buffer = new FastStringBuffer(80 + _parameters.size() * 40);

      boolean first = true;
      do {

         // Get the name and value
         String name  = (String) names.next();
         String value = _parameters.get(name);

         // If the value is null or an empty string, then output nothing
         if (value == null || value.length() == 0) {
            continue;
         }

         // Append an ampersand, except for the first entry
         if (!first) {
            buffer.append('&');
         } else {
            first = false;
         }

         // Append the key and the value, separated by an equals sign
         try {
            buffer.append(URLEncoder.encode(name, "UTF-8"));
            buffer.append('=');
            buffer.append(URLEncoder.encode(value, "UTF-8"));
         } catch (UnsupportedEncodingException uee) {
            throw Utils.logProgrammingError(uee);
         }
      } while (names.hasNext());

      return buffer.toString();
   }
}
