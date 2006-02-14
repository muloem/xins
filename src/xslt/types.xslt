<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 Utility XSLT that provides templates to convert a type to something else.

 $Id$

 Copyright 2003-2006 Wanadoo Nederland B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0" xmlns:xd="http://www.pnp-software.com/XSLTdoc">

	<xd:doc type="stylesheet">
		<xd:short>
			Utility XSLT that provides templates to convert a <code>type</code> to
			something else.
			XSLT provides multiple functions which converts the input <code>type</code>
			to some other required <code>type</code>.  <b>Example</b> some example...
		</xd:short>

		<xd:author>Anthony Goubard</xd:author>
		<xd:author>Ernst De Haan</xd:author>
		<xd:copyright>Copyright 2003-2006 Wanadoo Nederland B.V.</xd:copyright>
		<xd:cvsId>$Id$</xd:cvsId>
	</xd:doc>

	<xsl:include href="standard_types.xslt"  />
	<xsl:include href="firstline.xslt"       />
	<xsl:include href="package_for_api.xslt" />

	<xd:doc>
		<xd:short>
			Returns the name of the file for the specified type in the specified API.
			The should contain the definition of the specified type in the specified 
			API.
		</xd:short>
		<xd:param name="specsdir" type="string">
			the specification directory for the concerning XINS project, must be
			specified.
		</xd:param>
		<xd:param name="api" type="string">
			the name of the API to which the type belongs, must be specified.
		</xd:param>
		<xd:param name="type" type="string">
			the name of the type, must be specified.
		</xd:param>
	</xd:doc>
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

		<xsl:value-of select="concat($specsdir, '/', $type, '.typ')" />
	</xsl:template>

	<xsl:template name="typelink">
		<xsl:param name="api"      />
		<xsl:param name="specsdir" />
		<xsl:param name="type"     />

		<xsl:choose>
			<xsl:when test="starts-with($type, '_') or string-length($type) = 0">
				<span xsl:exclude-result-prefixes="text xd">
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

		<xsl:variable name="type_file" select="concat($specsdir, '/', $type, '.typ')" />
		<xsl:variable name="type_node" select="document($type_file)/type" />
		<xsl:variable name="type_url"  select="concat($type, '.html')" />
		<xsl:variable name="type_title">
			<xsl:call-template name="firstline">
				<xsl:with-param name="text" select="$type_node/description/text()" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:if test="not(boolean($type_node))">
			<xsl:message terminate="yes">
				<xsl:text>The type '</xsl:text>
				<xsl:value-of select="$type" />
				<xsl:text>' does not exist.</xsl:text>
			</xsl:message>
		</xsl:if>

		<a xsl:exclude-result-prefixes="text xd">
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
		<xsl:param name="project_node" />
		<xsl:param name="api"          />
		<xsl:param name="specsdir"     />
		<xsl:param name="type"         />

		<xsl:if test="not($project_node)">
			<xsl:message terminate="yes">Mandatory parameter 'project_node' is not defined.</xsl:message>
		</xsl:if>

		<xsl:choose>
			<xsl:when test="starts-with($type, '_') or string-length($type) = 0">
				<xsl:call-template name="javatypeclass_for_standardtype">
					<xsl:with-param name="type" select="$type" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="package_for_type_classes">
					<xsl:with-param name="project_node" select="$project_node" />
					<xsl:with-param name="api"          select="$api"          />
				</xsl:call-template>
				<xsl:text>.</xsl:text>
				<xsl:call-template name="hungarianUpper">
					<xsl:with-param name="text" select="$type" />
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

		<!-- Define parameters -->
		<xsl:param name="project_node" />
		<xsl:param name="specsdir"     />
		<xsl:param name="api"          />
		<xsl:param name="type"         />
		<xsl:param name="required"     />

		<!-- Determine file that defines type -->
		<xsl:variable name="type_file" select="concat($specsdir, '/', $type, '.typ')" />

		<!-- Check preconditions -->
		<xsl:if test="not($project_node)">
			<xsl:message terminate="yes">Parameter 'project_node' is mandatory.</xsl:message>
		</xsl:if>
		<xsl:if test="string-length($specsdir) &lt; 1">
			<xsl:message terminate="yes">Parameter 'specsdir' is mandatory.</xsl:message>
		</xsl:if>
		<xsl:if test="string-length($api) &lt; 1">
			<xsl:message terminate="yes">Parameter 'api' is mandatory.</xsl:message>
		</xsl:if>

		<xsl:choose>
			<!-- Determine Java type for standard type -->
			<xsl:when test="starts-with($type, '_') or string-length($type) = 0">
				<xsl:call-template name="javatype_for_standardtype">
					<xsl:with-param name="type"     select="$type"     />
					<xsl:with-param name="required" select="$required" />
				</xsl:call-template>
			</xsl:when>

			<!-- TODO: Determine Java type for pattern type ???? -->

			<!-- Determine Java type for enum type -->
			<xsl:when test="count(document($type_file)/type/enum/item) &gt; 0">
				<xsl:call-template name="javatype_for_customtype">
					<xsl:with-param name="project_node" select="$project_node" />
					<xsl:with-param name="api"          select="$api"          />
					<xsl:with-param name="type"         select="$type"         />
				</xsl:call-template>
				<xsl:text>.Item</xsl:text>
			</xsl:when>

			<!-- Determine Java type for list type or set type -->
			<xsl:when test="document($type_file)/type/list or document($type_file)/type/set">
				<xsl:call-template name="javatype_for_customtype">
					<xsl:with-param name="project_node" select="$project_node" />
					<xsl:with-param name="api"          select="$api"          />
					<xsl:with-param name="type"         select="$type"         />
				</xsl:call-template>
				<xsl:text>.Value</xsl:text>
			</xsl:when>

			<!-- Determine Java type for base type -->
			<xsl:otherwise>
				<!-- Determine base type -->
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
			</xsl:otherwise>
		</xsl:choose>
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
	*    underscore; either _boolean, _int8, _int16, _int32, _int64, _float32,
	*    _float64, _base64 or _text.
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
				<xsl:variable name="type_file">
					<xsl:call-template name="file_for_type">
						<xsl:with-param name="specsdir" select="$specsdir" />
						<xsl:with-param name="api"      select="$api"      />
						<xsl:with-param name="type"     select="$type"     />
					</xsl:call-template>
				</xsl:variable>
				<xsl:variable name="type_node" select="document($type_file)/type" />

				<xsl:choose>
					<xsl:when test="$type_node/properties">
						<xsl:text>_properties</xsl:text>
					</xsl:when>
					<xsl:when test="$type_node/int8">
						<xsl:text>_int8</xsl:text>
					</xsl:when>
					<xsl:when test="$type_node/int16">
						<xsl:text>_int16</xsl:text>
					</xsl:when>
					<xsl:when test="$type_node/int32">
						<xsl:text>_int32</xsl:text>
					</xsl:when>
					<xsl:when test="$type_node/int64">
						<xsl:text>_int64</xsl:text>
					</xsl:when>
					<xsl:when test="$type_node/float32">
						<xsl:text>_float32</xsl:text>
					</xsl:when>
					<xsl:when test="$type_node/float64">
						<xsl:text>_float64</xsl:text>
					</xsl:when>
					<xsl:when test="$type_node/base64">
						<xsl:text>_base64</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>_text</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="javatype_from_string_for_type">

		<!-- Define parameters -->
		<xsl:param name="specsdir"     />
		<xsl:param name="api"          />
		<xsl:param name="type"         />
		<xsl:param name="required"     />
		<xsl:param name="variable"     />

		<!-- Determine file that defines type -->
		<xsl:variable name="type_file" select="concat($specsdir, '/', $type, '.typ')" />

		<!-- Check preconditions -->
		<xsl:if test="string-length($specsdir) &lt; 1">
			<xsl:message terminate="yes">Parameter 'specsdir' is mandatory.</xsl:message>
		</xsl:if>
		<xsl:if test="string-length($api) &lt; 1">
			<xsl:message terminate="yes">Parameter 'api' is mandatory.</xsl:message>
		</xsl:if>

		<xsl:choose>
			<!-- Determine Java type for standard type -->
			<xsl:when test="starts-with($type, '_') or string-length($type) = 0">
				<xsl:call-template name="javatype_from_string_for_standardtype">
					<xsl:with-param name="required" select="$required" />
					<xsl:with-param name="type"     select="$type"     />
					<xsl:with-param name="variable" select="$variable" />
				</xsl:call-template>
			</xsl:when>

			<!-- Determine Java type for pattern type -->
			<xsl:when test="count(document($type_file)/type/pattern) &gt; 0">
				<xsl:variable name="class">
					<xsl:call-template name="javatype_for_customtype">
						<xsl:with-param name="project_node" select="$project_node" />
						<xsl:with-param name="api"          select="$api"          />
						<xsl:with-param name="type"         select="$type"         />
					</xsl:call-template>
				</xsl:variable>

				<xsl:value-of select="$class" />
				<xsl:choose>
					<xsl:when test="$required = 'true'">
						<xsl:text>.fromStringForRequired(</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>.fromStringForOptional(</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:value-of select="$variable" />
				<xsl:text>)</xsl:text>
			</xsl:when>

			<!-- Determine Java type for enum type -->
			<xsl:when test="count(document($type_file)/type/enum/item) &gt; 0">
				<xsl:variable name="class">
					<xsl:call-template name="javatype_for_customtype">
						<xsl:with-param name="project_node" select="$project_node" />
						<xsl:with-param name="api"          select="$api"          />
						<xsl:with-param name="type"         select="$type"         />
					</xsl:call-template>
				</xsl:variable>

				<xsl:value-of select="$class" />
				<xsl:text>.getItemByValue(</xsl:text>
				<xsl:value-of select="$variable" />
				<xsl:text>)</xsl:text>
			</xsl:when>

			<!-- Determine Java type for list type or set type-->
			<xsl:when test="document($type_file)/type/list or document($type_file)/type/set">
				<xsl:variable name="class">
					<xsl:call-template name="javatype_for_customtype">
						<xsl:with-param name="project_node" select="$project_node" />
						<xsl:with-param name="api"          select="$api"          />
						<xsl:with-param name="type"         select="$type"         />
					</xsl:call-template>
				</xsl:variable>

				<xsl:text>(</xsl:text>
				<xsl:value-of select="$class" />
				<xsl:text>.Value)</xsl:text>
				<xsl:value-of select="$class" />
				<xsl:text>.SINGLETON.fromString(</xsl:text>
				<xsl:value-of select="$variable" />
				<xsl:text>)</xsl:text>
			</xsl:when>

			<!-- Determine Java type for base type -->
			<xsl:otherwise>
				<!-- Determine base type -->
				<xsl:variable name="basetype">
					<xsl:call-template name="basetype_for_type">
						<xsl:with-param name="specsdir" select="$specsdir" />
						<xsl:with-param name="api"      select="$api"      />
						<xsl:with-param name="type"     select="$type"     />
					</xsl:call-template>
				</xsl:variable>

				<xsl:call-template name="javatype_from_string_for_standardtype">
					<xsl:with-param name="required" select="$required" />
					<xsl:with-param name="type"     select="$basetype" />
					<xsl:with-param name="variable" select="$variable" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="javatype_to_string_for_type">

		<!-- Define parameters -->
		<xsl:param name="specsdir"     />
		<xsl:param name="api"          />
		<xsl:param name="type"         />
		<xsl:param name="required"     />
		<xsl:param name="variable"     />

		<!-- Determine file that defines type -->
		<xsl:variable name="type_file" select="concat($specsdir, '/', $type, '.typ')" />

		<!-- Check preconditions -->
		<xsl:if test="string-length($specsdir) &lt; 1">
			<xsl:message terminate="yes">Parameter 'specsdir' is mandatory.</xsl:message>
		</xsl:if>
		<xsl:if test="string-length($api) &lt; 1">
			<xsl:message terminate="yes">Parameter 'api' is mandatory.</xsl:message>
		</xsl:if>

		<xsl:choose>
			<!-- Determine Java type for standard type -->
			<xsl:when test="starts-with($type, '_') or string-length($type) = 0">
				<xsl:call-template name="javatype_to_string_for_standardtype">
					<xsl:with-param name="required" select="$required" />
					<xsl:with-param name="type"     select="$type"     />
					<xsl:with-param name="variable" select="$variable" />
				</xsl:call-template>
			</xsl:when>

			<!-- TODO: Determine Java type for pattern type ???? -->

			<!-- Determine Java type for enum type -->
			<xsl:when test="count(document($type_file)/type/enum/item) &gt; 0">
				<xsl:value-of select="$variable" />
				<xsl:text>.getValue()</xsl:text>
			</xsl:when>

			<!-- Determine Java type for list type or set type -->
			<xsl:when test="document($type_file)/type/list or document($type_file)/type/set">
				<xsl:variable name="class">
					<xsl:call-template name="javatype_for_customtype">
						<xsl:with-param name="project_node" select="$project_node" />
						<xsl:with-param name="api"          select="$api"          />
						<xsl:with-param name="type"         select="$type"         />
					</xsl:call-template>
				</xsl:variable>

				<xsl:value-of select="$class" />
				<xsl:text>.SINGLETON.toString(</xsl:text>
				<xsl:value-of select="$variable" />
				<xsl:text>)</xsl:text>
			</xsl:when>

			<!-- Determine Java type for base type -->
			<xsl:otherwise>
				<!-- Determine base type -->
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
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--
	* Determines if the specified string is a Java primary data type.
	*
	* @param text
	*    the text of which determine if it is a Java primary data type, can be
	*    empty.
	*
	* @return
	*    the text 'true' if and only if the specified text is either
	*    'boolean', 'char', 'byte', 'short', 'int', 'long', 'float' or
	*    'double'; otherwise the string 'false'.
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

	<xsl:template name="javatype_for_customtype">
		<xsl:param name="project_node" />
		<xsl:param name="api"          />
		<xsl:param name="type"         />

		<xsl:call-template name="package_for_type_classes">
			<xsl:with-param name="project_node" select="$project_node" />
			<xsl:with-param name="api"          select="$api"          />
		</xsl:call-template>
		<xsl:text>.</xsl:text>
		<xsl:call-template name="hungarianUpper">
			<xsl:with-param name="text" select="$type" />
		</xsl:call-template>
	</xsl:template>

	<!--
	* Returns the Java class to import for the specified XINS type.
	* This Java data type or class is what values for the specified XINS
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
	* @return
	*    the Java type for the specified XINS type, for example
	*    'java.lang.Boolean', 'org.xins.common.collections.PropertyReader',
	*    'org.xins.common.types.standard.Date',
	*    'com.mycompany.allinone.types.Salutation'.
	-->
	<xsl:template name="javaimport_for_type">

		<!-- Define parameters -->
		<xsl:param name="project_node" />
		<xsl:param name="specsdir"     />
		<xsl:param name="api"          />
		<xsl:param name="type"         />

		<!-- Determine file that defines type -->
		<xsl:variable name="type_file" select="concat($specsdir, '/', $type, '.typ')" />

		<!-- Check preconditions -->
		<xsl:if test="not($project_node)">
			<xsl:message terminate="yes">Parameter 'project_node' is mandatory.</xsl:message>
		</xsl:if>
		<xsl:if test="string-length($specsdir) &lt; 1">
			<xsl:message terminate="yes">Parameter 'specsdir' is mandatory.</xsl:message>
		</xsl:if>
		<xsl:if test="string-length($api) &lt; 1">
			<xsl:message terminate="yes">Parameter 'api' is mandatory.</xsl:message>
		</xsl:if>

		<xsl:choose>
			<!-- Determine Java import for standard type -->
			<xsl:when test="starts-with($type, '_') or string-length($type) = 0">
				<xsl:call-template name="javaimport_for_standardtype">
					<xsl:with-param name="type"     select="$type" />
				</xsl:call-template>
			</xsl:when>

			<!-- Determine Java type for list type or set type -->
			<xsl:when test="count(document($type_file)/type/enum/item) &gt; 0 or document($type_file)/type/list or document($type_file)/type/set">
				<xsl:call-template name="javatype_for_customtype">
					<xsl:with-param name="project_node" select="$project_node" />
					<xsl:with-param name="api"          select="$api"          />
					<xsl:with-param name="type"         select="$type"         />
				</xsl:call-template>
			</xsl:when>

			<!-- Determine Java type for base type -->
			<xsl:otherwise>
				<!-- Determine base type -->
				<xsl:variable name="basetype">
					<xsl:call-template name="basetype_for_type">
						<xsl:with-param name="specsdir" select="$specsdir" />
						<xsl:with-param name="api"      select="$api"      />
						<xsl:with-param name="type"     select="$type"     />
					</xsl:call-template>
				</xsl:variable>

				<xsl:call-template name="javaimport_for_standardtype">
					<xsl:with-param name="type"     select="$basetype" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="javaclass_for_javatype">
		<xsl:param name="javatype" />

		<xsl:choose>
			<xsl:when test="$javatype = 'boolean'">java.lang.Boolean</xsl:when>
			<xsl:when test="$javatype = 'char'">java.lang.Character</xsl:when>
			<xsl:when test="$javatype = 'byte'">java.lang.Byte</xsl:when>
			<xsl:when test="$javatype = 'short'">java.lang.Short</xsl:when>
			<xsl:when test="$javatype = 'int'">java.lang.Integer</xsl:when>
			<xsl:when test="$javatype = 'long'">java.lang.Long</xsl:when>
			<xsl:when test="$javatype = 'float'">java.lang.Float</xsl:when>
			<xsl:when test="$javatype = 'double'">java.lang.Double</xsl:when>
			<xsl:otherwise>
				<xsl:message terminate="yes">
					<xsl:text>Unrecognized Java datatype '</xsl:text>
					<xsl:value-of select="$javatype" />
					<xsl:text>'.</xsl:text>
				</xsl:message>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!--
	* Returns the SOAP type for the specified XINS type.
	* The type could be a standard type or a defined type.
	* For more information about the returned type: http://www.w3.org/TR/xmlschema-2
	*
	* @param project_node
	*    the content of the xins-project.xml file.
	*
	* @param specsdir
	*    the specification directory for the concerning XINS project, must be
	*    specified.
	*
	* @param api
	*    the name of the API to which the type belongs, must be specified.
	*
	* @param type
	*    the name of the type of the parameter, can be empty.
	*
	* @return
	*    the SOAP type as defined at http://www.w3.org/2001/XMLSchema.xsd.
	*    Examples: integer, string, base64Binary
	-->
	<xsl:template name="soaptype_for_type">

		<!-- Define parameters -->
		<xsl:param name="project_node" />
		<xsl:param name="specsdir"     />
		<xsl:param name="api"          />
		<xsl:param name="type"         />
		
		<xsl:variable name="paramtype">
			<xsl:choose>
				<xsl:when test="string-length($type) = 0 or starts-with($type, '_')">
					<xsl:value-of select="$type" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="basetype_for_type">
						<xsl:with-param name="project_node" select="$project_node" />
						<xsl:with-param name="specsdir" select="$specsdir" />
						<xsl:with-param name="api" select="$api" />
						<xsl:with-param name="type" select="$type" />
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<!-- Note that boolean, date and timestamp values should be translated. -->
		<xsl:choose>
			<xsl:when test="string-length($paramtype) = 0 or $paramtype = '_text'">string</xsl:when>
			<xsl:when test="$paramtype = '_boolean'">boolean</xsl:when>
			<xsl:when test="$paramtype = '_int8'">byte</xsl:when>
			<xsl:when test="$paramtype = '_int16'">short</xsl:when>
			<xsl:when test="$paramtype = '_int32'">integer</xsl:when>
			<xsl:when test="$paramtype = '_int64'">long</xsl:when>
			<xsl:when test="$paramtype = '_float32'">float</xsl:when>
			<xsl:when test="$paramtype = '_float64'">double</xsl:when>
			<xsl:when test="$paramtype = '_base64'">base64Binary</xsl:when>
			<xsl:when test="$paramtype = '_url'">anyURI</xsl:when>
			<xsl:when test="$paramtype = '_date'">date</xsl:when>
			<xsl:when test="$paramtype = '_timestamp'">dateTime</xsl:when>
			<xsl:when test="$paramtype = '_properties'">string</xsl:when>
			<xsl:when test="$paramtype = '_descriptor'">string</xsl:when>
			<xsl:otherwise>
				<xsl:message terminate="yes">
					<xsl:text>Unrecognized type datatype '</xsl:text>
					<xsl:value-of select="$paramtype" />
					<xsl:text>'.</xsl:text>
				</xsl:message>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--
	* Returns the description of the type in Open Document Format.
	* The type could be a standard type or a defined type.
	*
	* @param project_node
	*    the content of the xins-project.xml file.
	*
	* @param specsdir
	*    the specification directory for the concerning XINS project, must be
	*    specified.
	*
	* @param api
	*    the name of the API to which the type belongs, must be specified.
	*
	* @param type
	*    the name of the type of the parameter, can be empty.
	*
	* @return
	*    the SOAP type as defined at http://www.w3.org/2001/XMLSchema.xsd.
	*    Examples: integer, string, base64Binary
	-->
	<xsl:template name="opendoc_for_type">

		<!-- Define parameters -->
		<xsl:param name="specsdir"     />
		<xsl:param name="api"          />
		<xsl:param name="type"         />
		
		<xsl:choose>
			<xsl:when test="string-length($type) = 0 or starts-with($type, '_')">
				<text:p text:style-name="Standard">
					<xsl:call-template name="description_for_standardtype">
						<xsl:with-param name="type" select="$type" />
					</xsl:call-template>
				</text:p>
			</xsl:when>
			<xsl:otherwise>
			
				<!-- Determine file that defines type -->
				<xsl:variable name="type_file" select="concat($specsdir, '/', $type, '.typ')" />
				<xsl:variable name="type_node" select="document($type_file)/type" />

				<xsl:choose>
					<xsl:when test="$type_node/pattern">
						<text:p text:style-name="Standard">
							<xsl:text>Pattern: </xsl:text>
							<text:span text:style-name="Code">
								<xsl:value-of select="$type_node/pattern/text()" />
							</text:span>
						</text:p>
					</xsl:when>
					<xsl:when test="$type_node/enum">
						<text:p text:style-name="Standard">
							<xsl:text>One of the value: </xsl:text>
						</text:p>
						<text:p text:style-name="Standard">
							<text:span text:style-name="Code">
								<xsl:for-each select="$type_node/enum/item">
									<xsl:value-of select="@value" />
									<xsl:if test="position() &lt; last()">
										<text:line-break/>
									</xsl:if>
								</xsl:for-each>
							</text:span>
						</text:p>
					</xsl:when>
					<xsl:when test="$type_node/int8 | $type_node/int16 | $type_node/int32 | $type_node/int64 | $type_node/float32 | $type_node/float64">
						<text:p text:style-name="Standard">
							<xsl:variable name="basetype">
								<xsl:call-template name="basetype_for_type">
									<xsl:with-param name="specsdir" select="$specsdir" />
									<xsl:with-param name="api" select="$api" />
									<xsl:with-param name="type" select="$type" />
								</xsl:call-template>
							</xsl:variable>
							<xsl:call-template name="description_for_standardtype">
								<xsl:with-param name="type" select="$basetype" />
							</xsl:call-template>
							<xsl:if test="$type_node//@min">
								<text:line-break/>
								<xsl:text>The minimum value is </xsl:text>
									<xsl:value-of select="$type_node//@min" />
								<xsl:text>.</xsl:text>
							</xsl:if>
							<xsl:if test="$type_node//@max">
								<text:line-break/>
								<xsl:text>The maximum value is </xsl:text>
									<xsl:value-of select="$type_node//@max" />
								<xsl:text>.</xsl:text>
							</xsl:if>
						</text:p>
					</xsl:when>
					<xsl:when test="$type_node/base64">
						<text:p text:style-name="Standard">
							<xsl:call-template name="description_for_standardtype">
								<xsl:with-param name="type" select="'_base64'" />
							</xsl:call-template>
							<xsl:if test="$type_node/base64/@min">
								<text:line-break/>
								<xsl:text>The minimum length of the binary should be </xsl:text>
									<xsl:value-of select="$type_node/base64/@min" />
								<xsl:text> bytes.</xsl:text>
							</xsl:if>
							<xsl:if test="$type_node/base64/@max">
								<text:line-break/>
								<xsl:text>The maximum length of the binary should be </xsl:text>
									<xsl:value-of select="$type_node/base64/@max" />
								<xsl:text> bytes.</xsl:text>
							</xsl:if>
						</text:p>
					</xsl:when>
					<xsl:otherwise>
						<!-- TODO the other cases ($type_node/properties, $type_node/list ...) -->
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
		
</xsl:stylesheet>
