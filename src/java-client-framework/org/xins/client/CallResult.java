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
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.xins.util.MandatoryArgumentChecker;
import org.xins.util.collections.CollectionUtils;

/**
 * Result of a call to a XINS API function.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @deprecated
 *    Deprecated since XINS 0.146. Use {@link XINSServiceCaller.Result}
 *    instead.
 */
public final class CallResult extends XINSServiceCaller.Result {

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
    *    the data element returned by the function, or <code>null</code>; if
    *    specified then the name must be <code>"data"</code>, with no
    *    namespace.
    *
    * @throws IllegalArgumentException
    *    if <code>dataElement != null &amp;&amp;
    *             !("data".equals(dataElement.</code>{@link Element#getName() getName()}<code>) &amp;&amp;</code>
    *               {@link Namespace#NO_NAMESPACE}<code>.equals(dataElement.</code>{@link Element#getNamespace() getNamespace()}<code>));</code>
    */
   public CallResult(boolean success,
                     String  code,
                     Map     parameters,
                     Element dataElement)
   throws IllegalArgumentException {
      super(success, code, parameters, dataElement);
   }

   /**
    * Constructs a new <code>CallResult</code> object, optionally specifying
    * the <code>ActualFunctionCaller</code> that produced it.
    *
    * @param functionCaller
    *    the {@link ActualFunctionCaller} that produced this
    *    <code>CallResult</code>, if any.
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
    *    the data element returned by the function, or <code>null</code>; if
    *    specified then the name must be <code>"data"</code>, with no
    *    namespace.
    *
    * @throws IllegalArgumentException
    *    if <code>dataElement != null &amp;&amp;
    *             !("data".equals(dataElement.</code>{@link Element#getName() getName()}<code>) &amp;&amp;</code>
    *               {@link Namespace#NO_NAMESPACE}<code>.equals(dataElement.</code>{@link Element#getNamespace() getNamespace()}<code>));</code>
    */
   public CallResult(ActualFunctionCaller functionCaller,
                     boolean              success,
                     String               code,
                     Map                  parameters,
                     Element              dataElement)
   throws IllegalArgumentException {
      super(functionCaller, success, code, parameters, dataElement);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
