<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="param" mode="method-argument">
		<xsl:param name="had-argument" />
		<xsl:variable name="nullable">
			<xsl:choose>
				<xsl:when test="@nullable = 'true'">true</xsl:when>
				<xsl:when test="@nullable = 'false'">false</xsl:when>
				<xsl:when test="string-length(@nullable) &lt; 1">true</xsl:when>
				<xsl:otherwise>
					<xsl:message terminate="yes">
						<xsl:text>The value '</xsl:text>
						<xsl:value-of select="@type" />
						<xsl:text>' is not allowed for the 'nullable' attribute.</xsl:text>
					</xsl:message>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:if test="@name = 'id'">
			<xsl:message terminate="yes">Parameter cannot be called 'id'.</xsl:message>
		</xsl:if>
		<xsl:if test="@name = 'exception'">
			<xsl:message terminate="yes">Parameter cannot be called 'exception'.</xsl:message>
		</xsl:if>
		<xsl:if test="(position() &gt; 1) or ($had-argument = 'true')">, </xsl:if>
		<xsl:choose>
			<xsl:when test="(@type = 'text') or (string-length(@type) &lt; 1)">
				<xsl:text>java.lang.String</xsl:text>
			</xsl:when>
			<xsl:when test="@type = 'object'">
				<xsl:text>java.lang.Object</xsl:text>
			</xsl:when>
			<xsl:when test="(@type = 'int64') and ($nullable = 'true')">
				<xsl:text>java.lang.Long</xsl:text>
			</xsl:when>
			<xsl:when test="@type = 'int64'">
				<xsl:text>long</xsl:text>
			</xsl:when>
			<xsl:when test="(@type = 'int32') and ($nullable = 'true')">
				<xsl:text>java.lang.Integer</xsl:text>
			</xsl:when>
			<xsl:when test="@type = 'int32'">
				<xsl:text>int</xsl:text>
			</xsl:when>
			<xsl:when test="(@type = 'int16') and ($nullable = 'true')">
				<xsl:text>java.lang.Short</xsl:text>
			</xsl:when>
			<xsl:when test="@type = 'int16'">
				<xsl:text>short</xsl:text>
			</xsl:when>
			<xsl:when test="(@type = 'int8') and ($nullable = 'true')">
				<xsl:text>java.lang.Byte</xsl:text>
			</xsl:when>
			<xsl:when test="@type = 'int8'">
				<xsl:text>byte</xsl:text>
			</xsl:when>
			<xsl:when test="(@type = 'boolean') and ($nullable = 'true')">
				<xsl:text>java.lang.Boolean</xsl:text>
			</xsl:when>
			<xsl:when test="@type = 'boolean'">
				<xsl:text>boolean</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:message terminate="yes">
					<xsl:text>The type '</xsl:text>
					<xsl:value-of select="@type" />
					<xsl:text>' is unknown.</xsl:text>
				</xsl:message>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text> </xsl:text>
		<xsl:value-of select="@name" />
	</xsl:template>
</xsl:stylesheet>
