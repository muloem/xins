/*
 * $Id$
 */
package org.xins.common.service;

/**
 * Abstraction of a request for a <code>ServiceCaller</code> call.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 0.207
 */
public abstract class CallRequest extends Object {

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
    * Constructs a new <code>CallRequest</code>. This constructor is only
    * available to subclasses, since this class is <code>abstract</code>.
    */
   protected CallRequest() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Describes this request. The description should be trimmed and should fit
    * in a sentence. Good examples include <code>"LDAP request #1592"</code>
    * and <code>"request #12903"</code>.
    *
    * @return
    *    the description of this request, should never be <code>null</code>
    *    and should never be empty and should never start or end with
    *    whitespace characters.
    */
   public abstract String describe();

   /**
    * Returns a textual presentation of this object.
    *
    * <p>The implementation of this method returns {@link #describe()}.
    *
    * @return
    *    a textual presentation of this object, should never be
    *    <code>null</code>.
    */
   public String toString() {
      return describe();
   }
}
