/*
 * $Id$
 */
package org.xins.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
      _random = new Random();
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * Randomizer. This field can not be <code>null</code>
    */
   private final Random _random;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   CallResult callImpl(String sessionID,
                       String functionName,
                       Map    parameters)
   throws IllegalArgumentException,
          CallIOException,
          InvalidCallResultException {

      List members = getMembers();
      int count = (members == null) ? 0 : members.size();

      List list1 = new ArrayList(members);
      List list2 = new ArrayList(count);

      // Randomize list
      for (int i = count; i > 0; i--) {
         int index = _random.nextInt(i);
         list2.add(list1.get(index));
         list1.remove(index);
      }

      Object result;
      int i = 0;
      do {
         FunctionCaller caller = (FunctionCaller) list2.get(i);
         result = tryCall(caller, sessionID, functionName, parameters);
         i++;
      } while (result instanceof Throwable && i < count);

      return callImplResult(result);
   }
}
