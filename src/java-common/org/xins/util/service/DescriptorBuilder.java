/*
 * $Id$
 */
package org.xins.util.service;

import java.net.MalformedURLException;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;
import org.xins.util.MandatoryArgumentChecker;
import org.xins.util.collections.InvalidPropertyValueException;
import org.xins.util.collections.MissingRequiredPropertyException;
import org.xins.util.collections.PropertyReader;
import org.xins.util.text.FastStringBuffer;

/**
 * Builder that can build a <code>Descriptor</code> object based on a set of
 * properties.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.105
 */
public final class DescriptorBuilder extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Logger for this class.
    */
   public static final Logger LOG = Logger.getLogger(DescriptorBuilder.class.getName());

   /**
    * Delimiter between tokens within a property value. This is the comma
    * character <code>','</code>.
    */
   public static final char DELIMITER = ',';

   /**
    * Delimiters between tokens within a property value.
    */
   private static final String DELIMITER_AS_STRING = String.valueOf(DELIMITER);

   /**
    * Name identifying an actual service descriptor.
    */
   public static final String SERVICE_DESCRIPTOR_TYPE = "service";

   /**
    * Name identifying a group of service descriptors.
    */
   public static final String GROUP_DESCRIPTOR_TYPE = "group";


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Tokenizes the specified string. The {@link #DELIMITER_AS_STRING} will be
    * used as the token delimiter. Every token will be one element in the
    * returned {@link String} array.
    *
    * @param s
    *    the {@link String} to tokenize, cannot be <code>null</code>.
    *
    * @return
    *    the list of tokens as a {@link String} array, never
    *    <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>s == null</code>.
    */
   private static String[] tokenize(String s)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("s", s);

      // Create a StringTokenizer
      StringTokenizer tokenizer = new StringTokenizer(s, DELIMITER_AS_STRING);

      // Create a new array to store the tokens in
      int count = tokenizer.countTokens();
      String[] tokens = new String[count];

      // Copy all tokens into the array
      for (int i = 0; i < count; i++) {
         tokens[i] = tokenizer.nextToken().trim();
      }

      return tokens;
   }

   /**
    * Builds a <code>Descriptor</code> based on the specified set of
    * properties.
    *
    * @param properties
    *    the properties to read from, cannot be <code>null</code>.
    *
    * @param propertyName
    *    the base for the property names, cannot be <code>null</code>.
    *
    * @return
    *    the {@link Descriptor} that was built, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>properties == null || propertyName == null</code>.
    *
    * @throws MissingRequiredPropertyException
    *    if the property named <code>propertyName</code> cannot be found in
    *    <code>properties</code>, or if a referenced property cannot be found.
    *
    * @throws InvalidPropertyValueException
    *    if the property named <code>propertyName</code> is found in
    *    <code>properties</code>, but the format of this property or the
    *    format of a referenced property is invalid.
    */
   public static Descriptor build(PropertyReader properties,
                                  String         propertyName)
   throws IllegalArgumentException,
          MissingRequiredPropertyException,
          InvalidPropertyValueException {

      // Check preconditions
      MandatoryArgumentChecker.check("properties", properties,
                                     "propertyName", propertyName);
      return build(properties, propertyName, null);
   }

   /**
    * Builds a <code>Descriptor</code> based on the specified set of
    * properties, specifying base property and reference.
    *
    * @param properties
    *    the properties to read from, should not be <code>null</code>.
    *
    * @param baseProperty
    *    the name of the base property, should not be <code>null</code>.
    *
    * @param reference
    *    the name of the reference, relative to the base property, can be
    *    <code>null</code>.
    *
    * @return
    *    the {@link Descriptor} that was built, never <code>null</code>.
    *
    * @throws NullPointerException
    *    if <code>properties == null</code>.
    *
    * @throws MissingRequiredPropertyException
    *    if a required property cannot be found.
    *
    * @throws InvalidPropertyValueException
    *    if the property named <code>propertyName</code> is found in
    *    <code>properties</code>, but the format of this property or the
    *    format of a referenced property is invalid.
    */
   private static Descriptor build(PropertyReader properties,
                                   String         baseProperty,
                                   String         reference)
   throws NullPointerException,
          MissingRequiredPropertyException,
          InvalidPropertyValueException {

      // Determine the property name
      String propertyName = reference == null
                          ? baseProperty
                          : baseProperty + '.' + reference;

      // Get the value of the property
      String value = properties.get(propertyName);
      if (value == null) {
         throw new MissingRequiredPropertyException(propertyName);
      }

      // Tokenize the value
      String[] tokens = tokenize(value);
      int tokenCount = tokens.length;
      if (tokenCount < 3) {
         throw new InvalidPropertyValueException(propertyName, value, "Expected at least 3 tokens.");
      }

      // Determine the type
      String descriptorType = tokens[0];

      // Parse service descriptor
      if (SERVICE_DESCRIPTOR_TYPE.equals(descriptorType)) {
         if (tokenCount != 3) {
            throw new InvalidPropertyValueException(propertyName, value, "Expected URL and time-out.");
         }
         String url = tokens[1];
         int timeOut;
         try {
            timeOut = Integer.parseInt(tokens[2]);
         } catch (NumberFormatException nfe) {
            throw new InvalidPropertyValueException(propertyName, value, "Unable to parse time-out.");
         }

         try {
            return new TargetDescriptor(url, timeOut);
         } catch (MalformedURLException exception) {
            LOG.error("URL \"" + url + "\" is malformed.", exception);
            throw new InvalidPropertyValueException(propertyName, value, "Malformed URL.");
         }

      // Parse group descriptor
      } else if (GROUP_DESCRIPTOR_TYPE.equals(descriptorType)) {

         GroupDescriptor.Type groupType = GroupDescriptor.getType(tokens[1]);
         if (groupType == null) {
            throw new InvalidPropertyValueException(propertyName, value, "Unrecognized group descriptor type.");
         }

         int memberCount = tokenCount - 2;
         Descriptor[] members = new Descriptor[memberCount];
         for (int i = 0; i < memberCount; i++) {
            members[i] = build(properties, baseProperty, tokens[i + 2]);
         }
         return new GroupDescriptor(groupType, members);

      // Unrecognized descriptor type
      } else {
         throw new InvalidPropertyValueException(propertyName, value, "Expected valid descriptor type: either \"" + SERVICE_DESCRIPTOR_TYPE + "\" or \"" + GROUP_DESCRIPTOR_TYPE + "\".");
      }
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>DescriptorBuilder</code>.
    */
   private DescriptorBuilder() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
