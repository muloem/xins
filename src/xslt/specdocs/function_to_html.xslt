<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Define parameters -->
	<xsl:param name="xins_version" />
	<xsl:param name="project_home" />
	<xsl:param name="project_file" />
	<xsl:param name="specsdir"     />
	<xsl:param name="api"          />
	<xsl:param name="api_file"     />

	<!-- Perform includes -->
	<xsl:include href="../header.xslt"    />
	<xsl:include href="../footer.xslt"    />
	<xsl:include href="../function.xslt"  />
	<xsl:include href="../types.xslt"     />
	<xsl:include href="../urlencode.xslt" />

	<xsl:variable name="resultcodes_file" select="'../../xml/default_resultcodes.xml'"                       />
	<xsl:variable name="cvsweb_url"       select="document($project_file)/project/cvsweb/@href"              />
	<xsl:variable name="function_name"    select="//function/@name"                                          />
	<xsl:variable name="function_file"    select="concat($specsdir, '/', $api, '/', $function_name, '.fnc')" />
	<xsl:variable name="sessionBased">
		<xsl:for-each select="//function">
			<xsl:call-template name="is_function_session_based" />
		</xsl:for-each>
	</xsl:variable>

	<xsl:output
	method="xml"
	indent="no"
	encoding="US-ASCII"
	doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
	doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
	omit-xml-declaration="yes" />

	<xsl:preserve-space elements="function/examples" />

	<!-- Default indentation setting -->
	<xsl:variable name="indentation" select="'&amp;nbsp;&amp;nbsp;&amp;nbsp;'" />

	<xsl:template match="function">

		<xsl:if test="not(@name)">
			<xsl:message terminate="yes">
				<xsl:text>Function does not specify the mandatory 'name' attribute.</xsl:text>
			</xsl:message>
		</xsl:if>

		<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
			<head>
				<title>
					<xsl:value-of select="$function_name" />
				</title>

				<meta name="generator" content="XINS" />

				<link rel="stylesheet" type="text/css" href="style.css"                               />
				<link rel="top"                        href="../index.html" title="API index"            />
				<link rel="up"                         href="index.html"    title="Overview of this API" />
			</head>
			<body>
				<xsl:call-template name="header">
					<xsl:with-param name="active">function</xsl:with-param>
				</xsl:call-template>

				<h1>
					<xsl:text>Function </xsl:text>
					<em>
						<xsl:value-of select="$function_name" />
					</em>
				</h1>

				<!-- Broken freezes -->
				<xsl:call-template name="broken_freeze" />

				<!-- Description -->
				<xsl:call-template name="description" />

				<!-- Session-based -->
				<xsl:if test="$sessionBased = 'true'">
					<p />
					<em>This function is session-based.</em>
				</xsl:if>

				<!-- References to other functions -->
				<xsl:if test="see">
					<table class="metadata">
						<tr>
							<td class="key">See also:</td>
							<td class="value">
								<xsl:apply-templates select="see" />
							</td>
						</tr>
					</table>
				</xsl:if>

				<xsl:call-template name="input_section" />
				<xsl:call-template name="output_section" />
				<xsl:call-template name="testforms_section" /> 
				<xsl:call-template name="examples_section" /> 
				<xsl:call-template name="footer">
					<xsl:with-param name="xins_version" select="$xins_version" />
				</xsl:call-template>
			</body>
		</html>
	</xsl:template>

	<xsl:template name="input_section">
		<h2>Input section</h2>
		<blockquote>
			<xsl:choose>
				<xsl:when test="input">
					<xsl:apply-templates select="input" />
				</xsl:when>
				<xsl:otherwise>
					<em>This function supports no input parameters.</em>
				</xsl:otherwise>
			</xsl:choose>
		</blockquote>
	</xsl:template>

	<xsl:template name="output_section">
		<h2>Output section</h2>
		<blockquote>
			<xsl:call-template name="resultcodes" />
			<xsl:choose>
				<xsl:when test="count(output) &gt; 1">
					<xsl:message terminate="yes">
						<xsl:text>Found </xsl:text>
						<xsl:value-of select="count(output)" />
						<xsl:text> output sections. Only one is allowed.</xsl:text>
					</xsl:message>
				</xsl:when>
				<xsl:when test="output">
						<xsl:apply-templates select="output" />
				</xsl:when>
				<xsl:otherwise>
					<em>This function supports no output parameters nor data section.</em>
				</xsl:otherwise>
			</xsl:choose>
		</blockquote>
	</xsl:template>

	<xsl:template name="testforms_section">
		<xsl:if test="boolean(document($api_file)/api/environment)">
			<h2>Test forms</h2>
			<ul>
				<xsl:for-each select="document($api_file)/api/environment">
					<li>
						<a>
							<xsl:attribute name="href">
								<xsl:value-of select="$function_name" />
								<xsl:text>-testform-</xsl:text>
								<xsl:value-of select="@id" />
								<xsl:text>.html</xsl:text>
							</xsl:attribute>

							<xsl:value-of select="@id" />
						</a>
					</li>
				</xsl:for-each>
			</ul>
		</xsl:if>
	</xsl:template>

	<xsl:template name="examples_section">
		<h2>Examples section</h2>
		<blockquote>
			<xsl:choose>
				<xsl:when test="example">
					<table class="example">
						<xsl:apply-templates select="example" />
					</table>
				</xsl:when>
				<xsl:otherwise>
					<em>No examples available.</em>
				</xsl:otherwise>
			</xsl:choose>
		</blockquote>
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

	<xsl:template match="function/input">
		<xsl:call-template name="parametertable">
			<xsl:with-param name="title">Input parameters</xsl:with-param>
			<xsl:with-param name="content">input parameters</xsl:with-param>
			<xsl:with-param name="class">inputparameters</xsl:with-param>
		</xsl:call-template>
		<xsl:apply-templates select="note" />
		<xsl:if test="param-combo[count(param-ref) = 0]">
			<xsl:message terminate="yes">Found param-combo with no param-ref children.</xsl:message>
		</xsl:if>
		<xsl:call-template name="additional-constraints" />
	</xsl:template>

	<xsl:template name="additional-constraints">
		<xsl:if test="param-combo">
			<h4>Additional constraints</h4>
			<xsl:text>The following </xsl:text>
			<xsl:choose>
				<xsl:when test="count(param-combo) &lt; 2">
					<xsl:text>constraint applies</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>constraints apply</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:text> to the input parameters, additional to the input parameters marked as required. A violation of </xsl:text>
			<xsl:choose>
				<xsl:when test="count(param-combo) &lt; 2">
					<xsl:text>this constraint</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>any of these constraints</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:text> will result in an unsuccessful result with code </xsl:text>
			<em>InvalidParameters</em>
			<xsl:text>.</xsl:text>
			<ul>
				<xsl:apply-templates select="param-combo" />
			</ul>
		</xsl:if>
	</xsl:template>

	<xsl:template match="input/param-combo[@type='exclusive-or']">
		<li>
			<em>Exactly</em>
			<xsl:text> one of these parameters must be set: </xsl:text>
			<xsl:apply-templates select="." mode="textlist" />
			<xsl:text>.</xsl:text>
		</li>
	</xsl:template>

	<xsl:template match="input/param-combo[@type='inclusive-or']">
		<li>
			<xsl:text>At </xsl:text>
			<em>least</em>
			<xsl:text> one of these parameters must be set: </xsl:text>
			<xsl:apply-templates select="." mode="textlist" />
			<xsl:text>.</xsl:text>
		</li>
	</xsl:template>

	<xsl:template match="input/param-combo[@type='all-or-none']">
		<li>
			<xsl:text>Either </xsl:text>
			<em>all</em>
			<xsl:text> of these parameters must be set, or </xsl:text>
			<em>none</em>
			<xsl:text> of them can be set: </xsl:text>
			<xsl:apply-templates select="." mode="textlist" />
			<xsl:text>.</xsl:text>
		</li>
	</xsl:template>

	<xsl:template match="input/param-combo" priority="-1">
		<xsl:message terminate="yes">Unrecognised type of param-combo.</xsl:message>
	</xsl:template>

	<xsl:template match="input/param-combo" mode="textlist">
		<xsl:variable name="count" select="count(param-ref)" />
		<xsl:for-each select="param-ref">
			<xsl:choose>
				<xsl:when test="position() = $count">
					<xsl:text> and </xsl:text>
				</xsl:when>
				<xsl:when test="position() &gt; 1">
					<xsl:text>, </xsl:text>
				</xsl:when>
			</xsl:choose>
			<em>
				<xsl:value-of select="@name" />
			</em>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="input/param-combo" priority="-1">
		<xsl:message terminate="yes">Unrecognised type of param-combo.</xsl:message>
	</xsl:template>

	<xsl:template match="function/output">
		<xsl:call-template name="parametertable">
			<xsl:with-param name="title">Output parameters</xsl:with-param>
			<xsl:with-param name="content">output parameters</xsl:with-param>
			<xsl:with-param name="class">outputparameters</xsl:with-param>
		</xsl:call-template>
		
		<xsl:call-template name="datasection" />
	</xsl:template>

	<xsl:template match="function/example">

		<xsl:variable name="examplenum"           select="@num" />
		<xsl:variable name="example-inputparams"  select="//function/input/param/example-value[@example=$examplenum]" />
		<xsl:variable name="example-outputparams" select="//function/output/param/example-value[@example=$examplenum]" />
		<xsl:variable name="resultcode">
			<xsl:choose>
				<xsl:when test="@returncode">
					<xsl:value-of select="@returncode" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="@resultcode" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="isgenericresultcode">
			<xsl:choose>
				<xsl:when test="document($resultcodes_file)/resultcodes/code[@value=$resultcode]">true</xsl:when>
				<xsl:otherwise>false</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="isapiresultcode">
			<xsl:choose>
				<xsl:when test="document($api_file)/api/resultcode[@name=$resultcode]">true</xsl:when>
				<xsl:otherwise>false</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="isfunctionresultcode">
			<xsl:choose>
				<xsl:when test="boolean(parent::function/output/resultcode[@value=$resultcode])">true</xsl:when>
				<xsl:otherwise>false</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="success">
			<xsl:choose>

				<!-- No result code -->
				<!-- TODO: Silently expect success -->
				<xsl:when test="string-length($resultcode) &lt; 1">
					<xsl:if test="not(boolean(@success))">
						<xsl:message terminate="yes">
							<xsl:text>Example </xsl:text>
							<xsl:value-of select="$examplenum" />
							<xsl:text> does not define if it is successful or not nor does it specify a result code.</xsl:text>
						</xsl:message>
					</xsl:if>
					<xsl:value-of select="@success" />
				</xsl:when>

				<!-- Have result code -->
				<xsl:otherwise>false</xsl:otherwise>

				<!-- TODO: Check that the result code is not defined in 2 places? -->
				<!-- TODO: Check that the result code exists? -->
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="sessionID">
			<xsl:if test="$sessionBased = 'true'">
				<xsl:if test="string-length(@sessionID) = 0">
					<xsl:message terminate="yes">
						<xsl:text>No sessionID specified for example </xsl:text>
						<xsl:value-of select="$examplenum" />
						<xsl:text>.</xsl:text>
					</xsl:message>
				</xsl:if>
				<xsl:value-of select="@sessionID" />
			</xsl:if>
		</xsl:variable>

		<xsl:if test="not($success='true' or $success='false')">
			<xsl:message terminate="yes">
				<xsl:text>Example </xsl:text>
				<xsl:value-of select="$examplenum" />
				<xsl:text> has an invalid success setting: '</xsl:text>
				<xsl:value-of select="$success" />
				<xsl:text>'. It should be either 'true' or 'false'.</xsl:text>
			</xsl:message>
		</xsl:if>

		<xsl:if test="@success and @resultcode">
			<xsl:message>
				<xsl:text>Example </xsl:text>
				<xsl:value-of select="$examplenum" />
				<xsl:text> defines both the success ('</xsl:text>
				<xsl:value-of select="@success" />
				<xsl:text>') and the result code ('</xsl:text>
				<xsl:value-of select="@resultcode" />
				<xsl:text>') attributes. Only the result code needs to be specified. The success indication can be determined from that.</xsl:text>
			</xsl:message>
		</xsl:if>

		<xsl:if test="$success = 'true'">
			<!--
			If this is an example of a successful case, then all required
			input parameters need to be set.
			-->
			<xsl:for-each select="parent::function/input/param[@required='true']">
				<xsl:variable name="required_attr">
					<xsl:value-of select="@name" />
				</xsl:variable>
				<xsl:if test="not(boolean(/function/input/param[@name=$required_attr]/example-value[@example=$examplenum]))">
					<xsl:message terminate="yes">
						<xsl:text>Example </xsl:text>
						<xsl:value-of select="$examplenum" />
						<xsl:text> is marked as successful, but it does not specify a value for the required input parameter '</xsl:text>
						<xsl:value-of select="$required_attr" />
						<xsl:text>'.</xsl:text>
					</xsl:message>
				</xsl:if>
			</xsl:for-each>

			<!--
			Same applies to output parameters.
			-->
			<xsl:for-each select="parent::function/output/param[@required='true']">
				<xsl:variable name="required_attr">
					<xsl:value-of select="@name" />
				</xsl:variable>
				<xsl:if test="not(boolean(/function/output/param[@name=$required_attr]/example-value[@example=$examplenum]))">
					<xsl:message terminate="yes">
						<xsl:text>Example </xsl:text>
						<xsl:value-of select="$examplenum" />
						<xsl:text> is marked as successful, but it does not specify a value for the required output parameter '</xsl:text>
						<xsl:value-of select="$required_attr" />
						<xsl:text>'.</xsl:text>
					</xsl:message>
				</xsl:if>
			</xsl:for-each>
		</xsl:if>

		<tr>
			<td colspan="2" class="header">
				<h3>
					<xsl:text>Example </xsl:text>
					<xsl:value-of select="@num" />

					<xsl:if test="description">
						<xsl:text>: </xsl:text>
						<xsl:apply-templates select="description" />
					</xsl:if>
				</h3>
			</td>
		</tr>
		<tr>
			<th>Request:</th>
			<td>
				<span class="url">
					<span class="protocol">http</span>
					<xsl:text>://</xsl:text>
					<span class="host">API_PATH</span>
					<xsl:text>?</xsl:text>
					<span class="functionparam">
						<span class="name">_function</span>
						<xsl:text>=</xsl:text>
						<span class="value">
							<xsl:value-of select="$function_name" />
						</span>
					</span>
					<xsl:if test="$sessionBased = 'true'">
						<xsl:text>&amp;</xsl:text>
						<span class="param">
							<span class="name">_session</span>
						</span>
						<xsl:text>=</xsl:text>
						<span class="value">
							<xsl:value-of select="$sessionID" />
						</span>
					</xsl:if>
					<xsl:for-each select="$example-inputparams">
						<xsl:text>&amp;</xsl:text>
						<span class="param">
							<xsl:attribute name="title">
								<xsl:value-of select="../@name" />
								<xsl:text>: </xsl:text>
								<xsl:value-of select="text()" />
							</xsl:attribute>
							<span class="name">
								<xsl:value-of select="../@name" />
							</span>
							<xsl:text>=</xsl:text>
							<span class="value">
								<xsl:call-template name="urlencode">
									<xsl:with-param name="text">
										<xsl:value-of select="text()" />
									</xsl:with-param>
								</xsl:call-template>
							</span>
						</span>
					</xsl:for-each>
				</span>
			</td>
		</tr>
		<tr>
			<th>Response:</th>
			<td>
				<span class="xml">
					<span class="decl">
						<xsl:text>&lt;?</xsl:text>
						<span class="elem">
							<span class="name">xml</span>
						</span>
						<xsl:text> </xsl:text>
						<span class="attr">
							<span class="name">version</span>
							<xsl:text>=</xsl:text>
							<span class="value">"1.0"</span>
						</span>
						<xsl:text>?&gt;</xsl:text>
					</span>

					<br />

					<!-- The <result/> element -->
					<span class="elem">
						<xsl:text>&lt;</xsl:text>
						<span class="name">result</span>
						<xsl:text> </xsl:text>
						<span class="attr">
							<span class="name">success</span>
							<xsl:text>=</xsl:text>
							<span class="value">
								<xsl:text>"</xsl:text>
								<xsl:value-of select="$success" />
								<xsl:text>"</xsl:text>
							</span>
						</span>
						<xsl:if test="string-length($resultcode) &gt; 0">
							<xsl:text> </xsl:text>
							<span class="attr">
								<!-- TODO: Get result code description for referenced result codes as well -->
								<xsl:attribute name="title">
									<xsl:call-template name="firstline">
										<xsl:with-param name="text">
											<xsl:value-of select="parent::function/output/resultcode[@value=$resultcode]/description/text()" />
										</xsl:with-param>
									</xsl:call-template>
								</xsl:attribute>
								<span class="name">code</span>
								<xsl:text>=</xsl:text>
								<span class="value">
									<xsl:text>"</xsl:text>
									<xsl:value-of select="$resultcode" />
									<xsl:text>"</xsl:text>
								</span>
							</span>
						</xsl:if>
						<xsl:choose>
							<xsl:when test="$example-outputparams or data-example">
								<xsl:text>&gt;</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text> /&gt;</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</span>

					<xsl:choose>
						<xsl:when test="$example-outputparams or data-example">
							<xsl:if test="$example-outputparams">
								<xsl:for-each select="$example-outputparams">
									<br />
									<xsl:value-of disable-output-escaping="yes" select="$indentation" />
									<span class="elem">
										<xsl:text>&lt;</xsl:text>
										<span class="name">param</span>
										<xsl:text> </xsl:text>
										<span class="attr">
											<span class="name">name</span>
											<xsl:text>=</xsl:text>
											<span class="value">
												<xsl:text>"</xsl:text>
												<xsl:value-of select="../@name" />
												<xsl:text>"</xsl:text>
											</span>
										</span>
										<xsl:text>&gt;</xsl:text>
									</span>
									<span class="pcdata">
										<xsl:apply-templates select="." />
									</span>
									<span class="elem">
										<xsl:text>&lt;/</xsl:text>
										<span class="name">param</span>
										<xsl:text>&gt;</xsl:text>
									</span>
								</xsl:for-each>
							</xsl:if>
							<xsl:if test="data-example">
								<br />
								<xsl:value-of disable-output-escaping="yes" select="$indentation" />
								<span class="elem">
									<xsl:text>&lt;</xsl:text>
									<span class="name">data</span>
									<xsl:text>&gt;</xsl:text>
								</span>

								<!-- First call, use $indent to set the start value of the indent param -->
								<xsl:apply-templates select="data-example/element-example">
									<!-- Insert the indentation -->
									<xsl:with-param name="indent" select="concat($indentation,$indentation)" />
								</xsl:apply-templates>
								
								<br />
								<xsl:value-of disable-output-escaping="yes" select="$indentation" />
								<span class="elem">
									<xsl:text>&lt;/</xsl:text>
									<span class="name">data</span>
									<xsl:text>&gt;</xsl:text>
								</span>
							</xsl:if>
							<br />
							<span class="elem">
								<xsl:text>&lt;/</xsl:text>
								<span class="name">result</span>
								<xsl:text>&gt;</xsl:text>
							</span>
						</xsl:when>
					</xsl:choose>
				</span>
			</td>
		</tr>
		<tr>
			<th>Test on:</th>
			<td>
				<xsl:for-each select="document($api_file)/api/environment">
					<a>
						<xsl:attribute name="href">
							<xsl:value-of select="@url" />
							<xsl:text>?_function=</xsl:text>
							<xsl:value-of select="$function_name" />
							<xsl:if test="$sessionBased = 'true'">
								<xsl:text>&amp;_session=</xsl:text>
								<xsl:value-of select="$sessionID" />
							</xsl:if>
							<xsl:for-each select="$example-inputparams">
								<xsl:text>&amp;</xsl:text>
								<xsl:attribute name="title">
									<xsl:value-of select="../@name" />
									<xsl:text>: </xsl:text>
									<xsl:value-of select="text()" />
								</xsl:attribute>
								<xsl:value-of select="../@name" />
								<xsl:text>=</xsl:text>
								<xsl:call-template name="urlencode">
									<xsl:with-param name="text">
										<xsl:value-of select="text()" />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:for-each>
						</xsl:attribute>

						<xsl:value-of select="@id" />
					</a>
					<xsl:text> </xsl:text>
				</xsl:for-each>
			</td>
		</tr>
	</xsl:template>

<!-- element examples -->

	<xsl:template match="element-example">
		<xsl:param name="indent" />

		<!-- TODO: Support pcdata-example -->
		<xsl:variable name="text">
			<xsl:value-of select="normalize-space(text())" />
		</xsl:variable>

		<br />
		<xsl:value-of disable-output-escaping="yes" select="$indent" />
		<span class="elem">
			<xsl:text>&lt;</xsl:text>
			<span class="name">
				<xsl:value-of select="@name" />
			</span>
			<xsl:apply-templates select="attribute-example" />
			<xsl:if test="not(element-example) and not(boolean($text) and not($text = ''))">
				<xsl:text> /</xsl:text>
			</xsl:if>
			<xsl:text>&gt;</xsl:text>
		</span>

		<xsl:if test="boolean(element-example) and boolean($text) and not($text = '')">
			<xsl:message terminate="yes">
				<xsl:text>Mixed content is currently not supported in element-examples.</xsl:text>
			</xsl:message>
		</xsl:if>

		<xsl:if test="boolean($text) and not($text = '')">
			<xsl:value-of select="$text" />
		</xsl:if>

		<xsl:apply-templates select="element-example">
			<xsl:with-param name="indent" select="concat($indentation,$indent)" />
		</xsl:apply-templates>

		<xsl:if test="boolean(element-example)">
			<br />
			<xsl:value-of disable-output-escaping="yes" select="$indent" />
		</xsl:if>

		<xsl:if test="boolean(element-example) or (boolean($text) and not($text=''))">
			<span class="elem">
				<xsl:text>&lt;/</xsl:text>
				<span class="name">
					<xsl:value-of select="@name" />
				</span>
				<xsl:text>&gt;</xsl:text>
			</span>
		</xsl:if>
	</xsl:template>

	<xsl:template match="attribute-example">
		<xsl:variable name="name" select="@name" />
		<xsl:if test="not(count(parent::*/attribute-example[@name = $name]) = 1)">
			<xsl:message terminate="yes">
				<xsl:text>There are </xsl:text>
				<xsl:value-of select="count(parent::*/attribute-example[@name = $name])" />
				<xsl:text> attribute-example tags for the element '</xsl:text>
				<xsl:value-of select="parent::*/@name" />
				<xsl:text>' that have the same attribute name '</xsl:text>
				<xsl:value-of select="$name" />
				<xsl:text>' while there can be only one.</xsl:text>
			</xsl:message>
		</xsl:if>
		<xsl:text> </xsl:text>
		<span class="attr">
			<span class="name">
				<xsl:value-of select="@name" />
			</span>
			<xsl:text>=</xsl:text>
			<span class="value">
				<xsl:text>"</xsl:text>
				<xsl:value-of select="text()" />
				<xsl:text>"</xsl:text>
			</span>
		</span>
	</xsl:template>

<!-- end -->

	<xsl:template name="resultcodes">
		<h3>Result codes</h3>
		<table class="resultcodes">
			<tr>
				<th>Name</th>
				<th>Description</th>
			</tr>
			<xsl:call-template name="default_resultcodes" />
			<xsl:call-template name="referenced_resultcodes" />
			<xsl:apply-templates select="//function/output/resultcode" />
		</table>
	</xsl:template>

	<xsl:template name="referenced_resultcodes">
		<xsl:for-each select="//function/output/resultcode-ref">
			<xsl:variable name="code" select="@name" />
			<xsl:variable name="file" select="concat($specsdir, '/', $api, '/', $code, '.rcd')" />

			<xsl:for-each select="document($file)/resultcode">
				<tr>
					<td class="value">
						<a href="{$code}.html">
							<xsl:value-of select="$code" />
						</a>
					</td>
					<td class="description">
						<xsl:apply-templates select="description" />
					</td>
				</tr>
			</xsl:for-each>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="default_resultcodes">
		<xsl:variable name="haveParams">
			<xsl:choose>
				<xsl:when test="//function/input/param">true</xsl:when>
				<xsl:otherwise>false</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:for-each select="document($resultcodes_file)/resultcodes/code">
			<xsl:choose>
				<xsl:when test="@value = 'MissingFunctionName'" />
				<xsl:when test="@value = 'NoSuchFunction'"      />
				<xsl:when test="$sessionBased = 'false' and @onlyIfSessionBased    = 'true'" />
				<xsl:when test="$haveParams   = 'false' and @onlyIfInputParameters = 'true'" />
				<xsl:otherwise>
					<xsl:call-template name="default_resultcode">
						<xsl:with-param name="value"       select="@value" />
						<xsl:with-param name="description" select="description/text()" />
					</xsl:call-template>
			   </xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="default_resultcode">
		<xsl:param name="value" />
		<xsl:param name="description" />

		<tr class="default">
			<td class="value">
				<span title="This result code is generic, not specific to this API">
					<xsl:value-of select="$value" />
				</span>
			</td>
			<td class="description">
				<xsl:value-of select="$description" />
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="resultcode">
		<tr>
			<td class="value">
				<xsl:value-of select="@value" />
			</td>
			<td class="description">
				<xsl:apply-templates select="description" />
			</td>
		</tr>
	</xsl:template>

	<xsl:template name="datasection">
		<h3>Data section</h3>
		<xsl:choose>
			<xsl:when test="data">
				<p>
					<xsl:text>Root element: </xsl:text>
					<code>
						<xsl:text>&lt;</xsl:text>
						<xsl:value-of select="data/@contains" />
						<xsl:text>/&gt;</xsl:text>
					</code>
					<xsl:text>.</xsl:text>
				</p>
				<xsl:apply-templates select="data/element" />
			</xsl:when>
			<xsl:otherwise>
				<p>
					<em>This function has no data section.</em>
				</p>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="parametertable">
		<xsl:param name="title" />
		<xsl:param name="content" />
		<xsl:param name="class" />

		<h3>
			<xsl:value-of select="$title" />
		</h3>

		<xsl:choose>
			<xsl:when test="param">
				<table class="parameters">
					<xsl:attribute name="class">
						<xsl:value-of select="$class" />
					</xsl:attribute>
					<tr>
						<th>Parameter</th>
						<th>Type</th>
						<th>Description</th>
						<th>Required</th>
					</tr>
					<xsl:for-each select="param[not(@required='true') and not(@required='false')]">
						<xsl:message terminate="yes">
							<xsl:text>Parameter '</xsl:text>
							<xsl:value-of select="@name" />
							<xsl:text>' in </xsl:text>
							<xsl:value-of select="$content" />
							<xsl:text> has required attribute set to '</xsl:text>
							<xsl:value-of select="@required" />
							<xsl:text>', while only 'true' and 'false' are allowed values.</xsl:text>
						</xsl:message>
					</xsl:for-each>
					<xsl:apply-templates select="param" />
				</table>
			</xsl:when>
			<xsl:otherwise>
				<p>
					<em>
						<xsl:text>This function defines no </xsl:text>
						<xsl:value-of select="$content" />
						<xsl:text>.</xsl:text>
					</em>
				</p>
			</xsl:otherwise>
		</xsl:choose>
		
	</xsl:template>

	<xsl:template match="param">

		<xsl:if test="boolean(deprecated) and (@required = 'true')">
			<xsl:message terminate="yes">
				<xsl:text>Parameter '</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>' is both deprecated and required!</xsl:text>
			</xsl:message>
		</xsl:if>

		<tr>
			<td class="name">
				<xsl:value-of select="@name" />
			</td>
			<td class="type">
				<xsl:call-template name="typelink">
					<xsl:with-param name="api"      select="$api" />
					<xsl:with-param name="specsdir" select="$specsdir" />
					<xsl:with-param name="type">
						<xsl:choose>
							<xsl:when test="boolean(@type)">
								<xsl:value-of select="@type" />
							</xsl:when>
							<xsl:otherwise>_text</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
				</xsl:call-template>
			</td>
			<td class="description">
				<xsl:call-template name="description" />
			</td>
			<td class="required">
				<xsl:if test="@required = 'true'">
					<xsl:text>yes</xsl:text>
				</xsl:if>
				<xsl:if test="@required = 'false'">
					<xsl:text>no</xsl:text>
				</xsl:if>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="data/element">
		<h4>
			<xsl:text>Element </xsl:text>
			<em>
				<xsl:text>&lt;</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>/&gt;</xsl:text>
			</em>
		</h4>

		<table class="element_details">
			<tr>
				<th>Name:</th>
				<td>
					<xsl:value-of select="@name" />
				</td>
			</tr>
			<tr>
				<th>Contains:</th>
				<td>
					<xsl:choose>
						<xsl:when test="contains/contained">
							<xsl:for-each select="contains/contained">
								<xsl:if test="position() != 1">
									<xsl:text>, </xsl:text>
								</xsl:if>
								<xsl:value-of select="@element" />
								<xsl:choose>
									<xsl:when test="@multiplicity = '0+'">
										<xsl:text>*</xsl:text>
									</xsl:when>
									<xsl:when test="@multiplicity = '1'" />
									<xsl:when test="@multiplicity = '1+'">
										<xsl:text>+</xsl:text>
									</xsl:when>
									<xsl:when test="not(@multiplicity)">
										<xsl:message>
											<xsl:text>No multiplicity set for element '</xsl:text>
											<xsl:value-of select="../../@name" />
											<xsl:text>'.</xsl:text>
										</xsl:message>
									</xsl:when>
									<xsl:otherwise>
										<xsl:message>
											<xsl:text>Unable to parse multiplicity for element '</xsl:text>
											<xsl:value-of select="../../@name" />
											<xsl:text>'. It should be either '0+', '1', or '1+', not '</xsl:text>
											<xsl:value-of select="@multiplicity" />
											<xsl:text>'.</xsl:text>
										</xsl:message>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:for-each>
						</xsl:when>
						<xsl:otherwise>
							<em>nothing</em>
						</xsl:otherwise>
					</xsl:choose>
				</td>
			</tr>
			<tr>
				<th>Description:</th>
				<td>
					<xsl:choose>
						<xsl:when test="description">
							<xsl:apply-templates select="description" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:text> </xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</td>
			</tr>
			<tr>
				<th>Attributes:</th>
				<td>
					<xsl:choose>
						<xsl:when test="attribute">
							<table class="parameters">
								<tr>
									<th>Name</th>
									<th>Type</th>
									<th>Description</th>
									<th>Required</th>
								</tr>
								<xsl:apply-templates select="attribute" />
							</table>
						</xsl:when>
						<xsl:otherwise>
							<em>none</em>
						</xsl:otherwise>
					</xsl:choose>
				</td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template match="data/element/attribute">
		<xsl:variable name="type">
			<xsl:choose>
				<xsl:when test="@type">
					<xsl:value-of select="@type" />
				</xsl:when>
				<xsl:otherwise>_text</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<tr>
			<td class="value">
				<xsl:value-of select="@name" />
			</td>
			<td>
				<xsl:call-template name="typelink">
					<xsl:with-param name="api"      select="$api"      />
					<xsl:with-param name="specsdir" select="$specsdir" />
					<xsl:with-param name="type">
						<xsl:choose>
							<xsl:when test="boolean(@type)">
								<xsl:value-of select="@type" />
							</xsl:when>
							<xsl:otherwise>_text</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
				</xsl:call-template>
			</td>
			<td>
				<xsl:call-template name="description" />
			</td>
			<td>
				<xsl:choose>
					<xsl:when test="@required = 'true'">
						<xsl:text>yes</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>no</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>
	</xsl:template>

	<xsl:template name="description">
		<xsl:if test="description">
			<xsl:apply-templates select="description" />
			<xsl:if test="deprecated">
				<br />
			</xsl:if>
		</xsl:if>
		<xsl:if test="deprecated">
			<em>
				<strong>Deprecated: </strong>
				<xsl:apply-templates select="deprecated" />
			</em>
		</xsl:if>
	</xsl:template>

	<xsl:template name="broken_freeze">
		<xsl:variable name="frozen_version" select="document($api_file)/api/function[@name=$function_name]/@freeze" />
		<xsl:variable name="version">
			<xsl:call-template name="revision2string">
				<xsl:with-param name="revision" select="@rcsversion" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:if test="string-length($frozen_version) &gt; 0">
			<xsl:if test="not($frozen_version = $version)">
				<div class="broken_freeze">
					<h3>Broken freeze!</h3>
					<p>
						<xsl:text>Version </xsl:text>
						<xsl:value-of select="$frozen_version" />
						<xsl:text> is marked as frozen.</xsl:text>
						<xsl:if test="string-length($cvsweb_url) &gt; 0">
							<br />
							<xsl:text>View differences between this version and the frozen version:</xsl:text>
							<br />
							<a href="{$cvsweb_url}/src/apis/{$api}/{$function_name}.fnc.diff?r1={$frozen_version}&amp;r2={$version}">
								<xsl:text>diff </xsl:text>
								<xsl:value-of select="$frozen_version" />
								<xsl:text> and </xsl:text>
								<xsl:value-of select="$version" />
							</a>
							<xsl:text> (</xsl:text>
							<a href="{$cvsweb_url}/src/apis/{$api}/{$function_name}.fnc.diff?r1={$frozen_version}&amp;r2={$version}&amp;f=h">
								<xsl:text>colored</xsl:text>
							</a>
							<xsl:text>)</xsl:text>
						</xsl:if>
					</p>
				</div>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template match="function-ref[@name]">
		<xsl:variable name="reffunction" select="@name" />
		<xsl:variable name="reffunction_file" select="concat($specsdir, '/', $api, '/', $reffunction, '.fnc')" />
		<a href="{$reffunction}.html">
			<xsl:attribute name="title">
				<xsl:call-template name="firstline">
					<xsl:with-param name="text">
						<xsl:value-of select="document($reffunction_file)/function/description/text()" />
					</xsl:with-param>
				</xsl:call-template>
			</xsl:attribute>
			<xsl:value-of select="$reffunction" />
		</a>
	</xsl:template>

</xsl:stylesheet>
