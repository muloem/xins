<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

$Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
	<xsl:include href="hungarian.xslt"       />
	<xsl:include href="package_to_dir.xslt"  />
	<xsl:include href="package_for_api.xslt" />
	
	<xsl:output indent="yes" />
	
	<xsl:param name="xins_home"    />
	<xsl:param name="project_home" />
	<xsl:param name="builddir"     />
	
	<xsl:variable name="xins_buildfile"    select="concat($xins_home, '/build.xml')" />
	<xsl:variable name="xins_majorversion" select="document($xins_buildfile)/project/target[@name='-init']/property[@name='version.major']/@value" />
	<xsl:variable name="xins_minorversion" select="document($xins_buildfile)/project/target[@name='-init']/property[@name='version.minor']/@value" />
	<xsl:variable name="xins_version"      select="concat($xins_majorversion, '.', $xins_minorversion)" />
	<xsl:variable name="project_file"      select="concat($project_home, '/xins-project.xml')" />
	<xsl:variable name="xins-common.jar"   select="concat($xins_home, '/build/xins-common.jar')" />
	<xsl:variable name="xins-server.jar"   select="concat($xins_home, '/build/xins-server.jar')" />
	<xsl:variable name="xins-client.jar"   select="concat($xins_home, '/build/xins-client.jar')" />
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
	<xsl:variable name="javaImplBaseDir">
		<xsl:value-of select="$project_home" />
		<xsl:text>/</xsl:text>
		<xsl:choose>
			<xsl:when test="document($project_file)/project/@javadir">
				<xsl:value-of select="document($project_file)/project/@javadir" />
			</xsl:when>
			<xsl:otherwise>src/impl-java</xsl:otherwise>
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
		<project default="all" basedir="..">
			
			<target name="clean" description="Removes all generated files">
				<delete dir="{$builddir}" />
			</target>
			
			<target name="-prepare" />
			
			<target name="-prepare-specdocs" depends="-prepare">
				<mkdir dir="{$builddir}/specdocs" />
				<copy
					todir="{$builddir}/specdocs"
				file="{$xins_home}/src/css/specdocs/style.css" />
			</target>
			
			<target name="specdocs-index" depends="-prepare-specdocs" description="Generates the API index">
				<style
					in="{$project_file}"
					out="{$builddir}/specdocs/index.html"
					style="{$xins_home}/src/xslt/specdocs/xins-project_to_index.xslt">
					<param name="project_home" expression="{$project_home}" />
					<param name="specsdir"     expression="{$specsdir}"       />
				</style>
			</target>
			
			<xsl:for-each select="api">
				<xsl:variable name="api"      select="@name" />
				<xsl:variable name="api_file" select="concat($specsdir, '/', $api, '/api.xml')" />
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
					<xsl:call-template name="package2dir">
						<xsl:with-param name="package">
							<xsl:value-of select="$clientPackage" />
						</xsl:with-param>
					</xsl:call-template>
				</xsl:variable>
				
				<target name="specdocs-api-{$api}" depends="-prepare-specdocs" description="Generates all specification docs for the '{$api}' API">
					<dependset>
						<srcfilelist   dir="{$specsdir}/{$api}"    files="{$functionIncludes}" />
						<srcfilelist   dir="{$specsdir}/{$api}"    files="*.typ"         />
						<targetfileset dir="{$project_home}/build/specdocs/{$api}" includes="index.html" />
					</dependset>
					<style
						in="{$specsdir}/{$api}/api.xml"
						out="{$project_home}/build/specdocs/{$api}/index.html"
						style="{$xins_home}/src/xslt/specdocs/api_to_html.xslt">
						<param name="project_home" expression="{$project_home}" />
						<param name="specsdir"     expression="{$specsdir}"     />
					</style>
					<style
						basedir="{$specsdir}/{$api}"
						destdir="{$project_home}/build/specdocs/{$api}"
						style="{$xins_home}/src/xslt/specdocs/function_to_html.xslt"
						includes="{$functionIncludes}">
						<param name="project_home" expression="{$project_home}" />
						<param name="specsdir"     expression="{$specsdir}"     />
					</style>
					<style
						basedir="{$specsdir}/{$api}"
						destdir="{$project_home}/build/specdocs/{$api}"
						style="{$xins_home}/src/xslt/specdocs/type_to_html.xslt"
						includes="{$typeIncludes}">
						<param name="project_home" expression="{$project_home}" />
						<param name="specsdir"     expression="{$specsdir}"     />
					</style>
					<style
						basedir="{$specsdir}/{$api}"
						destdir="{$project_home}/build/specdocs/{$api}"
						style="{$xins_home}/src/xslt/specdocs/resultcode_to_html.xslt"
						includes="{$resultcodeIncludes}">
						<param name="project_home" expression="{$project_home}" />
						<param name="specsdir"     expression="{$specsdir}"     />
					</style>
					<xsl:for-each select="document($api_file)/api/environment">
						<style
							basedir="{$specsdir}/{$api}"
							destdir="{$project_home}/build/specdocs/{$api}"
							style="{$xins_home}/src/xslt/testforms/function_to_html.xslt"
							includes="{$functionIncludes}"
							extension="-testform-{@id}.html">
							<param name="project_home" expression="{$project_home}" />
							<param name="specsdir"     expression="{$specsdir}"     />
							<param name="environment"  expression="{@id}"           />
						</style>
					</xsl:for-each>
				</target>

				<xsl:if test="document($api_file)/api/impl-java">
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
						<xsl:call-template name="package2dir">
							<xsl:with-param name="package">
								<xsl:value-of select="$package" />
							</xsl:with-param>
						</xsl:call-template>
					</xsl:variable>
					<xsl:variable name="javaImplDir"     select="concat($javaImplBaseDir, '/', $api)" />
					<xsl:variable name="javaDestDir"     select="concat($project_home, '/build/java-fundament/', $api)" />
					<xsl:variable name="classesDestDir"  select="concat($project_home, '/build/classes-api/', $api)"        />
					<xsl:variable name="javaCombinedDir" select="concat($project_home, '/build/java-combined/', $api)" />

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
							depends="-impl-{$api}-{$function}-unavail"
							unless="exists-{$api}-{$classname}">
							<style
								in="{$specsdir}/{$api}/{$function}.fnc"
								out="{$javaImplFile}"
								style="{$xins_home}/src/xslt/java-skeleton/function_to_java.xslt">
								<param name="specsdir"  expression="{$specsdir}"  />
								<param name="api"       expression="{$api}"       />
								<param name="package"   expression="{$package}"   />
								<param name="classname" expression="{$classname}" />
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

					<target name="classes-api-{$api}" depends="-prepare-classes,-skeletons-impl-{$api}" description="Compiles the Java classes for the '{$api}' API implementation">
						<mkdir dir="{$project_home}/build/java-fundament/{$api}/{$packageAsDir}" />
						<style
							in="{$api_file}"
							out="{$javaDestDir}/{$packageAsDir}/APIImpl.java"
							style="{$xins_home}/src/xslt/java-fundament/api_to_java.xslt">
							<param name="project_home" expression="{$project_home}" />
							<param name="specsdir"     expression="{$specsdir}"     />
							<param name="package"      expression="{$package}"      />
						</style>
						<style
							basedir="{$specsdir}/{$api}"
							destdir="{$javaDestDir}/{$packageAsDir}"
							style="{$xins_home}/src/xslt/java-fundament/function_to_java.xslt"
							extension=".java"
							includes="{$functionIncludes}">
							<param name="project_home" expression="{$project_home}" />
							<param name="specsdir"     expression="{$specsdir}"     />
							<param name="package"      expression="{$package}"      />
							<param name="api"          expression="{$api}"          />
							<param name="api_file"     expression="{$api_file}"     />
						</style>
						<xsl:for-each select="document($api_file)/api/type">
							<xsl:variable name="type" select="@name" />
							<xsl:variable name="classname">
								<xsl:call-template name="hungarianUpper">
									<xsl:with-param name="text">
										<xsl:value-of select="$type" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:variable>
							
							<style
								in="{$specsdir}/{$api}/{$type}.typ"
								out="{$javaDestDir}/{$packageAsDir}/{$classname}.java"
								style="{$xins_home}/src/xslt/java-fundament/type_to_java.xslt">
								<param name="project_home" expression="{$project_home}" />
								<param name="specsdir"     expression="{$specsdir}"     />
								<param name="package"      expression="{$package}"      />
								<param name="api"          expression="{$api}"          />
								<param name="api_file"     expression="{$api_file}"     />
							</style>
						</xsl:for-each>
						
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
						<javac
							srcdir="{$javaCombinedDir}"
							destdir="{$classesDestDir}"
							debug="true"
							deprecation="true">
							<classpath>
								<pathelement path="{$xins-common.jar}" />
								<pathelement path="{$xins-server.jar}" />
								<fileset dir="{$xins_home}/depends/compile"             includes="**/*.jar" />
								<fileset dir="{$xins_home}/depends/compile_and_runtime" includes="**/*.jar" />
								<xsl:for-each select="document($api_file)/api/impl-java/dependency[not(@type) or @type='compile' or @type='compile_and_runtime']">
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
								</xsl:for-each>
							</classpath>
						</javac>
					</target>
					
					<target name="war-api-{$api}" depends="classes-api-{$api}" description="Creates the WAR for the '{$api}' API">
						<xsl:if test="document($api_file)/api/impl-java/@deployment-required = 'true'">
							<condition property="deployment_required_and_set">
								<isset property="deployment" />
							</condition>
							<fail unless="deployment_required_and_set" message="Deployment setting is required. Please specify the deployment using 'ant -Ddeployment=&lt;identifier&gt;'" />
						</xsl:if>
						<mkdir dir="build/webapps/{$api}" />
						<taskdef name="hostname" classname="org.xins.util.ant.HostnameTask" classpath="{$xins_home}/build/xins-common.jar" />
						<tstamp>
							<format property="timestamp" pattern="yyyyMMdd_HHmm" />
						</tstamp>
						<hostname />
						<delete file="build/webapps/{$api}/web.xml" />
						<style
							in="{$specsdir}/{$api}/api.xml"
							out="build/webapps/{$api}/web.xml"
							style="{$xins_home}/src/xslt/webapp/api_to_webxml.xslt">
							<param name="project_home" expression="{$project_home}" />
							<param name="deployment"   expression="${{deployment}}" />
							<param name="hostname"     expression="${{hostname}}" />
							<param name="timestamp"    expression="${{timestamp}}" />
						</style>
						<war
							webxml="build/webapps/{$api}/web.xml"
							destfile="build/webapps/{$api}/{$api}.war">
							<lib dir="{$xins_home}/build"                       includes="xins-common.jar" />
							<lib dir="{$xins_home}/build"                       includes="xins-server.jar" />
							<lib dir="{$xins_home}/depends/compile_and_runtime" includes="**/*.jar" />
							<lib dir="{$xins_home}/depends/runtime"             includes="**/*.jar" />
							<xsl:for-each select="document($api_file)/api/impl-java/dependency[not(@type) or @type='runtime' or @type='compile_and_runtime']">
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
							</xsl:for-each>
							<classes dir="{$classesDestDir}" includes="**/*.class" />
							<classes dir="{$javaImplDir}"    excludes="**/*.java" />
						</war>
					</target>
					
					<target name="javadoc-api-{$api}" depends="-skeletons-impl-{$api}" description="Generates Javadoc API docs for the '{$api}' API">
						<mkdir dir="build/javadoc/{$api}" />
						<javadoc
						sourcepath="build/java-combined/{$api}"
						destdir="build/javadoc/{$api}"
						version="yes"
						use="yes"
						author="yes"
						private="no"
						windowtitle="TODO"
						doctitle="TODO"
						bottom="TODO">
							<packageset dir="build/java-combined/{$api}" />
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
							href="http://xmlenc.sourceforge.net/javadoc/0.34/"
							offline="true"
							packagelistloc="{$xins_home}/src/package-lists/xmlenc/" />
							<classpath>
								<pathelement location="{$xins_home}/build/xins-common.jar" />
								<pathelement location="{$xins_home}/build/xins-server.jar" />
								<pathelement location="{$xins_home}/depends/compile_and_runtime/log4j.jar" />
								<pathelement location="{$xins_home}/depends/compile_and_runtime/commons-logging.jar" />
								<pathelement location="{$xins_home}/depends/compile_and_runtime/xmlenc.jar" />
							</classpath>
						</javadoc>
						<copy
						file="{$xins_home}/src/css/javadoc/style.css"
						tofile="build/javadoc/{$api}/stylesheet.css"
						overwrite="true" />
					</target>
				</xsl:if>

				<target name="-stubs-capi-{$api}">
					<xsl:variable name="functionResultIncludes">
						<xsl:for-each select="document($api_file)/api/function">
							<xsl:variable name="functionName" select="@name" />
							<xsl:variable name="functionFile" select="concat($specsdir, '/', $api, '/', @name, '.fnc')" />
							<xsl:for-each select="document($functionFile)/function">
								<xsl:if test="output/param">
									<xsl:value-of select="@name" />
									<xsl:text>.fnc,</xsl:text>
								</xsl:if>
							</xsl:for-each>
						</xsl:for-each>
					</xsl:variable>

					<mkdir dir="{$project_home}/build/java-capi/{$api}/{$clientPackageAsDir}" />
					<style
					in="{$api_file}"
					out="{$project_home}/build/java-capi/{$api}/{$clientPackageAsDir}/API.java"
					style="{$xins_home}/src/xslt/java-capi/api_to_java.xslt">
						<param name="project_home" expression="{$project_home}" />
						<param name="specsdir"     expression="{$specsdir}"     />
						<param name="package"      expression="{$clientPackage}"      />
					</style>
					<style
					basedir="{$specsdir}/{$api}"
					destdir="{$project_home}/build/java-capi/{$api}/{$clientPackageAsDir}"
					style="{$xins_home}/src/xslt/java-capi/function_to_java.xslt"
					extension="Result.java"
					includes="{$functionResultIncludes}">
						<param name="project_home" expression="{$project_home}"  />
						<param name="specsdir"     expression="{$specsdir}"      />
						<param name="package"      expression="{$clientPackage}" />
						<param name="api"          expression="{$api}"           />
						<param name="api_file"     expression="{$api_file}"      />
					</style>
				</target>

				<target name="jar-capi-{$api}" depends="-prepare-classes,-stubs-capi-{$api}" description="Generates and compiles the Java classes for the client-side '{$api}' API stubs">
					<mkdir dir="{$project_home}/build/classes-capi/{$api}" />
					<javac
					srcdir="{$project_home}/build/java-capi/{$api}/"
					destdir="{$project_home}/build/classes-capi/{$api}"
					debug="true"
					deprecation="true">
						<classpath>
							<pathelement path="{$xins-common.jar}" />
							<pathelement path="{$xins-client.jar}" />
							<fileset dir="{$xins_home}/depends/compile"             includes="**/*.jar" />
							<fileset dir="{$xins_home}/depends/compile_and_runtime" includes="**/*.jar" />
						</classpath>
					</javac>
					<mkdir dir="{$project_home}/build/capis/" />
					<jar
					destfile="{$project_home}/build/capis/{$api}-capi.jar"
					basedir="{$project_home}/build/classes-capi/{$api}" />
				</target>
					
				<target name="javadoc-capi-{$api}" depends="-stubs-capi-{$api}" description="Generates Javadoc API docs for the client-side '{$api}' API stubs">
					<mkdir dir="build/javadoc-capi/{$api}" />
					<javadoc
					sourcepath="build/java-capi/{$api}"
					destdir="build/javadoc-capi/{$api}"
					version="yes"
					use="yes"
					author="yes"
					private="no"
					windowtitle="TODO"
					doctitle="TODO"
					bottom="TODO">
						<packageset dir="build/java-capi/{$api}" />
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
						href="http://xmlenc.sourceforge.net/javadoc/0.34/"
						offline="true"
						packagelistloc="{$xins_home}/src/package-lists/xmlenc/" />
						<classpath>
							<pathelement location="{$xins_home}/build/xins-common.jar" />
							<pathelement location="{$xins_home}/build/xins-client.jar" />
							<pathelement location="{$xins_home}/depends/compile_and_runtime/log4j.jar" />
							<pathelement location="{$xins_home}/depends/compile_and_runtime/commons-logging.jar" />
							<pathelement location="{$xins_home}/depends/compile_and_runtime/xmlenc.jar" />
						</classpath>
					</javadoc>
					<copy
					file="{$xins_home}/src/css/javadoc/style.css"
					tofile="build/javadoc-capi/{$api}/stylesheet.css"
					overwrite="true" />
				</target>
			</xsl:for-each>

			<target name="specdocs" description="Generates all specification docs">
				<xsl:attribute name="depends">
					<xsl:text>specdocs-index</xsl:text>
					<xsl:for-each select="api">
						<xsl:text>,specdocs-api-</xsl:text>
						<xsl:value-of select="@name" />
					</xsl:for-each>
				</xsl:attribute>
			</target>
			
			<target name="-prepare-classes" depends="-prepare">
				<mkdir dir="build/classes" />
			</target>
			
			<target name="classes" description="Compiles all Java classes">
				<xsl:attribute name="depends">
					<xsl:for-each select="api[document(concat($specsdir, '/', @name, '/api.xml'))/api/impl-java]">
						<xsl:if test="position() &gt; 1">,</xsl:if>
						<xsl:text>classes-api-</xsl:text>
						<xsl:value-of select="@name" />
					</xsl:for-each>
				</xsl:attribute>
			</target>
			
			<target name="wars" description="Creates the WARs for all APIs">
				<xsl:attribute name="depends">
					<xsl:for-each select="api[document(concat($specsdir, '/', @name, '/api.xml'))/api/impl-java]">
						<xsl:if test="position() &gt; 1">,</xsl:if>
						<xsl:text>war-api-</xsl:text>
						<xsl:value-of select="@name" />
					</xsl:for-each>
				</xsl:attribute>
			</target>
			
			<target name="all" depends="specdocs,wars" description="Generates everything" />
		</project>
	</xsl:template>
</xsl:stylesheet>
