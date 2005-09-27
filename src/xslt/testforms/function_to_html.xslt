<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 XSLT that generates the function-testform-environment HTML form.
 This form is use to test an API on a given environment.

 $Id$

 Copyright 2003-2005 Wanadoo Nederland B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Define parameters -->
	<xsl:param name="xins_version" />
	<xsl:param name="project_home" />
	<xsl:param name="project_file" />
	<xsl:param name="specsdir"     />
	<xsl:param name="api"          />
	<xsl:param name="api_file"     />
	<xsl:param name="env_file"     />

	<xsl:include href="../footer.xslt" />
	<xsl:include href="../header.xslt" />
	<xsl:include href="../types.xslt" />

	<xsl:output
	method="html"
	indent="yes"
	encoding="US-ASCII"
	doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
	doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
	omit-xml-declaration="yes" />

	<xsl:template match="function">

		<xsl:variable name="functionName" select="@name" />
		<xsl:variable name="init_environment">
			<xsl:choose>
				<xsl:when test="string-length($env_file) > 0">
					<xsl:value-of select="document($env_file)/environments/environment[1]/@url" />
				</xsl:when>
				<xsl:when test="document($api_file)/api/environment">
					<xsl:value-of select="document($api_file)/api/environment[1]/@url" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>http://API_PATH</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
			<head>
				<title>
					<xsl:value-of select="$functionName" />
					<xsl:text> test form</xsl:text>
				</title>
				<link rel="stylesheet" type="text/css" href="style.css" />
				<link rel="top" href="../index.html" title="API index" />
				<xsl:call-template name="javascript" />
			</head>
			<body>
				<xsl:call-template name="header">
					<xsl:with-param name="active">testform</xsl:with-param>
					<xsl:with-param name="name" select="$functionName" />
				</xsl:call-template>

				<h1>
					<xsl:text>Function </xsl:text>
					<em>
						<xsl:value-of select="$functionName" />
					</em>
					<xsl:text> test form</xsl:text>
				</h1>

				<form method="GET" action="{$init_environment}" target="xmlOutputFrame" 
				onsubmit="this.action=this._environment.value;return doRequest(this)">
					<p>
						<input name="_function" type="hidden">
							<xsl:attribute name="value">
								<xsl:value-of select="$functionName" />
							</xsl:attribute>
						</input>
						<input name="_convention" value="_xins-std" type="hidden" />
						<xsl:text>Execution environment: </xsl:text>
						<select name="_environment" class="required">
							<xsl:choose>
								<xsl:when test="string-length($env_file) > 0">
									<xsl:for-each select="document($env_file)/environments/environment">
										<option value="{@url}">
											<xsl:value-of select="@id" />
										</option>
									</xsl:for-each>
								</xsl:when>
								<xsl:when test="document($api_file)/api/environment">
									<xsl:for-each select="document($api_file)/api/environment">
										<option value="{@url}">
											<xsl:value-of select="@id" />
										</option>
									</xsl:for-each>
								</xsl:when>
								<xsl:otherwise>
									<option value="API_PATH">No environment</option>
								</xsl:otherwise>
							</xsl:choose>
						</select>
					</p>

					<xsl:call-template name="input_section">
						<xsl:with-param name="functionName" select="$functionName" />
					</xsl:call-template>
				</form>
				<div class="url" style="padding-bottom: 5pt" id="query">
					<xsl:text> </xsl:text>
				</div>
				<iframe height="500" id="xmlOutputFrame" name="xmlOutputFrame" src="about:blank" width="100%"></iframe>
				<xsl:call-template name="footer">
					<xsl:with-param name="xins_version" select="$xins_version" />
				</xsl:call-template>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="em">
		<em>
			<xsl:apply-templates />
		</em>
	</xsl:template>

	<xsl:template match="strong">
		<strong>
			<xsl:apply-templates />
		</strong>
	</xsl:template>

	<xsl:template match="list">
		<ul>
			<xsl:apply-templates />
		</ul>
	</xsl:template>

	<xsl:template match="list/item">
		<li>
			<xsl:apply-templates />
		</li>
	</xsl:template>

	<xsl:template name="input_section">
		<xsl:param name="functionName" />

		<h2>Test form</h2>
		<xsl:choose>
			<xsl:when test="input/param or input/data/element">
				<table>
					<xsl:apply-templates select="input/param" />
					<xsl:apply-templates select="input/data/element" />
					<tr>
						<td align="right" colspan="2">
							<input type="submit" value="Submit" />
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<hr />
						</td>
					</tr>
				</table>
			</xsl:when>
			<xsl:otherwise>
				<em>This function supports no input parameters.</em>
				<p>
					<input type="submit" value="Submit" />
				</p>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="param">

		<xsl:variable name="type">
			<xsl:choose>
				<xsl:when test="@type">
					<xsl:value-of select="@type" />
				</xsl:when>
				<xsl:otherwise>_text</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="type_file" select="concat($specsdir, '/', $type, '.typ')" />

		<xsl:variable name="isenum" select="not(starts-with($type, '_')) and boolean(document($type_file)/type/enum)" />

		<!-- TODO: Deprecated parameters -->

		<tr>
			<td class="name">
				<span>
					<xsl:if test="boolean(description/text())">
						<xsl:attribute name="title">
							<xsl:call-template name="firstline">
								<xsl:with-param name="text" select="description/text()" />
							</xsl:call-template>
						</xsl:attribute>
					</xsl:if>
					<xsl:value-of select="@name" />
				</span>
				<xsl:text> (</xsl:text>
				<xsl:call-template name="typelink">
					<xsl:with-param name="api"      select="$api" />
					<xsl:with-param name="specsdir" select="$specsdir" />
					<xsl:with-param name="type"     select="$type" />
				</xsl:call-template>
				<xsl:text>)</xsl:text>
			</td>
			<td class="value">
				<xsl:choose>
					<xsl:when test="$isenum">
						<select name="{@name}">
							<xsl:attribute name="class">
								<xsl:choose>
									<xsl:when test="@required = 'true'">required</xsl:when>
									<xsl:otherwise>optional</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
							<option></option>
							<xsl:for-each select="document($type_file)/type/enum/item">
								<option value="{@value}">
									<xsl:value-of select="@value" />
								</option>
							</xsl:for-each>
						</select>
					</xsl:when>
					<xsl:when test="$type = '_boolean'">
						<select name="{@name}">
							<option></option>
							<option value="true">true</option>
							<option value="false">false</option>
						</select>
					</xsl:when>
					<xsl:otherwise>
						<input type="text" name="{@name}">
							<xsl:attribute name="class">
								<xsl:choose>
									<xsl:when test="@required = 'true'">required</xsl:when>
									<xsl:otherwise>optional</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
						</input>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:if test="@required = 'true'"> *</xsl:if>
			</td>
		</tr>
	</xsl:template>

	<!--
		Write the row for the data section.
	-->
	<xsl:template match="element">
		<tr>
			<td class="name">
				<span title="data section of the request">
					Data section
				</span> (_text)
			</td>
			<td class="value">
				<input type="text" name="_data" class="optional" />
			</td>
		</tr>
	</xsl:template>
	
	<!--
		Write the Javascript section
	-->
	<xsl:template name="javascript">
		<script type="text/javascript">
			function doRequest(form) {
				var elems = form.elements;
				var iframe = document.getElementById('xmlOutputFrame');
				var querySpan = document.getElementById('query');
				var requestParams = [];
				var formattedRequestString = '';
				var value, name, requestString;

				iframe.src = "about:blank";
				for (var i = 0; i != elems.length; i++) {
					if (!(name = elems[i].name) || name == '_environment') {
						continue;
					}

					if (elems[i].type == 'text' || elems[i].type == 'hidden') {
						value = elems[i].value;
					} else if (elems[i].type == 'select-one') {
						value = elems[i].options[elems[i].selectedIndex].value;
					}

					if (value) {
						value = escape(value);
						requestParams[requestParams.length] = name + '=' + value;
						if (formattedRequestString) {
							formattedRequestString += '&amp;amp;';
						}

						if (name == '_function') {
							formattedRequestString += '&lt;span class="functionparam"&gt;';
						} else {
							formattedRequestString += '<span class="param">';
						}

						formattedRequestString += '<span class="name">' + name + '</span>';
						formattedRequestString += '=<span class="value">' + value + '</span>';
						formattedRequestString += '</span>';
					}	
				}

				requestString = form.action + '?' + requestParams.join('&amp;');
				formattedRequestString = form.action + '?' + formattedRequestString;

				iframe.src = requestString;
				querySpan.innerHTML = formattedRequestString;
				return false;
			}
		</script>
	</xsl:template>
</xsl:stylesheet>
