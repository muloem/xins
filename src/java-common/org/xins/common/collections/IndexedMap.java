/*
 * $Id$
 */
package org.xins.common.collections;

import java.util.Map;

/**
 * Indexed map.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public interface IndexedMap extends Map {

   /**
    * Gets the index for the specified key.
    *
    * @param key
    *    the key to look up, can be <code>null</code>.
    *
    * @return
    *    the index of the key, or a negative value if the key cannot be found.
    */
   public int getIndexForKey(Object key);

   /**
    * Gets the key at the specified index.
    *
    * @param index
    *    the index, must be &gt;= 0 and &lt; {@link #size()}.
    *
    * @return
    *    the key at the specified index, can be <code>null</code>.
    *
    * @throws IndexOutOfBoundsException
    *    if <code>index &lt; 0 || index &gt;= </code>{@link #size()}.
    */
   public Object getKey(int index)
   throws IndexOutOfBoundsException;

   /**
    * Gets the value at the specified index.
    *
    * @param index
    *    the index, must be &gt;= 0 and &lt; {@link #size()}.
    *
    * @return
    *    the value at the specified index, can be <code>null</code>.
    *
    * @throws IndexOutOfBoundsException
    *    if <code>index &lt; 0 || index &gt;= </code>{@link #size()}.
    */
   public Object getValue(int index)
   throws IndexOutOfBoundsException;
}
