/*
 * $Id$
 *
 * Copyright 2003-2008 Online Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Matcher;
import org.xins.common.text.PatternUtils;

/**
 * Class that represents the XINS/Java Server Framework library.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public final class Library {

   /**
    * Regular expression that production release versions of XINS match, and
    * non-production release version do not.
    */
   private static final String PRODUCTION_RELEASE_PATTERN_STRING = "[1-9][0-9]*\\.[0-9]+(\\.[0-9]+)?";

   /**
    * The pattern for a URL.
    */
   private static final Pattern PRODUCTION_RELEASE_PATTERN =
         PatternUtils.createPattern(PRODUCTION_RELEASE_PATTERN_STRING);

   /**
    * Constructs a new <code>Library</code> object.
    */
   private Library() {
      // empty
   }

   /**
    * Returns the version of this library.
    *
    * @return
    *    the version of this library, for example <code>"1.0.0"</code>,
    *    never <code>null</code>.
    */
   public static final String getVersion() {
      return "%%VERSION%%";
   }

   /**
    * Checks if the specified version indicates a production release of XINS.
    *
    * @param version
    *    the XINS version to check, cannot be <code>null</code>.
    *
    * @return
    *    <code>true</code> is the specified XINS version identifies a
    *    production release of XINS, <code>false</code> if it does not.
    *
    * @throws NullPointerException
    *    if <code>version == null</code>.
    */
   static final boolean isProductionRelease(String version)
   throws NullPointerException {
      Perl5Matcher patternMatcher = new Perl5Matcher();
      return patternMatcher.matches(version, PRODUCTION_RELEASE_PATTERN);
   }
}
