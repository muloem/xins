/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client;

import java.util.List;

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
    * <p>The list of violated constraints is passed. This list will be stored
    * internally in this exception instance.
    *
    * @param request
    *    the {@link AbstractCAPICallRequest} that is considered unacceptable,
    *    never <code>null</code>.
    *
    * @param violations
    *    a list of violated constraints, cannot be <code>null</code> and
    *    should contain at least one element; all elements should be instances
    *    of class {@link Constraint}.
    *
    * @throws IllegalArgumentException
    *    if <code>request == null || violations == null</code>.
    */
   UnacceptableRequestException(AbstractCAPICallRequest request,
                                List                    violations)
   throws IllegalArgumentException {

      // TODO: Check violations better

      super("Unacceptable XINS call request.");
      // TODO: Improve exception message. Include request and detail.

      // Check preconditions
      MandatoryArgumentChecker.check("request",    request,
                                     "violations", violations);

      // Store the request and detail
      _request    = request;
      _violations = violations;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The request that is considered unacceptable. Never <code>null</code>.
    */
   private final AbstractCAPICallRequest _request;

   /**
    * The list of violated constraints. Cannot be <code>null</code>.
    */
   private final List _violations;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   // TODO: Add "List getViolatedConstraints()"
}
