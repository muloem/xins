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
 * Patterns type. An enumeration type only accepts values that match a certain
 * pattern.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public abstract class PatternType extends Type {

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
    * Creates a new <code>PatternType</code> instance. The name of the type
    * needs to be specified. The value class (see
    * {@link Type#getValueClass()}) is set to {@link String String.class}.
    *
    * @param name
    *    the name of the type, not <code>null</code>.
    *
    * @param pattern
    *    the regular expression the values must match, or <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null || pattern == null</code>.
    */
   protected PatternType(String name, String pattern)
   throws IllegalArgumentException {
      super(name, String.class);

      _pattern = pattern;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The regular expression the values must match. Cannot be
    * <code>null</code>.
    */
   private final String _pattern;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public String getPattern() {
      return _pattern;
   }
}
