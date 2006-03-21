<?xml version="1.0" encoding="UTF-8" ?>
<!--
 XSLT that generates the index.html of the specification documentation.

 $Id$

 Copyright 2003-2006 Wanadoo Nederland B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0" xmlns:style="urn:oasis:names:tc:opendocument:xmlns:style:1.0" xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0" xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0" xmlns:fo="urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:meta="urn:oasis:names:tc:opendocument:xmlns:meta:1.0" office:version="1.0">

	<xsl:template match="function" mode="function-chapter">
		<xsl:param name="project_home" />
		<xsl:param name="project_node" />
		<xsl:param name="specsdir"     />
		<xsl:param name="api"          />

		<xsl:variable name="function_file" select="concat($specsdir, '/', @name, '.fnc')" />
		<xsl:variable name="function_node" select="document($function_file)/function" />

		<text:h text:style-name="Heading1" text:outline-level="1">
			<xsl:text>Function </xsl:text>
			<xsl:value-of select="@name" />
		</text:h>
		<text:h text:style-name="Heading2" text:outline-level="2">Description</text:h>
		<text:p text:style-name="P2">
			<xsl:value-of select="$function_node/description" />
		</text:p>
		<xsl:call-template name="parameter-section">
			<xsl:with-param name="type-name" select="'input'" />
			<xsl:with-param name="type-node" select="$function_node/input" />
			<xsl:with-param name="specsdir"  select="$specsdir" />
			<xsl:with-param name="api"       select="$api" />
		</xsl:call-template>
		<xsl:call-template name="parameter-section">
			<xsl:with-param name="type-name" select="'output'" />
			<xsl:with-param name="type-node" select="$function_node/output" />
			<xsl:with-param name="specsdir"  select="$specsdir" />
			<xsl:with-param name="api"       select="$api" />
		</xsl:call-template>
		<text:h text:style-name="Heading2" text:outline-level="2">Error codes</text:h>
		<table:table table:name="FunctionsTable" table:style-name="FunctionsTable">
			<table:table-column table:style-name="FunctionsTable.A"/>
			<table:table-column table:style-name="FunctionsTable.B"/>
			<table:table-row>
				<table:table-cell table:style-name="FunctionsTable.A1" office:value-type="string">
					<text:p text:style-name="P2">Error code</text:p>
				</table:table-cell>
				<table:table-cell table:style-name="FunctionsTable.A1" office:value-type="string">
					<text:p text:style-name="P2">Description</text:p>
				</table:table-cell>
			</table:table-row>
			<xsl:call-template name="standard-errorcodes" />
			<xsl:apply-templates select="$function_node/output/resultcode-ref">
				<xsl:with-param name="specsdir" select="$specsdir" />
			</xsl:apply-templates>
		</table:table>
		<xsl:if test="$function_node/example">
			<text:h text:style-name="Heading2" text:outline-level="2">Examples</text:h>
			<text:p text:style-name="Standard">Below are some example requests with corresponding responses. Note that these are non-normative.</text:p>
			<xsl:apply-templates select="$function_node/example" />
		</xsl:if>
	</xsl:template>

	<xsl:template name="parameter-section">
		<xsl:param name="type-name" />
		<xsl:param name="type-node" />
		<xsl:param name="specsdir" />
		<xsl:param name="api" />

		<text:h text:style-name="Heading2" text:outline-level="2">
			<xsl:value-of select="$type-name" />
			<xsl:text> parameters</xsl:text>
		</text:h>
		<xsl:choose>
			<xsl:when test="count($type-node) = 0 or count($type-node/param) = 0">
				<text:p text:style-name="Note">
					<xsl:text>This function does not define defines any </xsl:text>
					<xsl:value-of select="$type-name" />
					<xsl:text> parameters.</xsl:text>
				</text:p>
			</xsl:when>
			<xsl:otherwise>
				<table:table table:name="ParametersTable" table:style-name="ParametersTable">
					<table:table-column table:style-name="ParametersTable.A"/>
					<table:table-column table:style-name="ParametersTable.B"/>
					<table:table-column table:style-name="ParametersTable.C"/>
					<table:table-column table:style-name="ParametersTable.D"/>
					<table:table-row>
						<table:table-cell table:style-name="ParametersTable.A1" office:value-type="string">
							<text:p text:style-name="P2">Parameter</text:p>
						</table:table-cell>
						<table:table-cell table:style-name="ParametersTable.A1" office:value-type="string">
							<text:p text:style-name="P2">Type</text:p>
						</table:table-cell>
						<table:table-cell table:style-name="ParametersTable.A1" office:value-type="string">
							<text:p text:style-name="P2">Description</text:p>
						</table:table-cell>
						<table:table-cell table:style-name="ParametersTable.A1" office:value-type="string">
							<text:p text:style-name="P2">Req.</text:p>
						</table:table-cell>
					</table:table-row>
					<xsl:apply-templates select="$type-node/param" mode="parameters-table">
						<xsl:with-param name="specsdir"  select="$specsdir" />
						<xsl:with-param name="api"       select="$api" />
					</xsl:apply-templates>
				</table:table>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="param" mode="parameters-table">
		<xsl:param name="specsdir" />
		<xsl:param name="api"      />

		<table:table-row>
			<table:table-cell office:value-type="string">
				<text:p text:style-name="Standard">
					<xsl:value-of select="@name" />
				</text:p>
			</table:table-cell>
			<table:table-cell office:value-type="string">
				<xsl:call-template name="opendoc_for_type">
					<xsl:with-param name="specsdir" select="$specsdir" />
					<xsl:with-param name="api" select="$api" />
					<xsl:with-param name="type" select="@type" />
				</xsl:call-template>
			</table:table-cell>
			<table:table-cell office:value-type="string">
				<text:p text:style-name="Standard">
					<xsl:value-of select="description" />
				</text:p>
			</table:table-cell>
			<table:table-cell office:value-type="string">
				<text:p text:style-name="Standard">
					<xsl:choose>
						<xsl:when test="@required = 'true'">
							<xsl:text>yes</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>no</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</text:p>
			</table:table-cell>
		</table:table-row>
	</xsl:template>

	<xsl:template match="example">
		<text:p text:style-name="P2">
			<xsl:text>Example </xsl:text>
			<xsl:value-of select="position()" />
			<xsl:text>: </xsl:text>
			<xsl:value-of select="description/text()" />
		</text:p>
		<table:table table:name="ExampleTable" table:style-name="ExampleTable">
			<table:table-column table:style-name="ExampleTable.A"/>
			<table:table-column table:style-name="ExampleTable.B"/>
			<table:table-row>
				<table:table-cell table:style-name="ExampleTable.A1" office:value-type="string">
					<text:p text:style-name="P2">Request</text:p>
				</table:table-cell>
				<table:table-cell office:value-type="string">
					<text:p text:style-name="Standard">
						<xsl:text>http://API_HOST/</xsl:text>
						<text:line-break/>
						<xsl:text>?_function=</xsl:text>
						<xsl:value-of select="../@name" />
						<text:line-break/>
						<xsl:text>&amp;_convention=_xins-std</xsl:text>
						<xsl:for-each select="input-example">
							<text:line-break/>
							<xsl:text>&amp;</xsl:text>
							<text:span text:style-name="Name">
								<xsl:value-of select="@name" />
							</text:span>
							<xsl:text>=</xsl:text>
							<text:span text:style-name="Value">
								<xsl:call-template name="urlencode">
									<xsl:with-param name="text" select="text()" />
								</xsl:call-template>
							</text:span>
						</xsl:for-each>
						<xsl:if test="input-data-example/element-example">
							<xsl:variable name="example-inputdata">
								<xsl:if test="input-data-example/element-example">
									<xsl:text>&lt;data&gt;</xsl:text>
									<xsl:apply-templates select="input-data-example/element-example" mode="input" />
									<xsl:text>&lt;/data&gt;</xsl:text>
								</xsl:if>
							</xsl:variable>
							<xsl:text>&amp;</xsl:text>
							<text:span text:style-name="Name">
								<xsl:value-of select="'_data'" />
							</text:span>
							<xsl:text>=</xsl:text>
							<text:span text:style-name="Value">
								<xsl:call-template name="urlencode">
									<xsl:with-param name="text" select="$example-inputdata" />
								</xsl:call-template>
							</text:span>
						</xsl:if>
					</text:p>
				</table:table-cell>
			</table:table-row>
			<table:table-row>
				<table:table-cell table:style-name="ExampleTable.A1" office:value-type="string">
					<text:p text:style-name="P2">Response</text:p>
				</table:table-cell>
				<table:table-cell office:value-type="string">
					<text:p text:style-name="Xml">
						<text:span text:style-name="XmlDecl">
						 <xsl:text>&lt;?xml version="1.0" encoding="UTF-8"?&gt;</xsl:text>
						</text:span>
						<text:line-break/>
						<xsl:text>&lt;</xsl:text>
						<text:span text:style-name="Elem">result</text:span>
						<xsl:if test="string-length(@resultcode) &gt; 0">
							<xsl:call-template name="print-attr">
								<xsl:with-param name="name" select="'errorcode'" />
								<xsl:with-param name="value" select="@resultcode" />
							</xsl:call-template>
						</xsl:if>
						<xsl:choose>
							<xsl:when test="output-example or output-data-example or data-example">
								<xsl:text>&gt;</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text> /&gt;</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
						<xsl:for-each select="output-example">
							<text:line-break/>
							<text:s text:c="2" />
							<xsl:text>&lt;</xsl:text>
							<text:span text:style-name="Elem">param</text:span>
							<xsl:call-template name="print-attr">
								<xsl:with-param name="name" select="'name'" />
								<xsl:with-param name="value" select="@name" />
							</xsl:call-template>
							<xsl:text>&gt;</xsl:text>
							<text:span text:style-name="PCData">
								<xsl:value-of select="text()" />
							</text:span>
							<xsl:text>&lt;/</xsl:text>
							<text:span text:style-name="Elem">param</text:span>
							<xsl:text>&gt;</xsl:text>
						</xsl:for-each>
						<xsl:if test="output-data-example or data-example">
							<text:line-break/>
							<text:s text:c="2" />
							<xsl:text>&lt;</xsl:text>
							<text:span text:style-name="Elem">data</text:span>
							<xsl:text>&gt;</xsl:text>
							<xsl:apply-templates select="output-data-example/element-example | data-example/element-example" />
							<text:line-break/>
							<text:s text:c="2" />
							<xsl:text>&lt;/</xsl:text>
							<text:span text:style-name="Elem">data</text:span>
							<xsl:text>&gt;</xsl:text>
						</xsl:if>
						<xsl:if test="output-example or output-data-example or data-example">
							<text:line-break/>
							<xsl:text>&lt;/result&gt;</xsl:text>
						</xsl:if>
					</text:p>
				</table:table-cell>
			</table:table-row>
		</table:table>
	</xsl:template>

