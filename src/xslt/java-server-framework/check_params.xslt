<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 XSLT that generates the code used to check the input and output parameters.
 The code will check that the required parameters are set, that the parameters
 contains a value compliant with the type and that the param-combo
 specifications are not violated.

 $Id$

 Copyright 2003-2005 Wanadoo Nederland B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="text" />

	<xsl:template match="input | output | element" mode="checkParams">
		<xsl:param name="side" select="'server'" />

		<xsl:variable name="errorclass">
			<xsl:choose>
				<xsl:when test="$side='server' and (local-name() = 'input' or (local-name() = 'element' and local-name(../..) = 'input'))">
					<xsl:text>org.xins.server.InvalidRequestResult</xsl:text>
				</xsl:when>
				<xsl:when test="$side='server' and (local-name() = 'output' or (local-name() = 'element' and local-name(../..) = 'output'))">
					<xsl:text>org.xins.server.InvalidResponseResult</xsl:text>
				</xsl:when>
				<xsl:when test="$side='client' and (local-name() = 'input' or (local-name() = 'element' and local-name(../..) = 'input'))">
					<xsl:text>org.xins.client.UnacceptableRequestException</xsl:text>
				</xsl:when>
				<xsl:when test="$side='client' and (local-name() = 'output' or (local-name() = 'element' and local-name(../..) = 'output'))">
					<!-- TODO: Create another class for this purpose. -->
					<xsl:text>org.xins.client.UnacceptableRequestException</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:message terminate="yes">
						<xsl:text>Invalid node with side &quot;</xsl:text>
						<xsl:value-of select="$side" />
						<xsl:text>&quot; in the element &quot;</xsl:text>
						<xsl:value-of select="local-name()" />
						<xsl:text>&quot;.</xsl:text>
					</xsl:message>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:variable name="context">
			<xsl:choose>
				<xsl:when test="$side='server' and local-name() = 'input'">
					<xsl:text>context.</xsl:text>
				</xsl:when>
				<xsl:when test="$side='client' and local-name() = 'output'">
					<xsl:text>result.</xsl:text>
				</xsl:when>
				<xsl:when test="local-name() = 'element'">
					<xsl:value-of select="@name" />
					<xsl:text>NextElement.</xsl:text>
				</xsl:when>
				<xsl:otherwise />
			</xsl:choose>
		</xsl:variable>

		<xsl:if test="param | attribute">
			<xsl:text>
      // Get the parameters</xsl:text>

			<xsl:for-each select="param | attribute">
				<xsl:text>
      java.lang.String </xsl:text>
				<xsl:value-of select="@name" />
				<xsl:if test="local-name() = 'attribute'">
					<xsl:text>Attribute</xsl:text>
				</xsl:if>
				<xsl:text> = </xsl:text>
				<xsl:value-of select="$context" />
				<xsl:choose>
					<xsl:when test="local-name() = 'attribute'">
						<xsl:text>getAttribute("</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>getParameter("</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:value-of select="@name" />
				<xsl:text>");</xsl:text>
			</xsl:for-each>
		</xsl:if>

		<xsl:if test="local-name() = 'input' or local-name() = 'output'">
			<xsl:text>

      </xsl:text>
			<xsl:value-of select="$errorclass" />
			<xsl:text> _errorResult = null;</xsl:text>
		</xsl:if>

		<!-- ************************************************************* -->
		<!-- Check required parameters                                     -->
		<!-- ************************************************************* -->

		<xsl:if test="param[@required='true'] | attribute[@required='true']">
			<xsl:text>
      // Check the mandatory parameters</xsl:text>

			<xsl:for-each select="param[@required='true'] | attribute[@required='true']">
				<xsl:text>
      if (</xsl:text>
					<xsl:value-of select="@name" />
					<xsl:if test="local-name() = 'attribute'">
						<xsl:text>Attribute</xsl:text>
					</xsl:if>
					<xsl:text> == null) {</xsl:text>
					<xsl:call-template name="create-error">
						<xsl:with-param name="side" select="$side" />
						<xsl:with-param name="errorclass" select="$errorclass" />
					</xsl:call-template>
					<xsl:text>
         _errorResult.addMissingParameter("</xsl:text>
					<xsl:value-of select="@name" />
					<xsl:if test="local-name() = 'attribute'">
						<xsl:text>", "</xsl:text>
						<xsl:value-of select="../@name" />
					</xsl:if>
					<xsl:text>");
      }</xsl:text>
			</xsl:for-each>
		</xsl:if>

		<!-- ************************************************************* -->
		<!-- Check values for types for the input parameters               -->
		<!-- ************************************************************* -->

		<xsl:if test="param[not(@type='_text' or string-length(@type) = 0)] | attribute[not(@type='_text' or string-length(@type) = 0)]">
			<xsl:text>

      // Check values are valid for the associated types</xsl:text>
			<xsl:for-each select="param[not(@type='_text' or string-length(@type) = 0)] | attribute[not(@type='_text' or string-length(@type) = 0)]">
				<xsl:text>
      if (!</xsl:text>
				<xsl:call-template name="javatypeclass_for_type">
					<xsl:with-param name="project_file" select="$project_file" />
					<xsl:with-param name="api"          select="$api"          />
					<xsl:with-param name="specsdir"     select="$specsdir"     />
					<xsl:with-param name="type"         select="@type"         />
				</xsl:call-template>
				<xsl:text>.SINGLETON.isValidValue(</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:if test="local-name() = 'attribute'">
					<xsl:text>Attribute</xsl:text>
				</xsl:if>
				<xsl:text>)) {</xsl:text>
				<xsl:call-template name="create-error">
					<xsl:with-param name="side" select="$side" />
					<xsl:with-param name="errorclass" select="$errorclass" />
				</xsl:call-template>
				<xsl:text>
         _errorResult.addInvalidValueForType("</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>", "</xsl:text>
					<xsl:value-of select="@type" />
					<xsl:if test="local-name() = 'attribute'">
						<xsl:text>", "</xsl:text>
						<xsl:value-of select="../@name" />
					</xsl:if>
					<xsl:text>");
      }</xsl:text>
			</xsl:for-each>
		</xsl:if>

		<!-- ************************************************************* -->
		<!-- Check 'inclusive-or' combos                                   -->
		<!-- ************************************************************* -->

		<xsl:if test="param-combo[@type='inclusive-or']">
			<xsl:text>

      // Check inclusive-or parameter combinations</xsl:text>
			<xsl:for-each select="param-combo[@type='inclusive-or']">
				<xsl:text>
      if (</xsl:text>
				<xsl:for-each select="param-ref">
					<xsl:if test="position() &gt; 1"> &amp;&amp; </xsl:if>
					<xsl:value-of select="@name" />
					<xsl:text> == null</xsl:text>
				</xsl:for-each>
				<xsl:text>) {</xsl:text>
				<xsl:call-template name="create-error">
					<xsl:with-param name="side" select="$side" />
					<xsl:with-param name="errorclass" select="$errorclass" />
				</xsl:call-template>
				<xsl:text>
         java.util.List _invalidComboElements = new java.util.ArrayList();</xsl:text>
				<xsl:for-each select="param-ref">
				<xsl:text>
         _invalidComboElements.add("</xsl:text>
					<xsl:value-of select="@name" />
					<xsl:text>");</xsl:text>
				</xsl:for-each>
				<xsl:text>
         _errorResult.addParamCombo("inclusive-or", _invalidComboElements);
      }</xsl:text>
			</xsl:for-each>
		</xsl:if>


		<!-- ************************************************************* -->
		<!-- Check 'exclusive-or' combos                                   -->
		<!-- ************************************************************* -->

		<xsl:if test="param-combo[@type='exclusive-or']">
			<xsl:text>

      // Check exclusive-or parameter combinations</xsl:text>
			<xsl:for-each select="param-combo[@type='exclusive-or']">
				<xsl:text>
      if (</xsl:text>
				<xsl:for-each select="param-ref">
					<xsl:if test="position() &gt; 1"> &amp;&amp; </xsl:if>
					<xsl:value-of select="@name" />
					<xsl:text> == null</xsl:text>
				</xsl:for-each>
				<xsl:text>) {</xsl:text>
				<xsl:call-template name="create-error">
					<xsl:with-param name="side" select="$side" />
					<xsl:with-param name="errorclass" select="$errorclass" />
				</xsl:call-template>
				<xsl:text>
         java.util.List _invalidComboElements = new java.util.ArrayList();</xsl:text>
				<xsl:for-each select="param-ref">
				<xsl:text>
         _invalidComboElements.add("</xsl:text>
					<xsl:value-of select="@name" />
					<xsl:text>");</xsl:text>
				</xsl:for-each>
				<xsl:text>
         _errorResult.addParamCombo("exclusive-or", _invalidComboElements);
      }</xsl:text>
				<xsl:for-each select="param-ref">
					<xsl:variable name="active" select="@name" />
					<xsl:text>
      if (</xsl:text>
					<xsl:value-of select="$active" />
					<xsl:text>!= null &amp;&amp; (</xsl:text>
					<xsl:for-each select="../param-ref[not(@name = $active)]">
						<xsl:if test="position() &gt; 1"> || </xsl:if>
						<xsl:value-of select="@name" />
						<xsl:text> != null</xsl:text>
					</xsl:for-each>
					<xsl:text>)) {</xsl:text>
				<xsl:call-template name="create-error">
					<xsl:with-param name="side" select="$side" />
					<xsl:with-param name="errorclass" select="$errorclass" />
				</xsl:call-template>
				<xsl:text>
         java.util.List _invalidComboElements = new java.util.ArrayList();
         _invalidComboElements.add("</xsl:text>
					<xsl:value-of select="$active" />
					<xsl:text>");</xsl:text>
					<xsl:for-each select="../param-ref[not(@name = $active)]">
						<xsl:text>
         if (</xsl:text>
						<xsl:value-of select="@name" />
						<xsl:text> != null) {
            _invalidComboElements.add("</xsl:text>
					<xsl:value-of select="@name" />
					<xsl:text>");
         }</xsl:text>
					</xsl:for-each>
					<xsl:text>
         _errorResult.addParamCombo("exclusive-or", _invalidComboElements);
      }</xsl:text>
				</xsl:for-each>
			</xsl:for-each>
		</xsl:if>

		<!-- ************************************************************* -->
		<!-- Check 'all-or-none' combos                                    -->
		<!-- ************************************************************* -->

		<xsl:if test="param-combo[@type='all-or-none']">
			<xsl:text>

      // Check all-or-none parameter combinations</xsl:text>
			<xsl:for-each select="param-combo[@type='all-or-none']">
				<xsl:text>
      if (!(</xsl:text>
				<xsl:for-each select="param-ref">
					<xsl:if test="position() &gt; 1"> &amp;&amp; </xsl:if>
					<xsl:value-of select="@name" />
					<xsl:text> == null</xsl:text>
				</xsl:for-each>
				<xsl:text>) &amp;&amp; (</xsl:text>
				<xsl:for-each select="param-ref">
					<xsl:if test="position() &gt; 1"> || </xsl:if>
					<xsl:value-of select="@name" />
					<xsl:text> == null</xsl:text>
				</xsl:for-each>
				<xsl:text>)) {</xsl:text>
				<xsl:call-template name="create-error">
					<xsl:with-param name="side" select="$side" />
					<xsl:with-param name="errorclass" select="$errorclass" />
				</xsl:call-template>
				<xsl:text>
         java.util.List _invalidComboElements = new java.util.ArrayList();</xsl:text>
				<xsl:for-each select="param-ref">
					<xsl:text>
         _invalidComboElements.add("</xsl:text>
					<xsl:value-of select="@name" />
					<xsl:text>");</xsl:text>
				</xsl:for-each>
				<xsl:text>
         _errorResult.addParamCombo("all-or-none", _invalidComboElements);
      }</xsl:text>

			</xsl:for-each>
		</xsl:if>

		<!-- ************************************************************* -->
		<!-- Check 'not-all' combos                                        -->
		<!-- ************************************************************* -->

		<xsl:if test="param-combo[@type='not-all']">
			<xsl:text>

      // Check not-all parameter combinations</xsl:text>
			<xsl:for-each select="param-combo[@type='not-all']">
				<xsl:text>
      if (</xsl:text>
				<xsl:for-each select="param-ref">
					<xsl:if test="position() &gt; 1">
          &amp;&amp; </xsl:if>
					<xsl:text>(</xsl:text>
					<xsl:value-of select="@name" />
					<xsl:text> != null)</xsl:text>
				</xsl:for-each>
				<xsl:text>)
      {</xsl:text>
				<xsl:call-template name="create-error">
					<xsl:with-param name="side" select="$side" />
					<xsl:with-param name="errorclass" select="$errorclass" />
				</xsl:call-template>
				<xsl:text>
         java.util.List _invalidComboElements = new java.util.ArrayList();</xsl:text>
				<xsl:for-each select="param-ref">
					<xsl:text>
         _invalidComboElements.add("</xsl:text>
					<xsl:value-of select="@name" />
					<xsl:text>");</xsl:text>
				</xsl:for-each>
				<xsl:text>
         _errorResult.addParamCombo("not-all", _invalidComboElements);
      }</xsl:text>

			</xsl:for-each>
		</xsl:if>

		<!-- ************************************************************* -->
		<!-- Check data section                                            -->
		<!-- ************************************************************* -->
		<xsl:if test="local-name() = 'input' or local-name() = 'output'">
			<xsl:text>
      if (</xsl:text>
			<xsl:value-of select="$context" />
			<xsl:text>getDataElement() != null) {</xsl:text>
			<xsl:apply-templates select="data/contains/contained" mode="checkParams">
				<xsl:with-param name="parentelement" select="concat($context, 'getDataElement()')" />
				<xsl:with-param name="side" select="$side" />
			</xsl:apply-templates>
			<xsl:text>
      }</xsl:text>
		</xsl:if>

		<xsl:apply-templates select="contains/contained" mode="checkParams">
			<xsl:with-param name="parentelement" select="concat(@name, 'NextElement')" />
			<xsl:with-param name="side" select="$side" />
		</xsl:apply-templates>

		<xsl:choose>
			<xsl:when test="$side = 'server' and local-name() = 'input'">
				<xsl:text>

      if (_errorResult != null) {
         return _errorResult;
      }</xsl:text>
			</xsl:when>
			<xsl:when test="($side = 'client' and local-name() = 'input') or ($side = 'client' and local-name() = 'output') or ($side = 'server' and local-name() = 'output')">
				<xsl:text>
      return _errorResult;</xsl:text>
			</xsl:when>
		</xsl:choose>

	</xsl:template>

	<xsl:template match="contains/contained" mode="checkParams">
		<xsl:param name="parentelement" />
		<xsl:param name="side" select="'server'" />

		<xsl:variable name="elementname" select="@element" />
		<xsl:text>
      java.util.Iterator </xsl:text>
		<xsl:value-of select="$elementname" />
		<xsl:text>Iterator = </xsl:text>
		<xsl:value-of select="$parentelement" />
		<xsl:text>.getChildElements("</xsl:text>
		<xsl:value-of select="$elementname" />
		<xsl:text>").iterator();
      while (</xsl:text>
		<xsl:value-of select="$elementname" />
		<xsl:text>Iterator.hasNext()) {</xsl:text>
		<xsl:choose>
			<xsl:when test="$side='client' and ancestor::output">
				<xsl:text>
         org.xins.client.DataElement </xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>
         org.xins.common.xml.Element </xsl:text>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:value-of select="$elementname" />
		<xsl:choose>
			<xsl:when test="$side='client' and ancestor::output">
				<xsl:text>NextElement = (org.xins.client.DataElement) </xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>NextElement = (org.xins.common.xml.Element) </xsl:text>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:value-of select="$elementname" />
		<xsl:text>Iterator.next();</xsl:text>
		<xsl:apply-templates select="../../element[@name=$elementname]" mode="checkParams">
			<xsl:with-param name="side" select="$side" />
		</xsl:apply-templates>
		<xsl:apply-templates select="../../../element[@name=$elementname]" mode="checkParams">
			<xsl:with-param name="side" select="$side" />
		</xsl:apply-templates>
		<xsl:text>
      }</xsl:text>
	</xsl:template>

	<xsl:template name="create-error">
		<xsl:param name="side" select="'server'" />
		<xsl:param name="errorclass" />

		<xsl:text>
         if (_errorResult == null) {
            _errorResult = new </xsl:text>
		<xsl:value-of select="$errorclass" />
		<xsl:text>(</xsl:text>
		<xsl:if test="$side = 'client' and ancestor::input">
			<xsl:text>this</xsl:text>
		</xsl:if>
		<xsl:if test="$side = 'client' and ancestor::output">
			<xsl:text>null</xsl:text>
		</xsl:if>
		<xsl:text>);
         }</xsl:text>
	</xsl:template>
</xsl:stylesheet>
