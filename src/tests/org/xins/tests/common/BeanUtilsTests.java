/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common;

import com.mycompany.allinone.types.Salutation;
import java.util.Properties;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.BeanUtils;

import com.mycompany.allinone.capi.SimpleTypesRequest;
import com.mycompany.allinone.capi.DefinedTypesRequest;

/**
 * Tests for class <code>BeanUtils</code>
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class BeanUtilsTests extends TestCase {

   /**
    * Constructs a new <code>BeanUtilsTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public BeanUtilsTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(BeanUtilsTests.class);
   }

   public void testPopulate() throws Exception {

      // Populate from request to pojo
      SimpleTypesRequest request = new SimpleTypesRequest();
      request.setInputText("Test123");
      SimplePojo pojo = new SimplePojo();
      Object pojo2 = BeanUtils.populate(request, pojo);
      assertEquals("Test123", pojo.getInputText());
      assertEquals("Test123", ((SimplePojo) pojo2).getInputText());
      assertEquals(pojo, pojo2);

      // Populate from pojo to request
      pojo.setInputText("Another test");
      Object request2 = BeanUtils.populate(pojo, request);
      assertEquals("Another test", request.getInputText());
      assertEquals("Another test", ((SimpleTypesRequest) request2).getInputText());
      assertEquals(request, request2);
   }

   public void testPopulateWithMapping() throws Exception {

      // Populate from request to pojo
      Properties mapping1 = new Properties();
      mapping1.setProperty("InputBoolean", "AlmostBoolean");
      SimpleTypesRequest request = new SimpleTypesRequest();
      request.setInputText("Test123");
      request.setInputBoolean(Boolean.TRUE);
      SimplePojo pojo = new SimplePojo();

      // First without the mapping
      BeanUtils.populate(request, pojo);
      assertFalse(pojo.getAlmostBoolean().booleanValue());

      // Now with the mapping
      BeanUtils.populate(request, pojo, mapping1);
      assertTrue(pojo.getAlmostBoolean().booleanValue());
   }

   public void testPopulateWithConvertion() throws Exception {

      // Boolean to String
      Properties mapping1 = new Properties();
      mapping1.setProperty("InputBoolean", "InputText");
      SimpleTypesRequest request = new SimpleTypesRequest();
      request.setInputBoolean(Boolean.TRUE);
      SimplePojo pojo = new SimplePojo();
      BeanUtils.populate(request, pojo, mapping1);
      assertEquals("true", pojo.getInputText());

      // String to Boolean
      Properties mapping2 = new Properties();
      mapping2.setProperty("InputText", "InputBoolean");
      pojo.setInputText("true");
      BeanUtils.populate(pojo, request, mapping2);
      assertTrue(request.getInputBoolean().booleanValue());

      // String to Boolean with invalid value -> unchanged value
      pojo.setInputText("almost true");
      BeanUtils.populate(pojo, request, mapping2);
      assertTrue(request.getInputBoolean().booleanValue());
   }

   public void testTypeConvertion() throws Exception {

      // Boolean to boolean
      Properties mapping1 = new Properties();
      mapping1.setProperty("InputBoolean", "RealBoolean");
      SimpleTypesRequest request = new SimpleTypesRequest();
      request.setInputBoolean(Boolean.TRUE);
      SimplePojo pojo = new SimplePojo();
      BeanUtils.populate(request, pojo, mapping1);
      assertTrue(pojo.getRealBoolean());

      // String to EnumItem
      SimplePojo pojo2 = new SimplePojo();
      pojo2.setInputText("Mister");
      DefinedTypesRequest request2 = new DefinedTypesRequest();
      Properties mapping2 = new Properties();
      mapping2.setProperty("InputText", "InputSalutation");
      BeanUtils.populate(pojo2, request2, mapping2);
      assertEquals(Salutation.MISTER, request2.getInputSalutation());
   }

   public class SimplePojo {

      private String _inputText;
      private Boolean _almostBoolean = Boolean.FALSE;
      private boolean _realBoolean = false;
      private int _simpleInt;

      public void setInputText(String inputText) {
         _inputText = inputText;
      }

      public String getInputText() {
         return _inputText;
      }

      public void setAlmostBoolean(Boolean almostBoolean) {
         _almostBoolean = almostBoolean;
      }

      public Boolean getAlmostBoolean() {
         return _almostBoolean;
      }

      public void setRealBoolean(boolean realBoolean) {
         _realBoolean = realBoolean;
      }

      public boolean getRealBoolean() {
         return _realBoolean;
      }

      public void setSimpleInt(int anInt) {
         _simpleInt = anInt;
      }

      public int getSimpleInt() {
         return _simpleInt;
      }
   }
}
