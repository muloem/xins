/*
 * $Id$
 */
package org.xins.server;

import org.apache.log4j.Logger;
import org.xins.util.MandatoryArgumentChecker;

/**
 * A result code. Result codes are either generic or API-specific. Result
 * codes do not automatically apply to all functions of an API if they have
 * been defined for that API.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
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
    * @param success
    *    the success indication.
    *
    * @param name
    *    the symbolic name, can be <code>null</code>.
    *
    * @param value
    *    the actual value of this code, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>value == null</code>.
    */
   ResultCode(boolean success, String name, String value)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("value", value);

      _log     = Logger.getLogger(getClass().getName());
      _api     = null;
      _success = success;
      _name    = name;
      _value   = value;
   }

   /**
    * Constructs a new <code>ResultCode</code> for the specified API.
    *
    * @param api
    *    the API to which this result code belongs, not <code>null</code>.
    *
    * @param success
    *    the success indication.
    *
    * @param name
    *    the symbolic name, can be <code>null</code>.
    *
    * @param value
    *    the actual value of this code, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>api == null || value == null</code>.
    */
   public ResultCode(API api, boolean success, String name, String value)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("api", api, "value", value);

      _log     = Logger.getLogger(getClass().getName());
      _api     = api;
      _success = success;
      _name    = name;
      _value   = value;

      // TODO: Remove _log ?
      // TODO: _api.resultCodeAdded(this);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The logger used by this <code>ResultCode</code> instance. This field is
    * initialized by the constructor and set to a non-<code>null</code> value.
    */
   private final Logger _log;

   /**
    * The API implementation this result code is defined within. Cannot be
    * <code>null</code>.
    */
   private final API _api;

   /**
    * The success indication.
    */
   private final boolean _success;

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
    * Returns the success indication.
    *
    * @return
    *    <code>true</code> if this result code indicates a successful call,
    *    <code>false</code> otherwise.
    */
   public final boolean getSuccess() {
      return _success;
   }

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
