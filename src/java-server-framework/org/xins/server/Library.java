/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Matcher;
import org.xins.common.text.TextUtils;

/**
 * Class that represents the XINS/Java Server Framework library.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public final class Library {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Regular expression that production release versions of XINS match, and
    * non-production release version do not.
    */
   private static final String PRODUCTION_RELEASE_PATTERN_STRING = "[1-9][0-9]*\\.[0-9]+\\.[0-9]+";

   /**
    * The pattern for a URL.
    */
   private static final Pattern PRODUCTION_RELEASE_PATTERN = 
         TextUtils.createPattern(PRODUCTION_RELEASE_PATTERN_STRING);


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

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

   /**
    * Checks if the specified version is a more recent version as this version.
    *
    * @param buildVersion
    *    the XINS version to check, cannot be <code>null</code>.
    *
    * @return
    *    <code>true</code> is the given version is the same version or more
    *    recent, false is the given version is not a production released or is
    *    less recent or the current version is not a production released.
    *
    * @throws NullPointerException
    *    if <code>version == null</code>.
    */
   static final boolean isMoreRecent(String buildVersion)
   throws NullPointerException {

      if (!isProductionRelease(buildVersion) ||
          isProductionRelease(getVersion())) {
         return false;
      }
      if (buildVersion.equals(getVersion())) {
         return true;
      }
      int buildFirstDot = buildVersion.indexOf('.');
      int currentFirstDot = getVersion().indexOf('.');
      int buildMajor = Integer.parseInt(buildVersion.substring(0, buildFirstDot));
      int currentMajor = Integer.parseInt(getVersion().substring(0, currentFirstDot));
      if (buildMajor < currentMajor) {
         return false;
      } else if (buildMajor > currentMajor) {
         return true;
      } else {
         int buildSecondDot = buildVersion.indexOf('.', buildFirstDot + 1);
         int currentSecondDot = getVersion().indexOf('.', currentFirstDot + 1);
         int buildMinor = Integer.parseInt(buildVersion.substring(0, buildSecondDot));
         int currentMinor = Integer.parseInt(getVersion().substring(0, currentSecondDot));
         if (buildMinor < currentMinor) {
            return false;
         } else if (buildMinor > currentMinor) {
            return true;
         } else {
            int buildThirdDot = buildVersion.indexOf('.', buildFirstDot + 1);
            int currentThirdDot = getVersion().indexOf('.', currentFirstDot + 1);
            int buildMinor2 = Integer.parseInt(buildVersion.substring(0, buildThirdDot));
            int currentMinor2 = Integer.parseInt(getVersion().substring(0, currentThirdDot));
            if (buildMinor2 < currentMinor2) {
               return false;
            } else {
               return true;
            }
         }
      }
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
   // Methods
   //-------------------------------------------------------------------------
}
