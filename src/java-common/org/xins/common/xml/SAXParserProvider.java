/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.xml;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;

import org.xins.common.Utils;

/**
 * Provider for <code>SAXParser</code> instances. This class will cache one
 * instance of a <code>SAXParser</code> per thread.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.3.0
 */
public class SAXParserProvider extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The factory for SAX parsers. This field is never <code>null</code>, it
    * is initialized by a class initializer.
    */
   private static final SAXParserFactory SAX_PARSER_FACTORY;

   /**
    * The cache of <code>SAXParser</code> instances, one per thread. This
    * field is never <code>null</code>.
    */
   private static ThreadLocal CACHE;


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Initializes this class.
    */
   static {
      SAX_PARSER_FACTORY = SAXParserFactory.newInstance();
      SAX_PARSER_FACTORY.setNamespaceAware(true);

      CACHE = new ThreadLocal();;
   }

   /**
    * Returns a <code>SAXParser</code> instance that can be used in the
    * current thread.
    *
    * @return
    *    a {@link SAXParser} instance, never <code>null</code>.
    */
   public static SAXParser get() {
      Object o = CACHE.get();

      SAXParser parser;
      if (o == null) {
         try {
            parser = SAX_PARSER_FACTORY.newSAXParser();
         } catch (Exception exception) {
            final String CLASSNAME      = SAXParserProvider.class.getName();
            final String THIS_METHOD    = "get()";
            final String SUBJECT_CLASS  = SAX_PARSER_FACTORY.getClass().getName();
            final String SUBJECT_METHOD = "newSAXParser()";
            final String DETAIL         = null;
            throw Utils.logProgrammingError(CLASSNAME,     THIS_METHOD,
                                            SUBJECT_CLASS, SUBJECT_METHOD,
                                            DETAIL,        exception);
         }

         CACHE.set(parser);
      } else {
         parser = (SAXParser) o;
      }

      return parser;
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Creates a new <code>SAXParserProvider</code>.
    */
   private SAXParserProvider() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
