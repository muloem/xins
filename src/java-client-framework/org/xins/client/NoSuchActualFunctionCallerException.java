/*
 * $Id$
 */
package org.xins.client;

import org.xins.util.MandatoryArgumentChecker;

/**
 * Exception that indicates that a specified <code>ActualFunctionCaller</code>
 * was not found.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.41
 *
 * @deprecated
 *    Deprecated since XINS 0.63.
 */
public final class NoSuchActualFunctionCallerException
extends Exception {

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
    * Constructs a new <code>NoSuchActualFunctionCallerException</code> with
    * the specified URL used to identify the
    * <code>ActualFunctionCaller</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>url == null</code>.
    */
   public NoSuchActualFunctionCallerException(String url)
   throws IllegalArgumentException {
      super(url); // TODO
      MandatoryArgumentChecker.check("url", url);
      _url = url;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The URL used to identify the <code>ActualFunctionCaller</code>
    * that could not be found. The value of this field cannot be
    * <code>null</code>.
    */
   private final String _url;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the URL used to identify the <code>ActualFunctionCaller</code>
    * that could not be found.
    *
    * @return
    *    the URL, cannot be <code>null</code>.
    */
   public String getURL() {
      return _url;
   }
}
