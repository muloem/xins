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

	<xsl:template match="input | output" mode="checkParams">

		<xsl:variable name="errorclass">
			<xsl:choose>
				<xsl:when test="local-name() = 'input'">
					<xsl:text>InvalidRequestResult</xsl:text>
				</xsl:when>
				<xsl:when test="local-name() = 'output'">
					<xsl:text>InvalidResponseResult</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:message terminate="yes">
						Invalid node.
					</xsl:message>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="context">
			<xsl:if test="local-name() = 'input'">
				<xsl:text>context.</xsl:text>
			</xsl:if>
		</xsl:variable>
		
		<xsl:if test="param">
			<xsl:text>
      // Get the parameters</xsl:text>

			<xsl:for-each select="param">
				<xsl:text>
      java.lang.String </xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text> = </xsl:text>
				<xsl:value-of select="$context" />
				<xsl:text>getParameter("</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>");</xsl:text>
			</xsl:for-each>
		</xsl:if>
		
		<xsl:text>

      org.xins.server.</xsl:text>
		<xsl:value-of select="$errorclass" />
		<xsl:text> _errorResult = null;</xsl:text>

		<!-- ************************************************************* -->
		<!-- Check required parameters                               -->
		<!-- ************************************************************* -->

		<xsl:if test="param[@required='true']">
			<xsl:text>
				// Check the mandatory parameters</xsl:text>

			<xsl:for-each select="param[@required='true']">
				<xsl:text>
				if (</xsl:text>
					<xsl:value-of select="@name" />
					<xsl:text> == null) {
					 if (_errorResult == null) {
							_errorResult = new org.xins.server.</xsl:text>
					<xsl:value-of select="$errorclass" />
					<xsl:text>();
					 }
					 _errorResult.addMissingParameter("</xsl:text>
					<xsl:value-of select="@name" />
					<xsl:text>");
				}</xsl:text>
			</xsl:for-each>
		</xsl:if>

		<!-- ************************************************************* -->
		<!-- Check values for types for the input parameters               -->
		<!-- ************************************************************* -->

		<xsl:if test="param[not(@type='_text' or string-length(@type) = 0)]">
			<xsl:text>

      // Check values are valid for the associated types</xsl:text>
			<xsl:for-each select="param[not(@type='_text' or string-length(@type) = 0)]">
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
				<xsl:text>)) {
         if (_errorResult == null) {
            _errorResult = new org.xins.server.</xsl:text>
				<xsl:value-of select="$errorclass" />
				<xsl:text>();
         }
         _errorResult.addInvalidValueForType("</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>", "</xsl:text>
				<xsl:value-of select="@type" />
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
				<xsl:text>) {
         if (_errorResult == null) {
            _errorResult = new org.xins.server.</xsl:text>
				<xsl:value-of select="$errorclass" />
				<xsl:text>();
         }
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
				<xsl:text>) {
         if (_errorResult == null) {
            _errorResult = new org.xins.server.</xsl:text>
				<xsl:value-of select="$errorclass" />
				<xsl:text>();
         }
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
					<xsl:text>)) {
         if (_errorResult == null) {
            _errorResult = new org.xins.server.</xsl:text>
					<xsl:value-of select="$errorclass" />
					<xsl:text>();
         }
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
				<xsl:text>)) {
         if (_errorResult == null) {
            _errorResult = new org.xins.server.</xsl:text>
				<xsl:value-of select="$errorclass" />
				<xsl:text>();
         }
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

		<xsl:choose>
			<xsl:when test="local-name() = 'input'">
				<xsl:text>

      if (_errorResult != null) {
         return _errorResult;
      }</xsl:text>
			</xsl:when>
			<xsl:when test="local-name() = 'output'">
				<xsl:text>
      return _errorResult;</xsl:text>
			</xsl:when>
		</xsl:choose>

	</xsl:template>

</xsl:stylesheet>
