/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.collections.expiry;

import java.util.Map;

/**
 * Interface for objects that can receive expiry events from an
 * <code>ExpiryFolder</code>.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.0.0
 */
public interface ExpiryListener {

   /**
    * Notification of the expiry of the specified set of objects.
    *
    * @param folder
    *    the folder that has expired the entries , never <code>null</code>.
    *
    * @param expired
    *    the map containing the objects that have expired, indexed by key;
    *    never <code>null</code>.
    */
   void expired(ExpiryFolder folder, Map expired);
}
