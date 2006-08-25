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
    * Checks if the specified request can possibly be handled by this calling
    * convention as a function invocation.
    *
    * <p>Implementations of this method should be optimized for performance,
    * as this method may be called for each incoming request. Also, this
    * method should not have any side-effects except possibly some caching in
    * case there is a match.
    *
    * <p>The default implementation of this method always returns
    * <code>true</code>.
    *
    * <p>If this method throws any exception, the exception is logged as an
    * ignorable exception and <code>false</code> is assumed.
    *
    * <p>This method should only be called by the XINS/Java Server Framework.
    *
    * @param httpRequest
    *    the HTTP request to investigate, never <code>null</code>.
    *
    * @return
    *    <code>true</code> if this calling convention is <em>possibly</em>
    *    able to handle this request, or <code>false</code> if it is
    *    <em>definitely</em> not able to handle this request.
    *
    * @throws Exception
    *    if analysis of the request causes an exception; in this case
    *    <code>false</code> will be assumed by the framework.
    *
    * @since XINS 1.4.0
    */
   protected boolean matches(HttpServletRequest httpRequest)
   throws Exception {
      return true;
   }

}
