/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xins.common.collections.CollectionUtils;

import org.xins.common.types.Type;
import org.xins.common.types.TypeValueException;

/**
 * Base class for generated CAPI function request classes.
 *
 * <p>This class should not be subclassed manually. It is only intended to be
 * subclassed by classes generated by XINS.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.2.0
 */
public abstract class AbstractCAPICallRequest
extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Fully-qualified name of this class.
    */
   private static final String CLASSNAME = AbstractCAPICallRequest.class.getName();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Creates a new <code>AbstractCAPICallRequest</code> object.
    *
    * @param function
    *    representation of the function to call, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>function == null</code>.
    */
   protected AbstractCAPICallRequest(AbstractCAPIFunction function)
   throws IllegalArgumentException {
      _function          = function;
      _request           = new XINSCallRequest(function.getName());
      _constraintContext = new RequestConstraintContext();
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * Representation of the function to call. Never <code>null</code>.
    */
   private final AbstractCAPIFunction _function;

   /**
    * The underlying XINS call request. Initialized to a non-<code>null</code>
    * value in the constructor.
    */
   private final XINSCallRequest _request;

   /**
    * Mapping from parameter names to their associated values. This field is
    * lazily initialized and initially <code>null</code>.
    */
   private HashMap _parameters;

   /**
    * Constraint context. Never <code>null</code>.
    */
   private final RequestConstraintContext _constraintContext;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the wrapped <code>XINSCallRequest</code> object.
    *
    * @return
    *    the wrapped {@link XINSCallRequest} object, never <code>null</code>.
    */
   XINSCallRequest getXINSCallRequest() {
      return _request;
   }

   /**
    * Assigns the specified call configuration to this request.
    *
    * @param config
    *    the call configuration to apply when executing this request, or
    *    <code>null</code> if no specific call configuration should be
    *    associated with this request.
    */
   public void configure(XINSCallConfig config) {
      _request.setXINSCallConfig(config);
   }

   /**
    * Retrieves the call configuration currently associated with this request.
    *
    * @return
    *    the call configuration currently associated with this request, which
    *    will be applied when executing this request, or <code>null</code> if
    *    no specific call configuration is associated with this request.
    */
   public XINSCallConfig configuration() {
      return _request.getXINSCallConfig();
   }

   /**
    * Sets the specified parameter to the specified value.
    *
    * @param name
    *    the parameter name, cannot be <code>null</code>.
    *
    * @param value
    *    the parameter value, can be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null || type == null</code> or if <code>name</code>
    *    does not match the constraints for a parameter name, see
    *    {@link XINSCallRequest#PARAMETER_NAME_PATTERN_STRING} or if it equals
    *    <code>"function"</code>, which is currently still reserved.
    */
   protected final void parameter(String name, Object value)
   throws IllegalArgumentException {
      if (_parameters == null) {
         _parameters = new HashMap();
      }

      _parameters.put(name, value);
   }

   /**
    * Validates whether this request is considered acceptable. If any
    * constraints are violated, then an {@link UnacceptableRequestException}
    * is thrown.
    *
    * <p>This method is called when the request is executed, but it may also
    * be called in advance.
    *
    * @throws UnacceptableRequestException
    *    if this request is considered unacceptable.
    */
   public final void validate()
   throws UnacceptableRequestException {

      List constraints = _function.getInputConstraints();

      ArrayList violations = null;
      int constraintCount = constraints.size();
      for (int i = 0; i < constraintCount; i++) {
         Constraint constraint = (Constraint) constraints.get(i);
         if (! constraint.check(_constraintContext)) {
            if (violations == null) {
               violations = new ArrayList(constraintCount);
            }
            violations.add(constraint);
         }
      }

      // Throw an exception on error
      if (violations != null) {
         throw new UnacceptableRequestException(this, violations);
      }
   }


   //-------------------------------------------------------------------------
   // Inner classes
   //-------------------------------------------------------------------------

   /**
    * Implementation of a <code>ConstraintContext</code> based on an
    * <code>AbstractCAPICallRequest</code>.
    *
    * @version $Revision$ $Date$
    * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
    */
   private class RequestConstraintContext
   extends Object
   implements ConstraintContext {

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      /**
       * Constructs a new <code>RequestConstraintContext</code>.
       */
      private RequestConstraintContext() {
         // empty
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------

      /**
       * Retrieves the value of the specified parameter.
       *
       * @param name
       *    the name of the parameter, cannot be <code>null</code>.
       *
       * @return
       *    the value of the parameter, possibly be <code>null</code>.
       *
       * @throws IllegalArgumentException
       *    if <code>parameterName == null</code>.
       */
      public Object getParameter(String name) throws IllegalArgumentException {
         return AbstractCAPICallRequest.this._parameters.get(name);
      }
   }
}
