/*
 * $Id$
 */
package org.xins.server;

import org.xins.util.collections.PropertyReader;

/**
 * Interface for singleton classes registered with an API implementation.
 * Implementations must have a public no-argument constructor. The
 * {@link #init(PropertyReader)} method will be called during the initialization
 * of the XINS/Java Server Framework, while {@link #destroy()} will be called
 * during shutdown.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.55
 *
 * @deprecated
 *    Deprecated since XINS 0.120. Implement the {@link LifespanManager}
 *    interface instead.
 */
public interface Singleton extends LifespanManager{
   // empty
}
