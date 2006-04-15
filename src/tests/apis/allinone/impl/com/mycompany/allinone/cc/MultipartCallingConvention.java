/*
 * $Id$
 *
 * Copyright 2003-2006 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package com.mycompany.allinone.cc;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import org.xins.common.Utils;
import org.xins.common.text.ParseException;
import org.xins.common.text.TextUtils;
import org.xins.common.xml.Element;
import org.xins.common.xml.ElementParser;
import org.xins.server.CustomCallingConvention;
import org.xins.server.FunctionNotSpecifiedException;
import org.xins.server.FunctionRequest;
import org.xins.server.FunctionResult;
import org.xins.server.InvalidRequestException;

/**
 * Calling convention that supports RFC 1867 multipart content. This content
 * type supports uploading of content items (typically: files.)
 *
 * <p>Uploaded items will be retained in memory as long as they are reasonably
 * small. Larger items will be written to a temporary file on disk. Very large
 * upload requests are not permitted. 
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst.dehaan@nl.wanadoo.com">Ernst de Haan</a>
 */
public class MultipartCallingConvention
extends CustomCallingConvention {

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>MultipartCallingConvention</code>.
    */
   public MultipartCallingConvention() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   protected boolean matches(HttpServletRequest httpRequest)
   throws Exception {

      // Requirement 1: The request must be multi-part
      if (! FileUpload.isMultipartContent(httpRequest)) {
         return false;
      }

      // Create a factory for disk-based file items
      FileItemFactory factory = new DiskFileItemFactory();

      // Create a new file upload handler
      ServletFileUpload upload = new ServletFileUpload(factory);

      // Parse the request (list contains FileItem instances)
      List itemList;
      try {
         itemList = upload.parseRequest(httpRequest);
      } catch (Throwable exception) {
         throw new RuntimeException(exception);
      }

      // Determine the function name (in the "_function" parameter)
      String function = null;
      boolean found = false;
      for (int i = 0; i < itemList.size() && !found; i++) {
         FileItem item = (FileItem) itemList.get(i);
         if ("_function".equals(item.getFieldName())) {
            function = item.getString();
            found = true;
         }
      }

      // Requirement 2: The function name must be specified
      return function != null && !function.equals("");
   }

   protected FunctionRequest convertRequestImpl(HttpServletRequest httpRequest)
   throws InvalidRequestException,
          FunctionNotSpecifiedException {

      // The request must be multi-part
      if (! FileUpload.isMultipartContent(httpRequest)) {
         throw new InvalidRequestException("Request is not multi-part.");
      }

      // Create a factory for disk-based file items
      FileItemFactory factory = new DiskFileItemFactory();

      // Create a new file upload handler
      ServletFileUpload upload = new ServletFileUpload(factory);

      // Parse the request (list contains FileItem instances)
      List itemList;
      try {
         itemList = upload.parseRequest(httpRequest);
      } catch (FileUploadException exception) {
         throw new InvalidRequestException("Failed to parse request.",
                                           exception);
      }

      // Convert the list to a PropertyReader instance
      PropertyReader params = new PropertyReader(itemList);

      // Determine the function name
      String function = params.get("_function");
      if (function == null) {
         throw new FunctionNotSpecifiedException();
      }

      // Get data section
      String dataSectionValue = params.get("_data");
      Element dataElement = null;
      if (dataSectionValue != null && dataSectionValue.length() > 0) {
         ElementParser parser = new ElementParser();

         // Parse the data section
         StringReader reader = new StringReader(dataSectionValue);
         try {
            dataElement = parser.parse(reader);

         // I/O error, should never happen on a StringReader
         } catch (IOException exception) {
            String thisClass     = MultipartCallingConvention.class.getName();
            String thisMethod    = "convertRequestImpl("
                                 + HttpServletRequest.class.getName()
                                 + ')';
            String subjectClass  = ElementParser.class.getName();
            String subjectMethod = "parse(java.io.Reader)";
            String detail        = null;
            throw Utils.logProgrammingError(thisClass,    thisMethod,
                                            subjectClass, subjectMethod,
                                            detail,       exception);
         // Parsing error
         } catch (ParseException exception) {
            String detail = "Cannot parse the data section.";
            throw new InvalidRequestException(detail, exception);
         }
      }

      // Construct and return the request object
      return new FunctionRequest(function, params, null);
   }

   protected void convertResultImpl(FunctionResult      xinsResult,
                                    HttpServletResponse httpResponse,
                                    HttpServletRequest  httpRequest)
   throws IOException {

      // Send the XML output to the stream and flush
      httpResponse.setContentType("text/xml; charset=UTF-8");
      PrintWriter out = httpResponse.getWriter();
      httpResponse.setStatus(HttpServletResponse.SC_OK);
      CallResultOutputter.output(out, xinsResult);
      out.close();
   }


   //-------------------------------------------------------------------------
   // Inner classes
   //-------------------------------------------------------------------------

   /**
    * Implementation of a <code>PropertyReader</code> on top of set of
    * <code>FileItem</code> instances.
    *
    * @version $Revision$ $Date$
    * @author <a href="mailto:ernst.dehaan@nl.wanadoo.com">Ernst de Haan</a>
    */
   private static class PropertyReader
   extends Object
   implements org.xins.common.collections.PropertyReader {

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      /**
       * Construct a new <code>PropertyReader</code> for the specified list of
       * <code>FileItem</code> instances.
       *
       * @param itemList
       *    the {@link List} of {@link FileItem} instances, not
       *    <code>null</code>.
       *
       * @throws NullPointerException
       *    if <code>itemList == null</code>.
       */
      private PropertyReader(List itemList) throws NullPointerException {

         // Store the reference 
         _itemList = itemList;

         // Prepare a list of property names
         _names = new ArrayList(itemList.size());
         for (int i = 0; i < itemList.size(); i++) {
            FileItem item = (FileItem) itemList.get(i);
            String name = item.getFieldName();
            _names.add(name);
         }
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      /**
       * List of <code>FileItem</code> instances. Never <code>null</code>.
       */
      private final List _itemList;

      /**
       * List of property names. Never <code>null</code>.
       *
       * <p>TODO: Use a <code>HashSet</code> instead, for improved search
       * performance.
       */
      private final ArrayList _names;


      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------

      public String get(String name) throws IllegalArgumentException {

         // Find the index, by name
         int index = _names.indexOf(name);
         String value;

         // Name not found
         if (index < 0) {
            return null;

         // Name found
         } else {
            FileItem item = (FileItem) _itemList.get(index);
            value = item.getString();
         }

         return value;
      }

      public Iterator getNames() {
         return _names.iterator();
      }

      public int size() {
         return _names.size();
      }
   }
}
