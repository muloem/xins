/*
 * $Id$
 */
package org.xins.client;

import org.xins.util.MandatoryArgumentChecker;

/**
 * Base class for client-side calling interface classes.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.131
 */
public abstract class AbstractCAPI
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
    * Creates a new <code>AbstractCAPI</code> object.
    *
    * @param functionCaller
    *    the function caller, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>functionCaller == null</code>.
    */
   protected AbstractCAPI(FunctionCaller functionCaller)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("functionCaller", functionCaller);

      // Set fields
      _functionCaller = functionCaller;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The function caller to use. This field cannot be <code>null</code>.
    */
   private FunctionCaller _functionCaller;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the function caller to use.
    *
    * @return
    *    the function caller to use, never <code>null</code>.
    */
   protected FunctionCaller getFunctionCaller() {
      return _functionCaller;
   }

   /**
    * Pings the API. This is done by calling the <strong>_NoOp</strong>
    * function. If it returns an unsuccessful result, then this is ignored.
    *
    * @throws CallIOException
    *    if the call failed due to an I/O error.
    *
    * @throws InvalidCallResultException
    *    if the call result was not a valid XINS result document.
    *
    * @since XINS 0.137
    */
   public void ping()
   throws CallIOException, InvalidCallResultException {
      _functionCaller.call("_NoOp");
   }

   /**
    * Returns the remote XINS version.
    *
    * @return
    *    the remote XINS version, or <code>null</code> if it could not be
    *    retrieved.
    *
    * @throws CallIOException
    *    if the call failed due to an I/O error.
    *
    * @throws InvalidCallResultException
    *    if the call result was not a valid XINS result document.
    *
    * @throws UnsuccessfulCallException
    *    if the call was unsuccessful.
    *
    * @since XINS 0.137
    */
   public String getRemoteXINSVersion()
   throws CallIOException,
          InvalidCallResultException,
          UnsuccessfulCallException {

      // Call the function
      CallResult result = _functionCaller.call("_GetVersion");

      // The call must be successful
      if (!result.isSuccess()) {
         throw new UnsuccessfulCallException(result);
      }

      // Return the 'xins.version' parameter value
      String version = result.getParameter("xins.version");
      if (version == null || version.trim().length() < 1) {
         return null;
      } else {
         return version;
      }
   }
}
