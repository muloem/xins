/*
 * $Id$
 */
package com.mycompany.allinone.api;

import java.util.GregorianCalendar;

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
         GregorianCalendar calender = new GregorianCalendar(year, month, day);
         long birth = calender.getTimeInMillis();
         long now = System.currentTimeMillis();
         age = (int) ((now - birth) / 3600000 / 24 / 365);
      }

      SuccessfulResult result = new SuccessfulResult();
      result.setOutputMessage("You are " + age +" years old.");
      return result;
   }
}
