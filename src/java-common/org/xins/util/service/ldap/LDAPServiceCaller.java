/*
 * $Id$
 */
package org.xins.util.service.ldap;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import org.xins.util.MandatoryArgumentChecker;
import org.xins.util.service.CallFailedException;
import org.xins.util.service.CallResult;
import org.xins.util.service.Descriptor;
import org.xins.util.service.ServiceCaller;
import org.xins.util.service.ServiceDescriptor;

/**
 * LDAP service caller.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.115
 */
public final class LDAPServiceCaller extends ServiceCaller {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Constant representing the <em>none</em> authentication method.
    */
   public static final AuthenticationMethod NO_AUTHENTICATION = new AuthenticationMethod("none");

   /**
    * Constant representing the <em>simple</em> authentication method.
    */
   public static final AuthenticationMethod SIMPLE_AUTHENTICATION = new AuthenticationMethod("simple");

   /**
    * Authentication details to be used when none are specified.
    */
   public static final AuthenticationDetails FALLBACK_AUTHENTICATION_DETAILS = new AuthenticationDetails(NO_AUTHENTICATION, null, null);

   /**
    * The initial context factory.
    */
   private static final String INITIAL_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>LDAPServiceCaller</code> object.
    *
    * @param descriptor
    *    the descriptor of the service, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>descriptor == null</code>.
    */
   public LDAPServiceCaller(Descriptor descriptor)
   throws IllegalArgumentException {
      super(descriptor);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Performs the specified LDAP query using the specified authentication
    * details. If this succeeds with one of the targets, then a
    * {@link NamingEnumeration} is returned. Otherwise, if none of the targets
    * could successfully be called, a {@link CallFailedException} is thrown.
    *
    * @param authenticationDetails
    *    the authentication details, or <code>null</code> if no authentication
    *    details should be sent down.
    *
    * @param query
    *    the query to execute, or <code>null</code> if only a connection
    *    should be made.
    *
    * @return
    *    the result of the call, cannot be <code>null</code>.
    *
    * @throws CallFailedException
    *    if the call failed.
    */
   public NamingEnumeration call(AuthenticationDetails authenticationDetails,
                                 Query                 query)
   throws CallFailedException {

      // Construct a Request object
      Request request = new Request(authenticationDetails, query);

      // Perform the call
      CallResult callResult = doCall(request);

      // Return the result
      return (NamingEnumeration) callResult.getResult();
   }

   protected Object doCallImpl(ServiceDescriptor target,
                               Object            subject)
   throws Throwable {

      // Convert subject to a Request object
      Request request = (Request) subject;

      // Authenticate and connect
      AuthenticationDetails authenticationDetails = request._authenticationDetails;
      if (authenticationDetails == null) {
         authenticationDetails = FALLBACK_AUTHENTICATION_DETAILS;
      }
      InitialDirContext context = authenticate(target,  authenticationDetails);

      // Perform a query if applicable
      Query query = request._query;
      if (query != null) {
         return query(target, context, query);
      } else {
         return null;
      }
   }

   /**
    * Attempts to authenticate with a target service, using the specified
    * authentication details.
    *
    * @param target
    *    descriptor of the target service to connect to, cannot be
    *    <code>null</code>.
    *
    * @param authenticationDetails
    *    the authentication details, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>target == null || authenticationDetails == null</code>.
    *
    * @throws NamingException
    *    if the connection failed.
    */
   private InitialDirContext authenticate(ServiceDescriptor     target,
                                          AuthenticationDetails authenticationDetails)
   throws IllegalArgumentException, NamingException {

      // Check preconditions
      MandatoryArgumentChecker.check("target",                target,
                                     "authenticationDetails", authenticationDetails);

      // Determine what location to connect to
      String url = target.getURL();

      // Initialize connection settings
      Hashtable env = new Hashtable();
      env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
      env.put(Context.PROVIDER_URL,            url);
      env.put(Context.SECURITY_AUTHENTICATION, authenticationDetails.getMethod().getName());
      env.put(Context.SECURITY_PRINCIPAL,      authenticationDetails.getPrincipal());
      env.put(Context.SECURITY_CREDENTIALS,    authenticationDetails.getCredentials());

      // Connect
      // TODO: Connection time-out
      return new InitialDirContext(env);
   }

   /**
    * Performs the specified query.
    *
    * @param target
    *    the target service, cannot be <code>null</code>.
    *
    * @param context
    *    the directory context for the query, cannot be <code>null</code>.
    *
    * @param query
    *    the query to execute, cannot be <code>null</code>.
    *
    * @return
    *    the result of the query, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>target == null || context == null || query == null</code>.
    *
    * @throws NamingException
    *    if the search failed.
    */
   private NamingEnumeration query(ServiceDescriptor target,
                                   InitialDirContext context,
                                   Query             query)
   throws IllegalArgumentException, NamingException {

      // Check preconditions
      MandatoryArgumentChecker.check("target",  target,
                                     "context", context,
                                     "query",   query);

      // Get search base and filter
      String searchBase = query.getSearchBase();
      String filter     = query.getFilter();

      // Create SearchControls object
      SearchControls searchControls = new SearchControls(
         SearchControls.SUBTREE_SCOPE, // scope
         0L,                           // return all entries that match, no maximum
         target.getTimeOut(),          // time-out (in ms) or 0 if unlimited
         null,                         // return all attributes
         false,                        // do not return named objects
         false                         // do not dereference links
      );

      // Perform the search and return the result
      return context.search(searchBase, filter, searchControls);
   }

   //-------------------------------------------------------------------------
   // Inner classes
   //-------------------------------------------------------------------------

   /**
    * LDAP authentication method.
    *
    * @version $Revision$ $Date$
    * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
    *
    * @since XINS 0.115
    */
   public static final class AuthenticationMethod
   extends Object {

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      /**
       * Constructs a new <code>AuthenticationMethod</code> object.
       *
       * @param name
       *    the name of this authentication method, for example
       *    <code>"none"</code> or <code>"simple"</code>.
       */
      private AuthenticationMethod(String name) {
         _name = name;
      }

      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      /**
       * The name of this authentication method. Cannot be <code>null</code>.
       */
      private final String _name;


      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------

      /**
       * Returns the name of this authentication method. For example,
       * <code>"none"</code> or <code>"simple"</code>.
       *
       * @return
       *    the name of this authentication method, not <code>null</code>.
       */
      public String getName() {
         return _name;
      }

      public String toString() {
         return _name;
      }
   }

   /**
    * LDAP authentication details. Combines authentication method, principal
    * and credentials.
    *
    * @version $Revision$ $Date$
    * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
    *
    * @since XINS 0.115
    */
   public static final class AuthenticationDetails
   extends Object {

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      /**
       * Constructs a new <code>AuthenticationDetails</code> object.
       *
       * @param method
       *    the authentication method, for example {@link #NO_AUTHENTICATION}
       *    or {@link #SIMPLE_AUTHENTICATION}, cannot be <code>null</code>.
       *
       * @param principal
       *    the principal, cannot be <code>null</code> unless
       *    <code>method == </code>{@link #NO_AUTHENTICATION}.
       *
       * @param credentials
       *    the credentials, can be <code>null</code>.
       *
       * @throws IllegalArgumentException
       *    if <code>method == null
       *    || (method != NO_AUTHENTICATION &amp;&amp; principal == null)
       *    || (method == NO_AUTHENTICATION &amp;&amp; principal != null)
       *    || (method == NO_AUTHENTICATION &amp;&amp; credentials != null)</code>.
       */
      public AuthenticationDetails(AuthenticationMethod method,
                                   String               principal,
                                   String               credentials) {
         // Check preconditions
         if (method == null) {
            throw new IllegalArgumentException("method == null");
         } else if (method != NO_AUTHENTICATION && principal == null) {
            throw new IllegalArgumentException("method != NO_AUTHENTICATION && principal == null");
         } else if (method == NO_AUTHENTICATION && principal != null) {
            throw new IllegalArgumentException("method == NO_AUTHENTICATION && principal != null");
         } else if (method == NO_AUTHENTICATION && credentials != null) {
            throw new IllegalArgumentException("method == NO_AUTHENTICATION && credentials != null");
         }

         // Set fields
         _method      = method;
         _principal   = principal;
         _credentials = credentials;
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      /**
       * The authentication method. Cannot be <code>null</code>.
       */
      private final AuthenticationMethod _method;

      /**
       * The principal. Is <code>null</code> if and only if
       * {@link #_method}<code> == </code>{@link #NO_AUTHENTICATION}.
       */
      private final String _principal;

      /**
       * The credentials. Can be <code>null</code>. This field is always
       * <code>null</code> if
       * {@link #_method}<code> == </code>{@link #NO_AUTHENTICATION}.
       */
      private final String _credentials;


      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------

      /**
       * Returns the authentication method.
       *
       * @return
       *    the authentication method, not <code>null</code>.
       */
      public final AuthenticationMethod getMethod() {
         return _method;
      }

      /**
       * Returns the principal. Is <code>null</code> if and only if
       * {@link #getMethod()}<code> == </code>{@link #NO_AUTHENTICATION}.
       *
       * @return
       *    the principal, possibly <code>null</code>.
       */
      public final String getPrincipal() {
         return _principal;
      }

      /**
       * The credentials. Can be <code>null</code>. This field is always
       * <code>null</code> if
       * {@link #getMethod()}<code> == </code>{@link #NO_AUTHENTICATION}.
       *
       * @return
       *    the credentials, possibly <code>null</code>.
       */
      public final String getCredentials() {
         return _credentials;
      }
   }

   /**
    * LDAP query.
    *
    * @version $Revision$ $Date$
    * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
    *
    * @since XINS 0.115
    */
   public static final class Query
   extends Object {

      // TODO: Accept optionally list of attributes to fetch
      // TODO: Be able to set flag to return named objects or not
      // TODO: Be able to set flag to dereference links or not
      // TODO: Allow configuration of maximum items
      // TODO: Allow configuration of scope

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------

      /**
       * Returns the search base.
       *
       * @return
       *    the search base, can be <code>null</code>.
       */
      public String getSearchBase() {
         return null; // TODO
      }

      /**
       * Returns the filter.
       *
       * @return
       *    the filter, can be <code>null</code>.
       */
      public String getFilter() {
         return null; // TODO
      }
   }

   /**
    * LDAP search request. Combines
    * {@link LDAPServiceCaller.AuthenticationDetails authentication details}
    * and a {@link LDAPServiceCaller.Query query}.
    *
    * @version $Revision$ $Date$
    * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
    *
    * @since XINS 0.115
    */
   private static final class Request
   extends Object {

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      /**
       * Constructs a new <code>Request</code> object.
       *
       * @param authenticationDetails
       *    the authentication details, can be <code>null</code>.
       *
       * @param query
       *    the query to be executed, can be <code>null</code>.
       */
      private Request(AuthenticationDetails authenticationDetails, Query query) {
         _authenticationDetails = authenticationDetails;
         _query                 = query;
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      /**
       * The authentication details. Can be <code>null</code>.
       */
      private final AuthenticationDetails _authenticationDetails;

      /**
       * The query. Can be <code>null</code>.
       */
      private final Query _query;


      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------
   }

   /**
    * LDAP search result.
    *
    * @version $Revision$ $Date$
    * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
    *
    * @since XINS 0.115
    */
   private static final class Result
   extends Object {

      // TODO: Use this class

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------
   }
}
