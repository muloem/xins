<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 XSLT that generates the build.xml used to compile the different APIs.

 $Id$
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

	<xsl:variable name="xmlenc_version"    select="'0.43'"                                          />
	<xsl:variable name="xins_buildfile"    select="concat($xins_home,    '/build.xml')"             />
	<xsl:variable name="project_file"      select="concat($project_home, '/xins-project.xml')"      />
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
		<xsl:text>/</xsl:text>
		<xsl:choose>
			<xsl:when test="document($project_file)/project/@dependenciesdir">
				<xsl:value-of select="document($project_file)/project/@dependenciesdir" />
			</xsl:when>
			<xsl:otherwise>depends</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

	<xsl:template match="project">
		<project default="help" basedir="..">

			<target name="clean" description="Removes all generated files">
				<delete dir="{$builddir}" />
			</target>

			<target name="version">
				<ant antfile="build.xml" dir="{$xins_home}" target="version"/>
			</target>

			<target name="help">
				<ant antfile="build.xml" dir="{$xins_home}" target="version"/>
				<echo message="" />
				<echo message="Possible targets:" />
				<echo message="" />
				<echo message="war-&lt;api&gt;           Creates the WAR for the API." />
				<echo message="specdocs-&lt;api&gt;      Generates all specification docs for the API." />
				<echo message="javadoc-api-&lt;api&gt;   Generates Javadoc API docs for the API." />
				<echo message="server-&lt;api&gt;        Generates the war file, the Javadoc API docs for the server side and the specdocs for the API." />
				<echo message="jar-&lt;api&gt;           Generates and compiles the Java classes for the client-side API." />
				<echo message="javadoc-capi-&lt;api&gt;  Generates Javadoc API docs for the client-side API." />
				<echo message="client-&lt;api&gt;        Generates the Javadoc API docs for the client side and the client jar file for the API." />
				<echo message="clean-&lt;api&gt;         Cleans everything for the API." />
				<echo message="rebuild-&lt;api&gt;       Regenerates everything for the API." />
				<echo message="all-&lt;api&gt;           Generates everything for the API." />
				<echo message="" />
				<echo message="all                 Generates everything." />
				<echo message="clean               Removes all generated files." />
				<echo message="specdocs            Generates all specification docs." />
				<echo message="wars                Creates the WARs for all APIs." />
				<echo message="" />
				<echo message="version             Prints the version of XINS." />
				<echo message="help                Prints this message." />
				<echo message="" />
				<echo message="create-api          Generates a new api specification file." />
				<echo message="create-function     Generates a new function specification file." />
				<echo message="create-rcd          Generates a new result code specification file." />
				<echo message="create-type         Generates a new type specification file." />
				<echo message="" />
				<echo message="Possible APIs:" />
				<echo message="" />
				<echo><xsl:for-each select="api">
						<xsl:text>"</xsl:text>
						<xsl:value-of select="@name" />
						<xsl:text>" </xsl:text>
					</xsl:for-each>
				</echo>
			</target>

			<xsl:call-template name="createproject">
				<xsl:with-param name="specsdir">
					<xsl:value-of select="$specsdir" />
				</xsl:with-param>
			</xsl:call-template>

			<target name="-prepare" />

			<target name="-prepare-specdocs" depends="-prepare, -load-dtds">
				<mkdir dir="{$builddir}/specdocs" />
			</target>

			<target name="-load-dtds">
				<xmlcatalog id="all-dtds">
					<dtd publicId="-//XINS//DTD XINS Project 1.0 alpha//EN"
					     location="{$xins_home}/src/dtd/xins-project_1_0_alpha.dtd" />
					<dtd publicId="-//XINS//DTD XINS API 1.0 alpha//EN"
					     location="{$xins_home}/src/dtd/api_1_0_alpha.dtd" />
					<dtd publicId="-//XINS//DTD Function 1.0 alpha//EN"
					     location="{$xins_home}/src/dtd/function_1_0_alpha.dtd" />
					<dtd publicId="-//XINS//DTD Type 1.0 alpha//EN"
					     location="{$xins_home}/src/dtd/type_1_0_alpha.dtd" />
					<dtd publicId="-//XINS//DTD Result Code 1.0 alpha//EN"
					     location="{$xins_home}/src/dtd/resultcode_1_0_alpha.dtd" />
					<dtd publicId="-//XINS//DTD XINS Project 1.0//EN"
					     location="{$xins_home}/src/dtd/xins-project_1_0.dtd" />
					<dtd publicId="-//XINS//DTD XINS API 1.0//EN"
					     location="{$xins_home}/src/dtd/api_1_0.dtd" />
					<dtd publicId="-//XINS//DTD Function 1.0//EN"
					     location="{$xins_home}/src/dtd/function_1_0.dtd" />
					<dtd publicId="-//XINS//DTD Type 1.0//EN"
					     location="{$xins_home}/src/dtd/type_1_0.dtd" />
					<dtd publicId="-//XINS//DTD Result Code 1.0//EN"
					     location="{$xins_home}/src/dtd/resultcode_1_0.dtd" />
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

			<xsl:for-each select="api">
				<xsl:variable name="api"      select="@name" />
				<!-- This test is not garanted to work with all XSLT processors. -->
				<xsl:variable name="old_api_file" select="concat($specsdir, '/', $api, '/api.xml')" />
				<xsl:variable name="api_specsdir">
					<xsl:choose>
						<xsl:when test="document($old_api_file)">
							<xsl:value-of select="concat($specsdir, '/', $api)" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="concat($project_home, '/apis/', $api, '/spec')" />
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
						<xsl:with-param name="project_file">
							<xsl:value-of select="$project_file" />
						</xsl:with-param>
						<xsl:with-param name="api">
							<xsl:value-of select="$api" />
						</xsl:with-param>
					</xsl:call-template>
				</xsl:variable>
				<xsl:variable name="clientPackageAsDir">
					<xsl:value-of select="translate($clientPackage, '.','/')" />
				</xsl:variable>
				<xsl:variable name="apiHasTypes">
					<xsl:choose>
						<xsl:when test="document($api_file)/api/type">true</xsl:when>
						<xsl:otherwise>false</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>

				<target name="specdocs-{$api}" depends="-prepare-specdocs" description="Generates all specification docs for the '{$api}' API">
					<dependset>
						<srcfilelist   dir="{$api_specsdir}"    files="{$functionIncludes}" />
						<srcfilelist   dir="{$api_specsdir}"    files="*.typ"         />
						<targetfileset dir="{$project_home}/build/specdocs/{$api}" includes="index.html" />
					</dependset>
					<dependset>
						<srcfilelist   dir="{$api_specsdir}"    files="api.xml" />
						<targetfileset dir="{$project_home}/build/specdocs/{$api}" includes="*.html" />
					</dependset>
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
					<xsl:if test="document($project_file)/project/api[@name = $api]/environments">
						<xsl:variable name="env_file" select="concat($project_home, '/apis/', $api, '/environments.xml')" />
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
				</target>

				<xsl:if test="$apiHasTypes = 'true'">
					<target name="-classes-types-{$api}" depends="-prepare-classes">
						<xsl:variable name="package">
							<xsl:call-template name="package_for_type_classes">
								<xsl:with-param name="project_file">
									<xsl:value-of select="$project_file" />
								</xsl:with-param>
								<xsl:with-param name="api">
									<xsl:value-of select="$api" />
								</xsl:with-param>
							</xsl:call-template>
						</xsl:variable>
						<xsl:variable name="packageAsDir">
							<xsl:value-of select="translate($package, '.','/')" />
						</xsl:variable>
						<xsl:variable name="javaDestDir"    select="concat($project_home, '/build/java-types/', $api)" />
						<xsl:variable name="copiedTypesDir" select="concat($project_home, '/build/types/',      $api)" />

						<xsl:for-each select="document($api_file)/api/type">
							<xsl:variable name="type" select="@name" />
							<xsl:variable name="classname">
								<xsl:call-template name="hungarianUpper">
									<xsl:with-param name="text">
										<xsl:value-of select="$type" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:variable>
							<copy
							file="{$api_specsdir}/{$type}.typ"
							tofile="{$copiedTypesDir}/{$classname}.typ" />
						</xsl:for-each>

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
						deprecation="${{deprecated}}">
							<classpath>
								<pathelement path="{$xins-common.jar}" />
								<fileset dir="{$xins_home}/depends/compile"             includes="**/*.jar" />
								<fileset dir="{$xins_home}/depends/compile_and_runtime" includes="**/*.jar" />
								<xsl:apply-templates select="document($api_file)/api/impl-java/dependency[not(@type) or @type='compile' or @type='compile_and_runtime']" />
							</classpath>
						</javac>
					</target>
				</xsl:if>

				<xsl:if test="document($api_file)/api/impl-java or document($project_file)/project/api[@name = $api]/impl">
					<xsl:variable name="package">
						<xsl:call-template name="package_for_server_api">
							<xsl:with-param name="project_file">
								<xsl:value-of select="$project_file" />
							</xsl:with-param>
							<xsl:with-param name="api">
								<xsl:value-of select="$api" />
							</xsl:with-param>
						</xsl:call-template>
					</xsl:variable>
					<xsl:variable name="packageAsDir">
						<xsl:value-of select="translate($package, '.','/')" />
					</xsl:variable>
					<!-- This test is not garanted to work with all XSLT processors. -->
					<xsl:variable name="javaImplDir">
						<xsl:value-of select="$project_home" />
						<xsl:text>/</xsl:text>
						<xsl:choose>
							<xsl:when test="document($old_api_file)">
								<xsl:choose>
									<xsl:when test="document($project_file)/project/@javadir">
										<xsl:value-of select="document($project_file)/project/@javadir" />
									</xsl:when>
									<xsl:otherwise>src/impl-java</xsl:otherwise>
								</xsl:choose>
								<xsl:text>/</xsl:text>
								<xsl:value-of select="$api" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="concat('apis/', $api, '/impl')" />
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<!-- xsl:variable name="javaImplDir"     select="concat($javaImplBaseDir, '/',                      $api)" /-->
					<xsl:variable name="javaDestDir"     select="concat($project_home,    '/build/java-fundament/', $api)" />
					<xsl:variable name="classesDestDir"  select="concat($project_home,    '/build/classes-api/',    $api)" />
					<xsl:variable name="javaCombinedDir" select="concat($project_home,    '/build/java-combined/',  $api)" />
					<!-- This test is not garanted to work with all XSLT processors. -->
					<xsl:variable name="logdoc_dir">
						<xsl:choose>
							<xsl:when test="document($old_api_file)">
								<xsl:value-of select="concat($project_home, '/src/logdoc/', $api)" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="$javaImplDir" />
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<xsl:variable name="javaDestFileDir" select="concat($javaDestDir, '/', $packageAsDir)" />

					<target name="-classes-logdoc-{$api}" depends="-prepare-classes" if="logdoc.available.{$api}">
						<echo message="Generating the logdoc for {$api}" />
						<mkdir dir="build/logdoc/{$api}" />
						<style
						in="{$logdoc_dir}/log.xml"
						out="build/logdoc/{$api}/build.xml"
						style="{$xins_home}/src/xslt/logdoc/log_to_build.xslt">
							<xmlcatalog refid="all-dtds" />
							<param name="xins_home"       expression="{$xins_home}" />
							<param name="logdoc_xslt_dir" expression="{$xins_home}/src/xslt/logdoc" />
							<param name="sourcedir"       expression="{$logdoc_dir}" />
							<param name="html_destdir"    expression="html" />
							<param name="java_destdir"    expression="{$javaDestFileDir}" />
							<param name="package_name"    expression="{$package}" />
						</style>
						<ant antfile="build/logdoc/{$api}/build.xml" target="java" />
					</target>

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
							<xsl:if test="$apiHasTypes = 'true'">
								<xsl:text>-classes-types-</xsl:text>
								<xsl:value-of select="$api" />
								<xsl:text>,</xsl:text>
							</xsl:if>
							<xsl:text>-skeletons-impl-</xsl:text>
							<xsl:value-of select="$api" />
						</xsl:attribute>
						<mkdir dir="{$project_home}/build/java-fundament/{$api}/{$packageAsDir}" />
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
						<available property="logdoc.available.{$api}" file="{$logdoc_dir}/log.xml" />
						<antcall target="-classes-logdoc-{$api}" />

						<!-- Copy all .java files to a single directory -->
						<mkdir dir="{$javaCombinedDir}" />
						<copy todir="{$javaCombinedDir}">
							<fileset dir="{$javaImplDir}" includes="**/*.java" />
						</copy>
						<copy todir="{$javaCombinedDir}" overwrite="true">
							<fileset dir="{$javaDestDir}" includes="**/*.java" />
						</copy>

						<!-- Compile all classes -->
						<mkdir dir="{$classesDestDir}" />
						<!-- If not set by the user set it to true. -->
						<property name="deprecated" value="true" />
						<javac
						srcdir="{$javaCombinedDir}"
						destdir="{$classesDestDir}"
						debug="true"
						deprecation="${{deprecated}}">
							<classpath>
								<xsl:if test="$apiHasTypes = 'true'">
									<pathelement path="{$typeClassesDir}" />
								</xsl:if>
								<pathelement path="{$xins-common.jar}" />
								<pathelement path="{$xins-server.jar}" />
								<fileset dir="{$xins_home}/depends/compile"             includes="**/*.jar" />
								<fileset dir="{$xins_home}/depends/compile_and_runtime" includes="**/*.jar" />
								<xsl:apply-templates select="document($api_file)/api/impl-java/dependency[not(@type) or @type='compile' or @type='compile_and_runtime']" />
							</classpath>
						</javac>
					</target>

					<target name="war-{$api}" depends="classes-api-{$api}" description="Creates the WAR for the '{$api}' API">
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
							<param name="xins_version" expression="{$xins_version}" />
							<param name="project_home" expression="{$project_home}" />
							<param name="project_file" expression="{$project_file}" />
							<param name="api"          expression="{$api}"          />
							<param name="api_file"     expression="{$api_file}"     />
							<param name="hostname"     expression="${{hostname}}"   />
							<param name="timestamp"    expression="${{timestamp}}"  />
						</style>
						<war
							webxml="build/webapps/{$api}/web.xml"
							destfile="build/webapps/{$api}/{$api}.war">
							<lib dir="{$xins_home}/build"                       includes="xins-common.jar" />
							<lib dir="{$xins_home}/build"                       includes="xins-server.jar" />
							<lib dir="{$xins_home}/depends/compile_and_runtime" includes="**/*.jar" />
							<lib dir="{$xins_home}/depends/runtime"             includes="**/*.jar" />
							<xsl:apply-templates select="document($api_file)/api/impl-java/dependency[not(@type) or @type='runtime' or @type='compile_and_runtime']" mode="lib" />
							<classes dir="{$classesDestDir}" includes="**/*.class" />
							<xsl:if test="$apiHasTypes = 'true'">
								<classes dir="{$typeClassesDir}" includes="**/*.class" />
							</xsl:if>
							<classes dir="{$javaImplDir}" excludes="**/*.java" />
						</war>
						<checksum file="build/webapps/{$api}/{$api}.war" property="war.md5"/>
						<echo message="MD5: ${{war.md5}}" />
						<echo message="Build time: ${{timestamp}}" />
					</target>

					<target name="javadoc-api-{$api}" depends="classes-api-{$api}" description="Generates Javadoc API docs for the '{$api}' API">
						<property file="{$xins_home}/.version.properties" />
						<mkdir dir="build/javadoc-api/{$api}" />
						<javadoc
						sourcepath="build/java-combined/{$api}"
						destdir="build/javadoc-api/{$api}"
						version="yes"
						use="yes"
						author="yes"
						private="no"
						windowtitle="Implementation of {$api} API"
						doctitle="Implementation of {$api} API">
							<packageset dir="build/java-combined/{$api}" />
							<xsl:if test="$apiHasTypes = 'true'">
								<packageset dir="build/java-types/{$api}" />
							</xsl:if>
							<link
							href="http://xins.sourceforge.net/javadoc/${{version.prev.major}}.${{version.prev.minor}}/"
							offline="true"
							packagelistloc="{$xins_home}/build/javadoc/" />
							<link
							href="http://java.sun.com/j2se/1.3/docs/api"
							offline="true"
							packagelistloc="{$xins_home}/src/package-lists/j2se/" />
							<link
							href="http://jakarta.apache.org/log4j/docs/api/"
							offline="true"
							packagelistloc="{$xins_home}/src/package-lists/log4j/" />
							<link
							href="http://www.jdom.org/docs/apidocs/"
							offline="true"
							packagelistloc="{$xins_home}/src/package-lists/jdom/" />
							<link
							href="http://xmlenc.sourceforge.net/javadoc/{$xmlenc_version}/"
							offline="true"
							packagelistloc="{$xins_home}/src/package-lists/xmlenc/" />
							<link
							href="http://nagoya.apache.org/gump/javadoc/ant/build/javadocs/"
							offline="true"
							packagelistloc="{$xins_home}/src/package-lists/ant/" />
							<classpath>
								<pathelement location="{$xins_home}/build/xins-common.jar" />
								<pathelement location="{$xins_home}/build/xins-server.jar" />
								<pathelement location="{$xins_home}/depends/compile_and_runtime/jdom.jar" />
								<pathelement location="{$xins_home}/depends/compile_and_runtime/log4j.jar" />
								<pathelement location="{$xins_home}/depends/compile_and_runtime/commons-logging.jar" />
								<pathelement location="{$xins_home}/depends/compile_and_runtime/xmlenc.jar" />
								<fileset dir="${{ant.home}}/lib" includes="**/*.jar" />
								<xsl:apply-templates select="document($api_file)/api/impl-java/dependency[not(@type) or @type='compile' or @type='compile_and_runtime']" />
							</classpath>
						</javadoc>
						<copy
						file="{$xins_home}/src/css/javadoc/style.css"
						tofile="build/javadoc-api/{$api}/stylesheet.css"
						overwrite="true" />
					</target>

					<target name="server-{$api}"
					        depends="war-{$api}, specdocs-{$api}, javadoc-api-{$api}"
					        description="Generates the war file, the Javadoc API docs for the server side and the specdocs for the '{$api}' API stubs">
					</target>
				</xsl:if>

				<target name="-stubs-capi-{$api}" depends="-prepare-classes" >
					<xsl:variable name="functionResultIncludes">
						<xsl:for-each select="document($api_file)/api/function">
							<xsl:variable name="functionName" select="@name" />
							<xsl:variable name="functionFile" select="concat($api_specsdir, '/', $functionName, '.fnc')" />
							<xsl:for-each select="document($functionFile)/function">
								<xsl:choose>
									<xsl:when test="(output/param and output/data/element) or count(output/param) &gt; 1">
										<xsl:value-of select="$functionName" />
										<xsl:text>.fnc,</xsl:text>
									</xsl:when>
								</xsl:choose>
							</xsl:for-each>
						</xsl:for-each>
					</xsl:variable>

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
					<xsl:if test="string-length($functionResultIncludes) &gt; 0">
						<xmlvalidate warn="false">
							<fileset dir="{$api_specsdir}" includes="{$functionResultIncludes}"/>
							<xmlcatalog refid="all-dtds" />
						</xmlvalidate>
						<style
						basedir="{$api_specsdir}"
						destdir="{$project_home}/build/java-capi/{$api}/{$clientPackageAsDir}"
						style="{$xins_home}/src/xslt/java-capi/function_to_java.xslt"
						extension="Result.java"
						includes="{$functionResultIncludes}">
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
						<xsl:if test="$apiHasTypes = 'true'">
							<xsl:text>-classes-types-</xsl:text>
							<xsl:value-of select="$api" />
							<xsl:text>,</xsl:text>
						</xsl:if>
						<xsl:text>-stubs-capi-</xsl:text>
						<xsl:value-of select="$api" />
					</xsl:attribute>
					<mkdir dir="{$project_home}/build/classes-capi/{$api}" />
					<!-- If not set by the user set it to true. -->
					<property name="deprecated" value="true" />
					<javac
					srcdir="{$project_home}/build/java-capi/{$api}/"
					destdir="{$project_home}/build/classes-capi/{$api}"
					debug="true"
					deprecation="${{deprecated}}">
						<classpath>
							<pathelement path="{$xins-common.jar}" />
							<pathelement path="{$xins-client.jar}" />
							<xsl:if test="$apiHasTypes = 'true'">
								<pathelement path="{$typeClassesDir}"  />
							</xsl:if>
							<fileset dir="{$xins_home}/depends/compile"             includes="**/*.jar" />
							<fileset dir="{$xins_home}/depends/compile_and_runtime" includes="**/*.jar" />
						</classpath>
					</javac>
					<xsl:if test="$apiHasTypes = 'true'">
						<copy todir="{$project_home}/build/classes-capi/{$api}">
							<fileset dir="{$typeClassesDir}" includes="**/*.class" />
						</copy>
					</xsl:if>
					<mkdir dir="{$project_home}/build/capis/" />
					<jar
					destfile="{$project_home}/build/capis/{$api}-capi.jar"
					basedir="{$project_home}/build/classes-capi/{$api}" />
				</target>

				<target name="javadoc-capi-{$api}" description="Generates Javadoc API docs for the client-side '{$api}' API stubs">
					<xsl:attribute name="depends">
						<xsl:if test="$apiHasTypes = 'true'">
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
					private="no"
					windowtitle="Call interface for {$api} API"
					doctitle="Call interface for {$api} API">
						<packageset dir="build/java-capi/{$api}" />
						<xsl:if test="$apiHasTypes = 'true'">
							<packageset dir="build/java-types/{$api}" />
						</xsl:if>
						<link
						href="http://xins.sourceforge.net/javadoc/{$xins_version}/"
						offline="true"
						packagelistloc="{$xins_home}/build/javadoc/" />
						<link
						href="http://java.sun.com/j2se/1.3/docs/api"
						offline="true"
						packagelistloc="{$xins_home}/src/package-lists/j2se/" />
						<link
						href="http://jakarta.apache.org/log4j/docs/api/"
						offline="true"
						packagelistloc="{$xins_home}/src/package-lists/log4j/" />
						<link
						href="http://www.jdom.org/docs/apidocs/"
						offline="true"
						packagelistloc="{$xins_home}/src/package-lists/jdom/" />
						<link
						href="http://xmlenc.sourceforge.net/javadoc/{$xmlenc_version}/"
						offline="true"
						packagelistloc="{$xins_home}/src/package-lists/xmlenc/" />
						<link
						href="http://nagoya.apache.org/gump/javadoc/ant/build/javadocs/"
						offline="true"
						packagelistloc="{$xins_home}/src/package-lists/ant/" />
						<classpath>
							<pathelement location="{$xins_home}/build/xins-common.jar" />
							<pathelement location="{$xins_home}/build/xins-client.jar" />
							<pathelement location="{$xins_home}/depends/compile_and_runtime/jdom.jar" />
							<pathelement location="{$xins_home}/depends/compile_and_runtime/log4j.jar" />
							<pathelement location="{$xins_home}/depends/compile_and_runtime/commons-logging.jar" />
							<pathelement location="{$xins_home}/depends/compile_and_runtime/xmlenc.jar" />
							<fileset dir="${{ant.home}}/lib" includes="**/*.jar" />
						</classpath>
					</javadoc>
					<copy
					file="{$xins_home}/src/css/javadoc/style.css"
					tofile="build/javadoc-capi/{$api}/stylesheet.css"
					overwrite="true" />
				</target>

				<target name="client-{$api}"
				        depends="jar-{$api}, javadoc-capi-{$api}"
				        description="Generates the Javadoc API docs for the client side and the client jar file for the '{$api}' API stubs">
				</target>

				<target name="all-{$api}"
				        description="Generates everything for the '{$api}' API stubs.">
					<xsl:attribute name="depends">
						<xsl:if test="document($api_file)/api/impl-java">
							<xsl:text>server-</xsl:text>
							<xsl:value-of select="$api" />
							<xsl:text>,</xsl:text>
						</xsl:if>
						<xsl:text>client-</xsl:text>
						<xsl:value-of select="$api" />
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
			</xsl:for-each>

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
				<!-- If not set by the user set it to true. -->
				<property name="deprecated" value="true" />
				<mkdir dir="build/classes" />
			</target>

			<target name="classes" description="Compiles all Java classes">
				<xsl:attribute name="depends">
					<xsl:for-each select="api[document($project_file)/project/api/impl]">
						<xsl:if test="position() &gt; 1">,</xsl:if>
						<xsl:text>classes-api-</xsl:text>
						<xsl:value-of select="@name" />
					</xsl:for-each>
					<xsl:for-each select="api[document(concat($specsdir, '/', @name, '/api.xml'))/api/impl-java]">
						<xsl:if test="position() &gt; 1 or count(document($project_file)/project/api/impl) &gt; 1">,</xsl:if>
						<xsl:text>classes-api-</xsl:text>
						<xsl:value-of select="@name" />
					</xsl:for-each>
				</xsl:attribute>
			</target>

			<target name="wars" description="Creates the WARs for all APIs">
				<xsl:attribute name="depends">
					<xsl:for-each select="api[document($project_file)/project/api/impl]">
						<xsl:if test="position() &gt; 1">,</xsl:if>
						<xsl:text>war-</xsl:text>
						<xsl:value-of select="@name" />
					</xsl:for-each>
					<xsl:for-each select="api[document(concat($specsdir, '/', @name, '/api.xml'))/api/impl-java]">
						<xsl:if test="position() &gt; 1 or count(document($project_file)/project/api/impl) &gt; 1">,</xsl:if>
						<xsl:text>war-</xsl:text>
						<xsl:value-of select="@name" />
					</xsl:for-each>
				</xsl:attribute>
			</target>

			<target name="all" depends="specdocs,wars" description="Generates everything" />
		</project>
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