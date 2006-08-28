/*
 * $Id$
 */
package com.mycompany.allinone.api;

import org.xins.common.types.standard.Date;

/**
 * Implementation of the <code>SimpleTypes</code> function.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:anthony.goubard@orange-ft.com">Anthony Goubard</a>
 */
public class SimpleTypesImpl extends SimpleTypes  {

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
    * Constructs a new <code>SimpleTypesImpl</code> instance.
    *
    * @param api
    *    the API to which this function belongs, guaranteed to be not
    *    <code>null</code>.
    */
   public SimpleTypesImpl(APIImpl api) {
      super(api);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public final Result call(Request request) throws Throwable {

      short shortValue = -1;
      if (request.isSetInputShort()) {
         shortValue = request.getInputShort();
         shortValue += 10;
      }

      SuccessfulResult result = new SuccessfulResult();
      result.setOutputShort(shortValue);
      result.setOutputInt(request.getInputByte() * 2);
      result.setOutputLong(14L);
      result.setOutputFloat(3.5F);
      result.setOutputDouble(3.1415);
      if (request.getInputText().equals("perftests")) {
        result.setOutputText("hello");
      } else {
        result.setOutputText("Hello ~!@#$%^&*()_+<>?[]\\;',./ \u20AC\u0630&");
      }
      if (result.getOutputDate() != null) {
         throw new Exception("the outputDate parameter should be null.");
      }
      Date.Value outputDate = new Date.Value(2004,6,21);
      result.setOutputDate(outputDate);
      if (!result.getOutputDate().equals(outputDate)) {
         throw new Exception("Incorrect outputDate parameter.");
      }

      if (request.isSetInputBinary()) {
         byte[] inputBinary = request.getInputBinary();
         result.setOutputBinary(inputBinary);
      }

      return result;
   }
}
