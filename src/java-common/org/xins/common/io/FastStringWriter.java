/*
 * $Id$
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
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
public class FastStringWriter extends Writer {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   private final static int DEFAULT_INITIAL_SIZE = 128;


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Creates a new <code>FastStringWriter</code> using a default initial
    * string buffer size.
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
      if (initialSize < 0) {
         throw new IllegalArgumentException("initialSize (" + initialSize + ") < 0");
      }
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

   public void write(int c) throws IOException {
      assertOpen();
      _buffer.append((char) c);
   }

   public void write(char cbuf[]) throws IOException {
      assertOpen();
      MandatoryArgumentChecker.check("cbuf", cbuf);
      _buffer.append(cbuf);
   }

   public void write(char cbuf[], int off, int len)
   throws IllegalArgumentException, IndexOutOfBoundsException, IOException {
      assertOpen();
      MandatoryArgumentChecker.check("cbuf", cbuf);
      _buffer.append(cbuf, off, len);
   }

   public void write(String str)
   throws IllegalArgumentException, IOException {
      assertOpen();
      MandatoryArgumentChecker.check("str", str);
      _buffer.append(str);
   }

   public void write(String str, int off, int len)
   throws IllegalArgumentException, IOException {
      assertOpen();
      MandatoryArgumentChecker.check("str", str);
      _buffer.append(str.substring(off, off + len));
   }

   public void flush() throws IOException {
      assertOpen();
   }

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
