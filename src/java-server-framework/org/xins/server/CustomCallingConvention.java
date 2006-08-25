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
 * Base class for calling convention implementations that are not part of the 
 * core XINS framework.
 *
 * <p>Extend this class to create your own calling conventions. Make sure you
 * override {@link #matches(HttpServletRequest)}.
 *
 * <p>If your custom calling convention takes XML as input, you are advised to
 * use {@link #parseXMLRequest(HttpServletRequest)} to parse the request.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:anthony.goubard@orange-ft.com">Anthony Goubard</a>
 * @author <a href="mailto:ernst.dehaan@orange-ft.com">Ernst de Haan</a>
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
    * Returns meta information describing the characteristics of this calling 
    * convention.
    *
    * <p>This method is called during the initialization procedure for this
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
    * <p>Sublasses are encouraged to override this method to return a
    * different set of supported HTTP methods.
    *
    * <p>Example implementation:
    *
    * <blockquote><code>protected CallingConventionInfo getInfo() {
    * <br>&nbsp;&nbsp;&nbsp;CallingConventionInfo info = new CallingConventionInfo();
    * <br>&nbsp;&nbsp;&nbsp;info.addSupportedMethod("HEAD");
    * <br>&nbsp;&nbsp;&nbsp;info.addSupportedMethod("GET");
    * <br>&nbsp;&nbsp;&nbsp;info.addSupportedMethod("POST");
    * <br>&nbsp;&nbsp;&nbsp;return info;
    * <br>}</code></blockquote>
    *
    * <p>Note: As of XINS 2.0, this method may become <code>abstract</code>,
    * so that subclasses will <em>have</em> to implement it.
    *
    * @return
    *    the meta information for this calling convention, cannot be
    *    <code>null</code>.
    */
   protected CallingConventionInfo getInfo() {
      CallingConventionInfo info = new CallingConventionInfo();
      info.addSupportedMethod("HEAD");
      info.addSupportedMethod("GET");
      info.addSupportedMethod("POST");
      return info;
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
