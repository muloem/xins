/*
 * $Id$
 *
 * Copyright 2003-2006 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.servlet.container;

import java.io.BufferedReader;
import java.io.StringReader;
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
public class XINSServletRequest implements HttpServletRequest {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The localhost name.
    */
   private static String LOCALHOST_NAME;

   /**
    * The localhost address.
    */
   private static String LOCALHOST_ADDRESS;


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   static {
      try {
         LOCALHOST_ADDRESS = InetAddress.getLocalHost().getHostAddress();
      } catch (UnknownHostException exception) {
         LOCALHOST_ADDRESS = "127.0.0.1";
      }
      try {
         LOCALHOST_NAME = InetAddress.getLocalHost().getHostName();
      } catch (UnknownHostException exception) {
         LOCALHOST_NAME = "localhost";
      }
   }


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
   public XINSServletRequest(String url) {
      this(url, null, null);
   }

   /**
    * Creates a new Servlet request.
    *
    * @param url
    *    the request url or the list of the parameters (name=value) separated
    *    with comma's.
    *
    * @param data
    *    the content of the request.
    *
    * @param contentType
    *    the content type of the request.
    *
    * @since XINS 1.3.0
    */
   public XINSServletRequest(String url, char[] data, String contentType) {
      _url = url;
      _postData = data;
      _contentType = contentType;
      _date = System.currentTimeMillis();
      _attributes = new Hashtable();

      // Parse the URL
      _parameters = new Properties();
      int questionMarkPos = url.lastIndexOf('?');
      if (questionMarkPos != -1) {
         _queryString = url.substring(questionMarkPos + 1);
      } else if (questionMarkPos == url.length() - 1) {
         _queryString = "";
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
    * The requested URL included the optional parameters.
    */
   private String _url;

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

   /**
    * The content type of the query.
    */
   private String _contentType;

   /**
    * The content of the HTTP POST.
    */
   private char[] _postData;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

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
      return _attributes.get(str);
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
      int questionPos = _queryString.indexOf('?');
      if (questionPos > 0) {
         return _queryString.substring(0, questionPos);
      } else {
         return null;
      }
   }

   public String getPathInfo() {
      return getPathTranslated();
   }

   public Enumeration getParameterNames() {
      return _parameters.keys();
   }

   public Map getParameterMap() {
      return _parameters;
   }

   public String getMethod() {
      if (_postData == null) {
         return "GET";
      } else {
         return "POST";
      }
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
      if (_contentType == null) {
         return null;
      } else {
         int charsetPos = _contentType.indexOf("charset=");
         if (charsetPos == -1) {
            return "UTF-8";
         } else {
            return _contentType.substring(charsetPos + 8);
         }
      }
   }

   public int getContentLength() {
      throw new UnsupportedOperationException();
   }

   public String getContentType() {

      return _contentType;
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

   public BufferedReader getReader() {
      return new BufferedReader(new StringReader(new String(_postData)));
   }

   public String getRemoteAddr() {
      return LOCALHOST_ADDRESS;
   }

   public String getRemoteHost() {
      return LOCALHOST_NAME;
   }

   public String getRemoteUser() {
      return "";
   }

   public String getRequestURI() {
      return _url;
   }

   public StringBuffer getRequestURL() {
      return new StringBuffer(_url);
   }

   public String getRequestedSessionId() {
      throw new UnsupportedOperationException();
   }

   public String getScheme() {
      int separator = _url.indexOf("://");
      if (separator != -1) {
         return _url.substring(0, separator + 3);
      }
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
      return 8080;
   }

   public String getServletPath() {
      return "";
   }

   public HttpSession getSession() {
      return getSession(true);
   }

   public HttpSession getSession(boolean create) {
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
      return false;
   }
}
