/*
 * $Id$
 */
package com.mycompany.allinone.api;


/**
 * Implementation of the <code>RuntimeProps</code> function.
 *
 * @version $Revision$ $Date$
 * @author John Doe (<a href="mailto:john.doe@mycompany.com">john.doe@mycompany.com</a>)
 */
public class RuntimePropsImpl extends RuntimeProps {

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
    * Constructs a new <code>RuntimePropsImpl</code> instance.
    *
    * @param api
    *    the API to which this function belongs, guaranteed to be not
    *    <code>null</code>.
    */
   public RuntimePropsImpl(APIImpl api) {
      super(api);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public final Result call(Request request) throws Throwable {
      RuntimeProperties props = (RuntimeProperties) getAPI().getProperties();
      SuccessfulResult result = new SuccessfulResult();
      result.setTaxes(props.getAllinoneRate() * request.getPrice());
      if (props.getCurrency() != null) {
         result.setCurrency(props.getCurrency());
      }
      return result;
   }
}