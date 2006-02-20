/*
 * $Id$
 *
 * Copyright 2003-2006 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.servlet.container;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

/**
 * A user session.
 *
 * @version $Revision$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public class XINSHttpSession implements HttpSession {
   
   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------
   
   /**
    * Creates a new instance of XINSHttpSession.
    */
   XINSHttpSession() {
   }
   
   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The session attributes.
    */
   private Hashtable _attributes = new Hashtable();

   /**
    * The creation time of the session.
    */
   private long _creationTime = System.currentTimeMillis();

   /**
    * The ID of the session.
    */
   private int _sessionID = new Random().nextInt();


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public void removeValue(String value) {
      throw new UnsupportedOperationException();
   }

   public void removeAttribute(String name) {
      _attributes.remove(name);
   }

   public Object getAttribute(String name) {
      return _attributes.get(name);
   }

   public Object getValue(String name) {
      return getAttribute(name);
   }

   public void setMaxInactiveInterval(int i) {
   }

   public void setAttribute(String name, Object value) {
      _attributes.put(name, value);
   }

   public void putValue(String name, Object value) {
      setAttribute(name, value);
   }

   public Enumeration getAttributeNames() {
      return _attributes.keys();
   }

   public long getCreationTime() {
      return _creationTime;
   }

   public String getId() {
      return "" + _sessionID;
   }

   public long getLastAccessedTime() {
      throw new UnsupportedOperationException();
   }

   public int getMaxInactiveInterval() {
      throw new UnsupportedOperationException();
   }

   public ServletContext getServletContext() {
      throw new UnsupportedOperationException();
   }

   public HttpSessionContext getSessionContext() {
      throw new UnsupportedOperationException();
   }

   public String[] getValueNames() {
      throw new UnsupportedOperationException();
   }

   public void invalidate() {
      throw new UnsupportedOperationException();
   }

   public boolean isNew() {
      throw new UnsupportedOperationException();
   }
}