/*
 * $Id$
 */
package org.xins.common.ant;

import java.io.File;

import org.apache.tools.ant.util.FileNameMapper;


/**
 * Apache Ant mapper that adds an upper case to the file to the first
 * character if needed.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 *
 * @since XINS 0.183
 */
public class HungarianMapper implements FileNameMapper {

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
    * Constructs a new <code>HungarianMapper</code>.
    */
   public HungarianMapper() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

    /**
     * Ignored.
     */
    public void setFrom(String from) {
    }

    /**
     * Ignored.
     */
    public void setTo(String to) {
    }

    /**
     * Return the file with a upper case as first letter if needed.
     */
    public String[] mapFileName(String sourceFileName) {
       String fileName = new File(sourceFileName).getName();
       if (fileName.charAt(0) >= 'A' && fileName.charAt(0) <= 'Z') {
          return new String[] {sourceFileName};
       } else {
          String dir = sourceFileName.substring(0, sourceFileName.lastIndexOf(fileName));
          fileName = fileName.substring(0,1).toUpperCase() + fileName.substring(1);
          return new String[] {dir+fileName};
       }
    }
}
