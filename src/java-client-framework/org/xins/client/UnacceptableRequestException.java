/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.Utils;

import org.xins.common.collections.PropertyReader;
import org.xins.common.collections.ProtectedList;

import org.xins.common.constraint.Constraint;
import org.xins.common.constraint.ConstraintViolation;

import org.xins.common.text.FastStringBuffer;
import org.xins.common.text.WhislEncoding;

/**
 * Exception that indicates that a request for an API call is considered
 * unacceptable on the application-level. For example, a mandatory input
 * parameter may be missing.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.2.0
 */
public final class UnacceptableRequestException
extends RuntimeException {

   // TODO: Support XINSCallRequest objects?
   // TODO: Is the name UnacceptableRequestException okay?
   // TODO: Log UnacceptableRequestException! (not in this class though)

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Secret key used to protect <code>ProtectedList</code> instances.
    */
   private static final Object SECRET_KEY = new Object();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Constructs a detail message for the constructor to pass up to the
    * superclass constructor.
    *
    * @param request
    *    the {@link AbstractCAPICallRequest} that is considered unacceptable,
    *    never <code>null</code>.
    *
    * @param violations
    *    a list of constraint violations, cannot be <code>null</code> and
    *    should contain at least one element; all elements should be instances
    *    of class {@link ConstraintViolation}.
    *
    * @throws IllegalArgumentException
    *    if <code>request                ==   null
    *          || violations             ==   null
    *          || violations.size()      &lt; 1
    *          || violations[<em>i</em>] ==   null
    *          || !(violations[<em>i</em>] instanceof ConstraintViolation)
    *          || violations[<em>i</em>] == violations[<em>j</em>])</code>,
    *    <br>where <code>0 &lt;= <em>i</em> &lt; violations.size()</code>
    *    <br>and <code>0 &lt;= <em>j</em> &lt; violations.size()</code>.
    */
   private static final String createMessage(
      AbstractCAPICallRequest request,
      List                    violations)
   throws IllegalArgumentException {

      // Arguments cannot be null
      MandatoryArgumentChecker.check("request",    request,
                                     "violations", violations);

      // We need at least one violation
      int violationCount = violations.size();
      if (violationCount < 1) {
         throw new IllegalArgumentException("violationCount.size() == 0");
      }

      // Stick the message in a buffer
      FastStringBuffer buffer = new FastStringBuffer(250);
      buffer.append("Unacceptable XINS call request, due to ");
      if (violationCount == 1) {
         buffer.append(" 1 constraint violation: ");
      } else {
         buffer.append(violationCount);
         buffer.append(" constraint violations: ");
      }

      // TODO: Make sure violations are on input constraints only

      // Loop through the list of violations
      for (int i = 0; i < violationCount; i++) {
         Object elem = violations.get(i);

         // Disallow null elements
         if (elem == null) {
            throw new IllegalArgumentException("violations[" + i + "] == null");

         // Disallow other than ConstraintViolation instances
         } else if (! (elem instanceof ConstraintViolation)) {
            throw new IllegalArgumentException("violations["
                                             + i
                                             + "] is an instance of class "
                                             + Utils.getClassName(elem));
         }

         // Disallow duplicates
         int existing = violations.indexOf(elem);
         if (existing != i) {
            throw new IllegalArgumentException("violations[" + existing + "].equals(violations[" + i + "])");
         }

         ConstraintViolation violation = (ConstraintViolation) elem;
         buffer.append(violation.getDescription());
         buffer.append(' ');
      }

      // Append function name
      buffer.append("Request is to function \"");
      buffer.append(request.function().getName());

      // Append parameters
      Map params = request.parameterMap();
      int paramCount = (params == null) ? 0 : params.size();
      if (paramCount == 0) {
         buffer.append("\" with no parameters.");
      } else {
         buffer.append("\" with parameters ");
         Iterator keys = params.keySet().iterator();
         boolean hadOne = false;
         while (keys.hasNext()) {
            String key = (String) keys.next();
            if (key != null) {
               Object val = params.get(key);
               if (val != null) {
                  buffer.append(WhislEncoding.encode(key));
                  buffer.append('=');

                  // FIXME: Do not call val.toString, but use type to convert
                  //        from value instance to String instead
                  buffer.append(WhislEncoding.encode(val.toString()));
                  buffer.append('&');
                  hadOne = true;
               }
            }
         }

         if (hadOne) {
            // Remove last ampersand
            buffer.crop(buffer.getLength() - 1);
         }
      }

      return buffer.toString();
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>UnacceptableRequestException</code> using the
    * specified <code>AbstractCAPICallRequest</code>.
    *
    * <p>The list of violated constraints is passed. This list will be stored
    * internally in this exception instance.
    *
    * @param request
    *    the {@link AbstractCAPICallRequest} that is considered unacceptable,
    *    never <code>null</code>.
    *
    * @param violations
    *    a list of constraint violations, cannot be <code>null</code> and
    *    should contain at least one element; all elements should be instances
    *    of class {@link ConstraintViolation}.
    *
    * @throws IllegalArgumentException
    *    if <code>request                ==   null
    *          || violations             ==   null
    *          || violations.size()      &lt; 1
    *          || violations[<em>i</em>] ==   null
    *          || !(violations[<em>i</em>] instanceof ConstraintViolation)
    *          || violations[<em>i</em>] == violations[<em>j</em>])</code>,
    *    <br>where <code>0 &lt;= <em>i</em> &lt; violations.size()</code>
    *    <br>and <code>0 &lt;= <em>j</em> &lt; violations.size()</code>.
    */
   UnacceptableRequestException(AbstractCAPICallRequest request,
                                List                    violations)
   throws IllegalArgumentException {

      // Construct a detail message and pass that up
      super(createMessage(request, violations));

      // Store the information
      _request    = request;
      _violations = new ProtectedList(SECRET_KEY, violations);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The request that is considered unacceptable. Never <code>null</code>.
    */
   private final AbstractCAPICallRequest _request;

   /**
    * The list of constraint violations. Cannot be <code>null</code>. Every
    * element is an instance of class {@link ConstraintViolation}.
    */
   private final ProtectedList _violations;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   // TODO: Add "List getViolatedConstraints()"
}
