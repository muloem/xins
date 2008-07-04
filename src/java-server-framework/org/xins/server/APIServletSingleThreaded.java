/*
 * $Id$
 *
 * Copyright 2003-2008 Online Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import javax.servlet.SingleThreadModel;

/**
 * This class is similar to APIServlet except that it implements the javax.servlet.SingleThreadModel
 * to indique that only 1 thread can handle only 1 request at a time.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 2.2
 */
public class APIServletSingleThreaded extends APIServlet implements SingleThreadModel {
}
