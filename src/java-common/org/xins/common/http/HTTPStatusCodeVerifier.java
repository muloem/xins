/*
 * $Id$
 *
 * Copyright 2003-2006 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.http;

/**
 * Abstraction of an HTTP status code verifier.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst.dehaan@orange-ft.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public interface HTTPStatusCodeVerifier {

   /**
    * Checks if the specified HTTP status code is considered acceptable or
    * unacceptable.
    *
    * @param code
    *    the HTTP status code to check.
    *
    * @return
    *    <code>true</code> if the specified HTTP status code is considered
    *    acceptable, <code>false</code> otherwise.
    */
   boolean isAcceptable(int code);
}
