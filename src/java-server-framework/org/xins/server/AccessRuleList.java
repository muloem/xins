/*
 * $Id$
 */
package org.xins.server;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.xins.util.MandatoryArgumentChecker;
import org.xins.util.text.ParseException;

/**
 * Access rule list.
 *
 * <h3>Descriptor format</h3>
 *
 * <p>An access rule list <em>descriptor</em>, a character string, can be
 * converted to produce an {@link AccessRuleList} object. A valid descriptor
 * consists of a list of access rule descriptors (see class
 * {@link AccessRule}), separated by semi-colon characters (<code>';'</code>).
 * Optionally, the rules can have any amount of whitespace (space-, tab-,
 * newline- and carriage return-characters), before and after them. The last
 * descriptor cannot end with a semi-colon.
 *
 * <h3>Descriptor examples</h3>
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

      StringTokenizer tokenizer = new StringTokenizer(descriptor, ";");
      List rules = new ArrayList(tokenizer.countTokens());

      while (tokenizer.hasMoreTokens()) {
         String token = tokenizer.nextToken().trim();

         if (!"".equals(token)) {
            AccessRule rule = AccessRule.parseAccessRule(token);
            if (rule != null) {
               rules.add(rule);
            }
         }
      }

      return new AccessRuleList(rules);
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

   /**
    * Counts the number of rules in this list.
    *
    * @return
    *    the number of rules, always &gt;= 0.
    */
   public int getRuleCount() {
      return _rules.size();
   }

   /**
    * Determines if the specified IP address is allowed to access the
    * specified function. This method finds the first matching rule and then
    * returns the <em>allow</em> property of that rule (see
    * {@link AccessRule#isAllowRule()}). If there is no matching rule, then
    * <code>false</code> is returned.
    *
    * @param ip
    *    the IP address, cannot be <code>null</code>.
    *
    * @param functionName
    *    the name of the function, cannot be <code>null</code>.
    *
    * @return
    *    <code>true</code> if the request is allowed, <code>false</code> if
    *    the request is denied.
    *
    * @throws IllegalArgumentException
    *    if <code>ip == null || functionName == null</code>.
    */
   public boolean allow(String ip, String functionName)
   throws IllegalArgumentException {
      return false; // TODO
   }
}
