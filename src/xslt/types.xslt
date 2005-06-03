<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 Utility XSLT that provides templates to convert a type to something else.

 $Id$

 Copyright 2003-2005 Wanadoo Nederland B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:include href="standard_types.xslt"  />
	<xsl:include href="firstline.xslt"       />
	<xsl:include href="package_for_api.xslt" />

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

		<xsl:value-of select="concat($specsdir, '/', $type, '.typ')" />
	</xsl:template>

	<xsl:template name="typelink">
		<xsl:param name="api"      />
		<xsl:param name="specsdir" />
		<xsl:param name="type"     />

		<xsl:choose>
			<xsl:when test="starts-with($type, '_') or string-length($type) = 0">
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

		<xsl:variable name="type_file" select="concat($specsdir, '/', $type, '.typ')" />
		<xsl:variable name="type_url"  select="concat($type, '.html')" />
		<xsl:variable name="type_title">
			<xsl:call-template name="firstline">
				<xsl:with-param name="text" select="document($type_file)/type/description/text()" />
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
		<xsl:param name="project_file" />
		<xsl:param name="api"          />
		<xsl:param name="specsdir"     />
		<xsl:param name="type"         />

		<xsl:if test="string-length($project_file) &lt; 1">
			<xsl:message terminate="yes">Mandatory parameter 'project_file' is not defined.</xsl:message>
		</xsl:if>

		<xsl:choose>
			<xsl:when test="starts-with($type, '_') or string-length($type) = 0">
				<xsl:call-template name="javatypeclass_for_standardtype">
					<xsl:with-param name="type" select="$type" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="package_for_type_classes">
					<xsl:with-param name="project_file" select="$project_file" />
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
		<xsl:param name="project_file" />
		<xsl:param name="specsdir"     />
		<xsl:param name="api"          />
		<xsl:param name="type"         />
		<xsl:param name="required"     />

		<!-- Determine file that defines type -->
		<xsl:variable name="type_file" select="concat($specsdir, '/', $type, '.typ')" />

		<!-- Check preconditions -->
		<xsl:if test="string-length($project_file) &lt; 1">
			<xsl:message terminate="yes">Parameter 'project_file' is mandatory.</xsl:message>
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
					<xsl:with-param name="project_file" select="$project_file" />
					<xsl:with-param name="api"          select="$api"          />
					<xsl:with-param name="type"         select="$type"         />
				</xsl:call-template>
				<xsl:text>.Item</xsl:text>
			</xsl:when>

			<!-- Determine Java type for list type or set type -->
			<xsl:when test="document($type_file)/type/list or document($type_file)/type/set">
				<xsl:call-template name="javatype_for_customtype">
					<xsl:with-param name="project_file" select="$project_file" />
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
				<xsl:variable name="typefile">
					<xsl:call-template name="file_for_type">
						<xsl:with-param name="specsdir" select="$specsdir" />
						<xsl:with-param name="api"      select="$api"      />
						<xsl:with-param name="type"     select="$type"     />
					</xsl:call-template>
				</xsl:variable>

				<xsl:choose>
					<xsl:when test="document($typefile)/type/properties">
						<xsl:text>_properties</xsl:text>
					</xsl:when>
					<xsl:when test="document($typefile)/type/int8">
						<xsl:text>_int8</xsl:text>
					</xsl:when>
					<xsl:when test="document($typefile)/type/int16">
						<xsl:text>_int16</xsl:text>
					</xsl:when>
					<xsl:when test="document($typefile)/type/int32">
						<xsl:text>_int32</xsl:text>
					</xsl:when>
					<xsl:when test="document($typefile)/type/int64">
						<xsl:text>_int64</xsl:text>
					</xsl:when>
					<xsl:when test="document($typefile)/type/float32">
						<xsl:text>_float32</xsl:text>
					</xsl:when>
					<xsl:when test="document($typefile)/type/float64">
						<xsl:text>_float64</xsl:text>
					</xsl:when>
					<xsl:when test="document($typefile)/type/base64">
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
						<xsl:with-param name="project_file" select="$project_file" />
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
						<xsl:with-param name="project_file" select="$project_file" />
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
						<xsl:with-param name="project_file" select="$project_file" />
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
						<xsl:with-param name="project_file" select="$project_file" />
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
		<xsl:param name="project_file" />
		<xsl:param name="api"          />
		<xsl:param name="type"         />

		<xsl:call-template name="package_for_type_classes">
			<xsl:with-param name="project_file" select="$project_file" />
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
		<xsl:param name="project_file" />
		<xsl:param name="specsdir"     />
		<xsl:param name="api"          />
		<xsl:param name="type"         />

		<!-- Determine file that defines type -->
		<xsl:variable name="type_file" select="concat($specsdir, '/', $type, '.typ')" />

		<!-- Check preconditions -->
		<xsl:if test="string-length($project_file) &lt; 1">
			<xsl:message terminate="yes">Parameter 'project_file' is mandatory.</xsl:message>
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
					<xsl:with-param name="project_file" select="$project_file" />
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
	*
	* @param project_file
	*    the location of the xins-project.xml file.
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
		<xsl:param name="project_file" />
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
						<xsl:with-param name="project_file" select="$project_file" />
						<xsl:with-param name="specsdir" select="$specsdir" />
						<xsl:with-param name="api" select="$api" />
						<xsl:with-param name="type" select="$type" />
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
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
			<!-- the SOAP type for date and time is yyyy-mm-ddThh:mm:ss -->
			<xsl:when test="$paramtype = '_date'">string</xsl:when>
			<xsl:when test="$paramtype = '_timestamp'">string</xsl:when>
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
</xsl:stylesheet>
