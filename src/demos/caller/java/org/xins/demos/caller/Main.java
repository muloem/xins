/*
 * $Id$
 */
package org.xins.demos.caller;

import java.io.FileReader;
import java.io.Reader;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.xins.client.CallRequest;
import org.xins.client.CallRequestParser;
import org.xins.client.FunctionCaller;
import org.xins.client.FunctionCallerParser;

/**
 * Executes a call to a XINS API.
 *
 * <p>This program expects 2 or 3 arguments:
 *
 * <ol>
 *    <li>Function caller configuration file (e.g. <code>config.xml</code>)
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
      settings.setProperty("log4j.rootCategory",                              "DEBUG, console");
      settings.setProperty("log4j.appender.console",                          "org.apache.log4j.ConsoleAppender");
      settings.setProperty("log4j.appender.console.layout",                   "org.apache.log4j.PatternLayout");
      settings.setProperty("log4j.appender.console.layout.ConversionPattern", "%d %-5p - %m%n");
      PropertyConfigurator.configure(settings);
      Logger log = Logger.getLogger(Main.class.getName());

      // Get all parameters
      String configFileName = args[0];
      String requestFileName = args[1];
      int count = (argCount > 2) ? Integer.parseInt(args[2]) : 1;

      // Read the config file
      Reader configFile = new FileReader(configFileName);
      FunctionCallerParser functionCallerParser = new FunctionCallerParser();
      FunctionCaller caller = functionCallerParser.parse(configFile);

      // Read the request file
      Reader requestFile = new FileReader(requestFileName);
      CallRequestParser callRequestParser = new CallRequestParser();
      CallRequest request = callRequestParser.parse(requestFile);

      // Execute the call(s)
      try {
         for (int i = 0; i < count; i++) {
            caller.call(request);
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
