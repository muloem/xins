<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Define parameters -->
	<xsl:param name="logdoc_xslt_dir" />
	<xsl:param name="sourcedir"       />
	<xsl:param name="html_destdir"    />
	<xsl:param name="java_destdir"    />
	<xsl:param name="package_name"    />

	<!-- Perform includes -->
	<xsl:include href="../hungarian.xslt"       />
	<xsl:include href="../package_to_dir.xslt"  />
	<xsl:include href="../package_for_api.xslt" />

	<xsl:output indent="yes" />

	<xsl:template match="log">
		<project default="all" basedir="..">
			<target name="html" description="Generates HTML documentation">
				<mkdir dir="{$html_destdir}" />
				<style
				in="{$sourcedir}/log.xml"
				out="{$html_destdir}/index.html"
				style="{$logdoc_xslt_dir}/log_to_html.xslt" />
				<xsl:for-each select="group">
					<style
					in="{$sourcedir}/log.xml"
					out="{$html_destdir}/group-{@name}.html"
					style="{$logdoc_xslt_dir}/log_to_group_html.xslt">
						<param name="sourcedir" expression="../../{$sourcedir}" />
						<param name="group"     expression="{@name}"            />
					</style>
				</xsl:for-each>
				<xsl:for-each select="group/entry">
					<style
					in="{$sourcedir}/log.xml"
					out="{$html_destdir}/entry-{@id}.html"
					style="{$logdoc_xslt_dir}/log_to_entry_html.xslt">
						<param name="sourcedir" expression="../../{$sourcedir}" />
						<param name="entry"     expression="{@id}"              />
					</style>
				</xsl:for-each>
			</target>

			<target name="java" description="Generates Java code">
				<mkdir dir="{$java_destdir}" />
				<style
				in="{$sourcedir}/log.xml"
				out="{$java_destdir}/Log.java"
				style="{$logdoc_xslt_dir}/log_to_Log_java.xslt">
					<param name="package_name" expression="{$package_name}" />
				</style>
				<style
				in="{$sourcedir}/log.xml"
				out="{$java_destdir}/NoSuchTranslationBundleException.java"
				style="{$logdoc_xslt_dir}/log_to_NoSuchTranslationBundleException_java.xslt">
					<param name="package_name" expression="{$package_name}" />
				</style>
				<style
				in="{$sourcedir}/log.xml"
				out="{$java_destdir}/TranslationBundle.java"
				style="{$logdoc_xslt_dir}/log_to_TranslationBundle_java.xslt">
					<param name="package_name" expression="{$package_name}" />
				</style>
				<xsl:for-each select="translation-bundle">
					<style
					in="{$sourcedir}/translation-bundle-{@locale}.xml"
					out="{$java_destdir}/TranslationBundle_{@locale}.java"
					style="{$logdoc_xslt_dir}/translation-bundle_to_java.xslt">
						<param name="locale"       expression="{@locale}" />
						<param name="package_name" expression="{$package_name}" />
						<param name="log_file"     expression="../../{$sourcedir}/log.xml" />
					</style>
				</xsl:for-each>
			</target>

			<target name="all" depends="html, java" />
		</project>
	</xsl:template>
</xsl:stylesheet>
