<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template name="description_for_standardtype">
		<xsl:param name="type" />

		<xsl:choose>
			<xsl:when test="$type = '_text'">Plain text.</xsl:when>
			<xsl:when test="$type = '_boolean'">Boolean. Can be either true or false.</xsl:when>
			<xsl:when test="$type = '_int8'">Signed integer number, 8 bit.</xsl:when>
			<xsl:when test="$type = '_int16'">Signed integer number, 16 bit.</xsl:when>
			<xsl:when test="$type = '_int32'">Signed integer number, 32 bit.</xsl:when>
			<xsl:when test="$type = '_int64'">Signed integer number, 64 bit.</xsl:when>
			<xsl:otherwise>
				<xsl:message terminate="yes">
					<xsl:text>The type '</xsl:text>
					<xsl:value-of select="$type" />
					<xsl:text>' is not recognized as a standard type.</xsl:text>
				</xsl:message>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="javatype_for_standardtype">
		<xsl:param name="type"     />
		<xsl:param name="required" />

		<xsl:variable name="requiredBool">
			<xsl:choose>
				<xsl:when test="string-length($required) = 0">
					<xsl:text>false</xsl:text>
				</xsl:when>
				<xsl:when test="$required = 'false' or $required = 'true'">
					<xsl:value-of select="$required" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:message terminate="yes">
						<xsl:text>The parameter 'required' should be either 'true' or 'false', not '</xsl:text>
						<xsl:value-of select="$required" />
						<xsl:text>'.</xsl:text>
					</xsl:message>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:choose>
			<xsl:when test="$required = 'true'">
				<xsl:choose>
					<xsl:when test="$type = '_text'">java.lang.String</xsl:when>
					<xsl:when test="$type = '_boolean'">java.lang.Boolean</xsl:when>
					<xsl:when test="$type = '_int8'">java.lang.Byte</xsl:when>
					<xsl:when test="$type = '_int16'">java.lang.Short</xsl:when>
					<xsl:when test="$type = '_int32'">java.lang.Integer</xsl:when>
					<xsl:when test="$type = '_int64'">java.lang.Long</xsl:when>
					<xsl:otherwise>
						<xsl:message terminate="yes">
							<xsl:text>The type '</xsl:text>
							<xsl:value-of select="$required" />
							<xsl:text>' is not recognized as a standard type.</xsl:text>
						</xsl:message>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="$type = '_text'">java.lang.String</xsl:when>
					<xsl:when test="$type = '_boolean'">boolean</xsl:when>
					<xsl:when test="$type = '_int8'">byte</xsl:when>
					<xsl:when test="$type = '_int16'">short</xsl:when>
					<xsl:when test="$type = '_int32'">int</xsl:when>
					<xsl:when test="$type = '_int64'">long</xsl:when>
					<xsl:otherwise>
						<xsl:message terminate="yes">
							<xsl:text>The type '</xsl:text>
							<xsl:value-of select="$required" />
							<xsl:text>' is not recognized as a standard type.</xsl:text>
						</xsl:message>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
