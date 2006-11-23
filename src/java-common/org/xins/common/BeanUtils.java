/*
 * $Id$
 *
 * Copyright 2003-2006 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.xins.common.types.EnumItem;
import org.xins.common.xml.Element;

/**
 * This class contains some utility methods that fills an object with values
 * from another object.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:anthony.goubard@orange-ftgroup.com">Anthony Goubard</a>
 *
 * @since XINS 1.5.0.
 */
public class BeanUtils {

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Constant used to identified some methods.
    */
   private final static Class[] STRING_CLASS = {String.class};


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Get the values returned by the get methods of the source object and
    * call the set method of the destination object for the same property.
    *
    * e.g. String getFirstName() value of the source object will be used to
    * invoke setFirstName(String) of the destination object.
    *
    * If the no matching set method exists or the set method parameter is not the
    * same type as the object returned by the get method, the property is ignored.
    *
    * @param source
    *    the source object to get the values from. Cannot be <code>null</code>.
    * @param destination
    *    the destination object to put the values in. Cannot be <code>null</code>.
    *
    * @return
    *    the populated object, never <code>null</code>.
    */
   public static Object populate(Object source, Object destination) {
      return populate(source, destination, null);
   }

   /**
    * Get the values returned by the get methods of the source object and
    * call the set method of the destination object for the same property.
    *
    * e.g. String getFirstName() value of the source object will be used to
    * invoke setFirstName(String) of the destination object.
    *
    * If the no matching set method exists or the set method parameter is not the
    * same type as the object returned by the get method, the property is ignored.
    *
    * @param source
    *    the source object to get the values from. Cannot be <code>null</code>.
    * @param destination
    *    the destination object to put the values in. Cannot be <code>null</code>.
    * @param propertiesMapping
    *    the mapping between properties which does not have the same name.
    *
    * @return
    *    the populated object, never <code>null</code>.
    */
   public static Object populate(Object source, Object destination, Properties propertiesMapping) {

      // Go through all get methods of the source object
      Method[] sourceMethods = source.getClass().getMethods();
      for (int i = 0; i < sourceMethods.length; i++) {
         String getMethodName = sourceMethods[i].getName();
         if (getMethodName.startsWith("get") && getMethodName.length() > 3 && !getMethodName.equals("getClass")) {

            // Determine the name of the set method
            String destProperty = sourceMethods[i].getName().substring(3);
            if (propertiesMapping != null && propertiesMapping.getProperty(destProperty) != null) {
               destProperty = propertiesMapping.getProperty(destProperty);
            }
            String setMethodName = "set" + destProperty;

            // Invoke the set method with the value returned by the get method
            try {
               Object value = sourceMethods[i].invoke(source, null);
               if (value != null) {
                  Object setValue = convertObject(value, destination, destProperty);
                  if (setValue != null) {
                     Class setMethodArgClass = getClassForObject(setValue, destination, setMethodName);
                     Class[] returnType = {setMethodArgClass};
                     Method setMethod = destination.getClass().getMethod(setMethodName, returnType);
                     Object[] setParams = {setValue};
                     setMethod.invoke(destination, setParams);
                  }
               }
            } catch (Exception nsmex) {

               // Ignore this property
               Utils.logIgnoredException(nsmex);
            }
         }
      }

      // If the source object has a data section, fill the destination with it
      try {
         Method dataElementMethod = source.getClass().getMethod("dataElement", null);
         Object dataElement = dataElementMethod.invoke(source, null);
         if ("org.xins.client.DataElement".equals(dataElement.getClass().getName())) {
            Method toXMLElementMethod = dataElement.getClass().getMethod("toXMLElement", null);
            Element element = (Element) toXMLElementMethod.invoke(dataElement, null);
            xmlToObject(element, destination);
         }
      } catch (Exception e) {
         // Probably no method found
      }
      return destination;
   }

