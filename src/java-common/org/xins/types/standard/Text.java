/*
 * $Id$
 */
package org.xins.types.standard;

import org.xins.types.Type;

/**
 * Standard type <em>_text</em>.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<A href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</A>)
 */
public final class Text extends Type {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The only instance of this class. This field is never <code>null</code>.
    */
   public final static Text SINGLETON = new Text();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>Text</code>.
    * This constructor is private, the field {@link #SINGLETON} should be
    * used.
    */
   private Text() {
      super("text", java.lang.String.class);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   protected Object fromStringImpl(String string) {
      return string;
   }
}
