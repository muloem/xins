/*
 * $Id$
 *
 * Copyright 2004 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.service;

import org.xins.common.Log;
import org.xins.common.threads.Doorman;

/**
 * Abstraction of a call configuration. Objects of this type specify certain
 * aspects of <em>how</em> a call is executed. For example, for an HTTP
 * service caller, a <code>CallConfig</code> object could specify what HTTP
 * method (GET, POST, etc.) to use.
 *
 * <p>This base class only specifies the property <em>failOverAllowed</em>,
 * which indicates whether fail-over is unconditionally allowed, even if the
 * request was already received or even processed by the other end.
 *
 * <p>This class is thread-safe.
 *
 * <p>Note to implementors of this class: Subclasses <em>must</em> be
 * thread-safe as well. They should use {@link #_doorman} to synchronize the
 * read and write operations to all fields. For example, the
 * <em>failOverAllowed</em> getter and setter methods in this class could be
 * implemented as follows:
 *
 * <blockquote><pre>public final boolean isFailOverAllowed() {
 *   _doorman.enterAsReader();
 *   boolean b = _failOverAllowed;
 *   _doorman.leaveAsReader();
 *   return b;
 *}
 *
 *public final void setFailOverAllowed(boolean allowed) {
 *   _doorman.enterAsWriter();
 *   _failOverAllowed = allowed;
 *   _doorman.leaveAsWriter();
 *}</pre></blockquote>
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.1.0
 */
public abstract class CallConfig extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Fully-qualified name of this class.
    */
   private static final String CLASSNAME = CallConfig.class.getName();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>CallConfig</code> object. This constructor is
    * only available to subclasses, since this class is <code>abstract</code>.
    */
   protected CallConfig() {

      // TRACE: Enter constructor
      Log.log_1000(CLASSNAME, null);

      // TODO: Improve name sent to Doorman constructor
      _doorman = new Doorman("CallConfig", false, 3, 4000);

      // TRACE: Leave constructor
      Log.log_1002(CLASSNAME, null);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * Access controller for the fields in this object.
    *
    * <p>When reading a field, {@link Doorman#enterAsReader() enterAsReader()}
    * should be called, followed by
    * {@link Doorman#leaveAsReader() leaveAsReader()} when done.
    *
    * <p>When writing a field, {@link Doorman#enterAsWriter() enterAsWriter()}
    * should be called, followed by
    * {@link Doorman#leaveAsWriter() leaveAsWriter()} when done.
    */
   protected final Doorman _doorman;

   /**
    * Flag that indicates whether fail-over is unconditionally allowed.
    */
   private boolean _failOverAllowed;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Determines whether fail-over is unconditionally allowed.
    *
    * @return
    *    <code>true</code> if fail-over is unconditionally allowed, even if
    *    the request was already received or even processed by the other end,
    *    <code>false</code> otherwise.
    */
   public final boolean isFailOverAllowed() {
      _doorman.enterAsReader();
      boolean b = _failOverAllowed;
      _doorman.leaveAsReader();
      return b;
   }

   /**
    * Configures whether fail-over is unconditionally allowed.
    *
    * @param allowed
    *    <code>true</code> if fail-over is unconditionally allowed, even if
    *    the request was already received or even processed by the other end,
    *    <code>false</code> otherwise.
    */
   public final void setFailOverAllowed(boolean allowed) {
      _doorman.enterAsWriter();
      _failOverAllowed = allowed;
      _doorman.leaveAsWriter();
   }
}
