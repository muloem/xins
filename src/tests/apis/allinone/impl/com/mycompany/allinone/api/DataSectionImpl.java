/*
 * $Id$
 */
package com.mycompany.allinone.api;

/**
 * Implementation of the <code>DataSection</code> function.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public class DataSectionImpl extends DataSection  {

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
    * Constructs a new <code>DataSectionImpl</code> instance.
    *
    * @param api
    *    the API to which this function belongs, guaranteed to be not
    *    <code>null</code>.
    */
   public DataSectionImpl(APIImpl api) {
      super(api);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public final Result call(Request request) throws Throwable {
      SuccessfulResult result = new SuccessfulResult();

      // Always add the superuser
      User su = new User();
      su.setName("superuser");
      su.setAddress("12 Madison Avenue");
      su.pcdata("This user has the root authorisation.");
      result.addUser(su);

      if (request.isSetInputText()) {
         User user = new User();
         user.setName(request.getInputText());
         user.setAddress("Unknown");
         result.addUser(user);
      }
      return result;
   }
}
