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
		<xsl:variable name="default_locale" select="@default-locale" />
		<html>
			<head>
				<title>Log documentation</title>
				<meta name="generator" content="logdoc" />
				<link rel="stylesheet" type="text/css" href="style.css" />
			</head>
			<body>
				<table class="headerlinks">
					<tr>
						<td>
							<span class="active">Logdoc index</span>
							<xsl:text> | </xsl:text>
							<span class="disabled">Log entry group</span>
							<xsl:text> | </xsl:text>
							<span class="disabled">Log entry</span>
						</td>
					</tr>
				</table>

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

				<h2>Translation bundles</h2>
				<p>The following translation bundles are available:</p>
				<ul>
					<xsl:for-each select="translation-bundle">
						<li>
							<xsl:value-of select="@locale" />
							<xsl:if test="$default_locale = @locale"> (default)</xsl:if>
						</li>
					</xsl:for-each>
				</ul>

				<h2>Log entry groups</h2>
				<xsl:text>The following groups are defined:</xsl:text>
				<table type="groups">
					<tr>
						<th title="The name of the group">Name</th>
						<th title="The logging category for the group">Category</th>
						<th title="The number of log entries in this group">Entries</th>
					</tr>
					<xsl:for-each select="group">
						<xsl:variable name="group_link">
							<xsl:text>group-</xsl:text>
							<xsl:value-of select="@name" />
							<xsl:text>.html</xsl:text>
						</xsl:variable>
						<tr>
							<td>
								<a>
									<xsl:attribute name="href">
										<xsl:value-of select="$group_link" />
									</xsl:attribute>
									<xsl:value-of select="@name" />
								</a>
							</td>
							<td>
								<xsl:value-of select="@category" />
							</td>
							<td>
								<xsl:value-of select="count(entry)" />
							</td>
						</tr>
					</xsl:for-each>
				</table>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
