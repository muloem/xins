/*
 * $Id$
 */
package org.xins.tests.server.servlet;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * This class is an implementation of the HTTPServletResponse that can be 
 * invoked locally.
 *
 * @version $Revision$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public class LocalHTTPServletResponse implements HttpServletResponse {

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
    * Creates a new instance of LocalHTTPServletResponse 
    */
   public LocalHTTPServletResponse() {
   }

   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The content type of the result.
    */
   private String _contentType;
   
   /**
    * The status of the result.
    */
   private int _status;
   
   /**
    * The enconding of the result.
    */
   private String _encoding;
   
   /**
    * The writer where to write the result.
    */
   private StringWriter _writer;
   
   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public void addDateHeader(String str, long param) {
      throw new UnsupportedOperationException();
   }

   public void setDateHeader(String str, long param) {
      throw new UnsupportedOperationException();
   }

   public String encodeUrl(String str) {
      throw new UnsupportedOperationException();
   }

   public String encodeURL(String str) {
      throw new UnsupportedOperationException();
   }

   public String encodeRedirectUrl(String str) {
      throw new UnsupportedOperationException();
   }

   public String encodeRedirectURL(String str) {
      throw new UnsupportedOperationException();
   }

   public boolean containsHeader(String str) {
      throw new UnsupportedOperationException();
   }

   public void sendRedirect(String str) {
      throw new UnsupportedOperationException();
   }

   public void setContentType(String type) {
      _contentType = type;
   }

   public void setStatus(int sc) {
      _status = sc;
      System.err.println("Status code: " + sc);
   }

   public void sendError(int sc) {
      _status = sc;
      System.err.println("Error code: " + sc);
   }

   public void setBufferSize(int param) {
      throw new UnsupportedOperationException();
   }

   public void setContentLength(int param) {
      throw new UnsupportedOperationException();
   }

   public void addCookie(Cookie cookie) {
      throw new UnsupportedOperationException();
   }

   public void setLocale(Locale locale) {
      throw new UnsupportedOperationException();
   }

   public void setStatus(int param, String str) {
      throw new UnsupportedOperationException();
   }

   public void setIntHeader(String str, int param) {
      throw new UnsupportedOperationException();
   }

   public void addIntHeader(String str, int param) {
      throw new UnsupportedOperationException();
   }

   public void sendError(int sc, String msg) {
      System.err.println("Error: " + msg + "; code: " + sc);
   }

   public void setHeader(String str, String str1) {
      throw new UnsupportedOperationException();
   }

   public Locale getLocale() {
      throw new UnsupportedOperationException();
   }

   public String getCharacterEncoding() {
      return _encoding;
   }

   public int getBufferSize() {
      throw new UnsupportedOperationException();
   }

   public void flushBuffer() {
      throw new UnsupportedOperationException();
   }

   public void addHeader(String str, String str1) {
      throw new UnsupportedOperationException();
   }

   public ServletOutputStream getOutputStream() {
      throw new UnsupportedOperationException();
   }

   public PrintWriter getWriter() {
      _writer = new StringWriter();
      return new PrintWriter(_writer);
   }

   public boolean isCommitted() {
      throw new UnsupportedOperationException();
   }

   public void reset() {
      throw new UnsupportedOperationException();
   }

   public void resetBuffer() {
      throw new UnsupportedOperationException();
   }

   /**
    * Gets the returned message from the servlet.
    *
    * @return 
    *    the returned message.
    */
   public String getResult() {
      return _writer.toString();
   }
   
   /**
    * Gets the status of the returned message.
    *
    * @return
    *    The HTTP status returned.
    */
   public int getStatus() {
      return _status;
   }
   
   /**
    * Gets the context type of the returned text.
    *
    * @return
    *    The content type, cannot be <code>null</code>.
    */
   public String getContentType() {
      return _contentType;
   }
}
