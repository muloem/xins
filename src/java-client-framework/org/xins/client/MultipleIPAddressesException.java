/*
 * $Id$
 */
package org.xins.client;

import java.net.InetAddress;
import org.xins.util.text.FastStringBuffer;
import org.xins.util.MandatoryArgumentChecker;

/**
 * Exception that indicates that a host name resolved to multiple IP
 * addresses, while only one IP address is allowed.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.44
 */
public final class MultipleIPAddressesException
extends Exception {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Creates a message for an exception constructed with the specified
    * arguments.
    *
    * @param hostName
    *    the host name, cannot be <code>null</code>.
    *
    * @param addresses
    *    the array of IP addresses, cannot be <code>null</code>, size must be
    *    at least 2, cannot contain <code>null</code> values.
    *
    * @return
    *    the constructed message, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>hostName == null
    *          || addresses == null
    *          || addresses.length &lt; 2
    *          || addresses[<em>n</em>] == null</code>
    *    (where <code>0 &lt;= <em>n</em> &lt; addresses.length</code>).
    *
    * @since XINS 0.104
    */
   private static final String createMessage(String hostName, InetAddress[] addresses)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("hostName", hostName, "addresses", addresses);

      FastStringBuffer buffer = new FastStringBuffer(255);
      buffer.append("The host name \"");
      buffer.append(hostName);
      buffer.append("\" resolved to multiple IP addresses: ");

      // Append all but last 2 IP addresses
      InetAddress a;
      int i;
      for (i = 0; i < (addresses.length - 2); i++) {
         a = addresses[i];
         if (a == null) {
            throw new IllegalArgumentException("addresses[" + i + "] == null");
         }
         buffer.append(a.getHostAddress());
         buffer.append(", ");
      }

      // Append one before last
      a = addresses[i++];
      if (a == null) {
         throw new IllegalArgumentException("addresses[" + i + "] == null");
      }
      buffer.append(a.getHostAddress());

      // Append last
      buffer.append(" and ");
      a = addresses[i++];
      if (a == null) {
         throw new IllegalArgumentException("addresses[" + i + "] == null");
      }
      buffer.append(a.getHostAddress());
      buffer.append('.');

      return buffer.toString();
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>MultipleIPAddressesException</code>.
    *
    * @param hostName
    *    the host name, cannot be <code>null</code>.
    *
    * @param addresses
    *    the array of IP addresses, cannot be <code>null</code>, size must be
    *    at least 2, cannot contain <code>null</code> values.
    *
    * @throws IllegalArgumentException
    *    if <code>hostName == null
    *          || addresses == null
    *          || addresses.length &lt; 2
    *          || addresses[<em>n</em>] == null</code>
    *    (where <code>0 &lt;= <em>n</em> &lt; addresses.length</code>).
    */
   public MultipleIPAddressesException(String hostName, InetAddress[] addresses)
   throws IllegalArgumentException {

      // Call superclass constructor
      super(createMessage(hostName, addresses));

      // Store host name
      _hostName = hostName;

      // Store IP addresses
      _addresses = new InetAddress[addresses.length];
      System.arraycopy(addresses, 0, _addresses, 0, addresses.length);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * Host name. This field cannot be <code>null</code>.
    */
   private final String _hostName;

   /**
    * The addresses for the host name.
    */
   private final InetAddress[] _addresses;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Gets the host name.
    *
    * @return
    *    the host name, cannot be <code>null</code>.
    *
    * @since XINS 0.104
    */
   public String getHostName() {
      return _hostName;
   }

   /**
    * Gets the IP addresses.
    *
    * @return
    *    a new copy of the array of IP addresses, never <code>null</code>.
    *
    * @since XINS 0.104
    */
   public InetAddress[] getAddresses() {
      InetAddress[] array = new InetAddress[_addresses.length];
      System.arraycopy(_addresses, 0, array, 0, _addresses.length);
      return array;
   }
}
