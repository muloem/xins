/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.server;

import java.io.*;
import java.net.*;
import java.util.*;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.OptionsMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONArray;
import org.json.JSONObject;

import org.xins.common.collections.BasicPropertyReader;
import org.xins.common.http.HTTPCallRequest;
import org.xins.common.http.HTTPCallResult;
import org.xins.common.http.HTTPServiceCaller;
import org.xins.common.service.TargetDescriptor;
import org.xins.common.text.HexConverter;
import org.xins.common.text.ParseException;
import org.xins.common.xml.Element;
import org.xins.common.xml.ElementParser;

import org.xins.tests.AllTests;

/**
 * Tests for calling conventions.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:anthony.goubard@orange-ftgroup.com">Anthony Goubard</a>
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class JSONRPCCallingConventionTests extends TestCase {

   /**
    * Constructs a new <code>XMLCallingConventionTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public JSONRPCCallingConventionTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(JSONRPCCallingConventionTests.class);
   }

   /**
    * Test the JSON calling convention using the 1.0 specifications.
    */
   public void testJSONCallingConvention1_0() throws Throwable {
      String randomLong = HexConverter.toHexString(new Random().nextLong());
      String randomFive = randomLong.substring(0, 5);

      // Successful call
      postJSONRPCRequest1_0(randomFive, true);

      // Unsuccessful call
      postJSONRPCRequest1_0(randomFive, false);
   }

   /**
    * Test the JSON calling convention using the 1.1 specifications.
    */
   public void testJSONCallingConvention1_1() throws Throwable {
      String randomLong = HexConverter.toHexString(new Random().nextLong());
      String randomFive = randomLong.substring(0, 5);

      // Successful call
      postJSONRPCRequest1_1(randomFive, true);

      // Unsuccessful call
      postJSONRPCRequest1_1(randomFive, false);
   }

   /**
    * Test the system.descripbe function.
    */
   public void testSystemDescribe() throws Throwable {
      String destination = AllTests.url() + "allinone/?_convention=_xins-jsonrpc";
      String input = "{ \"version\" : \"1.1\", \"method\"  : \"system.describe\" }";
      String jsonResult = CallingConventionTests.postData(destination, input, "application/json", 200);
      JSONObject jsonObject = new JSONObject(jsonResult);
      String sdversion = jsonObject.getString("sdversion");
      assertEquals("1.0", sdversion);
      String name = jsonObject.getString("name");
      assertEquals("allinone", name);
   }

   /**
    * Posts JSON-RPC 1.0 request.
    *
    * @param randomFive
    *    A randomly generated String.
    * @param success
    *    <code>true</code> if the expected result should be successful,
    *    <code>false</code> otherwise.
    *
    * @throws Throwable
    *    If anything goes wrong.
    */
   private void postJSONRPCRequest1_0(String randomFive, boolean success) throws Throwable {
      String destination = AllTests.url() + "allinone/?_convention=_xins-jsonrpc";
      String input = "{ \"method\": \"ResultCode\", \"params\": [\"true\", \"" + randomFive + "\"], \"id\": 18}";
      String jsonResult = CallingConventionTests.postData(destination, input, "application/json", 200);
      System.err.println("1_0: " + jsonResult);
      JSONObject jsonObject = new JSONObject(jsonResult);
      int id = jsonObject.getInt("id");
      assertEquals(18, id);
      Object error = jsonObject.opt("error");
      JSONObject result = jsonObject.optJSONObject("result");
      if (success) {
         String outputText = result.getString("outputText");
         assertEquals("Incorrect result received: " + outputText, randomFive + " added.", outputText);
         assertNull(error);
      } else {
         assertNotNull(error);
         assertEquals("Incorrect error code received: " + error, "AlreadySet", error);
         assertNull(result);
      }
   }

   /**
    * Posts JSON-RPC 1.1 request.
    *
    * @param randomFive
    *    A randomly generated String.
    * @param success
    *    <code>true</code> if the expected result should be successful,
    *    <code>false</code> otherwise.
    *
    * @throws Throwable
    *    If anything goes wrong.
    */
   private void postJSONRPCRequest1_1(String randomFive, boolean success) throws Throwable {
      String destination = AllTests.url() + "allinone/?_convention=_xins-jsonrpc";
      String input = "{ \"version\" : \"1.1\", \"method\"  : \"ResultCode\", \"params\"  : { \"useDefault\" : false, \"inputText\" : \"" + randomFive + "\" } }";
      String jsonResult = CallingConventionTests.postData(destination, input, "application/json", 200);
      System.err.println("1_1: " + jsonResult);
      JSONObject jsonObject = new JSONObject(jsonResult);
      String version = jsonObject.getString("version");
      assertEquals("1.1", version);
      JSONObject error = jsonObject.optJSONObject("error");
      if (success) {
         JSONObject result = jsonObject.getJSONObject("result");
         String outputText = result.getString("outputText");
         assertEquals("Incorrect result received: " + outputText, randomFive + " added.", outputText);
         assertNull(error);
      } else {
         assertNotNull(error);
         String errorName = error.getString("name");
         assertEquals("Incorrect error code received: " + errorName, "AlreadySet", errorName);
         int errorCode = error.getInt("code");
         assertTrue(errorCode >=100 && errorCode <= 999);
         String errorMessage = error.getString("message");
         assertEquals("Unexpected error message: " + errorMessage, "The parameter has already been given.", errorMessage);
         JSONObject errorParams = error.getJSONObject("error");
         int count = errorParams.getInt("count");
         assertTrue(count > 0);
      }
   }
}
