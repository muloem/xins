/*
 * $Id$
 */
package org.xins.common.service.ldap;

import org.xins.common.MandatoryArgumentChecker;

/**
 * LDAP query.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 0.115
 */
public final class Query extends Object {

   // TODO: Be able to set flag to return named objects or not
   // TODO: Be able to set flag to dereference links or not
   // TODO: Allow configuration of maximum items
   // TODO: Allow configuration of scope

   //----------------------------------------------------------------------
   // Constructors
   //----------------------------------------------------------------------

   /**
    * Constructs a new <code>Query</code>.
    *
    * @param searchBase
    *    the search base, cannot be <code>null</code>.
    *
    * @param filter
    *    the filter expression, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>searchBase == null || filter == null</code>.
    */
   public Query(String searchBase, String filter)
   throws IllegalArgumentException {
      this(searchBase, filter, null);
   }

   /**
    * Constructs a new <code>Query</code>, specifying the attributes to be
    * returned with an entry.
    *
    * @param searchBase
    *    the search base, cannot be <code>null</code>.
    *
    * @param filter
    *    the filter expression, cannot be <code>null</code>.
    *
    * @param attributes
    *    identifiers of the attributes to return; if this is
    *    <code>null</code> then all attributes are returned; if this is an
    *    empty array then no attributes are returned.
    *
    * @throws IllegalArgumentException
    *    if <code>searchBase == null || filter == null</code>.
    */
   public Query(String searchBase, String filter, String[] attributes)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("searchBase", searchBase, "filter", filter);

      // Store data in fields
      _searchBase = searchBase;
      _filter     = filter;
      if (attributes == null) {
         _attributes = null;
      } else {
         _attributes = new String[attributes.length];
         System.arraycopy(attributes, 0, _attributes, 0, attributes.length);
      }
   }


   //----------------------------------------------------------------------
   // Fields
   //----------------------------------------------------------------------

   /**
    * The search base. Cannot be <code>null</code>.
    */
   private final String _searchBase;

   /**
    * The filter expression. Cannot be <code>null</code>.
    */
   private final String _filter;

   /**
    * The attributes to be returned with an entry. If this field is
    * <code>null</code> then all attributes are returned; if this is an
    * empty array then no attributes are returned.
    */
   final String[] _attributes;


   //----------------------------------------------------------------------
   // Methods
   //----------------------------------------------------------------------

   /**
    * Returns the search base.
    *
    * @return
    *    the search base, cannot be <code>null</code>.
    */
   public String getSearchBase() {
      return _searchBase;
   }

   /**
    * Returns the filter expression to use for the search.
    *
    * @return
    *    the filter expression, cannot be <code>null</code>.
    */
   public String getFilter() {
      return _filter;
   }

   /**
    * Returns the attributes associated with the entry.
    *
    * @return
    *    the attributes, or <code>null</code> if all attributes should be returned.
    */
    public String[] getAttributes() {
       return _attributes;
    }
}
