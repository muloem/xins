<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 XSLT that generates the package.html which is used by the javadoc.

 $Id$

 Copyright 2003-2005 Wanadoo Nederland B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Define parameters -->
	<xsl:param name="project_home" />
	<xsl:param name="project_file" />
	<xsl:param name="specsdir"     />
	<xsl:param name="package"      />
	<xsl:param name="api"          />
	<xsl:param name="api_file"     />

	<!-- Output is text/plain -->
	<xsl:output method="html" />

	<!-- Perform includes -->
	<xsl:include href="../casechange.xslt" />
	<xsl:include href="../rcs.xslt"        />
	<xsl:include href="../types.xslt"      />

	<!-- Determine the location of the online specification docs -->
	<xsl:variable name="specdocsURL">
		<xsl:value-of select="document($project_file)/project/specdocs/@href" />
	</xsl:variable>

	<!-- ***************************************************************** -->
	<!-- Match the root element: api                                       -->
	<!-- ***************************************************************** -->

	<xsl:template match="api">

		<html>
			<body>
				<xsl:text>Client-side calling interface (CAPI) for the </xsl:text>
				<em>
					<xsl:value-of select="$api" />
				</em>
				<xsl:text> API.</xsl:text>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
