/*
 * $Id$
 */
package org.xins.common.servlet;

import java.util.Iterator;

import javax.servlet.ServletConfig;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.collections.EnumerationIterator;
import org.xins.common.collections.AbstractPropertyReader;
import org.xins.common.collections.PropertyReader;

import org.xins.logdoc.LogdocStringBuffer;

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


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public String get(String name) throws IllegalArgumentException {
      MandatoryArgumentChecker.check("name", name);
      return _servletConfig.getInitParameter(name);
   }

   public Iterator getNames() {
      return new EnumerationIterator(_servletConfig.getInitParameterNames());
   }

   public void serialize(LogdocStringBuffer buffer)
   throws NullPointerException {
      AbstractPropertyReader.serialize(this, buffer);
   }
}
