/*
 * $Id$
 */
package org.xins.client;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.xins.util.MandatoryArgumentChecker;

/**
 * Call target group that iterates over the contained function callers. This
 * is known as the <em>round robin</em> method.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.43
 */
final class RoundRobinCallTargetGroup extends CallTargetGroup {

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
    * Constructs a new <code>RoundRobinCallTargetGroup</code>.
    *
    * @param members
    *    the members of this group, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>members == null</code>.
    */
   RoundRobinCallTargetGroup(List members) throws IllegalArgumentException {
      super(ROUND_ROBIN_TYPE, members);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   CallResult callImpl(String sessionID,
                       String functionName,
                       Map    parameters)
   throws IllegalArgumentException, IOException, InvalidCallResultException {
      return null; // TODO
   }
}
