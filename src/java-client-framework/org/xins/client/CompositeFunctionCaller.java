/*
 * $Id$
 */
package org.xins.client;

import java.util.List;

/**
 * Interface for composite function caller implementations.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.41
 */
public interface CompositeFunctionCaller extends FunctionCaller {

   /**
    * Gets the members of this composite function caller. All instances in the
    * returned list are guaranteed to be {@link FunctionCaller} instances.
    *
    * @return
    *    an unmodifiable view of the members, cannot be <code>null</code>.
    */
   List getMembers();

   /**
    * Returns the list of <code>ActualFunctionCaller</code> instances as a
    * result of a deep search. All instances in the
    * returned list are guaranteed to be {@link ActualFunctionCaller}
    * instances.
    *
    * @return
    *    an unmodifiable view of the {@link ActualFunctionCaller} instances in
    *    this composite function caller and in all underlying composite
    *    function callers (if any), cannot be <code>null</code>.
    */
   List getActualFunctionCallers();
}
