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
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
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

   /**
    * An empty string array.
    */
   private static final String[] STRING_ARRAY = new String[0];


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Checks that the specified argument is not <code>null</code>.
    *
    * @param argumentName
    *    the name of the argument that cannot be <code>null</code>.
    *
    * @param argumentValue
    *    the value of the argument that cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>argumentValue == null</code>.
    */
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

   /**
    * Checks that the specified two arguments are not <code>null</code>.
    *
    * @param argumentName1
    *    the name of the first argument that cannot be <code>null</code>.
    *
    * @param argumentValue1
    *    the value of the first argument that cannot be <code>null</code>.
    *
    * @param argumentName2
    *    the name of the second argument that cannot be <code>null</code>.
    *
    * @param argumentValue2
    *    the value of the second argument that cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>argumentValue1 == null || argumentValue2 == null</code>.
    */
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

   /**
    * Checks that the specified three arguments are not <code>null</code>.
    *
    * @param argumentName1
    *    the name of the first argument that cannot be <code>null</code>.
    *
    * @param argumentValue1
    *    the value of the first argument that cannot be <code>null</code>.
    *
    * @param argumentName2
    *    the name of the second argument that cannot be <code>null</code>.
    *
    * @param argumentValue2
    *    the value of the second argument that cannot be <code>null</code>.
    *
    * @param argumentName3
    *    the name of the third argument that cannot be <code>null</code>.
    *
    * @param argumentValue3
    *    the value of the third argument that cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>argumentValue1 == null
    *           || argumentValue2 == null
    *           || argumentValue3 == null</code>.
    */
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

   /**
    * Checks that the specified four arguments are not <code>null</code>.
    *
    * @param argumentName1
    *    the name of the first argument that cannot be <code>null</code>.
    *
    * @param argumentValue1
    *    the value of the first argument that cannot be <code>null</code>.
    *
    * @param argumentName2
    *    the name of the second argument that cannot be <code>null</code>.
    *
    * @param argumentValue2
    *    the value of the second argument that cannot be <code>null</code>.
    *
    * @param argumentName3
    *    the name of the third argument that cannot be <code>null</code>.
    *
    * @param argumentValue3
    *    the value of the third argument that cannot be <code>null</code>.
    *
    * @param argumentName4
    *    the name of the fourth argument that cannot be <code>null</code>.
    *
    * @param argumentValue4
    *    the value of the fourth argument that cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>argumentValue1 == null
    *           || argumentValue2 == null
    *           || argumentValue3 == null
    *           || argumentValue4 == null</code>.
    */
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
