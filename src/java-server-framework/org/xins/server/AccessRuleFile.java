/*
 * $Id$
 *
 * Copyright 2004 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.io.FileWatcher;
import org.xins.common.text.ParseException;

/**
 * A collection of access rules.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 *
 * @since XINS 1.1.0
 */
public class AccessRuleFile implements AccessRuleContainer {
   
   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------
   
   /**
    * Returns the next token in the descriptor
    *
    * @param descriptor
    *   the original descriptor, for use in the {@link ParseException}, if
    *   necessary.
    *
    * @param tokenizer
    *   the {@link StringTokenizer} to retrieve the next token from.
    *
    * @return
    *   the next token, never <code>null</code>.
    *
    * @throws ParseException
    *   if <code>tokenizer.</code>{@link StringTokenizer#hasMoreTokens() hasMoreTokens}()<code> == false</code>.
    */
   private static String nextToken(String descriptor, StringTokenizer tokenizer)
   throws ParseException {

      if (!tokenizer.hasMoreTokens()) {
         throw new ParseException("The string \"" + descriptor + "\" is invalid as an access rule descriptor. Too few tokens retrieved from the descriptor.");
      } else {
         return tokenizer.nextToken();
      }
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>AccessRuleFile</code>.
    * 
    * @param descriptor
    *    the access rule descriptor, the character string to parse, cannot be <code>null</code>.
    *    It also cannot be empty <code>(" ")</code>.
    *
    * @param interval
    *    the interval used to check the ACL file for modification.
    *
    * @throws ParseException
    *    If the token is incorrectly formatted.
    *
    * @throws IllegalArgumentException
    *    if <code>token == null</code>.
    */
   public AccessRuleFile(String descriptor, int interval) throws IllegalArgumentException, ParseException {

      // Check preconditions
      MandatoryArgumentChecker.check("descriptor", descriptor);

      StringTokenizer tokenizer = new StringTokenizer(descriptor," \t\n\r");

      String token = nextToken(descriptor, tokenizer);
      if (!"file".equals(token)) {
         throw new ParseException("First token of descriptor is \"" + token + "\", instead of 'file'.");
      }

      String file = nextToken(descriptor, tokenizer);

      // Create and start a file watch thread
      ACLFileListener aclFileListener = new ACLFileListener();
      if (interval > 0) {
         _aclFileWatcher = new FileWatcher(file, interval, aclFileListener);
         _aclFileWatcher.start();
      }
      try {
         parseAccessRuleFile(file, interval);
      } catch (IOException ioe) {
         throw new ParseException("Cannot parse the file " + file + " due to an IO exception: " + ioe.getMessage());
      }
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The ACL file.
    */
   private String _file;

   /**
    * The interval used to check the ACL file for modification.
    */
   private int _interval;

   /**
    * File watcher for this ACL file.
    */
   private FileWatcher _aclFileWatcher;

   /**
    * The list of rules. Cannot be <code>null</code>.
    */
   private AccessRuleContainer[] _rules;

   /**
    * String representation of this object. Cannot be <code>null</code>.
    * XXX TODO
    */
   //private final String _asString;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns whether the ip address is allowed to access the functionName.
    *
    * @return
    *    <code>Boolean.TRUE</code> if the functionName is allowed, 
    *    <code>Boolean.FALSE</code> if the functionName is denied or
    *    <code>null</code> if the ip address does not match any of the rules
    *    or if the functionName does not match the pattern.
    */
   public Boolean isAllowed(String ip, String functionName) throws IllegalArgumentException, ParseException {

      // Check preconditions
      MandatoryArgumentChecker.check("ip", ip, "functionName", functionName);

      for (int i = 0; i < _rules.length; i++) {
         Boolean allowed = _rules[i].isAllowed(ip, functionName);
         if (allowed != null) {
            return allowed;
         }
      }

      // Not found
      return null;
   }

   /**
    * Closes the file watcher.
    */
   public void close() {
      
      // Close all the children
      if (_rules != null) {
         for (int i = 0; i < _rules.length; i++) {
            _rules[i].close();
         }
      }
      _aclFileWatcher.end();
      _aclFileWatcher = null;
   }

   /**
    * Parses the ACL file.
    *
    * @param file
    *    the ACL file.
    *
    * @param interval
    *    the interval used to check the ACL file for modification.
    */
   private void parseAccessRuleFile(String file, int interval) throws IllegalArgumentException, ParseException, IOException {

      // Check preconditions
      MandatoryArgumentChecker.check("file", file);
      BufferedReader reader = new BufferedReader(new FileReader(file));

      List rules = new ArrayList(25);
      int lineNumber = 0;
      String nextLine = "";
      while(reader.ready() && nextLine != null) {
         nextLine = reader.readLine();
         lineNumber++;
         if (nextLine != null && (nextLine.startsWith("allow") || nextLine.startsWith("deny"))) {
            rules.add(AccessRule.parseAccessRule(nextLine));
         } else if (nextLine != null && nextLine.startsWith("file")) {
            rules.add(new AccessRuleFile(nextLine, interval));
         } else if (nextLine == null || nextLine.trim().equals("") || nextLine.startsWith("#")) {

            // Ignore comments and empty lines
         } else {

            // Incorrect line
            // TODO logdoc
            throw new ParseException("Incorrect line \"" + nextLine + "\" in the file " + file + " at line " + lineNumber + ".");
         }
      }
      _rules = (AccessRuleContainer[])rules.toArray(new AccessRuleContainer[0]);

   }
   
   /**
    * Listener that reloads the ACL file if it changes.
    *
    * @version $Revision$ $Date$
    * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
    *
    * @since XINS 1.1.0
    */
   private final class ACLFileListener
   extends Object
   implements FileWatcher.Listener {

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      /**
       * Constructs a new <code>ACLFileListener</code> object.
       */
      private ACLFileListener() {
         // empty
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------

      /**
       * Re-initializes the ACL rules for this file.
       */
      private void reinit() {
         
         // Close the children
         if (_rules != null) {
            for (int i = 0; i < _rules.length; i++) {
               _rules[i].close();
            }
         }
         try {
            parseAccessRuleFile(_file, _interval);
         } catch (Exception ioe) {
            
            // XXX log error
            _rules = new AccessRuleContainer[0];
         }
      }

      /**
       * Callback method called when the configuration file is found while it
       * was previously not found.
       *
       * <p>This will trigger re-initialization.
       */
      public void fileFound() {
         reinit();
      }

      /**
       * Callback method called when the configuration file is (still) not
       * found.
       *
       * <p>The implementation of this method does not perform any actions.
       */
      public void fileNotFound() {
         Log.log_3400(_file);
      }

      /**
       * Callback method called when the configuration file is (still) not
       * modified.
       *
       * <p>The implementation of this method does not perform any actions.
       */
      public void fileNotModified() {
      }

      /**
       * Callback method called when the configuration file could not be
       * examined due to a <code>SecurityException</code>.
       * modified.
       *
       * <p>The implementation of this method does not perform any actions.
       *
       * @param exception
       *    the caught security exception, should not be <code>null</code>
       *    (although this is not checked).
       */
      public void securityException(SecurityException exception) {
         Log.log_3401(exception, _file);
      }

      /**
       * Callback method called when the configuration file is modified since
       * the last time it was checked.
       *
       * <p>This will trigger re-initialization.
       */
      public void fileModified() {
         reinit();
      }
   }
}
