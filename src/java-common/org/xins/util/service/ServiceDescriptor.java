/*
 * $Id$
 */
package org.xins.util.service;

import java.net.MalformedURLException;
import java.util.NoSuchElementException;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.xins.util.MandatoryArgumentChecker;

/**
 * Descriptor for a single target service.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.105
 *
 * @deprecated
 *    Deprecated since XINS 0.146. Use {@link TargetDescriptor} instead.
 */
public final class ServiceDescriptor extends TargetDescriptor {

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
    * Constructs a new <code>ServiceDescriptor</code>.
    *
    * @param url
    *    the URL of the service, cannot be <code>null</code>.
    *
    * @param timeOut
    *    the time-out for the service, in milliseconds; if a negative value is
    *    passed then the service should be waited for forever.
    *
    * @throws IllegalArgumentException
    *    if <code>url == null</code>.
    *
    * @throws MalformedURLException
    *    if the specified URL is malformed.
    */
   public ServiceDescriptor(String url, int timeOut)
   throws IllegalArgumentException, MalformedURLException {
      super(url, timeOut);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
