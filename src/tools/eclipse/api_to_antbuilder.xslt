<?xml version="1.0" encoding="UTF-8" ?>
<!--
 Creates an Eclipse Ant Builder.launch file for an API.

 $Id$

 Copyright 2003-2006 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:output 
		omit-xml-declaration="no" 
		encoding="UTF-8"
		method="xml" 
		indent="yes" />
	
	<xsl:template match="api">
		<xsl:variable name="api" select="@name" />

		<launchConfiguration type="org.eclipse.ant.AntBuilderLaunchConfigurationType">
			<booleanAttribute key="org.eclipse.debug.ui.ATTR_LAUNCH_IN_BACKGROUND" value="false"/>
			<booleanAttribute key="org.eclipse.ant.ui.DEFAULT_VM_INSTALL" value="false"/>
			<booleanAttribute key="org.eclipse.jdt.launching.DEFAULT_CLASSPATH" value="true"/>
			<listAttribute key="org.eclipse.debug.core.MAPPED_RESOURCE_TYPES">
				<listEntry value="1"/>
			</listAttribute>
			<stringAttribute key="org.eclipse.ui.externaltools.ATTR_RUN_BUILD_KINDS" value="full,incremental,auto,clean"/>
			<booleanAttribute key="org.eclipse.ant.ui.ATTR_TARGETS_UPDATED" value="true"/>
			<booleanAttribute key="org.eclipse.ui.externaltools.ATTR_TRIGGERS_CONFIGURED" value="true"/>
			<booleanAttribute key="org.eclipse.debug.core.appendEnvironmentVariables" value="true"/>
			<stringAttribute key="org.eclipse.jdt.launching.CLASSPATH_PROVIDER" value="org.eclipse.ant.ui.AntClasspathProvider"/>
			<stringAttribute key="org.eclipse.ant.ui.ATTR_ANT_CLEAN_TARGETS" value="-init,clean,"/>
			<stringAttribute key="org.eclipse.ant.ui.ATTR_ANT_MANUAL_TARGETS" value="-init,build,"/>
			<stringAttribute key="org.eclipse.ant.ui.ATTR_ANT_AUTO_TARGETS" value="-init,build,"/>
			<stringAttribute key="org.eclipse.jdt.launching.PROJECT_ATTR" value="{$api}"/>
			<listAttribute key="org.eclipse.debug.core.MAPPED_RESOURCE_PATHS">
				<listEntry value="/{$api}/nbbuild.xml"/>
			</listAttribute>
			<stringAttribute key="org.eclipse.ui.externaltools.ATTR_LOCATION" value="${{workspace_loc:/{$api}/nbbuild.xml}}"/>
		</launchConfiguration>
	</xsl:template>

</xsl:stylesheet>
