/*
 * $Id$
 */
package org.xins.server;

import java.util.List;
import java.util.Map;
import java.util.Collections;
import org.xins.util.collections.PropertyReader;

/**
 * A call result.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.119
 */
interface CallResult {

   /**
    * Returns the success indication.
    *
    * @return
    *    success indication, <code>true</code> or <code>false</code>.
    */
   boolean isSuccess();

   /**
    * Returns the result code.
    *
    * @return
    *    the result code or <code>null</code> if no code was returned.
    */
   String getCode();

   /**
    * Gets all parameters.
    *
    * @return
    *    a {@link PropertyReader} containing all parameters, or
    *    <code>null</code> if no parameters are set; the keys will be the
    *    names of the parameters ({@link String} objects, cannot be
    *    <code>null</code>), the values will be the parameter values
    *    ({@link String} objects as well, cannot be <code>null</code>).
    */
   PropertyReader getParameters();

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
   String getParameter(String name) throws IllegalArgumentException;

   /**
    * Returns the optional extra data. The data is an XML {@link Element}, or
    * <code>null</code>.
    *
    * @return
    *    the extra data as an XML {@link Element}, can be <code>null</code>;
    *    if it is not <code>null</code>, then
    *    <code><em>return</em>.{@link Element#getType() getType()}.equals("data")</code>.
    */
   Element getDataElement();
}
