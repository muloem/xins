/*
 * $Id$
 */
package org.xins.common.service.ldap;

/**
 * LDAP authentication method.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.115
 */
public final class AuthenticationMethod
extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Constant representing the <em>none</em> authentication method.
    */
   public static final AuthenticationMethod NONE = new AuthenticationMethod("none");

   /**
    * Constant representing the <em>simple</em> authentication method.
    */
   public static final AuthenticationMethod SIMPLE = new AuthenticationMethod("simple");


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>AuthenticationMethod</code> object.
    *
    * @param name
    *    the name of this authentication method, for example
    *    <code>"none"</code> or <code>"simple"</code>.
    */
   private AuthenticationMethod(String name) {
      _name = name;
   }

   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The name of this authentication method. Cannot be <code>null</code>.
    */
   private final String _name;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the name of this authentication method. For example,
    * <code>"none"</code> or <code>"simple"</code>.
    *
    * @return
    *    the name of this authentication method, not <code>null</code>.
    */
   public String getName() {
      return _name;
   }

   public String toString() {
      return _name;
   }
}
