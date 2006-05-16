/*
 * $Id$
 *
 * Copyright 2003-2006 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.ant;

import java.util.Iterator;
import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Property;
import org.xins.common.ant.CallXINSTask;

/**
 * Tests for class <code>CallXINSTask</code>.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public class CallXINSTaskTests extends TestCase {

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(CallXINSTaskTests.class);
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>CallXINSTaskTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public CallXINSTaskTests(String name) {
      super(name);
   }

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public void testCallEcho() throws Exception {
      CallXINSTask callTask = createCallXINSTask();
      callTask.setFunction("Echo");
      callTask.setApiLocation("http://localhost:8080/allinone/");
      Property param = callTask.createParam();
      param.setName("in");
      param.setValue("calling via ant");
      callTask.execute();
      assertEquals("Invalid returned value received", "calling via ant", 
            callTask.getProject().getProperty("out"));
   }

   public void testCallDataSectionWithPrefix() throws Exception {
      CallXINSTask callTask = createCallXINSTask();
      callTask.setFunction("DataSection");
      callTask.setApiLocation("http://localhost:8080/allinone/");
      callTask.setPrefix("anttest");
      Property param = callTask.createParam();
      param.setName("inputText");
      param.setValue("Testing Ant");
      callTask.execute();
      assertEquals("Invalid returned name value received", "superuser", 
            callTask.getProject().getProperty("anttest.data.user.name"));
      assertEquals("Invalid returned address value received", "12 Madison Avenue", 
            callTask.getProject().getProperty("anttest.data.user.address"));
      assertEquals("Invalid returned PCDATA value received", "This user has the root authorisation.", 
            callTask.getProject().getProperty("anttest.data.user"));

   }
   
   /**
    * Creates the Ant CallXINSTask without any parameters set.
    *
    * @return
    *    The CallXINSTask, never <code>null</code>.
    */
   private CallXINSTask createCallXINSTask() {
      Project project = new Project();
      project.init();
      CallXINSTask callTask = new CallXINSTask();
      callTask.setProject(project);
      return callTask;
   }
}
