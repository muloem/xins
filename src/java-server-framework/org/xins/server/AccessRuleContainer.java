/*
 * $Id$
 *
 * Copyright 2004 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import org.xins.common.text.ParseException;

/**
 * A collection of access rules.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 *
 * @since XINS 1.1.0
 */
public interface AccessRuleContainer {

   /**
    * Returns whether the ip address is allowed to access the functionName.
    *
    * @return
    *    <code>Boolean.TRUE</code> if the functionName is allowed, 
    *    <code>Boolean.FALSE</code> if the functionName is denied or
    *    <code>null</code> if the ip address does not match any of the rules.
    */
   Boolean isAllowed(String ip, String functionName) throws IllegalArgumentException, ParseException;
   
   /**
    * Closes this access rules.
    */
   void close();
}
