/*
 * $Id$
 */
package org.xins.server;

import java.util.List;
import org.xins.util.MandatoryArgumentChecker;
import org.xins.util.text.ParseException;

/**
 * Access rule parser. Takes a character string and produces a list of
 * {@link AccessRule} objects from it.
 *
 * <h3>Examples</h3>
 *
 * <p>An example of an access rule list is:
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
public final class AccessRuleParser
extends Object {

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
    * Constructs a new <code>AccessRuleParser</code> for the specified API.
    *
    * @param api
    *    the API, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>api == null</code>.
    */
   public AccessRuleParser(API api)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("api", api);

      _api = api;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The API. Cannot be <code>null</code>.
    */
   private API _api;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Parses the specified character string to construct a list of access
    * rules.
    *
    * @param s
    *    the character string to parse, cannot be <code>null</code>.
    *
    * @return
    *    the {@link List} of parsed {@link AccessRule} objects, never
    *    <code>null</code>, although the list may be empty.
    *
    * @throws IllegalArgumentException
    *    if <code>s == null</code>.
    *
    * @throws ParseException
    *    if there was a parsing error.
    */
   public List parseRuleList(String s)
   throws IllegalArgumentException, ParseException {

      // Check preconditions
      MandatoryArgumentChecker.check("s", s);

      return null; // TODO
   }
}
