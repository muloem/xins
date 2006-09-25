<?xml version="1.0" encoding="US-ASCII"?>
<!DOCTYPE function PUBLIC "-//XINS//DTD Function 1.5//EN" "http://xins.sourceforge.net/dtd/function_1_5.dtd">

<function name="InvalidResponse" rcsversion="$Revision$" rcsdate="$Date$">

	<description>Test invalid responses.</description>

   <input>
      <param name="errorCode" required="false" type="_text">
			<description>The error code to (try to) return.</description>
      </param>
   </input>

	<output>
		<param name="outputText1" required="true" type="_text">
			<description>A required output parameter.</description>
		</param>
		<param name="pattern" required="false" type="allinone/IPAddress">
			<description>A pattern-type parameter.</description>
		</param>
	</output>
</function>
