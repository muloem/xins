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

				<h2>Message entries</h2>
				<p>The following message entries are declared:</p>
				<table type="entries">
					<tr>
						<th title="The unique identifier of the entry">ID</th>
						<th title="A description of the message entry, in US English">Description</th>
						<th title="The log level for the message, ranging from DEBUG to FATAL">Level</th>
						<th title="The logging category">Category</th>
						<th title="Number of parameters the message accepts">Parameters</th>
						<th title="Number of available translations for this message entry">Translations</th>
					</tr>
					<xsl:for-each select="entry">
						<xsl:variable name="entry_link">
							<xsl:text>entry-</xsl:text>
							<xsl:value-of select="@id" />
							<xsl:text>.html</xsl:text>
						</xsl:variable>
						<tr>
							<td>
								<a>
									<xsl:attribute name="href">
										<xsl:value-of select="$entry_link" />
									</xsl:attribute>
									<xsl:value-of select="@id" />
								</a>
							</td>
							<td>
								<a>
									<xsl:attribute name="href">
										<xsl:value-of select="$entry_link" />
									</xsl:attribute>
									<xsl:apply-templates select="description" />
								</a>
							</td>
							<td>
								<xsl:value-of select="@level" />
							</td>
							<td>
								<xsl:value-of select="@category" />
							</td>
							<td>
								<xsl:value-of select="count(param)" />
							</td>
							<td>
								<!-- TODO: Count translations -->
							</td>
						</tr>
					</xsl:for-each>
				</table>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
