/*
 * $Id$
 */
package org.xins.client;

/**
 * Exception that indicates that an API call returned a result that was
 * considered unacceptable by the application layer.
 *
 * <p>Note that this exception is <em>not</em> thrown if the result is
 * invalid according to the XINS rules for an result XML document. Only if the
 * result is just invalid in relation to the applicable API specification this
 * exception is thrown.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 0.136
 */
public final class UnacceptableCallResultException extends CallException {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>UnacceptableCallResultException</code>.
    *
    * @param result
    *    the {@link Result} that is considered
    *    unacceptable, never <code>null</code>.
    *
    * @param detail
    *    a detailed description of why the result is considered unacceptable,
    *    or <code>null</code> if such a description is not available.
    *
    * @throws IllegalArgumentException
    *    if <code>result == null</code>.
    *
    * @since XINS 0.202
    */
   public UnacceptableCallResultException(Result    result,
                                          String    detail,
                                          Throwable cause)
   throws IllegalArgumentException {

      super("Unacceptable call result", result, detail, cause);

      // Store the result
      _result = result;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The result that is considered unacceptable. Never <code>null</code>.
    */
   private final Result _result;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   // TODO: Add methods to retrieve result
}
