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

		<target name="create-api" description="Generates a new api file.">
			<input addproperty="api.name"
						 message="Please, enter the name of the api (in lowercase) :" />
			<mkdir dir="{$specsdir}/${{api.name}}" />
			<property name="xml.file" value="{$specsdir}/${{api.name}}/api.xml" />
			<available property="xml.exists" file="${{xml.file}}" />
			<fail message="The file ${{xml.file}} already exists!" if="xml.exists" />
			<input addproperty="api.description"
						 message="Please, enter the description of the new api:" />
			<echo file="${{xml.file}}"><![CDATA[<?xml version="1.0" encoding="US-ASCII"?>
<!DOCTYPE api PUBLIC "-//XINS//DTD XINS API//EN"
"http://xins.sourceforge.net/dtd/api_1_0.dtd">

<api name="]]>${api.name}<![CDATA[" owner="]]>${user.name}<![CDATA["
rcsversion="$]]><![CDATA[Revision$" rcsdate="$]]><![CDATA[Date$">

	<description>]]>${api.description}<![CDATA[</description>

	<impl-java mapping="/">
	</impl-java>

</api>]]></echo>
			<echo message="Don't forget to add &lt;api name=&quot;${{api.name}}&quot; /&gt; to the xins-project.xml file." />
		</target>

		<target name="create-function" description="Generates a new function.">
			<input addproperty="api.name"
						 message="Please, enter the name of the api:" />
			<input addproperty="function.name"
						 message="Please, enter the name of the new function:" />
			<property name="xml.file" value="{$specsdir}/${{api.name}}/${{function.name}}.fnc" />
			<available property="xml.exists" file="${{xml.file}}" />
			<fail message="The file ${{xml.file}} already exists!" if="xml.exists" />
			<input addproperty="function.description"
						 message="Please, enter the description of the new function:" />
			<echo file="${{xml.file}}"><![CDATA[<?xml version="1.0" encoding="US-ASCII"?>
<!DOCTYPE function PUBLIC "-//XINS//DTD Function//EN"
"http://xins.sourceforge.net/dtd/function_1_0.dtd">

<function name="]]>${function.name}<![CDATA["
rcsversion="$]]><![CDATA[Revision$" rcsdate="$]]><![CDATA[Date$">

	<description>]]>${function.description}<![CDATA[</description>

</function>]]></echo>
			<echo message="Don't forget to add &lt;function name=&quot;${{function.name}}&quot; /&gt; to the api.xml file." />
		</target>

		<target name="create-rcd" description="Generates a new result code.">
			<input addproperty="api.name"
						 message="Please, enter the name of the api:" />
			<input addproperty="rcd.name"
						 message="Please, enter the name of the new result code:" />
			<property name="xml.file" value="{$specsdir}/${{api.name}}/${{rcd.name}}.rcd" />
			<available property="xml.exists" file="${{xml.file}}" />
			<fail message="The file ${{xml.file}} already exists!" if="xml.exists" />
			<input addproperty="rcd.description"
						 message="Please, enter the description of the new result code:" />
			<echo file="${{xml.file}}"><![CDATA[<?xml version="1.0" encoding="US-ASCII"?>
<!DOCTYPE resultcode PUBLIC "-//XINS//DTD Result Code//EN"
"http://xins.sourceforge.net/dtd/resultcode_1_0.dtd">

<resultcode name="]]>${rcd.name}&quot; value=&quot;${rcd.name}<![CDATA["
rcsversion="$]]><![CDATA[Revision$" rcsdate="$]]><![CDATA[Date$">

	<description>]]>${rcd.description}<![CDATA[</description>

</resultcode>]]></echo>
			<echo message="Don't forget to add &lt;resultcode name=&quot;${{rcd.name}}&quot; /&gt; to the api.xml file." />
		</target>

		<target name="create-type" description="Generates a new result code.">
			<input addproperty="api.name"
						 message="Please, enter the name of the api:" />
			<input addproperty="type.name"
						 message="Please, enter the name of the new type:" />
			<property name="xml.file" value="{$specsdir}/${{api.name}}/${{type.name}}.typ" />
			<available property="xml.exists" file="${{xml.file}}" />
			<fail message="The file ${{xml.file}} already exists!" if="xml.exists" />
			<input addproperty="type.description"
						 message="Please, enter the description of the new type:" />
			<echo file="${{xml.file}}"><![CDATA[<?xml version="1.0" encoding="US-ASCII"?>
<!DOCTYPE type PUBLIC "-//XINS//DTD Type//EN"
"http://xins.sourceforge.net/dtd/type_1_0.dtd">

<type name="]]>${type.name}<![CDATA[" extends="_text"
rcsversion="$]]><![CDATA[Revision$" rcsdate="$]]><![CDATA[Date$">

	<description>]]>${type.description}<![CDATA[</description>

</type>]]></echo>
			<echo message="Don't forget to add &lt;type name=&quot;${{type.name}}&quot; /&gt; to the api.xml file." />
		</target>
	</xsl:template>
</xsl:stylesheet>
