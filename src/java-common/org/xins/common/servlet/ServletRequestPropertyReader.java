/*
 * $Id$
 *
 * Copyright 2004 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.servlet;

import java.util.Iterator;

import javax.servlet.ServletRequest;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.collections.EnumerationIterator;
import org.xins.common.collections.PropertyReader;

/**
 * Implementation of a <code>PropertyReader</code> that returns the
 * initialization properties from a <code>ServletRequest</code> object.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.0.0
 */
public final class ServletRequestPropertyReader
extends Object
implements PropertyReader {

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
    * Constructs a new <code>ServletRequestPropertyReader</code>.
    *
    * @param request
    *    the {@link ServletRequest} object, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>request == null</code>.
    */
   public ServletRequestPropertyReader(ServletRequest request)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("request", request);

      _request = request;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The servlet configuration object.
    */
   private final ServletRequest _request;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Retrieves the value of the property with the specified name.
    *
    * @param name
    *    the name of the property, cannot be <code>null</code>.
    *
    * @return
    *    the value of the property, possibly <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null</code>.
    */
   public String get(String name) throws IllegalArgumentException {
      MandatoryArgumentChecker.check("name", name);
      return _request.getParameter(name);
   }

   /**
    * Returns an <code>Iterator</code> that returns all property names.
    *
    * @return
    *    an {@link Iterator} for all property names, never <code>null</code>.
    */
   public Iterator getNames() {
      return new EnumerationIterator(_request.getParameterNames());
   }

   /**
    * Determines the number of properties.
    *
    * @return
    *    the size, always &gt;= 0.
    */
   public int size() {
      return _request.getParameterMap().size();
   }
}
