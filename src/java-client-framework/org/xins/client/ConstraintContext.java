/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client;

/**
 * Context for validation of constraints.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
interface ConstraintContext {

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
   Object getParameter(String name)
   throws IllegalArgumentException;
}
