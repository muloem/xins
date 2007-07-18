<?xml version="1.0" encoding="US-ASCII"?>
<!--
 XSLT that generates the build.xml used to compile the different APIs.

 $Id$

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Define parameters -->
	<xsl:param name="xins_home"    />
	<xsl:param name="project_home" />
	<xsl:param name="builddir"     />
	<xsl:param name="xins_version" />

	<!-- Perform includes -->
	<xsl:include href="hungarian.xslt"       />
	<xsl:include href="package_for_api.xslt" />

	<xsl:output indent="yes" />

	<xsl:variable name="project_file"      select="concat($project_home, '/xins-project.xml')"  />
	<xsl:variable name="project_node"      select="document($project_file)/project"             />
	<xsl:variable name="specsdir">
		<xsl:value-of select="$project_home" />
		<xsl:text>/</xsl:text>
		<xsl:choose>
			<xsl:when test="//project/@specsdir">
				<xsl:value-of select="//project/@specsdir" />
			</xsl:when>
			<xsl:otherwise>src/specs</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="dependenciesDir">
		<xsl:value-of select="$project_home" />
		<xsl:if test="//project/@dependenciesdir">
			<xsl:text>/</xsl:text>
			<xsl:value-of select="//project/@dependenciesdir" />
		</xsl:if>
	</xsl:variable>

	<xsl:template match="project">
		<project name="{//project/@name}" default="help" basedir="..">

			<import file="{$xins_home}/src/ant/build-macros.xml" optional="false" />
			<import file="{$xins_home}/src/ant/build-create.xml" optional="true" />
			<import file="{$xins_home}/src/ant/build-tools.xml" optional="true" />

			<target name="-load-properties-xins">
				<property name="xins_home" value="{$xins_home}" />
				<property name="project_home" value="{$project_home}" />
				<property name="builddir" value="{$builddir}" />
				<property name="cvsweb" value="{cvsweb/@href}" />
				<xsl:if test="@domain">
					<property name="domain" value="{@domain}" />
				</xsl:if>
				<property name="apis.list">
					<xsl:attribute name="value">
						<xsl:for-each select="//project/api/impl">
							<xsl:if test="position() &gt; 1">,</xsl:if>
							<xsl:value-of select="../@name" />
							<xsl:if test="@name">
								<xsl:value-of select="concat('-', @name)" />
							</xsl:if>
						</xsl:for-each>
					</xsl:attribute>
				</property>
			</target>

			<target name="specdocs" description="Generates all specification docs">
				<xsl:attribute name="depends">
					<xsl:text>index-specdocs</xsl:text>
					<xsl:for-each select="api">
						<xsl:text>,specdocs-</xsl:text>
						<xsl:value-of select="@name" />
					</xsl:for-each>
				</xsl:attribute>
			</target>

			<target name="classes" description="Compiles all Java classes">
				<xsl:attribute name="depends">
					<xsl:for-each select="//project/api/impl">
						<xsl:if test="position() &gt; 1">,</xsl:if>
						<xsl:text>classes-api-</xsl:text>
						<xsl:value-of select="../@name" />
						<xsl:if test="@name">
							<xsl:value-of select="concat('-', @name)" />
						</xsl:if>
					</xsl:for-each>
				</xsl:attribute>
			</target>

			<target name="clients" description="Generates all CAPI JAR files, corresponding Javadoc and the specdocs">
				<xsl:attribute name="depends">
					<xsl:for-each select="api">
						<xsl:if test="position() &gt; 1">,</xsl:if>
						<xsl:text>client-</xsl:text>
						<xsl:value-of select="@name" />
					</xsl:for-each>
				</xsl:attribute>
			</target>

			<target name="javadoc-capis" description="Generates all CAPI Javadoc">
				<xsl:attribute name="depends">
					<xsl:for-each select="api">
						<xsl:if test="position() &gt; 1">,</xsl:if>
						<xsl:text>javadoc-capi-</xsl:text>
						<xsl:value-of select="@name" />
					</xsl:for-each>
				</xsl:attribute>
			</target>

			<target name="capis" description="Generates all CAPI JAR files">
				<xsl:attribute name="depends">
					<xsl:for-each select="api">
						<xsl:if test="position() &gt; 1">,</xsl:if>
						<xsl:text>jar-</xsl:text>
						<xsl:value-of select="@name" />
					</xsl:for-each>
				</xsl:attribute>
			</target>

			<target name="javadoc-apis" description="Creates the Javadoc for all APIs">
				<xsl:attribute name="depends">
					<xsl:for-each select="//project/api/impl">
						<xsl:if test="position() &gt; 1">,</xsl:if>
						<xsl:text>javadoc-api-</xsl:text>
						<xsl:value-of select="../@name" />
						<xsl:if test="@name">
							<xsl:value-of select="concat('-', @name)" />
						</xsl:if>
					</xsl:for-each>
				</xsl:attribute>
			</target>

			<target name="wars" description="Creates the WARs for all APIs">
				<xsl:attribute name="depends">
					<xsl:for-each select="//project/api/impl">
						<xsl:if test="position() &gt; 1">,</xsl:if>
						<xsl:text>war-</xsl:text>
						<xsl:value-of select="../@name" />
						<xsl:if test="@name">
							<xsl:value-of select="concat('-', @name)" />
						</xsl:if>
					</xsl:for-each>
				</xsl:attribute>
			</target>

			<target name="tests" description="Tests all APIs that have tests.">
				<xsl:attribute name="depends">
					<xsl:for-each select="//project/api/test">
						<xsl:if test="position() &gt; 1">,</xsl:if>
						<xsl:text>test-</xsl:text>
						<xsl:value-of select="../@name" />
					</xsl:for-each>
				</xsl:attribute>
			</target>

			<target name="all" description="Generates everything">
				<xsl:attribute name="depends">
					<xsl:for-each select="//project/api">
						<xsl:if test="position() &gt; 1">,</xsl:if>
						<xsl:text>all-</xsl:text>
						<xsl:value-of select="@name" />
					</xsl:for-each>
				</xsl:attribute>
			</target>

			<xsl:apply-templates select="api" />
		</project>
	</xsl:template>

	<xsl:template match="api">
		<xsl:variable name="api" select="@name" />
		<xsl:variable name="api_specsdir" select="concat($project_home, '/apis/', $api, '/spec')" />
		<xsl:variable name="api_file" select="concat($api_specsdir, '/api.xml')" />
		<xsl:variable name="api_node" select="document($api_file)/api" />
		<xsl:variable name="typeClassesDir"    select="concat($builddir, '/classes-types/', $api)" />
		<xsl:variable name="functionIncludes">
			<xsl:for-each select="$api_node/function">
				<xsl:if test="position() &gt; 1">,</xsl:if>
				<xsl:value-of select="@name" />
				<xsl:text>.fnc</xsl:text>
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="typeIncludes">
			<xsl:for-each select="$api_node/type">
				<xsl:if test="not(contains(@name, '/'))">
					<xsl:if test="position() &gt; 1">,</xsl:if>
					<xsl:value-of select="@name" />
					<xsl:text>.typ</xsl:text>
				</xsl:if>
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="typeIncludesAll">
			<xsl:for-each select="$api_node/type">
				<xsl:if test="position() &gt; 1">,</xsl:if>
				<xsl:choose>
					<xsl:when test="contains(@name, '/')">
						<xsl:value-of select="concat('../../', substring-before(@name, '/'), '/spec/', substring-after(@name, '/'), '.typ')" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="@name" />
						<xsl:text>.typ</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="resultcodeIncludes">
			<xsl:for-each select="$api_node/resultcode">
				<xsl:if test="not(contains(@name, '/'))">
					<xsl:if test="position() &gt; 1">,</xsl:if>
					<xsl:value-of select="@name" />
					<xsl:text>.rcd</xsl:text>
				</xsl:if>
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="resultcodeIncludesAll">
			<xsl:for-each select="$api_node/resultcode">
				<xsl:if test="position() &gt; 1">,</xsl:if>
				<xsl:choose>
					<xsl:when test="contains(@name, '/')">
						<xsl:value-of select="concat('../../', substring-before(@name, '/'), '/spec/', substring-after(@name, '/'), '.rcd')" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="@name" />
						<xsl:text>.rcd</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="categoryIncludes">
			<xsl:for-each select="$api_node/category">
				<xsl:if test="position() &gt; 1">,</xsl:if>
				<xsl:value-of select="@name" />
				<xsl:text>.cat</xsl:text>
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="clientPackage">
			<xsl:call-template name="package_for_client_api">
				<xsl:with-param name="project_node" select="$project_node" />
				<xsl:with-param name="api" select="$api" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="clientPackageAsDir" select="translate($clientPackage, '.','/')" />
		<xsl:variable name="apiHasTypes" select="boolean($api_node/type)" />
		<xsl:variable name="package">
			<xsl:call-template name="package_for_server_api">
				<xsl:with-param name="project_node" select="$project_node" />
				<xsl:with-param name="api" select="$api" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="packageAsDir" select="translate($package, '.','/')" />

		<target name="-load-properties-{$api}" depends="-load-properties">
			<property name="api" value="@name" />
			<property name="api_specsdir" value="${{project_home}}/apis/${{api}}/spec" />
			<property name="typeIncludesAll" value="{$typeIncludesAll}" />
			<property name="resultcodeIncludesAll" value="{$resultcodeIncludesAll}" />
		</target>

		<target name="specdocs-{$api}" depends="index-specdocs" description="Generates all specification docs for the '{$api}' API">
      <mkdir dir="{$builddir}/specdocs/{$api}" />
			<dependset>
				<srcfilelist dir="{$api_specsdir}" files="{$functionIncludes}" />
				<xsl:if test="string-length($typeIncludesAll) &gt; 0">
					<srcfilelist dir="{$api_specsdir}" files="{$typeIncludesAll}" />
				</xsl:if>
				<xsl:if test="string-length($resultcodeIncludesAll) &gt; 0">
					<srcfilelist dir="{$api_specsdir}" files="{$resultcodeIncludesAll}" />
				</xsl:if>
				<xsl:if test="string-length($categoryIncludes) &gt; 0">
					<srcfilelist dir="{$api_specsdir}" files="{$categoryIncludes}" />
				</xsl:if>
				<targetfileset dir="{$builddir}/specdocs/{$api}" includes="index.html" />
			</dependset>
			<dependset>
				<srcfilelist dir="{$api_specsdir}" files="api.xml" />
				<targetfileset dir="{$builddir}/specdocs/{$api}" includes="*.html" />
			</dependset>
			<xsl:if test="environments">
				<xsl:variable name="env_dir" select="concat($project_home, '/apis/', $api)" />
				<dependset>
					<srcfilelist dir="{$env_dir}" files="environments.xml" />
					<targetfileset dir="{$builddir}/specdocs/{$api}" includes="*.html" />
				</dependset>
			</xsl:if>
			<copy todir="{$builddir}/specdocs/{$api}" file="{$xins_home}/src/css/specdocs/style.css" />
			<copy todir="{$builddir}/specdocs/{$api}" file="{$xins_home}/src/xslt/testforms/testforms.js" />
			<copy tofile="{$builddir}/specdocs/{$api}/favicon.ico" file="{$xins_home}/xins.ico" />
			<xmlvalidate file="{$api_file}" warn="false">
				<xmlcatalog refid="all-dtds" />
			</xmlvalidate>
			<xslt
			in="{$api_file}"
			out="{$builddir}/specdocs/{$api}/index.html"
			style="{$xins_home}/src/xslt/specdocs/api_to_html.xslt">
				<xmlcatalog refid="all-dtds" />
				<param name="xins_version" expression="{$xins_version}" />
				<param name="project_home" expression="{$project_home}" />
				<param name="project_file" expression="{$project_file}" />
				<param name="specsdir"     expression="{$api_specsdir}" />
				<param name="api"          expression="{$api}"          />
			</xslt>
			<xmlvalidate warn="false">
				<fileset dir="{$api_specsdir}" includes="{$functionIncludes}"/>
				<xmlcatalog refid="all-dtds" />
			</xmlvalidate>
			<xslt
			basedir="{$api_specsdir}"
			destdir="{$builddir}/specdocs/{$api}"
			style="{$xins_home}/src/xslt/specdocs/function_to_html.xslt"
			includes="{$functionIncludes}"
			reloadstylesheet="${{reload.stylesheet}}">
				<xmlcatalog refid="all-dtds" />
				<param name="xins_version" expression="{$xins_version}" />
				<param name="project_home" expression="{$project_home}" />
				<param name="project_file" expression="{$project_file}" />
				<param name="specsdir"     expression="{$api_specsdir}" />
				<param name="api"          expression="{$api}"          />
				<param name="api_file"     expression="{$api_file}"     />
			</xslt>
			<xsl:if test="string-length($typeIncludes) &gt; 0">
				<xmlvalidate warn="false">
					<fileset dir="{$api_specsdir}" includes="{$typeIncludes}"/>
					<xmlcatalog refid="all-dtds" />
				</xmlvalidate>
				<xslt
				basedir="{$api_specsdir}"
				destdir="{$builddir}/specdocs/{$api}"
				style="{$xins_home}/src/xslt/specdocs/type_to_html.xslt"
				includes="{$typeIncludes}"
				reloadstylesheet="${{reload.stylesheet}}">
					<xmlcatalog refid="all-dtds" />
					<param name="xins_version" expression="{$xins_version}" />
					<param name="project_home" expression="{$project_home}" />
					<param name="project_file" expression="{$project_file}" />
					<param name="specsdir"     expression="{$api_specsdir}" />
					<param name="api"          expression="{$api}"          />
					<param name="api_file"     expression="{$api_file}"     />
				</xslt>
			</xsl:if>
			<xsl:for-each select="$api_node/type">
				<xsl:if test="contains(@name, '/')">
					<xsl:variable name="in_type_file"
					select="concat($project_home, '/apis/', substring-before(@name, '/'), '/spec/', substring-after(@name, '/'), '.typ')" />
					<xsl:variable name="out_html_file"
					select="concat($builddir, '/specdocs/', $api, '/', substring-after(@name, '/'), '.html')" />
					<xslt
					in="{$in_type_file}"
					out="{$out_html_file}"
					style="{$xins_home}/src/xslt/specdocs/type_to_html.xslt">
						<xmlcatalog refid="all-dtds" />
						<param name="xins_version" expression="{$xins_version}" />
						<param name="project_home" expression="{$project_home}" />
						<param name="project_file" expression="{$project_file}" />
						<param name="specsdir"     expression="{$api_specsdir}" />
						<param name="api"          expression="{$api}"          />
						<param name="api_file"     expression="{$api_file}"     />
					</xslt>
				</xsl:if>
			</xsl:for-each>
			<xsl:if test="string-length($resultcodeIncludes) &gt; 0">
				<xmlvalidate warn="false">
					<fileset dir="{$api_specsdir}" includes="{$resultcodeIncludes}"/>
					<xmlcatalog refid="all-dtds" />
				</xmlvalidate>
				<xslt
				basedir="{$api_specsdir}"
				destdir="{$builddir}/specdocs/{$api}"
				style="{$xins_home}/src/xslt/specdocs/resultcode_to_html.xslt"
				includes="{$resultcodeIncludes}"
				reloadstylesheet="${{reload.stylesheet}}">
					<xmlcatalog refid="all-dtds" />
					<param name="xins_version" expression="{$xins_version}" />
					<param name="project_home" expression="{$project_home}" />
					<param name="project_file" expression="{$project_file}" />
					<param name="specsdir"     expression="{$api_specsdir}" />
					<param name="api"          expression="{$api}"          />
					<param name="api_file"     expression="{$api_file}"     />
				</xslt>
			</xsl:if>
			<xsl:for-each select="$api_node/resultcode">
				<xsl:if test="contains(@name, '/')">
					<xsl:variable name="in_resultcode_file"
					select="concat($project_home, '/apis/', substring-before(@name, '/'), '/spec/', substring-after(@name, '/'), '.rcd')" />
					<xsl:variable name="out_html_file"
					select="concat($builddir, '/specdocs/', $api, '/', substring-after(@name, '/'), '.html')" />
					<xslt
					in="{$in_resultcode_file}"
					out="{$out_html_file}"
					style="{$xins_home}/src/xslt/specdocs/resultcode_to_html.xslt">
						<xmlcatalog refid="all-dtds" />
						<param name="xins_version" expression="{$xins_version}" />
						<param name="project_home" expression="{$project_home}" />
						<param name="project_file" expression="{$project_file}" />
						<param name="specsdir"     expression="{$api_specsdir}" />
						<param name="api"          expression="{$api}"          />
						<param name="api_file"     expression="{$api_file}"     />
					</xslt>
				</xsl:if>
			</xsl:for-each>
			<xsl:if test="$api_node/category">
				<xmlvalidate warn="false">
					<fileset dir="{$api_specsdir}" includes="{$categoryIncludes}"/>
					<xmlcatalog refid="all-dtds" />
				</xmlvalidate>
				<xslt
				basedir="{$api_specsdir}"
				destdir="{$builddir}/specdocs/{$api}"
				style="{$xins_home}/src/xslt/specdocs/category_to_html.xslt"
				includes="{$categoryIncludes}">
					<xmlcatalog refid="all-dtds" />
					<param name="xins_version" expression="{$xins_version}" />
					<param name="specsdir"     expression="{$api_specsdir}" />
					<param name="api"          expression="{$api}"          />
				</xslt>
			</xsl:if>
			<xsl:if test="not(environments)">
				<xslt
				basedir="{$api_specsdir}"
				destdir="{$builddir}/specdocs/{$api}"
				style="{$xins_home}/src/xslt/testforms/function_to_html.xslt"
				includes="{$functionIncludes}"
				extension="-testform.html"
				reloadstylesheet="${{reload.stylesheet}}">
					<xmlcatalog refid="all-dtds" />
					<param name="xins_version" expression="{$xins_version}" />
					<param name="project_home" expression="{$project_home}" />
					<param name="project_file" expression="{$project_file}" />
					<param name="specsdir"     expression="{$api_specsdir}" />
					<param name="api"          expression="{$api}"          />
					<param name="api_file"     expression="{$api_file}"     />
				</xslt>
			</xsl:if>
			<xsl:if test="environments">
				<xsl:variable name="env_file" select="concat($project_home, '/apis/', $api, '/environments.xml')" />
				<xmlvalidate file="{$env_file}" warn="false">
					<xmlcatalog refid="all-dtds" />
				</xmlvalidate>
			</xsl:if>
			<xsl:variable name="env_file" select="''" />
			<xslt
			basedir="{$api_specsdir}"
			destdir="{$builddir}/specdocs/{$api}"
			style="{$xins_home}/src/xslt/testforms/function_to_html.xslt"
			includes="{$functionIncludes}"
			extension="-testform.html"
			reloadstylesheet="${{reload.stylesheet}}">
				<xmlcatalog refid="all-dtds" />
				<param name="xins_version" expression="{$xins_version}" />
				<param name="project_home" expression="{$project_home}" />
				<param name="project_file" expression="{$project_file}" />
				<param name="specsdir"     expression="{$api_specsdir}" />
				<param name="api"          expression="{$api}"          />
				<param name="api_file"     expression="{$api_file}"     />
				<param name="env_file"     expression="{$env_file}"     />
			</xslt>
			<xsl:for-each select="impl">
				<xsl:variable name="implName" select="@name" />
				<xsl:variable name="implName2">
					<xsl:if test="@name and string-length($implName) &gt; 0">
						<xsl:value-of select="concat('-', $implName)" />
					</xsl:if>
				</xsl:variable>
				<xsl:variable name="impl_dir" select="concat($project_home, '/apis/', $api, '/impl', $implName2)" />
				<xsl:variable name="impl_file" select="concat($impl_dir, '/impl.xml')" />
				<xsl:variable name="impl_node" select="document($impl_file)/impl" />
				<xsl:if test="$impl_node/runtime-properties">
					<xmlvalidate file="{$impl_file}" warn="false">
						<xmlcatalog refid="all-dtds" />
					</xmlvalidate>
					<xslt
					in="{$impl_file}"
					out="{$builddir}/specdocs/{$api}/properties{$implName2}.html"
					style="{$xins_home}/src/xslt/specdocs/impl_to_html.xslt">
						<xmlcatalog refid="all-dtds" />
						<param name="project_home" expression="{$project_home}" />
						<param name="project_file" expression="{$project_file}" />
						<param name="specsdir"     expression="{$api_specsdir}"     />
						<param name="xins_version" expression="{$xins_version}" />
						<param name="api"          expression="{$api}"          />
					</xslt>
				</xsl:if>
				<xsl:if test="$impl_node/logdoc">
					<xsl:variable name="javaDestFileDir" select="concat($builddir, '/java-fundament/', $api, $implName2, '/', $packageAsDir)" />
					<echo message="Generating the logdoc for {$api}{$implName2}" />
					<mkdir dir="{$builddir}/logdoc/{$api}{$implName2}" />
					<xmlvalidate file="{$impl_dir}/log.xml" warn="false">
						<xmlcatalog refid="all-dtds" />
					</xmlvalidate>
					<xslt
					in="{$impl_dir}/log.xml"
					out="{$builddir}/logdoc/{$api}{$implName2}/build.xml"
					style="{$xins_home}/src/xslt/logdoc/log_to_build.xslt">
						<xmlcatalog refid="all-dtds" />
						<param name="xins_home"       expression="{$xins_home}" />
						<param name="logdoc_xslt_dir" expression="{$xins_home}/src/xslt/logdoc" />
						<param name="sourcedir"       expression="{$impl_dir}" />
						<param name="html_destdir"    expression="{$builddir}/specdocs/{$api}/logdoc{$implName2}" />
						<param name="java_destdir"    expression="{$javaDestFileDir}" />
						<param name="package_name"    expression="{$package}" />
					</xslt>
					<copy file="{$xins_home}/src/css/logdoc/style.css" todir="{$builddir}/specdocs/{$api}/logdoc{$implName2}" />
					<ant dir="{$builddir}/logdoc/{$api}{$implName2}" target="html" inheritall="false" />
				</xsl:if>
			</xsl:for-each>
		</target>

		<xsl:if test="$apiHasTypes">
			<target name="-classes-types-{$api}" depends="-prepare-classes">
				<xsl:variable name="typePackage">
					<xsl:call-template name="package_for_type_classes">
						<xsl:with-param name="project_node" select="$project_node" />
						<xsl:with-param name="api" select="$api" />
					</xsl:call-template>
				</xsl:variable>
				<xsl:variable name="typePackageAsDir" select="translate($typePackage, '.','/')" />
				<xsl:variable name="javaDestDir"    select="concat($builddir, '/java-types/', $api)" />
				<xsl:variable name="copiedTypesDir" select="concat($builddir, '/types/',      $api)" />

				<xsl:if test="string-length($typeIncludes) &gt; 0">
					<copy todir="{$copiedTypesDir}">
						<fileset dir="{$api_specsdir}" includes="{$typeIncludes}" />
						<mapper classname="org.xins.common.ant.HungarianMapper" classpath="{$xins_home}/build/xins-common.jar" />
					</copy>
				</xsl:if>
				<xsl:for-each select="$api_node/type">
					<xsl:if test="contains(@name, '/')">
						<xsl:variable name="shared_type_dir"
						select="concat($api_specsdir, '/../../', substring-before(@name, '/'), '/spec')" />
						<xsl:variable name="shared_type_filename"
						select="concat(substring-after(@name, '/'), '.typ')" />
						<copy todir="{$copiedTypesDir}">
							<fileset dir="{$shared_type_dir}" includes="{$shared_type_filename}" />
							<mapper classname="org.xins.common.ant.HungarianMapper" classpath="{$xins_home}/build/xins-common.jar" />
						</copy>
					</xsl:if>
				</xsl:for-each>

				<xmlvalidate file="{$api_file}" warn="false">
					<xmlcatalog refid="all-dtds" />
				</xmlvalidate>
				<xslt
				in="{$api_file}"
				out="{$javaDestDir}/{$typePackageAsDir}/package.html"
				style="{$xins_home}/src/xslt/java-types/api_to_packagehtml.xslt">
					<xmlcatalog refid="all-dtds" />
					<param name="api" expression="{$api}" />
				</xslt>
				<xmlvalidate warn="false">
					<fileset dir="{$copiedTypesDir}" includes="*.typ"/>
					<xmlcatalog refid="all-dtds" />
				</xmlvalidate>
				<xslt
				basedir="{$copiedTypesDir}"
				destdir="{$javaDestDir}/{$typePackageAsDir}/"
				style="{$xins_home}/src/xslt/java-types/type_to_java.xslt"
				extension=".java"
				reloadstylesheet="${{reload.stylesheet}}">
					<xmlcatalog refid="all-dtds" />
					<param name="project_file" expression="{$project_file}" />
					<param name="specsdir"     expression="{$api_specsdir}" />
					<param name="package"      expression="{$typePackage}"  />
					<param name="api"          expression="{$api}"          />
				</xslt>

				<mkdir dir="{$typeClassesDir}" />
				<javac
				srcdir="{$javaDestDir}"
				destdir="{$typeClassesDir}"
				debug="true"
				deprecation="${{build.deprecation}}"
				source="${{build.java.version}}"
				target="${{build.java.version}}">
					<classpath>
						<pathelement path="{$xins_home}/build/logdoc.jar" />
						<pathelement path="{$xins_home}/build/xins-common.jar" />
					</classpath>
				</javac>
			</target>
		</xsl:if>

		<target name="wsdl-{$api}" description="Generates the WSDL specification of the '{$api}' API">
			<property name="subtarget" value="-wsdl" />
			<antcall target="opendoc-{$api}" />
		</target>

		<target name="opendoc-{$api}" depends="-load-properties-{$api}" description="Generates the specification document for the '{$api}' API">
			<property name="subtarget" value="-opendoc" />
			<path id="all.dependset">
				<filelist dir="{$api_specsdir}" files="${{functionIncludes}}" />
				<xsl:if test="string-length($typeIncludesAll) &gt; 0">
					<filelist dir="{$api_specsdir}" files="${{typeIncludesAll}}" />
				</xsl:if>
				<xsl:if test="string-length($resultcodeIncludesAll) &gt; 0">
					<filelist dir="{$api_specsdir}" files="${{resultcodeIncludesAll}}" />
				</xsl:if>
			</path>
			<antcall target="${{subtarget}}" inheritRefs="true" />
		</target>

		<xsl:for-each select="impl">
			<xsl:variable name="implName" select="@name" />
			<xsl:variable name="implName2">
				<xsl:if test="@name and string-length($implName) &gt; 0">
					<xsl:value-of select="concat('-', $implName)" />
				</xsl:if>
			</xsl:variable>
			<xsl:variable name="javaImplDir" select="concat($project_home, '/apis/', $api, '/impl', $implName2)" />
			<xsl:variable name="javaDestDir"     select="concat($builddir, '/java-fundament/', $api, $implName2)" />
			<xsl:variable name="classesDestDir"  select="concat($builddir, '/classes-api/',    $api, $implName2)" />
			<xsl:variable name="javaDestFileDir" select="concat($javaDestDir, '/', $packageAsDir)" />
			<xsl:variable name="impl_dir"     select="concat($project_home, '/apis/', $api, '/impl', $implName2)" />
			<xsl:variable name="impl_file"    select="concat($impl_dir, '/impl.xml')" />
			<xsl:variable name="impl_node"    select="document($impl_file)/impl" />

			<target name="-impl-{$api}{$implName2}-existencechecks">
				<xsl:for-each select="$api_node/function">
					<xsl:variable name="function"        select="@name" />
					<xsl:variable name="classname"       select="concat(@name, 'Impl')" />
					<xsl:variable name="javaImplFile"    select="concat($javaImplDir, '/', $packageAsDir, '/', $classname, '.java')" />
					<available
						property="exists-{$api}{$implName2}-{$classname}"
						file="{$javaImplFile}"
						type="file" />
				</xsl:for-each>
			</target>

			<xsl:for-each select="$api_node/function">
				<xsl:variable name="function"        select="@name" />
				<xsl:variable name="classname"       select="concat(@name, 'Impl')" />
				<xsl:variable name="javaImplFile"    select="concat($javaImplDir, '/', $packageAsDir, '/', $classname, '.java')" />
				<target
					name="-skeleton-impl-{$api}{$implName2}-{$function}"
					depends="-impl-{$api}{$implName2}-existencechecks, -prepare-classes"
					unless="exists-{$api}{$implName2}-{$classname}">
					<xmlvalidate file="{$api_specsdir}/{$function}.fnc" warn="false">
						<xmlcatalog refid="all-dtds" />
					</xmlvalidate>
					<xslt
					in="{$api_specsdir}/{$function}.fnc"
					out="{$javaImplFile}"
					style="{$xins_home}/src/xslt/java-server-framework/function_to_impl_java.xslt">
						<xmlcatalog refid="all-dtds" />
						<param name="xins_version" expression="{$xins_version}" />
						<param name="project_home" expression="{$project_home}" />
						<param name="project_file" expression="{$project_file}" />
						<param name="specsdir"     expression="{$api_specsdir}" />
						<param name="api"          expression="{$api}"          />
						<param name="api_file"     expression="{$api_file}"     />
						<param name="package"      expression="{$package}"      />
					</xslt>
				</target>
			</xsl:for-each>

			<target name="-skeletons-impl-{$api}{$implName2}">
				<xsl:attribute name="depends">
					<xsl:for-each select="$api_node/function">
						<xsl:variable name="function" select="@name" />
						<xsl:if test="position() &gt; 1">,</xsl:if>
						<xsl:text>-skeleton-impl-</xsl:text>
						<xsl:value-of select="$api" />
						<xsl:value-of select="$implName2" />
						<xsl:text>-</xsl:text>
						<xsl:value-of select="$function" />
					</xsl:for-each>
				</xsl:attribute>
			</target>

			<target name="classes-api-{$api}{$implName2}" description="Compiles the Java classes for the '{$api}{$implName2}' API implementation">
				<xsl:attribute name="depends">
					<xsl:text>-prepare-classes,</xsl:text>
					<xsl:if test="$apiHasTypes">
						<xsl:text>-classes-types-</xsl:text>
						<xsl:value-of select="$api" />
						<xsl:text>,</xsl:text>
					</xsl:if>
					<xsl:text>-skeletons-impl-</xsl:text>
					<xsl:value-of select="$api" />
					<xsl:value-of select="$implName2" />
				</xsl:attribute>
				<mkdir dir="{$javaDestDir}/{$packageAsDir}" />
				<dependset>
					<srcfilelist dir="{$api_specsdir}/../impl{$implName2}" files="impl.xml" />
					<srcfileset dir="{$api_specsdir}">
						<include name="{$functionIncludes} {$typeIncludes} {$resultcodeIncludes}" />
					</srcfileset>
					<targetfileset dir="{$javaDestDir}/{$packageAsDir}" includes="*.java" />
					<xsl:if test="$api_node/resultcode">
						<targetfileset dir="{$javaDestDir}" includes="resultcodes.xml" />
					</xsl:if>
				</dependset>
				<xmlvalidate file="{$impl_file}" warn="false">
					<xmlcatalog refid="all-dtds" />
				</xmlvalidate>
				<xmlvalidate file="{$api_file}" warn="false">
					<xmlcatalog refid="all-dtds" />
				</xmlvalidate>
				<xslt
				in="{$api_file}"
				out="{$javaDestDir}/{$packageAsDir}/APIImpl.java"
				style="{$xins_home}/src/xslt/java-server-framework/api_to_java.xslt">
					<xmlcatalog refid="all-dtds" />
					<param name="xins_version" expression="{$xins_version}" />
					<param name="project_home" expression="{$project_home}" />
					<param name="project_file" expression="{$project_file}" />
					<param name="specsdir"     expression="{$api_specsdir}" />
					<param name="api"          expression="{$api}"          />
					<param name="impl_file"    expression="{$impl_file}"    />
					<param name="package"      expression="{$package}"      />
				</xslt>
				<xslt
				in="{$api_file}"
				out="{$javaDestDir}/{$packageAsDir}/package.html"
				style="{$xins_home}/src/xslt/java-server-framework/api_to_packagehtml.xslt">
					<xmlcatalog refid="all-dtds" />
					<param name="api" expression="{$api}" />
				</xslt>
				<xslt
				in="{$impl_file}"
				out="{$javaDestDir}/{$packageAsDir}/RuntimeProperties.java"
				style="{$xins_home}/src/xslt/java-server-framework/impl_to_java.xslt">
					<xmlcatalog refid="all-dtds" />
					<param name="project_file" expression="{$project_file}" />
					<param name="specsdir"     expression="{$api_specsdir}" />
					<param name="package"      expression="{$package}"      />
					<param name="api"          expression="{$api}"          />
				</xslt>

				<xmlvalidate warn="false">
					<fileset dir="{$api_specsdir}" includes="{$functionIncludes}"/>
					<xmlcatalog refid="all-dtds" />
				</xmlvalidate>
				<xslt
				basedir="{$api_specsdir}"
				destdir="{$javaDestDir}/{$packageAsDir}"
				style="{$xins_home}/src/xslt/java-server-framework/function_to_java.xslt"
				extension=".java"
				includes="{$functionIncludes}"
				reloadstylesheet="${{reload.stylesheet}}">
					<xmlcatalog refid="all-dtds" />
					<param name="xins_version" expression="{$xins_version}"     />
					<param name="project_home" expression="{$project_home}"     />
					<param name="project_file" expression="{$project_file}"     />
					<param name="specsdir"     expression="{$api_specsdir}"     />
					<param name="package"      expression="{$package}"          />
					<param name="api"          expression="{$api}"              />
					<param name="api_file"     expression="{$api_file}"         />
					<param name="impl_file"    expression="{$impl_file}"        />
					<param name="generics"     expression="${{build.generics}}" />
				</xslt>

				<!-- Generation of the result code files. -->
				<!-- If have added a resultcode-ref in your function the java file should be regenerated. -->
				<xsl:if test="$api_node/resultcode">
					<!-- An intermediate file containing all the functions/result codes is created for performance reasons. -->
					<xslt
					in="{$api_file}"
					out="{$javaDestDir}/resultcodes.xml"
					style="{$xins_home}/src/xslt/java-server-framework/api_to_resultcodes.xslt">
						<xmlcatalog refid="all-dtds" />
						<param name="specsdir"     expression="{$api_specsdir}" />
					</xslt>
				</xsl:if>
				<xsl:if test="string-length($resultcodeIncludes) &gt; 0">
					<xmlvalidate warn="false">
						<fileset dir="{$api_specsdir}" includes="{$resultcodeIncludes}"/>
						<xmlcatalog refid="all-dtds" />
					</xmlvalidate>
					<xslt
					basedir="{$api_specsdir}"
					destdir="{$javaDestDir}/{$packageAsDir}"
					style="{$xins_home}/src/xslt/java-server-framework/resultcode_to_java.xslt"
					extension="Result.java"
					includes="{$resultcodeIncludes}"
					reloadstylesheet="${{reload.stylesheet}}">
						<xmlcatalog refid="all-dtds" />
						<param name="xins_version" expression="{$xins_version}" />
						<param name="project_home" expression="{$project_home}" />
						<param name="project_file" expression="{$project_file}" />
						<param name="specsdir"     expression="{$api_specsdir}" />
						<param name="package"      expression="{$package}"      />
						<param name="api"          expression="{$api}{$implName2}" />
						<param name="api_file"     expression="{$api_file}"     />
					</xslt>
				</xsl:if>
				<xsl:for-each select="$api_node/resultcode">
					<xsl:if test="contains(@name, '/')">
						<xsl:variable name="in_resultcode_file"
						select="concat($project_home, '/apis/', substring-before(@name, '/'), '/spec/', substring-after(@name, '/'), '.rcd')" />
						<xsl:variable name="out_java_file"
						select="concat($javaDestDir, '/', $packageAsDir, '/', substring-after(@name, '/'), 'Result.java')" />
						<xslt
						in="{$in_resultcode_file}"
						out="{$out_java_file}"
						style="{$xins_home}/src/xslt/java-server-framework/resultcode_to_java.xslt">
							<xmlcatalog refid="all-dtds" />
							<param name="xins_version" expression="{$xins_version}" />
							<param name="project_home" expression="{$project_home}" />
							<param name="project_file" expression="{$project_file}" />
							<param name="specsdir"     expression="{$api_specsdir}" />
							<param name="package"      expression="{$package}"      />
							<param name="api"          expression="{$api}{$implName2}" />
							<param name="api_file"     expression="{$api_file}"     />
						</xslt>
					</xsl:if>
				</xsl:for-each>

				<!-- Generate the logdoc java file is needed -->
				<xmlvalidate file="{$impl_file}" warn="false">
					<xmlcatalog refid="all-dtds" />
				</xmlvalidate>
				<xsl:if test="$impl_node/logdoc">
					<echo message="Generating the logdoc for {$api}{$implName2}" />
					<mkdir dir="{$builddir}/logdoc/{$api}{$implName2}" />
					<xmlvalidate file="{$impl_dir}/log.xml" warn="false">
						<xmlcatalog refid="all-dtds" />
					</xmlvalidate>
					<xsl:variable name="accesslevel" select="$impl_node/logdoc/@accesslevel" />
					<xslt
					in="{$impl_dir}/log.xml"
					out="{$builddir}/logdoc/{$api}{$implName2}/build.xml"
					style="{$xins_home}/src/xslt/logdoc/log_to_build.xslt">
						<xmlcatalog refid="all-dtds" />
						<param name="xins_home"       expression="{$xins_home}" />
						<param name="logdoc_xslt_dir" expression="{$xins_home}/src/xslt/logdoc" />
						<param name="sourcedir"       expression="{$impl_dir}" />
						<param name="html_destdir"    expression="{$builddir}/specdocs/{$api}/logdoc{$implName2}" />
						<param name="java_destdir"    expression="{$javaDestFileDir}" />
						<param name="package_name"    expression="{$package}" />
					</xslt>
					<ant antfile="{$builddir}/logdoc/{$api}{$implName2}/build.xml" target="java">
						<property name="accesslevel" value="{$accesslevel}" />
					</ant>
				</xsl:if>

				<!-- Compile all classes -->
				<mkdir dir="{$classesDestDir}" />
				<javac
				destdir="{$classesDestDir}"
				debug="true"
				deprecation="${{build.deprecation}}"
				source="${{build.java.version}}"
				target="${{build.java.version}}">
					<src path="{$javaDestDir}" />
					<src path="{$javaImplDir}" />
					<classpath>
						<xsl:if test="$apiHasTypes">
							<pathelement path="{$typeClassesDir}" />
						</xsl:if>
						<path refid="xins.classpath" />
						<xsl:apply-templates select="$impl_node/dependency" />
					</classpath>
				</javac>

				<!-- Try to load the API specific .version.properties -->
				<property prefix="api." file="{$api_specsdir}/../.version.properties" />
				<condition property="api.version" value="${{api.version.major}}.${{api.version.minor}}">
					<isset property="api.version.major" />
				</condition>
			</target>

			<target name="war-{$api}{$implName2}" depends="classes-api-{$api}{$implName2}, -load-version, wsdl-{$api}" description="Creates the WAR for the '{$api}{$implName2}' API" unless="no-war-{$api}">
				<mkdir dir="{$builddir}/webapps/{$api}{$implName2}" />
				<taskdef name="hostname" classname="org.xins.common.ant.HostnameTask" classpath="{$xins_home}/build/xins-common.jar" />
				<tstamp>
					<format property="timestamp" pattern="yyyy.MM.dd HH:mm:ss.SS" />
				</tstamp>
				<hostname />
				<delete file="{$builddir}/webapps/{$api}{$implName2}/web.xml" />
				<xmlvalidate file="{$api_file}" warn="false">
					<xmlcatalog refid="all-dtds" />
				</xmlvalidate>
				<xslt
				in="{$api_file}"
				out="{$builddir}/webapps/{$api}{$implName2}/web.xml"
				style="{$xins_home}/src/xslt/webapp/api_to_webxml.xslt">
					<xmlcatalog refid="all-dtds" />
					<param name="xins_version" expression="{$xins_version}"  />
					<param name="project_home" expression="{$project_home}"  />
					<param name="project_file" expression="{$project_file}"  />
					<param name="api"          expression="{$api}"           />
					<param name="api_file"     expression="{$api_file}"      />
					<param name="api_version"  expression="${{api.version}}" />
					<param name="java_version" expression="${{build.java.version}}" />
					<param name="hostname"     expression="${{hostname}}"    />
					<param name="timestamp"    expression="${{timestamp}}"   />
				</xslt>
				<fixcrlf srcdir="{$builddir}/webapps/{$api}{$implName2}" includes="web.xml" eol="unix" />
				<manifest file="{$builddir}/webapps/{$api}{$implName2}/MANIFEST.MF">
					<attribute name="Main-Class" value="org.xins.common.servlet.container.HTTPServletStarter" />
					<attribute name="XINS-Version" value="{$xins_version}" />
					<attribute name="API-Version" value="${{api.version}}" />
				</manifest>
				<unjar dest="{$builddir}/webapps/{$api}{$implName2}"
					src="{$xins_home}/build/xins-common.jar">
					<patternset>
						<include name="org/xins/common/servlet/container/HTTPServletStarter*.class" />
						<include name="org/xins/common/servlet/container/ServletClassLoader*.class" />
					</patternset>
				</unjar>
				<unjar dest="{$builddir}/webapps/{$api}{$implName2}"
					src="{$xins_home}/lib/servlet.jar">
				</unjar>
				<property name="classes.api.dir" value="{$classesDestDir}" />
				<war
					webxml="{$builddir}/webapps/{$api}{$implName2}/web.xml"
					destfile="{$builddir}/webapps/{$api}{$implName2}/{$api}{$implName2}.war"
					manifest="{$builddir}/webapps/{$api}{$implName2}/MANIFEST.MF"
					duplicate="fail">
					<lib dir="{$xins_home}/build" includes="logdoc.jar" />
					<lib dir="{$xins_home}/build" includes="xins-common.jar" />
					<lib dir="{$xins_home}/build" includes="xins-server.jar" />
					<lib dir="{$xins_home}/build" includes="xins-client.jar" />
					<lib dir="{$xins_home}/lib"   includes="commons-codec.jar commons-httpclient.jar commons-logging.jar jakarta-oro.jar log4j.jar xmlenc.jar json.jar" />
					<xsl:apply-templates select="$impl_node/dependency" mode="lib" />
					<xsl:apply-templates select="$impl_node/content" />
					<classes dir="${{classes.api.dir}}" includes="**/*.class" />
					<xsl:if test="$apiHasTypes">
						<classes dir="{$typeClassesDir}" includes="**/*.class" />
					</xsl:if>
					<classes dir="{$javaImplDir}" excludes="**/*.java,**/*.class,impl.xml" />
					<zipfileset dir="{$builddir}/webapps/{$api}{$implName2}" includes="org/xins/common/servlet/container/*.class" /> 
					<zipfileset dir="{$builddir}/webapps/{$api}{$implName2}" includes="javax/servlet/**/*" /> 
					<zipfileset dir="{$builddir}/wsdl" includes="{$api}.wsdl" prefix="WEB-INF" />
					<zipfileset dir="{$api_specsdir}" includes="api.xml {$functionIncludes} {$typeIncludes} {$resultcodeIncludes} {$categoryIncludes}" prefix="WEB-INF/specs" />
					<xsl:for-each select="$api_node/type">
						<xsl:if test="contains(@name, '/')">
							<xsl:variable name="type_dir"
							select="concat($project_home, '/apis/', substring-before(@name, '/'), '/spec')" />
							<xsl:variable name="type_filename"
							select="concat(substring-after(@name, '/'), '.typ')" />
							<zipfileset dir="{$type_dir}" includes="{$type_filename}" prefix="WEB-INF/specs" />
						</xsl:if>
					</xsl:for-each>
					<xsl:for-each select="$api_node/resultcode">
						<xsl:if test="contains(@name, '/')">
							<xsl:variable name="resultcode_dir"
							select="concat($project_home, '/apis/', substring-before(@name, '/'), '/spec')" />
							<xsl:variable name="resultcode_filename"
							select="concat(substring-after(@name, '/'), '.rcd')" />
							<zipfileset dir="{$resultcode_dir}" includes="{$resultcode_filename}" prefix="WEB-INF/specs" />
						</xsl:if>
					</xsl:for-each>
				</war>
				<checksum file="{$builddir}/webapps/{$api}{$implName2}/{$api}{$implName2}.war" property="war.md5"/>
				<echo message="MD5: ${{war.md5}}" />
				<echo message="Build time: ${{timestamp}}" />
			</target>

			<target name="run-{$api}{$implName2}" depends="war-{$api}{$implName2}" description="Runs the '{$api}{$implName2}' API">
				<!-- XXX probably done by war- -->
				<property name="api" value="{$api}" />
				<property name="implName2" value="{$implName2}" />
				<path id="run.classpath">
					<path location="{$builddir}/classes-api/{$api}{$implName2}" />
					<xsl:if test="$apiHasTypes">
						<path location="{$builddir}/classes-types/{$api}" />
					</xsl:if>
				</path>
				<antcall target="-run" inheritRefs="true" />
			</target>

			<target name="javadoc-api-{$api}{$implName2}" depends="-load-properties, classes-api-{$api}{$implName2}" description="Generates Javadoc API docs for the '{$api}{$implName2}' API">
				<!-- XXX probably done by classes- -->
				<property name="api" value="{$api}" />
				<path id="javadoc.api.packageset">
					<dirset dir="{$javaDestDir}" />
					<dirset dir="{$javaImplDir}" />
					<xsl:if test="$apiHasTypes">
						<dirset dir="{$builddir}/java-types/{$api}" />
					</xsl:if>
				</path>
				<path id="javadoc.api.classpath">
					<path refid="xins.classpath" />
					<xsl:apply-templates select="$impl_node/dependency" />
				</path>
				<antcall target="-javadoc-api" inheritRefs="true" />
			</target>

			<target name="create-impl-{$api}{$implName2}" unless="impl.exists">
				<mkdir dir="{$api_specsdir}/../impl{$implName2}" />
				<echo file="{$api_specsdir}/../impl{$implName2}/impl.xml"><![CDATA[<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE impl PUBLIC "-//XINS//DTD Implementation 2.0//EN" "http://www.xins.org/dtd/impl_2_0.dtd">
<!-- The order of the elements is logdoc, bootstrap-properties, runtime-properties, content, dependency, calling-convention, instance. -->
<impl>
</impl>]]></echo>
			</target>

			<target name="stub-{$api}{$implName2}" depends="-prepare-classes" description="Generates an Stub API using the defined examples">
				<xsl:variable name="javaImplDir"    select="concat($javaImplDir, '/', $packageAsDir)" />
				<xmlvalidate warn="false">
					<xmlcatalog refid="all-dtds" />
					<fileset dir="{$api_specsdir}" includes="{$functionIncludes}" />
				</xmlvalidate>
				<available file="{$api_specsdir}/../impl{$implName2}/impl.xml" property="impl.exists" />
				<antcall target="create-impl-{$api}{$implName2}" />
				<input message="Are you sure you want to generate the stub files in the {$javaImplDir} directory? Previous files will be orverwritten."
				addproperty="stub.overwrite" defaultvalue="y" validargs="y,n" />
				<condition property="stub.notoverwrite">
					<equals arg1="${{stub.overwrite}}" arg2="n" />
				</condition>
				<fail message="Stopped the generation of the stub files in order not to overwrite the current files." if="stub.notoverwrite" />
				<xslt basedir="{$api_specsdir}"
				includes="{$functionIncludes}"
				destdir="{$javaImplDir}"
				extension="Impl.java"
				style="{$xins_home}/src/xslt/java-server-framework/function_to_stub.xslt"
				reloadstylesheet="${{reload.stylesheet}}">
					<xmlcatalog refid="all-dtds" />
					<param name="xins_version" expression="{$xins_version}" />
					<param name="project_home" expression="{$project_home}" />
					<param name="project_file" expression="{$project_file}" />
					<param name="specsdir"     expression="{$api_specsdir}" />
					<param name="api"          expression="{$api}"          />
					<param name="api_file"     expression="{$api_file}"     />
					<param name="package"      expression="{$package}"      />
				</xslt>
			</target>

			<target name="server-{$api}{$implName2}"
							depends="specdocs-{$api}, javadoc-api-{$api}{$implName2}, war-{$api}{$implName2}"
							description="Generates the war file, the Javadoc API docs for the server side and the specdocs for the '{$api}{$implName2}' API.">
			</target>
		</xsl:for-each>


		<xsl:if test="test">
			<xsl:variable name="packageTests">
				<xsl:call-template name="package_for_tests">
					<xsl:with-param name="project_node" select="$project_node" />
					<xsl:with-param name="api" select="$api" />
				</xsl:call-template>
			</xsl:variable>
			<xsl:variable name="packageTestsAsDir" select="translate($packageTests, '.','/')" />

			<target name="test-{$api}" description="Generates (if needed) and run the tests for the {$api} API.">
				<xsl:attribute name="depends">
					<xsl:text>-prepare-classes,</xsl:text>
					<xsl:if test="$apiHasTypes">
						<xsl:text>-classes-types-</xsl:text>
						<xsl:value-of select="$api" />
						<xsl:text>,</xsl:text>
					</xsl:if>
					<xsl:text>jar-</xsl:text>
					<xsl:value-of select="$api" />
				</xsl:attribute>

				<available property="test.generated" file="apis/{$api}/test" type="dir" />
				<antcall target="generatetests-{$api}" />
				<property name="test.environment" value="" />
				<property name="test.start.server" value="false" />
				<property name="org.xins.server.config" value="" />
				<property name="servlet.port" value="8080" />
				<property name="classes.api.dir" value="{$builddir}/classes-api/{$api}" />
				<mkdir dir="{$builddir}/classes-tests/{$api}" />
				<javac
				destdir="{$builddir}/classes-tests/{$api}"
				debug="true"
				deprecation="${{build.deprecation}}"
				source="${{build.java.version}}"
				target="${{build.java.version}}">
					<src path="apis/{$api}/test" />
					<classpath>
						<path refid="xins.classpath" />
						<pathelement path="{$builddir}/capis/{$api}-capi.jar" />
						<pathelement path="${{classes.api.dir}}" />
						<xsl:if test="$apiHasTypes">
							<pathelement path="{$builddir}/classes-types/{$api}" />
						</xsl:if>
						<xsl:if test="impl">
							<xsl:variable name="impl_file" select="concat($project_home, '/apis/', $api, '/impl/impl.xml')" />
							<xsl:apply-templates select="document($impl_file)/impl/dependency" />
						</xsl:if>
						<fileset dir="{$project_home}/apis/{$api}/test" includes="**/*.jar" />
					</classpath>
				</javac>
				<condition property="no-war-{$api}">
					<isfalse value="${{test.start.server}}" />
				</condition>
				<antcall target="war-{$api}" />
				<mkdir dir="{$builddir}/testresults/xml" />
				<junit fork="true" showoutput="true" dir="{$project_home}" printsummary="true" failureproperty="tests.failed">
					<sysproperty key="user.dir" value="{$project_home}" />
					<sysproperty key="test.environment" value="${{test.environment}}" />
					<sysproperty key="test.start.server" value="${{test.start.server}}" />
					<sysproperty key="org.xins.server.config" value="${{org.xins.server.config}}" />
					<sysproperty key="servlet.port" value="${{servlet.port}}" />
					<!--sysproperty key="net.sourceforge.cobertura.datafile"	file="{$builddir}/coverage/{$api}/cobertura.ser" /-->
					<sysproperty key="emma.coverage.out.file"	file="{$builddir}/emma/{$api}/coverage.emma" />
          <formatter usefile="false" type="brief"/>
					<formatter type="xml" />
					<test name="{$packageTests}.APITests" todir="{$builddir}/testresults/xml" outfile="testresults-{$api}"/>
					<classpath>
						<path refid="xins.classpath" />
						<pathelement path="{$builddir}/capis/{$api}-capi.jar" />
						<pathelement path="{$builddir}/classes-tests/{$api}" />
						<pathelement path="${{classes.api.dir}}" />
						<xsl:if test="$apiHasTypes">
							<pathelement path="{$builddir}/classes-types/{$api}" />
						</xsl:if>
						<xsl:if test="impl">
							<xsl:variable name="impl_file" select="concat($project_home, '/apis/', $api, '/impl/impl.xml')" />
							<xsl:apply-templates select="document($impl_file)/impl/dependency" />
						</xsl:if>
						<fileset dir="{$project_home}/apis/{$api}/test" includes="**/*.jar" />
					</classpath>
				</junit>
				<mkdir dir="{$builddir}/testresults/html" />
				<xslt
				in="{$builddir}/testresults/xml/testresults-{$api}.xml"
				out="{$builddir}/testresults/html/testresults-{$api}.html"
				style="{$xins_home}/src/xslt/tests/index.xslt" />
				<copy
				file="{$xins_home}/src/css/tests/stylesheet.css"
				todir="{$builddir}/testresults/html" />
			</target>

			<target name="generatetests-{$api}" depends="-prepare-classes" unless="test.generated">
				<xsl:variable name="javaTestDir">
					<xsl:value-of select="concat('apis/', $api, '/test/', $packageTestsAsDir)" />
				</xsl:variable>

				<xmlvalidate warn="false">
					<xmlcatalog refid="all-dtds" />
					<fileset dir="{$api_specsdir}" includes="api.xml" />
				</xmlvalidate>
				<xslt
				in="{$api_file}"
				out="{$javaTestDir}/APITests.java"
				style="{$xins_home}/src/xslt/tests/api_to_test.xslt">
					<xmlcatalog refid="all-dtds" />
					<param name="package"      expression="{$packageTests}"      />
				</xslt>
				<xmlvalidate warn="false">
					<xmlcatalog refid="all-dtds" />
					<fileset dir="{$api_specsdir}" includes="{$functionIncludes}" />
				</xmlvalidate>
				<xslt basedir="{$api_specsdir}"
				includes="{$functionIncludes}"
				destdir="{$javaTestDir}"
				extension="Tests.java"
				style="{$xins_home}/src/xslt/tests/function_to_test.xslt"
				reloadstylesheet="${{reload.stylesheet}}">
					<xmlcatalog refid="all-dtds" />
					<param name="api"          expression="{$api}"          />
					<param name="package"      expression="{$packageTests}" />
				</xslt>
			</target>

			<target name="javadoc-test-{$api}" description="Generates the Javadoc of the unit tests of the {$api} API.">
				<property name="api" value="{$api}" />
				<path id="javadoc.test.classpath">
					<pathelement path="{$builddir}/classes-tests/{$api}" />
					<xsl:if test="$apiHasTypes">
						<pathelement path="{$builddir}/classes-types/{$api}" />
					</xsl:if>
					<xsl:if test="impl">
						<xsl:variable name="impl_file" select="concat($project_home, '/apis/', $api, '/impl/impl.xml')" />
						<xsl:apply-templates select="document($impl_file)/impl/dependency" />
					</xsl:if>
				</path>
				<antcall target="-javadoc-test" inheritRefs="true" />
			</target>
		</xsl:if>

		<target name="-stubs-capi-{$api}" depends="-prepare-classes" >
			<mkdir dir="{$builddir}/java-capi/{$api}/{$clientPackageAsDir}" />
			<dependset>
				<srcfilelist dir="{$api_specsdir}" files="{$functionIncludes}" />
				<xsl:if test="string-length($typeIncludesAll) &gt; 0">
					<srcfilelist dir="{$api_specsdir}" files="{$typeIncludesAll}" />
				</xsl:if>
				<xsl:if test="string-length($resultcodeIncludesAll) &gt; 0">
					<srcfilelist dir="{$api_specsdir}" files="{$resultcodeIncludesAll}" />
				</xsl:if>
				<targetfileset dir="{$builddir}/java-capi/{$api}/{$clientPackageAsDir}" includes="CAPI.java" />
			</dependset>
			<xmlvalidate file="{$api_file}" warn="false">
				<xmlcatalog refid="all-dtds" />
			</xmlvalidate>
			<xslt
			in="{$api_file}"
			out="{$builddir}/java-capi/{$api}/{$clientPackageAsDir}/CAPI.java"
			style="{$xins_home}/src/xslt/java-capi/api_to_java.xslt">
				<xmlcatalog refid="all-dtds" />
				<param name="project_file" expression="{$project_file}"  />
				<param name="project_home" expression="{$project_home}" />
				<param name="specsdir"     expression="{$api_specsdir}"  />
				<param name="package"      expression="{$clientPackage}" />
				<param name="api"          expression="{$api}"           />
				<param name="xins_version" expression="{$xins_version}"  />
			</xslt>
			<xslt
			in="{$api_file}"
			out="{$builddir}/java-capi/{$api}/{$clientPackageAsDir}/package.html"
			style="{$xins_home}/src/xslt/java-capi/api_to_packagehtml.xslt">
				<xmlcatalog refid="all-dtds" />
				<param name="api" expression="{$api}" />
			</xslt>
			<xsl:if test="string-length($functionIncludes) &gt; 0">
				<xmlvalidate warn="false">
					<fileset dir="{$api_specsdir}" includes="{$functionIncludes}"/>
					<xmlcatalog refid="all-dtds" />
				</xmlvalidate>
				<xslt
				basedir="{$api_specsdir}"
				destdir="{$builddir}/java-capi/{$api}/{$clientPackageAsDir}"
				style="{$xins_home}/src/xslt/java-capi/function_to_result_java.xslt"
				extension="Result.java"
				includes="{$functionIncludes}"
				reloadstylesheet="${{reload.stylesheet}}">
					<xmlcatalog refid="all-dtds" />
					<param name="xins_version" expression="{$xins_version}"     />
					<param name="project_home" expression="{$project_home}"     />
					<param name="project_file" expression="{$project_file}"     />
					<param name="specsdir"     expression="{$api_specsdir}"     />
					<param name="api"          expression="{$api}"              />
					<param name="api_file"     expression="{$api_file}"         />
					<param name="package"      expression="{$clientPackage}"    />
					<param name="generics"     expression="${{build.generics}}" />
				</xslt>
				<xslt
				basedir="{$api_specsdir}"
				destdir="{$builddir}/java-capi/{$api}/{$clientPackageAsDir}"
				style="{$xins_home}/src/xslt/java-capi/function_to_request_java.xslt"
				extension="Request.java"
				includes="{$functionIncludes}"
				reloadstylesheet="${{reload.stylesheet}}">
					<xmlcatalog refid="all-dtds" />
					<param name="project_file" expression="{$project_file}"  />
					<param name="specsdir"     expression="{$api_specsdir}"  />
					<param name="package"      expression="{$clientPackage}" />
					<param name="api"          expression="{$api}"           />
				</xslt>
			</xsl:if>
			<xsl:if test="string-length($resultcodeIncludes) &gt; 0">
				<xmlvalidate warn="false">
					<fileset dir="{$api_specsdir}" includes="{$resultcodeIncludes}"/>
					<xmlcatalog refid="all-dtds" />
				</xmlvalidate>
				<xslt
				basedir="{$api_specsdir}"
				destdir="{$builddir}/java-capi/{$api}/{$clientPackageAsDir}"
				style="{$xins_home}/src/xslt/java-capi/resultcode_to_java.xslt"
				extension="Exception.java"
				includes="{$resultcodeIncludes}"
				reloadstylesheet="${{reload.stylesheet}}">
					<xmlcatalog refid="all-dtds" />
					<param name="specsdir"     expression="{$api_specsdir}"  />
					<param name="package"      expression="{$clientPackage}" />
					<param name="api"          expression="{$api}"           />
					<param name="api_file"     expression="{$api_file}"      />
				</xslt>
			</xsl:if>
			<xsl:for-each select="$api_node/resultcode">
				<xsl:if test="contains(@name, '/')">
					<xsl:variable name="in_resultcode_file"
					select="concat($project_home, '/apis/', substring-before(@name, '/'), '/spec/', substring-after(@name, '/'), '.rcd')" />
					<xsl:variable name="out_java_file"
					select="concat($builddir, '/java-capi/', $api, '/', $clientPackageAsDir, '/', substring-after(@name, '/'), 'Exception.java')" />
					<xslt
					in="{$in_resultcode_file}"
					out="{$out_java_file}"
					style="{$xins_home}/src/xslt/java-capi/resultcode_to_java.xslt">
						<xmlcatalog refid="all-dtds" />
						<param name="specsdir"     expression="{$api_specsdir}"  />
						<param name="package"      expression="{$clientPackage}" />
						<param name="api"          expression="{$api}"           />
						<param name="api_file"     expression="{$api_file}"      />
					</xslt>
				</xsl:if>
			</xsl:for-each>

			<!-- Try to load the API specific .version.properties -->
			<property prefix="api." file="{$api_specsdir}/../.version.properties" />
			<condition property="api.version" value="${{api.version.major}}.${{api.version.minor}}">
				<isset property="api.version.major" />
			</condition>
		</target>

		<target name="jar-{$api}" description="Generates and compiles the Java classes for the client-side '{$api}' API stubs">
			<xsl:attribute name="depends">
				<xsl:text>-prepare-classes,</xsl:text>
				<xsl:if test="$apiHasTypes">
					<xsl:text>-classes-types-</xsl:text>
					<xsl:value-of select="$api" />
					<xsl:text>,</xsl:text>
				</xsl:if>
				<xsl:text>-stubs-capi-</xsl:text>
				<xsl:value-of select="$api" />
				<xsl:text>,-load-version</xsl:text>
			</xsl:attribute>
			<mkdir dir="{$builddir}/classes-capi/{$api}" />
			<javac
			srcdir="{$builddir}/java-capi/{$api}/"
			destdir="{$builddir}/classes-capi/{$api}"
			debug="true"
			deprecation="${{build.deprecation}}"
			source="${{build.java.version}}"
			target="${{build.java.version}}">
				<classpath>
					<path refid="xins.classpath" />
					<xsl:if test="$apiHasTypes">
						<pathelement path="{$typeClassesDir}"  />
					</xsl:if>
				</classpath>
			</javac>
			<xsl:if test="$apiHasTypes">
				<copy todir="{$builddir}/classes-capi/{$api}">
					<fileset dir="{$typeClassesDir}" includes="**/*.class" />
				</copy>
			</xsl:if>
			<mkdir dir="{$builddir}/capis/" />
			<manifest file="{$builddir}/capis/{$api}-MANIFEST.MF">
				<attribute name="XINS-Version" value="{$xins_version}" />
				<attribute name="API-Version" value="${{api.version}}" />
			</manifest>
			<jar
			destfile="{$builddir}/capis/{$api}-capi.jar"
			manifest="{$builddir}/capis/{$api}-MANIFEST.MF">
				<fileset dir="{$builddir}/classes-capi/{$api}" includes="**/*.class" />
				<zipfileset dir="{$api_specsdir}" includes="api.xml {$functionIncludes} {$typeIncludes} {$resultcodeIncludes}" prefix="specs" />
				<xsl:for-each select="type">
					<xsl:if test="contains(@name, '/')">
						<xsl:variable name="type_dir"
						select="concat($project_home, '/apis/', substring-before(@name, '/'), '/spec')" />
						<xsl:variable name="type_filename"
						select="concat(substring-after(@name, '/'), '.typ')" />
						<zipfileset dir="{$type_dir}" includes="{$type_filename}" prefix="specs" />
					</xsl:if>
				</xsl:for-each>
				<xsl:for-each select="resultcode">
					<xsl:if test="contains(@name, '/')">
						<xsl:variable name="resultcode_dir"
						select="concat($project_home, '/apis/', substring-before(@name, '/'), '/spec')" />
						<xsl:variable name="resultcode_filename"
						select="concat(substring-after(@name, '/'), '.rcd')" />
						<zipfileset dir="{$resultcode_dir}" includes="{$resultcode_filename}" prefix="specs" />
					</xsl:if>
				</xsl:for-each>
			</jar>
		</target>
		<target name="capi-{$api}" depends="jar-{$api}" />

		<target name="javadoc-capi-{$api}" description="Generates Javadoc API docs for the client-side '{$api}' API stubs">
			<xsl:attribute name="depends">
				<xsl:if test="$apiHasTypes">
					<xsl:value-of select="concat('-classes-types-', $api, ',')" />
				</xsl:if>
				<xsl:value-of select="concat('-stubs-capi-', $api)" />
			</xsl:attribute>
			<property name="api" value="{$api}" />
			<path id="javadoc.capi.packages">
				<dirset dir="{$builddir}/java-capi/{$api}" />
				<xsl:if test="$apiHasTypes">
					<dirset dir="{$builddir}/java-types/{$api}" />
				</xsl:if>
			</path>
			<antcall target="-javadoc-capi" inheritRefs="true" />
		</target>

		<target name="client-{$api}"
						depends="jar-{$api}, javadoc-capi-{$api}, specdocs-{$api}, wsdl-{$api}, opendoc-{$api}"
						description="Generates the Javadoc API docs for the client side and the client JAR file for the '{$api}' API stubs and zip the result.">
			<property name="api" value="{$api}" />
			<antcall target="-client" />
		</target>

		<target name="all-{$api}"
						description="Generates everything for the '{$api}' API stubs.">
			<xsl:attribute name="depends">
				<xsl:text>client-</xsl:text>
				<xsl:value-of select="$api" />
				<xsl:if test="impl">
					<xsl:text>, server-</xsl:text>
					<xsl:value-of select="$api" />
				</xsl:if>
			</xsl:attribute>
		</target>

		<target name="clean-{$api}" description="Deletes everything for the '{$api}' API stubs.">
			<property name="api" value="{$api}" />
			<antcall target="-clean" />
			<xsl:for-each select="impl/@name">
				<xsl:variable name="impl" select="." />
				<antcall target="-clean-impl">
					<param name="impl" value="{$impl}" />
				</antcall>
			</xsl:for-each>
		</target>

		<target name="rebuild-{$api}" depends="clean-{$api}, all-{$api}"
						description="Regenerates everything for the '{$api}' API stubs." />
	</xsl:template>

	<xsl:template match="content">
		<zipfileset dir="{$dependenciesDir}/{@dir}">
			<xsl:attribute name="includes">
				<xsl:choose>
					<xsl:when test="@includes">
						<xsl:value-of select="@includes" />
					</xsl:when>
					<xsl:otherwise>**/*</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:if test="@web-path">
				<xsl:attribute name="prefix">
					<xsl:value-of select="@web-path" />
				</xsl:attribute>
			</xsl:if>
		</zipfileset>
	</xsl:template>

	<xsl:template match="dependency">
		<fileset dir="{$dependenciesDir}/{@dir}">
			<xsl:attribute name="includes">
				<xsl:choose>
					<xsl:when test="@includes">
						<xsl:value-of select="@includes" />
					</xsl:when>
					<xsl:otherwise>**/*.jar</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
		</fileset>
	</xsl:template>

	<xsl:template match="dependency" mode="lib">
		<lib dir="{$dependenciesDir}/{@dir}">
			<xsl:attribute name="includes">
				<xsl:choose>
					<xsl:when test="@includes">
						<xsl:value-of select="@includes" />
					</xsl:when>
					<xsl:otherwise>**/*.jar</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
		</lib>
	</xsl:template>
</xsl:stylesheet>
