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
	
	<xsl:variable name="project_file" select="concat($project_home, '/xins-project.xml')" />
	<xsl:variable name="xins_jar"     select="concat($xins_home,    '/build/xins.jar')" />
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
	<xsl:variable name="javaImplDir">
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
				
				<target name="specdocs-api-{$api}" depends="-prepare-specdocs" description="Generates all specification docs for the '{$api}' API">
					<dependset>
						<srcfilelist   dir="{$specsdir}/{$api}"    files="*.fnc"         />
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
						basedir="{$specsdir}"
						destdir="{$project_home}/build/specdocs"
						style="{$xins_home}/src/xslt/specdocs/function_to_html.xslt"
						includes="{$api}/*.fnc">
						<param name="project_home" expression="{$project_home}" />
						<param name="specsdir"     expression="{$specsdir}"     />
					</style>
					<style
						basedir="{$specsdir}"
						destdir="{$project_home}/build/specdocs"
						style="{$xins_home}/src/xslt/specdocs/type_to_html.xslt"
						includes="{$api}/*.typ">
						<param name="project_home" expression="{$project_home}" />
						<param name="specsdir"     expression="{$specsdir}"     />
					</style>
					<xsl:for-each select="document($api_file)/api/environment">
						<style
							basedir="{$specsdir}"
							destdir="{$project_home}/build/specdocs"
							style="{$xins_home}/src/xslt/testforms/function_to_html.xslt"
							includes="{$api}/*.fnc"
							extension="-testform-{@id}.html">
							<param name="project_home" expression="{$project_home}" />
							<param name="specsdir"     expression="{$specsdir}"     />
							<param name="environment"  expression="{@id}"           />
						</style>
					</xsl:for-each>
				</target>
				
				<xsl:if test="document($api_file)/api/impl-java">
					<xsl:variable name="package">
						<xsl:call-template name="package_for_api">
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
					<xsl:variable name="javaDestDir"    select="concat($project_home, '/build/java-fundament/', $api)" />
					<xsl:variable name="classesDestDir" select="concat($project_home, '/build/classes/', $api)"        />
					<xsl:variable name="javaCombinedDir" select="concat($project_home, '/build/java-combined/', $api)" />
					
					<target name="classes-api-{$api}" depends="-prepare-classes" description="Compiles the Java classes for the '{$api}' API">
						<mkdir dir="{$project_home}/build/java-fundament/{$api}/{$packageAsDir}" />
						<style
							in="{$api_file}"
							out="{$javaDestDir}/{$packageAsDir}/APIImpl.java"
							style="{$xins_home}/src/xslt/java-fundament/api_to_java.xslt">
							<param name="project_home" expression="{$project_home}" />
							<param name="specsdir"     expression="{$specsdir}"     />
							<param name="package"      expression="{$package}"      />
						</style>
						<!-- TODO: Include only functions mentioned in api.xml -->
						<style
							basedir="{$specsdir}/{$api}"
							destdir="{$javaDestDir}/{$packageAsDir}"
							style="{$xins_home}/src/xslt/java-fundament/function_to_java.xslt"
							includes="*.fnc"
							extension=".java">
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
							<fileset dir="{$javaImplDir}/{$api}" includes="**/*.java" />
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
								<pathelement path="{$xins_jar}" />
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
						<mkdir dir="build/webapps/{$api}" />
						<style
							in="{$specsdir}/{$api}/api.xml"
							out="build/webapps/{$api}/web.xml"
							style="{$xins_home}/src/xslt/webapp/api_to_webxml.xslt">
							<param name="project_home" expression="{$project_home}" />
						</style>
						<war
							webxml="build/webapps/{$api}/web.xml"
							destfile="build/webapps/{$api}/{$api}.war">
							<lib dir="{$xins_home}/build"                       includes="xins.jar" />
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
						</war>
					</target>
				</xsl:if>
				
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
