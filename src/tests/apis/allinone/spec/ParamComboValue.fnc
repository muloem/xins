<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE function PUBLIC "-//XINS//DTD Function 1.5//EN" "http://xins.sourceforge.net/dtd/function_1_5.dtd">

<function name="ParamComboValue"
rcsversion="$Revision$" rcsdate="$Date$">

	<description>A function to test the param-combo based on a parameter value.</description>
	<input>
		<param name="salutation" required="true" type="Salutation">
			<description>The gender of the person.</description>
		</param>
		<param name="maidenName" required="false" type="_text">
			<description>The maiden name.</description>
		</param>
		<param name="surname" required="false" type="_text">
			<description>The name of the person.</description>
		</param>
		<param name="country" required="false" type="_text">
			<description>The country from which this person comes from.</description>
		</param>
		<param name="nationality" required="false" type="_text">
			<description>The nationality of the person.</description>
		</param>
		<param name="passportNumber" required="false" type="_text">
			<description>The passport number of the person.</description>
		</param>

		<!-- If the salutation is Madam, the maiden name is required -->
		<param-combo type="inclusive-or">
			<param-ref name="salutation" value="Madam" />
			<param-ref name="maidenName" />
		</param-combo>

		<!-- If the country is Canada, the nationality should not be filled, otherwise the nationality should be filled -->
		<param-combo type="exclusive-or">
			<param-ref name="country" value="Canada" />
			<param-ref name="nationality" />
		</param-combo>

		<!-- Passport number and nationality are required if country is Other. If country is not Other, the nationality and the password should not be set. -->
		<!--param-combo type="all-or-none">
			<param-ref name="country" value="Other" />
			<param-ref name="nationality" />
			<param-ref name="passportNumber" />
		</param-combo-->
		
	</input>
	<output>
	</output>
</function>
