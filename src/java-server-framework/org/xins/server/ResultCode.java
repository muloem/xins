/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import org.xins.common.MandatoryArgumentChecker;

/**
 * Abstraction of an error code returned by a function. Result codes are
 * either generic or API-specific.
 *
 * <p>Result codes do not automatically apply to all functions of an API if
 * they have been defined for that API. Instead they are associated with each
 * individual function.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public final class ResultCode {

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
