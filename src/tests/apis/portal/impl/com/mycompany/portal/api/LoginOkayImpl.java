/*
 * $Id$
 */
package com.mycompany.portal.api;


/**
 * Implementation of the <code>LoginOkay</code> function.
 *
 * <p>Description: Log a user in.
 *
 * @version $Revision$ $Date$
 * @author TODO
 */
public final class LoginOkayImpl extends LoginOkay {

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>LoginOkayImpl</code> instance.
    *
    * @param api
    *    the API to which this function belongs, guaranteed to be not
    *    <code>null</code>.
    */
   public LoginOkayImpl(APIImpl api) {
      super(api);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Calls this function. If the function fails, it may throw any kind of
    * exception. All exceptions will be handled by the caller.
    *
    * @param request
    *    the request, never <code>null</code>.
    *
    * @return
    *    the result of the function call, should never be <code>null</code>.
    *
    * @throws Throwable
    *    if anything went wrong.
    */
   public Result call(Request request) throws Throwable {
      SuccessfulResult result = new SuccessfulResult();
      // TODO
      return result;
   }
}
