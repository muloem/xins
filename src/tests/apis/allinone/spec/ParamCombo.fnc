<?xml version="1.0" encoding="US-ASCII"?>
<!DOCTYPE function PUBLIC "-//XINS//DTD Function 1.1//EN" "http://xins.sourceforge.net/dtd/function_1_1.dtd">

<function name="ParamCombo"
rcsversion="$Revision$" rcsdate="$Date$">

	<description>A function to test the param-combo.</description>
	<input>
		<param name="birthDate" required="false" type="_date">
			<description>The birth date.</description>
		</param>
		<param name="birthYear" required="false" type="_int32">
			<description>The birth date's year.</description>
		</param>
		<param name="birthMonth" required="false" type="_int32">
			<description>The birth date's month.</description>
		</param>
		<param name="birthDay" required="false" type="_int32">
			<description>The birth date's day.</description>
		</param>
		<param name="birthCountry" required="false" type="_text">
			<description>The country where the person is borned.</description>
		</param>
		<param name="birthCity" required="false" type="_text">
			<description>The city where the person is borned.</description>
		</param>
		<param name="age" required="false" type="Age">
			<description>An example of input for a int8 type with a minimum and maximum.</description>
		</param>
		<!-- One of the two parameters must be filled but not both -->
		<param-combo type="exclusive-or">
			<param-ref name="birthDate" />
			<param-ref name="birthYear" />
			<param-ref name="age"       />
		</param-combo>
		<!-- At least one of the two parameters must be filled -->
		<param-combo type="inclusive-or">
			<param-ref name="birthCountry" />
			<param-ref name="birthCity"    />
		</param-combo>
		<!-- These parameters must be filled together or not filled at all -->
		<param-combo type="all-or-none">
			<param-ref name="birthYear"  />
			<param-ref name="birthMonth" />
			<param-ref name="birthDay"   />
		</param-combo>
	</input>
	<output>
		<param name="outputMessage" required="true" type="_text">
			<description>The output message with the information passed in the input.</description>
		</param>
	</output>

	<example resultcode="_InvalidRequest">
		<description>Invalid parameter.</description>
		<data-example>
			<element-example name="param-combo">
				<attribute-example name="type">inclusive-or</attribute-example>
				<element-example name="param">
					<attribute-example name="name">birthCountry</attribute-example>
				</element-example>
				<element-example name="param">
					<attribute-example name="name">birthCity</attribute-example>
				</element-example>
			</element-example>
			<element-example name="param-combo">
				<attribute-example name="type">exclusive-or</attribute-example>
				<element-example name="param">
					<attribute-example name="name">birthDate</attribute-example>
				</element-example>
				<element-example name="param">
					<attribute-example name="name">birthYear</attribute-example>
				</element-example>
				<element-example name="param">
					<attribute-example name="name">age</attribute-example>
				</element-example>
			</element-example>
		</data-example>
	</example>
	<example>
		<description>Correct combination.</description>
		<input-example name="birthYear">1973</input-example>
		<input-example name="birthMonth">8</input-example>
		<input-example name="birthDay">19</input-example>
		<input-example name="birthCountry">France</input-example>
		<output-example name="outputMessage">You are 31 years old.</output-example>
	</example>
</function>