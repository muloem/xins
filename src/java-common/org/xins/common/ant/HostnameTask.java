/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.ant;

import java.io.InputStream;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import org.xins.common.net.IPAddressUtils;

/**
 * Apache Ant task that determines the host name.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.0.0
 */
public class HostnameTask extends Task {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Default name for the property to set.
    */
   public static final String DEFAULT_PROPERTY_NAME = "hostname";


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Tries running the POSIX <code>hostname</code> command to determine the
    * name of the local host. If this does not succeed, then <code>null</code>
    * is returned instead of the hostname.
    *
    * @return
    *    the name of the local host, or <code>null</code>.
    */
   private static String runHostnameCommand() {

      String hostname;
      try {
         Process process = Runtime.getRuntime().exec("hostname");
         process.waitFor();

         int exitValue = process.exitValue();
         if (exitValue != 0) {
            // TODO: Log
            return null;
         }

         // Get the stdout output from the process
         InputStream in = process.getInputStream();

         // Configure max expected hostname length
         final int MAX = 500;

         // Read the whole output
         byte[] bytes = new byte[MAX];
         int read = in.read(bytes);
         if (read < 0) {
            return null;
         }

         // TODO: Check all characters in the hostname

         final String ENCODING = "US-ASCII";
         hostname = new String(bytes, 0, read, ENCODING);

         for (int i = 0; i < read; i++) {
            char ch = hostname.charAt(i);
            if (ch >= 'a' && ch <= 'z') {
               // OK: fall through
            } else if (ch > 'A' && ch <= 'Z') {
               // OK: fall through
            } else if (ch > '0' && ch <= '9') {
               // OK: fall through
            } else if (ch == '-' || ch == '_' || ch == '.') {
               // OK: fall through
            } else if (ch == '\n' || ch == '\r') {
               hostname = hostname.substring(0, i);
               i = read;
            }
         }

      } catch (Exception exception) {
         // TODO: Log
         hostname = null;
      }

      if (hostname != null) {
         hostname = hostname.trim();
         if (hostname.length() < 1) {
            hostname = null;
         }
      }

      return hostname;
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * Name of the property to store the host name in. Default is
    * {@link #DEFAULT_PROPERTY_NAME}.
    */
   private String _propertyName = DEFAULT_PROPERTY_NAME;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Sets the name of the property. If <code>null</code> or <code>""</code>
    * is passed as the argument, then {@link #DEFAULT_PROPERTY_NAME} is
    * assumed.
    *
    * @param newPropertyName
    *    the name of the property to store the host name in, or
    *    <code>null</code> if the {@link #DEFAULT_PROPERTY_NAME} should be
    *    assumed.
    */
   public void setProperty(String newPropertyName) {
      if (newPropertyName == null) {
         _propertyName = DEFAULT_PROPERTY_NAME;
      } else {
         newPropertyName = newPropertyName.trim();
         if ("".equals(newPropertyName)) {
            _propertyName = DEFAULT_PROPERTY_NAME;
         } else {
            _propertyName = newPropertyName;
         }
      }
   }

   /**
    * Called by the project to let the task do its work.
    *
    * @throws BuildException
    *    if something goes wrong with the build.
    */
   public void execute() throws BuildException {

      // Do not override the property value
      if (getProject().getUserProperty(_propertyName) != null) {
         log("Override ignored for property \"" + _propertyName + "\".", Project.MSG_VERBOSE);
         return;
      }

      // First try using the IPAddressUtils class from the JDK
      String hostname = IPAddressUtils.getLocalHost();

      // Normalize the host name and filter out fakes (empty or "localhost")
      if (hostname != null) {
         hostname = hostname.trim();
         if ("".equals(hostname) || "localhost".equals(hostname)) {
            hostname = null;
         }
      }

      // No hostname yet? Try running the 'hostname' command on POSIX systems
      if (hostname == null) {
         hostname = runHostnameCommand();
      }

      // Still no hostname, fallback to a default and then log a warning
      if (hostname == null) {
         hostname = "localhost";

         String message = "Determining hostname of localhost failed. "
                        + "Setting property to \""
                        + hostname
                        + "\".";
         log(message, Project.MSG_WARN);
      }

      // Actually set the property
      getProject().setUserProperty(_propertyName, hostname);
   }
}
