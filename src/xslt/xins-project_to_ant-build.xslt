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
				<mkdir dir="specdocs" />
			</target>

			<target name="specdocs" depends="-prepare">
				<style
				in="xins-project.xml"
				out="specdocs/index.html"
				style="${{xins_home}}/src/xslt/xins-project_to_specdocs-index.xslt" />
			</target>

			<target name="all" depends="specdocs" />
		</project>
	</xsl:template>
</xsl:stylesheet>
