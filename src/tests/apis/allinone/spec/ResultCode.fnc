<?xml version="1.0" encoding="US-ASCII"?>
<!DOCTYPE function PUBLIC "-//XINS//DTD Function 1.0//EN" "http://xins.sourceforge.net/dtd/function_1_0.dtd">

<function name="ResultCode"
rcsversion="$Revision$" rcsdate="$Date$">

	<description>A function that may return different result codes.</description>

	<input>
		<param name="inputText" required="true" type="_text">
			<description>An example of input for a text.</description>
		</param>
	</input>
	<output>
		<resultcode-ref name="AlreadySet" />
		<param name="outputText" required="true" type="_text">
			<description>An example of output for a text.</description>
		</param>
	</output>

	<example num="1" resultcode="_InvalidRequest">
		<description>Missing parameter.</description>
		<data-example>
			<element-example name="missing-param">
				<attribute-example name="param">inputText</attribute-example>
			</element-example>
		</data-example>
	</example>
	<example num="2" resultcode="AlreadySet">
		<description>The text has already been set.</description>
		<input-example name="inputText">hello</input-example>
		<output-example name="count">1</output-example>
	</example>
	<example num="3">
		<description>A new text was sent.</description>
		<input-example name="inputText">hello you!</input-example>
		<output-example name="outputText">hello you! added.</output-example>
	</example>

</function>