/*
 * $Id$
 */
package org.xins.client;

import java.util.Map;
import java.util.Collections;
import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.Namespace;
import org.xins.util.MandatoryArgumentChecker;
import org.xins.util.collections.CollectionUtils;
import org.xins.util.collections.PropertyReader;
import org.xins.util.service.CallFailedException;
import org.xins.util.service.CallResult;
import org.xins.util.service.Descriptor;
import org.xins.util.service.ServiceCaller;
import org.xins.util.service.TargetDescriptor;

/**
 * XINS service caller.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.146
 */
public final class XINSServiceCaller extends ServiceCaller {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Logger for this class.
    */
   public static final Logger LOG = Logger.getLogger(XINSServiceCaller.class.getName());


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>XINSServiceCaller</code> object.
    *
    * @param descriptor
    *    the descriptor of the service, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>descriptor == null || method == null</code>.
    */
   public XINSServiceCaller(Descriptor descriptor)
   throws IllegalArgumentException {
      super(descriptor);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   // TODO: call(...) method

   protected Object doCallImpl(TargetDescriptor target,
                               Object           subject)
   throws Throwable {

      // TODO
      return null;
   }


   //-------------------------------------------------------------------------
   // Inner classes
   //-------------------------------------------------------------------------

   /**
    * Result of a call to a XINS API function.
    *
    * @version $Revision$ $Date$
    * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
    *
    * @since XINS 0.146
    */
   public static class Result extends Object {

      // TODO: Make final as soon as CallResult class has been removed

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      /**
       * Constructs a new <code>Result</code> object.
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
      public Result(boolean success,
                    String  code,
                    Map     parameters,
                    Element dataElement)
      throws IllegalArgumentException {
         this(null, success, code, parameters, dataElement);
      }

      /**
       * Constructs a new <code>Result</code> object, optionally specifying
       * the <code>ActualFunctionCaller</code> that produced it.
       *
       * @param functionCaller
       *    the {@link ActualFunctionCaller} that produced this
       *    <code>Result</code>, if any.
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
      public Result(ActualFunctionCaller functionCaller,
                    boolean              success,
                    String               code,
                    Map                  parameters,
                    Element              dataElement)
      throws IllegalArgumentException {

         // Clone the data element if there is one
         if (dataElement != null) {
            String    dataElementName = dataElement.getName();
            Namespace ns              = dataElement.getNamespace();
            if (!"data".equals(dataElement.getName())) {
               throw new IllegalArgumentException("dataElement.getName() returned \"" + dataElementName + "\", instead of \"data\".");
            } else if (!Namespace.NO_NAMESPACE.equals(ns)) {
               throw new IllegalArgumentException("dataElement.getNamespace() returned a namespace with URI \"" + ns.getURI() + "\", instead of no namespace.");
            }
            dataElement = (Element) dataElement.clone();
         }

         _functionCaller = functionCaller;
         _success     = success;
         _code        = code;
         _parameters  = parameters == null
                      ? CollectionUtils.EMPTY_MAP
                      : Collections.unmodifiableMap(parameters);
         _dataElement = dataElement;
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      /**
       * The <code>ActualFunctionCaller</code> that produced this object. Can be
       * <code>null</code>.
       */
      private final ActualFunctionCaller _functionCaller;

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


      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------

      /**
       * Returns the <code>ActualFunctionCaller</code> associated with this call
       * result.
       *
       * @return
       *    the {@link ActualFunctionCaller} specified at construction time, or
       *    <code>null</code> if none was specified.
       */
      public ActualFunctionCaller getFunctionCaller() {
         return _functionCaller;
      }

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
       *    ({@link String} objects, cannot be <code>null</code>), the values
       *    will be the parameter values ({@link String} objects as well, cannot
       *    be <code>null</code>).
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

         // Check preconditions
         MandatoryArgumentChecker.check("name", name);

         // Short-circuit if there are no parameters at all
         if (_parameters == null) {
            return null;
         }

         // Otherwise return the parameter value
         return (String) _parameters.get(name);
      }

      /**
       * Returns the optional extra data. The data is an XML {@link Element}, or
       * <code>null</code>.
       *
       * @return
       *    the extra data as an XML {@link Element}, can be <code>null</code>;
       *    if it is not <code>null</code>, then
       *    <code><em>return</em>.{@link Element#getName() getName()}.equals("data") &amp;&amp; <em>return</em>.{@link Element#getNamespace() getNamespace()}.equals({@link Namespace#NO_NAMESPACE NO_NAMESPACE})</code>.
       */
      public Element getDataElement() {

         // If there is no data element, return null
         if (_dataElement == null) {
            return null;

         // Otherwise return a clone of the data element
         } else {
            return (Element) _dataElement.clone();
         }
      }
   }
}
