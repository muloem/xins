/*
 * $Id$
 *
 * Copyright 2004 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.server.servlet;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.xins.common.text.URLEncoding;

/**
 * This class is an implementation of the HTTPServletRequest that can be
 * called localy.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public class LocalHTTPServletRequest implements HttpServletRequest {

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Creates a new Servlet request.
    *
    * @param url
    *    the request url or the list of the parameters (name=value) separated
    *    with comma's.
    */
   public LocalHTTPServletRequest(String url) {
      _date = System.currentTimeMillis();
      _attributes = new Hashtable();

      // Parse the URL
      _parameters = new Properties();
      int questionMarkPos = url.lastIndexOf('?');
      if (questionMarkPos != -1 ) {
         _queryString = url.substring(questionMarkPos);
      } else {
         _queryString = url;
      }
      StringTokenizer paramsParser = new StringTokenizer(_queryString, "&");
      while (paramsParser.hasMoreTokens()) {
         String parameter = paramsParser.nextToken();
         int equalPos = parameter.indexOf('=');
         if (equalPos != -1 && equalPos != parameter.length()-1) {
            String paramName = URLEncoding.decode(parameter.substring(0, equalPos));
            String paramValue = URLEncoding.decode(parameter.substring(equalPos + 1));
            _parameters.setProperty(paramName, paramValue);
         }
      }
   }

   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The parameters retrieved from the URL.
    */
   private Properties _parameters;

   /**
    * The date when the request was created.
    */
   private long _date;

   /**
    * The attributes of the request.
    */
   private Hashtable _attributes;

   /**
    * The URL query string.
    */
   private String _queryString;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public HttpSession getSession(boolean param) {
      throw new UnsupportedOperationException();
   }

   public void setCharacterEncoding(String str) {
      throw new UnsupportedOperationException();
   }

   public String[] getParameterValues(String str) {
      Collection values = _parameters.values();
      return (String[]) values.toArray(new String[0]);
   }

   public String getParameter(String str) {
      return _parameters.getProperty(str);
   }

   public int getIntHeader(String str) {
      throw new UnsupportedOperationException();
   }

   public Object getAttribute(String str) {
      throw new UnsupportedOperationException();
   }

   public long getDateHeader(String str) {
      return _date;
   }

   public String getHeader(String str) {
      throw new UnsupportedOperationException();
   }

   public Enumeration getHeaders(String str) {
      throw new UnsupportedOperationException();
   }

   public String getRealPath(String str) {
      throw new UnsupportedOperationException();
   }

   public RequestDispatcher getRequestDispatcher(String str) {
      throw new UnsupportedOperationException();
   }

   public boolean isUserInRole(String str) {
      return false;
   }

   public void removeAttribute(String str) {
      _attributes.remove(str);
   }

   public void setAttribute(String str, Object obj) {
      _attributes.put(str, obj);
   }

   public String getQueryString() {
      return _queryString;
   }

   public String getProtocol() {
      return "file://";
   }

   public String getPathTranslated() {
      return "";
   }

   public String getPathInfo() {
      return "";
   }

   public Enumeration getParameterNames() {
      return _parameters.keys();
   }

   public Map getParameterMap() {
      return _parameters;
   }

   public String getMethod() {
      return "GET";
   }

   public Enumeration getLocales() {
      throw new UnsupportedOperationException();
   }

   public Locale getLocale() {
      throw new UnsupportedOperationException();
   }

   public Enumeration getAttributeNames() {
      return _attributes.keys();
   }

   public String getAuthType() {
      return "";
   }

   public String getCharacterEncoding() {
      return "UTF-8";
   }

   public int getContentLength() {
      throw new UnsupportedOperationException();
   }

   public String getContentType() {
      throw new UnsupportedOperationException();
   }

   public String getContextPath() {
      throw new UnsupportedOperationException();
   }

   public Cookie[] getCookies() {
      throw new UnsupportedOperationException();
   }

   public Enumeration getHeaderNames() {
      throw new UnsupportedOperationException();
   }

   public ServletInputStream getInputStream() {
      throw new UnsupportedOperationException();
   }

   public java.io.BufferedReader getReader() {
      throw new UnsupportedOperationException();
   }

   public String getRemoteAddr() {
      try {
         return InetAddress.getLocalHost().getHostAddress();
      } catch (UnknownHostException exception) {
         return "127.0.0.1";
      }
   }

   public String getRemoteHost() {
      try {
         return InetAddress.getLocalHost().getHostName();
      } catch (UnknownHostException exception) {
         return "localhost";
      }
   }

   public String getRemoteUser() {
      return "";
   }

   public String getRequestURI() {
      return "";
   }

   public StringBuffer getRequestURL() {
      return new StringBuffer();
   }

   public String getRequestedSessionId() {
      throw new UnsupportedOperationException();
   }

   public String getScheme() {
      return "file://";
   }

   public String getServerName() {
      try {
         return InetAddress.getLocalHost().getHostName();
      } catch (Exception ioe) {
         return "localhost";
      }
   }

   public int getServerPort() {
      return -1;
   }

   public String getServletPath() {
      return "";
   }

   public HttpSession getSession() {
      throw new UnsupportedOperationException();
   }

   public Principal getUserPrincipal() {
      throw new UnsupportedOperationException();
   }

   public boolean isRequestedSessionIdFromCookie() {
      return false;
   }

   public boolean isRequestedSessionIdFromURL() {
      return false;
   }

   public boolean isRequestedSessionIdFromUrl() {
      return false;
   }

   public boolean isRequestedSessionIdValid() {
      return false;
   }

   public boolean isSecure() {
      return true;
   }
}
