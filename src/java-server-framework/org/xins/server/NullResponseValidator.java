/*
 * $Id$
 */
package org.xins.server;

/**
 * Response validator that has no implementation. No checks at all are
 * performed.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.50
 */
public class NullResponseValidator
extends Object
implements ResponseValidator {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Singleton instance.
    */
   public static final NullResponseValidator SINGLETON = new NullResponseValidator();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>NullResponseValidator</code>.
    */
   protected NullResponseValidator() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public final void startResponse(boolean success, String code) {
      // empty
   }

   public final void param(String name, String value) {
      // empty
   }

   public void startTag(String name)
   throws InvalidResponseException {
      // empty
   }

   public void attribute(String name, String value)
   throws InvalidResponseException {
      // empty
   }

   public void pcdata(String text)
   throws InvalidResponseException {
      // empty
   }

   public void endTag()
   throws InvalidResponseException {
      // empty
   }

   public final void endResponse() {
      // empty
   }

   public final void cancelResponse() {
      // empty
   }
}
