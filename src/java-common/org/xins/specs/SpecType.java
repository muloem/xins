/*
 * $Id$
 */
package org.xins.specs;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.xins.util.MandatoryArgumentChecker;

/**
 * Type of a component of a XINS API specification.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @see Spec
 */
public abstract class SpecType
extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Perl 5 pattern compiler.
    */
   private static final Perl5Compiler PATTERN_COMPILER = new Perl5Compiler();

   /**
    * Pattern matcher.
    */
   private static final Perl5Matcher PATTERN_MATCHER = new Perl5Matcher();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>SpecType</code> with the specified name.
    *
    * @param typeName
    *    the name for the type, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null</code>.
    */
   SpecType(String typeName) throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("typeName", typeName);

      _typeName    = typeName;
      _nameRE      = null;
      _namePattern = null;
   }

   /**
    * Constructs a new <code>SpecType</code> with the specified name and
    * regular expression for actual components.
    *
    * @param typeName
    *    the name for the type, not <code>null</code>.
    *
    * @param nameRE
    *    the regular expression that names for components must match, or
    *    <code>null</code> if there are no restrictions on the name.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null</code>.
    */
   SpecType(String typeName, String nameRE)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("typeName", typeName);

      _typeName = typeName;
      _nameRE   = nameRE;

      // Compile the regular expression
      if (nameRE != null) {
         try {
            _namePattern = PATTERN_COMPILER.compile(nameRE, Perl5Compiler.READ_ONLY_MASK);
         } catch (MalformedPatternException mpe) {
            throw new Error("The pattern \"" + nameRE + "\" is malformed.");
         }
      } else {
         _namePattern = null;
      }
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The name of the type. Cannot be <code>null</code>.
    */
   private final String _typeName;

   /**
    * The regular expression that names for components must match. Is
    * <code>null</code> if there are no restrictions on the name.
    */
   private final String _nameRE;

   /**
    * The regular expression that names for components must match, converted
    * to a <code>Pattern</code> object. Is never <code>null</code>.
    */
   private final Pattern _namePattern;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the name of the component type.
    *
    * @return
    *    the name, cannot be <code>null</code>.
    */
   public final String getTypeName() {
      return _typeName;
   }

   /**
    * Checks that the specified name for a component of this type matches the
    * criteria for such a name.
    *
    * @param name
    *    the name to check, cannot be <code>null</code>.
    *
    * @return
    *    <code>true</code> if the name is considered valid,
    *
    * @throws IllegalArgumentException
    *    if <code>name == null</code>.
    */
   public final boolean isValidName(String name) {

      // Check preconditions
      MandatoryArgumentChecker.check("name", name);

      if (_namePattern == null) {
         return true;
      } else {
         return PATTERN_MATCHER.matches(name, _namePattern);
      }
   }
}
