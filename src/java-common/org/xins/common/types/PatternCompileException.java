/*
 * $Id$
 *
 * Copyright 2003-2008 Online Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.types;

/**
 * Exception thrown to indicate a pattern string could not be compiled.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public class PatternCompileException extends RuntimeException {

   /**
    * Creates a new <code>PatternCompileException</code>.
    *
    * @param message
    *    the detail message, or <code>null</code>.
    */
   protected PatternCompileException(String message) {
      super(message);
   }
}
