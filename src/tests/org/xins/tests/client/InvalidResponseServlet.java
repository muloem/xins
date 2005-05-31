/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.client;

import java.io.IOException;
import java.io.Writer;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that returns invalid XINS results.
 *
 * @version $Revision$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public class InvalidResponseServlet extends HttpServlet {
   
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
    * Creates a new instance of InvalidResponseServlet
    */
   public InvalidResponseServlet() {
   }
   
   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------
   
   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
   
   /**
    * Handles a request to this servlet (wrapper method). If any of the
    * arguments is <code>null</code>, then the behaviour of this method is
    * undefined.
    *
    * @param request
    *    the servlet request, should not be <code>null</code>.
    *
    * @param response
    *    the servlet response, should not be <code>null</code>.
    *
    * @throws IOException
    *    if there is an error error writing to the response output stream.
    */
   public void service(HttpServletRequest request, HttpServletResponse response)
   throws IOException {
      String function = request.getParameter("_function");
      if (function == null) {
         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         throw new IOException("Invalid request, no \"_function\" parameter passed.");
      }
      if (function.equals("SimpleTypes")) {
         response.setStatus(HttpServletResponse.SC_OK);
         response.setContentType("text/xml;charset=UTF-8");
         Writer writer = response.getWriter();
         writer.write(getInvalidSimpleTypesResult());
         writer.close();
      } else if (function.equals("DefinedTypes")) {
         response.setStatus(HttpServletResponse.SC_OK);
         response.setContentType("text/xml;charset=UTF-8");
         Writer writer = response.getWriter();
         writer.write(getInvalidDefinedTypesResult());
         writer.close();
      } else if (function.equals("ResultCode")) {
         response.setStatus(HttpServletResponse.SC_OK);
         response.setContentType("text/xml;charset=UTF-8");
         Writer writer = response.getWriter();
         writer.write(getInvalidResultCodeResult());
         writer.close();
      } else {
         response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      }
   }
   
   /**
    * Returns an invalid result for the SimpleTypes function.
    *
    * @returns
    *    the invalid result as XML String.
    */
   private String getInvalidSimpleTypesResult() {
      return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
             "<result>"+
             "<param name=\"outputInt\">16</param>"+
             "<param name=\"outputShort\">-1</param>"+
             "<param name=\"outputLong\">14</param>"+
             "<param name=\"outputFloat\">3.5</param>"+
             "<param name=\"outputDouble\">3.1415</param>"+
             "<param name=\"outputDate\">20040621</param>"+
             "</result>";
   }
   
   /**
    * Returns an invalid result for the DefinedTypes function.
    *
    * @returns
    *    the invalid result as XML String.
    */
   private String getInvalidDefinedTypesResult() {
      return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
             "<result/>";
   }
   
   /**
    * Returns an invalid result for the ResultCode function.
    *
    * @returns
    *    the invalid result as XML String.
    */
   private String getInvalidResultCodeResult() {
      return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
             "<result errorcode=\"InvalidNumber\">"+
             "</result>";
   }
}
