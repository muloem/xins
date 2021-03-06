/*
 * $Id$
 *
 * Copyright 2003-2008 Online Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.collections;

import java.io.Serializable;
import java.util.Iterator;

/**
 * Property reader. Object that implement this interface allow read-only
 * access to a set of property entries. Each entry consists of a name and a
 * value. Both are {@link String} objects.
 *
 * <p>Some implementations of this interface also support changing the
 * underlying set of property entries. In this case, setting a property value
 * to <code>null</code> has the same effect as removing the property
 * altogether.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 *
 * @see PropertyReaderUtils
 */
public interface PropertyReader extends Serializable {

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
    * Gets an iterator that iterates over all the property names. The
    * {@link Iterator} will return only {@link String} instances.
    *
    * @return
    *    the {@link Iterator} that will iterate over all the names, never
    *    <code>null</code>.
    */
   Iterator getNames();

   /**
    * Returns the number of entries.
    *
    * @return
    *    the size, always &gt;= 0.
    */
   int size();
}
