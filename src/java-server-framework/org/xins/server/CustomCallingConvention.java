/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xins.common.Utils;

/**
 * Abstraction of a custom calling convention.
 *
 * <p>Extend this class to create your own calling conventions.
 *
 * <p>If your custom calling convention takes XML as input, you are advised to
 * use {@link #parseXMLRequest(HttpServletRequest)} to parse the request.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan.goubard@nl.wanadoo.com</a>)
 */
public abstract class CustomCallingConvention extends CallingConvention {

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
    * Constructs a new <code>CustomCallingConvention</code>. It is not
    * considered to be deprecated.
    */
   public CustomCallingConvention() {
      this(false);
   }

   /**
    * Constructs a new <code>CustomCallingConvention</code>, indicating
    * whether it should be considered deprecated.
    *
    * @param deprecated
    *    <code>true</code> if this calling convention is to be considered
    *    deprecated, or <code>false</code> if not.
    *
    * @since XINS 1.4.0
    */
   protected CustomCallingConvention(boolean deprecated) {
      super(deprecated);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Checks if the specified request can be handled by this calling
    * convention. This method delegates to
    * {@link #matches(HttpServletRequest)}.
    *
    * <p>The return value is as follows:
    *
    * <ul>
    *    <li>a positive value indicates that the request <em>can</em>
    *        be handled;
    *    <li>the value <code>0</code> indicates that the request
    *        <em>cannot</em> be handled;
    *    <li>a negative number indicates that it is <em>unknown</em>
    *        whether the request can be handled by this calling convention.
    * </ul>
    *
    * <p>If {@link #matches(HttpServletRequest)} throws an exception, then
    * this exception is logged and ignored and a negative value is returned.
    *
    * <p>This method is guaranteed not to throw any exception.
    *
    * @param httpRequest
    *    the HTTP request to investigate, cannot be <code>null</code>.
    *
    * @return
    *    a positive value if the request can be handled; <code>0</code> if the
    *    request cannot be handled or a negative value if it is unknown.
    */
   final int matchesRequest(HttpServletRequest httpRequest) {

      // Delegate to the 'matches' method
      try {
         return matches(httpRequest);

      // Log and ignore any exception, just indicate it is unknown whether the
      // request can indeed be handled
      } catch (Throwable exception) {
         Utils.logIgnoredException(CallingConvention.class.getName(),
                                   "matchesRequest",
                                   getClass().getName(),
                                   "matches",
                                   exception);
         return -1;
      }
   }

   /**
    * Checks if the specified request can be handled by this calling
    * convention.
    *
    * <p>The return value is as follows:
    *
    * <ul>
    *    <li>a positive value indicates that the request <em>can</em>
    *        be handled;
    *    <li>the value <code>0</code> indicates that the request
    *        <em>cannot</em> be handled;
    *    <li>a negative number indicates that it is <em>unknown</em>
    *        whether the request can be handled by this calling convention.
    * </ul>
    *
    * <p>Implementations of this method should be optimized for performance.
    *
    * <p>The implementation of this method in class
    * <code>CustomCallingConvention</code> returns a negative value to
    * indicate it is unknown whether the request can be handled by this
    * calling convention.
    *
    * <p>Implementations of this method should not throw any exceptions.
    *
    * @param httpRequest
    *    the HTTP request to investigate, cannot be <code>null</code>.
    *
    * @return
    *    a positive value if the request can be handled; <code>0</code> if the
    *    request cannot be handled or a negative value if it is unknown.
    *
    * @since XINS 1.4.0
    */
   protected int matches(HttpServletRequest httpRequest) {

      // It is unknown whether the request can be handled
      return -1;
   }

   /**
    * Converts an HTTP request to a XINS request (implementation method).
    * This method should be implemented by your calling convention.
    *
    * @param httpRequest
    *    the HTTP request, will not be <code>null</code>.
    *
    * @return
    *    the XINS request object, should not be <code>null</code>.
    *
    * @throws InvalidRequestException
    *    if the request is considerd to be invalid.
    *
    * @throws FunctionNotSpecifiedException
    *    if the request does not indicate the name of the function to execute.
    */
   protected abstract FunctionRequest convertRequestImpl(HttpServletRequest httpRequest)
   throws InvalidRequestException,
          FunctionNotSpecifiedException;

   /**
    * Converts a XINS result to an HTTP response (implementation method).
    * This method should be implemented by your calling convention.
    *
    * @param xinsResult
    *    the XINS result object that should be converted to an HTTP response,
    *    will not be <code>null</code>.
    *
    * @param httpResponse
    *    the HTTP response object to configure, will not be <code>null</code>.
    *
    * @param httpRequest
    *    the HTTP request, will not be <code>null</code>.
    *
    * @throws IOException
    *    if calling any of the methods in <code>httpResponse</code> causes an
    *    I/O error.
    */
   protected abstract void convertResultImpl(FunctionResult      xinsResult,
                                             HttpServletResponse httpResponse,
                                             HttpServletRequest  httpRequest)
   throws IOException;

}
