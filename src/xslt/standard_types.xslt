<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 XSLT that converts a standard types to something else.

 $Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:include href="hungarian.xslt" />

	<xsl:template name="description_for_standardtype">
		<xsl:param name="type" />

		<xsl:choose>
			<xsl:when test="$type = '_text'">Plain text.</xsl:when>
			<xsl:when test="$type = '_properties'">Set of properties.</xsl:when>
			<xsl:when test="$type = '_date'">Date, in the format YYYYMMDD.</xsl:when>
			<xsl:when test="$type = '_timestamp'">Timestamp, in the format YYYYMMDDhhmmss.</xsl:when>
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
			<xsl:when test="$requiredBool = 'false'">
				<xsl:choose>
					<xsl:when test="$type = '_text'">java.lang.String</xsl:when>
					<xsl:when test="$type = '_properties'">org.xins.common.collections.PropertyReader</xsl:when>
					<xsl:when test="$type = '_date'">org.xins.common.types.standard.Date.Value</xsl:when>
					<xsl:when test="$type = '_timestamp'">org.xins.common.types.standard.Timestamp.Value</xsl:when>
					<xsl:when test="$type = '_boolean'">java.lang.Boolean</xsl:when>
					<xsl:when test="$type = '_int8'">java.lang.Byte</xsl:when>
					<xsl:when test="$type = '_int16'">java.lang.Short</xsl:when>
					<xsl:when test="$type = '_int32'">java.lang.Integer</xsl:when>
					<xsl:when test="$type = '_int64'">java.lang.Long</xsl:when>
					<xsl:otherwise>
						<xsl:message terminate="yes">
							<xsl:text>The type '</xsl:text>
							<xsl:value-of select="$type" />
							<xsl:text>' is not recognized as a standard type.</xsl:text>
						</xsl:message>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise> <!-- $requiredBool = 'true' -->
				<xsl:choose>
					<xsl:when test="$type = '_text'">java.lang.String</xsl:when>
					<xsl:when test="$type = '_properties'">org.xins.common.collections.PropertyReader</xsl:when>
					<xsl:when test="$type = '_date'">org.xins.common.types.standard.Date.Value</xsl:when>
					<xsl:when test="$type = '_timestamp'">org.xins.common.types.standard.Timestamp.Value</xsl:when>
					<xsl:when test="$type = '_boolean'">boolean</xsl:when>
					<xsl:when test="$type = '_int8'">byte</xsl:when>
					<xsl:when test="$type = '_int16'">short</xsl:when>
					<xsl:when test="$type = '_int32'">int</xsl:when>
					<xsl:when test="$type = '_int64'">long</xsl:when>
					<xsl:otherwise>
						<xsl:message terminate="yes">
							<xsl:text>The type '</xsl:text>
							<xsl:value-of select="$type" />
							<xsl:text>' is not recognized as a standard type.</xsl:text>
						</xsl:message>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="javatypeclass_for_standardtype">
		<xsl:param name="type" />

		<xsl:text>org.xins.common.types.standard.</xsl:text>
		<xsl:call-template name="hungarianUpper">
			<xsl:with-param name="text">
				<xsl:value-of select="substring($type, 2)" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="javatype_from_string_for_standardtype">
		<xsl:param name="required" />
		<xsl:param name="type"     />
		<xsl:param name="variable" />

		<xsl:variable name="javatypeclass">
			<xsl:call-template name="javatypeclass_for_standardtype">
				<xsl:with-param name="type" select="$type" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:choose>
			<xsl:when test="$required = 'true'">
				<xsl:value-of select="$javatypeclass" />
				<xsl:text>.fromStringForRequired(</xsl:text>
				<xsl:value-of select="$variable" />
				<xsl:text>)</xsl:text>
			</xsl:when>
			<xsl:otherwise> <!-- $required = 'false' -->
				<xsl:value-of select="$javatypeclass" />
				<xsl:text>.fromStringForOptional(</xsl:text>
				<xsl:value-of select="$variable" />
				<xsl:text>)</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="javatype_to_string_for_standardtype">
		<xsl:param name="required" />
		<xsl:param name="type"     />
		<xsl:param name="variable" />

		<xsl:variable name="javatypeclass">
			<xsl:call-template name="javatypeclass_for_standardtype">
				<xsl:with-param name="type" select="$type" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:choose>
			<xsl:when test="$type = '_text'">
				<xsl:value-of select="$variable" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$javatypeclass" />
				<xsl:text>.toString(</xsl:text>
				<xsl:value-of select="$variable" />
				<xsl:text>)</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
