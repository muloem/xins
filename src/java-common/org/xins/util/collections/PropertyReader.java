/*
 * $Id$
 */
package org.xins.util.collections;

/**
 * Property reader.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public interface PropertyReader {

   /**
    * Gets the value of the property with the specified name.
    *
    * @param name
    *    the name of the property, cannot be <code>null</code>.
    *
    * @return
    *    the value of the property, or <code>null</code> if it is not set.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null</code>.
    */
   public String get(String name) throws IllegalArgumentException;
}
