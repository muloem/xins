<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE function PUBLIC "-//XINS//DTD Function 1.5//EN" "http://xins.sourceforge.net/dtd/function_1_5.dtd">

<function name="DefaultValue"
rcsversion="$Revision$" rcsdate="$Date$">

	<description>An example for default values as input as output parameters.</description>

	<input>
		<param name="inputBoolean" required="false" type="_boolean" default="true">
			<description>An example of input for a boolean with a default value.</description>
		</param>
		<param name="inputInt" required="false" type="_int32" default="33">
			<description>An example of input for an integer with a default value.</description>
		</param>
		<data>
			<!-- The data section includes persons. -->
			<contains>
				<contained element="person" />
			</contains>
			<element name="person">
				<description>The person data.</description>
				<attribute name="gender" required="false" type="Salutation" default="Mister">
					<description>The gender of the person.</description>
				</attribute>
				<!--attribute name="name" required="true" type="portal/Username" default="failing">
					<description>The name of the person.</description>
				</attribute-->
				<attribute name="age" required="false" type="Age" default="35">
					<description>The age of the person.</description>
				</attribute>
				<attribute name="size" required="false" type="_int16" default="170">
					<description>The size of the person in centimeters.</description>
				</attribute>
			</element>
		</data>
	</input>
	<output>
		<!-- XXX param name="outputText" required="false" type="_text" default="Test of default &amp; &quot; { é"-->
		<param name="outputText" required="false" type="_text" default="Test of default">
			<description>An example of output for a text with a default value.</description>
		</param>
	</output>

	<example>
		<description>Example for this data section.</description>
		<input-data-example>
			<element-example name="person">
			</element-example>
			<element-example name="person">
				<attribute-example name="gender">Miss</attribute-example>
				<attribute-example name="age">54</attribute-example>
				<attribute-example name="size">158</attribute-example>
			</element-example>
		</input-data-example>
		<output-example name="outputText">Test of default</output-example>
	</example>

</function>