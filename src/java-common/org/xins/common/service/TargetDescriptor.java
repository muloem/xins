/*
 * $Id$
 */
package org.xins.common.service;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.NoSuchElementException;
import java.util.zip.CRC32;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

import org.xins.common.MandatoryArgumentChecker;

/**
 * Descriptor for a single target service. A target service descriptor defines
 * a URL that identifies the location of the service. Also, it may define 3
 * kinds of time-out:
 *
 * <dl>
 *    <dt>total time-out</dt>
 *    <dd>the maximum duration of a call, including connection time, time used
 *    to send the request, time used to receive the response, etc.</dd>
 *
 *    <dt>connection time-out</dt>
 *    <dd>the maximum time for attempting to establish a connection</dd>
 *
 *    <dt>socket time-out</dt>
 *    <dd>the maximum time for attempting to receive data on a socket</dd>
 * </dl>
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 0.146
 */
public final class TargetDescriptor extends Descriptor {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Perl 5 pattern compiler.
    */
   private static final Perl5Compiler PATTERN_COMPILER = new Perl5Compiler();

   /**
    * Pattern matcher.
    */
   private static final Perl5Matcher PATTERN_MATCHER = new Perl5Matcher();

   /**
    * The pattern for a URL, as a character string.
    */
   private static final String PATTERN_STRING = "[a-z][a-z0-9]*:\\/\\/[a-zA-Z0-9\\-]+(\\.[a-zA-Z0-9\\-]+)*(:[1-9][0-9]*)?(\\/([a-zA-Z0-9\\-_~\\.]*))*";

