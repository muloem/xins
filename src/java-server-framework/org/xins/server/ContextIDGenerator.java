/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.collections.PropertyReader;
import org.xins.common.manageable.Manageable;
import org.xins.common.net.IPAddressUtils;
import org.xins.common.text.FastStringBuffer;
import org.xins.common.text.HexConverter;
import org.xins.common.text.TextUtils;

/**
 * Generator for diagnostic context identifiers. Generated context
 * identifiers will be in the format:
 *
 * <blockquote><em>app</em>@<em>host</em>:<em>time</em>:<em>rnd</em></blockquote>
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
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
final class ContextIDGenerator extends Manageable {

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
    * Constructs a new <code>ContextIDGenerator</code>.
    *
    * @param engine
    *    the {@link Engine} that owns this <code>ContextIDGenerator</code>,
    *    cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>engine == null</code>.
    */
   ContextIDGenerator(Engine engine)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("engine", engine);

      // Initialize the fields
      _engine   = engine;
      _format   = new SimpleDateFormat("yyMMdd-HHmmssSSS");
      _random   = new Random();
      _hostname = IPAddressUtils.getLocalHost();;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The <code>Engine</code> that owns this <code>ContextIDGenerator</code>.
    * Never <code>null</code>.
    */
   private final Engine _engine;

   /**
    * The date formatter used for generating the context identifier. Never
    * <code>null</code>.
    */
   private final SimpleDateFormat _format;

   /**
    * Pseudo-random number generator. Used for the last 5 hex digits in the
    * generated context identifier. Never <code>null</code>.
    */
   private final Random _random;

   /**
    * The name for the local host. Never <code>null</code>.
    */
   private String _hostname;

   /**
    * The name for the API. Never <code>null</code>.
    */
   private String _apiName;

   /**
    * The fixed prefix for generated context identifiers.
    */
   private String _prefix;

   /**
    * The length of a generated diagnostic context identifier.
    */
   private int _length;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   protected void initImpl(PropertyReader properties) {

      // Determine if the hostname has changed
      String hostname = properties.get(APIServlet.HOSTNAME_PROPERTY);
      if (!(TextUtils.isEmpty(hostname) || hostname.equals(_hostname))) {
         Log.log_3310(_hostname, hostname);
         _hostname = hostname;
      }

      // Get API name
      _apiName = _engine.getAPIName();

      // Determine prefix and total context ID length
      _prefix = _apiName + '@' + _hostname + ':';
      _length = _prefix.length() + 22;
   }

   /**
    * Generates a diagnostic context identifier.
    *
    * @return
    *    the generated diagnostic context identifier, never <code>null</code>.
    *
    * @throws IllegalStateException
    *    if this object is currently not usable, i.e. in the
    *    {@link #USABLE} state.
    */
   public String generate() throws IllegalStateException {

      // Check preconditions
      assertUsable();

      // TODO: Synchronize before calling nextLong() ?

      // Construct a new string buffer with the exact needed capacity
      FastStringBuffer buffer = new FastStringBuffer(_length + 3, _prefix);

      // Append the time stamp
      buffer.append(_format.format(new Date()));
      buffer.append(':');

      // Append 5 random hex digits
      HexConverter.toHexString(buffer, _random.nextInt());
		buffer.crop(_length);

      // Log and return the context ID
      String contextID = buffer.toString();
      return contextID;
   }
}
