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