   /**
    * The pattern for a URL.
    */
   private static final Pattern PATTERN;


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Initializes this class. This function compiles {@link #PATTERN_STRING}
    * to a {@link Pattern} and then stores that in {@link #PATTERN}.
    */
   static {
      try {
         PATTERN = PATTERN_COMPILER.compile(PATTERN_STRING, Perl5Compiler.READ_ONLY_MASK);
      } catch (MalformedPatternException mpe) {
         throw new Error("The pattern \"" + PATTERN_STRING + "\" is malformed.");
      }
   }

   /**
    * Computes the CRC-32 checksum for the specified character string.
    *
    * @param s
    *    the string for which to compute the checksum, not <code>null</code>.
    *
    * @return
    *    the checksum for <code>s</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>s == null</code>.
    */
   private int computeCRC32(String s)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("s", s);

      // Compute the CRC-32 checksum
      CRC32 checksum = new CRC32();
      byte[] bytes;
      final String ENCODING = "US-ASCII";
      try {
         bytes = s.getBytes(ENCODING);
      } catch (UnsupportedEncodingException exception) {
         throw new Error("Encoding \"" + ENCODING + "\" is not supported.");
      }
      checksum.update(bytes, 0, bytes.length);
      return (int) (checksum.getValue() & 0x00000000ffffffffL);
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>TargetDescriptor</code> for the specified URL,
    * with the specifed total time-out.
    *
    * <p>Note: Both the connection time-out and the socket time-out will be
    * set to equal the total time-out.
    *
    * @param url
    *    the URL of the service, cannot be <code>null</code>.
    *
    * @param timeOut
    *    the total time-out for the service, in milliseconds; or a negative
    *    value for no total time-out.
    *
    * @throws IllegalArgumentException
    *    if <code>url == null</code>.
    *
    * @throws MalformedURLException
    *    if the specified URL is malformed.
    */
   public TargetDescriptor(String url, int timeOut)
   throws IllegalArgumentException, MalformedURLException {
      this(url, timeOut, timeOut, timeOut);
   }

   /**
    * Constructs a new <code>TargetDescriptor</code> for the specified URL,
    * with the specifed total time-out and connection time-out.
    *
    * <p>Note: The socket time-out will be set to equal the total time-out.
    *
    * <p>Note: If the passed connection time-out is greater than the total
    * time-out, then it will be adjusted to equal the total time-out.
    *
    * @param url
    *    the URL of the service, cannot be <code>null</code>.
    *
    * @param timeOut
    *    the total time-out for the service, in milliseconds; or a negative
    *    value for no total time-out.
    *
    * @param connectionTimeOut
    *    the connection time-out for the service, in milliseconds; or a
    *    negative value for no connection time-out.
    *
    * @throws IllegalArgumentException
    *    if <code>url == null</code>.
    *
    * @throws MalformedURLException
    *    if the specified URL is malformed.
    *
    * @since XINS 0.195
    */
   public TargetDescriptor(String url, int timeOut, int connectionTimeOut)
   throws IllegalArgumentException, MalformedURLException {
      this(url, timeOut, connectionTimeOut, timeOut);
   }

   /**
    * Constructs a new <code>TargetDescriptor</code> for the specified URL,
    * with the specifed total time-out, connection time-out and socket
    * time-out.
    *
    * <p>Note: If the passed connection time-out is greater than the total
    * time-out, then it will be adjusted to equal the total time-out.
    *
    * <p>Note: If the passed socket time-out is greater than the total
    * time-out, then it will be adjusted to equal the total time-out.
    *
    * @param url
    *    the URL of the service, cannot be <code>null</code>.
    *
    * @param timeOut
    *    the total time-out for the service, in milliseconds; or a negative
    *    value for no total time-out.
    *
    * @param connectionTimeOut
    *    the connection time-out for the service, in milliseconds; or a
    *    negative value for no connection time-out.
    *
    * @param socketTimeOut
    *    the socket time-out for the service, in milliseconds; or a
    *    negative value for no socket time-out.
    *
    * @throws IllegalArgumentException
    *    if <code>url == null</code>.
    *
    * @throws MalformedURLException
    *    if the specified URL is malformed.
    *
    * @since XINS 0.195
    */
   public TargetDescriptor(String url,
                           int    timeOut,
                           int    connectionTimeOut,
                           int    socketTimeOut)
   throws IllegalArgumentException, MalformedURLException {

      // Check preconditions
      MandatoryArgumentChecker.check("url", url);
      if (! PATTERN_MATCHER.matches(url, PATTERN)) {
         throw new MalformedURLException(url);
      }

      // Set fields
      _url               = url;
      _timeOut           = timeOut;
      _connectionTimeOut = connectionTimeOut;
      _socketTimeOut     = socketTimeOut;
      _crc               = computeCRC32(url);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The URL for the service. Cannot be <code>null</code>.
    */
   private final String _url;

   /**
    * The total time-out for the service. Is set to a negative value if no
    * total time-out should be applied.
    */
   private final int _timeOut;

   /**
    * The connection time-out for the service. Is set to a negative value if
    * no connection time-out should be applied.
    */
   private final int _connectionTimeOut;

   /**
    * The socket time-out for the service. Is set to a negative value if no
    * socket time-out should be applied.
    */
   private final int _socketTimeOut;

   /**
    * The CRC-32 checksum for the URL. See {@link #_url}.
    */
   private final int _crc;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Checks if this service descriptor denotes a group.
    *
    * @return
    *    <code>false</code> since this descriptor does not denote a group.
    */
   public boolean isGroup() {
      return false;
   }

   /**
    * Returns the URL for the service.
    *
    * @return
    *    the URL for the service, not <code>null</code>.
    */
   public String getURL() {
      return _url;
   }

   /**
    * Returns the total time-out for a call to the service. A negative
    * value indicates there is no time-out.
    *
    * @return
    *    the total time-out for the service, in milli-seconds, or a
    *    negative number if there is no total time-out.
    */
   public int getTimeOut() {
      return _timeOut;
   }

   /**
    * Returns the connection time-out for a call to the service. A negative
    * value indicates there is no connection time-out.
    *
    * @return
    *    the connection time-out for the service, in milli-seconds, or a
    *    negative number if there is no connection time-out.
    *
    * @since XINS 0.195
    */
   public int getConnectionTimeOut() {
      return _connectionTimeOut;
   }

   /**
    * Returns the socket time-out for a call to the service. A negative
    * value indicates there is no socket time-out.
    *
    * @return
    *    the socket time-out for the service, in milli-seconds, or a
    *    negative number if there is no socket time-out.
    *
    * @since XINS 0.195
    */
   public int getSocketTimeOut() {
      return _socketTimeOut;
   }

   /**
    * Returns the CRC-32 checksum for the URL of this function caller.
    *
    * @return
    *    the CRC-32 checksum.
    */
   public int getCRC() {
      return _crc;
   }

   public java.util.Iterator iterateTargets() {
      return new Iterator();
   }

   public int getTargetCount() {
      return 1;
   }

   public TargetDescriptor getTargetByCRC(int crc) {
      return (_crc == crc) ? this : null;
   }

   /**
    * Textual description of this object.
    *
    * @return
    *    this <code>TargetDescriptor</code> as a {@link String}, never
    *    <code>null</code>.
    */
   public String toString() {
      // XXX: Cache in _asString ?
      // TODO: Include connection time-out and socket time-out
      return "TargetDescriptor(url=\"" + _url + "\"; timeOut=" + _timeOut + ')';
   }


   //-------------------------------------------------------------------------
   // Inner classes
   //-------------------------------------------------------------------------

   /**
    * Iterator over this (single) service descriptor. Needed for the
    * implementation of {@link #iterateTargets()}.
    *
    * @version $Revision$ $Date$
    * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
    *
    * @since XINS 0.105
    */
   private final class Iterator
   extends Object
   implements java.util.Iterator {

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      /**
       * Constructs a new <code>Iterator</code>.
       */
      private Iterator() {
         // empty
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      /**
       * Flag that indicates if this iterator is already done iterating over
       * the single element.
       */
      private boolean _done;


      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------

      public boolean hasNext() {
         return ! _done;
      }

      public Object next() {
         if (_done) {
            throw new NoSuchElementException();
         } else {
            _done = true;
            return TargetDescriptor.this;
         }
      }

      public void remove() throws UnsupportedOperationException {
         throw new UnsupportedOperationException();
      }
   }
}
