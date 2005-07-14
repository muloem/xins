/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import org.xins.common.net.IPAddressUtils;
import org.xins.common.text.FastStringBuffer;
import org.xins.common.text.HexConverter;
import org.xins.common.text.TextUtils;

/**
 * Xins ContextID generator.
 *
 * @version $Revision$
 * @author Mees Witteman (<a href="mailto:mees.witteman@nl.wanadoo.com">mees.witteman@nl.wanadoo.com</a>)
 */
final class ContextIDGenerator {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The date formatter used for generating the context identifier.
    */
   private static final SimpleDateFormat DATE_FORMATTER =
      new SimpleDateFormat("yyMMdd-HHmmssSSS");

   /**
    * Pseudo-random number generator. Used for the automatic generation of
    * diagnostic context identifiers.
    */
   private static final Random RANDOM = new Random();

   /**
    * The hostname for localhost.
    */
   private static String _hostname = IPAddressUtils.getLocalHost();;


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Generates a diagnostic context identifier. The generated context
    * identifier will be in the format:
    *
    * <blockquote>app@host:time:rnd</blockquote>
    *
    * where:
    *
    * <ul>
    *    <li><em>app</em> is the name of the deployed application, e.g.
    *       <code>"sso"</code>;
    *
    *    <li><em>host</em> is the hostname of the computer running this
    *    engine, e.g. <code>"freddy.bravo.com"</code>;
    *
    *    <li><em>time</em> is the current date and time in the format
    *    <code>yyMMdd-HHmmssNNN</code>, e.g. <code>"050806-171522358"</code>;
    *
    *    <li><em>rnd</em> is a 5 hex-digits randomly generated number, e.g.
    *        <code>"2f4e6"</code>.
    * </ul>
    *
    * @param apiName
    *    The api to generate the context id for
    *
    * @return
    *    the generated diagnostic context identifier, never <code>null</code>.
    */
   static String generate(String apiName) {

      // TODO: Improve performance of this method

      String currentDate = DATE_FORMATTER.format(new Date());

      FastStringBuffer buffer = new FastStringBuffer(16);
      HexConverter.toHexString(buffer, RANDOM.nextLong());
      String randomFive = buffer.toString().substring(0, 5);

      int length = apiName.length() + _hostname.length() + 27;
      FastStringBuffer contextID = new FastStringBuffer(length);
      contextID.append(apiName);
      contextID.append('@');
      contextID.append(_hostname);
      contextID.append(':');
      contextID.append(currentDate);
      contextID.append(':');
      contextID.append(randomFive);

      return contextID.toString();
   }

   /**
    * Changes the hostname property if if the hostname property from the
    * passed properties object is not empty and not equal to the current
    * property value.
    *
    * @param properties
    *    Then properties to get the hostname from
    */
   static void changeHostNameIfNeeded(Properties properties) {

      String hostname = properties.getProperty(APIServlet.HOSTNAME_PROPERTY);
      if (!(TextUtils.isEmpty(hostname) || hostname.equals(_hostname))) {
         Log.log_3310(_hostname, hostname);
         _hostname = hostname;
      }
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>ContextIDGenerator</code> object.
    */
   private ContextIDGenerator() {
      // Empty
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}