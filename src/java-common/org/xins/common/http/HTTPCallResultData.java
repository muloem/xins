/*
 * $Id$
 */
package org.xins.common.http;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.xins.common.Log;
import org.xins.common.MandatoryArgumentChecker;

import org.xins.common.service.CallException;
import org.xins.common.service.CallExceptionList;
import org.xins.common.service.CallResult;
import org.xins.common.service.TargetDescriptor;

/**
 * Data part of an HTTP call result.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 0.209
 */
public final class HTTPCallResultData extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The fully-qualified name of this class.
    */
   private static final String CLASSNAME = HTTPCallResultData.class.getName();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>HTTPCallResultData</code> object.
    *
    * @param code
    *    the HTTP status code.
    *
    * @param data
    *    the data returned from the call, as a set of bytes.
    */
   HTTPCallResultData(int code, byte[] data) {
      _code = code;
      _data = data;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The HTTP status code.
    */
   private final int _code;

   /**
    * The data returned.
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
}
