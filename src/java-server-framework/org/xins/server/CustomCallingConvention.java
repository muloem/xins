/*
 * $Id$
 *
 * Copyright 2003-2006 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Abstraction of a custom calling convention.
 *
 * <p>Extend this class to create your own calling conventions. Make sure you
 * override {@link #matches(HttpServletRequest)}.
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
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>CustomCallingConvention</code>.
    */
   public CustomCallingConvention() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Determines which HTTP methods are supported by this calling convention.
    * This method is called during the initialization procedure for this
    * <code>CallingConvention</code>, after the
    * {@link #initImpl(org.xins.common.collections.PropertyReader)} method is
    * called.
    *
    * <p>The implementation of this method in class
    * <code>CustomCallingConvention</code> indicates the following HTTP
    * methods are supported:
    *
    * <ul>
    *    <li><em>HEAD</em>
    *    <li><em>GET</em>
    *    <li><em>POST</em>
    * </ul>
    *
    * <p>Sublasses may override this method to return a different set of
    * supported HTTP methods.
    *
    * @return
    *    the HTTP methods supported, in a <code>String</code> array, never
    *    <code>null</code>.
    */
   protected final String[] supportedMethods() {
      return new String[] { "HEAD", "GET", "POST" };
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
