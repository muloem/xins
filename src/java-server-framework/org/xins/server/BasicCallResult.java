/*
 * $Id$
 */
package org.xins.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import org.xins.util.MandatoryArgumentChecker;
import org.xins.util.collections.PropertyReader;
import org.xins.util.collections.ProtectedPropertyReader;

/**
 * Basic implementation of a <code>CallResult</code>.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.119
 */
final class BasicCallResult extends Object implements CallResult {

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
    * Constructs a new <code>BasicCallResult</code> object.
    *
    * @param success
    *    the success indication.
    *
    * @param code
    *    the result code, can be <code>null</code>.
    *
    * @param parameters
    *    the parameters, can be <code>null</code>.
    *
    * @param dataElement
    *    the data element, can be <code>null</code>.
    */
   BasicCallResult(boolean success, String code, PropertyReader parameters, Element dataElement) {
      _success     = success;
      _code        = code;
      _parameters  = parameters;
      _dataElement = dataElement;

      // TODO: Cloning or copying ?
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * Success indication.
    */
   private final boolean _success;

   /**
    * The result code. This field is <code>null</code> if there is no result
    * code.
    */
   private final String _code;

   /**
    * The parameters and their values. This field can be <code>null</code>.
    */
   private final PropertyReader _parameters;

   /**
    * The data element. This field is <code>null</code> if there is no data
    * element.
    */
   private final Element _dataElement;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public boolean isSuccess() {
      return _success;
   }

   public String getCode() {
      return _code;
   }

   public PropertyReader getParameters() {
      return _parameters;
   }

   public String getParameter(String name)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("name", name);

      // The set of parameters is lazily initialized, recognize this
      if (_parameters == null) {
         return null;
      }

      return _parameters.get(name);
   }

   public Element getDataElement() {
      return _dataElement;
   }
}
