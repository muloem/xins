/*
 * $Id$
 */
package org.xins.server;

import java.util.Enumeration;
import java.util.Properties;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.NullEnumeration;
import org.xins.util.MandatoryArgumentChecker;

/**
 * Class that represents the XINS/Java Server Framework library.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.8
 */
public final class Library extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The logging category used by the XINS/Java Server Framework core during
    * runtime. This field is not <code>null</code>.
    */
   static final Logger RUNTIME_LOG = Logger.getLogger("org.xins.server.runtime");

   /**
    * The logging category used by the XINS/Java Server Framework core during
    * runtime for ACL-related messages. This field is not <code>null</code>.
    */
   static final Logger RUNTIME_ACL_LOG = Logger.getLogger("org.xins.server.runtime.acl");


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Returns the version of this library.
    *
    * @return
    *    the version of this library, for example <code>"%%VERSION%%"</code>,
    *    never <code>null</code>.
    */
   public static final String getVersion() {
      return "%%VERSION%%";
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>Library</code> object.
    */
   private Library() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
