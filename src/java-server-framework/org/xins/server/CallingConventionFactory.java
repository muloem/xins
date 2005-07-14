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
 * XINS Calling convention factory.
 *
 * @version $Revision$
 * @author Mees Witteman (<a href="mailto:mees.witteman@nl.wanadoo.com">mees.witteman@nl.wanadoo.com</a>)
 */
class CallingConventionFactory {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Retrieves a calling convention based on a name. If the name is not
    * recognized as identifying a certain calling convention, then
    * <code>null</code> is returned.
    *
    * <p>Either an existing {@link CallingConvention} object is retrieved or a
    * new one is constructed.
    *
    * @param name
    *    the name of the calling convention to retrieve, can be
    *    <code>null</code>.
    *
    * @return
    *    a {@link CallingConvention} object that matches the specified calling
    *    convention name, or <code>null</code> if no match is found.
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
   static CallingConvention createCallingConvention(String name,
                                             ServletConfig servletConfig,
                                             API api)
   throws MissingRequiredPropertyException,
          InvalidPropertyValueException,
          BootstrapException {

      CallingConvention createdConvention = null;
      // Old-style calling convention
      if (APIServlet.OLD_STYLE_CALLING_CONVENTION.equals(name)) {
         createdConvention = new OldStyleCallingConvention();

      // Standard calling convention
      } else if (APIServlet.STANDARD_CALLING_CONVENTION.equals(name)) {
         createdConvention = new StandardCallingConvention();

      // XML calling convention
      } else if (APIServlet.XML_CALLING_CONVENTION.equals(name)) {
         createdConvention = new XMLCallingConvention();

      // XSLT calling convention
      } else if (APIServlet.XSLT_CALLING_CONVENTION.equals(name)) {
         createdConvention = new XSLTCallingConvention();

      // SOAP calling convention
      } else if (APIServlet.SOAP_CALLING_CONVENTION.equals(name)) {
         createdConvention = new SOAPCallingConvention(api);

      // Custom calling convention
      } else if (name.charAt(0) != '_') {
         if (!name.equals(servletConfig.getInitParameter(
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
               Class[]  construtorClasses = {API.class};
               Object[] constructorArgs   = {api};
               Constructor customConstructor = Class.forName(conventionClass).getConstructor(construtorClasses);
               createdConvention = (CustomCallingConvention) customConstructor.newInstance(constructorArgs);
            } catch (NoSuchMethodException nsmex) {
               createdConvention = (CustomCallingConvention) Class.forName(conventionClass).newInstance();
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
      createdConvention.bootstrap(new ServletConfigPropertyReader(servletConfig));
      return createdConvention;
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