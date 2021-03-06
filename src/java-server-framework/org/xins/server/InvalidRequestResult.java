/*
 * $Id$
 *
 * Copyright 2003-2008 Online Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

/**
 * Result code that indicates that an input parameter is either missing or invalid.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 1.0.0
 */
public class InvalidRequestResult extends InvalidMessageResult {

   /**
    * Constructs a new <code>InvalidRequestResult</code> object.
    */
   public InvalidRequestResult() {
      super(DefaultResultCodes._INVALID_REQUEST.getName());
   }
}
