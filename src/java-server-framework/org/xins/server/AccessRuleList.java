/*
 * $Id$
 */
package org.xins.server;

import java.util.List;
import org.xins.util.MandatoryArgumentChecker;
import org.xins.util.text.ParseException;

/**
 * Access rule list. This class can take a character string to produce an
 * {@link AccessRuleList} object from it.
 *
 * <h3>Examples</h3>
 *
 * <p>An example of an access rule list descriptor is:
 *
 * <blockquote><code>allow 194.134.168.213/32 *;
 * <br>deny  194.134.168.213/24 _*;
 * <br>allow 194.134.168.213/24 *;
 * <br>deny 0.0.0.0/0 *</code></blockquote>
 *
 * <p>The above access control list grants access to the IP address
 * 194.134.168.213 to access all functions. Then in the second rule it denies
 * access to all IP addresses in the range 194.134.168.0 to 194.134.168.255 to
 * all functions that start with an underscore (<code>'_'</code>). Then it
 * allows access for those IP addresses to all other functions, and finally
 * all other IP addresses are denied access to any of the functions.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public final class AccessRuleList
extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Parses the specified character string to construct a new
    * <code>AccessRuleList</code> object.
    *
    * @param descriptor
    *    the access rule list descriptor, the character string to parse,
    *    cannot be <code>null</code>.
    *
    * @return
    *    an {@link AccessRuleList} instance, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>descriptor == null</code>.
    *
    * @throws ParseException
    *    if there was a parsing error.
    */
   public static final AccessRuleList parseAccessRuleList(String descriptor)
   throws IllegalArgumentException, ParseException {

      // Check preconditions
      MandatoryArgumentChecker.check("descriptor", descriptor);

      return null; // TODO
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Creates a new <code>AccessRuleList</code> object.
    *
    * @param rules
    *    the list of rules ({@link AccessRule} objects), cannot be
    *    <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>rules == null</code>.
    */
   public AccessRuleList(List rules)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("rules", rules);

      _rules = rules;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The list of rules. Cannot be <code>null</code>.
    */
   private List _rules;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
