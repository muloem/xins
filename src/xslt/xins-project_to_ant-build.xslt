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

			<target name="-specdocs-prepare" depends="-prepare">
				<mkdir dir="build/specdocs" />
				<copy
				todir="build/specdocs"
				file="${{xins_home}}/src/css/specdocs/style.css" />
			</target>

			<target name="specdocs-index" depends="-specdocs-prepare">
				<style
				in="xins-project.xml"
				out="build/specdocs/index.html"
				style="${{xins_home}}/src/xslt/specdocs/xins-project_to_index.xslt">
					<param name="project_home" expression="${{project_home}}" />
				</style>
			</target>

			<target name="specdocs-apis" depends="-specdocs-prepare">
				<style
				basedir="${{project_home}}/src/specs"
				destdir="${{project_home}}/build/specdocs"
				style="${{xins_home}}/src/xslt/specdocs/api_to_html.xslt"
				includes="**/api.xml">
					<param name="project_home" expression="${{project_home}}" />
				</style>
			</target>

			<target name="specdocs-functions" depends="-specdocs-prepare">
				<style
				basedir="${{project_home}}/src/specs"
				destdir="${{project_home}}/build/specdocs"
				style="${{xins_home}}/src/xslt/specdocs/function_to_html.xslt"
				includes="**/*.fnc">
					<param name="project_home" expression="${{project_home}}" />
				</style>
			</target>

			<target name="specdocs-types" depends="-specdocs-prepare">
				<style
				basedir="${{project_home}}/src/specs"
				destdir="${{project_home}}/build/specdocs"
				style="${{xins_home}}/src/xslt/specdocs/type_to_html.xslt"
				includes="**/*.typ">
					<param name="project_home" expression="${{project_home}}" />
				</style>
			</target>

			<target name="specdocs" depends="specdocs-index,specdocs-apis,specdocs-functions,specdocs-types" />

			<target name="all" depends="specdocs" />
		</project>
	</xsl:template>
</xsl:stylesheet>
