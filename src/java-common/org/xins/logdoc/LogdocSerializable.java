/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.logdoc;

/**
 * Interface for objects that support serialization for logdoc.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public interface LogdocSerializable {

   /**
    * Serializes this object for logging to the specified string buffer.
    *
    * <p>If the argument is null, then a <code>NullPointerException</code>
    * should be thrown by the implementation. This can just be accomplished by
    * dereferencing it, for example:
    *
    * <blockquote><pre>buffer.append("Something");</pre></blockquote>
    *
    * <p>Implementations should use {@link LogCentral#getLocale()} to
    * determine which locale (language) to choose.
    *
    * @param buffer
    *    the {@link LogdocStringBuffer} to serialize to, cannot be
    *    <code>null</code>.
    *
    * @throws NullPointerException
    *    if <code>buffer == null</code>.
    */
   void serialize(LogdocStringBuffer buffer) throws NullPointerException;
}
