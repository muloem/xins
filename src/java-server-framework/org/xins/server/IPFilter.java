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
 * Authorization filter for IP addresses. An <code>IPFilter</code> object is
 * created and used as follows:
 *
 * <blockquote><code>IPFilter filter = IPFilter.parseFilter("10.0.0.0/24");
 * <br>if (filter.isAuthorized("10.0.0.1")) {
 * <br>&nbsp;&nbsp;&nbsp;// IP is granted access
 * <br>} else {
 * <br>&nbsp;&nbsp;&nbsp;// IP is denied access
 * <br>}</code></blockquote>
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 * @author Peter Troon (<a href="mailto:peter.troon@nl.wanadoo.com">peter.troon@nl.wanadoo.com</a>)
 */
public final class IPFilter
extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The character that delimits the 4 sections of an IP address.
    */
   private static final String IP_ADDRESS_DELIMETER = ".";

   /**
    * The character that delimits the IP address and the mask of the provided
    * filter.
    */
   private static final char IP_MASK_DELIMETER = '/';


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Creates an <code>IPFilter</code> object for the specified filter
    * expression. The expression consists of a base IP address and a bit
    * count. The bit count indicates how many bits in an IP address must match
    * the bits in the base IP address. 
    *
    * @param expression
    *    the filter expression, cannot be <code>null</code> and must match the
    *    form:
    *    <code><em>a</em>.<em>a</em>.<em>a</em>.<em>a</em>/<em>n</em></code>,
    *    where <em>a</em> is a number between 0 and 255, with no leading
    *    zeroes, and <em>n</em> is a number between <em>0</em> and
    *    <em>32</em>, no leading zeroes.
    *
    * @return the created IP Filter object.
    *
    * @throws IllegalArgumentException
    *    if <code>expression == null</code>.
    *
    * @throws ParseException
    *    if <code>expression</code> does not match the specified format.
    */
   public static final IPFilter parseFilter(String expression)
   throws IllegalArgumentException, ParseException {

      MandatoryArgumentChecker.check("expression", expression);

      boolean validFilter = isValidFilter(expression);

      if (validFilter == false) {
         throw new ParseException("The provided filter " + expression + " is invalid.");
      }

      return new IPFilter(expression);
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Creates an <code>IPFilter</code> object for the specified filter
    * expression. The expression consists of a base IP address and a bit
    * count. The bit count indicates how many bits in an IP address must match
    * the bits in the base IP address. 
    *
    * @param expression
    *    the filter expression, cannot be <code>null</code> and must match the
    *    form:
    *    <code><em>a</em>.<em>a</em>.<em>a</em>.<em>a</em>/<em>n</em></code>,
    *    where <em>a</em> is a number between 0 and 255, with no leading
    *    zeroes, and <em>n</em> is a number between <em>0</em> and
    *    <em>32</em>, no leading zeroes.
    */
   private IPFilter(String expression) {
      _expression = expression;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The expression of this filter.
    */
   private final String _expression;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the filter expression.
    *
    * @return
    *    the original filter expression, never <code>null</code>.
    */
   public final String getExpression() {
      return _expression;
   }

   /**
    * Determines if the specified IP address is authorized.
    *
    * @param ip
    *    the IP address of which must be determined if it is authorized,
    *    cannot be <code>null</code> and must match the form:
    *    <code><em>a</em>.<em>a</em>.<em>a</em>.<em>a</em>/<em>n</em></code>,
    *    where <em>a</em> is a number between 0 and 255, with no leading
    *    zeroes.
    *
    * @return
    *    <code>true</code> if the IP address is authorized to access the
    *    protected resource, otherwise <code>false</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>ip == null</code>.
    *
    * @throws ParseException
    *    if <code>ip</code> does not match the specified format.
    */
   public final boolean isAuthorized(String ip)
   throws IllegalArgumentException, ParseException {

      MandatoryArgumentChecker.check("ip", ip);

      List ipFieldList = getIPFields(ip);

      if (ipFieldList == null) {
         throw new ParseException("The provided IP " + ip + " is invalid.");
      }

      return false;
   }

   /**
    * Returns a textual representation of this filter. The implementation of
    * this method returns the filter expression passed.
    *
    * @return
    *    a textual presentation, never <code>null</code>.
    */
   public final String toString() {
      return getExpression();
   }

   /**
    * Determines whether the provided expression is a valid IP filter.
    *
    * @param
    *    expression the IP filter expression.
    *
    * @return
    *    a boolean with the value <code>true</code> when the expression
    *    is a valid IP filter, otherwise <code>false</code>.
    */
   private static boolean isValidFilter(String expression) {
      String ip = null;
      String mask = null;
      boolean validFilter = true;
      int slashPosition = expression.indexOf(IP_MASK_DELIMETER);

      if (slashPosition < 0 || slashPosition == expression.length() - 1) {
         validFilter = false;
      } else {
         ip = expression.substring(0, slashPosition);
      }

      if (validFilter == true && isValidIp(ip) == false) {
         validFilter = false;
      }

      if (validFilter == true) {
         mask = expression.substring(slashPosition + 1);
      }

      if (validFilter == true && isValidMask(mask) == false) {
         validFilter = false;
      }

      return validFilter;
   }

   /**
    * Determines whether the provided IP address is of a valid format.
    *
    * @param
    *    ip the IP address.
    *
    * @return
    *    boolean with the value <code>true</code> if the IP is valid,
    *    otherwise false.
    */
   private static boolean isValidIp(String ip) {
      List ipFieldList = getIPFields(ip);
      boolean validIP = ipFieldList == null ? false : true;
      return validIP;
   }

   /**
    * Determines whether the provided mask is of a valid format.
    *
    * @param
    *    mask the mask.
    *
    * @return
    *    boolean with the value <code>true</code> if the mask is valid,
    *    otherwise false.
    */
   private static boolean isValidMask(String mask) {
      return isAllowedValue(mask, 32);
   }

   /**
    * Determines whether the provided IP section (the part of the IP address)
    * is of a valid format, i.e. an integer between 0 and 255.
    *
    * @param
    *    ipSection the IP section.
    *
    * @return
    *    boolean with the value <code>true</code> if the IP section is
    *    valid, otherwise false.
    */
   private static boolean isValidIPSection(String ipSection) {
      return isAllowedValue(ipSection, 255);
   }

   /**
    * Determines whether the contents of the provided string are valid, i.e.
    * can be translated into an integer value that lies between zero and the
    * specified maximum allowed value.
    *
    * @param
    *    value the value to be checked.
    *
    * @param
    *    maxAllowedValue the maximum allowed integer value.
    *
    * @return
    *    boolean with the value <code>true</code> if the provided value
    *    is valid, otherwise false.
    */
   private static boolean isAllowedValue(String value, int maxAllowedValue) {
      boolean validValue = true;
      int intValue = -1;

      try {
         intValue = Integer.parseInt(value);
      }
      catch (NumberFormatException nfe) {
         validValue = false;
      }

      if (intValue < 0 || intValue > maxAllowedValue) {
         validValue = false;
      }

      return validValue;
   }

   /**
    * Creates a list with the several fields (parts separated by a dot) of 
    * the provided IP. If the provided IP is invalid <code>null</code> is
    * returned.
    *
    * @param
    *    ip the IP address.
    *
    * @return
    *    a list with the strings representing the value of each IP field
    *    or <code>null</code> if the provided IP is invalid.
    */
   private static List getIPFields(String ip) {
      StringTokenizer tokenizer = new StringTokenizer(ip, IP_ADDRESS_DELIMETER);
      String currIPSection = null;
      boolean validToken = true;
      boolean validIP = false;
      int counter = 0;
      List ipFieldList = new ArrayList(4);

      while (tokenizer.hasMoreTokens() && validToken == true) {
          currIPSection = tokenizer.nextToken();
          validToken = isValidIPSection(currIPSection);
          ipFieldList.add(currIPSection);
          counter++;
      }

      if (validToken == true && counter == 4) {
         validIP = true;
      }

      if (validIP == false) {
         ipFieldList = null;
      }

      return ipFieldList;
   }

}
