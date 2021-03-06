/*
 * $Id$
 *
 * Copyright 2003-2008 Online Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.types.standard;

import org.xins.common.service.DescriptorBuilder;
import org.xins.common.service.GroupDescriptor;
import org.xins.common.service.TargetDescriptor;
import org.xins.common.types.Type;
import org.xins.common.types.TypeValueException;
import org.xins.common.MandatoryArgumentChecker;

/**
 * Standard type <em>_descriptor</em>.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 1.2.0
 */
public class Descriptor extends Type {

   /**
    * The only instance of this class. This field is never <code>null</code>.
    */
   public static final Descriptor SINGLETON = new Descriptor();

   /**
    * Constructs a new <code>Properties</code>.
    * This constructor is private, the field {@link #SINGLETON} should be
    * used.
    */
   private Descriptor() {
      super("_descriptor", org.xins.common.service.Descriptor.class);
   }

   /**
    * Constructs a <code>org.xins.common.service.Descriptor</code> from the specified string
    * which is guaranteed to be non-<code>null</code>.
    *
    * @param string
    *    the string to convert, cannot be <code>null</code>.
    *
    * @return
    *    the {@link org.xins.common.service.Descriptor} object, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>string == null</code>.
    *
    * @throws TypeValueException
    *    if the specified string does not represent a valid value for this
    *    type.
    */
   public static org.xins.common.service.Descriptor fromStringForRequired(String string)
   throws IllegalArgumentException, TypeValueException {

      // Check preconditions
      MandatoryArgumentChecker.check("string", string);

      return (org.xins.common.service.Descriptor) SINGLETON.fromString(string);
   }

   /**
    * Constructs a <code>org.xins.common.service.Descriptor</code> from the specified string.
    *
    * @param string
    *    the string to convert, can be <code>null</code>.
    *
    * @return
    *    the {@link org.xins.common.service.Descriptor}, or <code>null</code> if
    *    <code>string == null</code>.
    *
    * @throws TypeValueException
    *    if the specified string does not represent a valid value for this
    *    type.
    */
   public static org.xins.common.service.Descriptor fromStringForOptional(String string)
   throws TypeValueException {
      return (org.xins.common.service.Descriptor) SINGLETON.fromString(string);
   }

   /**
    * Converts the specified <code>org.xins.common.service.Descriptor</code> to a string.
    *
    * @param value
    *    the value to convert, can be <code>null</code>.
    *
    * @return
    *    the textual representation of the value, or <code>null</code> if and
    *    only if <code>value == null</code>.
    */
   public static String toString(org.xins.common.service.Descriptor value) {

      // Short-circuit if the argument is null
      if (value == null) {
         return null;
      }

      return toString(value, "descriptor");
   }

   /*
    * Converts the specified <code>org.xins.common.service.Descriptor</code> to a string.
    *
    * @param value
    *    the value to convert, can be <code>null</code>.
    * @param prefix
    *    the property prefix.
    *
    * @return
    *    the textual representation of the value, or <code>null</code> if and
    *    only if <code>value == null</code>.
    */
   private static String toString(org.xins.common.service.Descriptor value, String prefix) {
      // Use a buffer to create the string
      StringBuffer buffer = new StringBuffer(255);
      buffer.append(prefix);
      buffer.append("=");
      if (value instanceof GroupDescriptor) {
         GroupDescriptor group = (GroupDescriptor)value;
         org.xins.common.service.Descriptor[] targets = group.getMembers();
         buffer.append("group, ");
         buffer.append(group.getType().toString());
         buffer.append(", ");
         for (int i = 0; i < targets.length; i++) {
            buffer.append(prefix);
            buffer.append(i + 1);
            if (i < targets.length - 1) {
               buffer.append(", ");
            }
         }
         for (int i = 0; i < targets.length; i++) {
            buffer.append('\n');
            String targetPrefix = prefix + "." + prefix + i;
            buffer.append(toString(targets[i], targetPrefix));
         }
      } else if (value instanceof TargetDescriptor) {
         TargetDescriptor target = (TargetDescriptor)value;
         buffer.append("service, ");
         buffer.append(target.getURL());
         buffer.append(", ");
         buffer.append(target.getTotalTimeOut());
         buffer.append(", ");
         buffer.append(target.getConnectionTimeOut());
         buffer.append(", ");
         buffer.append(target.getSocketTimeOut());
      }
      return buffer.toString();
   }

   protected final boolean isValidValueImpl(String string) {

      if (string == null) {
         return false;
      }

      try {
         DescriptorBuilder.build(string);
         return true;
      } catch (Exception ex) {
         return false;
      }
   }

   protected final Object fromStringImpl(String string)
   throws TypeValueException {

      try {
         return DescriptorBuilder.build(string);
      } catch (Exception ex) {
         throw new TypeValueException(SINGLETON, string);
      }
   }

   public final String toString(Object value)
   throws IllegalArgumentException, ClassCastException, TypeValueException {

      // Check preconditions
      MandatoryArgumentChecker.check("value", value);

      // The argument must be a PropertyReader
      return toString((org.xins.common.service.Descriptor) value);
   }

   public String getDescription() {
      return "A XINS descriptor. It can be a single service descriptor or a group descriptor.";
   }
}
