/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xins.logdoc.LogCentral;

/**
 * HTTP servlet that forwards requests to an <code>API</code>.
 *
 * <p>This servlet supports the following HTTP request methods:
 *
 * <ul>
 *   <li>GET
 *   <li>POST
 *   <li>HEAD
 *   <li>OPTIONS
 * </ul>
 *
 * <p>A method with any other request method will make this servlet return:
 * <blockquote><code>405 Method Not Allowed</code></blockquote>
 *
 * <p>If no matching function is found, then this servlet will return:
 * <blockquote><code>404 Not Found</code></blockquote>
 *
 * <p>If the state is not <em>ready</em>, then depending on the state, an HTTP
 * response code will be returned:
 *
 * <table class="APIServlet_HTTP_response_codes">
 *    <tr><th>State               </th><th>HTTP response code       </th></tr>
 *
 *    <tr><td>Initial             </td><td>503 Service Unavailable  </td></tr>
 *    <tr><td>Bootstrapping
 *            framework           </td><td>503 Service Unavailable  </td></tr>
 *    <tr><td>Framework bootstrap
 *                          failed</td><td>500 Internal Server Error</td></tr>
 *    <tr><td>Constructing API    </td><td>503 Service Unavailable  </td></tr>
 *    <tr><td>API construction
 *            failed              </td><td>500 Internal Server Error</td></tr>
 *    <tr><td>Bootstrapping API   </td><td>503 Service Unavailable  </td></tr>
 *    <tr><td>API bootstrap failed</td><td>500 Internal Server Error</td></tr>
 *    <tr><td>Initializing API    </td><td>503 Service Unavailable  </td></tr>
 *    <tr><td>API initialization
 *            failed              </td><td>500 Internal Server Error</td></tr>
 *    <tr><td>Disposing           </td><td>500 Internal Server Error</td></tr>
 *    <tr><td>Disposed            </td><td>500 Internal Server Error</td></tr>
 * <table>
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 *
 * @since XINS 1.0.0
 */