<!-- element examples -->

	<xsl:template match="element-example">
		<xsl:param name="indent" select="4" />

		<xsl:variable name="text" select="pcdata-example/text()" />

		<text:line-break/>
		<text:s text:c="{$indent}" />
		<xsl:text>&lt;</xsl:text>
		<text:span text:style-name="Elem">
				<xsl:value-of select="@name" />
		</text:span>
		<xsl:for-each select="attribute-example">
			<xsl:call-template name="print-attr">
				<xsl:with-param name="name" select="@name" />
				<xsl:with-param name="value" select="text()" />
			</xsl:call-template>
		</xsl:for-each>
		<xsl:if test="not(element-example) and not(boolean($text) and not($text = ''))">
			<xsl:text> /</xsl:text>
		</xsl:if>
		<xsl:text>&gt;</xsl:text>

		<xsl:if test="boolean($text) and not($text = '')">
			<xsl:value-of select="$text" />
		</xsl:if>

		<xsl:apply-templates select="element-example">
			<xsl:with-param name="indent" select="$indent + 2" />
		</xsl:apply-templates>

		<xsl:if test="boolean(element-example)">
			<text:line-break />
			<text:s text:c="{$indent}" />
			<xsl:value-of disable-output-escaping="yes" select="$indent" />
		</xsl:if>

		<xsl:if test="boolean(element-example) or (boolean($text) and not($text=''))">
			<xsl:text>&lt;/</xsl:text>
			<text:span text:style-name="Elem">
				<xsl:value-of select="@name" />
			</text:span>
			<xsl:text>&gt;</xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="element-example" mode="input">

		<xsl:variable name="text" select="pcdata-example/text()" />

		<xsl:text>&lt;</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:apply-templates select="attribute-example" mode="input" />
		<xsl:if test="not(element-example) and not(boolean($text) and not($text = ''))">
			<xsl:text> /</xsl:text>
		</xsl:if>
		<xsl:text>&gt;</xsl:text>

		<xsl:if test="boolean($text) and not($text = '')">
			<xsl:value-of select="$text" />
		</xsl:if>

		<xsl:apply-templates select="element-example" mode="input" />

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

	<xsl:template match="attribute-example" mode="input">
		<xsl:variable name="name" select="@name" />
		<xsl:text> </xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>="</xsl:text>
		<xsl:value-of select="text()" />
		<xsl:text>"</xsl:text>
	</xsl:template>
