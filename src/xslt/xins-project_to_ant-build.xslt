<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output indent="yes" />

	<xsl:variable name="specsdir">
		<xsl:choose>
			<xsl:when test="//project/@specsdir">
				<xsl:value-of select="//project/@specsdir" />
			</xsl:when>
			<xsl:otherwise>src/specs</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

	<xsl:template match="project">
		<project default="all" basedir="..">

			<target name="-prepare" />

			<target name="-prepare-specdocs" depends="-prepare">
				<mkdir dir="build/specdocs" />
				<copy
				todir="build/specdocs"
				file="${{xins_home}}/src/css/specdocs/style.css" />
			</target>

			<target name="specdocs-index" depends="-prepare-specdocs" description="Generates the API index">
				<style
				in="xins-project.xml"
				out="build/specdocs/index.html"
				style="${{xins_home}}/src/xslt/specdocs/xins-project_to_index.xslt">
					<param name="project_home" expression="${{project_home}}" />
					<param name="specsdir"     expression="{$specsdir}"       />
				</style>
			</target>

			<xsl:for-each select="api">
				<xsl:variable name="api" select="@name" />

				<target name="specdocs-api-{@name}" depends="-prepare-specdocs" description="Generates all specification docs for the '{@name}' API">
					<dependset>
						<srcfilelist   dir="${{project_home}}/{$specsdir}/{@name}"    files="*.fnc" />
						<srcfilelist   dir="${{project_home}}/{$specsdir}/{@name}"    files="*.typ" />
						<targetfileset dir="${{project_home}}/build/specdocs/{@name}" includes="api.html" />
					</dependset>
					<style
					in="${{project_home}}/{$specsdir}/{@name}/api.xml"
					out="${{project_home}}/build/specdocs/{@name}/api.html"
					style="${{xins_home}}/src/xslt/specdocs/api_to_html.xslt">
						<param name="project_home" expression="${{project_home}}" />
						<param name="specsdir"     expression="{$specsdir}"       />
					</style>
					<style
					basedir="${{project_home}}/{$specsdir}"
					destdir="${{project_home}}/build/specdocs"
					style="${{xins_home}}/src/xslt/specdocs/function_to_html.xslt"
					includes="{@name}/*.fnc">
						<param name="project_home" expression="${{project_home}}" />
						<param name="specsdir"     expression="{$specsdir}"       />
					</style>
					<style
					basedir="${{project_home}}/{$specsdir}"
					destdir="${{project_home}}/build/specdocs"
					style="${{xins_home}}/src/xslt/specdocs/type_to_html.xslt"
					includes="{@name}/*.typ">
						<param name="project_home" expression="${{project_home}}" />
						<param name="specsdir"     expression="{$specsdir}"       />
					</style>
					<xsl:for-each select="//api/environment">
						<style
						basedir="${{project_home}}/{$specsdir}"
						destdir="${{project_home}}/build/specdocs"
						style="${{xins_home}}/src/xslt/testforms/function_to_html.xslt"
						includes="{$api}/*.fnc"
						extension="-testform-{@id}.html">
							<param name="project_home" expression="${{project_home}}" />
							<param name="specsdir"     expression="{$specsdir}"       />
							<param name="environment"  expression="{@id}" />
						</style>
					</xsl:for-each>
				</target>
			</xsl:for-each>

			<target name="specdocs" description="Generates the specification docs for all APIs">
				<xsl:attribute name="depends">
					<xsl:text>specdocs-index</xsl:text>
					<xsl:for-each select="api">
						<xsl:text>,specdocs-api-</xsl:text>
						<xsl:value-of select="@name" />
					</xsl:for-each>
				</xsl:attribute>
			</target>

			<target name="all" depends="specdocs" description="Generates everything" />
		</project>
	</xsl:template>
</xsl:stylesheet>
