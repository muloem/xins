/*
 * $Id$
 */
package org.xins.client;

import java.util.Collections;
import java.util.List;
import org.xins.util.MandatoryArgumentChecker;

/**
 * Abstract base class for <code>CompositeFunctionCaller</code>
 * implementations.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.41
 */
public abstract class AbstractCompositeFunctionCaller
extends AbstractFunctionCaller {

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
    * Creates a new <code>AbstractCompositeFunctionCaller</code>.
    *
    * @param members
    *    the members, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>url == null</code>.
    */
   protected AbstractCompositeFunctionCaller(List members)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("members", members);

      _members = members;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The members of this composite function caller. This field cannot be
    * <code>null</code>.
    */
   private final List _members;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the list of members.
    *
    * @return
    *    an unmodifiable view of the list of members, never <code>null</code>.
    */
   public final List getMembers() {
      // TODO: Cache the unmodifiable list ?
      return Collections.unmodifiableList(_members);
   }
}
