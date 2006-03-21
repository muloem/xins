<?xml version="1.0" encoding="US-ASCII"?>
<!--
 XSLT that generated an XML file containing the list of all result codes
 per function.

 $Id$

 Copyright 2003-2006 Wanadoo Nederland B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Define parameters -->
	<xsl:param name="specsdir"     />

	<xsl:output method="xml" indent="yes" />

	<xsl:template match="api">

		<xsl:comment>Generated file used to create result code Java files.</xsl:comment>
		<api name="{@name}">
			<xsl:for-each select="function">
				<function name="{@name}">
					<xsl:variable name="function_file" select="concat($specsdir, '/', @name, '.fnc')" />
					<xsl:variable name="function_node" select="document($function_file)/function" />
					<xsl:for-each select="$function_node/output/resultcode-ref">
						<resultcode name="{@name}" />
					</xsl:for-each>
				</function>
			</xsl:for-each>
		</api>

	</xsl:template>

</xsl:stylesheet>
