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
    * the specified host name used to identify the
    * <code>ActualFunctionCaller</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>hostName == null</code>.
    */
   public NoSuchActualFunctionCallerException(String hostName)
   throws IllegalArgumentException {
      super(hostName); // TODO
      MandatoryArgumentChecker.check("hostName", hostName);
      _hostName = hostName;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The host name used to identify the <code>ActualFunctionCaller</code>
    * that could not be found. The value of this field cannot be
    * <code>null</code>.
    */
   private final String _hostName;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the host name used to identify the
    * <code>ActualFunctionCaller</code> that could not be found.
    *
    * @return
    *    the host name, cannot be <code>null</code>.
    */
   public String getHostName() {
      return _hostName;
   }
}
