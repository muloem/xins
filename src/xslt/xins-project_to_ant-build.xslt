<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 XSLT that generates the build.xml used to compile the different APIs.

 $Id$

 Copyright 2003-2005 Wanadoo Nederland B.V.
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
	<xsl:include href="create_project.xslt"  />

	<xsl:output indent="yes" />

	<xsl:variable name="xmlenc_version"    select="'0.47'"                                          />
	<xsl:variable name="xins_buildfile"    select="concat($xins_home,    '/build.xml')"             />
	<xsl:variable name="project_file"      select="concat($project_home, '/xins-project.xml')"      />
	<xsl:variable name="logdoc.jar"        select="concat($xins_home,    '/build/logdoc.jar')"      />
	<xsl:variable name="xins-common.jar"   select="concat($xins_home,    '/build/xins-common.jar')" />
	<xsl:variable name="xins-server.jar"   select="concat($xins_home,    '/build/xins-server.jar')" />
	<xsl:variable name="xins-client.jar"   select="concat($xins_home,    '/build/xins-client.jar')" />
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
		<project default="help" basedir="..">
			<xsl:attribute name="name">
				<xsl:value-of select="//project/@name" />
			</xsl:attribute>

			<target name="clean" description="Removes all generated files">
				<delete dir="{$builddir}" />
			</target>

			<target name="version" description="Prints current versions of Java, Ant and XINS">
				<ant
				antfile="build.xml"
				dir="{$xins_home}"
				target="version"
				inheritall="false" />
				<echo>
					<xsl:text>
