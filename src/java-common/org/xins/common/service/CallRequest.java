/*
 * $Id$
 *
 * Copyright 2004 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.service;

import org.xins.common.Log;

/**
 * Abstraction of a request for a <code>ServiceCaller</code> call. Specific
 * service callers typically only accept a single type of request, derived
 * from this class.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.0.0
 */
public abstract class CallRequest extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Fully-qualified name of this class.
    */
   private static final String CLASSNAME = CallRequest.class.getName();


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

      // TRACE: Enter constructor
      Log.log_1000(CLASSNAME, null);

      // empty

      // TRACE: Leave constructor
      Log.log_1002(CLASSNAME, null);
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
    *    the description of this request, should never be <code>null</code>,
    *    should never be empty and should never start or end with whitespace
    *    characters.
    */
   public abstract String describe();

   /**
    * Returns a textual presentation of this object.
    *
    * <p>The implementation of this method in class {@link CallRequest}
    * returns {@link #describe()}.
    *
    * @return
    *    a textual presentation of this object, should never be
    *    <code>null</code>.
    */
   public String toString() {
      return describe();
   }
}
