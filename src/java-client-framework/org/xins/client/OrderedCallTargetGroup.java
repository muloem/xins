/*
 * $Id$
 */
package org.xins.client;

import java.util.List;
import java.util.Map;
import org.xins.util.MandatoryArgumentChecker;

/**
 * Call target group that tries to call the underlying function callers in
 * order.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.41
 */
final class OrderedCallTargetGroup extends CallTargetGroup {

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
    * Constructs a new <code>OrderedCallTargetGroup</code>.
    *
    * @param members
    *    the members of this group, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>members == null</code>.
    */
   OrderedCallTargetGroup(List members) throws IllegalArgumentException {
      super(ORDERED_TYPE, members);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   CallResult callImpl(String sessionID, String functionName, Map parameters)
   throws IllegalArgumentException,
          CallIOException,
          InvalidCallResultException {

      List members = getMembers();
      int count = (members == null) ? 0 : members.size();
      Object result;
      int i = 0;
      do {
         FunctionCaller caller = (FunctionCaller) members.get(i);
         result = tryCall(caller, sessionID, functionName, parameters);
         i++;
      } while (result instanceof Throwable && i < count);

      return callImplResult(result);
   }
}
