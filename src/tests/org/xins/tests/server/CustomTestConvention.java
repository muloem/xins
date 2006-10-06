/*
 * $Id$
 *
 * Copyright 2003-2006 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xins.common.collections.BasicPropertyReader;
import org.xins.server.API;
import org.xins.server.CustomCallingConvention;
import org.xins.server.FunctionNotSpecifiedException;
import org.xins.server.FunctionRequest;
import org.xins.server.FunctionResult;
import org.xins.server.InvalidRequestException;

import org.xins.tests.AllTests;

/**
 * Custom calling convention for testing purposes.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:anthony.goubard@orange-ft.com">Anthony Goubard</a>
 */
public class CustomTestConvention extends CustomCallingConvention {

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Creates a new <code>CustomTestConvention</code> instance.
    */
   public CustomTestConvention() {
      throw new IllegalStateException("This constructor should never be called, instead the variant that takes an API instance should be called.");
   }

   /**
    * Creates a new <code>CustomTestConvention</code> instance, for the
    * specified <code>API</code>.
    */
   public CustomTestConvention(API api) {
      if (api == null) {
         throw new IllegalArgumentException("api == null");
      } else if (api != getAPI()) {
         throw new IllegalStateException("api != getAPI()");
      }
   }

   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   protected FunctionRequest convertRequestImpl(HttpServletRequest httpRequest)
   throws InvalidRequestException, FunctionNotSpecifiedException {
      String[] query = httpRequest.getParameterValues("query");
      if (query == null) {
         throw new InvalidRequestException("Expected input parameter \"query\" to be set.");
      } else if (query.length > 1) {
         throw new InvalidRequestException("Multiple values for input parameter \"query\": \"" + query[0] + "\" and \"" + query[1] + "\".");
      }

      BasicPropertyReader properties = new BasicPropertyReader();
      properties.set("inputText", query[0]);
      properties.set("useDefault", "false");
      return new FunctionRequest("ResultCode", properties, null);
   }

   protected void convertResultImpl(FunctionResult xinsResult,
         HttpServletResponse httpResponse, HttpServletRequest httpRequest) throws IOException {

      if (xinsResult.getErrorCode() != null) {
         httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      } else {
         httpResponse.setStatus(HttpServletResponse.SC_OK);
         PrintWriter out = httpResponse.getWriter();
         out.print("Done.");
         out.close();
      }
   }

}
