/*
 * $Id$
 */
package com.mycompany.allinone.api;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the <code>ResultCode</code> function.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public class ResultCodeImpl extends ResultCode  {

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
    * Constructs a new <code>ResultCodeImpl</code> instance.
    *
    * @param api
    *    the API to which this function belongs, guaranteed to be not
    *    <code>null</code>.
    */
   public ResultCodeImpl(APIImpl api) {
      super(api);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   private Map paramsCount = new HashMap();

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public final Result call(Request request) throws Throwable {
      String parameter = request.getInputText();

      // Test in the local map
      if (paramsCount.containsKey(parameter)) {
         int count = ((Integer) paramsCount.get(parameter)).intValue();
         AlreadySetResult invalidResult = new AlreadySetResult();
         invalidResult.setCount(count);
         count++;
         paramsCount.put(parameter, new Integer(count));
         return invalidResult;
      }

      // Lookup in the shared map
      if (_sharedInstance.get(parameter) != null) {
         AlreadySetResult invalidResult = new AlreadySetResult();
         invalidResult.setCount(-1);
         return invalidResult;
      }

      paramsCount.put(parameter, new Integer(1));

      SuccessfulResult result = new SuccessfulResult();
      result.setOutputText(parameter + " added.");
      return result;
   }
}
