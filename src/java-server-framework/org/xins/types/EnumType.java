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
import nl.wanadoo.util.MandatoryArgumentChecker;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

/**
 * Enumeration type. An enumeration type only accepts a limited set of
 * possible values.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<A href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</A>)
 */
public abstract class EnumType extends Type {

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
    * Creates a new <code>EnumType</code> instance. The name of the type needs
    * to be specified. The value class (see {@link Type#getValueClass()}) is
    * set to {@link String String.class}.
    *
    * @param name
    *    the name of the type, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null</code>.
    */
   protected EnumType(String name) throws IllegalArgumentException {
      super(name, String.class);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   protected void checkValueImpl(String value) throws TypeValueException {
      // TODO
   }

   protected Object fromStringImpl(String value) {
      return value;
   }
}
