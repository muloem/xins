/*
 * $Id$
 */
package org.xins.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.log4j.Logger;
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

   /**
    * The logging category used by this class. This class field is never
    * <code>null</code>.
    */
   private final static Logger LOG = Logger.getLogger(RandomCallTargetGroup.class.getName());


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

   XINSServiceCaller.Result callImpl(String sessionID,
                            String functionName,
                            Map    parameters)
   throws IllegalArgumentException,
          CallIOException,
          InvalidCallResultException {

      // Get all members
      List members = getMembers();
      int count = (members == null) ? 0 : members.size();

      // Copy all members to a new list to move them from during randomizing
      List list1 = new ArrayList(members);

      // Create a new list to move into during randomizing
      List list2 = new ArrayList(count);

      // Randomize list, moving from list1 to list2
      for (int i = count; i > 0; i--) {
         int index = _random.nextInt(i);
         list2.add(list1.get(index));
         list1.remove(index);
      }

      // The first list is now unneeded
      list1 = null;

      // Go through the randomized list in order
      Object result;
      int i = 0;
      boolean divert;
      do {
         FunctionCaller caller = (FunctionCaller) list2.get(i);

         // Increase the counter, both since the log message is 1-based and
         // for the loop condition
         i++;

         // Log this attempt
         if (LOG.isDebugEnabled()) {
            LOG.debug("Trying to call " + caller + " (attempt " + i + '/' + count + ')');
         }

         // Attempt the call
         result = tryCall(caller, sessionID, functionName, parameters);

         // Determine if the call failed
         String code = "";
         if (i == count) {
            divert = false;
         } else {
            divert = result instanceof Throwable;
            if (!divert) {
               XINSServiceCaller.Result r = (XINSServiceCaller.Result) result;
               code = r.getCode();
               if (code != null) {
                  divert = divertOnCode(code);
               }
            }
         }

         // Log wether the call was successful
         if (LOG.isDebugEnabled()) {
            if (divert) {
               if (result instanceof Throwable) {
                  LOG.debug("Call attempt " + i + '/' + count + " failed due to " + result.getClass().getName() + '.');
               } else {
                  LOG.debug("Call attempt " + i + '/' + count + " failed due to result code \"" + code + "\".");
               }
            } else {
               LOG.debug("Call attempt " + i + '/' + count + " succeeded.");
            }
         }
      } while (divert);

      return callImplResult(result);
   }
}
