/*
 * $Id$
 */
package org.xins.util.service.ldap;

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
    * details. If this succeeds with one of the targets, then a {@link Result}
    * object is returned. Otherwise, if none of the targets could successfully
    * be called, a {@link CallFailedException} is thrown.
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
   public Result call(AuthenticationDetails authenticationDetails,
                      Query                 query)
   throws CallFailedException {
      CallResult callResult = doCall(new Request(authenticationDetails, query));
      return (Result) callResult.getResult();
   }

   protected Object doCallImpl(ServiceDescriptor target,
                               Object            subject)
   throws Throwable {

      // Convert subject to a Request object
      Request request = (Request) subject;

      return null; // TODO
   }


   //-------------------------------------------------------------------------
   // Inner classes
   //-------------------------------------------------------------------------

   /**
    * Authentication details. Combines authentication method, principal and
    * credentials.
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

      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------
   }

   /**
    * Query.
    *
    * @version $Revision$ $Date$
    * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
    *
    * @since XINS 0.115
    */
   public static final class Query
   extends Object {

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

   /**
    * Search request. Combines {@link AuthenticationDetails} and a
    * {@link Query}.
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
    * Result object.
    *
    * @version $Revision$ $Date$
    * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
    *
    * @since XINS 0.115
    */
   public static final class Result
   extends Object {

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
