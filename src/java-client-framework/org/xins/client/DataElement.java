/*
 * $Id$
 */
package org.xins.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * The data element received from the server when any.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 *
 * @since XINS 0.203
 */
public class DataElement {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   private List _children = new ArrayList();

   private Properties _attributes = new Properties();

   private String _pcdata;

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   void addChild(DataElement element) {
      _children.add(element);
   }

   void addAttribute(String key, String value) {
      _attributes.put(key, value);
   }

   void setPCData(String pcdata) {
      _pcdata = pcdata;
   }
}