/*
 * $Id$
 */
package org.xins.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
import org.xins.util.http.HTTPRequester;

/**
 * Accessor for a remote XINS API.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @deprecated
 *    Deprecated since XINS 0.41. Use {@link ActualFunctionCaller} instead.
 */
public class RemoteAPI
extends ActualFunctionCaller {

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
    * Creates a <code>RemoteAPI</code> object for a XINS API at the specified
    * URL.
    *
    * @param url
    *    the URL for the API, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>url == null</code>.
    */
   public RemoteAPI(URL url)
   throws IllegalArgumentException {
      super(url);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
