/*
 * $Id$
 */
package org.xins.server;

import java.util.Enumeration;
import java.util.Properties;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.NullEnumeration;
import org.xins.util.MandatoryArgumentChecker;

/**
 * Class that represents the XINS/Java Server Framework library.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.8
 */
public final class Library extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The logging category used by the XINS/Java Server Framework during
    * startup/initialization. This field is not <code>null</code>.
    */
   static final Logger BOOTSTRAP_LOG = Logger.getLogger("org.xins.server.lifespan.bootstrap");

   /**
    * The logging category used by the XINS/Java Server Framework during
    * re-initialization. This field is not <code>null</code>.
    */
   static final Logger INIT_LOG = Logger.getLogger("org.xins.server.lifespan.init");

   /**
    * The logging category used by the XINS/Java Server Framework during
    * re-initialization for ACL-related messages. This field is not
    * <code>null</code>.
    */
   static final Logger INIT_ACL_LOG = Logger.getLogger("org.xins.server.lifespan.init.acl");

   /**
    * The logging category used by the XINS/Java Server Framework during
    * shutdown/deinitialization. This field is not <code>null</code>.
    */
   static final Logger SHUTDOWN_LOG = Logger.getLogger("org.xins.server.lifespan.shutdown");

   /**
    * The logging category used by the XINS/Java Server Framework core during
    * runtime. This field is not <code>null</code>.
    */
   static final Logger RUNTIME_LOG = Logger.getLogger("org.xins.server.runtime");

   /**
    * The logging category used by the XINS/Java Server Framework core during
    * runtime for ACL-related messages. This field is not <code>null</code>.
    */
   static final Logger RUNTIME_ACL_LOG = Logger.getLogger("org.xins.server.runtime.acl");


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Initializes the loggers to log to the console using a simple format
    * and no threshold.
    */
   static {
      configureLoggerFallback();
   }

   /**
    * Configures or reconfigures the logging subsystem using the specified
    * properties.
    *
    * @param log
    *    the logger to log to, cannot be <code>null</code>.
    *
    * @param properties
    *    the properties that should initialize the logging subsystem, cannot
    *    be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>log == null || properties == null</code>.
    */
   static final void configure(Logger log, Properties properties)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("log", log, "properties", properties);

      // Get the logger repository
      Logger rootLogger = log.getRootLogger();

      // Attempt to configure Log4J
      PropertyConfigurator.configure(properties);

      // Determine if Log4J is properly initialized
      Enumeration appenders = rootLogger.getAllAppenders();
      if (appenders instanceof NullEnumeration) {
         configureLoggerFallback();
         log.error("System administration issue detected. Logging subsystem is not properly initialized. Falling back to default output method.");
      } else {
         log.debug("Logging subsystem is properly initialized.");
      }
   }

   /**
    * Initializes the logging subsystem with fallback default settings.
    */
   private static final void configureLoggerFallback() {
      Properties settings = new Properties();
      settings.setProperty("log4j.rootLogger",              "ALL, console");
      settings.setProperty("log4j.appender.console",        "org.apache.log4j.ConsoleAppender");
      settings.setProperty("log4j.appender.console.layout", "org.apache.log4j.SimpleLayout");
      PropertyConfigurator.configure(settings);
   }

   /**
    * Returns the version of this library.
    *
    * @return
    *    the version of this library, for example <code>"%%VERSION%%"</code>,
    *    never <code>null</code>.
    */
   public static final String getVersion() {
      return "%%VERSION%%";
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>Library</code> object.
    */
   private Library() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