   /**
    * Converts the value of an object to another object in case that the
    * set method doesn't accept the same obejct as the get method.
    *
    * @param origValue
    *    the original value of the object to be converted, if needed. Cannot be <code>null</code>.
    * @param destination
    *    the destination class containing the set method, cannot be <code>null</code>.
    * @param property
    *    the name of the destination property, cannot be <code>null</code>.
    *
    * @return
    *    the converted object.
    *
    * @throws Exception
    *    if error occurs when using the reflection API.
    */
   private static Object convertObject(Object origValue, Object destination, String property) throws Exception {
      String setMethodName = "set" + property.substring(0, 1).toUpperCase() + property.substring(1);

      // First test if the method with the same class as source exists
      try {
         Class[] idemClass = {origValue.getClass()};
         destination.getClass().getMethod(setMethodName, idemClass);
         return origValue;
      } catch (NoSuchMethodException nsmex) {
         // Ignore, try to find the other methods
      }

      Method[] destMethods = destination.getClass().getMethods();
      for (int i = 0; i < destMethods.length; i++) {
         if (destMethods[i].getName().equals(setMethodName)) {
            Class destClass = destMethods[i].getParameterTypes()[0];

            // Convert a String or an EnumItem to another EnumItem.
            if (EnumItem.class.isAssignableFrom(destClass)) {
               String enumTypeClassName = destClass.getName().substring(0, destClass.getName().length() - 5);
               Object enumType = Class.forName(enumTypeClassName).getDeclaredField("SINGLETON").get(null);
               Method convertionMethod = enumType.getClass().getMethod("getItemByValue", STRING_CLASS);
               Object[] convertParams = {origValue.toString()};
               Object convertedObj = convertionMethod.invoke(null, convertParams);
               return convertedObj;

            // Convert whatever to a String
            } else if (destClass == String.class) {
               return origValue.toString();

            // Convert an Object to a boolean
            } else if (destClass == Boolean.class || destClass == Boolean.TYPE) {
               if ("true".equals(origValue) || Boolean.TRUE.equals(origValue)) {
                  return Boolean.TRUE;
               } else if ("false".equals(origValue) || Boolean.FALSE.equals(origValue)) {
                  return Boolean.FALSE;
               }

            // Convert a String to whatever is asked
            } else if (origValue instanceof String) {
               Method convertionMethod = null;
               try {
                  convertionMethod = destClass.getMethod("valueOf", STRING_CLASS);
               } catch (NoSuchMethodException nsmex) {
                  //Ignore
               }
               try {
                  convertionMethod = destClass.getMethod("fromStringForOptional", STRING_CLASS);
               } catch (NoSuchMethodException nsmex) {
                  //Ignore
               }
               if (convertionMethod != null) {
                  String[] convertParams = {origValue.toString()};
                  Object convertedObj = convertionMethod.invoke(null, convertParams);
                  return convertedObj;
               }
            }
         }
      }

      // No method found
      return null;
   }

   /**
    * Gets the class for an object.
    *
    * In most of cases it will be <code>object.getClass()</code> except for
    * primitive type where it can be the primitive class.
    *
    * @param value
    *    the value of the object.
    *
    * @param destination
    *    the object containing the set method.
    *
    * @param setMethodName
    *    the name of the set method.
    *
    * @return
    *    the {@link java.lang.Class} object to use for the set method, never <code>null</code>.
    */
   private static Class getClassForObject(Object value, Object destination, String setMethodName) {
      Class valueClass = value.getClass();
      if (value instanceof Byte || value instanceof Short || value instanceof Character
            || value instanceof Integer || value instanceof Long || value instanceof Float
            || value instanceof Double || value instanceof Boolean) {
         try {
            Class primitiveClass = (Class) valueClass.getDeclaredField("TYPE").get(value);
            Class[] setArgsClasses = {primitiveClass};
            try {
               destination.getClass().getMethod(setMethodName, setArgsClasses);

               // The destination has a set method associated with the primitive type.
               return primitiveClass;
            } catch (NoSuchMethodException nsmex) {
               // Ignore no primitive set method found
            }
         } catch (IllegalAccessException iaex) {
            // Ignore
         } catch (NoSuchFieldException nsfex) {
            // Ignore
         }
      }
      return valueClass;
   }
   /**
    * Fills the result object with of the content of the XML element object.
    *
    * @param element
    *    the XML element object, cannot be <code>null</code>.
    * @param result
    *    the object to put the values in, cannot be <code>null</code>.
    *
    * @return
    *    the result object filled with the values of the element object, never <code>null</code>.
    */
   public static Object xmlToObject(Element element, Object result) {
      return xmlToObject(element, result, true);
   }

