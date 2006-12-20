<?xml version="1.0" encoding="US-ASCII"?>
<!--
 XSLT that generates the build.xml used to compile the different APIs.

 $Id$

 Copyright 2003-2006 Orange Nederland Breedband B.V.
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
	<xsl:include href="tools.xslt"  />

	<xsl:output indent="yes" />

	<xsl:variable name="xmlenc_version"    select="'0.52'"                                      />
	<xsl:variable name="xins_buildfile"    select="concat($xins_home,    '/build.xml')"         />
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
- create-rcd          Generates a new error code specification
                      file.
- create-type         Generates a new type specification file.
- create-example      Generates a new example for a function.
- create-logdoc       Generates the basic logdoc files for an API.

The following commands can be used to run a tool on an API:
java2html, pmd, checkstyle, coverage, findbugs, lint4j, jdepend,
cvschangelog, jmeter, run-jmeter, maven, eclipse.
More information is available in the user guide.

The following targets are specific for a single API,
replace <api> with the name of an existing API:
- run-<api>           Runs the WAR file for the API.
- war-<api>           Creates the WAR file for the API.
- specdocs-<api>      Generates all specification docs for the API.
- javadoc-api-<api>   Generates Javadoc for the server API
                      implementation.
- server-<api>        Generates the WAR file, the API Javadoc for
                      the server side and the specdocs for the API.
- jar-<api>           Generates and compiles the CAPI classes.
- javadoc-capi-<api>  Generates the Javadoc for the CAPI classes.
- client-<api>        Generates the CAPI JAR file and the
                      corresponding Javadoc.
- clean-<api>         Cleans everything for the API.
- rebuild-<api>       Regenerates everything for the API.
- all-<api>           Generates everything for the API.
- wsdl-<api>          Generates the WSDL for the API.
- stub-<api>          Generates the stub for the API.
- test-<api>          Generates (if needed) and runs the tests.
- opendoc-<api>       Generates the specifications in Opendoc format for the API.

