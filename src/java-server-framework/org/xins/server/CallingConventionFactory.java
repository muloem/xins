/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.lang.reflect.Constructor;

import javax.servlet.ServletConfig;

import org.xins.common.collections.InvalidPropertyValueException;
import org.xins.common.collections.MissingRequiredPropertyException;
import org.xins.common.manageable.BootstrapException;
import org.xins.common.servlet.ServletConfigPropertyReader;

/**
 * Factory for <code>CallingConvention</code> instances.
 *
 * @version $Revision$
 * @author Mees Witteman (<a href="mailto:mees.witteman@nl.wanadoo.com">mees.witteman@nl.wanadoo.com</a>)
 */
class CallingConventionFactory extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Retrieves a calling convention based on a name. If the name does not
    * identify a recognized calling convention, then <code>null</code> is
    * returned.
    *
    * <p>Either an existing {@link CallingConvention} object is retrieved or a
    * new one is constructed.
    *
    * <p>Before returning the {@link CallingConvention} instance, it will be
    * bootstrapped with the properties of the servlet configuration.
    *
    * @param name
    *    the name of the calling convention to retrieve, cannot be
    *    <code>null</code>.
    *
    * @param servletConfig
    *    the servlet configuration, cannot be <code>null</code>.
    *
    * @param api
    *    the API, cannot be <code>null</code>.
    *
    * @return
    *    a bootstrapped {@link CallingConvention} that matches the specified
    *    calling convention name, or <code>null</code> if no match is found.
    *
    * @throws IllegalArgumentException
    *    if <code>name          == null
    *          || servletConfig == null
    *          || api           == null</code>.
    *
    * @throws MissingRequiredPropertyException
    *    if the created calling convention requires a bootstrap property that
    *    is missing.
    *
    * @throws InvalidPropertyValueException
    *    if the created calling convention has a bootstrap property with an
    *    incorrect value.
    *
    * @throws BootstrapException
    *    if an error occured during the bootstraping of the calling
    *    convention.
    */
   static CallingConvention create(String        name,
                                   ServletConfig servletConfig,
                                   API           api)
   throws IllegalArgumentException,
          MissingRequiredPropertyException,
          InvalidPropertyValueException,
          BootstrapException {

      // Check preconditions
      MandatoryArgumentChecker.check("name",          name,
                                     "servletConfig", servletConfig,
                                     "api",           api);

      CallingConvention created = null;

      // Old-style XINS calling convention
      if (APIServlet.OLD_STYLE_CALLING_CONVENTION.equals(name)) {
         created = new OldStyleCallingConvention();

      // XINS standard calling convention
      } else if (APIServlet.STANDARD_CALLING_CONVENTION.equals(name)) {
         created = new StandardCallingConvention();

      // XINS XML calling convention
      } else if (APIServlet.XML_CALLING_CONVENTION.equals(name)) {
         created = new XMLCallingConvention();

      // XINS XSLT calling convention
      } else if (APIServlet.XSLT_CALLING_CONVENTION.equals(name)) {
         created = new XSLTCallingConvention();

      // XINS SOAP calling convention
      } else if (APIServlet.SOAP_CALLING_CONVENTION.equals(name)) {
         created = new SOAPCallingConvention(api);

      // Custom calling convention
      } else if (name.charAt(0) != '_') {
         if (! name.equals(servletConfig.getInitParameter(
                APIServlet.API_CALLING_CONVENTION_PROPERTY))) {

            // TODO: Log
            return null;
         }
         String conventionClass = servletConfig.getInitParameter(
            APIServlet.API_CALLING_CONVENTION_CLASS_PROPERTY);
         try {

            // First try with a constructor with the API as parameter then
            // with the empty constructor
            try {
               Class[]  constructorClasses = { API.class };
               Object[] constructorArgs    = { api       };
               Constructor customConstructor = Class.forName(conventionClass).getConstructor(constructorClasses);
               created = (CustomCallingConvention) customConstructor.newInstance(constructorArgs);
            } catch (NoSuchMethodException nsmex) {
               created = (CustomCallingConvention) Class.forName(conventionClass).newInstance();
            }
         } catch (Exception ex) {

            // TODO: Log
            ex.printStackTrace();
            return null;
         }

      // Otherwise return nothing
      } else {
         return null;
      }

      // Bootstrap the calling convention
      created.bootstrap(new ServletConfigPropertyReader(servletConfig));

      return created;
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
