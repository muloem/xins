/*
 * $Id$
 */
package org.xins.util.collections.expiry;

import java.util.Map;

/**
 * Interface for objects that can receive expiry events from an
 * <code>ExpiryFolder</code>.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public interface ExpiryListener {

   /**
    * Notification of the expiry of the specified set of objects.
    *
    * @param expired
    *    the map containing the objects that have expired, indexed by key;
    *    never <code>null</code>.
    */ 
   void expired(Map expired);
}
