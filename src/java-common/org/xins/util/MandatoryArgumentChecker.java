/*
 * $Id$
 */
package org.xins.util;

import java.util.ArrayList;
import java.util.List;
import org.xins.util.text.FastStringBuffer;

/**
 * Utility class used to check mandatory method arguments.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public final class MandatoryArgumentChecker extends Object {

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
         FastStringBuffer buffer = new FastStringBuffer(40);
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
    *    if <code>argumentValue1 == null || argumentValue2 == null</code>.
    */
   public static void check(String argumentName1, Object argumentValue1,
                            String argumentName2, Object argumentValue2)
   throws IllegalArgumentException {
      if (argumentValue1 == null && argumentValue2 == null) {
         FastStringBuffer buffer = new FastStringBuffer(80);
         buffer.append(argumentName1);
         buffer.append(" == null && ");
         buffer.append(argumentName2);
         buffer.append(" == null");
         throw new IllegalArgumentException(buffer.toString());
      } else if (argumentValue1 == null) {
         FastStringBuffer buffer = new FastStringBuffer(40);
         buffer.append(argumentName1);
         buffer.append(" == null");
         throw new IllegalArgumentException(buffer.toString());
      } else if (argumentValue2 == null) {
         FastStringBuffer buffer = new FastStringBuffer(40);
         buffer.append(argumentName2);
         buffer.append(" == null");
         throw new IllegalArgumentException(buffer.toString());
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
      if (argumentValue1 == null || argumentValue2 == null || argumentValue3 == null) {
	 if (argumentValue1 == null && argumentValue2 == null && argumentValue3 == null) {
	    FastStringBuffer buffer = new FastStringBuffer(120);
	    buffer.append(argumentName1);
	    buffer.append(" == null && ");
	    buffer.append(argumentName2);
	    buffer.append(" == null && ");
	    buffer.append(argumentName3);
	    buffer.append(" == null");
	    throw new IllegalArgumentException(buffer.toString());
	 } else if (argumentValue1 == null && argumentValue2 == null) {
	    FastStringBuffer buffer = new FastStringBuffer(80);
	    buffer.append(argumentName1);
	    buffer.append(" == null && ");
	    buffer.append(argumentName2);
	    buffer.append(" == null");
	    throw new IllegalArgumentException(buffer.toString());
	 } else if (argumentValue1 == null && argumentValue3 == null) {
	    FastStringBuffer buffer = new FastStringBuffer(80);
	    buffer.append(argumentName1);
	    buffer.append(" == null && ");
	    buffer.append(argumentName3);
	    buffer.append(" == null");
	    throw new IllegalArgumentException(buffer.toString());
	 } else if (argumentValue1 == null) {
	    FastStringBuffer buffer = new FastStringBuffer(40);
	    buffer.append(argumentName1);
	    buffer.append(" == null");
	    throw new IllegalArgumentException(buffer.toString());
	 } else if (argumentValue2 == null && argumentValue3 == null) {
	    FastStringBuffer buffer = new FastStringBuffer(80);
	    buffer.append(argumentName2);
	    buffer.append(" == null && ");
	    buffer.append(argumentName3);
	    buffer.append(" == null");
	    throw new IllegalArgumentException(buffer.toString());
	 } else if (argumentValue2 == null) {
	    FastStringBuffer buffer = new FastStringBuffer(40);
	    buffer.append(argumentName2);
	    buffer.append(" == null");
	    throw new IllegalArgumentException(buffer.toString());
	 } else if (argumentValue3 == null) {
	    FastStringBuffer buffer = new FastStringBuffer(40);
	    buffer.append(argumentName3);
	    buffer.append(" == null");
	    throw new IllegalArgumentException(buffer.toString());
	 }
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
      if (argumentValue1 == null || argumentValue2 == null || argumentValue3 == null || argumentValue4 == null) {
	 if (argumentValue1 == null && argumentValue2 == null && argumentValue3 == null && argumentValue4 == null) {
	    FastStringBuffer buffer = new FastStringBuffer(160);
	    buffer.append(argumentName1);
	    buffer.append(" == null && ");
	    buffer.append(argumentName2);
	    buffer.append(" == null && ");
	    buffer.append(argumentName3);
	    buffer.append(" == null && ");
	    buffer.append(argumentName4);
	    buffer.append(" == null");
	    throw new IllegalArgumentException(buffer.toString());
	 } else if (argumentValue1 == null && argumentValue2 == null && argumentValue3 == null) {
	    FastStringBuffer buffer = new FastStringBuffer(120);
	    buffer.append(argumentName1);
	    buffer.append(" == null && ");
	    buffer.append(argumentName2);
	    buffer.append(" == null && ");
	    buffer.append(argumentName3);
	    buffer.append(" == null");
	    throw new IllegalArgumentException(buffer.toString());
	 } else if (argumentValue1 == null && argumentValue2 == null && argumentValue4 == null) {
	    FastStringBuffer buffer = new FastStringBuffer(120);
	    buffer.append(argumentName1);
	    buffer.append(" == null && ");
	    buffer.append(argumentName2);
	    buffer.append(" == null && ");
	    buffer.append(argumentName4);
	    buffer.append(" == null");
	    throw new IllegalArgumentException(buffer.toString());
	 } else if (argumentValue1 == null && argumentValue2 == null) {
	    FastStringBuffer buffer = new FastStringBuffer(80);
	    buffer.append(argumentName1);
	    buffer.append(" == null && ");
	    buffer.append(argumentName2);
	    buffer.append(" == null");
	    throw new IllegalArgumentException(buffer.toString());
	 } else if (argumentValue1 == null && argumentValue3 == null && argumentValue4 == null) {
	    FastStringBuffer buffer = new FastStringBuffer(120);
	    buffer.append(argumentName1);
	    buffer.append(" == null && ");
	    buffer.append(argumentName3);
	    buffer.append(" == null && ");
	    buffer.append(argumentName4);
	    buffer.append(" == null");
	    throw new IllegalArgumentException(buffer.toString());
	 } else if (argumentValue1 == null && argumentValue3 == null) {
	    FastStringBuffer buffer = new FastStringBuffer(80);
	    buffer.append(argumentName1);
	    buffer.append(" == null && ");
	    buffer.append(argumentName3);
	    buffer.append(" == null");
	    throw new IllegalArgumentException(buffer.toString());
	 } else if (argumentValue1 == null && argumentValue4 == null) {
	    FastStringBuffer buffer = new FastStringBuffer(80);
	    buffer.append(argumentName1);
	    buffer.append(" == null && ");
	    buffer.append(argumentName4);
	    buffer.append(" == null");
	    throw new IllegalArgumentException(buffer.toString());
	 } else if (argumentValue1 == null) {
	    FastStringBuffer buffer = new FastStringBuffer(40);
	    buffer.append(argumentName1);
	    buffer.append(" == null");
	    throw new IllegalArgumentException(buffer.toString());
	 } else if (argumentValue2 == null && argumentValue3 == null && argumentValue4 == null) {
	    FastStringBuffer buffer = new FastStringBuffer(120);
	    buffer.append(argumentName2);
	    buffer.append(" == null && ");
	    buffer.append(argumentName3);
	    buffer.append(" == null && ");
	    buffer.append(argumentName4);
	    buffer.append(" == null");
	    throw new IllegalArgumentException(buffer.toString());
	 } else if (argumentValue2 == null && argumentValue3 == null) {
	    FastStringBuffer buffer = new FastStringBuffer(80);
	    buffer.append(argumentName2);
	    buffer.append(" == null && ");
	    buffer.append(argumentName3);
	    buffer.append(" == null");
	    throw new IllegalArgumentException(buffer.toString());
	 } else if (argumentValue2 == null && argumentValue4 == null) {
	    FastStringBuffer buffer = new FastStringBuffer(80);
	    buffer.append(argumentName2);
	    buffer.append(" == null && ");
	    buffer.append(argumentName4);
	    buffer.append(" == null");
	    throw new IllegalArgumentException(buffer.toString());
	 } else if (argumentValue2 == null) {
	    FastStringBuffer buffer = new FastStringBuffer(40);
	    buffer.append(argumentName2);
	    buffer.append(" == null");
	    throw new IllegalArgumentException(buffer.toString());
	 } else if (argumentValue3 == null && argumentValue4 == null) {
	    FastStringBuffer buffer = new FastStringBuffer(80);
	    buffer.append(argumentName3);
	    buffer.append(" == null && ");
	    buffer.append(argumentName4);
	    buffer.append(" == null");
	    throw new IllegalArgumentException(buffer.toString());
	 } else if (argumentValue3 == null) {
	    FastStringBuffer buffer = new FastStringBuffer(40);
	    buffer.append(argumentName3);
	    buffer.append(" == null");
	    throw new IllegalArgumentException(buffer.toString());
	 } else if (argumentValue4 == null) {
	    FastStringBuffer buffer = new FastStringBuffer(40);
	    buffer.append(argumentName4);
	    buffer.append(" == null");
	    throw new IllegalArgumentException(buffer.toString());
	 }
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
