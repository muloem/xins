<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:include href="package_to_dir.xslt" />

	<xsl:output indent="yes" />

	<xsl:param name="xins_home"    />
	<xsl:param name="project_home" />
	<xsl:param name="builddir"     />

	<xsl:variable name="specsdir">
		<xsl:choose>
			<xsl:when test="//project/@specsdir">
				<xsl:value-of select="//project/@specsdir" />
			</xsl:when>
			<xsl:otherwise>src/specs</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="project_file" select="concat($project_home, '/xins-project.xml')" />

	<xsl:template match="project">
		<project default="all" basedir="..">

			<target name="clean" description="Removes all generated files">
				<delete dir="build" />
			</target>

			<target name="-prepare" />

			<target name="-prepare-specdocs" depends="-prepare">
				<mkdir dir="build/specdocs" />
				<copy
				todir="build/specdocs"
				file="{$xins_home}/src/css/specdocs/style.css" />
			</target>

			<target name="specdocs-index" depends="-prepare-specdocs" description="Generates the API index">
				<style
				in="{$project_file}"
				out="build/specdocs/index.html"
				style="{$xins_home}/src/xslt/specdocs/xins-project_to_index.xslt">
					<param name="project_home" expression="{$project_home}" />
					<param name="specsdir"     expression="{$specsdir}"       />
				</style>
			</target>

			<xsl:for-each select="api">
				<xsl:variable name="api"      select="@name" />
				<xsl:variable name="api_file" select="concat($project_home, '/', $specsdir, '/', $api, '/api.xml')" />

				<target name="specdocs-api-{$api}" depends="-prepare-specdocs" description="Generates all specification docs for the '{$api}' API">
					<dependset>
						<srcfilelist   dir="{$project_home}/{$specsdir}/{$api}"    files="*.fnc" />
						<srcfilelist   dir="{$project_home}/{$specsdir}/{$api}"    files="*.typ" />
						<targetfileset dir="{$project_home}/build/specdocs/{$api}" includes="index.html" />
					</dependset>
					<style
					in="{$project_home}/{$specsdir}/{$api}/api.xml"
					out="{$project_home}/build/specdocs/{$api}/index.html"
					style="{$xins_home}/src/xslt/specdocs/api_to_html.xslt">
						<param name="project_home" expression="{$project_home}" />
						<param name="specsdir"     expression="{$specsdir}"       />
					</style>
					<style
					basedir="{$project_home}/{$specsdir}"
					destdir="{$project_home}/build/specdocs"
					style="{$xins_home}/src/xslt/specdocs/function_to_html.xslt"
					includes="{$api}/*.fnc">
						<param name="project_home" expression="{$project_home}" />
						<param name="specsdir"     expression="{$specsdir}"       />
					</style>
					<style
					basedir="{$project_home}/{$specsdir}"
					destdir="{$project_home}/build/specdocs"
					style="{$xins_home}/src/xslt/specdocs/type_to_html.xslt"
					includes="{$api}/*.typ">
						<param name="project_home" expression="{$project_home}" />
						<param name="specsdir"     expression="{$specsdir}"       />
					</style>
					<xsl:for-each select="document($api_file)/api/environment">
						<style
						basedir="{$project_home}/{$specsdir}"
						destdir="{$project_home}/build/specdocs"
						style="{$xins_home}/src/xslt/testforms/function_to_html.xslt"
						includes="{$api}/*.fnc"
						extension="-testform-{@id}.html">
							<param name="project_home" expression="{$project_home}" />
							<param name="specsdir"     expression="{$specsdir}"       />
							<param name="environment"  expression="{@id}" />
						</style>
					</xsl:for-each>
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

			<xsl:for-each select="api[document(concat($project_home, '/', $specsdir, '/', @name, '/api.xml'))/api/impl-java]">
				<xsl:variable name="api"      select="@name"                                                         />
				<xsl:variable name="api_file" select="concat($project_home, '/', $specsdir, '/', @name, '/api.xml')" />
				<xsl:variable name="package">
					<xsl:call-template name="package_for_api">
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

				<target name="classes-api-{$api}" depends="-prepare-classes" description="Compiles the Java classes for the '{$api}' API">
					<mkdir dir="{$project_home}/build/java-fundament/{$packageAsDir}" />
					<style
					in="{$api_file}"
					out="{$project_home}/build/java-fundament/{$packageAsDir}/APIImpl.java"
					style="{$xins_home}/src/xslt/java-fundament/api_to_java.xslt">
						<param name="project_home" expression="{$project_home}" />
						<param name="specsdir"     expression="{$specsdir}"     />
						<param name="package"      expression="{$package}"      />
					</style>
					<!-- TODO: Include only functions mentioned in api.xml -->
					<style
					basedir="{$project_home}/{$specsdir}/{$api}"
					destdir="{$project_home}/build/java-fundament/{$packageAsDir}"
					style="{$xins_home}/src/xslt/java-fundament/function_to_java.xslt"
					includes="*.fnc"
					extension=".java">
						<param name="project_home" expression="{$project_home}" />
						<param name="specsdir"     expression="{$specsdir}"     />
						<param name="package"      expression="{$package}"      />
						<param name="api"          expression="{@api}"          />
						<param name="api_file"     expression="{@api_file}"     />
					</style>
				</target>
			</xsl:for-each>

			<target name="classes" description="Compiles all Java classes">
				<xsl:attribute name="depends">
					<xsl:for-each select="api[document(concat($project_home, '/', $specsdir, '/', @name, '/api.xml'))/api/impl-java]">
						<xsl:if test="position() &gt; 1">,</xsl:if>
						<xsl:text>classes-api-</xsl:text>
						<xsl:value-of select="@name" />
					</xsl:for-each>
				</xsl:attribute>
			</target>

			<xsl:for-each select="api[document(concat($project_home, '/', $specsdir, '/', @name, '/api.xml'))/api/impl-java]">
				<target name="war-api-{@name}" depends="classes-api-{@name}" description="Creates the WAR for the '{@name}' API" />
			</xsl:for-each>

			<target name="wars" description="Creates the WARs for all APIs">
				<xsl:attribute name="depends">
					<xsl:for-each select="api[document(concat($project_home, '/', $specsdir, '/', @name, '/api.xml'))/api/impl-java]">
						<xsl:if test="position() &gt; 1">,</xsl:if>
						<xsl:text>war-api-</xsl:text>
						<xsl:value-of select="@name" />
					</xsl:for-each>
				</xsl:attribute>
			</target>

			<target name="all" depends="specdocs,wars" description="Generates everything" />
		</project>
	</xsl:template>

	<xsl:template name="package_for_api">
		<xsl:param name="api" />

		<xsl:variable name="prefix" select="document($project_file)/project/java-impls/@packageprefix" />
		<xsl:variable name="suffix" select="document($project_file)/project/java-impls/@packagesuffix" />

		<xsl:if test="string-length($prefix) &gt; 0">
			<xsl:value-of select="$prefix" />
			<xsl:text>.</xsl:text>
		</xsl:if>

		<xsl:value-of select="$api" />

		<xsl:if test="string-length($suffix) &gt; 0">
			<xsl:text>.</xsl:text>
			<xsl:value-of select="$suffix" />
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
