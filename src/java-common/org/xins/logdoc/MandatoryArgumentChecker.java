/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.logdoc;

/**
 * Utility class used to check mandatory method arguments.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 * @deprecated since XINS 2.0, check directly the parameter.
 */
final class MandatoryArgumentChecker {

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
    *    if <code>argumentName == null</code> or <code>argumentValue == null</code>.
    */
   public static void check(String argumentName, Object argumentValue)
   throws IllegalArgumentException {

      if (argumentName == null) {
         check("argumentName", null);
         return;
      }

      if (argumentValue == null) {
         StringBuffer buffer = new StringBuffer(argumentName.length() + 8);
         buffer.append(argumentName);
         buffer.append(" == null");
         throw new IllegalArgumentException(buffer.toString());
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
    *    if <code>argumentName1 == null || argumentName2 == null</code>
    *    or if <code>argumentValue1 == null || argumentValue2 == null</code>.
    */
   public static void check(String argumentName1, Object argumentValue1,
                            String argumentName2, Object argumentValue2)
   throws IllegalArgumentException {

      if (argumentName1 == null || argumentName2 == null) {
         check("argumentName1", argumentName1, "argumentName2", argumentName2);
         return;
      }

      if (argumentValue1 == null || argumentValue2 == null) {
         if (argumentValue1 == null && argumentValue2 == null) {
            StringBuffer buffer = new StringBuffer(20 + argumentName1.length() + argumentName2.length());
            buffer.append(argumentName1);
            buffer.append(" == null && ");
            buffer.append(argumentName2);
            buffer.append(" == null");
            throw new IllegalArgumentException(buffer.toString());
         } else if (argumentValue1 == null) {
            StringBuffer buffer = new StringBuffer(8 + argumentName1.length());
            buffer.append(argumentName1);
            buffer.append(" == null");
            throw new IllegalArgumentException(buffer.toString());
         } else if (argumentValue2 == null) {
            StringBuffer buffer = new StringBuffer(8 + argumentName2.length());
            buffer.append(argumentName2);
            buffer.append(" == null");
            throw new IllegalArgumentException(buffer.toString());
         }
      }
   }

   /**
    * Constructs a new <code>MandatoryArgumentChecker</code>. This constructor
    * is private since this no instances of this class should be created.
    */
   private MandatoryArgumentChecker() {
      // empty
   }
}
