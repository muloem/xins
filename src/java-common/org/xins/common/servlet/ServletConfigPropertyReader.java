/*
 * $Id$
 *
 * Copyright 2004 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.servlet;

import java.util.Enumeration;
import java.util.Iterator;

import javax.servlet.ServletConfig;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.collections.EnumerationIterator;
import org.xins.common.collections.PropertyReader;

/**
 * Implementation of a <code>PropertyReader</code> that returns the
 * initialization properties from a <code>ServletConfig</code> object.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
public final class ServletConfigPropertyReader
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
    * Constructs a new <code>ServletConfigPropertyReader</code>.
    *
    * @param servletConfig
    *    the {@link ServletConfig} object, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>servletConfig == null</code>.
    */
   public ServletConfigPropertyReader(ServletConfig servletConfig)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("servletConfig", servletConfig);

      _servletConfig = servletConfig;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The servlet configuration object.
    */
   private final ServletConfig _servletConfig;

   /**
    * The number of properties. This field is lazily initialized by
    * {@link #size()}.
    */
   private int _size;


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
      return _servletConfig.getInitParameter(name);
   }

   /**
    * Returns an <code>Iterator</code> that returns all property names.
    *
    * @return
    *    an {@link Iterator} for all property names, never <code>null</code>.
    */
   public Iterator getNames() {
      return new EnumerationIterator(_servletConfig.getInitParameterNames());
   }

   /**
    * Determines the number of properties.
    *
    * @return
    *    the size, always &gt;= 0.
    */
   public int size() {
      if (_size < 0) {
         int size = 0;
         Enumeration e = _servletConfig.getInitParameterNames();
         while (e.hasMoreElements()) {
            e.nextElement();
            size++;
         }
         _size = size;
      }

      return _size;
   }
}
