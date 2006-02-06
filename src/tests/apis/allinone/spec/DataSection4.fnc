<?xml version="1.0" encoding="US-ASCII"?>
<!DOCTYPE function PUBLIC "-//XINS//DTD Function 1.4//EN" "http://xins.sourceforge.net/dtd/function_1_4.dtd">

<function name="DataSection4"
rcsversion="$Revision$" rcsdate="$Date$">

	<description>An example for the input data section with multiple root elements.</description>

	<input>
		<data>
			<!-- The data section includes packet or envelope elements. -->
			<contains>
				<contained element="person" />
				<contained element="address" />
			</contains>
			<element name="person">
				<description>The person data.</description>
				<attribute name="gender" required="true" type="Salutation">
					<description>The gender of the person.</description>
				</attribute>
				<attribute name="name" required="true" type="_text">
					<description>The name of the person.</description>
				</attribute>
				<attribute name="age" required="false" type="Age">
					<description>The age of the person.</description>
				</attribute>
				<attribute name="size" required="false" type="_int16">
					<description>The size of the person in centimeters.</description>
				</attribute>
				<attribute name="birthdate" required="true" type="_date">
					<description>The birth date of the person.</description>
				</attribute>
				<attribute name="deathdate" required="false" type="_date">
					<description>The death date of the person.</description>
				</attribute>
				<attribute-combo type="inclusive-or">
					<attribute-ref name="birthdate" />
					<attribute-ref name="deathdate" />
					<attribute-ref name="age" />
				</attribute-combo>
				<attribute-combo type="exclusive-or">
					<attribute-ref name="birthdate" />
					<attribute-ref name="deathdate" />
					<attribute-ref name="age" />
				</attribute-combo>
				<attribute-combo type="all-or-none">
					<attribute-ref name="gender" />
					<attribute-ref name="name" />
					<attribute-ref name="age" />
				</attribute-combo>
				<attribute-combo type="not-all">
					<attribute-ref name="birthdate" />
					<attribute-ref name="deathdate" />
					<attribute-ref name="age" />
				</attribute-combo>
			</element>
			<element name="address">
				<description>The address.</description>
				<contains>
					<pcdata />
				</contains>
			</element>
		</data>
	</input>

	<example>
		<description>Example for this data section.</description>
		<input-data-example>
			<element-example name="person">
				<attribute-example name="gender">Mister</attribute-example>
				<attribute-example name="name">Doe</attribute-example>
				<attribute-example name="age">55</attribute-example>
				<attribute-example name="size">168</attribute-example>
				<attribute-example name="birthdate">19551205</attribute-example>
			</element-example>
			<element-example name="person">
				<attribute-example name="gender">Miss</attribute-example>
				<attribute-example name="name">Doe</attribute-example>
				<attribute-example name="age">54</attribute-example>
				<attribute-example name="size">158</attribute-example>
				<attribute-example name="birthdate">19561105</attribute-example>
			</element-example>
			<element-example name="address">
				<pcdata-example>22 Washinton square, 54632 London, UK</pcdata-example>
			</element-example>
		</input-data-example>
	</example>

</function>