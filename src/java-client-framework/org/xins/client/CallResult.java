/*
 * $Id$
 */
package org.xins.client;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import nl.wanadoo.util.CollectionUtils; // TODO
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xins.util.MandatoryArgumentChecker;

/**
 * Result of a call to a XINS API function.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public final class CallResult extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The logging category used by this class. This class field is never
    * <code>null</code>.
    */
   private final static Logger LOG = Logger.getLogger(CallResult.class.getName());


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>CallResult</code> object.
    *
    * @param success
    *    success indication returned by the function.
    *
    * @param code
    *    the return code, if any, can be <code>null</code>.
    *
    * @param parameters
    *    output parameters returned by the function, or <code>null</code>.
    *
    * @param dataElement
    *    the data element returned by the function, or <code>null</code>.
    */
   CallResult(boolean success, String code, Map parameters, Element dataElement) {

      // Clone the data element if there is one
      if (dataElement != null) {
         dataElement = (Element) dataElement.clone();
      }

      _success     = success;
      _code        = code;
      _parameters  = parameters == null ? CollectionUtils.EMPTY_MAP : Collections.unmodifiableMap(parameters);
      _dataElement = dataElement;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * Success indication.
    */
   private final boolean _success;

   /**
    * The result code. This field is <code>null</code> if no code was
    * returned.
    */
   private final String _code;

   /**
    * The parameters and their values. This field is never <code>null</code>.
    * If there are no parameters, then this field will be set to
    * {@link CollectionUtils#EMPTY_MAP}.
    */
   private final Map _parameters;

   /**
    * The data element. This field is <code>null</code> if there is no data
    * element.
    */
   private final Element _dataElement;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the success indication.
    *
    * @return
    *    success indication, <code>true</code> or <code>false</code>.
    */
   public boolean isSuccess() {
      return _success;
   }

   /**
    * Returns the result code.
    *
    * @return
    *    the result code or <code>null</code> if no code was returned.
    */
   public String getCode() {
      return _code;
   }

   /**
    * Gets all parameters.
    *
    * @return
    *    a <code>Map</code> containing all parameters, never
    *    <code>null</code>; the keys will be the names of the parameters
    *    ({@link String} objects, cannot be <code>null</code>), the values will be the parameter values
    *    ({@link String} objects as well, cannot be <code>null</code>).
    */
   public Map getParameters() {
      return _parameters;
   }

   /**
    * Gets the value of the specified parameter.
    *
    * @param name
    *    the parameter element name, not <code>null</code>.
    *
    * @return
    *    string containing the value of the parameter element,
    *    not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null</code>.
    */
   public String getParameter(String name)
   throws IllegalArgumentException {

      MandatoryArgumentChecker.check("name", name);

      if (_parameters == null) {
         return null;
      }

      return (String) _parameters.get(name);
   }

   /**
    * Returns the optional extra data. The data is an XML {@link Element}, or
    * <code>null</code>.
    *
    * @return
    *    the extra data as an XML {@link Element}, can be <code>null</code>.
    */
   public Element getDataElement() {
      if (_dataElement == null) {
         return null;
      } else {
         return (Element) _dataElement.clone();
      }
   }
}
