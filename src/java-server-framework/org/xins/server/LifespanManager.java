/*
 * $Id$
 */
package org.xins.server;

import org.xins.util.collections.PropertyReader;

/**
 * Lifespan manager. Abstract base class for lifespan management classes
 * registered with an API implementation. Implementations must have a public
 * no-argument constructor.
 *
 * <p>The {@link #bootstrap(PropertyReader)} method will
 * be called exactly once during the boostrap of this lifespan manager.
 *
 * <p>After that the {@link #init(PropertyReader)} method will be called to
 * initialize or re-initialize this lifespan manager.
 *
 * <p>The {@link #destroy()} method will be called at shutdown.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.120
 */
public abstract class LifespanManager extends Object {

   // TODO: Add state management

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
    * Constructs a new <code>LifespanManager</code>.
    */
   protected LifespanManager() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Performs the bootstrap procedure
    *
    * @param buildSettings
    *    the build-time configuration properties, not <code>null</code>.
    *
    * @throws Throwable
    *    if the bootstrapping failed, for any reason.
    */
   public abstract void bootstrap(PropertyReader buildSettings)
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
   public abstract void init(PropertyReader runtimeSettings)
   throws Throwable;

   /**
    * Deinitializes this instance.
    *
    * @throws Throwable
    *    if the deinitialization fails.
    */
   public abstract void destroy()
   throws Throwable;
}
