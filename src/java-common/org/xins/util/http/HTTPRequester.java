/*
 * $Id$
 */
package org.xins.util.http;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.HttpURLConnection;
import org.apache.log4j.Logger;
import org.xins.util.MandatoryArgumentChecker;

/**
 * Utility class for performing HTTP requests.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public class HTTPRequester extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The logging category used by this class. This class field is never
    * <code>null</code>.
    */
   private final static Logger LOG = Logger.getLogger(HTTPRequester.class.getName());


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Fetches the data from the connection.
    *
    * @param connection
    *    the connection to get the data from, not <code>null</code>.
    *
    * @param bufferSize
    *    the buffer size to use when reading data from the HTTP server.
    *
    * @return
    *    a byte array of the result data, not <code>null</code>.
    *
    * @throws IOException
    *    if there was an I/O error.
    */
   private static byte[] fetchData(HttpURLConnection connection,
                                   int               bufferSize)
   throws IOException {

      // Get the input stream from the connection
      InputStream input = connection.getInputStream();

      try {
         // Create a buffer to read into
         byte buffer[] = new byte[bufferSize];

         // Fetch everything into the buffer
         ByteArrayOutputStream output = new ByteArrayOutputStream();
         int count = input.read(buffer);
         while(count >= 0) {
            output.write(buffer, 0, count);
            count = input.read(buffer);
         }

         // Close input stream and output stream
         input.close();
         input = null;

         return output.toByteArray();
      } finally {
         if (input != null) {
            try {
               input.close();
            } catch (IOException exception) {
               LOG.error("I/O error while attempting to close input stream. Ignoring.", exception);
               // ignore
            }
         }
      }
   }


   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Creates a new <code>HTTPRequester</code>.
    */
   public HTTPRequester() {
      // empty
   }

   /**
    * Posts the specified <code>String</code> content to a given URL, assuming
    * the specified encoding.
    *
    * <p />This method will <em>not</em> URL encode the content. URL encoding
    * must be performed by the caller. Use
    * {@link java.net.URLEncoder#encode(String)} for this.
    *
    * @param url
    *    the encoded URL to be posted to, not <code>null</code>.
    *
    * @param content
    *    the content to be posted, or <code>null</code>.
    *
    * @param encoding
    *    the encoding to use, cannot be <code>null</code>.
    *
    * @return
    *    the result of the post.
    *
    * @throws IOException
    *    if there was an I/O error.
    */
   public Result post(URL url, String content, String encoding)
   throws IOException {
      byte[] bytes = content.getBytes(encoding);
      return post(url, bytes);
   }

   /**
    * Posts the specified <code>String</code> content to a given URL. The
    * encoding <code>US-ASCII</code> will be assumed.
    *
    * <p />This method will <em>not</em> URL encode the content. URL encoding
    * must be performed by the caller. Use
    * {@link java.net.URLEncoder#encode(String)} for this.
    *
    * @param url
    *    the encoded URL to be posted to, not <code>null</code>.
    *
    * @param content
    *    the content to be posted, or <code>null</code>.
    *
    * @return
    *    the result of the post.
    *
    * @throws IOException
    *    if there was an I/O error.
    */
   public Result post(URL url, String content) throws IOException {
      return post(url, content, "US-ASCII");
   }

   /**
    * Posts the specified <code>byte[]</code> content to a given URL.
    *
    * <p />This method will <em>not</em> URL encode the content. URL encoding
    * must be performed by the caller. Use
    * {@link java.net.URLEncoder#encode(String)} for this.
    *
    * @param url
    *    the encoded URL to be posted to, not <code>null</code>.
    *
    * @param content
    *    the content to be posted, or <code>null</code>.
    *
    * @return
    *    the result of the post.
    *
    * @throws IllegalArgumentException
    *    if <code>url == null</code>.
    *
    * @throws IOException
    *    if there was an I/O error.
    */
   public Result post(URL url, byte[] content)
   throws IllegalArgumentException, IOException {
      return post(url, content, null);
   }

   /**
    * Posts the specified <code>byte[]</code> content to a given URL using the
    * specified host name.
    *
    * <p />This method will <em>not</em> URL encode the content. URL encoding
    * must be performed by the caller. Use
    * {@link java.net.URLEncoder#encode(String)} for this.
    *
    * @param url
    *    the encoded URL to be posted to, not <code>null</code>.
    *
    * @param content
    *    the content to be posted, or <code>null</code>.
    *
    * @param hostName
    *    the host name to send with the HTTP/1.1 request, or <code>null</code>
    *    if the default should be used.
    *
    * @return
    *    the result of the post.
    *
    * @throws IllegalArgumentException
    *    if <code>url == null</code>.
    *
    * @throws IOException
    *    if there was an I/O error.
    */
   public Result post(URL url, byte[] content, String hostName)
   throws IllegalArgumentException, IOException {

      // Check preconditions
      MandatoryArgumentChecker.check("url", url);

      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      boolean doOutput = content != null && content.length > 0;
      try {
         connection.setDoOutput(doOutput);
         connection.setDoInput(true);
         connection.setUseCaches(false); // XXX: Configurable?
         connection.setRequestMethod("POST"); // XXX: Configurable?
         connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
         connection.setRequestProperty("Content-Length", String.valueOf(content.length));
         if (hostName != null) {
            connection.setRequestProperty("Host", hostName);
         }

         if (doOutput) {
            OutputStream out = connection.getOutputStream();
            try {
               out.write(content);
            } finally {

               try {
                  // Flush and close the output stream
                  out.close();
               } catch (IOException exception) {
                  LOG.error("I/O error while attempting to close output stream. Ignoring.", exception);
                  // ignore
               }
            }
         }

         // Get the code and data
         int code = connection.getResponseCode();
         byte[] data = fetchData(connection, 1024);

         // Disconnect
         connection.disconnect();
         connection = null;

         return new Result(code, data);

      } finally {
         if (connection != null) {
            try {
               connection.disconnect();
            } catch (Exception exception) {
               LOG.error("I/O error while attempting to disconnect. Ignoring.", exception);
               // ignore
            }
         }
      }
   }

   /**
    * Result returned from an HTTP request.
    *
    * @version $Revision$ $Date$
    * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
    */
   public class Result extends Object {

      //----------------------------------------------------------------------
      // Constructor
      //----------------------------------------------------------------------

      /**
       * Constructs a new <code>Result</code> object.
       *
       * @param code
       *    the HTTP return code.
       *
       * @param data
       *    the retrieved data, not <code>null</code>.
       *
       * @throws IllegalArgumentException
       *    if <code>data == null</code>.
       *
       * @throws IOException
       *    if there was an I/O error.
       */
      private Result(int code, byte[] data)
      throws IllegalArgumentException, IOException {

         // Check preconditions
         MandatoryArgumentChecker.check("data", data);

         _code       = code;
         _data       = data;
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      /**
       * The HTTP return code.
       */
      private final int _code;

      /**
       * The data returned.
       */
      private final byte[] _data;


      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------

      /**
       * Returns the HTTP code.
       *
       * @return
       *    the HTTP return code.
       */
      public int getCode() {
         return _code;
      }

      /**
       * Returns the returned data as a byte array.
       *
       * @return
       *    a byte array of the result data, not <code>null</code>.
       *
       * @throws IOException
       *    if there was an I/O error.
       */
      public byte[] getData() throws IOException {
         return _data;
      }

      /**
       * Returns the returned data as a <code>String</code>. The encoding
       * <code>US-ASCII</code> is assumed.
       *
       * @return
       *    the result data as a text string, not <code>null</code>.
       *
       * @throws IOException
       *    if there was an I/O error.
       */
      public String getString() throws IOException {
         return getString("US-ASCII");
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
       * @throws IOException
       *    if there was an I/O error.
       *
       * @throws UnsupportedEncodingException
       *    if the specified encoding is not supported; (when writing a
       *    try-catch statement, note that this is a subclass of
       *    {@link IOException}).
       */
      public String getString(String encoding)
      throws IOException, UnsupportedEncodingException {
         byte[] bytes = getData();
         return new String(bytes, encoding);
      }
   }
}
