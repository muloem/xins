/*
 * $Id$
 *
 * Copyright 2003-2006 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.xins.common.servlet.container.HTTPServletHandler;

/**
 * Combination of all XINS/Java tests.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
public class AllTests extends TestSuite {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   private static final int DEFAULT_PORT = 9123;

   private static final boolean RUN_SERVER = true;

   public static HTTPServletHandler HTTP_SERVER;


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Returns the port (server socket) to start the server on.
    *
    * @return
    *    the port to run the server on.
    *
    * @deprecated
    *    Use {@link #port()} instead.
    */
   public static final int getPort() {
      return DEFAULT_PORT;
   }

   /**
    * Returns the host to use to connect to the server.
    *
    * @return
    *    the host, for example <code>"127.0.0.1"</code>.
    */
   public static final String host() {
      return "127.0.0.1";
   }

   /**
    * Returns the port to use to connect to the server.
    *
    * @return
    *    the port, for example <code>80</code>.
    */
   public static final int port() {
      return DEFAULT_PORT;
   }

   /**
    * Returns the URL to use to connect to the server.
    *
    * @return
    *    the URL to connect to, for example 
    *    <code>"http://127.0.0.1:8080/"</code>, never <code>null</code>.
    */
   public static final String url() {
      return "http://" + host() + ":" + port() + "/";
   }

   /**
    * Returns a test suite with all test cases.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      TestSuite suite = new TestSuite();

      // Start the server
      if (RUN_SERVER) {
         suite.addTestSuite(StartServer.class);
      }

      //
      // Add all tests
      //
      suite.addTestSuite(org.xins.tests.common.ExceptionUtilsTests.class);
      suite.addTestSuite(org.xins.tests.common.MandatoryArgumentCheckerTests.class);
      suite.addTestSuite(org.xins.tests.common.UtilsTests.class);

      suite.addTestSuite(org.xins.tests.common.ant.CallXINSTaskTests.class);

      suite.addTestSuite(org.xins.tests.common.collections.BasicPropertyReaderTests.class);
      suite.addTestSuite(org.xins.tests.common.collections.ChainedMapTests.class);
      suite.addTestSuite(org.xins.tests.common.collections.CollectionUtilsTests.class);
      suite.addTestSuite(org.xins.tests.common.collections.PropertyReaderUtilsTests.class);
      suite.addTestSuite(org.xins.tests.common.collections.ProtectedListTests.class);
      suite.addTestSuite(org.xins.tests.common.collections.ProtectedPropertyReaderTests.class);
      suite.addTestSuite(org.xins.tests.common.collections.StatsPropertyReaderTests.class);

      suite.addTestSuite(org.xins.tests.common.collections.expiry.ExpiryFolderTests.class);
      suite.addTestSuite(org.xins.tests.common.collections.expiry.ExpiryStrategyTests.class);

      suite.addTestSuite(org.xins.tests.common.http.HTTPCallConfigTests.class);
      suite.addTestSuite(org.xins.tests.common.http.HTTPServiceCallerTests.class);

      suite.addTestSuite(org.xins.tests.common.manageable.InitializationExceptionTests.class);

      suite.addTestSuite(org.xins.tests.common.net.IPAddressUtilsTests.class);

      suite.addTestSuite(org.xins.tests.common.spec.AttributeComboTests.class);
      suite.addTestSuite(org.xins.tests.common.spec.APITests.class);
      suite.addTestSuite(org.xins.tests.common.spec.DataSectionElementTests.class);
      suite.addTestSuite(org.xins.tests.common.spec.ErrorCodeTests.class);
      suite.addTestSuite(org.xins.tests.common.spec.FunctionTests.class);
      suite.addTestSuite(org.xins.tests.common.spec.ParamComboTests.class);
      suite.addTestSuite(org.xins.tests.common.spec.ParameterTests.class);

      suite.addTestSuite(org.xins.tests.common.service.DescriptorBuilderTests.class);
      suite.addTestSuite(org.xins.tests.common.service.TargetDescriptorTests.class);
      suite.addTestSuite(org.xins.tests.common.service.GroupDescriptorTests.class);
      suite.addTestSuite(org.xins.tests.common.service.UnsupportedProtocolExceptionTests.class);

      suite.addTestSuite(org.xins.tests.common.servlet.ServletRequestPropertyReaderTests.class);
      suite.addTestSuite(org.xins.tests.common.servlet.container.XINSServletRequestTests.class);

      suite.addTestSuite(org.xins.tests.common.text.DateConverterTests.class);
      suite.addTestSuite(org.xins.tests.common.text.FastStringBufferTest.class);
      suite.addTestSuite(org.xins.tests.common.text.FormatExceptionTests.class);
      suite.addTestSuite(org.xins.tests.common.text.HexConverterTests.class);
      suite.addTestSuite(org.xins.tests.common.text.NonASCIIExceptionTests.class);
      suite.addTestSuite(org.xins.tests.common.text.PatternParserTests.class);
      suite.addTestSuite(org.xins.tests.common.text.ParseExceptionTests.class);
      suite.addTestSuite(org.xins.tests.common.text.SimplePatternParserTests.class);
      suite.addTestSuite(org.xins.tests.common.text.TextUtilsTests.class);
      suite.addTestSuite(org.xins.tests.common.text.URLEncodingTests.class);
      suite.addTestSuite(org.xins.tests.common.text.WhislEncodingTests.class);

      suite.addTestSuite(org.xins.tests.common.types.standard.BooleanTests.class);
      suite.addTestSuite(org.xins.tests.common.types.standard.DateTests.class);
      suite.addTestSuite(org.xins.tests.common.types.standard.DescriptorTests.class);
      suite.addTestSuite(org.xins.tests.common.types.standard.Int8Tests.class);
      suite.addTestSuite(org.xins.tests.common.types.standard.Int16Tests.class);
      suite.addTestSuite(org.xins.tests.common.types.standard.Int32Tests.class);
      suite.addTestSuite(org.xins.tests.common.types.standard.Int64Tests.class);
      suite.addTestSuite(org.xins.tests.common.types.standard.Float32Tests.class);
      suite.addTestSuite(org.xins.tests.common.types.standard.Float64Tests.class);
      suite.addTestSuite(org.xins.tests.common.types.standard.Base64Tests.class);
      suite.addTestSuite(org.xins.tests.common.types.standard.HexTests.class);
      suite.addTestSuite(org.xins.tests.common.types.standard.PropertiesTests.class);
      suite.addTestSuite(org.xins.tests.common.types.standard.TimestampTests.class);
      suite.addTestSuite(org.xins.tests.common.types.standard.URLTests.class);

      suite.addTestSuite(org.xins.tests.common.xml.ElementBuilderTests.class);
      suite.addTestSuite(org.xins.tests.common.xml.ElementParserTests.class);
      suite.addTestSuite(org.xins.tests.common.xml.ElementSerializerTests.class);
      suite.addTestSuite(org.xins.tests.common.xml.SAXParserProviderTests.class);

      suite.addTestSuite(org.xins.tests.client.AllInOneAPITests.class);
      suite.addTestSuite(org.xins.tests.client.CAPIRequestTests.class);
      suite.addTestSuite(org.xins.tests.client.CAPITests.class);
      suite.addTestSuite(org.xins.tests.client.DataElementTests.class);
      suite.addTestSuite(org.xins.tests.client.InvalidRequestTests.class);
      suite.addTestSuite(org.xins.tests.client.InvalidResponseTests.class);
      suite.addTestSuite(org.xins.tests.client.UnacceptableResultXINSCallExceptionTests.class);
      suite.addTestSuite(org.xins.tests.client.XINSCallConfigTests.class);
      suite.addTestSuite(org.xins.tests.client.XINSCallRequestTests.class);
      suite.addTestSuite(org.xins.tests.client.XINSCallResultParserTests.class);
      suite.addTestSuite(org.xins.tests.client.XINSServiceCallerTests.class);

      suite.addTestSuite(org.xins.tests.client.async.CallCAPIThreadTests.class);
      suite.addTestSuite(org.xins.tests.client.async.AsynchronousCallTests.class);

      suite.addTestSuite(org.xins.tests.server.AccessRuleListTests.class);
      suite.addTestSuite(org.xins.tests.server.AccessRuleTests.class);
      suite.addTestSuite(org.xins.tests.server.APITests.class);
      suite.addTestSuite(org.xins.tests.server.APIServletTests.class);
      suite.addTestSuite(org.xins.tests.server.CallingConventionTests.class);
      suite.addTestSuite(org.xins.tests.server.ElementTests.class);
      suite.addTestSuite(org.xins.tests.server.IPFilterTests.class);
      suite.addTestSuite(org.xins.tests.server.MetaFunctionsTests.class);

      suite.addTestSuite(org.xins.tests.xslt.FirstlineXSLTTestCase.class);
      suite.addTestSuite(org.xins.tests.xslt.JavaXSLTTestCase.class);
      suite.addTestSuite(org.xins.tests.xslt.RcsXSLTTestCase.class);
      suite.addTestSuite(org.xins.tests.xslt.ResultcodeUniquenessTestCase.class);
      suite.addTestSuite(org.xins.tests.xslt.WarningXSLTTestCase.class);

      // XXX: Perform just a single test
      //suite.addTestSuite(org.xins.tests.server.CallingConventionTests.class);

      // Stop the server
      if (RUN_SERVER) {
         suite.addTestSuite(StopServer.class);
      }

      return suite;
   }


   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>AllTests</code> object with the specified name.
    * The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test case.
    */
   public AllTests(String name) {
      super(name);
   }


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
