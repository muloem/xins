/*
 * $Id$
 */
package org.xins.common.service.http;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.xins.common.MandatoryArgumentChecker;

/**
 * Result returned from an HTTP request.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 0.207
 */
public final class HTTPCallResult extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>HTTPCallResult</code> object. The status code and
    * the byte data need to be given. Note that the byte data will be stored
    * as-is, no copying will be done. And the data will be returned as-is by
    * {@link #getData()}.
    *
    * @param code
    *    the HTTP return code, must be &gt;= 0.
    *
    * @param data
    *    the retrieved data, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>data == null || code &lt; 0</code>.
    */
   public HTTPCallResult(int code, byte[] data)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("data", data);
      if (code < 0) {
         throw new IllegalArgumentException("code (" + code + ") < 0");
      }

      // Just store the arguments in fields
      _code = code;
      _data = data;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The HTTP status code. Guaranteed to be greater than 0.
    */
   private final int _code;

   /**
    * The data returned. Never <code>null</code>.
    */
   private final byte[] _data;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the HTTP status code.
    *
    * @return
    *    the HTTP status code.
    */
   public int getStatusCode() {
      return _code;
   }

   /**
    * Returns the result data as a byte array. Note that this is not a copy or
    * clone of the internal data structure, but it is a link to the actual
    * data structure itself.
    *
    * @return
    *    a byte array of the result data, never <code>null</code>.
    */
   public byte[] getData() {
      return _data;
   }

   /**
    * Returns the returned data as a <code>String</code>. The encoding
    * <code>US-ASCII</code> is assumed.
    *
    * @return
    *    the result data as a text string, not <code>null</code>.
    */
   public String getString() {
      final String ENCODING = "US-ASCII";
      try {
         return getString(ENCODING);
      } catch (UnsupportedEncodingException exception) {
         throw new Error("Encoding \"" + ENCODING + "\" is unsupported.");
      }
   }

   /**
    * Returns the returned data as a <code>String</code> in the specified
    * encoding.
    *
    * @param encoding
    *    the encoding to use in the conversion from bytes to a text string,
    *    not <code>null</code>.
    *
    * @return
    *    the result data as a text string, not <code>null</code>.
    *
    * @throws UnsupportedEncodingException
    *    if the specified encoding is not supported.
    */
   public String getString(String encoding)
   throws UnsupportedEncodingException {
      byte[] bytes = getData();
      return new String(bytes, encoding);
   }

   /**
    * Returns the returned data as an <code>InputStream</code>. The input
    * stream is based directly on the underlying byte array.
    *
    * @return
    *    an {@link InputStream} that returns the returned data, never
    *    <code>null</code>.
    */
   public InputStream getStream() {
      return new ByteArrayInputStream(_data);
   }
}
