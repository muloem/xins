<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output
	method="xml"
	indent="no"
	encoding="US-ASCII"
	doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
	doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
	omit-xml-declaration="yes" />

	<xsl:template match="log">
		<html>
			<head>
				<title>Log documentation</title>
				<meta name="generator" content="logdoc" />
				<link rel="stylesheet" type="text/css" href="style.css" />
			</head>
			<body>
				<h1>Log documentation</h1>

				<h2>Log levels</h2>
				<p>The following log levels can be used:</p>
				<table class="loglevels">
					<tr>
						<th>ID</th>
						<th>Description</th>
					</tr>
					<tr>
						<td>debug</td>
						<td>Debugging messages. Only useful for programmers.  This is the only level that may contain implementation details that are not exposed outside individual functions.</td>
					</tr>
					<tr>
						<td>info</td>
						<td>Informational messages. Typically not important to operational people, except in cases where a problem is being traced or if behaviour is investigated.</td>
					</tr>
					<tr>
						<td>notice</td>
						<td>Informational messages that should typically be noticed by operational people.</td>
					</tr>
					<tr>
						<td>warning</td>
						<td>Warning messages. Should be noticed, but typically require no immediate action, although they may indicate a problem that should be fixed.</td>
					</tr>
					<tr>
						<td>error</td>
						<td>Error messages. Indicates an error that should be fixed. However, it does not keep the whole application from functioning.</td>
					</tr>
					<tr>
						<td>fatal</td>
						<td>Fatal error messages. Indicates an error that keeps the whole application from functioning. The application must be restarted in order to recover from the problem.</td>
					</tr>
				</table>

				<h2>Message sets</h2>
				<p>The following message sets are available:</p>
				<ul>
					<li>RAW (default)</li>
					<xsl:for-each select="messageset">
						<li>
							<xsl:value-of select="@id" />
						</li>
					</xsl:for-each>
				</ul>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
