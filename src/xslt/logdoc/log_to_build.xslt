<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 XSLT that generates the build.xml that will create the logdoc java or html files.

 $Id$

 Copyright 2003-2005 Wanadoo Nederland B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Define parameters -->
	<xsl:param name="xins_home"       />
	<xsl:param name="logdoc_xslt_dir" />
	<xsl:param name="logdoc_dtd_dir"  />
	<xsl:param name="sourcedir"       />
	<xsl:param name="html_destdir"    />
	<xsl:param name="java_destdir"    />
	<xsl:param name="package_name"    />

	<!-- Perform includes -->
	<xsl:include href="../hungarian.xslt"       />
	<xsl:include href="../package_for_api.xslt" />

	<xsl:output indent="yes" />

	<xsl:template match="log">
		<project default="all" basedir="..">
			<target name="html" description="Generates HTML documentation">
				<mkdir dir="{$html_destdir}" />
				<!-- TODO: Define the xmlcatalog only in one place -->
				<xmlcatalog id="log-dtds">
					<dtd location="{$xins_home}/src/dtd/log_1_0.dtd"
					     publicId="-//XINS//DTD XINS Logdoc 1.0//EN" />
					<dtd location="{$xins_home}/src/dtd/translation-bundle_1_0.dtd"
					     publicId="-//XINS//DTD XINS Translation Bundle 1.0//EN" />
					<dtd location="{$xins_home}/src/dtd/translation-bundle_1_1.dtd"
					     publicId="-//XINS//DTD XINS Translation Bundle 1.1//EN" />
					<dtd location="{$xins_home}/src/dtd/log_1_0_alpha.dtd"
					     publicId="-//XINS//DTD XINS Logdoc 1.0 alpha//EN" />
					<dtd location="{$xins_home}/src/dtd/translation-bundle_1_0_alpha.dtd"
					     publicId="-//XINS//DTD XINS Translation Bundle 1.0 alpha//EN" />
				</xmlcatalog>
				<xmlvalidate warn="false" file="{$sourcedir}/log.xml">
					<xmlcatalog refid="log-dtds" />
				</xmlvalidate>
				<style
				in="{$sourcedir}/log.xml"
				out="{$html_destdir}/index.html"
				style="{$logdoc_xslt_dir}/log_to_html.xslt">
					<xmlcatalog refid="log-dtds" />
					<param name="package_name" expression="{$package_name}" />
				</style>
				<style
				in="{$sourcedir}/log.xml"
				out="{$html_destdir}/entry-list.html"
				style="{$logdoc_xslt_dir}/log_to_list_html.xslt">
					<xmlcatalog refid="log-dtds" />
				</style>
				<xsl:for-each select="group">
					<style
					in="{$sourcedir}/log.xml"
					out="{$html_destdir}/group-{@id}.html"
					style="{$logdoc_xslt_dir}/log_to_group_html.xslt">
						<xmlcatalog refid="log-dtds" />
						<param name="package_name" expression="{$package_name}" />
						<param name="sourcedir" expression="../../{$sourcedir}" />
						<param name="group"     expression="{@id}"              />
					</style>
				</xsl:for-each>
				<xsl:for-each select="group/entry">
					<style
					in="{$sourcedir}/log.xml"
					out="{$html_destdir}/entry-{@id}.html"
					style="{$logdoc_xslt_dir}/log_to_entry_html.xslt">
						<xmlcatalog refid="log-dtds" />
						<param name="package_name" expression="{$package_name}" />
						<param name="sourcedir" expression="{$sourcedir}" />
						<param name="entry"     expression="{@id}"              />
					</style>
				</xsl:for-each>
			</target>

			<target name="java" description="Generates Java code">
				<mkdir dir="{$java_destdir}" />
				<xmlcatalog id="log-dtds">
					<dtd location="{$xins_home}/src/dtd/log_1_0.dtd"
					     publicId="-//XINS//DTD XINS Logdoc 1.0//EN" />
					<dtd location="{$xins_home}/src/dtd/translation-bundle_1_0.dtd"
					     publicId="-//XINS//DTD XINS Translation Bundle 1.0//EN" />
					<dtd location="{$xins_home}/src/dtd/translation-bundle_1_1.dtd"
					     publicId="-//XINS//DTD XINS Translation Bundle 1.1//EN" />
					<dtd location="{$xins_home}/src/dtd/log_1_0_alpha.dtd"
					     publicId="-//XINS//DTD XINS Logdoc 1.0 alpha//EN" />
					<dtd location="{$xins_home}/src/dtd/translation-bundle_1_0_alpha.dtd"
					     publicId="-//XINS//DTD XINS Translation Bundle 1.0 alpha//EN" />
				</xmlcatalog>
				<xmlvalidate warn="false" file="{$sourcedir}/log.xml">
					<xmlcatalog refid="log-dtds" />
				</xmlvalidate>
				<style
				in="{$sourcedir}/log.xml"
				out="{$java_destdir}/Log.java"
				style="{$logdoc_xslt_dir}/log_to_Log_java.xslt">
					<xmlcatalog refid="log-dtds" />
					<param name="package_name" expression="{$package_name}" />
					<param name="accesslevel" expression="${{accesslevel}}" />
				</style>
				<style
				in="{$sourcedir}/log.xml"
				out="{$java_destdir}/TranslationBundle.java"
				style="{$logdoc_xslt_dir}/log_to_TranslationBundle_java.xslt">
					<xmlcatalog refid="log-dtds" />
					<param name="package_name" expression="{$package_name}" />
					<param name="accesslevel"  expression="${{accesslevel}}" />
				</style>
				<xsl:for-each select="translation-bundle">
					<xmlvalidate warn="false" file="{$sourcedir}/translation-bundle-{@locale}.xml">
						<xmlcatalog refid="log-dtds" />
					</xmlvalidate>
					<style
					in="{$sourcedir}/translation-bundle-{@locale}.xml"
					out="{$java_destdir}/TranslationBundle_{@locale}.java"
					style="{$logdoc_xslt_dir}/translation-bundle_to_java.xslt">
						<xmlcatalog refid="log-dtds" />
						<param name="locale"       expression="{@locale}" />
						<param name="package_name" expression="{$package_name}" />
						<param name="log_file"     expression="{$sourcedir}/log.xml" />
						<param name="accesslevel" expression="${{accesslevel}}" />
					</style>
				</xsl:for-each>
			</target>

			<target name="all" depends="html, java" />
		</project>
	</xsl:template>
</xsl:stylesheet>
