/*
 * $Id$
 */
package org.xins.types;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.xins.util.MandatoryArgumentChecker;

/**
 * Item in an enumeration type.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<A href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</A>)
 */
public final class EnumItem extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Creates a new <code>EnumItem</code>.
    *
    * @param name
    *    the symbolic (friendly) name for the enumeration value, not
    *    <code>null</code>.
    *
    * @param value
    *    the actual value of the enumeration item, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null || value == null</code>.
    */
   public EnumItem(String name, String value)
   throws IllegalArgumentException {
      MandatoryArgumentChecker.check("name", name, "value", value);

      _name  = name;
      _value = value;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The symbolic (friendly) name for the enumeration value. Cannot be
    * <code>null</code>.
    */
   private final String _name;

   /**
    * The actual value of this enumeration item. Cannot <code>null</code>.
    */
   private final String _value;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Gets the symbolic (friendly) name for the enumeration value.
    *
    * @return
    *    the symbolic name, not <code>null</code>.
    */
   public final String getName() {
      return _name;
   }

   /**
    * Gets the value for this enumeration item.
    *
    * @return
    *    the actual value of this enumeration item, not <code>null</code>.
    */
   protected String getValue() {
      return _value;
   }
}
