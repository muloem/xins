/*
 * $Id$
 *
 * Copyright 2004 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.io;

import java.io.IOException;
import java.io.Writer;
import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.text.FastStringBuffer;

/**
 * A non-synchronized equivalent of <code>StringWriter</code>. This class
 * implements a character stream that collects its output in a fast,
 * unsynchronized string buffer, which can then be used to construct a string.
 *
 * <p>Instances of this class are not thread-safe.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
public class FastStringWriter extends Writer {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The default initial internal buffer size.
    */
   private final static int DEFAULT_INITIAL_SIZE = 128;


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Creates a new <code>FastStringWriter</code> using a default initial
    * internal buffer size.
    */
   public FastStringWriter() {
      _buffer = new FastStringBuffer(DEFAULT_INITIAL_SIZE);
   }

   /**
    * Creates a new <code>FastStringWriter</code> using a specified initial
    * string buffer size.
    *
    * @param initialSize
    *    the initial size of the buffer, must be &gt;= 0.
    *
    * @throws IllegalArgumentException
    *    if <code>initialSize &lt; 0</code>.
    */
   public FastStringWriter(int initialSize)
   throws IllegalArgumentException {

      // Check preconditions
      if (initialSize < 0) {
         throw new IllegalArgumentException("initialSize (" + initialSize + ") < 0");
      }

      // Initialize internal buffer
      _buffer = new FastStringBuffer(initialSize);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The buffer to write to.
    */
   private FastStringBuffer _buffer;

   /**
    * Flag that indicates if this stream has been closed.
    */
   private boolean _closed = false;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Asserts that this character stream is open. If it is not, then an
    * <code>IOException</code> is thrown.
    *
    * @throws IOException
    *    if this character stream is closed.
    */
   private void assertOpen() throws IOException {
      if (_closed) {
         throw new IOException("This character stream is closed.");
      }
   }

   /**
    * Writes a single character.
    *
    * @param c
    *    the character to write.
    *
    * @throws IOException
    *    if this writer has been closed (see {@link #close()}).
    */
   public void write(int c) throws IOException {
      assertOpen();
      _buffer.append((char) c);
   }

   /**
    * Writes an array of characters.
    *
    * @param cbuf
    *    the array of characters to write, cannot be <code>null</code>.
    *
    * @throws IOException
    *    if this writer has been closed (see {@link #close()}).
    *
    * @throws IllegalArgumentException
    *    if <code>cbuf == null</code>.
    */
   public void write(char cbuf[])
   throws IOException, IllegalArgumentException {
      assertOpen();
      MandatoryArgumentChecker.check("cbuf", cbuf);
      _buffer.append(cbuf);
   }

   /**
    * Writes a portion of an array of characters.
    *
    * @param cbuf
    *    the array of characters to write a portion of, cannot be
    *    <code>null</code>.
    *
    * @param off
    *    offset from which to start writing characters, must be &gt;= 0 and
    *    &lt; <code>sbuf.length</code>.
    *
    * @param len
    *    the number of characters to write.
    *
    * @throws IOException
    *    if this writer has been closed (see {@link #close()}).
    *
    * @throws IllegalArgumentException
    *    if <code>cbuf == null</code>.
    *
    * @throws IndexOutOfBoundsException
    *    if the offset and/or the length is invalid.
    */
   public void write(char cbuf[], int off, int len)
   throws IllegalArgumentException, IOException, IndexOutOfBoundsException {
      assertOpen();
      MandatoryArgumentChecker.check("cbuf", cbuf);
      _buffer.append(cbuf, off, len);
   }

   /**
    * Writes a character string.
    *
    * @param str
    *    the character string to write, cannot be <code>null</code>.
    *
    * @throws IOException
    *    if this writer has been closed (see {@link #close()}).
    *
    * @throws IllegalArgumentException
    *    if <code>str == null</code>.
    */
   public void write(String str)
   throws IOException, IllegalArgumentException {
      assertOpen();
      MandatoryArgumentChecker.check("str", str);
      _buffer.append(str);
   }

   /**
    * Writes a portion of a character string.
    *
    * @param str
    *    the character string to write a portion of, cannot be
    *    <code>null</code>.
    *
    * @param off
    *    offset from which to start writing characters, must be &gt;= 0 and
    *    &lt; <code>str.{@link String#length() length()}</code>.
    *
    * @param len
    *    the number of characters to write.
    *
    * @throws IOException
    *    if this writer has been closed (see {@link #close()}).
    *
    * @throws IllegalArgumentException
    *    if <code>str == null</code>.
    *
    * @throws IndexOutOfBoundsException
    *    if the offset and/or the length is invalid.
    */
   public void write(String str, int off, int len)
   throws IllegalArgumentException, IOException, IndexOutOfBoundsException {
      assertOpen();
      MandatoryArgumentChecker.check("str", str);
      _buffer.append(str.substring(off, off + len));
   }

   /**
    * Flushes this writer.
    *
    * <p>The implementation of this method does not nothing except checking
    * that this writer is not yet closed.
    *
    * @throws IOException
    *    if this writer has been closed (see {@link #close()}).
    */
   public void flush() throws IOException {
      assertOpen();
   }

   /**
    * Closes this writer. If this writer was already closed, then nothing
    * happens. After calling this method, the write and flush methods will
    * throw an {@link IOException} if called.
    */
   public void close() {
      _closed = true;
   }

   /**
    * Returns the current value of the underlying buffer as a string.
    *
    * @return
    *    the current string, not <code>null</code>.
    */
   public String toString() {
      return _buffer.toString();
   }

   /**
    * Returns the underlying string buffer itself.
    *
    * @return
    *    the underlying string buffer, not <code>null</code>.
    */
   public FastStringBuffer getBuffer() {
      return _buffer;
   }
}
