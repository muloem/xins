/*
 * $Id$
 */
package org.xins.client;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.xins.util.MandatoryArgumentChecker;

/**
 * Call target group that chooses one of the underlying function callers
 * randomly.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.43
 */
final class RandomCallTargetGroup extends CallTargetGroup {

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
    * Constructs a new <code>RandomCallTargetGroup</code>.
    *
    * @param members
    *    the members of this group, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>members == null</code>.
    */
   RandomCallTargetGroup(List members) throws IllegalArgumentException {
      super(RANDOM_TYPE, members);
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
