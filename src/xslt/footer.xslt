<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:include href="rcs.xslt" />

	<xsl:template name="footer">

		<xsl:variable name="version">
	    	<xsl:call-template name="revision2string">
				<xsl:with-param name="revision">
					<xsl:value-of select="@rcsversion" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="timestamp">
			<xsl:choose>
				<xsl:when test="@rcsdate = concat('$', 'Date$')">
					<xsl:text>?/?/? ?:?:?</xsl:text>
				</xsl:when>
				<xsl:when test="string-length(@rcsdate) &lt; 20">
					<xsl:message>
						<xsl:text>Unable to parse RCS date. It should be specified in the 'rcsdate' attribute of the '</xsl:text>
						<xsl:value-of select="name()" />
						<xsl:text>' element.</xsl:text>
					</xsl:message>
					<xsl:text>?/?/? ?:?:?</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="substring(@rcsdate, 8, string-length(@rcsdate) - 9)" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="date">
			<xsl:value-of select="substring-before($timestamp, ' ')" />
		</xsl:variable>

		<xsl:variable name="time">
			<xsl:value-of select="substring-after($timestamp, ' ')" />
		</xsl:variable>

		<xsl:variable name="date_year">
			<xsl:value-of select="substring-before($date, '/')" />
		</xsl:variable>

		<xsl:variable name="date_month">
			<xsl:value-of select="substring-before(substring-after($date, '/'), '/')" />
		</xsl:variable>

		<xsl:variable name="date_day">
			<xsl:value-of select="substring-after(substring-after($date, '/'), '/')" />
		</xsl:variable>

		<xsl:variable name="time_hour">
			<xsl:value-of select="substring-before($time, ':')" />
		</xsl:variable>

		<xsl:variable name="time_minute">
			<xsl:value-of select="substring-before(substring-after($time, ':'), ':')" />
		</xsl:variable>

		<xsl:variable name="time_second">
			<xsl:value-of select="substring-after(substring-after($time, ':'), ':')" />
		</xsl:variable>

		<xsl:if test="not(string-length(@rcsdate) &gt; 0)">
			<xsl:message terminate="yes">
				<xsl:text>The RCS date is not specified. It should be specified in the 'rcsdate' attribute of the '</xsl:text>
				<xsl:value-of select="name()" />
				<xsl:text>' element.</xsl:text>
			</xsl:message>
		</xsl:if>

		<div class="footer">
			<xsl:attribute name="title">
				<xsl:text>Version </xsl:text>
				<xsl:value-of select="$version" />
				<xsl:text> (</xsl:text>
				<xsl:value-of select="$date_year" />
				<xsl:text>.</xsl:text>
				<xsl:value-of select="$date_month" />
				<xsl:text>.</xsl:text>
				<xsl:value-of select="$date_day" />
				<xsl:text>, </xsl:text>
				<xsl:value-of select="$time_hour" />
				<xsl:text>:</xsl:text>
				<xsl:value-of select="$time_minute" />
				<xsl:text>)</xsl:text>
			</xsl:attribute>
			<xsl:text>Version </xsl:text>
			<xsl:value-of select="$version" />
		</div>
	</xsl:template>
</xsl:stylesheet>
