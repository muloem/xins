/*
 * $Id$
 */
package org.xins.server;

import org.apache.oro.text.regex.Perl5Pattern;
import org.xins.util.MandatoryArgumentChecker;
import org.xins.util.text.ParseException;
import org.xins.util.text.SimplePatternParser;

/**
 * Access rule. This class can take a character string to produce an
 * {@link AccessRule} object from it.
 *
 * <h3>Descriptor format</h3>
 *
 * <p>A descriptor must comply to the following format:
 * <ul>
 *    <li>start with either <code>"allow"</code> or <code>"deny"</code>;
 *    <li>followed by any number of white space characters;
 *    <li>followed by a valid IP address;
 *    <li>followed by a slash character (<code>'/'</code>);
 *    <li>followed by a mask between 0 and 32 in decimal format, no leading zeroes;
 *    <li>followed by any number of white space characters;
 *    <li>followed by a simple pattern, see class {@link SimplePatternParser}.
 * </ul>
 *
 * <h3>Descriptor examples</h3>
 *
 * <p>Example of access rule descriptors:
 *
 * <dl>
 *    <dt><code>"allow&nbsp;194.134.168.213/32 *"</code></dt>
 *    <dd>Allows 194.134.168.213 to access any function.</dd>
 *
 *    <dt><code>"deny&nbsp;&nbsp;194.134.168.213/24\t_*"</code></dt>
 *    <dd>Denies all 194.134.168.x IP addresses to access any function
 *        starting with an underscore (<code>'_'</code>).</dd>
 * </dl>
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

   /**
    * Returns if this rule is an <em>allow</em> or a <em>deny</em> rule.
    *
    * @return
    *    <code>true</code> if this is an <em>allow</em> rule, or
    *    <code>false</code> if this is a <em>deny</em> rule.
    */
   public boolean isAllowRule() {
      return false; // TODO
   }

   /**
    * Returns the IP filter.
    *
    * @return
    *    the IP filter, cannot be <code>null</code>.
    */
   public IPFilter getIPFilter() {
      return null; // TODO
   }

   /**
    * Determines if the specified IP address and function match this rule.
    *
    * @param ip
    *    the IP address to match, cannot be <code>null</code>.
    *
    * @param functionName
    *    the name of the function to match, cannot be <code>null</code>.
    *
    * @return
    *    <code>true</code> if this rule matches, <code>false</code> otherwise.
    *
    * @throws IllegalArgumentException
    *    if <code>ip == null || functionName == null</code>.
    */
   public boolean match(String ip, String functionName)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("ip", ip, "functionName", functionName);

      return false; // TODO
   }
}
