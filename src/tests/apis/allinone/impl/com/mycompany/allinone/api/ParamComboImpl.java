/*
 * $Id$
 */
package com.mycompany.allinone.api;

import java.util.Calendar;
import org.xins.common.types.standard.Date;

/**
 * Implementation of the <code>ParamCombo</code> function.
 *
 * @version $Revision$ $Date$
 * @author John Doe (<a href="mailto:john.doe@mycompany.com">john.doe@mycompany.com</a>)
 */
public class ParamComboImpl extends ParamCombo  {

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
    * Constructs a new <code>ParamComboImpl</code> instance.
    *
    * @param api
    *    the API to which this function belongs, guaranteed to be not
    *    <code>null</code>.
    */
   public ParamComboImpl(APIImpl api) {
      super(api);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public final Result call(Request request) throws Throwable {
      int age;
      if (request.isSetAge()) {
         age = request.getAge();

         SuccessfulResult result = new SuccessfulResult();
         Calendar calendar = Calendar.getInstance();
         result.setRegistrationYear(calendar.get(Calendar.YEAR) - age + 1);
         result.setRegistrationMonth(calendar.get(Calendar.MONTH));
         return result;
      } else {
         int year;
         int month;
         int day;
         if (request.isSetBirthDate()) {
            year = request.getBirthDate().getYear();
            month = request.getBirthDate().getMonthOfYear();
            day = request.getBirthDate().getDayOfMonth();
         } else {
            year = request.getBirthYear();
            month = request.getBirthMonth();
            day = request.getBirthDay();
         }

         // Create an invalid response
         // This is only for demonstration purpose as no API should normally
         // return an invalid response.
         if (year > 2005) {
            return new SuccessfulResult();
         }

         SuccessfulResult result = new SuccessfulResult();
         result.setRegistrationDate(new Date.Value(year + 1, month, 1));
         return result;
      }
   }
}
