/*
 * $Id$
 */
package org.xins.server;

/**
 * State of a <code>Responder</code>.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @deprecated
 *    Deprecated since XINS 0.168.
 *    This class doesn't need to be used anymore as the responder which
 *    is now the {@link FunctionResult} is executed from generated classes only.
 */
public final class ResponderState {

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Creates a new <code>ResponderState</code>.
    *
    * @param name
    *    the name of the state, should not be <code>null</code>.
    *
    * @deprecated
    *    Deprecated since XINS 0.168.
    */
   ResponderState(String name) {
      _name = name;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The name of this state. This field should never be <code>null</code>.
    */
   private final String _name;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public String toString() {
      return _name;
   }
}
