/*
 * $Id$
 */
package org.xins.common.collections;

import java.util.Iterator;

import org.xins.logdoc.LogdocSerializable;

/**
 * Property reader.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
public interface PropertyReader
extends LogdocSerializable {

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
   String get(String name) throws IllegalArgumentException;

   /**
    * Gets an iterator that iterates over all the property names.
    *
    * @return
    *    the {@link Iterator} that will iterate over all the names, never
    *    <code>null</code>.
    */
   Iterator getNames();
}
