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
 */
public interface CompositeFunctionCaller extends FunctionCaller {

   /**
    * Gets the members of this composite function caller.
    *
    * @return
    *    an unmodifiable view of the members, cannot be <code>null</code>.
    */
   List getMembers();
}
