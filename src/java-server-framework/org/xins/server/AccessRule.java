/*
 * $Id$
 */
package org.xins.server;

import org.apache.oro.text.regex.Perl5Pattern;
import org.xins.util.MandatoryArgumentChecker;
import org.xins.util.text.ParseException;

/**
 * Access rule.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public final class AccessRule
extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Parses the specified character string to construct a new
    * <code>AccessRule</code> object.
    *
    * @param descriptor
    *    the access rule descriptor, the character string to parse, cannot be <code>null</code>.
    *
    * @return
    *    an {@link AccessRule} instance, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>descriptor == null</code>.
    *
    * @throws ParseException
    *    if there was a parsing error.
    */
   public static final AccessRule parseAccessRule(String descriptor)
   throws IllegalArgumentException, ParseException {

      // Check preconditions
      MandatoryArgumentChecker.check("descriptor", descriptor);

      return null; // TODO
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>AccessRule</code>.
    *
    * @param allow
    *    flag that indicates if this rule grants access (<code>true</code>) or
    *    denies access (<code>false</code>).
    *
    * @param ipFilter
    *    filter used for matching (or not) IP addresses, cannot be
    *    <code>null</code>.
    *
    * @param functionNamePattern
    *    regular expression used for matching (or not) a function name; cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>filter == null || functionNamePattern == null</code>.
    */
   private AccessRule(boolean      allow,
                      IPFilter     ipFilter,
                      Perl5Pattern functionNamePattern) {

      // Check preconditions
      MandatoryArgumentChecker.check("ipFilter", ipFilter,
                                     "functionNamePattern", functionNamePattern);

      // TODO
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
