/*
 * $Id$
 */
package com.mycompany.allinone.api;

import java.util.Date;

import org.xins.logdoc.LogdocSerializable;
import org.xins.logdoc.LogdocStringBuffer;

/**
 * Implementation of the <code>Logdoc</code> function.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public class LogdocImpl extends Logdoc  {

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
    * Constructs a new <code>LogdocImpl</code> instance.
    *
    * @param api
    *    the API to which this function belongs, guaranteed to be not
    *    <code>null</code>.
    */
   public LogdocImpl(APIImpl api) {
      super(api);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public final Result call(Request request) throws Throwable {
      String input = request.getInputText();
      int firstSpacePos = input.indexOf(' ');
      try {
         int number = -1;
         if (firstSpacePos != -1) {
            number = Integer.parseInt(input.substring(0, firstSpacePos));
         } else {
            number = Integer.parseInt(input);
         }
         Log.log_10000(input, number);
         boolean odd = number % 2 == 0;
         boolean thousands = number > 1000;
         long squareNumber = (long) (number * number);
         MyThread thread = new MyThread();
         thread.start();
         Log.log_10002(odd, new Boolean(thousands), squareNumber, thread, new Date());
      } catch (NumberFormatException nfe) {
         Log.log_10001(nfe);
         return new InvalidNumberResult();
      }
      return new SuccessfulResult();
   }

   /**
    * Example of an object that uses the LogdocSerializable interface
    * to write directly to the logdoc.
    */
   class MyThread extends Thread implements LogdocSerializable {

      //-------------------------------------------------------------------------
      // Constructors
      //-------------------------------------------------------------------------

      //-------------------------------------------------------------------------
      // Fields
      //-------------------------------------------------------------------------

      //-------------------------------------------------------------------------
      // Methods
      //-------------------------------------------------------------------------

      public void serialize(LogdocStringBuffer buffer) {
         buffer.append(getName());
         buffer.append(" [");
         buffer.append(getPriority());
         buffer.append(']');
      }

      public void run() {
         // do nothing it's just an example
      }
   }
}
