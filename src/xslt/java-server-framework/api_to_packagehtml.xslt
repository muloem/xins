<?xml version="1.0" encoding="US-ASCII"?>
<!--
 XSLT that generates the package.html which is used by Javadoc.

 $Id$

 Copyright 2003-2008 Online Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Define parameters -->
	<xsl:param name="api"          />

	<!-- Output is HTML -->
	<xsl:output method="html" />

   <!-- Generate the HTML document -->
	<xsl:template match="api">
		<html>
			<body>
				<xsl:text>Implementation of the </xsl:text>
				<em>
					<xsl:value-of select="$api" />
				</em>
				<xsl:text> API.</xsl:text>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
