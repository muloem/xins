/*
 * $Id$
 */
package org.xins.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.znerd.xmlenc.XMLOutputter;

/**
 * Base class for API implementation classes.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public abstract class API
extends Object
implements DefaultReturnCodes {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Checks if the specified value is <code>null</code> or an empty string.
    * Only if it is then <code>true</code> is returned.
    *
    * @param value
    *    the value to check.
    *
    * @return
    *    <code>true</code> if and only if <code>value != null &amp;&amp;
    *    value.length() != 0</code>.
    */
   protected final static boolean isMissing(String value) {
      return value == null || value.length() == 0;
   }


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>API</code> object.
    */
   protected API() {
      _functionsByName = new HashMap();
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * Map that maps function names to <code>Function</code> instances.
    * Contains all functions associated with this API.
    *
    * <p />This field is initialised to a non-<code>null</code> value by the
    * constructor.
    */
   private final Map _functionsByName;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Initialises this API.
    *
    * <p />The implementation of this method in class {@link API} is empty.
    *
    * @param properties
    *    the properties, can be <code>null</code>.
    *
    * @throws Throwable
    *    if the initialisation fails.
    */
   public void init(Properties properties)
   throws Throwable {
      // empty
   }

   /**
    * Callback method invoked when a function is constructed.
    *
    * @param function
    *    the function that is added, not <code>null</code>.
    *
    * @throws NullPointerException
    *    if <code>function == null</code>.
    */
   final void functionAdded(Function function) {
      _functionsByName.put(function.getName(), function);
   }

   /**
    * Returns the function with the specified name.
    *
    * @param name
    *    the name of the function, will not be checked if it is
    *    <code>null</code>.
    *
    * @return
    *    the function with the specified name, or <code>null</code> if there
    *    is no match.
    */
   final Function getFunction(String name) {
      return (Function) _functionsByName.get(name);
   }

   /**
    * Forwards a call to the <code>handleCall(CallContext)</code> method.
    *
    * @param out
    *    the output stream to write to, not <code>null</code>.
    *
    * @param map
    *    the parameters, not <code>null</code>.
    *
    * @throws IOException
    *    if an I/O error occurs.
    */
   final void handleCall(PrintWriter out, Map map) throws IOException {

      // Reset the XMLOutputter
      StringWriter stringWriter = new StringWriter();
      XMLOutputter xmlOutputter = new XMLOutputter(stringWriter, "UTF-8");

      // Create a new call context
      CallContext context = new CallContext(xmlOutputter, map);

      // Determine the function name
      String functionName = context.getFunction();
      if ("_GetFunctionList".equals(functionName)) {
         doGetFunctionList(out, map);
      }

      // Forward the call
      boolean exceptionThrown = true;
      boolean success;
      String code;
      try {
         handleCall(context);
         success = context.getSuccess();
         code    = context.getCode();
         exceptionThrown = false;
      } catch (Throwable exception) {
         success = false;
         code    = INTERNAL_ERROR;

         xmlOutputter.reset(out, "UTF-8");
         xmlOutputter.startTag("result");
         xmlOutputter.attribute("success", "false");
         xmlOutputter.attribute("code", code);
         xmlOutputter.startTag("param");
         xmlOutputter.attribute("name", "_exception.class");
         xmlOutputter.pcdata(exception.getClass().getName());

         String message = exception.getMessage();
         if (message != null && message.length() > 0) {
            xmlOutputter.endTag();
            xmlOutputter.startTag("param");
            xmlOutputter.attribute("name", "_exception.message");
            xmlOutputter.pcdata(message);
         }

         StringWriter stWriter = new StringWriter();
         PrintWriter printWriter = new PrintWriter(stWriter);
         exception.printStackTrace(printWriter);
         String stackTrace = stWriter.toString();
         if (stackTrace != null && stackTrace.length() > 0) {
            xmlOutputter.endTag();
            xmlOutputter.startTag("param");
            xmlOutputter.attribute("name", "_exception.stacktrace");
            xmlOutputter.pcdata(stackTrace);
         }
         xmlOutputter.close();
      }

      if (!exceptionThrown) {
         out.print(stringWriter.toString());
      }
      Function f    = getFunction(functionName);
      long start    = context.getStart();
      long duration = System.currentTimeMillis() - start;
      f.performedCall(start, duration, success, code);

      out.flush();
   }

   /**
    * Handles a call to this API.
    *
    * @param context
    *    the context for this call, never <code>null</code>.
    *
    * @throws Throwable
    *    if anything goes wrong.
    */
   protected abstract void handleCall(CallContext context)
   throws Throwable;

   /**
    * Returns a list of all functions in this API. Per function the name and
    * the version are returned.
    *
    * @param out
    *    the output stream to write to, not <code>null</code>.
    *
    * @param map
    *    the parameters, not <code>null</code>.
    *
    * @throws IOException
    *    if an I/O error occurs.
    */
   private final void doGetFunctionList(PrintWriter out, Map map)
   throws IOException {
      // TODO: For each function:
      // context.startTag("function");
      // context.attribute("name", name);
      // context.attribute("version", version);
      // context.endTag();</xsl:text>
   }
}
