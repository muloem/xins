/*
 * $Id$
 */
package org.xins.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class used to check mandatory method arguments.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
public class MandatoryArgumentChecker extends Object {

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>MandatoryArgumentChecker</code>. This constructor
    * is private since this no instances of this class should be created.
    */
   private MandatoryArgumentChecker() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Class constants
   //-------------------------------------------------------------------------

   private final static String[] STRING_ARRAY = new String[0];


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   public static void check(String argumentName, Object argumentValue)
   throws IllegalArgumentException {

      if (argumentValue == null) {
         throw new MissingArgumentException(argumentName);
      }
   }

   public static void check(String argumentName, Object[] argumentValue) {
      check(argumentName, argumentValue, false);
   }

   public static void check(String   argumentName,
                            Object[] argumentValue,
                            boolean  elementsMandatory)
   throws IllegalArgumentException {

      // First check if the array itsself is null
      if (argumentValue == null) {
         throw new MissingArgumentException(argumentName);
      }

      if (elementsMandatory) {
         int count = argumentValue.length;
         List missingNames = new ArrayList(count);
         int missingCount = 0;
         for (int i=0; i<count; i++) {
            if (argumentValue[i] == null) {
               missingNames.add(argumentName + '[' + i + ']');
               missingCount++;
            }
         }

         if (missingCount > 0) {
            String[] array = (String[]) missingNames.toArray(STRING_ARRAY);
            throw new MissingArgumentException(array);
         }
      }
   }

   public static void check(String argumentName1, Object argumentValue1,
                            String argumentName2, Object argumentValue2)
   throws IllegalArgumentException {
      if (argumentValue1 == null && argumentValue2 == null) {
         throw new MissingArgumentException(new String[]{argumentName1, argumentName2});
      } else if (argumentValue1 == null) {
         throw new MissingArgumentException(argumentName1);
      } else if (argumentValue2 == null) {
         throw new MissingArgumentException(argumentName2);
      }
   }

   public static void check(String argumentName1, Object argumentValue1,
                            String argumentName2, Object argumentValue2,
                            String argumentName3, Object argumentValue3)
   throws IllegalArgumentException {
      if (argumentValue1==null && argumentValue2==null && argumentValue3==null) {
         throw new MissingArgumentException(new String[]{argumentName1, argumentName2, argumentName3});
      } else if (argumentValue1==null && argumentValue2==null) {
         throw new MissingArgumentException(new String[]{argumentName1, argumentName2});
      } else if (argumentValue1==null && argumentValue3==null) {
         throw new MissingArgumentException(new String[]{argumentName1, argumentName3});
      } else if (argumentValue2==null && argumentValue3==null) {
         throw new MissingArgumentException(new String[]{argumentName2, argumentName3});
      } else if (argumentValue1 == null) {
         throw new MissingArgumentException(argumentName1);
      } else if (argumentValue2 == null) {
         throw new MissingArgumentException(argumentName2);
      } else if (argumentValue3 == null) {
         throw new MissingArgumentException(argumentName3);
      }
   }

   public static void check(String argumentName1, Object argumentValue1,
                            String argumentName2, Object argumentValue2,
                            String argumentName3, Object argumentValue3,
                            String argumentName4, Object argumentValue4)
   throws IllegalArgumentException {
      if (argumentValue1==null && argumentValue2==null && argumentValue3==null && argumentValue4==null) {
         throw new MissingArgumentException(new String[]{argumentName1, argumentName2, argumentName3, argumentName4});
      } else if (argumentValue1==null && argumentValue2==null && argumentValue3==null) {
         throw new MissingArgumentException(new String[]{argumentName1, argumentName2, argumentName3});
      } else if (argumentValue1==null && argumentValue2==null && argumentValue4==null) {
         throw new MissingArgumentException(new String[]{argumentName1, argumentName2, argumentName4});
      } else if (argumentValue2==null && argumentValue3==null && argumentValue4==null) {
         throw new MissingArgumentException(new String[]{argumentName2, argumentName3, argumentName4});
      } else if (argumentValue1==null && argumentValue2==null) {
         throw new MissingArgumentException(new String[]{argumentName1, argumentName2});
      } else if (argumentValue1==null && argumentValue3==null) {
         throw new MissingArgumentException(new String[]{argumentName1, argumentName3});
      } else if (argumentValue1==null && argumentValue4==null) {
         throw new MissingArgumentException(new String[]{argumentName1, argumentName4});
      } else if (argumentValue2==null && argumentValue3==null) {
         throw new MissingArgumentException(new String[]{argumentName2, argumentName3});
      } else if (argumentValue2==null && argumentValue4==null) {
         throw new MissingArgumentException(new String[]{argumentName2, argumentName4});
      } else if (argumentValue3==null && argumentValue4==null) {
         throw new MissingArgumentException(new String[]{argumentName3, argumentName4});
      } else if (argumentValue1 == null) {
         throw new MissingArgumentException(argumentName1);
      } else if (argumentValue2 == null) {
         throw new MissingArgumentException(argumentName2);
      } else if (argumentValue3 == null) {
         throw new MissingArgumentException(argumentName3);
      } else if (argumentValue4 == null) {
         throw new MissingArgumentException(argumentName4);
      }
   }
}
