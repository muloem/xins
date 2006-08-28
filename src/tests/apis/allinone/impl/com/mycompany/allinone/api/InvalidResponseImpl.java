/*
 * $Id$
 */
package com.mycompany.allinone.api;

import org.xins.server.FunctionResult;

/**
 * Implementation of the <code>InvalidResponse</code> function.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst.dehaan@orange-ft.com">Ernst de Haan</a>
 */
public class InvalidResponseImpl extends InvalidResponse {

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
    * Constructs a new <code>InvalidResponseImpl</code> instance.
    *
    * @param api
    *    the API to which this function belongs, guaranteed to be not
    *    <code>null</code>.
    */
   public InvalidResponseImpl(APIImpl api) {
      super(api);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public final Result call(Request request) throws Throwable {

      if (request.isSetErrorCode()) {
         String errorCode = request.getErrorCode();
         return new NaughtyResult(errorCode);
      }

      SuccessfulResult result = new SuccessfulResult();
      result.setPattern("bla");
      return result;
   }


   //-------------------------------------------------------------------------
   // Inner classes
   //-------------------------------------------------------------------------

   // XXX: This is a hack!
   private static final class NaughtyResult
   extends FunctionResult
   implements UnsuccessfulResult {

      private NaughtyResult(String errorCode) {
         super(errorCode);
      }
   }
}
