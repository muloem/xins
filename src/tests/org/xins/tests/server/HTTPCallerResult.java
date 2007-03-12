/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Result returned by executing a HTTP request using the HTTPCaller.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 * @author <a href="mailto:anthony.goubard@orange-ftgroup.com">Anthony Goubard</a>
 */
public class HTTPCallerResult {

   HTTPCallerResult() {
   }

   private String _status;
   private String _body;
   private HashMap _headers = new HashMap();

   void setStatus(String status) {
      _status = status;
   }

   public String getStatus() {
      return _status;
   }

   void setBody(String body) {
      _body = body;
   }

   public String getBody() {
      return (_body == null) ? "" : _body;
   }

   void addHeader(String key, String value) {

      // Always convert the key to upper case
      key = key.toUpperCase();

      // Always trim the value
      value = value.trim();

      // Store the value in the list associated by key
      List list = (List) _headers.get(key);
      if (list == null) {
         list = new ArrayList();
         _headers.put(key, list);
      }
      list.add(value);
   }

   public List getHeaderValues(String key) {
      Object value = _headers.get(key.toUpperCase());
      return (value == null) ? new ArrayList() : (List) value;
   }
}

