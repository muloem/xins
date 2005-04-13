<?xml version="1.0" encoding="UTF-8" ?>

<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 XSLT stylesheet that converts a JUnit testresult to HTML.

 $Id$
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:output
	method="xml"
	indent="no"
	doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
	doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" />

	<xsl:template match="testsuite">
		<xsl:variable name="TimeAllInOne" select="testcase[@name='testPerformanceAllInOne']/@time" />
		<xsl:variable name="OvrhAllInOne" select="testcase[@name='testOverheadAllInOne']/@time"    />
		<xsl:variable name="PerfAllInOne" select="$TimeAllInOne - $OvrhAllInOne" />

		<html>
			<head>
				<title>Performance test results</title>
				<link rel="stylesheet" type="text/css" href="stylesheet.css" />
			</head>
			<body>
				<h1>Performance test results</h1>
				<table class="perftests">
					<tr>
						<th>#</th>
						<th>Name</th>
						<th>Net time</th>
					</tr>
					<tr>
						<td>1</td>
						<td>AllInOne</td>
						<td>
<xsl:value-of select="round($PerfAllInOne * 100) div 100" />
						</td>
					</tr>
				</table>

				<xsl:if test="count(testcase[failure or error]) &gt; 0">
					<h2>Failed performance tests</h2>
					<xsl:apply-templates select="testcase[failure or error]" />
				</xsl:if>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="testcase[failure]">
		<xsl:variable name="name_orig"  select="substring-after(@name, 'test')" />
		<xsl:variable name="name_start" select="translate(substring($name_orig, 1, 1), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')" />
		<xsl:variable name="name_end"   select="substring($name_orig, 2, string-length($name_orig) - 1)" />

		<xsl:variable name="name"    select="concat($name_start, $name_end)" />

		<xsl:variable name="details_orig"            select="failure/text()" />
		<xsl:variable name="details_orig_length"     select="string-length($details_orig)" />
		<xsl:variable name="details_after_at"        select="substring-after($details_orig, 'at ')" />
		<xsl:variable name="details_after_at_length" select="string-length(details_after_at)" />
		<xsl:variable name="details"                 select="$details_after_at" />

		<a>
			<xsl:attribute name="name">
				<xsl:text>test-</xsl:text>
				<xsl:value-of select="$name" />
			</xsl:attribute>
		</a>
		<h3>
			<xsl:text>Test </xsl:text>
			<xsl:value-of select="$name" />
			<xsl:text> (</xsl:text>
			<span class="failure">Failed</span>
			<xsl:text>)</xsl:text>
		</h3>
		<table class="testcase_details">
			<tr>
				<th>Name:</th>
				<td>
					<xsl:value-of select="$name" />
				</td>
			</tr>
			<tr>
				<th>Time:</th>
				<td>
					<xsl:value-of select="@time" />
					<xsl:text> second</xsl:text>
					<xsl:if test="not(@time = 1)">
						<xsl:text>s</xsl:text>
					</xsl:if>
				</td>
			</tr>
			<tr>
				<th>Failure type:</th>
				<td>
					<xsl:value-of select="failure/@type" />
				</td>
			</tr>
			<tr>
				<th>Message:</th>
				<td class="testdetails_message">
					<xsl:value-of select="normalize-space(failure/@message)" />
				</td>
			</tr>
			<tr>
				<th>Details:</th>
				<td class="testdetails_details">
					<xsl:value-of select="$details" />
				</td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template match="testcase[error]">
		<xsl:variable name="name_orig"  select="substring-after(@name, 'test')" />
		<xsl:variable name="name_start" select="translate(substring($name_orig, 1, 1), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')" />
		<xsl:variable name="name_end"   select="substring($name_orig, 2, string-length($name_orig) - 1)" />

		<xsl:variable name="name"    select="concat($name_start, $name_end)" />

		<xsl:variable name="details_orig"        select="error/text()" />
		<xsl:variable name="details_orig_length" select="string-length($details_orig)" />
		<xsl:variable name="details_after_at"   select="substring-after($details_orig, 'at ')" />
		<xsl:variable name="details_after_at_length" select="string-length(details_after_at)" />
		<xsl:variable name="details"             select="$details_after_at" />

		<a>
			<xsl:attribute name="name">
				<xsl:text>test-</xsl:text>
				<xsl:value-of select="$name" />
			</xsl:attribute>
		</a>
		<h3>
			<xsl:text>Test </xsl:text>
			<xsl:value-of select="$name" />
			<xsl:text> (</xsl:text>
			<span class="error">Error</span>
			<xsl:text>)</xsl:text>
		</h3>
		<table class="testcase_details">
			<tr>
				<th>Name:</th>
				<td>
					<xsl:value-of select="$name" />
				</td>
			</tr>
			<tr>
				<th>Time:</th>
				<td>
					<xsl:value-of select="@time" />
					<xsl:text> second</xsl:text>
					<xsl:if test="not(@time = 1)">
						<xsl:text>s</xsl:text>
					</xsl:if>
				</td>
			</tr>
			<tr>
				<th>Error type:</th>
				<td>
					<xsl:value-of select="error/@type" />
				</td>
			</tr>
			<tr>
				<th>Message:</th>
				<td class="testdetails_message">
					<xsl:value-of select="normalize-space(error/@message)" />
				</td>
			</tr>
			<tr>
				<th>Details:</th>
				<td class="testdetails_details">
					<xsl:value-of select="$details" />
				</td>
			</tr>
		</table>
	</xsl:template>
</xsl:stylesheet>
