/*
 * $Id$
 */
package org.xins.server;

import org.xins.common.MandatoryArgumentChecker;

/**
 * A result code. Result codes are either generic or API-specific. Result
 * codes do not automatically apply to all functions of an API if they have
 * been defined for that API.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
public final class ResultCode
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
    * Constructs a new generic <code>ResultCode</code>. This constructor can
    * only be called by classes in the same package.
    *
    * @param name
    *    the symbolic name, can be <code>null</code>.
    *
    * @param value
    *    the actual value of this code, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>value == null</code>.
    *
    * @since XINS 0.117.
    */
   ResultCode(String name, String value)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("value", value);

      _api     = null;
      _name    = name;
      _value   = value;
   }

   /**
    * Constructs a new <code>ResultCode</code> for the specified API.
    *
    * @param api
    *    the API to which this result code belongs, not <code>null</code>.
    *
    * @param name
    *    the symbolic name, can be <code>null</code>.
    *
    * @param value
    *    the actual value of this code, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>api == null || value == null</code>.
    *
    * @since XINS 0.117
    */
   public ResultCode(API api, String name, String value)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("api", api, "value", value);

      _api     = api;
      _name    = name;
      _value   = value;

      _api.resultCodeAdded(this);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The API implementation this result code is defined within. Cannot be
    * <code>null</code>.
    */
   private final API _api;

   /**
    * The symbolic name of this result code. Can be <code>null</code>.
    */
   private final String _name;

   /**
    * The value of this result code. This field cannot be <code>null</code>.
    */
   private final String _value;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the symbolic name of this result code.
    *
    * @return
    *    the symbolic name, can be <code>null</code>.
    */
   public final String getName() {
      return _name;
   }

   /**
    * Returns the value of this result code.
    *
    * @return
    *    the value, not <code>null</code>.
    */
   public final String getValue() {
      return _value;
   }
}
