/*
 * $Id$
 */
package org.xins.common.ant;

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
 * @since XINS 0.87
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

      if (getProject().getUserProperty(_propertyName) != null) {
         log("Override ignored for property \"" + _propertyName + "\".", Project.MSG_VERBOSE);
         return;
      }

      String hostname = IPAddressUtils.getLocalHost();

      if (hostname == null || "".equals(hostname.trim()) || "localhost".equals(hostname.trim())) {
         log("Determining hostname of localhost failed. Setting property to \"" + _propertyName + "\".", Project.MSG_WARN);
      }

      getProject().setUserProperty(_propertyName, hostname);
   }
}