public final class APIServlet
extends HttpServlet {

   //-------------------------------------------------------------------------
   // TODO: Add trace logging for all methods

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The fully-qualified name of this class.
    */
   private static final String CLASSNAME = APIServlet.class.getName();

   /**
    * The name of the system property that specifies the location of the
    * configuration file.
    */
   static final String CONFIG_FILE_SYSTEM_PROPERTY =
      "org.xins.server.config";

   /**
    * The name of the runtime property that specifies the interval
    * for the configuration file modification checks, in seconds.
    */
   static final String CONFIG_RELOAD_INTERVAL_PROPERTY =
      "org.xins.server.config.reload";

   /**
    * The name of the runtime property that hostname for the server
    * running the API.
    */
   static final String HOSTNAME_PROPERTY = "org.xins.server.hostname";

   /**
    * The default configuration file modification check interval, in seconds.
    */
   static final int DEFAULT_CONFIG_RELOAD_INTERVAL = 60;

   /**
    * The name of the build property that specifies the name of the
    * API class to load.
    */
   static final String API_CLASS_PROPERTY = "org.xins.api.class";

   /**
    * The name of the build property that specifies the name of the
    * API.
    */
   static final String API_NAME_PROPERTY = "org.xins.api.name";

   /**
    * The name of the build property that specifies the version with which the
    * API was built.
    */
   static final String API_BUILD_VERSION_PROPERTY =
      "org.xins.api.build.version";

   /**
    * The name of the build property that specifies the default calling
    * convention.
    */
   static final String API_CALLING_CONVENTION_PROPERTY =
      "org.xins.api.calling.convention";

   /**
    * The name of the build property that specifies the class of the default
    * calling convention.
    */
   static final String API_CALLING_CONVENTION_CLASS_PROPERTY =
      "org.xins.api.calling.convention.class";

   /**
    * The parameter of the query to specify the calling convention.
    */
   static final String CALLING_CONVENTION_PARAMETER = "_convention";

   /**
    * The standard calling convention.
    */
   static final String STANDARD_CALLING_CONVENTION = "_xins-std";

   /**
    * The old style calling convention.
    */
   static final String OLD_STYLE_CALLING_CONVENTION = "_xins-old";

   /**
    * The XML calling convention.
    */
   static final String XML_CALLING_CONVENTION = "_xins-xml";

   /**
    * The XSLT calling convention.
    */
   static final String XSLT_CALLING_CONVENTION = "_xins-xslt";

   /**
    * The SOAP calling convention.
    */
   static final String SOAP_CALLING_CONVENTION = "_xins-soap";

   /**
    * The name of the runtime property that specifies the locale for the log
    * messages.
    *
    * @deprecated
    *    Use {@link LogCentral#LOG_LOCALE_PROPERTY}.
    */
   static final String LOG_LOCALE_PROPERTY =
      "org.xins.server.log.locale";


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Initializes the loggers to log to the console using a simple format
    * and no threshold. This is done by calling
    * {@link Engine#configureLoggerFallback()}.
    */
   static {
      ConfigManager.configureLoggerFallback();
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>APIServlet</code> object.
    */
   public APIServlet() {
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * XINS server engine. Never <code>null</code>.
    */
   private Engine _engine;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns information about this servlet, as plain text.
    *
    * @return
    *    textual description of this servlet, not <code>null</code> and not an
    *    empty character string.
    */
   public String getServletInfo() {
      return "XINS/Java Server Framework " + Library.getVersion();
   }

   /**
    * Initializes this servlet using the specified configuration (wrapper
    * method).
    *
    * @param config
    *    the {@link ServletConfig} object which contains build properties for
    *    this servlet, as specified by the <em>assembler</em>, cannot be
    *    <code>null</code>.
    *
    * @throws ServletException
    *    if the servlet could not be initialized.
    */
   public void init(ServletConfig config)
   throws ServletException {

      // Get the ServletContext
      ServletContext context = config.getServletContext();
      if (context == null) {
         Log.log_3202("config.getServletContext() == null");
         throw new ServletException();
      }

      // Check the expected vs implemented Java Servlet API version
      // 2.2, 2.3 and 2.4 are supported
      int major = context.getMajorVersion();
      int minor = context.getMinorVersion();
      if (major != 2 || (minor != 2 && minor != 3 && minor != 4)) {
         String expected = "2.2/2.3/2.4";
         String actual   = "" + major + '.' + minor;
         Log.log_3203(actual, expected);
      }

      // Starting servlet initialization
      Log.log_3000();

      try {

         // Construct an engine
         _engine = new Engine(config);

         // Initialization succeeded
         Log.log_3001();
      } catch (Throwable exception) {

         // Initialization failed, log the exception
         Log.log_3002(exception);

         // TODO: Make sure the current state is an error state?

         // Pass the exception through
         if (exception instanceof ServletException) {
            throw (ServletException) exception;
         } else if (exception instanceof Error) {
            throw (Error) exception;
         } else if (exception instanceof RuntimeException) {
            throw (RuntimeException) exception;

         } else {
            // Should in theory never happen, but because of the design of the
            // JVM this cannot be guaranteed
            throw new Error();
         }
      }
   }

   /**
    * Returns the <code>ServletConfig</code> object which contains the
    * build properties for this servlet. The returned {@link ServletConfig}
    * object is the one passed to the {@link #init(ServletConfig)} method.
    *
    * @return
    *    the {@link ServletConfig} object that was used to initialize this
    *    servlet, not <code>null</code> if this servlet is indeed already
    *    initialized.
    */
   public ServletConfig getServletConfig() {
      return (_engine == null) ? null : _engine.getServletConfig();
   }

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
   public void service(HttpServletRequest  request,
                       HttpServletResponse response)
   throws IOException {
      _engine.service(request, response);
   }

   /**
    * Destroys this servlet. A best attempt will be made to release all
    * resources.
    *
    * <p>After this method has finished, it will set the state to
    * <em>disposed</em>. In that state no more requests will be handled.
    */
   public void destroy() {
      _engine.destroy();
   }
}
