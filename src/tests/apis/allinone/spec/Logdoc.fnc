<?xml version="1.0" encoding="US-ASCII"?>
<!DOCTYPE function PUBLIC "-//XINS//DTD Function 1.0//EN" "http://xins.sourceforge.net/dtd/function_1_0.dtd">

<function name="Logdoc"
rcsversion="$Revision$" rcsdate="$Date$">

	<description>A function that uses logdoc as logging system.</description>

	<input>
		<param name="inputText" required="true" type="_text">
			<description>An example of input for a text.</description>
		</param>
	</input>
	<output>
		<resultcode-ref name="InvalidNumber" />
	</output>

	<example num="1" resultcode="InvalidNumber">
		<description>The entered input is not a number.</description>
		<input-example name="inputText">foo</input-example>
	</example>
	<example num="2">
		<description>The entered text is a number</description>
		<input-example name="inputText">12000</input-example>
	</example>
</function>