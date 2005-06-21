/*
 * $Id$
 */
package com.mycompany.allinone.api;

/**
 * Implementation of the <code>ParamComboNotAll</code> function.
 *
 * @version $Revision$ $Date$
 * @author John Doe (<a href="mailto:john.doe@mycompany.com">john.doe@mycompany.com</a>)
 */
public final class ParamComboNotAllImpl extends ParamComboNotAll {

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
    * Constructs a new <code>ParamComboNotAllImpl</code> instance.
    *
    * @param api
    *    the API to which this function belongs, guaranteed to be not
    *    <code>null</code>.
    */
   public ParamComboNotAllImpl(APIImpl api) {
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
    * @throws java.lang.Throwable
    *    if anything went wrong.
    */
   public Result call(Request request)
   throws java.lang.Throwable {
      SuccessfulResult result = new SuccessfulResult();
      return result;
   }
}
