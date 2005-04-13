<?xml version="1.0" encoding="UTF-8" ?>

<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 XSLT that transform the _GetVersion XML to an HTML page.

 $Id$

 Copyright 2003-2005 Wanadoo Nederland B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output encoding="UTF-8"
		            media-type="text/html" />

    <xsl:template match="/">
        <html>
            <head>
                <title>getVersion2.xslt</title>
            </head>
            <body>
							API version : 
							<xsl:value-of select="/result/param[@name='api.version']" />
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>
