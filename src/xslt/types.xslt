<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:include href="standard_types.xslt" />
	<xsl:include href="firstline.xslt"      />

	<!--
	* Returns the name of the file for the specified type in the specified
	* API.
	*
	* @param specsdir
	*    the specification directory for the concerning XINS project, must be
	*    specified.
	*
	* @param api
	*    the name of the API to which the type belongs, must be specified.
	*
	* @param type
	*    the name of the type, must be specified.
	*
	* @return
	*    the name of the file that should contain the definition of the
	*    specified type in the specified API.
	-->
	<xsl:template name="file_for_type">
		<xsl:param name="specsdir" />
		<xsl:param name="api"      />
		<xsl:param name="type"     />

		<xsl:if test="string-length($specsdir) &lt; 1">
			<xsl:message terminate="yes">Parameter 'specsdir' is mandatory.</xsl:message>
		</xsl:if>
		<xsl:if test="string-length($api) &lt; 1">
			<xsl:message terminate="yes">Parameter 'api' is mandatory.</xsl:message>
		</xsl:if>
		<xsl:if test="string-length($type) &lt; 1">
			<xsl:message terminate="yes">Parameter 'type' is mandatory.</xsl:message>
		</xsl:if>

		<xsl:value-of select="concat($specsdir, '/', $api, '/', $type, '.typ')" />
	</xsl:template>

	<xsl:template name="typelink">
		<xsl:param name="api"      />
		<xsl:param name="specsdir" />
		<xsl:param name="type"     />

		<xsl:if test="string-length($type) &lt; 1">
			<xsl:message terminate="yes">No type specified.</xsl:message>
		</xsl:if>

		<xsl:choose>
			<xsl:when test="starts-with($type, '_')">
				<span>
					<xsl:attribute name="title">
						<xsl:call-template name="firstline">
							<xsl:with-param name="text">
								<xsl:call-template name="description_for_standardtype">
									<xsl:with-param name="type" select="$type" />
								</xsl:call-template>
							</xsl:with-param>
						</xsl:call-template>
					</xsl:attribute>
					<xsl:value-of select="$type" />
				</span>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="typelink_customtype">
					<xsl:with-param name="api"      select="$api" />
					<xsl:with-param name="specsdir" select="$specsdir" />
					<xsl:with-param name="type"     select="$type" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="typelink_customtype">
		<xsl:param name="api"      />
		<xsl:param name="specsdir" />
		<xsl:param name="type"     />

		<xsl:variable name="type_file" select="concat($specsdir, '/', $api, '/', $type, '.typ')" />
		<xsl:variable name="type_url"  select="concat($type, '.html')" />
		<xsl:variable name="type_title">
			<xsl:call-template name="firstline">
				<xsl:with-param name="text">
					<xsl:value-of select="document($type_file)/type/description/text()" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>

		<xsl:if test="not(boolean(document($type_file)))">
			<xsl:message terminate="yes">
				<xsl:text>The type '</xsl:text>
				<xsl:value-of select="$type" />
				<xsl:text>' does not exist.</xsl:text>
			</xsl:message>
		</xsl:if>

		<a>
			<xsl:attribute name="href">
				<xsl:value-of select="$type_url" />
			</xsl:attribute>
			<xsl:attribute name="title">
				<xsl:value-of select="$type_title" />
			</xsl:attribute>
			<xsl:value-of select="$type" />
		</a>
	</xsl:template>

	<xsl:template name="javatypeclass_for_type">
		<xsl:param name="api"      />
		<xsl:param name="specsdir" />
		<xsl:param name="type" />

		<xsl:choose>
			<xsl:when test="starts-with($type, '_')">
				<xsl:call-template name="javatypeclass_for_standardtype">
					<xsl:with-param name="type" select="$type" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="hungarianUpper">
					<xsl:with-param name="text">
						<xsl:value-of select="$type" />
					</xsl:with-param>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--
	* Returns the Java primary data type or Java class for the specified XINS
	* type. This Java data type or class is what values for the specified XINS
	* type will be instances of.
	*
	* @param specsdir
	*    the specification directory for the concerning XINS project, must be
	*    specified.
	*
	* @param api
	*    the name of the API to which the type belongs, must be specified.
	*
	* @param type
	*    the name of the type to lookup the base type for, can be empty.
	*
	* @param required
	*    indication if this is concerning a mandatory value for the specified
	*    type or not.
	*
	* @return
	*    the Java type for the specified XINS type, for example 'boolean',
	*    'byte', 'short', 'int', 'long' or 'java.lang.String'.
	-->
	<xsl:template name="javatype_for_type">
		<xsl:param name="specsdir" />
		<xsl:param name="api"      />
		<xsl:param name="type"     />
		<xsl:param name="required" />

		<xsl:if test="string-length($specsdir) &lt; 1">
			<xsl:message terminate="yes">Parameter 'specsdir' is mandatory.</xsl:message>
		</xsl:if>
		<xsl:if test="string-length($api) &lt; 1">
			<xsl:message terminate="yes">Parameter 'api' is mandatory.</xsl:message>
		</xsl:if>

		<xsl:variable name="basetype">
			<xsl:call-template name="basetype_for_type">
				<xsl:with-param name="specsdir" select="$specsdir" />
				<xsl:with-param name="api"      select="$api"      />
				<xsl:with-param name="type"     select="$type"     />
			</xsl:call-template>
		</xsl:variable>

		<xsl:call-template name="javatype_for_standardtype">
			<xsl:with-param name="type"     select="$basetype" />
			<xsl:with-param name="required" select="$required" />
		</xsl:call-template>
	</xsl:template>

	<!--
	* Returns the base type for the specified type. If the specified type is
	* an empty string, then '_text' is returned. If it is a standard type
	* (i.e. it starts with an underscore), then that standard type is
	* returned. Otherwise the type descriptor file is examined to find the
	* supertype. Then the basetype of that supertype will be examined
	* recursively.
	*
	* @param specsdir
	*    the specification directory for the concerning XINS project, must be
	*    specified.
	*
	* @param api
	*    the name of the API to which the type belongs, must be specified.
	*
	* @param type
	*    the name of the type to lookup the base type for, can be empty.
	*
	* @return
	*    the base type for the specified type, always starting with an
	*    underscore; either _boolean, _int8, _int16, _int32, _int64 or _text.
	-->
	<xsl:template name="basetype_for_type">
		<xsl:param name="specsdir" />
		<xsl:param name="api"      />
		<xsl:param name="type"     />

		<xsl:if test="string-length($specsdir) &lt; 1">
			<xsl:message terminate="yes">Parameter 'specsdir' is mandatory.</xsl:message>
		</xsl:if>
		<xsl:if test="string-length($api) &lt; 1">
			<xsl:message terminate="yes">Parameter 'api' is mandatory.</xsl:message>
		</xsl:if>

		<xsl:choose>
			<xsl:when test="string-length($type) &lt; 1">
				<xsl:text>_text</xsl:text>
			</xsl:when>
			<xsl:when test="starts-with($type, '_')">
				<xsl:value-of select="$type" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="typefile">
					<xsl:call-template name="file_for_type">
						<xsl:with-param name="specsdir" select="$specsdir" />
						<xsl:with-param name="api"      select="$api"      />
						<xsl:with-param name="type"     select="$type"     />
					</xsl:call-template>
				</xsl:variable>
				<xsl:variable name="supertype">
					<xsl:value-of select="document($typefile)/type/@extends" />
				</xsl:variable>

				<xsl:choose>
					<xsl:when test="starts-with($supertype, '_')">
						<xsl:value-of select="$supertype" />
					</xsl:when>
					<xsl:when test="string-length($supertype) &gt; 0">
						<xsl:call-template name="basetype_for_type">
							<xsl:with-param name="api"      select="$api"       />
							<xsl:with-param name="specsdir" select="$specsdir"  />
							<xsl:with-param name="type"     select="$supertype" />
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>_text</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="javatype_from_string_for_type">
		<xsl:param name="specsdir" />
		<xsl:param name="api"      />
		<xsl:param name="type"     />
		<xsl:param name="required" />
		<xsl:param name="variable" />

		<xsl:variable name="basetype">
			<xsl:call-template name="basetype_for_type">
				<xsl:with-param name="specsdir" select="$specsdir" />
				<xsl:with-param name="api"      select="$api"      />
				<xsl:with-param name="type"     select="$type"     />
			</xsl:call-template>
		</xsl:variable>

		<xsl:call-template name="javatype_from_string_for_standardtype">
			<xsl:with-param name="required" select="$required" />
			<xsl:with-param name="type"     select="$basetype"     />
			<xsl:with-param name="variable" select="$variable" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="javatype_to_string_for_type">
		<xsl:param name="api"      />
		<xsl:param name="specsdir" />
		<xsl:param name="required" />
		<xsl:param name="type"     />
		<xsl:param name="variable" />

		<xsl:variable name="basetype">
			<xsl:call-template name="basetype_for_type">
				<xsl:with-param name="specsdir" select="$specsdir" />
				<xsl:with-param name="api"      select="$api"      />
				<xsl:with-param name="type"     select="$type"     />
			</xsl:call-template>
		</xsl:variable>

		<xsl:call-template name="javatype_to_string_for_standardtype">
			<xsl:with-param name="required" select="$required" />
			<xsl:with-param name="type"     select="$basetype" />
			<xsl:with-param name="variable" select="$variable" />
		</xsl:call-template>
	</xsl:template>

	<!--
	* Determines if the specified string is a Java primary data type.
	*
	* @param text
	*    the text of which determine if it is a Java primary data type, can be
	*    empty.
	*
	* @return
	*    the string 'true' if and only if the specified text is either
	*    'boolean', 'char', 'byte', 'short', 'int', 'long', 'float' or
	*    'double'.
	-->
	<xsl:template name="is_java_datatype">
		<xsl:param name="text" />

		<xsl:choose>
			<xsl:when test="$text='boolean' or $text='char' or $text='byte' or $text='short' or $text='int' or $text='long' or $text='float' or $text='double'">
				<xsl:text>true</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>false</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
