/*
 * $Id$
 */
package org.xins.client;

import java.io.ByteArrayInputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xins.util.MandatoryArgumentChecker;

/**
 * Abstract base class for <code>FunctionCaller</code> implementations.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.41
 */
public abstract class AbstractFunctionCaller
extends Object
implements FunctionCaller {

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
    * Creates a new <code>AbstractFunctionCaller</code>.
    */
   protected AbstractFunctionCaller() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public XINSServiceCaller.Result call(String functionName)
   throws CallIOException, InvalidCallResultException {
      return call(null, functionName, null);
   }

   public XINSServiceCaller.Result call(String functionName, Map parameters)
   throws IllegalArgumentException,
          CallIOException,
          InvalidCallResultException {
      return call(null, functionName, parameters);
   }

   public XINSServiceCaller.Result call(CallRequest request)
   throws IllegalArgumentException,
          CallIOException,
          InvalidCallResultException {
      MandatoryArgumentChecker.check("request", request);
      return call(request.getSessionID(),
                  request.getFunctionName(),
                  request.getParameters());
   }
}
