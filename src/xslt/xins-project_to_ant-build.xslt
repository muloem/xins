<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output indent="yes" />

	<xsl:template match="project">
		<project default="all" basedir="..">

			<target name="-prepare" />

			<target name="-prepare-specdocs" depends="-prepare">
				<mkdir dir="build/specdocs" />
				<copy
				todir="build/specdocs"
				file="${{xins_home}}/src/css/specdocs/style.css" />
			</target>

			<target name="specdocs-index" depends="-prepare-specdocs">
				<style
				in="xins-project.xml"
				out="build/specdocs/index.html"
				style="${{xins_home}}/src/xslt/specdocs/xins-project_to_index.xslt">
					<param name="project_home" expression="${{project_home}}" />
				</style>
			</target>

			<target name="specdocs-apis" depends="-prepare-specdocs">
				<style
				basedir="${{project_home}}/src/specs"
				destdir="${{project_home}}/build/specdocs"
				style="${{xins_home}}/src/xslt/specdocs/api_to_html.xslt"
				includes="**/api.xml">
					<param name="project_home" expression="${{project_home}}" />
				</style>
			</target>

			<target name="specdocs-functions" depends="-prepare-specdocs">
				<style
				basedir="${{project_home}}/src/specs"
				destdir="${{project_home}}/build/specdocs"
				style="${{xins_home}}/src/xslt/specdocs/function_to_html.xslt"
				includes="**/*.fnc">
					<param name="project_home" expression="${{project_home}}" />
				</style>
			</target>

			<target name="specdocs-types" depends="-prepare-specdocs">
				<style
				basedir="${{project_home}}/src/specs"
				destdir="${{project_home}}/build/specdocs"
				style="${{xins_home}}/src/xslt/specdocs/type_to_html.xslt"
				includes="**/*.typ">
					<param name="project_home" expression="${{project_home}}" />
				</style>
			</target>

			<target name="testforms">
				<xsl:attribute name="depends">
					<xsl:for-each select="api">
						<xsl:if test="position() &gt; 1">,</xsl:if>
						<xsl:text>testforms-</xsl:text>
						<xsl:value-of select="@name" />
					</xsl:for-each>
				</xsl:attribute>
			</target>

			<xsl:for-each select="api">
				<target name="testforms-{@name}" depends="-prepare-specdocs">
					<xsl:for-each select="//project/environment">
						<style
						basedir="${{project_home}}/src/specs"
						destdir="${{project_home}}/build/specdocs"
						style="${{xins_home}}/src/xslt/testforms/function_to_html.xslt"
						includes="**/*.fnc"
						extension="-testform-{@id}.html">
							<param name="project_home" expression="${{project_home}}" />
							<param name="environment"  expression="{@id}" />
						</style>
					</xsl:for-each>
				</target>
			</xsl:for-each>

			<target name="specdocs" depends="specdocs-index,specdocs-apis,specdocs-functions,specdocs-types,testforms" />

			<target name="all" depends="specdocs" />
		</project>
	</xsl:template>
</xsl:stylesheet>
