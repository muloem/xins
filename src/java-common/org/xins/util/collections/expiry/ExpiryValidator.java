/*
 * $Id$
 */
package org.xins.util.collections.expiry;

import java.util.Map;

/**
 * Expiry validator.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public interface ExpiryValidator {

   /**
    * Validates if the specified entries can be expired. A {@link Map} should
    * be returned that contains all entries that should <em>not</em> be
    * expired yet.
    *
    * @param toBeExpired
    *    the entries to be expired, not <code>null</code>.
    * 
    * @return
    *    the entries in the <code>toBeExpired</code> that should not be
    *    expired but rather retouched, or <code>null</code>, which indicates
    *    none of the entries should be retouched and all should be expired.
    * 
    * @throws IllegalArgumentException
    *    if <code>toBeExpired == null</code>.
    */
   Map validateExpiry(Map toBeExpired)
   throws IllegalArgumentException;
}
