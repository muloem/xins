/*
 * $Id$
 */
package org.xins.demos.caller;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.xins.client.CallRequest;
import org.xins.client.CallRequestParser;
import org.xins.client.XINSServiceCaller;
import org.xins.util.collections.PropertiesPropertyReader;
import org.xins.util.collections.PropertyReader;
import org.xins.util.service.Descriptor;
import org.xins.util.service.DescriptorBuilder;

/**
 * Executes a call to a XINS API.
 *
 * <p>This program expects 2 or 3 arguments:
 *
 * <ol>
 *    <li>Function caller configuration file (e.g. <code>caller.properties</code>)
 *    <li>Request file (e.g. <code>request.xml</code>)
 *    <li>Count (optional, e.g. 3, default is 1)
 * </ol>
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.46
 */
public final class Main extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Main method.
    *
    * @param args
    *    the arguments passed to this program, not <code>null</code>.
    *
    * @throws Throwable
    *    if anything goes wrong.
    */
   public static void main(String[] args) throws Throwable {

      int argCount = (args != null) ? args.length : 0;

      if (argCount < 2 || argCount > 3) {
         System.err.println("Usage: java " + Main.class.getName() + " <config> <request> <count>");
         System.err.println("   <config>  -- Name of config file (required).");
         System.err.println("   <request> -- Name of request file (required).");
         System.err.println("   <count>   -- Number of times to execute the request (optional).");
         System.exit(1);
      }

      // Initialize Log4J
      Properties settings = new Properties();
      settings.setProperty("log4j.rootLogger",                                "DEBUG, console");
      settings.setProperty("log4j.appender.console",                          "org.apache.log4j.ConsoleAppender");
      settings.setProperty("log4j.appender.console.layout",                   "org.apache.log4j.PatternLayout");
      settings.setProperty("log4j.appender.console.layout.ConversionPattern", "%d %-5p [%c] %m%n");
      settings.setProperty("log4j.logger.httpclient.wire",                    "WARN");
      settings.setProperty("log4j.logger.org.apache.commons.httpclient",      "WARN");
      PropertyConfigurator.configure(settings);
      Logger log = Logger.getLogger(Main.class.getName());

      // Get all parameters
      String configFileName  = args[0];
      String requestFileName = args[1];
      int count = (argCount > 2) ? Integer.parseInt(args[2]) : 1;

      // Read the config file
      InputStream configFile = new FileInputStream(configFileName);
      Properties p = new Properties();
      p.load(configFile);
      PropertyReader properties = new PropertiesPropertyReader(p);
      Descriptor descriptor = DescriptorBuilder.build(properties, "caller");
      XINSServiceCaller caller = new XINSServiceCaller(descriptor);

      // Read the request file
      Reader requestFile = new FileReader(requestFileName);
      CallRequestParser callRequestParser = new CallRequestParser();
      CallRequest request = callRequestParser.parse(requestFile);

      // Execute the call(s)
      XINSServiceCaller.Result result;
      try {
         for (int i = 0; i < count; i++) {
            result = caller.call(request);
            log.info("Call " + i + " performed (success=" + result.isSuccess() + ").");
         }
      } catch (Throwable exception) {
         log.error("Failed to execute call.", exception);
      }
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>Main</code> object.
    */
   private Main() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
