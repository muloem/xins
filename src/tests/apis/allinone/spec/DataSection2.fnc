<?xml version="1.0" encoding="US-ASCII"?>
<!DOCTYPE function PUBLIC "-//XINS//DTD Function 1.0//EN" "http://xins.sourceforge.net/dtd/function_1_0.dtd">

<function name="DataSection2"
rcsversion="$Revision$" rcsdate="$Date$">

	<description>An example of a data section.</description>

	<input>
		<param name="inputText" required="false" type="_text">
			<description>An example of input for a text.</description>
		</param>
	</input>
	<output>
		<data contains="packet">
			<!-- packet has 1 attribute destination.
			     It also can contain several product sub-element. -->
			<element name="packet">
				<description>The packet.</description>
				<contains>
					<contained element="product" />
				</contains>
				<attribute name="destination" required="true" type="_text">
					<description>The destination of the packet.</description>
				</attribute>
			</element>
			<element name="product">
				<description>The packet.</description>
				<attribute name="id" required="true" type="_int64">
					<description>The id of the product.</description>
				</attribute>
				<attribute name="price" required="false" type="_int32">
					<description>The description of the product.</description>
				</attribute>
			</element>
		</data>
	</output>

	<example num="1">
		<description>Example for this data section.</description>
		<data-example>
			<element-example name="packet">
				<attribute-example name="destination">20 West Street, New York</attribute-example>
				<element-example name="product">
					<attribute-example name="id">123456</attribute-example>
					<attribute-example name="price">12</attribute-example>
				</element-example>
				<element-example name="product">
					<attribute-example name="id">321654</attribute-example>
					<attribute-example name="price">23</attribute-example>
				</element-example>
			</element-example>
			<element-example name="packet">
				<attribute-example name="destination">55 Kennedy lane, Washinton DC</attribute-example>
				<element-example name="product">
					<attribute-example name="id">123456</attribute-example>
					<attribute-example name="price">12</attribute-example>
				</element-example>
			</element-example>
		</data-example>
	</example>

</function>