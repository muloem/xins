<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Print the footer -->
	<xsl:template name="createproject">

		<!-- Define parameters -->
		<xsl:param name="specsdir"    />

		<target name="create-project" description="Generates a new project file.">
			<input addproperty="project.name"
						 message="Please, enter the project name (in lowercase) :" />
			<mkdir dir="{$specsdir}/${{project.name}}" />
			<property name="xml.file" value="{$specsdir}/${{project.name}}/api.xml" />
			<available property="xml.exists" file="${{xml.file}}" />
			<fail message="The file ${{xml.file}} already exists!" if="xml.exists" />
			<echo file="${{xml.file}}"><![CDATA[<?xml version="1.0" encoding="US-ASCII"?>
<!DOCTYPE api PUBLIC "-//XINS//DTD XINS API//EN"
"http://xins.sourceforge.net/dtd/api.dtd">
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $]]><![CDATA[Id$
-->

<api name="]]>${project.name}<![CDATA[" owner="]]>${user.name}<![CDATA["
rcsversion="$]]><![CDATA[Revision$" rcsdate="$]]><![CDATA[Date$">

	<description></description>


	<impl-java mapping="/">
	</impl-java>

</api>]]></echo>
			<echo message="Don't forget to add &lt;api name=&quot;${{project.name}}&quot; /&gt; to the xins-project.xml file." />
		</target>

		<target name="create-function" description="Generates a new function.">
			<input addproperty="project.name"
						 message="Please, enter the name of the project:" />
			<input addproperty="function.name"
						 message="Please, enter the name of the new function:" />
			<property name="xml.file" value="{$specsdir}/${{project.name}}/${{function.name}}.fnc" />
			<available property="xml.exists" file="${{xml.file}}" />
			<fail message="The file ${{xml.file}} already exists!" if="xml.exists" />
			<echo file="${{xml.file}}"><![CDATA[<?xml version="1.0" encoding="US-ASCII"?>
<!DOCTYPE function PUBLIC "-//XINS//DTD Function//EN"
"http://xins.sourceforge.net/dtd/function.dtd">
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $]]><![CDATA[Id$
-->

<function name="]]>${function.name}<![CDATA["
rcsversion="$]]><![CDATA[Revision$" rcsdate="$]]><![CDATA[Date$">

	<description></description>


</function>]]></echo>
			<echo message="Don't forget to add &lt;function name=&quot;${{function.name}}&quot; /&gt; to the api.xml file." />
		</target>

		<target name="create-rcd" description="Generates a new result code.">
			<input addproperty="project.name"
						 message="Please, enter the name of the project:" />
			<input addproperty="rcd.name"
						 message="Please, enter the name of the new result code:" />
			<property name="xml.file" value="{$specsdir}/${{project.name}}/${{rcd.name}}.rcd" />
			<available property="xml.exists" file="${{xml.file}}" />
			<fail message="The file ${{xml.file}} already exists!" if="xml.exists" />
			<echo file="${{xml.file}}"><![CDATA[<?xml version="1.0" encoding="US-ASCII"?>
<!DOCTYPE resultcode PUBLIC "-//XINS//DTD Result Code//EN"
"http://xins.sourceforge.net/dtd/resultcode.dtd">
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $]]><![CDATA[Id$
-->

<resultcode name="]]>${rcd.name}&quot; value=&quot;${rcd.name}<![CDATA["
rcsversion="$]]><![CDATA[Revision$" rcsdate="$]]><![CDATA[Date$">

	<description></description>


</resultcode>]]></echo>
			<echo message="Don't forget to add &lt;resultcode name=&quot;${{rcd.name}}&quot; /&gt; to the api.xml file." />
		</target>

		<target name="create-type" description="Generates a new result code.">
			<input addproperty="project.name"
						 message="Please, enter the name of the project:" />
			<input addproperty="type.name"
						 message="Please, enter the name of the new type:" />
			<property name="xml.file" value="{$specsdir}/${{project.name}}/${{type.name}}.typ" />
			<available property="xml.exists" file="${{xml.file}}" />
			<fail message="The file ${{xml.file}} already exists!" if="xml.exists" />
			<echo file="${{xml.file}}"><![CDATA[<?xml version="1.0" encoding="US-ASCII"?>
<!DOCTYPE type PUBLIC "-//XINS//DTD Type//EN"
"http://xins.sourceforge.net/dtd/type.dtd">
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $]]><![CDATA[Id$
-->

<type name="]]>${type.name}<![CDATA[" extends="_text"
rcsversion="$]]><![CDATA[Revision$" rcsdate="$]]><![CDATA[Date$">

	<description></description>


</type>]]></echo>
			<echo message="Don't forget to add &lt;type name=&quot;${{type.name}}&quot; /&gt; to the api.xml file." />
		</target>
	</xsl:template>
</xsl:stylesheet>
