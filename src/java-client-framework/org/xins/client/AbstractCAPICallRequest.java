/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client;

/**
 * Base class for CAPI call request classes.
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
    * @param functionName
    *    the name of the function to call, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>functionName == null</code>.
    */
   protected AbstractCAPICallRequest(String functionName)
   throws IllegalArgumentException {
      _request = new XINSCallRequest(functionName);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The underlying XINS call request. Initialized to a non-<code>null</code>
    * value in the constructor.
    */
   protected final XINSCallRequest _request;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

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
    * Validates whether this request is considered acceptable (wrapper
    * method). If required parameters are missing or if certain parameter
    * values are out of bounds, then an exception is thrown.
    *
    * <p>This method is called when the request is executed, but it may also
    * be called in advance.
    *
    * <p>The implementation of this method is delegated to the abstract
    * {@link #validateImpl()} method, which must be implemented by concrete
    * subclasses.
    *
    * @throws UnacceptableRequestException
    *    if this request is considered unacceptable.
    */
   public final void validate()
   throws UnacceptableRequestException {

      // Call implementation method
      String s = validateImpl();

      // Throw an exception on error
      if (s != null) {
         throw new UnacceptableRequestException(this, s);
      }
   }

   /**
    * Validates whether this request is considered acceptable (implementation
    * method). If required parameters are missing or if certain parameter
    * values are out of bounds, then a description or the problem is returned.
    *
    * <p>This method is called by {@link #validate()}. It should not be called
    * from anywhere else.
    *
    * @return
    *    <code>null</code> if this request is considered acceptable or a
    *    non-<code>null</code> description if this request is considered
    *    unacceptable.
    */
   protected abstract String validateImpl();
}
