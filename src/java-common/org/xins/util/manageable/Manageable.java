/*
 * $Id$
 */
package org.xins.util.manageable;

import org.xins.util.MandatoryArgumentChecker;
import org.xins.util.collections.PropertyReader;

/**
 * Lifespan manager. Abstract base class for classes that support bootstrap,
 * initialization and destruction functions.
 *
 * <p>In environments where lifespan manager instances are constructed
 * dynamically, they are typically expected to have a public no-argument
 * constructor.
 *
 * <p>The {@link #bootstrap(PropertyReader)} method should be called exactly
 * once before initializing and using this object.
 *
 * <p>After that the {@link #init(PropertyReader)} method should be called to
 * initialize or re-initialize this object. This should be done at least once
 * before this object can be used. This method should only be called after
 * {@link #bootstrap(PropertyReader)}.
 *
 * <p>The {@link #deinit()} method will be called when this object is no
 * longer needed. After that, {@link #bootstrap(PropertyReader)} could be
 * called again, though.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.147
 */
public abstract class Manageable extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The <em>UNUSABLE</em> state.
    */
   public static final State UNUSABLE = new State("UNUSABLE");

   /**
    * The <em>BOOTSTRAPPED</em> state.
    */
   public static final State BOOTSTRAPPED = new State("BOOTSTRAPPED");

   /**
    * The <em>USABLE</em> state.
    */
   public static final State USABLE = new State("USABLE");


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>Manageable</code>.
    */
   protected Manageable() {
      _state = UNUSABLE;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The state of this manageable object.
    */
   private State _state;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Gets the current state of this object
    *
    * @return
    *    the current state, never <code>null</code>.
    */
   public final State getState() {
      return _state;
   }

   /**
    * Performs the bootstrap procedure (wrapper method).
    *
    * <p>If the state of this object is valid (it must be {@link #UNUSABLE})
    * and the argument is not <code>null</code>, then
    * {@link #bootstrapImpl(PropertyReader)} will be called. If that method
    * succeeds, then this object will be left in the {@link #BOOTSTRAPPED}
    * state.
    *
    * <p>If {@link #bootstrapImpl(PropertyReader)} throws any exception (even
    * {@link Error}s), it is wrapped in an {@link BootstrapException} and then
    * the latter is thrown instead.
    *
    * @param properties
    *    the bootstrap properties, not <code>null</code>.
    *
    * @throws IllegalStateException
    *    if the current state is not {@link #UNUSABLE}.
    *
    * @throws IllegalArgumentException
    *    if <code>properties == null</code>. 
    *
    * @throws MissingRequiredPropertyException
    *    if a required property is not given.
    *
    * @throws InvalidPropertyValueException
    *    if the value of a certain property is invalid.
    *
    * @throws BootstrapException
    *    if the bootstrapping failed for any other reason.
    */
   public final void bootstrap(PropertyReader properties)
   throws IllegalStateException,
          IllegalArgumentException,
          MissingRequiredPropertyException,
          InvalidPropertyValueException,
          BootstrapException {

      // Check state
      if (_state != UNUSABLE) {
         throw new IllegalStateException("The current state is " + _state + " instead of " + UNUSABLE + '.');
      }

      // Check arguments
      MandatoryArgumentChecker.check("properties", properties);

      // Delegate to subclass
      try {
         bootstrapImpl(properties);
      } catch (MissingRequiredPropertyException exception) {
         throw exception;
      } catch (InvalidPropertyValueException exception) {
         throw exception;
      } catch (BootstrapException exception) {
         throw exception;

      // Catch-all: Wrap other exceptions in a BootstrapException
      } catch (Throwable exception) {
         throw new BootstrapException(exception);
      }

      // Upgrade the state
      _state = BOOTSTRAPPED;
   }

   /**
    * Performs the bootstrap procedure (actual implementation). When this
    * method is called from {@link #bootstrap(PropertyReader)}, the state and
    * the argument will have been checked.
    *
    * <p>The implementation of this method in class {@link Manageable} is
    * empty.
    *
    * @param properties
    *    the bootstrap properties, not <code>null</code>.
    *
    * @throws MissingRequiredPropertyException
    *    if a required property is not given.
    *
    * @throws InvalidPropertyValueException
    *    if the value of a certain property is invalid.
    *
    * @throws BootstrapException
    *    if the bootstrapping failed for any other reason.
    */
   protected void bootstrapImpl(PropertyReader properties)
   throws MissingRequiredPropertyException,
          InvalidPropertyValueException,
          BootstrapException {
      // empty
   }

   /**
    * Performs the initialization procedure (wrapper method).
    *
    * <p>If the state of this object is valid (it must be either
    * {@link #BOOTSTRAPPED} or {@link USABLE}) and the argument is not
    * <code>null</code>, then {@link #initImpl(PropertyReader)} will be
    * called. If that method succeeds, then this object will be left in the
    * {@link #USABLE} state.
    *
    * <p>If {@link #initImpl(PropertyReader)} throws any exception (even
    * {@link Error}s), it is wrapped in an {@link InitializationException} and
    * then the latter is thrown instead.
    *
    * @param properties
    *    the initialization properties, not <code>null</code>.
    *
    * @throws IllegalStateException
    *    if the current state is not {@link #BOOTSTRAPPED} or {@link #USABLE}.
    *
    * @throws IllegalArgumentException
    *    if <code>properties == null</code>. 
    *
    * @throws MissingRequiredPropertyException
    *    if a required property is not given.
    *
    * @throws InvalidPropertyValueException
    *    if the value of a certain property is invalid.
    *
    * @throws InitializationException
    *    if the initialization failed for any other reason.
    */
   public final void init(PropertyReader properties)
   throws IllegalStateException,
          IllegalArgumentException,
          MissingRequiredPropertyException,
          InvalidPropertyValueException,
          InitializationException {

      // Check state
      if (_state != BOOTSTRAPPED && _state != USABLE) {
         throw new IllegalStateException("The current state is " + _state + " instead of either " + BOOTSTRAPPED + " or " + USABLE + '.');
      }

      // Check arguments
      MandatoryArgumentChecker.check("properties", properties);

      // Delegate to subclass
      try {
         initImpl(properties);
      } catch (MissingRequiredPropertyException exception) {
         throw exception;
      } catch (InvalidPropertyValueException exception) {
         throw exception;
      } catch (InitializationException exception) {
         throw exception;

      // Catch-all: Wrap other exceptions in an InitializationException
      } catch (Throwable exception) {
         throw new InitializationException(exception);
      }

      // Upgrade the state
      _state = USABLE;
   }

   /**
    * Performs the initialization procedure (actual implementation). When this
    * method is called from {@link #bootstrap(PropertyReader)}, the state and
    * the argument will have been checked.
    *
    * <p>The implementation of this method in class {@link Manageable} is
    * empty.
    *
    * @param properties
    *    the initialization properties, not <code>null</code>.
    *
    * @throws MissingRequiredPropertyException
    *    if a required property is not given.
    *
    * @throws InvalidPropertyValueException
    *    if the value of a certain property is invalid.
    *
    * @throws InitializationException
    *    if the initialization failed, for any other reason.
    */
   protected void initImpl(PropertyReader properties)
   throws MissingRequiredPropertyException,
          InvalidPropertyValueException,
          InitializationException {
      // empty
   }

   /**
    * Deinitializes this instance (wrapper method). This method relies on
    * {@link #deinitImpl()} to actually perform the deinitialization.
    *
    * <p>The current state of this object must be either {@link #BOOTSTRAPPED}
    * or {@link #USABLE}.
    *
    * <p>When this method returns, the state has been set to
    * {@link #UNUSABLE}, even if {@link #deinitImpl()} threw an exception.
    *
    * <p>If {@link #deinitImpl()} throws any exception, it is wrapped in a
    * {@link DeinitializationException} and
    * then the latter is thrown instead.
    *
    * @throws IllegalStateException
    *    if the state is not {@link #BOOTSTRAPPED} nor {@link #USABLE}.
    *
    * @throws DeinitializationException
    *    if the deinitialization caused an exception in
    *    {@link #deinitImpl()}.
    */
   public final void deinit()
   throws IllegalStateException, DeinitializationException {

      try {
         deinitImpl();
      } catch (Throwable exception) {
         throw new DeinitializationException(exception);
      } finally {
         _state = UNUSABLE;
      }
   }

   /**
    * Deinitializes this instance (actual implementation). This method will be
    * called from {@link #deinit()} each time the latter is called and it
    * finds that the state is correct.
    *
    * @throws Throwable
    *    if the deinitialization caused an exception.
    */
   public final void deinitImpl()
   throws Throwable {
      // empty
   }

   /**
    * Checks that this object is currently usable. If it is not, then an
    * {@link IllegalStateException} is thrown.
    *
    * @throws IllegalStateException
    *    if this object is not in the {@link #USABLE} state.
    */
   protected final void assertUsable()
   throws IllegalStateException {
      if (_state != USABLE) {
         throw new IllegalStateException("The current state is " + _state + " instead of " + USABLE + '.');
      }
   }


   //-------------------------------------------------------------------------
   // Inner classes
   //-------------------------------------------------------------------------

   /**
    * State of a <code>Manageable</code> object.
    *
    * @version $Revision$ $Date$
    * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
    *
    * @since XINS 0.147
    */
   public static final class State extends Object {

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      /**
       * Constructs a new <code>State</code> object.
       *
       * @param name
       *    the name of this state, cannot be <code>null</code>.
       *
       * @throws IllegalArgumentException
       *    if <code>name == null</code>.
       */
      private State(String name) throws IllegalArgumentException {

         // Check preconditions
         MandatoryArgumentChecker.check("name", name);

         _name = name;
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      /**
       * The name of this state. Cannot be <code>null</code>.
       */
      private final String _name; 


      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------

      /**
       * Returns the name of this state.
       *
       * @return
       *    the name of this state, cannot be <code>null</code>.
       */
      public String getName() {
         return _name;
      }

      /**
       * Returns a textual representation of this object.
       *
       * @return
       *    the name of this state, never <code>null</code>.
       */
      public String toString() {
         return _name;
      }
   }
}
