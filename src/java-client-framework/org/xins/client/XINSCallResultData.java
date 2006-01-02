/*
 * $Id$
 *
 * Copyright 2003-2006 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client;

import org.xins.common.collections.PropertyReader;

/**
 * Data part of a XINS call result.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.0.0
 */
public interface XINSCallResultData {

   /**
    * Returns the error code. If <code>null</code> is returned the call was
    * successful and thus no error code was returned. Otherwise the call was
    * unsuccessful.
    *
    * <p>This method will never return an empty string, so if the result is
    * not <code>null</code>, then it is safe to assume the length of the
    * string is at least 1 character.
    *
    * @return
    *    the returned error code, or <code>null</code> if the call was
    *    successful.
    */
   String getErrorCode();

   /**
    * Gets all parameters.
    *
    * @return
    *    a {@link PropertyReader} with all parameters, or <code>null</code> if
    *    there are none.
    */
   PropertyReader getParameters();

   /**
    * Returns the optional extra data. The data is an XML {@link DataElement},
    * or <code>null</code>.
    *
    * @return
    *    the extra data as an XML {@link DataElement}, can be
    *    <code>null</code>;
    */
   DataElement getDataElement();
}
