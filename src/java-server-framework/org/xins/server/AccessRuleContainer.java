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
    * Determines if the specified IP address is allowed to access the
    * specified function. If there is no matching rule, then
    * <code>null</code> is returned.
    *
    * @return
    *    <code>Boolean.TRUE</code> if the functionName is allowed, 
    *    <code>Boolean.FALSE</code> if the functionName is denied or
    *    <code>null</code> if the ip address does not match any of the rules.
    *
    * @throws IllegalArgumentException
    *    if <code>ip == null || functionName == null</code>.
    *
    * @throws ParseException
    *    if the specified IP address is malformed.
    */
   Boolean isAllowed(String ip, String functionName) throws IllegalArgumentException, ParseException;
   
   /**
    * Closes this access rules.
    */
   void close();
}
