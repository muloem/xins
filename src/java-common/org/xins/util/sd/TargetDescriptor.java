/*
 * $Id$
 */
package org.xins.util.sd;

import org.xins.util.MandatoryArgumentChecker;

/**
 * Descriptor for a single target service.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.105
 */
public final class TargetDescriptor extends Descriptor {

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
    * Constructs a new <code>TargetDescriptor</code>.
    *
    * @param url
    *    the URL of the service, cannot be <code>null</code>.
    *
    * @param timeOut
    *    the time-out for the service, in milliseconds; if a negative value is
    *    passed then the service should be waited for forever.
    *
    * @throws IllegalArgumentException
    *    if <code>url == null</code>.
    */
   public TargetDescriptor(String url, long timeOut)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("url", url);

      // Set fields
      _url     = url;
      _timeOut = timeOut;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The URL for the service. Cannot be <code>null</code>.
    */
   private final String _url;

   /**
    * The time-out for the service. Is set to a negative value if the service
    * should be waited for forever.
    */
   private final long _timeOut;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Checks if this service descriptor denotes a group.
    *
    * @return
    *    <code>false</code> since this descriptor does not denote a group.
    */
   public boolean isGroup() {
      return false;
   }

   /**
    * Returns the URL for the service.
    *
    * @return
    *    the URL for the service, not <code>null</code>.
    */
   public String getURL() {
      return _url;
   }

   /**
    * Returns the time-out for the service. A negative value is returned if the service
    * should be waited for forever.
    *
    * @return
    *    the time-out for the service, or a negative value if the service
    *    should be waited for forever.
    */
   public long getTimeOut() {
      return _timeOut;
   }
}
