/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.http;

/**
 * Abstraction of the data part of an HTTP call result.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.0.0
 */
public interface HTTPCallResultData {


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the HTTP status code.
    *
    * @return
    *    the HTTP status code.
    */
   public int getStatusCode();

   /**
    * Returns the result data as a byte array. Note that this is not a copy or
    * clone of the internal data structure, but it is a link to the actual
    * data structure itself.
    *
    * @return
    *    a byte array of the result data, never <code>null</code>.
    */
   public byte[] getData();
}
