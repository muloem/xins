/*
 * $Id$
 */
package org.xins.common.servlet;

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

   /**
    * Gets the initialization settings from the specified servlet
    * configuration object.
    *
    * @param config
    *    the servlet configuration object, not <code>null</code>.
    *
    * @return
    *    a <code>Properties</code> object containing all initialization
    *    parameters from the servlet configuration object.
    *
    * @throws IllegalArgumentException
    *    if <code>config == null</code>.
    */
   public static Properties settingsAsProperties(ServletConfig config)
   throws IllegalArgumentException {

      // Check preconditions
      if (config == null) {
         throw new IllegalArgumentException("config == null");
      }

      Properties properties = new Properties();

      Enumeration paramNames = config.getInitParameterNames();
      while (paramNames.hasMoreElements()) {
         String name = (String) paramNames.nextElement();
         Object value = config.getInitParameter(name);

         if (value instanceof String) {
            String stringValue = (String) value;
            properties.setProperty(name, stringValue);
         }
      }

      return properties;
   }


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
}
