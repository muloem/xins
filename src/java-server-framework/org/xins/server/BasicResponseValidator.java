/*
 * $Id$
 */
package org.xins.server;

/**
 * Response validator that just performs some common checks. The following
 * checks are performed:
 *
 * <ul>
 *    <li>No duplicate parameter names
 *    <li>No duplicate attribute names
 * </ul>
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.50
 */
public class BasicResponseValidator
extends Object
implements ResponseValidator {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public void startResponse(boolean success, String code)
   throws InvalidResponseException {
      // empty
   }

   public void param(String name, String value)
   throws InvalidResponseException {
      // TODO: Check duplicates
   }

   public void endResponse()
   throws InvalidResponseException {
      // TODO: Cleanup parameter map, if any
      // TODO: Cleanup attribute map, if any
   }

   public void cancelResponse() {
      // TODO: Cleanup parameter map, if any
      // TODO: Cleanup attribute map, if any
   }
}
