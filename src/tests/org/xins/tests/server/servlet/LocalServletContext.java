/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.server.servlet;

import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;


/**
 * This class is an implementation of the ServletContext that can be
 * called locally.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public class LocalServletContext implements ServletContext {

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /** Creates a new instance of LocalServletContext */
   public LocalServletContext() {
   }

   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public void removeAttribute(String str) {
      throw new UnsupportedOperationException();
   }

   public void log(String msg) {
      System.out.println(msg);
   }

   public Servlet getServlet(String str) {
      throw new UnsupportedOperationException();
   }

   public Set getResourcePaths(String str) {
      throw new UnsupportedOperationException();
   }

   public Object getAttribute(String str) {
      throw new UnsupportedOperationException();
   }

   public ServletContext getContext(String str) {
      throw new UnsupportedOperationException();
   }

   public String getInitParameter(String str) {
      throw new UnsupportedOperationException();
   }

   public String getMimeType(String str) {
      throw new UnsupportedOperationException();
   }

   public RequestDispatcher getNamedDispatcher(String str) {
      throw new UnsupportedOperationException();
   }

   public String getRealPath(String str) {
      throw new UnsupportedOperationException();
   }

   public RequestDispatcher getRequestDispatcher(String str) {
      throw new UnsupportedOperationException();
   }

   public URL getResource(String str) {
      throw new UnsupportedOperationException();
   }

   public InputStream getResourceAsStream(String str) {
      throw new UnsupportedOperationException();
   }

   public void log(Exception exception, String msg) {
      log(msg, exception);
   }

   public void log(String msg, Throwable throwable) {
      System.err.println(msg);
      throwable.printStackTrace();
   }

   public void setAttribute(String str, Object obj) {
      throw new UnsupportedOperationException();
   }

   public Enumeration getServlets() {
      throw new UnsupportedOperationException();
   }

   public Enumeration getServletNames() {
      throw new UnsupportedOperationException();
   }

   public String getServletContextName() {
      throw new UnsupportedOperationException();
   }

   public String getServerInfo() {
      throw new UnsupportedOperationException();
   }

   public Enumeration getAttributeNames() {
      throw new UnsupportedOperationException();
   }

   public Enumeration getInitParameterNames() {
      throw new UnsupportedOperationException();
   }

   public int getMajorVersion() {
      return 2;
   }

   public int getMinorVersion() {
      return 3;
   }
}
