/*
 * $Id$
 */
package org.xins.server;

import org.xins.util.collections.PropertyReader;

/**
 * Interface for lifespan management classes registered with an API
 * implementation. Implementations must have a public no-argument constructor.
 * The {@link #init(PropertyReader,PropertyReader)} method will be called
 * during the initialization of the XINS/Java Server Framework, while
 * {@link #destroy()} will be called during shutdown.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.120
 */
public interface LifespanManager {

   /**
    * Bootstraps this instance.
    *
    * @param buildSettings
    *    the build-time configuration properties, not <code>null</code>.
    *
    * @throws Throwable
    *    if the bootstrapping failed, for any reason.
    */
   void bootstrap(PropertyReader buildSettings)
   throws Throwable;

   /**
    * Initializes or re-initializes this instance.
    *
    * @param runtimeSettings
    *    the runtime configuration properties, not <code>null</code>.
    *
    * @throws InitializationException
    *    if the initialization failed, for any reason.
    */
   void init(PropertyReader runtimeSettings)
   throws InitializationException;

   /**
    * Deinitializes this instance.
    *
    * @throws Throwable
    *    if the deinitialization fails.
    */
   void destroy() throws Throwable;
}
