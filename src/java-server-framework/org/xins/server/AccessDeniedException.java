/*
 * $Id$
 */
package org.xins.server;

/**
 * Exception that indicates that there is no function matching the request.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 0.153
 */
public final class AccessDeniedException
extends Exception {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Creates the error message for this exception.
    *
    * @param ip
    *    the IP address, or <code>null</code>.
    *
    * @param functionName
    *    the name of the function, or <code>null</code>.
    */
   private static String createMessage(String ip, String functionName) {
      if (functionName != null && ip != null) {
         return "The function \"" + functionName + "\" cannot be accessed from IP address " + ip + ".";
      } else if (ip != null) {
         return "A function cannot be accessed from IP address " + ip + ".";
      } else if (functionName != null) {
         return "The function \"" + functionName + "\" cannot be accessed.";
      } else {
         return "An undefined function cannot be accessed.";
      }
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>AccessDeniedException</code> for the specified
    * IP address and function name.
    *
    * @param ip
    *    the IP address, or <code>null</code>.
    *
    * @param functionName
    *    the name of the function, or <code>null</code>.
    */
   AccessDeniedException(String ip, String functionName) {
      super(createMessage(ip, functionName));
      _ip = ip;
      _functionName = functionName;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The IP address which is denied for the given function.
    */
    private final String _ip;

    /**
     * The name of the function which does not grant the access.
     */
    private final String _functionName;

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Gets the IP address which is denied for the given function.
    *
    * @return
    *    the IP address, or <code>null</code> if no IP address was provided.
    */
   public String getIP() {
      return _ip;
   }

   /**
    * Gets the name of the function which does not grant the access.
    *
    * @return
    *    the name of the function, or <code>null</code> if no function was provided.
    */
   public String getFunctionName() {
      return _functionName;
   }
}
