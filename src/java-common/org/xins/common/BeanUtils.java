/*
 * $Id$
 *
 * Copyright 2003-2006 Orange Nederland Breeband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.xins.common.spec.DataSectionElementSpec;
import org.xins.common.spec.FunctionSpec;
import org.xins.common.spec.ParameterSpec;
import org.xins.common.types.EnumType;
import org.xins.common.xml.Element;

/**
 * This class contains some utility methods that fills an object with values
 * from another object.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:anthony.goubard@orange-ft.com">Anthony Goubard</a>
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
    *    the populated object, never <code>null</null>
    */
   public static Object populate(Object source, Object destination) {
      Method[] sourceMethods = source.getClass().getMethods();
      for (int i = 0; i < sourceMethods.length; i++) {
         if (sourceMethods[i].getName().startsWith("get") && sourceMethods[i].getName().length() > 3) {
            Class[] returnType = {sourceMethods[i].getReturnType()};
            String setMethodName = "set" + sourceMethods[i].getName().substring(3);
            try {
               Method setMethod = destination.getClass().getMethod(setMethodName, returnType);
               Object value = sourceMethods[i].invoke(source, null);
               Object[] setParams = {value};
               setMethod.invoke(destination, setParams);
            } catch (Exception nsmex) {
               // Ignore this property
            }
         }
      }
      return destination;
   }

   /**
    * Fills the result object with of the content of the XML element object.
    *
    * @param element
    *    the XML element object, cannot be <code>null</code>.
    * @param result
    *    the object to put the values in, cannot be <code>null</code>.
    * @param functionSpec
    *    the specification of the function for the result object, cannot be <code>null</code>.
    *
    * @return
    *    the result object filled with the values of the element object, never <code>null</code>.
    */
   public static Object xmlToObject(Element element, Object result, FunctionSpec functionSpec) {
      return xmlToObject(element, result, functionSpec, true);
   }

   /**
    * Fills the result object with of the content of the XML element object.
    *
    * @param element
    *    the XML element object, cannot be <code>null</code>.
    * @param result
    *    the object to put the values in, cannot be <code>null</code>.
    * @param functionSpec
    *    the specification of the function for the result object, cannot be <code>null</code>.
    * @param topLevel
    *    <code>true</code> if the element passed is the top element, 
    *    <code>false</code> if it is a sub-element.
    *
    * @return
    *    the result object filled with the values of the element object, never <code>null</code>.
    */
   private static Object xmlToObject(Element element, Object result, FunctionSpec functionSpec, boolean topLevel) {

      // Short-circuit if arg is null
      if (element == null) {
         return result;
      }
      String elementName = element.getLocalName();
      if (topLevel && elementName.equals("data")) {
         Iterator itChildren = element.getChildElements().iterator();
         while (itChildren.hasNext()) {
            Element nextChild = (Element) itChildren.next();
            xmlToObject(element, result, functionSpec, true);
         }
      } else {
         try {
            String hungarianName = elementName.substring(0, 1).toUpperCase() + elementName.substring(1);
            Class[] argsClasses = {getElementClass(elementName, result)};
            Method addMethod = result.getClass().getMethod("add" + hungarianName, argsClasses);
            Object childElement = elementToObject(element, result, functionSpec, topLevel);
            Object[] addArgs = { childElement };
            if (childElement != null) {
               addMethod.invoke(result, addArgs);
            }
         } catch (Exception ex) {
            ex.printStackTrace();
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
    * @param functionSpec
    *    the specification of the function for the result object, cannot be <code>null</code>.
    * @param topLevel
    *    <code>true</code> if the element passed is the top element, 
    *    <code>false</code> if it is a sub-element.
    *
    * @return
    *    the result object filled with the values of the element object, never <code>null</code>.
    */
   private static Object elementToObject(Element element, Object result, FunctionSpec functionSpec, boolean topLevel) {
      String elementName = element.getLocalName();
      //String newElementClassName = result.getClass().getName() + "." + elementName;
      Object newElement = null;
      DataSectionElementSpec elementSpec = null;
      try {
         newElement = getElementClass(elementName, result).newInstance();
         if (topLevel) {
            elementSpec = functionSpec.getOutputDataSectionElement(elementName);
         } else {
            String parentName = result.getClass().getName();
            int lastDot = parentName.lastIndexOf('$');
            parentName = parentName.substring(lastDot + 1, lastDot + 2).toLowerCase() + parentName.substring(lastDot + 2);
            elementSpec = functionSpec.getOutputDataSectionElement(parentName).getSubElement(elementName);
         }
      } catch (Exception ex) {
         ex.printStackTrace();
         return null;
      }

      // Copy the attributes
      Iterator itAttr = element.getAttributeMap().entrySet().iterator();
      while (itAttr.hasNext()) {
         Map.Entry attr = (Map.Entry) itAttr.next();
         String name  = ((Element.QualifiedName) attr.getKey()).getLocalName();
         String value = (String) attr.getValue();
         try {
            ParameterSpec attributeSpec = elementSpec.getAttribute(name);
            Method convertionMethod = null;
            if (attributeSpec.getType() instanceof EnumType) {
               convertionMethod = attributeSpec.getType().getClass().getMethod("getItemByValue", STRING_CLASS);
            } else if (attributeSpec.isRequired()) {
               convertionMethod = attributeSpec.getType().getClass().getMethod("fromStringForRequired", STRING_CLASS);
            } else {
               convertionMethod = attributeSpec.getType().getClass().getMethod("fromStringForOptional", STRING_CLASS);
            }
            Class[] convertionMethodReturnClass = new Class[1];
            if (convertionMethod.getReturnType().equals(Boolean.class)) {
               convertionMethodReturnClass[0] = Boolean.TYPE;
            } else {
               convertionMethodReturnClass[0] = convertionMethod.getReturnType();
            }
            Method setMethod = newElement.getClass().getMethod("set" + name.substring(0, 1).toUpperCase() + name.substring(1), convertionMethodReturnClass);
            Object[] convertionArgs = { value };
            Object[] setArgs = { convertionMethod.invoke(null, convertionArgs) };
            setMethod.invoke(newElement, setArgs);
         } catch (Exception ex) {
            ex.printStackTrace();
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
            ex.printStackTrace();
         }
      }

      // Copy the children
      Iterator itChildren = element.getChildElements().iterator();
      while (itChildren.hasNext()) {
         Element child = (Element) itChildren.next();
         xmlToObject(child, newElement, functionSpec, false);
      }

      return newElement;
   }
}