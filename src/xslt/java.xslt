<?xml version="1.0"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 XSLT that generates the header of the generated java files.

 $Id$

 Copyright 2003-2005 Wanadoo Nederland B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template name="java-header">
		<xsl:text>// This is a generated file. Please do not edit.&#10;&#10;</xsl:text>
	</xsl:template>
</xsl:stylesheet>
