/*
 * $Id$
 */
package org.xins.server;

/**
 * Constants referring to all existing <code>ResponderState</code>
 * instances.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public interface ResponderStates {

   /**
    * Uninitialized state. In this state no output can be written.
    */
   static final ResponderState UNINITIALIZED = new ResponderState("uninitialized");

   /**
    * Initial state, before response output is started.
    */
   static final ResponderState BEFORE_START = new ResponderState("before start");

   /**
    * State active when the output parameters can be written. This states
    * comes after {@link #BEFORE_START}.
    */
   static final ResponderState WITHIN_PARAMS = new ResponderState("within params");

   /**
    * State within the data section when a start tag has been opened, but not
    * closed yet. This state comes after {@link #WITHIN_PARAMS}.
    */
   static final ResponderState START_TAG_OPEN = new ResponderState("start tag open");

   /**
    * State within the data section after a start tag has been closed, but the
    * root element has not been closed yet. This state comes after
    * {@link #START_TAG_OPEN}.
    */
   static final ResponderState WITHIN_ELEMENT = new ResponderState("within element");

   /**
    * Final state, after response output is finished.
    */
   static final ResponderState AFTER_END = new ResponderState("after end");

   /**
    * Error state. Entered if an exception is thrown within an output method.
    */
   static final ResponderState ERROR = new ResponderState("error");
}
