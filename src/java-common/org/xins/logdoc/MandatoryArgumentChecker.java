/*
 * $Id$
 */
package org.xins.logdoc;

/**
 * Utility class used to check mandatory method arguments.
 * This class is a copy of the MandatoryArgumentChecker class in the package
 * org.xins.common. This file has been copied to make the library
 * logdoc.jar independant from the xins-commons.jar file.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
final class MandatoryArgumentChecker extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

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
         LogdocStringBuffer buffer = new LogdocStringBuffer(40);
         buffer.append(argumentName);
         buffer.append(" == null");
         throw new IllegalArgumentException(buffer.toString());
      }
   }

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>MandatoryArgumentChecker</code>. This constructor
    * is private since this no instances of this class should be created.
    */
   private MandatoryArgumentChecker() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