<!-- end -->

	<xsl:template name="print-attr">
		<xsl:param name="name" />
		<xsl:param name="value" />

		<xsl:text> </xsl:text>
		<text:span text:style-name="Name">
			<xsl:value-of select="$name" />
		</text:span>
		<xsl:text>=</xsl:text>
		<text:span text:style-name="Value">
			<xsl:text>"</xsl:text>
			<xsl:value-of select="$value" />
			<xsl:text>"</xsl:text>
		</text:span>
	</xsl:template>

	<xsl:template name="standard-errorcodes">
		<xsl:variable name="resultcodes_node" select="document('../../xml/default_resultcodes.xml')/resultcodes" />
		<xsl:for-each select="$resultcodes_node/code">
			<table:table-row>
				<table:table-cell office:value-type="string">
					<text:p text:style-name="Standard">
						<xsl:value-of select="@name" />
					</text:p>
				</table:table-cell>
				<table:table-cell office:value-type="string">
					<text:p text:style-name="Standard">
						<xsl:value-of select="description/text()" />
					</text:p>
				</table:table-cell>
			</table:table-row>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="resultcode-ref">
		<xsl:param name="specsdir" />

		<xsl:variable name="rcd_file" select="concat($specsdir, '/', @name, '.rcd')"/>
		<xsl:variable name="rcd_node" select="document($rcd_file)/resultcode"/>
		<table:table-row>
			<table:table-cell office:value-type="string">
				<text:p text:style-name="Standard">
					<xsl:value-of select="$rcd_node/@name" />
				</text:p>
			</table:table-cell>
			<table:table-cell office:value-type="string">
				<text:p text:style-name="Standard">
					<xsl:value-of select="$rcd_node/description/text()" />
				</text:p>
			</table:table-cell>
		</table:table-row>
	</xsl:template>
</xsl:stylesheet>
