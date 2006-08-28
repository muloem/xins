/*
 * $Id$
 *
 * Copyright 2003-2006 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.http;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.service.CallConfig;
import org.xins.common.text.FastStringBuffer;
import org.xins.common.text.TextUtils;

/**
 * Call configuration for the HTTP service caller. The HTTP method and the
 * <em>User-Agent</em> string can be configured. By default the HTTP method is
 * <em>POST</em> and the no <em>User-Agent</em> string is set.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst.dehaan@orange-ft.com">Ernst de Haan</a>
 *
 * @since XINS 1.1.0
 */
public final class HTTPCallConfig extends CallConfig {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The number of instances of this class. Initially zero.
    */
   private static int INSTANCE_COUNT;

   /**
    * Lock object for field <code>INSTANCE_COUNT</code>.
    */
   private static Object INSTANCE_COUNT_LOCK = new Object();


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>HTTPCallConfig</code> object.
    */
   public HTTPCallConfig() {

      // First determine instance number
      synchronized (INSTANCE_COUNT_LOCK) {
         _instanceNumber = ++INSTANCE_COUNT;
      }

      // Default to the POST method
      _method = HTTPMethod.POST;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The 1-based sequence number of this instance. Since this number is
    * 1-based, the first instance of this class will have instance number 1
    * assigned to it.
    */
   private final int _instanceNumber;

   /**
    * The HTTP method to use. This field cannot be <code>null</code>.
    */
   private HTTPMethod _method;

   /**
    * The HTTP user agent. This field can be <code>null</code>.
    */
   private String _userAgent;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the HTTP method associated with this configuration.
    *
    * @return
    *    the HTTP method, never <code>null</code>.
    */
   public HTTPMethod getMethod() {
      synchronized (getLock()) {
         return _method;
      }
   }

   /**
    * Sets the HTTP method associated with this configuration.
    *
    * @param method
    *    the HTTP method to be associated with this configuration, cannot be
    *    <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>method == null</code>.
    */
   public void setMethod(HTTPMethod method)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("method", method);

      // Store the new HTTP method
      synchronized (getLock()) {
         _method = method;
      }
   }

   /**
    * Sets the user agent associated with the HTTP call.
    *
    * @param agent
    *    the HTTP user agent, or <code>null</code> if no user-agent header
    *    should be sent.
    *
    * @since XINS 1.3.0
    */
   public void setUserAgent(String agent) {
      synchronized (getLock()) {
         _userAgent = agent;
      }
   }

   /**
    * Returns the HTTP user agent associated with the HTTP call.
    *
    * @return
    *    the HTTP user agent or <code>null</code> no user agent has been
    *    specified.
    *
    * @since XINS 1.3.0
    */
   public String getUserAgent() {
      synchronized (getLock()) {
         return _userAgent;
      }
   }

   /**
    * Describes this configuration.
    *
    * @return
    *    the description of this configuration, should never be
    *    <code>null</code>, should never be empty and should never start or
    *    end with whitespace characters.
    */
   public String describe() {

      boolean    failOverAllowed;
      HTTPMethod method;
      String     userAgent;
      synchronized (getLock()) {
         failOverAllowed = isFailOverAllowed();
         method          = _method;
         userAgent       = _userAgent;
      }

      FastStringBuffer buffer = new FastStringBuffer(55);
      buffer.append("HTTP call config #");
      buffer.append(_instanceNumber);
      buffer.append(" [failOverAllowed=");
      buffer.append(failOverAllowed);
      buffer.append("; method=");
      buffer.append(TextUtils.quote(method.toString()));
      buffer.append("; userAgent=");
      buffer.append(TextUtils.quote(userAgent));
      buffer.append(']');

      return buffer.toString();
   }
}