This build file was generated with XINS </xsl:text>
					<xsl:value-of select="$xins_version" />
					<xsl:text>.</xsl:text>
				</echo>
			</target>

			<target name="help" depends="version" description="Shows the supported commands.">
				<echo><![CDATA[Generic targets:
- version             Prints the version of XINS.
- help                Prints this message.
- all                 Generates everything.
- clean               Removes all generated files.
- specdocs            Generates all specification docs.
- wars                Generates all WAR files.
- capis               Generates all CAPI JAR files.
- javadoc-capis       Generates all CAPI Javadoc.

The following commands assist in authoring specifications:
- create-api          Generates a new api specification file.
- create-function     Generates a new function specification file.
- create-rcd          Generates a new result code specification file.
- create-type         Generates a new type specification file.

The following targets are specific for a single API, replace <api> with the name of an existing API:
- run-<api>           Runs the WAR file for the API.
- war-<api>           Creates the WAR file for the API.
- specdocs-<api>      Generates all specification docs for the API.
- javadoc-api-<api>   Generates Javadoc for the API implementation (server).
- server-<api>        Generates the WAR file, the API Javadoc for the server side and the specdocs for the API.
- jar-<api>           Generates and compiles the CAPI classes.
- javadoc-capi-<api>  Generates the Javadoc for the CAPI classes (client).
- client-<api>        Generates the CAPI JAR file and the corresponding Javadoc.
- clean-<api>         Cleans everything for the API.
- rebuild-<api>       Regenerates everything for the API.
- all-<api>           Generates everything for the API.

APIs in this project are:
]]></echo>
				<echo><xsl:for-each select="api">
						<xsl:text>"</xsl:text>
							<xsl:value-of select="@name" />
						<xsl:text>" </xsl:text>
					</xsl:for-each>
				</echo>
			</target>

			<target name="ask" description="Asks for the command and API to execute.">
				<input addproperty="command"
				       message="Command "
				       validargs="war,specdocs,javadoc-api,jar,javadoc-capi,all,clean,client,server" />
				<input addproperty="api"
				       message="API ">
					<xsl:attribute name="validargs">
						<xsl:for-each select="api">
							<xsl:if test="position() &gt; 1">,</xsl:if>
							<xsl:value-of select="@name" />
						</xsl:for-each>
					</xsl:attribute>
				</input>
				<antcall target="${{command}}-${{api}}" />
			</target>

			<xsl:call-template name="createproject" />

			<target name="-prepare" />

			<target name="-prepare-specdocs" depends="-prepare, -load-dtds">
				<mkdir dir="{$builddir}/specdocs" />
			</target>

			<target name="-load-dtds">
				<xmlcatalog id="all-dtds">
					<classpath>
						<pathelement path="{$xins_home}/src/dtd"/>
					</classpath>
					<dtd publicId="-//XINS//DTD XINS Project 1.0 alpha//EN"
					     location="xins-project_1_0_alpha.dtd" />
					<dtd publicId="-//XINS//DTD XINS API 1.0 alpha//EN"
					     location="api_1_0_alpha.dtd" />
					<dtd publicId="-//XINS//DTD Function 1.0 alpha//EN"
					     location="function_1_0_alpha.dtd" />
					<dtd publicId="-//XINS//DTD Type 1.0 alpha//EN"
					     location="type_1_0_alpha.dtd" />
					<dtd publicId="-//XINS//DTD Result Code 1.0 alpha//EN"
					     location="resultcode_1_0_alpha.dtd" />
					<dtd publicId="-//XINS//DTD XINS Logdoc 1.0 alpha//EN"
					     location="log_1_0_alpha.dtd" />
					<dtd publicId="-//XINS//DTD XINS Translation Bundle 1.0 alpha//EN"
					     location="translation-bundle_1_0_alpha.dtd" />

					<dtd publicId="-//XINS//DTD XINS Project 1.0//EN"
					     location="xins-project_1_0.dtd" />
					<dtd publicId="-//XINS//DTD XINS API 1.0//EN"
					     location="api_1_0.dtd" />
					<dtd publicId="-//XINS//DTD Function 1.0//EN"
					     location="function_1_0.dtd" />
					<dtd publicId="-//XINS//DTD Type 1.0//EN"
					     location="type_1_0.dtd" />
					<dtd publicId="-//XINS//DTD Result Code 1.0//EN"
					     location="resultcode_1_0.dtd" />
					<dtd publicId="-//XINS//DTD Implementation 1.0//EN"
					     location="impl_1_0.dtd" />
					<dtd publicId="-//XINS//DTD Environments 1.0//EN"
					     location="environments_1_0.dtd" />
					<dtd publicId="-//XINS//DTD XINS Logdoc 1.0//EN"
					     location="log_1_0.dtd" />
					<dtd publicId="-//XINS//DTD XINS Translation Bundle 1.0//EN"
					     location="translation-bundle_1_0.dtd" />

					<dtd publicId="-//XINS//DTD XINS Project 1.1//EN"
					     location="xins-project_1_1.dtd" />
					<dtd publicId="-//XINS//DTD XINS API 1.1//EN"
					     location="api_1_1.dtd" />
					<dtd publicId="-//XINS//DTD Function 1.1//EN"
					     location="function_1_1.dtd" />
					<dtd publicId="-//XINS//DTD Type 1.1//EN"
					     location="type_1_1.dtd" />
					<dtd publicId="-//XINS//DTD Result Code 1.1//EN"
					     location="resultcode_1_1.dtd" />
					<dtd publicId="-//XINS//DTD Implementation 1.1//EN"
					     location="impl_1_1.dtd" />
					<dtd publicId="-//XINS//DTD Environments 1.1//EN"
					     location="environments_1_1.dtd" />
					<dtd publicId="-//XINS//DTD XINS Logdoc 1.1//EN"
					     location="log_1_1.dtd" />
					<dtd publicId="-//XINS//DTD XINS Translation Bundle 1.1//EN"
					     location="translation-bundle_1_1.dtd" />

					<dtd publicId="-//XINS//DTD XINS Project 1.2//EN"
					     location="xins-project_1_2.dtd" />
					<dtd publicId="-//XINS//DTD XINS API 1.2//EN"
					     location="api_1_2.dtd" />
					<dtd publicId="-//XINS//DTD Function 1.2//EN"
					     location="function_1_2.dtd" />
					<dtd publicId="-//XINS//DTD Type 1.2//EN"
					     location="type_1_2.dtd" />
					<dtd publicId="-//XINS//DTD Result Code 1.2//EN"
					     location="resultcode_1_2.dtd" />
					<dtd publicId="-//XINS//DTD Implementation 1.2//EN"
					     location="impl_1_2.dtd" />
					<dtd publicId="-//XINS//DTD Environments 1.2//EN"
					     location="environments_1_2.dtd" />
					<dtd publicId="-//XINS//DTD XINS Logdoc 1.2//EN"
					     location="log_1_2.dtd" />
					<dtd publicId="-//XINS//DTD XINS Translation Bundle 1.2//EN"
					     location="translation-bundle_1_2.dtd" />
				</xmlcatalog>
			</target>

			<target name="index-specdocs" depends="-prepare-specdocs" description="Generates the API index">
				<xmlvalidate file="{$project_file}" warn="false">
					<xmlcatalog refid="all-dtds" />
				</xmlvalidate>
				<style
				in="{$project_file}"
				out="{$builddir}/specdocs/index.html"
				style="{$xins_home}/src/xslt/specdocs/xins-project_to_index.xslt">
					<xmlcatalog refid="all-dtds" />
					<param name="xins_version" expression="{$xins_version}" />
					<param name="project_home" expression="{$project_home}" />
					<param name="project_file" expression="{$project_file}" />
					<param name="specsdir"     expression="{$specsdir}"     />
				</style>
				<copy todir="{$builddir}/specdocs" file="{$xins_home}/src/css/specdocs/style.css" />
			</target>

			<target name="-load-version">
				<property prefix="api." file="{$project_home}/.version.properties" />
				<condition property="api.version" value="${{api.version.major}}.${{api.version.minor}}">
					<isset property="api.version.major" />
				</condition>
				<property name="api.version" value="Not specified" />
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

			<target name="-prepare-classes" depends="-prepare,-load-dtds">
				<property file="{$project_home}/build.properties" />
				<!-- If not set by the user set it to true. -->
				<property name="build.deprecation" value="true" />
			</target>

			<target name="classes" description="Compiles all Java classes">
				<xsl:attribute name="depends">
					<xsl:for-each select="//project/api/impl">
						<xsl:if test="position() &gt; 1">,</xsl:if>
						<xsl:text>classes-api-</xsl:text>
						<xsl:value-of select="../@name" />
					</xsl:for-each>
					<xsl:for-each select="api">
						<!-- If old API -->
						<xsl:variable name="api"      select="@name" />
						<xsl:if test="not(impl) and not(document(concat($project_home, '/apis/', $api, '/spec/api.xml')))">
							<xsl:if test="document(concat($specsdir, '/', @name, '/api.xml'))/api/impl-java">
								<xsl:if test="position() &gt; 1 or count(//project/api/impl) &gt; 0">,</xsl:if>
								<xsl:text>classes-api-</xsl:text>
								<xsl:value-of select="@name" />
							</xsl:if>
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

			<target name="wars" description="Creates the WARs for all APIs">
				<xsl:attribute name="depends">
					<xsl:for-each select="//project/api/impl">
						<xsl:if test="position() &gt; 1">,</xsl:if>
						<xsl:text>war-</xsl:text>
						<xsl:value-of select="../@name" />
					</xsl:for-each>
					<xsl:for-each select="api">
						<!-- If old API -->
						<xsl:variable name="api"      select="@name" />
						<xsl:if test="not(impl) and not(document(concat($project_home, '/apis/', $api, '/spec/api.xml')))">
							<xsl:if test="document(concat($specsdir, '/', @name, '/api.xml'))/api/impl-java">
								<xsl:if test="position() &gt; 1 or count(//project/api/impl) &gt; 0">,</xsl:if>
								<xsl:text>war-</xsl:text>
								<xsl:value-of select="@name" />
							</xsl:if>
						</xsl:if>
					</xsl:for-each>
				</xsl:attribute>
			</target>

			<target name="all" depends="specdocs,wars" description="Generates everything" />

			<xsl:apply-templates select="api" />
		</project>
	</xsl:template>

	<xsl:template match="api">
		<xsl:variable name="api"      select="@name" />
		<xsl:variable name="new_api_file" select="concat($project_home, '/apis/', $api, '/spec/api.xml')" />
		<xsl:variable name="api_specsdir">
			<xsl:choose>
				<xsl:when test="impl or environments or document($new_api_file)">
					<xsl:value-of select="concat($project_home, '/apis/', $api, '/spec')" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat($specsdir, '/', $api)" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="api_file" select="concat($api_specsdir, '/api.xml')" />
		<xsl:variable name="typeClassesDir"    select="concat($project_home, '/build/classes-types/', $api)" />
		<xsl:variable name="functionIncludes">
			<xsl:for-each select="document($api_file)/api/function">
				<xsl:if test="position() &gt; 1">,</xsl:if>
				<xsl:value-of select="@name" />
				<xsl:text>.fnc</xsl:text>
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="typeIncludes">
			<xsl:for-each select="document($api_file)/api/type">
				<xsl:if test="position() &gt; 1">,</xsl:if>
				<xsl:value-of select="@name" />
				<xsl:text>.typ</xsl:text>
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="resultcodeIncludes">
			<xsl:for-each select="document($api_file)/api/resultcode">
				<xsl:if test="position() &gt; 1">,</xsl:if>
				<xsl:value-of select="@name" />
				<xsl:text>.rcd</xsl:text>
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="clientPackage">
			<xsl:call-template name="package_for_client_api">
				<xsl:with-param name="project_file" select="$project_file" />
				<xsl:with-param name="api" select="$api" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="clientPackageAsDir" select="translate($clientPackage, '.','/')" />
		<xsl:variable name="apiHasTypes" select="boolean(document($api_file)/api/type)" />

		<target name="specdocs-{$api}" depends="index-specdocs" description="Generates all specification docs for the '{$api}' API">
			<dependset>
				<srcfilelist   dir="{$api_specsdir}"    files="{$functionIncludes}" />
				<srcfilelist   dir="{$api_specsdir}"    files="*.typ"         />
				<targetfileset dir="{$project_home}/build/specdocs/{$api}" includes="index.html" />
			</dependset>
			<dependset>
				<srcfilelist   dir="{$api_specsdir}"    files="api.xml" />
				<targetfileset dir="{$project_home}/build/specdocs/{$api}" includes="*.html" />
			</dependset>
			<xsl:if test="environments">
				<xsl:variable name="env_dir" select="concat($project_home, '/apis/', $api)" />
				<dependset>
					<srcfilelist   dir="{$env_dir}"    files="environments.xml" />
					<targetfileset dir="{$project_home}/build/specdocs/{$api}" includes="*.html" />
				</dependset>
			</xsl:if>
			<copy todir="{$builddir}/specdocs/{$api}" file="{$xins_home}/src/css/specdocs/style.css" />
			<xmlvalidate file="{$api_file}" warn="false">
				<xmlcatalog refid="all-dtds" />
			</xmlvalidate>
			<style
			in="{$api_file}"
			out="{$project_home}/build/specdocs/{$api}/index.html"
			style="{$xins_home}/src/xslt/specdocs/api_to_html.xslt">
				<xmlcatalog refid="all-dtds" />
				<param name="xins_version" expression="{$xins_version}" />
				<param name="project_home" expression="{$project_home}" />
				<param name="project_file" expression="{$project_file}" />
				<param name="specsdir"     expression="{$api_specsdir}" />
				<param name="api"          expression="{$api}"          />
				<param name="api_file"     expression="{$api_file}"     />
			</style>
			<xmlvalidate warn="false">
				<fileset dir="{$api_specsdir}" includes="{$functionIncludes}"/>
				<xmlcatalog refid="all-dtds" />
			</xmlvalidate>
			<style
			basedir="{$api_specsdir}"
			destdir="{$project_home}/build/specdocs/{$api}"
			style="{$xins_home}/src/xslt/specdocs/function_to_html.xslt"
			includes="{$functionIncludes}">
				<xmlcatalog refid="all-dtds" />
				<param name="xins_version" expression="{$xins_version}" />
				<param name="project_home" expression="{$project_home}" />
				<param name="project_file" expression="{$project_file}" />
				<param name="specsdir"     expression="{$api_specsdir}" />
				<param name="api"          expression="{$api}"          />
				<param name="api_file"     expression="{$api_file}"     />
			</style>
			<xsl:if test="string-length($typeIncludes) &gt; 0">
				<xmlvalidate warn="false">
					<fileset dir="{$api_specsdir}" includes="{$typeIncludes}"/>
					<xmlcatalog refid="all-dtds" />
				</xmlvalidate>
				<style
				basedir="{$api_specsdir}"
				destdir="{$project_home}/build/specdocs/{$api}"
				style="{$xins_home}/src/xslt/specdocs/type_to_html.xslt"
				includes="{$typeIncludes}">
					<xmlcatalog refid="all-dtds" />
					<param name="xins_version" expression="{$xins_version}" />
					<param name="project_home" expression="{$project_home}" />
					<param name="project_file" expression="{$project_file}" />
					<param name="specsdir"     expression="{$api_specsdir}" />
					<param name="api"          expression="{$api}"          />
					<param name="api_file"     expression="{$api_file}"     />
				</style>
			</xsl:if>
			<xsl:if test="string-length($resultcodeIncludes) &gt; 0">
				<xmlvalidate warn="false">
					<fileset dir="{$api_specsdir}" includes="{$resultcodeIncludes}"/>
					<xmlcatalog refid="all-dtds" />
				</xmlvalidate>
				<style
				basedir="{$api_specsdir}"
				destdir="{$project_home}/build/specdocs/{$api}"
				style="{$xins_home}/src/xslt/specdocs/resultcode_to_html.xslt"
				includes="{$resultcodeIncludes}">
					<xmlcatalog refid="all-dtds" />
					<param name="xins_version" expression="{$xins_version}" />
					<param name="project_home" expression="{$project_home}" />
					<param name="project_file" expression="{$project_file}" />
					<param name="specsdir"     expression="{$api_specsdir}" />
					<param name="api"          expression="{$api}"          />
					<param name="api_file"     expression="{$api_file}"     />
				</style>
			</xsl:if>
			<xsl:for-each select="document($api_file)/api/environment">
				<style
				basedir="{$api_specsdir}"
				destdir="{$project_home}/build/specdocs/{$api}"
				style="{$xins_home}/src/xslt/testforms/function_to_html.xslt"
				includes="{$functionIncludes}"
				extension="-testform-{@id}.html">
					<xmlcatalog refid="all-dtds" />
					<param name="xins_version" expression="{$xins_version}" />
					<param name="project_home" expression="{$project_home}" />
					<param name="project_file" expression="{$project_file}" />
					<param name="specsdir"     expression="{$api_specsdir}" />
					<param name="api"          expression="{$api}"          />
					<param name="api_file"     expression="{$api_file}"     />
					<param name="environment"  expression="{@id}"           />
					<param name="env_url"      expression="{@url}"          />
				</style>
			</xsl:for-each>
			<xsl:if test="environments">
				<xsl:variable name="env_file" select="concat($project_home, '/apis/', $api, '/environments.xml')" />
				<xmlvalidate file="{$env_file}" warn="false">
					<xmlcatalog refid="all-dtds" />
				</xmlvalidate>
				<xsl:for-each select="document($env_file)/environments/environment">
					<style
					basedir="{$api_specsdir}"
					destdir="{$project_home}/build/specdocs/{$api}"
					style="{$xins_home}/src/xslt/testforms/function_to_html.xslt"
					includes="{$functionIncludes}"
					extension="-testform-{@id}.html">
						<xmlcatalog refid="all-dtds" />
						<param name="xins_version" expression="{$xins_version}" />
						<param name="project_home" expression="{$project_home}" />
						<param name="project_file" expression="{$project_file}" />
						<param name="specsdir"     expression="{$api_specsdir}" />
						<param name="api"          expression="{$api}"          />
						<param name="api_file"     expression="{$api_file}"     />
						<param name="environment"  expression="{@id}"           />
						<param name="env_url"      expression="{@url}"          />
					</style>
				</xsl:for-each>
			</xsl:if>
			<xsl:if test="impl">
				<xsl:variable name="impl_file" select="concat($project_home, '/apis/', $api, '/impl/impl.xml')" />
				<xsl:if test="document($impl_file)/impl/runtime-properties">
					<xmlvalidate file="{$impl_file}" warn="false">
						<xmlcatalog refid="all-dtds" />
					</xmlvalidate>
					<style
					in="{$impl_file}"
					out="{$project_home}/build/specdocs/{$api}/properties.html"
					style="{$xins_home}/src/xslt/specdocs/impl_to_html.xslt">
						<xmlcatalog refid="all-dtds" />
						<param name="xins_version" expression="{$xins_version}" />
						<param name="project_home" expression="{$project_home}" />
						<param name="project_file" expression="{$project_file}" />
						<param name="specsdir"     expression="{$api_specsdir}" />
						<param name="api"          expression="{$api}"          />
						<param name="api_file"     expression="{$api_file}"     />
					</style>
				</xsl:if>
			</xsl:if>
		</target>

		<xsl:if test="$apiHasTypes">
			<target name="-classes-types-{$api}" depends="-prepare-classes">
				<xsl:variable name="package">
					<xsl:call-template name="package_for_type_classes">
						<xsl:with-param name="project_file" select="$project_file" />
						<xsl:with-param name="api" select="$api" />
					</xsl:call-template>
				</xsl:variable>
				<xsl:variable name="packageAsDir" select="translate($package, '.','/')" />
				<xsl:variable name="javaDestDir"    select="concat($project_home, '/build/java-types/', $api)" />
				<xsl:variable name="copiedTypesDir" select="concat($project_home, '/build/types/',      $api)" />

				<copy todir="{$copiedTypesDir}">
					<fileset dir="{$api_specsdir}" includes="{$typeIncludes}" />
					<mapper classname="org.xins.common.ant.HungarianMapper" classpath="{$xins_home}/build/xins-common.jar" />
				</copy>

				<xmlvalidate file="{$api_file}" warn="false">
					<xmlcatalog refid="all-dtds" />
				</xmlvalidate>
				<style
				in="{$api_file}"
				out="{$javaDestDir}/{$packageAsDir}/package.html"
				style="{$xins_home}/src/xslt/java-types/api_to_packagehtml.xslt">
					<xmlcatalog refid="all-dtds" />
					<param name="xins_version" expression="{$xins_version}"  />
					<param name="project_home" expression="{$project_home}"  />
					<param name="project_file" expression="{$project_file}"  />
					<param name="specsdir"     expression="{$api_specsdir}"  />
					<param name="api"          expression="{$api}"           />
					<param name="api_file"     expression="{$api_file}"      />
					<param name="package"      expression="{$package}"       />
				</style>
				<xmlvalidate warn="false">
					<fileset dir="{$copiedTypesDir}" includes="**/*.typ"/>
					<xmlcatalog refid="all-dtds" />
				</xmlvalidate>
				<style
				basedir="{$copiedTypesDir}"
				destdir="{$javaDestDir}/{$packageAsDir}/"
				style="{$xins_home}/src/xslt/java-types/type_to_java.xslt"
				reloadstylesheet="true"
				extension=".java">
					<xmlcatalog refid="all-dtds" />
					<param name="xins_version" expression="{$xins_version}" />
					<param name="project_home" expression="{$project_home}" />
					<param name="project_file" expression="{$project_file}" />
					<param name="specsdir"     expression="{$api_specsdir}" />
					<param name="api"          expression="{$api}"          />
					<param name="api_file"     expression="{$api_file}"     />
					<param name="package"      expression="{$package}"      />
				</style>

				<mkdir dir="{$typeClassesDir}" />
				<javac
				srcdir="{$javaDestDir}"
				destdir="{$typeClassesDir}"
				debug="true"
				deprecation="${{build.deprecation}}"
				source="1.3"
				target="1.3">
					<classpath>
						<pathelement path="{$logdoc.jar}" />
						<pathelement path="{$xins-common.jar}" />
					</classpath>
				</javac>
			</target>
		</xsl:if>

		<xsl:if test="document($api_file)/api/impl-java or impl">
			<xsl:variable name="package">
				<xsl:call-template name="package_for_server_api">
					<xsl:with-param name="project_file" select="$project_file" />
					<xsl:with-param name="api" select="$api" />
				</xsl:call-template>
			</xsl:variable>
			<xsl:variable name="packageAsDir" select="translate($package, '.','/')" />
			<!-- This test is not garanted to work with all XSLT processors. -->
			<xsl:variable name="javaImplDir">
				<xsl:value-of select="$project_home" />
				<xsl:text>/</xsl:text>
				<xsl:choose>
					<xsl:when test="document($new_api_file)">
						<xsl:value-of select="concat('apis/', $api, '/impl')" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when test="//project/@javadir">
								<xsl:value-of select="//project/@javadir" />
							</xsl:when>
							<xsl:otherwise>src/impl-java</xsl:otherwise>
						</xsl:choose>
						<xsl:text>/</xsl:text>
						<xsl:value-of select="$api" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<!-- xsl:variable name="javaImplDir"     select="concat($javaImplBaseDir, '/',                      $api)" /-->
			<xsl:variable name="javaDestDir"     select="concat($project_home,    '/build/java-fundament/', $api)" />
			<xsl:variable name="classesDestDir"  select="concat($project_home,    '/build/classes-api/',    $api)" />
			<!-- This test is not garanted to work with all XSLT processors. -->
			<xsl:variable name="logdoc_dir">
				<xsl:choose>
					<xsl:when test="document($new_api_file)">
						<xsl:value-of select="$javaImplDir" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="concat($project_home, '/src/logdoc/', $api)" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:variable name="javaDestFileDir" select="concat($javaDestDir, '/', $packageAsDir)" />

			<target name="-impl-{$api}-existencechecks">
				<xsl:for-each select="document($api_file)/api/function">
					<xsl:variable name="function"        select="@name" />
					<xsl:variable name="classname"       select="concat(@name, 'Impl')" />
					<xsl:variable name="javaImplFile"    select="concat($javaImplDir, '/', $packageAsDir, '/', $classname, '.java')" />
					<available
						property="exists-{$api}-{$classname}"
						file="{$javaImplFile}"
						type="file" />
				</xsl:for-each>
			</target>

			<xsl:for-each select="document($api_file)/api/function">
				<xsl:variable name="function"        select="@name" />
				<xsl:variable name="classname"       select="concat(@name, 'Impl')" />
				<xsl:variable name="javaImplFile"    select="concat($javaImplDir, '/', $packageAsDir, '/', $classname, '.java')" />
				<target
					name="-impl-{$api}-{$function}-unavail"
					depends="-impl-{$api}-existencechecks"
					if="exists-{$api}-{$classname}">
					<echo message="Not overwriting existing file: {$javaImplFile}" />
				</target>
				<target
					name="-skeleton-impl-{$api}-{$function}"
					depends="-impl-{$api}-{$function}-unavail, -prepare-classes"
					unless="exists-{$api}-{$classname}">
					<xmlvalidate file="{$api_specsdir}/{$function}.fnc" warn="false">
						<xmlcatalog refid="all-dtds" />
					</xmlvalidate>
					<style
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
						<param name="classname"    expression="{$classname}"    />
					</style>
				</target>
			</xsl:for-each>

			<target name="-skeletons-impl-{$api}">
				<xsl:attribute name="depends">
					<xsl:for-each select="document($api_file)/api/function">
						<xsl:variable name="function" select="@name" />
						<xsl:if test="position() &gt; 1">,</xsl:if>
						<xsl:text>-skeleton-impl-</xsl:text>
						<xsl:value-of select="$api" />
						<xsl:text>-</xsl:text>
						<xsl:value-of select="$function" />
					</xsl:for-each>
				</xsl:attribute>
			</target>

			<target name="classes-api-{$api}" description="Compiles the Java classes for the '{$api}' API implementation">
				<xsl:attribute name="depends">
					<xsl:text>-prepare-classes,</xsl:text>
					<xsl:if test="$apiHasTypes">
						<xsl:text>-classes-types-</xsl:text>
						<xsl:value-of select="$api" />
						<xsl:text>,</xsl:text>
					</xsl:if>
					<xsl:text>-skeletons-impl-</xsl:text>
					<xsl:value-of select="$api" />
				</xsl:attribute>
				<mkdir dir="{$javaDestDir}" />
				<dependset>
					<xsl:choose>
						<xsl:when test="impl">
							<srcfilelist   dir="{$api_specsdir}/../impl" files="impl.xml" />
						</xsl:when>
						<xsl:otherwise>
							<srcfilelist   dir="{$api_specsdir}" files="api.xml" />
						</xsl:otherwise>
					</xsl:choose>
					<targetfileset dir="{$javaDestDir}/{$packageAsDir}" includes="*.java"/>
				</dependset>
				<xmlvalidate file="{$api_file}" warn="false">
					<xmlcatalog refid="all-dtds" />
				</xmlvalidate>
				<style
				in="{$api_file}"
				out="{$javaDestDir}/{$packageAsDir}/APIImpl.java"
				style="{$xins_home}/src/xslt/java-server-framework/api_to_java.xslt">
					<xmlcatalog refid="all-dtds" />
					<param name="xins_version" expression="{$xins_version}" />
					<param name="project_home" expression="{$project_home}" />
					<param name="project_file" expression="{$project_file}" />
					<param name="specsdir"     expression="{$api_specsdir}" />
					<param name="api"          expression="{$api}"          />
					<param name="api_file"     expression="{$api_file}"     />
					<param name="package"      expression="{$package}"      />
				</style>
				<xsl:variable name="impl_file">
					<xsl:choose>
						<xsl:when test="impl">
							<xsl:value-of select="concat($project_home, '/apis/', $api, '/impl/impl.xml')" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$api_file" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xmlvalidate file="{$impl_file}" warn="false">
					<xmlcatalog refid="all-dtds" />
				</xmlvalidate>
				<style
				in="{$impl_file}"
				out="{$javaDestDir}/{$packageAsDir}/RuntimeProperties.java"
				style="{$xins_home}/src/xslt/java-server-framework/impl_to_java.xslt">
					<xmlcatalog refid="all-dtds" />
					<param name="xins_version" expression="{$xins_version}" />
					<param name="project_home" expression="{$project_home}" />
					<param name="project_file" expression="{$project_file}" />
					<param name="specsdir"     expression="{$api_specsdir}" />
					<param name="api"          expression="{$api}"          />
					<param name="api_file"     expression="{$api_file}"     />
					<param name="package"      expression="{$package}"      />
				</style>

				<xmlvalidate warn="false">
					<fileset dir="{$api_specsdir}" includes="{$functionIncludes}"/>
					<xmlcatalog refid="all-dtds" />
				</xmlvalidate>
				<style
				basedir="{$api_specsdir}"
				destdir="{$javaDestDir}/{$packageAsDir}"
				style="{$xins_home}/src/xslt/java-server-framework/function_to_java.xslt"
				extension=".java"
				includes="{$functionIncludes}">
					<xmlcatalog refid="all-dtds" />
					<param name="xins_version" expression="{$xins_version}" />
					<param name="project_home" expression="{$project_home}" />
					<param name="project_file" expression="{$project_file}" />
					<param name="specsdir"     expression="{$api_specsdir}" />
					<param name="package"      expression="{$package}"      />
					<param name="api"          expression="{$api}"          />
					<param name="api_file"     expression="{$api_file}"     />
				</style>
				<!-- Generation of the result code files. -->
				<!-- If have added a resultcode-ref in your function the java file should be regenerated. -->
				<xsl:if test="string-length($resultcodeIncludes) &gt; 0">
					<dependset>
						<srcfilelist   dir="{$api_specsdir}" files="{$functionIncludes}" />
						<targetfileset dir="{$javaDestDir}/{$packageAsDir}" includes="*Result.java"/>
					</dependset>
					<xmlvalidate warn="false">
						<fileset dir="{$api_specsdir}" includes="{$resultcodeIncludes}"/>
						<xmlcatalog refid="all-dtds" />
					</xmlvalidate>
					<style
					basedir="{$api_specsdir}"
					destdir="{$javaDestDir}/{$packageAsDir}"
					style="{$xins_home}/src/xslt/java-server-framework/resultcode_to_java.xslt"
					extension="Result.java"
					includes="{$resultcodeIncludes}">
						<xmlcatalog refid="all-dtds" />
						<param name="xins_version" expression="{$xins_version}" />
						<param name="project_home" expression="{$project_home}" />
						<param name="project_file" expression="{$project_file}" />
						<param name="specsdir"     expression="{$api_specsdir}" />
						<param name="package"      expression="{$package}"      />
						<param name="api"          expression="{$api}"          />
						<param name="api_file"     expression="{$api_file}"     />
					</style>
				</xsl:if>

				<!-- Generate the logdoc java file is needed -->
				<xsl:if test="document($api_file)/api/impl-java/logdoc">
					<echo message="Generating the logdoc for {$api}" />
					<mkdir dir="build/logdoc/{$api}" />
					<style
					in="{$project_home}/src/logdoc/{$api}/log.xml"
					out="build/logdoc/{$api}/build.xml"
					style="{$xins_home}/src/xslt/logdoc/log_to_build.xslt">
						<xmlcatalog refid="all-dtds" />
						<param name="xins_home"       expression="{$xins_home}" />
						<param name="logdoc_xslt_dir" expression="{$xins_home}/src/xslt/logdoc" />
						<param name="logdoc_dtd_dir"  expression="{$xins_home}/src/dtd" />
						<param name="sourcedir"       expression="{$project_home}/src/logdoc/{$api}" />
						<param name="html_destdir"    expression="html" />
						<param name="java_destdir"    expression="{$javaDestFileDir}" />
						<param name="package_name"    expression="{$package}" />
					</style>
					<ant antfile="build/logdoc/{$api}/build.xml" target="java" />
				</xsl:if>
				<xsl:if test="impl">
					<xsl:variable name="impl_dir"     select="concat($project_home, '/apis/', $api, '/impl')" />
					<xsl:variable name="impl_file"    select="concat($impl_dir, '/impl.xml')" />
					<xmlvalidate file="{$impl_file}" warn="false">
						<xmlcatalog refid="all-dtds" />
					</xmlvalidate>
					<xsl:if test="document($impl_file)/impl/logdoc">
						<echo message="Generating the logdoc for {$api}" />
						<mkdir dir="build/logdoc/{$api}" />
						<xmlvalidate file="{$impl_dir}/log.xml" warn="false">
							<xmlcatalog refid="all-dtds" />
						</xmlvalidate>
						<xsl:variable name="accesslevel" select="document($impl_file)/impl/logdoc/@accesslevel" />
						<style
						in="{$impl_dir}/log.xml"
						out="build/logdoc/{$api}/build.xml"
						style="{$xins_home}/src/xslt/logdoc/log_to_build.xslt">
							<xmlcatalog refid="all-dtds" />
							<param name="xins_home"       expression="{$xins_home}" />
							<param name="logdoc_xslt_dir" expression="{$xins_home}/src/xslt/logdoc" />
							<param name="sourcedir"       expression="{$impl_dir}" />
							<param name="html_destdir"    expression="html" />
							<param name="java_destdir"    expression="{$javaDestFileDir}" />
							<param name="package_name"    expression="{$package}" />
						</style>
						<ant antfile="build/logdoc/{$api}/build.xml" target="java">
							<property name="accesslevel" value="{$accesslevel}" />
						</ant>
					</xsl:if>
				</xsl:if>

				<!-- Compile all classes -->
				<mkdir dir="{$classesDestDir}" />
				<javac
				destdir="{$classesDestDir}"
				debug="true"
				deprecation="${{build.deprecation}}"
				source="1.3"
				target="1.3">
					<src path="{$javaDestDir}" />
					<src path="{$javaImplDir}" />
					<classpath>
						<xsl:if test="$apiHasTypes">
							<pathelement path="{$typeClassesDir}" />
						</xsl:if>
						<pathelement path="{$logdoc.jar}" />
						<pathelement path="{$xins-common.jar}" />
						<pathelement path="{$xins-server.jar}" />
						<pathelement path="{$xins-client.jar}" />
						<fileset dir="{$xins_home}/lib" includes="**/*.jar" />
						<xsl:apply-templates select="document($api_file)/api/impl-java/dependency[not(@type) or @type='compile' or @type='compile_and_runtime']" />
						<xsl:if test="impl">
							<xsl:variable name="impl_file"    select="concat($project_home, '/apis/', $api, '/impl/impl.xml')" />
							<xsl:apply-templates select="document($impl_file)/impl/dependency[not(@type) or @type='compile' or @type='compile_and_runtime']" />
						</xsl:if>
					</classpath>
				</javac>
			</target>

			<target name="war-{$api}" depends="classes-api-{$api}, -load-version" description="Creates the WAR for the '{$api}' API">
				<mkdir dir="build/webapps/{$api}" />
				<taskdef name="hostname" classname="org.xins.common.ant.HostnameTask" classpath="{$xins_home}/build/xins-common.jar" />
				<tstamp>
					<format property="timestamp" pattern="yyyy.MM.dd HH:mm:ss.SS" />
				</tstamp>
				<hostname />
				<delete file="build/webapps/{$api}/web.xml" />
				<xmlvalidate file="{$api_file}" warn="false">
					<xmlcatalog refid="all-dtds" />
				</xmlvalidate>
				<style
				in="{$api_file}"
				out="build/webapps/{$api}/web.xml"
				style="{$xins_home}/src/xslt/webapp/api_to_webxml.xslt">
					<xmlcatalog refid="all-dtds" />
					<param name="xins_version" expression="{$xins_version}"  />
					<param name="project_home" expression="{$project_home}"  />
					<param name="project_file" expression="{$project_file}"  />
					<param name="api"          expression="{$api}"           />
					<param name="api_file"     expression="{$api_file}"      />
					<param name="api_version"  expression="${{api.version}}" />
					<param name="hostname"     expression="${{hostname}}"    />
					<param name="timestamp"    expression="${{timestamp}}"   />
				</style>
				<fixcrlf srcdir="build/webapps/{$api}" includes="web.xml" eol="unix" />
				<manifest file="build/webapps/{$api}/MANIFEST.MF">
					<attribute name="XINS-Version" value="{$xins_version}" />
					<attribute name="API-Version" value="${{api.version}}" />
				</manifest>
				<war
					webxml="build/webapps/{$api}/web.xml"
					destfile="build/webapps/{$api}/{$api}.war"
					manifest="build/webapps/{$api}/MANIFEST.MF">
					<lib dir="{$xins_home}/build" includes="logdoc.jar" />
					<lib dir="{$xins_home}/build" includes="xins-common.jar" />
					<lib dir="{$xins_home}/build" includes="xins-server.jar" />
					<lib dir="{$xins_home}/build" includes="xins-client.jar" />
					<lib dir="{$xins_home}/lib"   includes="commons-codec.jar commons-httpclient.jar commons-logging.jar jakarta-oro.jar log4j.jar xmlenc.jar" />
					<xsl:apply-templates select="document($api_file)/api/impl-java/dependency[not(@type) or @type='runtime' or @type='compile_and_runtime']" mode="lib" />
					<xsl:if test="impl">
						<xsl:variable name="impl_file"    select="concat($project_home, '/apis/', $api, '/impl/impl.xml')" />
						<xsl:apply-templates select="document($impl_file)/impl/dependency[not(@type) or @type='runtime' or @type='compile_and_runtime']" mode="lib" />
					</xsl:if>
					<classes dir="{$classesDestDir}" includes="**/*.class" />
					<xsl:if test="$apiHasTypes">
						<classes dir="{$typeClassesDir}" includes="**/*.class" />
					</xsl:if>
					<classes dir="{$javaImplDir}" excludes="**/*.java" />
				</war>
				<checksum file="build/webapps/{$api}/{$api}.war" property="war.md5"/>
				<echo message="MD5: ${{war.md5}}" />
				<echo message="Build time: ${{timestamp}}" />
			</target>

			<target name="run-{$api}" depends="war-{$api}" description="Runs the '{$api}' API">
				<fail message="Please, specify the org.xins.server.config property as explained in the user guide." unless="org.xins.server.config" />
				<property name="servlet.port" value="8080" />
				<java classname="org.xins.common.servlet.container.HTTPServletStarter"
							fork="true">
					<jvmarg value="-Dorg.xins.server.config=${{org.xins.server.config}}" />
					<arg path="build/webapps/{$api}/{$api}.war" />
					<arg value="${{servlet.port}}" />
					<classpath>
						<fileset dir="{$xins_home}/build" includes="logdoc.jar xins-common.jar xins-client.jar xins-server.jar" />
						<fileset dir="{$xins_home}/lib" includes="commons-codec.jar commons-httpclient.jar commons-logging.jar commons-net.jar jakarta-oro.jar log4j.jar servlet.jar xmlenc.jar" />
						<path location="build/classes-api/{$api}" />
						<xsl:if test="$apiHasTypes">
							<path location="build/classes-types/{$api}" />
						</xsl:if>
					</classpath>
				</java>
			</target>

			<target name="javadoc-api-{$api}" depends="classes-api-{$api}" description="Generates Javadoc API docs for the '{$api}' API">
				<property file="{$xins_home}/.version.properties" />
				<mkdir dir="build/javadoc-api/{$api}" />
				<javadoc
				destdir="build/javadoc-api/{$api}"
				version="yes"
				use="yes"
				author="yes"
				access="package"
				source="1.3"
				windowtitle="Implementation of {$api} API"
				doctitle="Implementation of {$api} API">
					<packageset dir="{$javaDestDir}" />
					<packageset dir="{$javaImplDir}" />
					<xsl:if test="$apiHasTypes">
						<packageset dir="build/java-types/{$api}" />
					</xsl:if>
					<link
					href="http://www.xins.org/javadoc/${{version.major}}.${{version.middle}}.${{version.minor}}${{version.build}}/"
					offline="true"
					packagelistloc="{$xins_home}/docs/javadoc/" />
					<link
					href="http://java.sun.com/j2se/1.4.2/docs/api"
					offline="true"
					packagelistloc="{$xins_home}/src/package-lists/j2se/" />
					<link
					href="http://jakarta.apache.org/log4j/docs/api/"
					offline="true"
					packagelistloc="{$xins_home}/src/package-lists/log4j/" />
					<link
					href="http://xmlenc.sourceforge.net/javadoc/{$xmlenc_version}/"
					offline="true"
					packagelistloc="{$xins_home}/src/package-lists/xmlenc/" />
					<link
					href="http://xins.sourceforge.net/ant-1.6.2-docs/"
					offline="true"
					packagelistloc="{$xins_home}/src/package-lists/ant/" />
					<classpath>
						<pathelement location="{$xins_home}/build/logdoc.jar"       />
						<pathelement location="{$xins_home}/build/xins-client.jar"   />
						<pathelement location="{$xins_home}/build/xins-common.jar"   />
						<pathelement location="{$xins_home}/build/xins-server.jar"   />
						<pathelement location="{$xins_home}/lib/log4j.jar"           />
						<pathelement location="{$xins_home}/lib/jakarta-oro.jar" />
						<pathelement location="{$xins_home}/lib/commons-codec.jar" />
						<pathelement location="{$xins_home}/lib/commons-httpclient.jar" />
						<pathelement location="{$xins_home}/lib/commons-logging.jar" />
						<pathelement location="{$xins_home}/lib/xmlenc.jar"          />
						<fileset dir="${{ant.home}}/lib" includes="**/*.jar" />
						<xsl:apply-templates select="document($api_file)/api/impl-java/dependency[not(@type) or @type='compile' or @type='compile_and_runtime']" />
						<xsl:if test="impl">
							<xsl:variable name="impl_file"    select="concat($project_home, '/apis/', $api, '/impl/impl.xml')" />
							<xsl:apply-templates select="document($impl_file)/impl/dependency[not(@type) or @type='compile' or @type='compile_and_runtime']" />
						</xsl:if>
					</classpath>
				</javadoc>
				<copy
				file="{$xins_home}/src/css/javadoc/style.css"
				tofile="build/javadoc-api/{$api}/stylesheet.css"
				overwrite="true" />
			</target>

			<target name="server-{$api}"
							depends="specdocs-{$api}, javadoc-api-{$api}, war-{$api}"
							description="Generates the war file, the Javadoc API docs for the server side and the specdocs for the '{$api}' API stubs">
			</target>
		</xsl:if>

		<target name="-stubs-capi-{$api}" depends="-prepare-classes" >
			<mkdir dir="{$project_home}/build/java-capi/{$api}/{$clientPackageAsDir}" />
			<xmlvalidate file="{$api_file}" warn="false">
				<xmlcatalog refid="all-dtds" />
			</xmlvalidate>
			<style
			in="{$api_file}"
			out="{$project_home}/build/java-capi/{$api}/{$clientPackageAsDir}/CAPI.java"
			style="{$xins_home}/src/xslt/java-capi/api_to_java.xslt">
				<xmlcatalog refid="all-dtds" />
				<param name="xins_version" expression="{$xins_version}"  />
				<param name="project_home" expression="{$project_home}"  />
				<param name="project_file" expression="{$project_file}"  />
				<param name="specsdir"     expression="{$api_specsdir}"  />
				<param name="api"          expression="{$api}"           />
				<param name="api_file"     expression="{$api_file}"      />
				<param name="package"      expression="{$clientPackage}" />
			</style>
			<style
			in="{$api_file}"
			out="{$project_home}/build/java-capi/{$api}/{$clientPackageAsDir}/package.html"
			style="{$xins_home}/src/xslt/java-capi/api_to_packagehtml.xslt">
				<xmlcatalog refid="all-dtds" />
				<param name="xins_version" expression="{$xins_version}"  />
				<param name="project_home" expression="{$project_home}"  />
				<param name="project_file" expression="{$project_file}"  />
				<param name="specsdir"     expression="{$api_specsdir}"  />
				<param name="api"          expression="{$api}"           />
				<param name="api_file"     expression="{$api_file}"      />
				<param name="package"      expression="{$clientPackage}" />
			</style>
			<xsl:if test="string-length($functionIncludes) &gt; 0">
				<xmlvalidate warn="false">
					<fileset dir="{$api_specsdir}" includes="{$functionIncludes}"/>
					<xmlcatalog refid="all-dtds" />
				</xmlvalidate>
				<style
				basedir="{$api_specsdir}"
				destdir="{$project_home}/build/java-capi/{$api}/{$clientPackageAsDir}"
				style="{$xins_home}/src/xslt/java-capi/function_to_result_java.xslt"
				extension="Result.java"
				includes="{$functionIncludes}">
					<xmlcatalog refid="all-dtds" />
					<param name="xins_version" expression="{$xins_version}"  />
					<param name="project_home" expression="{$project_home}"  />
					<param name="project_file" expression="{$project_file}"  />
					<param name="specsdir"     expression="{$api_specsdir}"  />
					<param name="api"          expression="{$api}"           />
					<param name="api_file"     expression="{$api_file}"      />
					<param name="package"      expression="{$clientPackage}" />
				</style>
				<style
				basedir="{$api_specsdir}"
				destdir="{$project_home}/build/java-capi/{$api}/{$clientPackageAsDir}"
				style="{$xins_home}/src/xslt/java-capi/function_to_request_java.xslt"
				extension="Request.java"
				includes="{$functionIncludes}">
					<xmlcatalog refid="all-dtds" />
					<param name="xins_version" expression="{$xins_version}"  />
					<param name="project_home" expression="{$project_home}"  />
					<param name="project_file" expression="{$project_file}"  />
					<param name="specsdir"     expression="{$api_specsdir}"  />
					<param name="api"          expression="{$api}"           />
					<param name="api_file"     expression="{$api_file}"      />
					<param name="package"      expression="{$clientPackage}" />
				</style>
			</xsl:if>
			<xsl:if test="string-length($resultcodeIncludes) &gt; 0">
				<xmlvalidate warn="false">
					<fileset dir="{$api_specsdir}" includes="{$resultcodeIncludes}"/>
					<xmlcatalog refid="all-dtds" />
				</xmlvalidate>
				<style
				basedir="{$api_specsdir}"
				destdir="{$project_home}/build/java-capi/{$api}/{$clientPackageAsDir}"
				style="{$xins_home}/src/xslt/java-capi/resultcode_to_java.xslt"
				extension="Exception.java"
				includes="{$resultcodeIncludes}">
					<xmlcatalog refid="all-dtds" />
					<param name="xins_version" expression="{$xins_version}"  />
					<param name="project_home" expression="{$project_home}"  />
					<param name="project_file" expression="{$project_file}"  />
					<param name="specsdir"     expression="{$api_specsdir}"  />
					<param name="api"          expression="{$api}"           />
					<param name="api_file"     expression="{$api_file}"      />
					<param name="package"      expression="{$clientPackage}" />
				</style>
			</xsl:if>
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
			<mkdir dir="{$project_home}/build/classes-capi/{$api}" />
			<javac
			srcdir="{$project_home}/build/java-capi/{$api}/"
			destdir="{$project_home}/build/classes-capi/{$api}"
			debug="true"
			deprecation="${{build.deprecation}}"
			source="1.3"
			target="1.3">
				<classpath>
					<pathelement path="{$logdoc.jar}"      />
					<pathelement path="{$xins-common.jar}" />
					<pathelement path="{$xins-client.jar}" />
					<xsl:if test="$apiHasTypes">
						<pathelement path="{$typeClassesDir}"  />
					</xsl:if>
					<fileset dir="{$xins_home}/lib" includes="**/*.jar" />
				</classpath>
			</javac>
			<xsl:if test="$apiHasTypes">
				<copy todir="{$project_home}/build/classes-capi/{$api}">
					<fileset dir="{$typeClassesDir}" includes="**/*.class" />
				</copy>
			</xsl:if>
			<mkdir dir="{$project_home}/build/capis/" />
			<manifest file="{$project_home}/build/capis/{$api}-MANIFEST.MF">
				<attribute name="XINS-Version" value="{$xins_version}" />
				<attribute name="API-Version" value="${{api.version}}" />
			</manifest>
			<jar
			destfile="{$project_home}/build/capis/{$api}-capi.jar"
			basedir="{$project_home}/build/classes-capi/{$api}"
			manifest="{$project_home}/build/capis/{$api}-MANIFEST.MF" />
		</target>

		<target name="javadoc-capi-{$api}" description="Generates Javadoc API docs for the client-side '{$api}' API stubs">
			<xsl:attribute name="depends">
				<xsl:if test="$apiHasTypes">
					<xsl:text>-classes-types-</xsl:text>
					<xsl:value-of select="$api" />
					<xsl:text>,</xsl:text>
				</xsl:if>
				<xsl:text>-stubs-capi-</xsl:text>
				<xsl:value-of select="$api" />
			</xsl:attribute>
			<mkdir dir="build/javadoc-capi/{$api}" />
			<javadoc
			sourcepath="build/java-capi/{$api}"
			destdir="build/javadoc-capi/{$api}"
			version="yes"
			use="yes"
			author="yes"
			access="public"
			source="1.3"
			windowtitle="Call interface for {$api} API"
			doctitle="Call interface for {$api} API">
				<packageset dir="build/java-capi/{$api}" />
				<xsl:if test="$apiHasTypes">
					<packageset dir="build/java-types/{$api}" />
				</xsl:if>
				<link
				href="http://www.xins.org/javadoc/{$xins_version}/"
				offline="true"
				packagelistloc="{$xins_home}/docs/javadoc/" />
				<link
				href="http://java.sun.com/j2se/1.4.2/docs/api"
				offline="true"
				packagelistloc="{$xins_home}/src/package-lists/j2se/" />
				<link
				href="http://jakarta.apache.org/log4j/docs/api/"
				offline="true"
				packagelistloc="{$xins_home}/src/package-lists/log4j/" />
				<link
				href="http://xmlenc.sourceforge.net/javadoc/{$xmlenc_version}/"
				offline="true"
				packagelistloc="{$xins_home}/src/package-lists/xmlenc/" />
				<link
				href="http://xins.sourceforge.net/ant-1.6.2-docs/"
				offline="true"
				packagelistloc="{$xins_home}/src/package-lists/ant/" />
				<classpath>
					<pathelement location="{$xins_home}/build/logdoc.jar"       />
					<pathelement location="{$xins_home}/build/xins-common.jar"   />
					<pathelement location="{$xins_home}/build/xins-client.jar"   />
					<pathelement location="{$xins_home}/lib/log4j.jar"           />
					<pathelement location="{$xins_home}/lib/jakarta-oro.jar" />
					<pathelement location="{$xins_home}/lib/commons-codec.jar" />
					<pathelement location="{$xins_home}/lib/commons-httpclient.jar" />
					<pathelement location="{$xins_home}/lib/commons-logging.jar" />
					<pathelement location="{$xins_home}/lib/xmlenc.jar"          />
					<fileset dir="${{ant.home}}/lib" includes="**/*.jar" />
				</classpath>
			</javadoc>
			<copy
			file="{$xins_home}/src/css/javadoc/style.css"
			tofile="build/javadoc-capi/{$api}/stylesheet.css"
			overwrite="true" />
		</target>

		<target name="client-{$api}"
						depends="jar-{$api}, javadoc-capi-{$api}, specdocs-{$api}"
						description="Generates the Javadoc API docs for the client side and the client JAR file for the '{$api}' API stubs and zip the result.">
			<zip destfile="{$builddir}/specdocs/{$api}/{$api}-client.zip">
				<fileset dir="{$builddir}/capis" includes="{$api}-capi.jar" />
				<zipfileset dir="{$builddir}/javadoc-capi/{$api}" prefix="javadoc" />
				<zipfileset dir="{$builddir}/java-capi/{$api}" prefix="java" />
				<zipfileset dir="{$builddir}/specdocs/{$api}" excludes="{$api}-client.zip" prefix="specdocs" />
			</zip>
		</target>

		<target name="all-{$api}"
						description="Generates everything for the '{$api}' API stubs.">
			<xsl:attribute name="depends">
				<xsl:text>client-</xsl:text>
				<xsl:value-of select="$api" />
				<xsl:if test="document($api_file)/api/impl-java or impl">
					<xsl:text>, server-</xsl:text>
					<xsl:value-of select="$api" />
				</xsl:if>
			</xsl:attribute>
		</target>

		<target name="clean-{$api}" description="Deletes everything for the '{$api}' API stubs.">
			<delete dir="build/capis/{$api}-capi.jar" />
			<delete dir="build/classes-api/{$api}" />
			<delete dir="build/classes-capi/{$api}" />
			<delete dir="build/classes-types/{$api}" />
			<delete dir="build/java-capi/{$api}" />
			<delete dir="build/java-combined/{$api}" />
			<delete dir="build/java-fundament/{$api}" />
			<delete dir="build/java-types/{$api}" />
			<delete dir="build/javadoc-api/{$api}" />
			<delete dir="build/javadoc-capi/{$api}" />
			<delete dir="build/logdoc/{$api}" />
			<delete dir="build/specdocs/{$api}" />
			<delete dir="build/types/{$api}" />
			<delete dir="build/webapps/{$api}" />
		</target>

		<target name="rebuild-{$api}" depends="clean-{$api}, all-{$api}"
						description="Regenerates everything for the '{$api}' API stubs." />
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
