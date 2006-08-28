/*
 * $Id$
 *
 * Copyright 2003-2006 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import org.xins.common.text.ParseException;

/**
 * Collection of one or more access rules.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:anthony.goubard@orange-ft.com">Anthony Goubard</a>
 *
 * @since XINS 1.1.0
 */
public interface AccessRuleContainer {

   /**
    * Determines if the specified IP address is allowed to access the
    * specified function.
    *
    * <p>This method finds the first matching rule and then returns the
    * <em>allow</em> property of that rule (see
    * {@link AccessRule#isAllowRule()}). If there is no matching rule, then
    * <code>null</code> is returned.
    *
    * @param ip
    *    the IP address, cannot be <code>null</code>.
    *
    * @param functionName
    *    the name of the function, cannot be <code>null</code>.
    *
    * @return
    *    {@link Boolean#TRUE} if the specified IP address is allowed to access
    *    the specified function, {@link Boolean#FALSE} if it is disallowed
    *    access or <code>null</code> if there is no match.
    *
    * @throws IllegalStateException
    *    if {@link #dispose()} has been called previously
    *    (<em>since XINS 1.3.0</em>).
    *
    * @throws IllegalArgumentException
    *    if <code>ip == null || functionName == null</code>.
    *
    * @throws ParseException
    *    if the specified IP address is malformed.
    */
   Boolean isAllowed(String ip, String functionName)
   throws IllegalArgumentException, ParseException;

   /**
    * Disposes this access rule. All claimed resources are freed as much as
    * possible.
    *
    * <p>Once disposed, the {@link #isAllowed} method should no longer be
    * called.
    *
    * @throws IllegalStateException
    *    if {@link #dispose()} has been called previously
    *    (<em>since XINS 1.3.0</em>).
    */
   void dispose();
}
