/*
 * $Id$
 */
package org.xins.server;

/**
 * Response validator. Validates the commands sent to a {@link Responder}. A
 * thread will always handle a single response at a time. The first method
 * that will be called is always {@link #startResponse(boolean,String)}. The
 * last method that will be called is either {@link #endResponse()} or
 * {@link #cancelResponse()}.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.50
 */
public interface ResponseValidator {

   /**
    * Callback that indicates the response is started.
    *
    * @param success
    *    success indication, can be <code>null</code>.
    *
    * @param code
    *    if response code, or <code>null</code>.
    *
    * @throws InvalidResponseException
    *    if the specified combination of success indication and result code is
    *    considered invalid.
    */
   void startResponse(boolean success, String code)
   throws InvalidResponseException;

   /**
    * Callback that indicates the response is ended.
    *
    * <p>Either this method or {@link #cancelResponse()} is used to indicate a
    * response is concluded.
    *
    * @throws InvalidResponseException
    *    if the response until now is considered invalid, for example if some
    *    mandatory parameters or elements were missing.
    */
   void endResponse()
   throws InvalidResponseException;

   /**
    * Callback that indicates the response was cancelled due to an error. This
    * method should only perform cleanup, no further validation can be done,
    * since the call is already unsuccessful.
    *
    * <p>Either this method or {@link #endResponse()} is used to indicate a
    * response is concluded.
    */
   void cancelResponse();
}