APIs in this project are:
]]></echo>
				<echo>
					<xsl:for-each select="api">
						<xsl:variable name="api" select="@name" />
						<xsl:text>"</xsl:text>
							<xsl:value-of select="$api" />
						<xsl:text>" </xsl:text>
						<xsl:for-each select="impl/@name">
							<xsl:variable name="impl" select="." />
							<xsl:value-of select="concat('&quot;', $api, '-', $impl, '&quot; ')" />
						</xsl:for-each>
					</xsl:for-each>
				</echo>
			</target>

			<target name="ask" description="Asks for the command and API to execute.">
				<input addproperty="command"
				       message="Command "
				       validargs="run,war,specdocs,javadoc-api,jar,javadoc-capi,all,clean,client,server,wsdl,stub,test" />
				<input addproperty="api"
				       message="API ">
					<xsl:attribute name="validargs">
						<xsl:for-each select="api">
							<xsl:if test="position() &gt; 1">,</xsl:if>
							<xsl:variable name="api" select="@name" />
							<xsl:value-of select="$api" />
							<xsl:for-each select="impl/@name">
								<xsl:variable name="impl" select="." />
								<xsl:value-of select="concat(',', $api, '-', $impl)" />
							</xsl:for-each>
						</xsl:for-each>
					</xsl:attribute>
				</input>
				<antcall target="${{command}}-${{api}}" />
			</target>

			<xsl:call-template name="createproject" />

			<xsl:call-template name="createexample">
				<xsl:with-param name="xins_home" select="$xins_home" />
			</xsl:call-template>

			<xsl:call-template name="tools">
				<xsl:with-param name="xins_home" select="$xins_home" />
				<xsl:with-param name="project_home" select="$project_home" />
				<xsl:with-param name="cvsweb" select="cvsweb/@href" />
			</xsl:call-template>

			<target name="-prepare">
				<property file="{$project_home}/build.properties" />
				<property name="reload.stylesheet" value="false" />
			</target>

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

					<dtd publicId="-//XINS//DTD XINS Project 1.3//EN"
					     location="xins-project_1_3.dtd" />
					<dtd publicId="-//XINS//DTD XINS API 1.3//EN"
					     location="api_1_3.dtd" />
					<dtd publicId="-//XINS//DTD Function 1.3//EN"
					     location="function_1_3.dtd" />
					<dtd publicId="-//XINS//DTD Category 1.3//EN"
					     location="category_1_3.dtd" />
					<dtd publicId="-//XINS//DTD Type 1.3//EN"
					     location="type_1_3.dtd" />
					<dtd publicId="-//XINS//DTD Result Code 1.3//EN"
					     location="resultcode_1_3.dtd" />
					<dtd publicId="-//XINS//DTD Implementation 1.3//EN"
					     location="impl_1_3.dtd" />
					<dtd publicId="-//XINS//DTD Environments 1.3//EN"
					     location="environments_1_3.dtd" />
					<dtd publicId="-//XINS//DTD XINS Logdoc 1.3//EN"
					     location="log_1_3.dtd" />
					<dtd publicId="-//XINS//DTD XINS Translation Bundle 1.3//EN"
					     location="translation-bundle_1_3.dtd" />

					<dtd publicId="-//XINS//DTD XINS Project 1.4//EN"
					     location="xins-project_1_4.dtd" />
					<dtd publicId="-//XINS//DTD XINS API 1.4//EN"
					     location="api_1_4.dtd" />
					<dtd publicId="-//XINS//DTD Function 1.4//EN"
					     location="function_1_4.dtd" />
					<dtd publicId="-//XINS//DTD Category 1.4//EN"
					     location="category_1_4.dtd" />
					<dtd publicId="-//XINS//DTD Type 1.4//EN"
					     location="type_1_4.dtd" />
					<dtd publicId="-//XINS//DTD Result Code 1.4//EN"
					     location="resultcode_1_4.dtd" />
					<dtd publicId="-//XINS//DTD Implementation 1.4//EN"
					     location="impl_1_4.dtd" />
					<dtd publicId="-//XINS//DTD Environments 1.4//EN"
					     location="environments_1_4.dtd" />
					<dtd publicId="-//XINS//DTD XINS Logdoc 1.4//EN"
					     location="log_1_4.dtd" />
					<dtd publicId="-//XINS//DTD XINS Translation Bundle 1.4//EN"
					     location="translation-bundle_1_4.dtd" />

					<dtd publicId="-//XINS//DTD XINS Project 1.5//EN"
					     location="xins-project_1_5.dtd" />
					<dtd publicId="-//XINS//DTD XINS API 1.5//EN"
					     location="api_1_5.dtd" />
					<dtd publicId="-//XINS//DTD Function 1.5//EN"
					     location="function_1_5.dtd" />
					<dtd publicId="-//XINS//DTD Category 1.5//EN"
					     location="category_1_5.dtd" />
					<dtd publicId="-//XINS//DTD Type 1.5//EN"
					     location="type_1_5.dtd" />
					<dtd publicId="-//XINS//DTD Result Code 1.5//EN"
					     location="resultcode_1_5.dtd" />
					<dtd publicId="-//XINS//DTD Implementation 1.5//EN"
					     location="impl_1_5.dtd" />
					<dtd publicId="-//XINS//DTD Environments 1.5//EN"
					     location="environments_1_5.dtd" />
					<dtd publicId="-//XINS//DTD XINS Logdoc 1.5//EN"
					     location="log_1_5.dtd" />
					<dtd publicId="-//XINS//DTD XINS Translation Bundle 1.5//EN"
					     location="translation-bundle_1_5.dtd" />
				</xmlcatalog>
			</target>

			<target name="index-specdocs" depends="-prepare-specdocs" description="Generates the API index">
				<xmlvalidate file="{$project_file}" warn="false">
					<xmlcatalog refid="all-dtds" />
				</xmlvalidate>
				<xslt
				in="{$project_file}"
				out="{$builddir}/specdocs/index.html"
				style="{$xins_home}/src/xslt/specdocs/xins-project_to_index.xslt">
					<xmlcatalog refid="all-dtds" />
					<param name="xins_version" expression="{$xins_version}" />
					<param name="project_home" expression="{$project_home}" />
					<param name="project_file" expression="{$project_file}" />
					<param name="specsdir"     expression="{$specsdir}"     />
				</xslt>
				<xslt
				in="{$project_file}"
				out="{$builddir}/specdocs/help.html"
				style="{$xins_home}/src/xslt/specdocs/xins-project_to_help.xslt">
					<xmlcatalog refid="all-dtds" />
					<param name="xins_version" expression="{$xins_version}" />
					<param name="project_home" expression="{$project_home}" />
					<param name="project_file" expression="{$project_file}" />
					<param name="specsdir"     expression="{$specsdir}"     />
				</xslt>
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
				<!-- If not set by the user set the default properties. -->
				<property name="build.deprecation" value="true" />
				<property name="build.java.version" value="${{ant.java.version}}" />
				<condition property="build.generics">
					<and>
						<not>
							<equals arg1="${{build.java.version}}" arg2="1.3" />
						</not>
						<not>
							<equals arg1="${{build.java.version}}" arg2="1.4" />
						</not>
					</and>
				</condition>
				<path id="xins.classpath">
					<pathelement path="{$xins_home}/build/logdoc.jar" />
					<pathelement path="{$xins_home}/build/xins-common.jar" />
					<pathelement path="{$xins_home}/build/xins-server.jar" />
					<pathelement path="{$xins_home}/build/xins-client.jar" />
					<fileset dir="{$xins_home}/lib" includes="**/*.jar" />
				</path>
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
					<xsl:for-each select="api">
						<!-- If old API -->
						<xsl:variable name="api" select="@name" />
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
						<xsl:if test="@name">
							<xsl:value-of select="concat('-', @name)" />
						</xsl:if>
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
		<xsl:variable name="api_node" select="document($api_file)/api" />
		<xsl:variable name="typeClassesDir"    select="concat($project_home, '/build/classes-types/', $api)" />
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
		<xsl:variable name="resultcodeIncludes">
			<xsl:for-each select="$api_node/resultcode">
				<xsl:if test="position() &gt; 1">,</xsl:if>
				<xsl:value-of select="@name" />
				<xsl:text>.rcd</xsl:text>
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

		<target name="specdocs-{$api}" depends="index-specdocs" description="Generates all specification docs for the '{$api}' API">
      <mkdir dir="{$project_home}/build/specdocs/{$api}" />
			<dependset>
				<srcfilelist dir="{$api_specsdir}" files="{$functionIncludes}" />
				<xsl:if test="string-length($typeIncludes) &gt; 0">
					<srcfilelist dir="{$api_specsdir}" files="{$typeIncludes}" />
				</xsl:if>
				<targetfileset dir="{$project_home}/build/specdocs/{$api}" includes="index.html" />
			</dependset>
			<dependset>
				<srcfilelist dir="{$api_specsdir}" files="api.xml" />
				<targetfileset dir="{$project_home}/build/specdocs/{$api}" includes="*.html" />
			</dependset>
			<xsl:if test="environments">
				<xsl:variable name="env_dir" select="concat($project_home, '/apis/', $api)" />
				<dependset>
					<srcfilelist dir="{$env_dir}" files="environments.xml" />
					<targetfileset dir="{$project_home}/build/specdocs/{$api}" includes="*.html" />
				</dependset>
			</xsl:if>
			<copy todir="{$builddir}/specdocs/{$api}" file="{$xins_home}/src/css/specdocs/style.css" />
			<copy todir="{$builddir}/specdocs/{$api}" file="{$xins_home}/src/xslt/testforms/testforms.js" />
			<xmlvalidate file="{$api_file}" warn="false">
				<xmlcatalog refid="all-dtds" />
			</xmlvalidate>
			<xslt
			in="{$api_file}"
			out="{$project_home}/build/specdocs/{$api}/index.html"
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
			destdir="{$project_home}/build/specdocs/{$api}"
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
				destdir="{$project_home}/build/specdocs/{$api}"
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
					select="concat($project_home, '/build/specdocs/', $api, '/', substring-after(@name, '/'), '.html')" />
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
				destdir="{$project_home}/build/specdocs/{$api}"
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
			<xsl:if test="$api_node/category">
				<xmlvalidate warn="false">
					<fileset dir="{$api_specsdir}" includes="{$categoryIncludes}"/>
					<xmlcatalog refid="all-dtds" />
				</xmlvalidate>
				<xslt
				basedir="{$api_specsdir}"
				destdir="{$project_home}/build/specdocs/{$api}"
				style="{$xins_home}/src/xslt/specdocs/category_to_html.xslt"
				includes="{$categoryIncludes}">
					<xmlcatalog refid="all-dtds" />
					<param name="xins_version" expression="{$xins_version}" />
					<param name="specsdir"     expression="{$api_specsdir}" />
					<param name="api"          expression="{$api}"          />
				</xslt>
			</xsl:if>
			<xsl:if test="$api_node/environment or not(environments)">
				<xslt
				basedir="{$api_specsdir}"
				destdir="{$project_home}/build/specdocs/{$api}"
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
				<xslt
				basedir="{$api_specsdir}"
				destdir="{$project_home}/build/specdocs/{$api}"
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
			</xsl:if>
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
					out="{$project_home}/build/specdocs/{$api}/properties{$implName2}.html"
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
					<xsl:variable name="javaDestFileDir" select="concat($project_home, '/build/java-fundament/', $api, $implName2, '/', $packageAsDir)" />
					<echo message="Generating the logdoc for {$api}{$implName2}" />
					<mkdir dir="{$project_home}/build/logdoc/{$api}{$implName2}" />
					<xmlvalidate file="{$impl_dir}/log.xml" warn="false">
						<xmlcatalog refid="all-dtds" />
					</xmlvalidate>
					<xslt
					in="{$impl_dir}/log.xml"
					out="{$project_home}/build/logdoc/{$api}{$implName2}/build.xml"
					style="{$xins_home}/src/xslt/logdoc/log_to_build.xslt">
						<xmlcatalog refid="all-dtds" />
						<param name="xins_home"       expression="{$xins_home}" />
						<param name="logdoc_xslt_dir" expression="{$xins_home}/src/xslt/logdoc" />
						<param name="sourcedir"       expression="{$impl_dir}" />
						<param name="html_destdir"    expression="{$project_home}/build/specdocs/{$api}/logdoc{$implName2}" />
						<param name="java_destdir"    expression="{$javaDestFileDir}" />
						<param name="package_name"    expression="{$package}" />
					</xslt>
					<copy file="{$xins_home}/src/css/logdoc/style.css" todir="{$project_home}/build/specdocs/{$api}/logdoc{$implName2}" />
					<ant dir="{$project_home}/build/logdoc/{$api}{$implName2}" target="html" inheritall="false" />
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
				<xsl:variable name="javaDestDir"    select="concat($project_home, '/build/java-types/', $api)" />
				<xsl:variable name="copiedTypesDir" select="concat($project_home, '/build/types/',      $api)" />

				<xsl:if test="string-length($typeIncludes) &gt; 0">
					<copy todir="{$copiedTypesDir}">
						<fileset dir="{$api_specsdir}" includes="{$typeIncludes}" />
						<mapper classname="org.xins.common.ant.HungarianMapper" classpath="{$xins_home}/build/xins-common.jar" />
					</copy>
				</xsl:if>
				<xsl:for-each select="$api_node/type">
					<xsl:if test="contains(@name, '/')">
						<xsl:variable name="shared_type_file"
						select="concat($api_specsdir, '/../../', substring-before(@name, '/'), '/spec/', substring-after(@name, '/'), '.typ')" />
						<copy
						file="{$shared_type_file}"
						todir="{$copiedTypesDir}">
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
			<property file="{$project_home}/build.properties" />
			<property name="wsdl.endpoint" value="" />
			<mkdir dir="{$builddir}/wsdl" />
			<tstamp>
				<format property="timestamp" pattern="yyyy.MM.dd HH:mm:ss.SS" />
			</tstamp>
			<xslt
			in="{$api_specsdir}/api.xml"
			out="{$builddir}/wsdl/{$api}.wsdl"
			style="{$xins_home}/src/xslt/webapp/api_to_wsdl.xslt">
				<xmlcatalog refid="all-dtds" />
				<param name="project_home" expression="{$project_home}" />
				<param name="project_file" expression="{$project_file}" />
				<param name="specsdir"     expression="{$api_specsdir}" />
				<param name="endpoint"     expression="${{wsdl.endpoint}}" />
				<param name="xins_version" expression="{$xins_version}" />
				<param name="timestamp"    expression="${{timestamp}}"  />
			</xslt>
			<replace file="{$builddir}/wsdl/{$api}.wsdl">
				<replacefilter token="urn:apiname" value="urn:{$api}" />
				<replacefilter token="//?_convention=_xins-soap" value="/?_convention=_xins-soap" />
			</replace>
		</target>

		<target name="opendoc-{$api}" description="Generates the specification document for the '{$api}' API">
			<mkdir dir="{$builddir}/opendoc/{$api}" />
			<dependset>
				<srcfilelist dir="{$api_specsdir}" files="{$functionIncludes}" />
				<srcfilelist dir="{$api_specsdir}" files="{$resultcodeIncludes}" />
				<xsl:if test="string-length($typeIncludes) &gt; 0">
					<srcfilelist dir="{$api_specsdir}" files="{$typeIncludes}" />
				</xsl:if>
				<targetfileset dir="{$builddir}/opendoc/{$api}" includes="content.xml" />
			</dependset>
			<xslt
			in="{$api_specsdir}/api.xml"
			out="{$builddir}/opendoc/{$api}/content.xml"
			style="{$xins_home}/src/xslt/opendoc/api_to_content.xslt">
				<xmlcatalog refid="all-dtds" />
				<param name="project_home" expression="{$project_home}" />
				<param name="project_file" expression="{$project_file}" />
				<param name="specsdir"     expression="{$api_specsdir}" />
				<param name="api"          expression="{$api}"          />
			</xslt>
			<copy file="{$xins_home}/src/opendoc/meta.xml" tofile="{$builddir}/opendoc/{$api}/meta.xml" />
			<tstamp>
				<format property="timestamp" pattern="yyyy-MM-dd'T'HH:mm:ss" />
			</tstamp>
			<replace file="{$builddir}/opendoc/{$api}/meta.xml">
				<replacefilter token="#version#" value="{$xins_version}" />
				<replacefilter token="#date#" value="${{timestamp}}" />
			</replace>
			<zip destfile="{$builddir}/opendoc/{$api}/{$api}-specs.odt">
				<fileset dir="{$builddir}/opendoc/{$api}" includes="content.xml meta.xml" />
				<fileset dir="{$xins_home}/src/opendoc" includes="mimetype styles.xml" />
				<zipfileset dir="{$xins_home}/src/opendoc" includes="manifest.xml" prefix="META-INF" />
			</zip>
		</target>

		<xsl:for-each select="$api_node/impl-java | impl">
			<xsl:variable name="implName" select="@name" />
			<xsl:variable name="implName2">
				<xsl:if test="@name and string-length($implName) &gt; 0">
					<xsl:value-of select="concat('-', $implName)" />
				</xsl:if>
			</xsl:variable>
			<xsl:variable name="javaImplDir">
				<xsl:value-of select="$project_home" />
				<xsl:text>/</xsl:text>
				<xsl:choose>
					<xsl:when test="$project_node/api[@name=$api]/impl">
						<xsl:value-of select="concat('apis/', $api, '/impl', $implName2)" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when test="$project_node/@javadir">
								<xsl:value-of select="$project_node/@javadir" />
							</xsl:when>
							<xsl:otherwise>src/impl-java</xsl:otherwise>
						</xsl:choose>
						<xsl:text>/</xsl:text>
						<xsl:value-of select="$api" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:variable name="javaDestDir"     select="concat($project_home,    '/build/java-fundament/', $api, $implName2)" />
			<xsl:variable name="classesDestDir"  select="concat($project_home,    '/build/classes-api/',    $api, $implName2)" />
			<xsl:variable name="javaDestFileDir" select="concat($javaDestDir, '/', $packageAsDir)" />

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
					name="-impl-{$api}{$implName2}-{$function}-unavail"
					depends="-impl-{$api}{$implName2}-existencechecks"
					if="exists-{$api}{$implName2}-{$classname}">
					<echo message="Not overwriting existing file: {$javaImplFile}" />
				</target>
				<target
					name="-skeleton-impl-{$api}{$implName2}-{$function}"
					depends="-impl-{$api}{$implName2}-{$function}-unavail, -prepare-classes"
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
					<xsl:choose>
						<xsl:when test="local-name() = 'impl'">
							<srcfilelist dir="{$api_specsdir}/../impl{$implName2}" files="impl.xml" />
						</xsl:when>
						<xsl:otherwise>
							<srcfilelist dir="{$api_specsdir}" files="api.xml" />
						</xsl:otherwise>
					</xsl:choose>
					<srcfileset dir="{$api_specsdir}">
						<include name="{$functionIncludes} {$typeIncludes} {$resultcodeIncludes}" />
					</srcfileset>
					<targetfileset dir="{$javaDestDir}/{$packageAsDir}" includes="*.java" />
				</dependset>
				<xsl:variable name="impl_file">
					<xsl:choose>
						<xsl:when test="local-name() = 'impl'">
							<xsl:value-of select="concat($project_home, '/apis/', $api, '/impl', $implName2, '/impl.xml')" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$api_file" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
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
				<xsl:if test="string-length($resultcodeIncludes) &gt; 0">
					<dependset>
						<srcfilelist   dir="{$api_specsdir}" files="{$functionIncludes}" />
						<targetfileset dir="{$javaDestDir}/{$packageAsDir}" includes="*Result.java" />
						<targetfileset dir="{$javaDestDir}" includes="resultcodes.xml" />
					</dependset>
					<xmlvalidate warn="false">
						<fileset dir="{$api_specsdir}" includes="{$resultcodeIncludes}"/>
						<xmlcatalog refid="all-dtds" />
					</xmlvalidate>
					<!-- An intermediate file containing all the functions/result codes is created for performance reasons. -->
					<xslt
					in="{$api_file}"
					out="{$javaDestDir}/resultcodes.xml"
					style="{$xins_home}/src/xslt/java-server-framework/api_to_resultcodes.xslt">
						<xmlcatalog refid="all-dtds" />
						<param name="specsdir"     expression="{$api_specsdir}" />
					</xslt>
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

				<!-- Generate the logdoc java file is needed -->
				<xsl:if test="$api_node/impl-java/logdoc">
					<echo message="Generating the logdoc for {$api}" />
					<mkdir dir="{$project_home}/build/logdoc/{$api}" />
					<xslt
					in="{$project_home}/src/logdoc/{$api}/log.xml"
					out="{$project_home}/build/logdoc/{$api}/build.xml"
					style="{$xins_home}/src/xslt/logdoc/log_to_build.xslt"
					reloadstylesheet="${{reload.stylesheet}}">
						<xmlcatalog refid="all-dtds" />
						<param name="xins_home"       expression="{$xins_home}" />
						<param name="logdoc_xslt_dir" expression="{$xins_home}/src/xslt/logdoc" />
						<param name="logdoc_dtd_dir"  expression="{$xins_home}/src/dtd" />
						<param name="sourcedir"       expression="{$project_home}/src/logdoc/{$api}" />
						<param name="html_destdir"    expression="{$project_home}/build/specdocs/{$api}/logdoc" />
						<param name="java_destdir"    expression="{$javaDestFileDir}" />
						<param name="package_name"    expression="{$package}" />
					</xslt>
					<ant antfile="{$project_home}/build/logdoc/{$api}/build.xml" target="java" />
				</xsl:if>
				<xsl:if test="local-name() = 'impl'">
					<xsl:variable name="impl_dir"     select="concat($project_home, '/apis/', $api, '/impl', $implName2)" />
					<xsl:variable name="impl_file"    select="concat($impl_dir, '/impl.xml')" />
					<xsl:variable name="impl_node"    select="document($impl_file)/impl" />
					<xmlvalidate file="{$impl_file}" warn="false">
						<xmlcatalog refid="all-dtds" />
					</xmlvalidate>
					<xsl:if test="$impl_node/logdoc">
						<echo message="Generating the logdoc for {$api}{$implName2}" />
						<mkdir dir="{$project_home}/build/logdoc/{$api}{$implName2}" />
						<xmlvalidate file="{$impl_dir}/log.xml" warn="false">
							<xmlcatalog refid="all-dtds" />
						</xmlvalidate>
						<xsl:variable name="accesslevel" select="$impl_node/logdoc/@accesslevel" />
						<xslt
						in="{$impl_dir}/log.xml"
						out="{$project_home}/build/logdoc/{$api}{$implName2}/build.xml"
						style="{$xins_home}/src/xslt/logdoc/log_to_build.xslt">
							<xmlcatalog refid="all-dtds" />
							<param name="xins_home"       expression="{$xins_home}" />
							<param name="logdoc_xslt_dir" expression="{$xins_home}/src/xslt/logdoc" />
							<param name="sourcedir"       expression="{$impl_dir}" />
							<param name="html_destdir"    expression="{$project_home}/build/specdocs/{$api}/logdoc{$implName2}" />
							<param name="java_destdir"    expression="{$javaDestFileDir}" />
							<param name="package_name"    expression="{$package}" />
						</xslt>
						<ant antfile="{$project_home}/build/logdoc/{$api}{$implName2}/build.xml" target="java">
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
				source="${{build.java.version}}"
				target="${{build.java.version}}">
					<src path="{$javaDestDir}" />
					<src path="{$javaImplDir}" />
					<classpath>
						<xsl:if test="$apiHasTypes">
							<pathelement path="{$typeClassesDir}" />
						</xsl:if>
						<path refid="xins.classpath" />
						<xsl:apply-templates select="$api_node/impl-java/dependency[not(@type) or @type='compile' or @type='compile_and_runtime']" />
						<xsl:if test="local-name() = 'impl'">
							<xsl:variable name="impl_file"    select="concat($project_home, '/apis/', $api, '/impl', $implName2, '/impl.xml')" />
							<xsl:apply-templates select="document($impl_file)/impl/dependency" />
						</xsl:if>
					</classpath>
				</javac>

				<!-- Try to load the API specific .version.properties -->
				<property prefix="api." file="{$api_specsdir}/../.version.properties" />
				<condition property="api.version" value="${{api.version.major}}.${{api.version.minor}}">
					<isset property="api.version.major" />
				</condition>
			</target>

			<target name="war-{$api}{$implName2}" depends="classes-api-{$api}{$implName2}, -load-version, wsdl-{$api}" description="Creates the WAR for the '{$api}{$implName2}' API">
				<mkdir dir="{$project_home}/build/webapps/{$api}{$implName2}" />
				<taskdef name="hostname" classname="org.xins.common.ant.HostnameTask" classpath="{$xins_home}/build/xins-common.jar" />
				<tstamp>
					<format property="timestamp" pattern="yyyy.MM.dd HH:mm:ss.SS" />
				</tstamp>
				<hostname />
				<delete file="{$project_home}/build/webapps/{$api}{$implName2}/web.xml" />
				<xmlvalidate file="{$api_file}" warn="false">
					<xmlcatalog refid="all-dtds" />
				</xmlvalidate>
				<xslt
				in="{$api_file}"
				out="{$project_home}/build/webapps/{$api}{$implName2}/web.xml"
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
				</xslt>
				<fixcrlf srcdir="{$project_home}/build/webapps/{$api}{$implName2}" includes="web.xml" eol="unix" />
				<manifest file="{$project_home}/build/webapps/{$api}{$implName2}/MANIFEST.MF">
					<attribute name="XINS-Version" value="{$xins_version}" />
					<attribute name="API-Version" value="${{api.version}}" />
				</manifest>
				<property name="classes.api.dir" value="{$classesDestDir}" />
				<war
					webxml="{$project_home}/build/webapps/{$api}{$implName2}/web.xml"
					destfile="{$project_home}/build/webapps/{$api}{$implName2}/{$api}{$implName2}.war"
					manifest="{$project_home}/build/webapps/{$api}{$implName2}/MANIFEST.MF"
					duplicate="fail">
					<lib dir="{$xins_home}/build" includes="logdoc.jar" />
					<lib dir="{$xins_home}/build" includes="xins-common.jar" />
					<lib dir="{$xins_home}/build" includes="xins-server.jar" />
					<lib dir="{$xins_home}/build" includes="xins-client.jar" />
					<lib dir="{$xins_home}/lib"   includes="commons-codec.jar commons-httpclient.jar commons-logging.jar jakarta-oro.jar log4j.jar xmlenc.jar" />
					<xsl:apply-templates select="$api_node/impl-java/dependency[not(@type) or @type='runtime' or @type='compile_and_runtime']" mode="lib" />
					<xsl:if test="local-name() = 'impl'">
						<xsl:variable name="impl_file"    select="concat($project_home, '/apis/', $api, '/impl', $implName2, '/impl.xml')" />
						<xsl:apply-templates select="document($impl_file)/impl/dependency" mode="lib" />
						<xsl:apply-templates select="document($impl_file)/impl/content" />
					</xsl:if>
					<classes dir="${{classes.api.dir}}" includes="**/*.class" />
					<xsl:if test="$apiHasTypes">
						<classes dir="{$typeClassesDir}" includes="**/*.class" />
					</xsl:if>
					<classes dir="{$javaImplDir}" excludes="**/*.java" />
					<zipfileset dir="{$project_home}/build/wsdl" includes="{$api}.wsdl" prefix="WEB-INF" />
					<zipfileset dir="{$api_specsdir}" includes="api.xml {$functionIncludes} {$typeIncludes} {$resultcodeIncludes} {$categoryIncludes}" prefix="specs" />
					<xsl:for-each select="$api_node/type">
						<xsl:if test="contains(@name, '/')">
							<xsl:variable name="type_dir"
							select="concat($project_home, '/apis/', substring-before(@name, '/'), '/spec')" />
							<xsl:variable name="type_filename"
							select="concat(substring-after(@name, '/'), '.typ')" />
							<zipfileset dir="{$type_dir}" includes="{$type_filename}" prefix="specs" />
						</xsl:if>
					</xsl:for-each>
				</war>
				<checksum file="{$project_home}/build/webapps/{$api}{$implName2}/{$api}{$implName2}.war" property="war.md5"/>
				<echo message="MD5: ${{war.md5}}" />
				<echo message="Build time: ${{timestamp}}" />
			</target>

			<target name="run-{$api}{$implName2}" depends="war-{$api}{$implName2}" description="Runs the '{$api}{$implName2}' API">
				<property name="org.xins.server.config" value="" />
				<property name="servlet.port" value="8080" />
				<java classname="org.xins.common.servlet.container.HTTPServletStarter"
							fork="true">
					<jvmarg value="-Dorg.xins.server.config=${{org.xins.server.config}}" />
					<jvmarg value="-Dcom.sun.management.jmxremote" />
					<jvmarg value="-Dcom.sun.management.jmxremote.port=1090"/>
					<jvmarg value="-Dcom.sun.management.jmxremote.authenticate=false"/>
					<jvmarg value="-Dcom.sun.management.jmxremote.ssl=false"/>
					<arg path="{$project_home}/build/webapps/{$api}{$implName2}/{$api}{$implName2}.war" />
					<arg value="${{servlet.port}}" />
					<classpath>
						<path refid="xins.classpath" />
						<path location="{$project_home}/build/classes-api/{$api}{$implName2}" />
						<xsl:if test="$apiHasTypes">
							<path location="{$project_home}/build/classes-types/{$api}" />
						</xsl:if>
					</classpath>
				</java>
			</target>

			<target name="javadoc-api-{$api}{$implName2}" depends="classes-api-{$api}{$implName2}" description="Generates Javadoc API docs for the '{$api}{$implName2}' API">
				<property file="{$xins_home}/.version.properties" />
				<mkdir dir="{$project_home}/build/javadoc-api/{$api}{$implName2}" />
				<javadoc
				destdir="{$project_home}/build/javadoc-api/{$api}{$implName2}"
				version="yes"
				use="yes"
				author="yes"
				access="package"
				windowtitle="Implementation of {$api} API"
				doctitle="Implementation of {$api} API">
					<packageset dir="{$javaDestDir}" />
					<packageset dir="{$javaImplDir}" />
					<xsl:if test="$apiHasTypes">
						<packageset dir="{$project_home}/build/java-types/{$api}" />
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
					<classpath>
						<path refid="xins.classpath" />
						<xsl:apply-templates select="$api_node/impl-java/dependency[not(@type) or @type='compile' or @type='compile_and_runtime']" />
						<xsl:if test="local-name() = 'impl'">
							<xsl:variable name="impl_file"    select="concat($project_home, '/apis/', $api, '/impl', $implName2, '/impl.xml')" />
							<xsl:apply-templates select="document($impl_file)/impl/dependency" />
						</xsl:if>
					</classpath>
				</javadoc>
				<copy
				file="{$xins_home}/src/css/javadoc/style.css"
				tofile="{$project_home}/build/javadoc-api/{$api}/stylesheet.css"
				overwrite="true" />
			</target>

			<target name="create-impl-{$api}{$implName2}" unless="impl.exists">
				<mkdir dir="{$api_specsdir}/../impl{$implName2}" />
				<echo file="{$api_specsdir}/../impl{$implName2}/impl.xml"><![CDATA[<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE impl PUBLIC "-//XINS//DTD Implementation 1.4//EN" "http://xins.sourceforge.net/dtd/impl_1_4.dtd">

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
				<property name="classes.api.dir" value="{$project_home}/build/classes-api/{$api}" />
				<mkdir dir="{$project_home}/build/classes-tests/{$api}" />
				<javac
				destdir="{$project_home}/build/classes-tests/{$api}"
				debug="true"
				deprecation="${{build.deprecation}}"
				source="${{build.java.version}}"
				target="${{build.java.version}}">
					<src path="apis/{$api}/test" />
					<classpath>
						<path refid="xins.classpath" />
						<pathelement path="{$project_home}/build/capis/{$api}-capi.jar" />
						<pathelement path="${{classes.api.dir}}" />
						<xsl:if test="$apiHasTypes">
							<pathelement path="{$project_home}/build/classes-types/{$api}" />
						</xsl:if>
						<xsl:if test="impl">
							<xsl:variable name="impl_file" select="concat($project_home, '/apis/', $api, '/impl/impl.xml')" />
							<xsl:apply-templates select="document($impl_file)/impl/dependency" />
						</xsl:if>
						<fileset dir="{$project_home}/apis/{$api}/test" includes="**/*.jar" />
					</classpath>
				</javac>
				<mkdir dir="{$project_home}/build/testresults/xml" />
				<junit fork="true" showoutput="true" dir="{$project_home}" printsummary="true" failureproperty="tests.failed">
					<sysproperty key="user.dir" value="{$project_home}" />
					<sysproperty key="test.environment" value="${{test.environment}}" />
					<sysproperty key="test.start.server" value="${{test.start.server}}" />
					<sysproperty key="org.xins.server.config" value="${{org.xins.server.config}}" />
					<sysproperty key="servlet.port" value="${{servlet.port}}" />
					<!--sysproperty key="net.sourceforge.cobertura.datafile"	file="{$project_home}/build/coverage/{$api}/cobertura.ser" /-->
          <formatter usefile="false" type="brief"/>
					<formatter type="xml" />
					<test name="{$packageTests}.APITests" todir="{$project_home}/build/testresults/xml" outfile="testresults-{$api}"/>
					<classpath>
						<path refid="xins.classpath" />
						<pathelement path="{$project_home}/build/capis/{$api}-capi.jar" />
						<pathelement path="{$project_home}/build/classes-tests/{$api}" />
						<pathelement path="${{classes.api.dir}}" />
						<xsl:if test="$apiHasTypes">
							<pathelement path="{$project_home}/build/classes-types/{$api}" />
						</xsl:if>
						<xsl:if test="impl">
							<xsl:variable name="impl_file" select="concat($project_home, '/apis/', $api, '/impl/impl.xml')" />
							<xsl:apply-templates select="document($impl_file)/impl/dependency" />
						</xsl:if>
						<fileset dir="{$project_home}/apis/{$api}/test" includes="**/*.jar" />
					</classpath>
				</junit>
				<mkdir dir="{$project_home}/build/testresults/html" />
				<xslt
				in="{$project_home}/build/testresults/xml/testresults-{$api}.xml"
				out="{$project_home}/build/testresults/html/testresults-{$api}.html"
				style="{$xins_home}/src/xslt/tests/index.xslt" />
				<copy
				file="{$xins_home}/src/css/tests/stylesheet.css"
				todir="{$project_home}/build/testresults/html" />
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
		</xsl:if>

		<target name="-stubs-capi-{$api}" depends="-prepare-classes" >
			<mkdir dir="{$project_home}/build/java-capi/{$api}/{$clientPackageAsDir}" />
			<xmlvalidate file="{$api_file}" warn="false">
				<xmlcatalog refid="all-dtds" />
			</xmlvalidate>
			<xslt
			in="{$api_file}"
			out="{$project_home}/build/java-capi/{$api}/{$clientPackageAsDir}/CAPI.java"
			style="{$xins_home}/src/xslt/java-capi/api_to_java.xslt">
				<xmlcatalog refid="all-dtds" />
				<param name="project_file" expression="{$project_file}"  />
				<param name="specsdir"     expression="{$api_specsdir}"  />
				<param name="package"      expression="{$clientPackage}" />
				<param name="api"          expression="{$api}"           />
				<param name="xins_version" expression="{$xins_version}"  />
			</xslt>
			<xslt
			in="{$api_file}"
			out="{$project_home}/build/java-capi/{$api}/{$clientPackageAsDir}/package.html"
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
				destdir="{$project_home}/build/java-capi/{$api}/{$clientPackageAsDir}"
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
				destdir="{$project_home}/build/java-capi/{$api}/{$clientPackageAsDir}"
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
				destdir="{$project_home}/build/java-capi/{$api}/{$clientPackageAsDir}"
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
			<mkdir dir="{$project_home}/build/classes-capi/{$api}" />
			<javac
			srcdir="{$project_home}/build/java-capi/{$api}/"
			destdir="{$project_home}/build/classes-capi/{$api}"
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
			manifest="{$project_home}/build/capis/{$api}-MANIFEST.MF">
				<fileset dir="{$project_home}/build/classes-capi/{$api}" includes="**/*.class" />
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
			</jar>
		</target>
		<target name="capi-{$api}" depends="jar-{$api}" />

		<target name="javadoc-capi-{$api}" description="Generates Javadoc API docs for the client-side '{$api}' API stubs">
			<xsl:attribute name="depends">
				<xsl:text>-prepare-classes,</xsl:text>
				<xsl:if test="$apiHasTypes">
					<xsl:text>-classes-types-</xsl:text>
					<xsl:value-of select="$api" />
					<xsl:text>,</xsl:text>
				</xsl:if>
				<xsl:text>-stubs-capi-</xsl:text>
				<xsl:value-of select="$api" />
			</xsl:attribute>
			<mkdir dir="{$project_home}/build/javadoc-capi/{$api}" />
			<javadoc
			sourcepath="{$project_home}/build/java-capi/{$api}"
			destdir="{$project_home}/build/javadoc-capi/{$api}"
			version="yes"
			use="yes"
			author="yes"
			access="public"
			windowtitle="Call interface for {$api} API"
			doctitle="Call interface for {$api} API">
				<packageset dir="{$project_home}/build/java-capi/{$api}" />
				<xsl:if test="$apiHasTypes">
					<packageset dir="{$project_home}/build/java-types/{$api}" />
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
				<classpath>
					<path refid="xins.classpath" />
				</classpath>
			</javadoc>
			<copy
			file="{$xins_home}/src/css/javadoc/style.css"
			tofile="{$project_home}/build/javadoc-capi/{$api}/stylesheet.css"
			overwrite="true" />
		</target>

		<target name="client-{$api}"
						depends="jar-{$api}, javadoc-capi-{$api}, specdocs-{$api}, wsdl-{$api}, opendoc-{$api}"
						description="Generates the Javadoc API docs for the client side and the client JAR file for the '{$api}' API stubs and zip the result.">
			<zip destfile="{$builddir}/specdocs/{$api}/{$api}-client.zip">
				<fileset dir="{$builddir}/capis" includes="{$api}-capi.jar" />
				<zipfileset dir="{$builddir}/javadoc-capi/{$api}" prefix="javadoc" />
				<zipfileset dir="{$builddir}/java-capi/{$api}" prefix="java" />
				<zipfileset dir="{$builddir}/specdocs/{$api}" excludes="{$api}-client.zip" prefix="specdocs" />
				<fileset dir="{$builddir}/opendoc/{$api}" includes="{$api}-specs.odt" />
				<fileset dir="{$builddir}/wsdl" includes="{$api}.wsdl" />
			</zip>
		</target>

		<target name="all-{$api}"
						description="Generates everything for the '{$api}' API stubs.">
			<xsl:attribute name="depends">
				<xsl:text>client-</xsl:text>
				<xsl:value-of select="$api" />
				<xsl:if test="$api_node/impl-java or impl">
					<xsl:text>, server-</xsl:text>
					<xsl:value-of select="$api" />
				</xsl:if>
			</xsl:attribute>
		</target>

		<target name="clean-{$api}" description="Deletes everything for the '{$api}' API stubs.">
			<delete dir="{$project_home}/build/capis/{$api}-capi.jar" />
			<delete dir="{$project_home}/build/classes-api/{$api}" />
			<delete dir="{$project_home}/build/classes-capi/{$api}" />
			<delete dir="{$project_home}/build/classes-types/{$api}" />
			<delete dir="{$project_home}/build/classes-tests/{$api}" />
			<delete dir="{$project_home}/build/java-capi/{$api}" />
			<delete dir="{$project_home}/build/java-combined/{$api}" />
			<delete dir="{$project_home}/build/java-fundament/{$api}" />
			<delete dir="{$project_home}/build/java-types/{$api}" />
			<delete dir="{$project_home}/build/javadoc-api/{$api}" />
			<delete dir="{$project_home}/build/javadoc-capi/{$api}" />
			<delete dir="{$project_home}/build/logdoc/{$api}" />
			<delete dir="{$project_home}/build/specdocs/{$api}" />
			<delete dir="{$project_home}/build/types/{$api}" />
			<delete dir="{$project_home}/build/webapps/{$api}" />
			<xsl:for-each select="impl/@name">
				<xsl:variable name="impl" select="." />
				<delete dir="{$project_home}/build/classes-api/{$api}-{$impl}" />
				<delete dir="{$project_home}/build/java-fundament/{$api}-{$impl}" />
				<delete dir="{$project_home}/build/javadoc-api/{$api}-{$impl}" />
				<delete dir="{$project_home}/build/logdoc/{$api}-{$impl}" />
				<delete dir="{$project_home}/build/webapps/{$api}-{$impl}" />
				<delete dir="{$project_home}/build/logdoc/{$api}-{$impl}" />
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
