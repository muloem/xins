/*
 * $Id$
 *
 * Copyright 2003-2006 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.spec;

/**
 * Thrown when the required entity cannot be found in the API.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:anthony.goubard@orange-ft.com">Anthony Goubard</a>
 *
 * @since XINS 1.3.0
 */
public class EntityNotFoundException extends Exception {

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Creates a new <code>InvalidSpecificationException</code> with the reason
    * of the problem.
    *
    * @param message
    *    the reason why this exception has been thrown, can be <code>null</code>.
    */
   EntityNotFoundException(String message) {
      super(message);
   }

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

}
