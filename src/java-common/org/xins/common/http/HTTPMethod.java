/*
 * $Id$
 *
 * Copyright 2004 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.http;

/**
 * HTTP method. Possible values for variable of this class:
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 0.207
 */
public final class HTTPMethod extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The GET method.
    */
   public static final HTTPMethod GET = new HTTPMethod("GET");

   /**
    * The POST method.
    */
   public static final HTTPMethod POST = new HTTPMethod("POST");


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>HTTPMethod</code> object with the specified name.
    *
    * @param name
    *    the name of the method, for example <code>"GET"</code> or
    *    <code>"POST"</code>; should not be <code>null</code>.
    */
   private HTTPMethod(String name) {
      _name = name;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The name of this method. For example <code>"GET"</code> or
    * <code>"POST"</code>. This field should never be <code>null</code>.
    */
   private final String _name;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns a textual representation of this object. The implementation
    * of this method returns the name of this HTTP method, like
    * <code>"GET"</code> or <code>"POST"</code>.
    *
    * @return
    *    the name of this method, e.g. <code>"GET"</code> or
    *    <code>"POST"</code>; never <code>null</code>.
    */
   public String toString() {
      return _name;
   }
}
