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

			<target name="-prepare">
				<mkdir dir="build/specdocs" />
			</target>

			<target name="specdocs" depends="-prepare">
				<copy
				todir="build/specdocs"
				file="${{xins_home}}/src/css/specdocs/style.css" />
				<style
				in="xins-project.xml"
				out="build/specdocs/index.html"
				style="${{xins_home}}/src/xslt/xins-project_to_specdocs-index.xslt">
					<param name="project_home" expression="${{project_home}}" />
				</style>
			</target>

			<target name="all" depends="specdocs" />
		</project>
	</xsl:template>
</xsl:stylesheet>
