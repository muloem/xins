/*
 * $Id$
 */
package org.xins.common.servlet;

import java.util.Iterator;

import javax.servlet.ServletRequest;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.collections.EnumerationIterator;
import org.xins.common.collections.AbstractPropertyReader;
import org.xins.common.collections.PropertyReader;

import org.xins.logdoc.LogdocStringBuffer;

/**
 * Implementation of a <code>PropertyReader</code> that returns the
 * initialization properties from a <code>ServletRequest</code> object.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
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

   public String get(String name) throws IllegalArgumentException {
      MandatoryArgumentChecker.check("name", name);
      return _request.getParameter(name);
   }

   public Iterator getNames() {
      return new EnumerationIterator(_request.getParameterNames());
   }

   public void serialize(LogdocStringBuffer buffer)
   throws NullPointerException {
      AbstractPropertyReader.serialize(this, buffer);
   }
}