   /**
    * Fills the result object with of the content of the XML element object.
    *
    * @param element
    *    the XML element object, cannot be <code>null</code>.
    * @param result
    *    the object to put the values in, cannot be <code>null</code>.
    * @param topLevel
    *    <code>true</code> if the element passed is the top element,
    *    <code>false</code> if it is a sub-element.
    *
    * @return
    *    the result object filled with the values of the element object, never <code>null</code>.
    */
   private static Object xmlToObject(Element element, Object result, boolean topLevel) {

      // Short-circuit if arg is null
      if (element == null) {
         return result;
      }
      String elementName = element.getLocalName();
      if (topLevel && elementName.equals("data")) {
         Iterator itChildren = element.getChildElements().iterator();
         while (itChildren.hasNext()) {
            Element nextChild = (Element) itChildren.next();
            xmlToObject(nextChild, result, true);
         }
      } else {
         try {
            String hungarianName = elementName.substring(0, 1).toUpperCase() + elementName.substring(1);
            Class[] argsClasses = {getElementClass(elementName, result)};
            Method addMethod = result.getClass().getMethod("add" + hungarianName, argsClasses);
            Object childElement = elementToObject(element, result);
            Object[] addArgs = { childElement };
            if (childElement != null) {
               addMethod.invoke(result, addArgs);
            }
         } catch (Exception ex) {
            Utils.logIgnoredException(ex);
         }
      }

      return result;
   }

   /**
    * Gets the class matching the XML element.
    *
    * @param elementName
    *    the name of the XML element, cannot be <code>null</code>.
    * @param result
    *    the base object to get the class from, cannot be <code>null</code>.
    *
    * @return
    *    the class to used to fill the XML values with, never <code>null</code>
    *
    * @throws ClassNotFoundException
    *    if the class cannot be found.
    */
   private static Class getElementClass(String elementName, Object result) throws ClassNotFoundException {
      String hungarianName = elementName.substring(0, 1).toUpperCase() + elementName.substring(1);
      String elementClassName = result.getClass().getName();
      if (elementClassName.indexOf("$") != -1) {
         elementClassName = elementClassName.substring(0, elementClassName.indexOf("$"));
      }
      elementClassName += "$" + hungarianName;
      Class elementClass = Class.forName(elementClassName);
      return elementClass;
   }

   /**
    * Fills the result object with of the content of the XML element object.
    *
    * @param element
    *    the XML element object, cannot be <code>null</code>.
    * @param result
    *    the object to put the values in, cannot be <code>null</code>.
    *
    * @return
    *    the result object filled with the values of the element object, never <code>null</code>.
    */
   private static Object elementToObject(Element element, Object result) {
      String elementName = element.getLocalName();
      //String newElementClassName = result.getClass().getName() + "." + elementName;
      Object newElement = null;
      try {
         newElement = getElementClass(elementName, result).newInstance();
      } catch (Exception ex) {
         Utils.logIgnoredException(ex);
         return null;
      }

      // Copy the attributes
      Iterator itAttr = element.getAttributeMap().entrySet().iterator();
      while (itAttr.hasNext()) {
         Map.Entry attr = (Map.Entry) itAttr.next();
         String name  = ((Element.QualifiedName) attr.getKey()).getLocalName();
         String value = (String) attr.getValue();
         try {
            Object setArg = convertObject(value, newElement, name);
            Class[] convertionMethodReturnClass = { setArg.getClass() };
            Method setMethod = newElement.getClass().getMethod(
                  "set" + name.substring(0, 1).toUpperCase() + name.substring(1), convertionMethodReturnClass);
            Object[] setArgs = { setArg };
            setMethod.invoke(newElement, setArgs);
         } catch (Exception ex) {
            Utils.logIgnoredException(ex);
         }
      }

      // Copy the character data content
      String text = element.getText();
      if (text != null && text.trim().length() > 0) {
         try {
            Method pcdataMethod = newElement.getClass().getMethod("pcdata", STRING_CLASS);
            Object[] pcdataArgs = { text };
            pcdataMethod.invoke(newElement, pcdataArgs);
         } catch (Exception ex) {
            Utils.logIgnoredException(ex);
         }
      }

      // Copy the children
      Iterator itChildren = element.getChildElements().iterator();
      while (itChildren.hasNext()) {
         Element child = (Element) itChildren.next();
         xmlToObject(child, newElement, false);
      }

      return newElement;
   }
}