/*
 * $Id$
 */
package org.xins.util.text;

import org.xins.util.MandatoryArgumentChecker;

/**
 * Simple pattern parser. A simple pattern is a text string that may contain
 * the wildcard characters <code>'*'</code> (asterisk) and <code>'?'</code>
 * (question mark).
 *
 * <p>The location of an asterisk indicates any number of characters is
 * allowed. The location of a question mark indicates exactly one character is
 * expected.
 *
 * <p>To allow matching of simple patterns, a simple pattern is first compiled
 * into a Perl 5 regular expression. Every asterisk is converted to
 * <code>".*"</code>, while every question mark is converted to
 * <code>"."</code>.
 *
 * <p>Examples of conversions from a simple pattern to a Perl 4 regular
 * expression:
 *
 * <table>
 *    <tr><th>Simple pattern</th><th>Perl 5 regex equivalent</th></tr>
 *    <tr><td>*</td>             <td>^.*$</td>                   </tr>
 *    <tr><td>?</td>             <td>^.$</td>                    </tr>
 *    <tr><td>_Get*</td>         <td>^_Get.*$</td>               </tr>
 *    <tr><td>_Get*i?n</td>      <td>^_Get.*i.n$</td>            </tr>
 *    <tr><td>*on</td>           <td>^.*on$</td>                 </tr>
 * </table>
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.152
 */
public class SimplePatternParser extends Object {

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
    * Creates a new <code>SimplePatternParser</code> object.
    */
   public SimplePatternParser() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
