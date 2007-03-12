/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.util.Iterator;

import org.xins.common.collections.PropertyReader;
import org.xins.common.text.URLEncoding;
import org.xins.common.xml.Element;

/**
 * Logdoc-serializable for parameters.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
class FormattedParameters {

   /**
    * Constructs a new <code>FormattedParameters</code> object.
    *
    * @param parameters
    *    the parameters, can be <code>null</code>.
    */
   public FormattedParameters(PropertyReader parameters) {
      this(parameters, null);
   }

   /**
    * Constructs a new <code>FormattedParameters</code> object.
    *
    * @param parameters
    *    the parameters, can be <code>null</code>.
    *
    * @param dataSection
    *    the data section, can be <code>null</code>.
    */
   public FormattedParameters(PropertyReader parameters, Element dataSection) {
      _parameters = parameters;
      _dataSection = dataSection;
   }

   /**
    * The parameters to serialize. This field can be <code>null</code>.
    */
   private final PropertyReader _parameters;

   /**
    * The data section.
    */
   private final Element _dataSection;

   /**
    * String representation of the parameters including the data section.
    * TODO This class should move to common to also be used by the client.
    *
    * @return
    *    the String representation of the request or "-" if the request is empty.
    */
   public String toString() {

      Iterator names = (_parameters == null) ? null : _parameters.getNames();

      // If there are no parameters, then just return a hyphen
      if ((names == null || ! names.hasNext()) && _dataSection == null) {
         return "-";
      }

      StringBuffer buffer = new StringBuffer(80 + _parameters.size() * 40);

      boolean first = true;
      while (names != null && names.hasNext()) {

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
         buffer.append(URLEncoding.encode(name));
         buffer.append('=');
         buffer.append(URLEncoding.encode(value));
      }

      if (_dataSection != null) {
         if (!first) {
            buffer.append('&');
         }
         buffer.append("_data=");
         buffer.append(URLEncoding.encode(_dataSection.toString()));
      }

      return buffer.toString();
   }
}
