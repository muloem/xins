/*
 * $Id$
 */
package org.xins.util.servlet;

import javax.servlet.ServletConfig;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Utility functions for servlets.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public class ServletUtils extends Object {

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
    * Creates a new <code>ServletUtils</code>. This constructor is
    * <code>private</code>, since no instances of this class should be
    * created.
    */
   private ServletUtils() {
      // empty
   }

   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Gets the initialization settings from the specified servlet
    * configuration object.
    *
    * @param config
    *    The servlet configuration object, not <code>null</code>.
    * 
    * @return
    *    A <code>Properties</code> object containing all initialization
    *    parameters from the servlet configuration object.
    *
    * @throws IllegalArgumentException
    *    If <code>config == null</code>.
    */
   public static Properties settingsAsProperties(ServletConfig config)
   throws IllegalArgumentException {

      // Check preconditions
      if (config == null) {
         throw new IllegalArgumentException("The specified servlet configuration is null.");
      }

      Properties properties = new Properties();

      Enumeration e = config.getInitParameterNames();
      while (e.hasMoreElements()) {
         String name = (String) e.nextElement();
         Object value = config.getInitParameter(name);

         if (value instanceof String) {
            String stringValue = (String) value;
            properties.setProperty(name, stringValue);
         }
      }

      return properties;
   }
}
