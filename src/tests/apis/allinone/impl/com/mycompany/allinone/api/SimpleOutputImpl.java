/*
 * $Id$
 */
package com.mycompany.allinone.api;


/**
 * Implementation of the <code>SimpleOutput</code> function.
 *
 * @version $Revision$ $Date$
 * @author John Doe (<a href="mailto:john.doe@mycompany.com">john.doe@mycompany.com</a>)
 */
public class SimpleOutputImpl extends SimpleOutput {

   /**
    * Constructs a new <code>SimpleOutputImpl</code> instance.
    *
    * @param api
    *    the API to which this function belongs, guaranteed to be not
    *    <code>null</code>.
    */
   public SimpleOutputImpl(APIImpl api) {
      super(api);
   }

   public final Result call(Request request) throws Throwable {
      SuccessfulResult result = new SuccessfulResult();
      // TODO
      return result;
   }
}
