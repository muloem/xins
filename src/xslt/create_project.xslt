<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 XSLT that generates ANT targets that can be used to create a new api,
 a new function, a new type or a new result code.

 $Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Print the footer -->
	<xsl:template name="createproject">

		<target name="create-api" description="Generates a new api specification file.">
			<input addproperty="api.name"
						 message="Please, enter the name of the api (in lowercase):" />
			<property name="api.specdir" value="apis/${{api.name}}/spec" />
			<mkdir dir="${{api.specdir}}" />
			<property name="xml.file" value="${{api.specdir}}/api.xml" />
			<available property="xml.exists" file="${{xml.file}}" />
			<fail message="The file ${{xml.file}} already exists!" if="xml.exists" />
			<input addproperty="api.description"
						 message="Please, enter the description of the new api:" />
			<echo file="${{xml.file}}"><![CDATA[<?xml version="1.0" encoding="US-ASCII"?>
<!DOCTYPE api PUBLIC "-//XINS//DTD XINS API 1.0//EN" "http://xins.sourceforge.net/dtd/api_1_0.dtd">

<api name="]]>${api.name}<![CDATA[" owner="]]>${user.name}<![CDATA["
rcsversion="$]]><![CDATA[Revision$" rcsdate="$]]><![CDATA[Date$">

	<description>]]>${api.description}<![CDATA[</description>

</api>]]></echo>
			<replace file="xins-project.xml">
				<replacetoken><![CDATA[</project>]]></replacetoken>
				<replacevalue><![CDATA[	<api name="]]>${api.name}<![CDATA[" />
</project>]]></replacevalue>
			</replace>
			<input message="Do you want to create an implementation for the API (y/n)?"
			       validargs="y,n"
			       addproperty="do.implementation" />
			<condition property="create.impl">
				<equals arg1="y" arg2="${{do.implementation}}"/>
			</condition>
			<antcall target="create-impl" />
			<input message="Do you want to define some environments for the API (y/n)?"
			       validargs="y,n"
			       addproperty="do.environments" />
			<condition property="create.environments">
				<equals arg1="y" arg2="${{do.environments}}"/>
			</condition>
			<antcall target="create-environments" />
			<replace file="xins-project.xml" token="$${{api.name}}" value="${{api.name}}" />
			<!--echo message="Don't forget to add &lt;api name=&quot;${{api.name}}&quot; /&gt; to the xins-project.xml file." /-->
		</target>

		<target name="create-impl" if="create.impl">
			<property name="api.impldir" value="apis/${{api.name}}/impl" />
			<mkdir dir="${{api.impldir}}" />
			<property name="impl.file" value="${{api.impldir}}/impl.xml" />
			<available property="impl.exists" file="${{impl.file}}" />
			<fail message="The file ${{impl.file}} already exists!" if="impl.exists" />
			<echo file="${{impl.file}}"><![CDATA[<?xml version="1.0" encoding="US-ASCII"?>
<!DOCTYPE impl PUBLIC "-//XINS//DTD Implementation 1.0//EN" "http://xins.sourceforge.net/dtd/impl_1_0.dtd">

<impl>
</impl>
]]></echo>
			<replace file="xins-project.xml">
				<replacetoken><![CDATA[	<api name="]]>${api.name}<![CDATA[" />]]></replacetoken>
				<replacevalue><![CDATA[	<api name="]]>${api.name}<![CDATA[">
		<impl />
	</api>]]></replacevalue>
			</replace>
		</target>

		<target name="create-environments" if="create.environments">
			<property name="environments.file" value="apis/${{api.name}}/environments.xml" />
			<available property="environments.exists" file="${{environments.file}}" />
			<fail message="The file ${{environments.file}} already exists!" if="environments.exists" />
			<echo file="${{environments.file}}"><![CDATA[<?xml version="1.0" encoding="US-ASCII"?>
<!DOCTYPE environments PUBLIC "-//XINS//DTD Environments 1.0//EN" "http://xins.sourceforge.net/dtd/environments_1_0.dtd">

<environments>

	<environment id="localhost" url="http://127.0.0.1:8080/]]>${api.name}<![CDATA[/" />

</environments>
]]></echo>
			<replace file="xins-project.xml">
				<replacetoken><![CDATA[	<api name="]]>${api.name}<![CDATA[" />]]></replacetoken>
				<replacevalue><![CDATA[	<api name="]]>${api.name}<![CDATA[">
		<environments />
	</api>]]></replacevalue>
			</replace>
			<replace file="xins-project.xml">
				<replacetoken><![CDATA[	<api name="]]>${api.name}<![CDATA[">
		<impl />
	</api>]]></replacetoken>
				<replacevalue><![CDATA[	<api name="]]>${api.name}<![CDATA[">
		<impl />
		<environments />
	</api>]]></replacevalue>
			</replace>
		</target>

		<target name="create-function" description="Generates a new function specification file.">
			<input addproperty="api.name"
						 message="Please, enter the name of the api:" />
			<input addproperty="function.name"
						 message="Please, enter the name of the new function:" />
			<property name="xml.file" value="apis/${{api.name}}/spec/${{function.name}}.fnc" />
			<available property="xml.exists" file="${{xml.file}}" />
			<fail message="The file ${{xml.file}} already exists!" if="xml.exists" />
			<input addproperty="function.description"
						 message="Please, enter the description of the new function:" />
			<echo file="${{xml.file}}"><![CDATA[<?xml version="1.0" encoding="US-ASCII"?>
<!DOCTYPE function PUBLIC "-//XINS//DTD Function 1.0//EN" "http://xins.sourceforge.net/dtd/function_1_0.dtd">

<function name="]]>${function.name}<![CDATA["
rcsversion="$]]><![CDATA[Revision$" rcsdate="$]]><![CDATA[Date$">

	<description>]]>${function.description}<![CDATA[</description>

</function>]]></echo>
			<replace file="apis/${{api.name}}/spec/api.xml">
				<replacetoken><![CDATA[</description>
]]></replacetoken>
				<replacevalue><![CDATA[</description>

	<function name="]]>${function.name}<![CDATA[" />]]></replacevalue>
			</replace>
			<replace file="apis/${{api.name}}/spec/api.xml" token="$${{function.name}}" value="${{function.name}}" />
			<!-- Make sure that there is always a blank line before the </api> -->
			<replace file="apis/${{api.name}}/spec/api.xml">
				<replacetoken><![CDATA[/>
</api>]]></replacetoken>
				<replacevalue><![CDATA[/>

</api>]]></replacevalue>
			</replace>
			<echo message="Don't forget to add &lt;function name=&quot;${{function.name}}&quot; /&gt; to the api.xml file." />
		</target>

		<target name="create-rcd" description="Generates a new result code specification file.">
			<input addproperty="api.name"
						 message="Please, enter the name of the api:" />
			<input addproperty="rcd.name"
						 message="Please, enter the name of the new result code:" />
			<property name="xml.file" value="apis/${{api.name}}/spec/${{rcd.name}}.rcd" />
			<available property="xml.exists" file="${{xml.file}}" />
			<fail message="The file ${{xml.file}} already exists!" if="xml.exists" />
			<input addproperty="rcd.description"
						 message="Please, enter the description of the new result code:" />
			<echo file="${{xml.file}}"><![CDATA[<?xml version="1.0" encoding="US-ASCII"?>
<!DOCTYPE resultcode PUBLIC "-//XINS//DTD Result Code 1.0//EN" "http://xins.sourceforge.net/dtd/resultcode_1_0.dtd">

<resultcode name="]]>${rcd.name}&quot; value=&quot;${rcd.name}<![CDATA["
rcsversion="$]]><![CDATA[Revision$" rcsdate="$]]><![CDATA[Date$">

	<description>]]>${rcd.description}<![CDATA[</description>

</resultcode>]]></echo>
			<replace file="apis/${{api.name}}/spec/api.xml">
				<replacetoken><![CDATA[
</api>]]></replacetoken>
				<replacevalue><![CDATA[	<resultcode name="]]>${rcd.name}<![CDATA[" />

</api>]]></replacevalue>
			</replace>
			<replace file="apis/${{api.name}}/spec/api.xml" token="$${{rcd.name}}" value="${{rcd.name}}" />
			<!--echo message="Don't forget to add &lt;resultcode name=&quot;${{rcd.name}}&quot; /&gt; to the api.xml file." /-->
		</target>

		<target name="create-type" description="Generates a new type specification file.">
			<input addproperty="api.name"
						 message="Please, enter the name of the api:" />
			<input addproperty="type.name"
						 message="Please, enter the name of the new type:" />
			<property name="xml.file" value="apis/${{api.name}}/spec/${{type.name}}.typ" />
			<available property="xml.exists" file="${{xml.file}}" />
			<fail message="The file ${{xml.file}} already exists!" if="xml.exists" />
			<input addproperty="type.description"
						 message="Please, enter the description of the new type:" />
			<echo file="${{xml.file}}"><![CDATA[<?xml version="1.0" encoding="US-ASCII"?>
<!DOCTYPE type PUBLIC "-//XINS//DTD Type 1.0//EN" "http://xins.sourceforge.net/dtd/type_1_0.dtd">

<type name="]]>${type.name}<![CDATA[" rcsversion="$]]><![CDATA[Revision$" rcsdate="$]]><![CDATA[Date$">

	<description>]]>${type.description}<![CDATA[</description>

</type>]]></echo>
			<echo message="Don't forget to add &lt;type name=&quot;${{type.name}}&quot; /&gt; to the api.xml file." />
		</target>

		<target name="create-logdoc" description="Generates a new logdoc file for the API.">
			<input addproperty="api.name"
						 message="Please, enter the name of the api:" />
			<property name="xml.file" value="apis/${{api.name}}/spec/impl/log.xml" />
			<property name="xml.file2" value="apis/${{api.name}}/spec/impl/translation-bundle-en_US.xml" />
			<available property="xml.exists" file="${{xml.file}}" />
			<available property="xml.exists2" file="${{xml.file2}}" />
			<fail message="The file ${{xml.file}} already exists!" if="xml.exists" />
			<fail message="The file ${{xml.file2}} already exists!" if="xml.exists2" />
			<property name="domain">
				<xsl:attribute name="value">
					<xsl:value-of select="document($project_file)/project/@domain" />
				</xsl:attribute>
			</property>
			<echo file="${{xml.file}}"><![CDATA[<?xml version="1.0" encoding="US-ASCII"?>
<!DOCTYPE log PUBLIC "-//XINS//DTD XINS Logdoc 1.0//EN" "http://xins.sourceforge.net/dtd/log_1_0.dtd">

<log default-locale="en_US" domain="]]>${domain}.${api.name}.api<![CDATA[">

	<translation-bundle locale="en_US" />

	<group id="exampleid" name="Example">
		<entry id="10000" level="DEBUG">
			<description>Example of logdoc with some parameters.</description>
			<param name="functionName" />
			<param name="number" nullable="false" type="int32" />
		</entry>
		<entry id="10001" level="ERROR" exception="true">
			<description>Example with an exception.</description>
		</entry>
	</group>
</log>
]]></echo>
			<echo file="${{xml.file2}}"><![CDATA[<?xml version="1.0" encoding="US-ASCII"?>
<!DOCTYPE translation-bundle PUBLIC "-//XINS//DTD XINS Translation Bundle 1.0//EN" "http://xins.sourceforge.net/dtd/translation-bundle_1_0.dtd">

<translation-bundle>
	<translation entry="10000">Example of logdoc with the parameters <value-of-param name="functionName" format="quoted" /> and <value-of-param name="number" />.</translation>
	<translation entry="10001">Example of an exception.</translation>
</translation-bundle>
]]></echo>
			<replace file="apis/${{api.name}}/impl/impl.xml">
				<replacetoken><![CDATA[<impl>]]></replacetoken>
				<replacevalue><![CDATA[<impl>
	<logdoc />]]></replacevalue>
			</replace>
		</target>
	</xsl:template>
</xsl:stylesheet>
