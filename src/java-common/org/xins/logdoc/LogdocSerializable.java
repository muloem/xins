/*
 * $Id$
 */
package org.xins.logdoc;

/**
 * Interface for objects that support serialization for logdoc.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 0.198
 */
public interface LogdocSerializable {

   /**
    * Serializes this object for logging to the specified string buffer.
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
