/*
 * $Id$
 */
package org.xins.server;

import java.io.IOException;

/**
 * Base class for function implementation classes.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public abstract class Function
extends Object {

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
    * Constructs a new <code>Function</code> object.
    *
    * @param api
    *    the API to which this function belongs, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>api == null</code>.
    */
   protected Function(API api)
   throws IllegalArgumentException {

      // Check argument
      if (api == null) {
         throw new IllegalArgumentException("api == null");
      }

      _api = api;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The API implementation this function is part of.
    */
   private final API _api;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
