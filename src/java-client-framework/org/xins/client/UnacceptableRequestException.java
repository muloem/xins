/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.collections.PropertyReader;

/**
 * Exception that indicates that a request for an API call is considered
 * unacceptable on the application-level. For example, a mandatory input
 * parameter may be missing.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.2.0
 */
public final class UnacceptableRequestException
extends RuntimeException {

   // TODO: Support XINSCallRequest objects?
   // TODO: Is the name UnacceptableRequestException okay?

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
    * Constructs a new <code>UnacceptableRequestException</code> using the
    * specified <code>AbstractCAPICallRequest</code>.
    *
    * @param request
    *    the {@link AbstractCAPICallRequest} that is considered unacceptable,
    *    never <code>null</code>.
    *
    * @param detail
    *    a detailed description of why the request is considered unacceptable,
    *    or <code>null</code> if such a description is not available.
    *
    * @throws IllegalArgumentException
    *    if <code>request == null</code>.
    */
   public UnacceptableRequestException(AbstractCAPICallRequest request,
                                       String                  detail)
   throws IllegalArgumentException {

      super("Unacceptable XINS call request.");
      // TODO: Improve exception message. Include request and detail.

      // Store the request and detail
      _request = request;
      _detail  = detail;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The request that is considered unacceptable. Never <code>null</code>.
    */
   private final AbstractCAPICallRequest _request;

   /**
    * The detailed description of why the request is considered unacceptable.
    * Is <code>null</code> if such a description is not available.
    */
   private final String _detail;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   // TODO: Add getter(s)
}
