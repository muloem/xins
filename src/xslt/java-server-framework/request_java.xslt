<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $Id$
-->
<!-- This stylesheet transform the input parameters of a function
     to a Request object with get method that will be used by the user
     in the call method of the implementation.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="text" />

	<xsl:param name="project_home" />
	<xsl:param name="project_file" />
	<xsl:param name="specsdir"     />
	<xsl:param name="api"          />
	<xsl:param name="api_file"     />

	<xsl:variable name="version">
		<xsl:call-template name="revision2string">
			<xsl:with-param name="revision" select="//function/@rcsversion" />
		</xsl:call-template>
	</xsl:variable>

	<xsl:template name="request">
		<xsl:param name="sessionBased"   />
		<xsl:param name="createsSession" />

<xsl:text><![CDATA[
/**
 * Container for the input parameters of the <em>]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[</em> function.
 */
public final static class Request {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------
]]></xsl:text>
		<xsl:call-template name="constructor_request">
			<xsl:with-param name="sessionBased"   select="$sessionBased"   />
			<xsl:with-param name="createsSession" select="$createsSession" />
		</xsl:call-template>
		<xsl:text>

   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------</xsl:text>
		<xsl:apply-templates select="input/param" mode="field" />

	 	<!-- If this function is session based also has a session field. -->
		<xsl:if test="$sessionBased = 'true' or $createsSession = 'true'">
			<xsl:text>
   private final org.xins.server.Session __session;</xsl:text>
		</xsl:if>

		<xsl:text>

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------</xsl:text>

		<xsl:apply-templates select="input/param" mode="method" />

	 	<!-- If this function is session based also generates a getSession() method. -->
		<xsl:if test="$sessionBased = 'true' or $createsSession = 'true'">
			<xsl:text><![CDATA[

   /**
    * Retrieves the user's session.
    *
    * @return
    *    the session of the user, never <code>null</code>.
    */
   org.xins.server.Session retrieveSession() {
      return __session;
   }]]></xsl:text>
		</xsl:if>

		<xsl:text>
}
</xsl:text>
	</xsl:template>

	<!-- Generated the constructor for the request
	     The contructor sets the input values.
	-->
	<xsl:template name="constructor_request">
		<xsl:param name="sessionBased"   />
		<xsl:param name="createsSession" />

		<xsl:text><![CDATA[
   /**
    * Constructs a new <code>Request</code> instance.
    */
   public Request(]]></xsl:text>
		<xsl:for-each select="input/param">
			<xsl:variable name="javatype">
				<xsl:call-template name="javatype_for_type">
					<xsl:with-param name="project_file" select="$project_file" />
					<xsl:with-param name="api"          select="$api"          />
					<xsl:with-param name="specsdir"     select="$specsdir"     />
					<xsl:with-param name="required"     select="@required"     />
					<xsl:with-param name="type"         select="@type"         />
				</xsl:call-template>
			</xsl:variable>
			<xsl:if test="not(position() = 1)">, </xsl:if>
			<xsl:value-of select="$javatype" />
			<xsl:text> </xsl:text>
			<xsl:value-of select="@name" />
		</xsl:for-each>
		<xsl:if test="$sessionBased = 'true' or $createsSession = 'true'">
			<xsl:text>, org.xins.server.Session _session</xsl:text>
		</xsl:if>
		<xsl:text>) {
</xsl:text>
		<xsl:for-each select="input/param">
			<xsl:text>      _</xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text> = </xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>;
</xsl:text>
		</xsl:for-each>
		<xsl:if test="$sessionBased = 'true' or $createsSession = 'true'">
			<xsl:text>      __session = _session;</xsl:text>
		</xsl:if>
		<xsl:text>
   }</xsl:text>
	</xsl:template>

	<!-- Generates the fields. -->
	<xsl:template match="function/input/param" mode="field">
		<xsl:variable name="javatype">
			<xsl:call-template name="javatype_for_type">
				<xsl:with-param name="project_file" select="$project_file" />
				<xsl:with-param name="api"          select="$api"          />
				<xsl:with-param name="specsdir"     select="$specsdir"     />
				<xsl:with-param name="required"     select="@required"     />
				<xsl:with-param name="type"         select="@type"         />
			</xsl:call-template>
		</xsl:variable>

		<xsl:text>

   private final </xsl:text>
		<xsl:value-of select="$javatype" />
		<xsl:text> _</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>;</xsl:text>
	</xsl:template>

	<!-- Generates the get methods. -->
	<xsl:template match="function/input/param" mode="method">
		<xsl:variable name="basetype">
			<xsl:call-template name="basetype_for_type">
				<xsl:with-param name="specsdir" select="$specsdir" />
				<xsl:with-param name="api"      select="$api"      />
				<xsl:with-param name="type"     select="@type"     />
			</xsl:call-template>
		</xsl:variable>
		<!-- Get the name of the get method. -->
		<xsl:variable name="hungarianName">
			<xsl:call-template name="hungarianUpper">
				<xsl:with-param name="text" select="@name" />
			</xsl:call-template>
		</xsl:variable>
		<!-- Get the return type of the variable. -->
		<xsl:variable name="javatype">
			<xsl:call-template name="javatype_for_type">
				<xsl:with-param name="project_file" select="$project_file" />
				<xsl:with-param name="api"          select="$api"          />
				<xsl:with-param name="specsdir"     select="$specsdir"     />
				<xsl:with-param name="required"     select="@required"     />
				<xsl:with-param name="type"         select="@type"         />
			</xsl:call-template>
		</xsl:variable>
		<!-- Get the return type of the get method. -->
		<xsl:variable name="javasimpletype">
			<xsl:call-template name="javatype_for_type">
				<xsl:with-param name="project_file" select="$project_file" />
				<xsl:with-param name="api"          select="$api"          />
				<xsl:with-param name="specsdir"     select="$specsdir"     />
				<xsl:with-param name="required"     select="'true'"     />
				<xsl:with-param name="type"         select="@type"         />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="typeIsPrimary">
			<xsl:call-template name="is_java_datatype">
				<xsl:with-param name="text" select="$javasimpletype" />
			</xsl:call-template>
		</xsl:variable>

		<!-- If the object is not required, write a isSetType() method -->
		<xsl:if test="not(@required = 'true')">
			<xsl:text>
   /**
    * As the parameter is optional, this method checks whether this parameter
    * has been sent.
    *
    * @return
    *    true is the parameter has been sent, false otherwise.
    */
   boolean isSet</xsl:text>
			<xsl:value-of select="$hungarianName" />
			<xsl:text>() {
      return _</xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text> != null;
   }
			</xsl:text>
		</xsl:if>
		<!-- Generates the get method. -->
		<xsl:text><![CDATA[

   /**
    * Gets the value of the ]]></xsl:text>
		<xsl:choose>
			<xsl:when test="@required = 'true'">
				<xsl:text>required</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>optional</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text><![CDATA[ input parameter <em>]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[</em>.
    *
    * @return
    *    the value of the <em>]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[</em> input parameter]]></xsl:text>
		<xsl:choose>
			<xsl:when test="not($basetype = '_text')">.</xsl:when>
			<xsl:otherwise>
				<xsl:text><![CDATA[, never <code>null</code>.]]></xsl:text>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:if test="not(@required = 'true')">
			<xsl:text>
    *
    * @throw ParameterNotInitializedException
    *    if the value has not been set.</xsl:text>
		</xsl:if>
		<xsl:text>
    */
   </xsl:text>
		<xsl:value-of select="$javasimpletype" />
		<xsl:text> get</xsl:text>
		<xsl:value-of select="$hungarianName" />
		<xsl:text>() </xsl:text>
		<xsl:if test="not(@required = 'true')">
			<xsl:text>throws org.xins.server.ParameterNotInitializedException </xsl:text>
		</xsl:if>
		<xsl:text>{
      </xsl:text>
		<xsl:choose>
			<xsl:when test="@required = 'true'">
				<xsl:text>return _</xsl:text>
				<xsl:value-of select="@name" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>if (!isSet</xsl:text>
				<xsl:value-of select="$hungarianName" />
				<xsl:text>()) {
         throw new org.xins.server.ParameterNotInitializedException("</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>");
      }
      return _</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:if test="$typeIsPrimary = 'true'">
					<xsl:text>.</xsl:text>
					<xsl:value-of select="$javasimpletype" />
					<xsl:text>Value()</xsl:text>
				</xsl:if>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text>;
   }</xsl:text>
	</xsl:template>

</xsl:stylesheet>

