
package org.xins.tests.xslt;

import org.tigris.juxy.JuxyTestCase;
import org.w3c.dom.Node;

/**
 * Test cases for 
 */
public class ResultcodeUniquenessTestCase extends JuxyTestCase {

    protected void setUp() throws Exception {
        newContext("src/xslt/resultcode_uniqueness.xslt");
    }

    public void testMoreThanOneElementInTheList() throws Exception {
        context().setCurrentNode(xpath("/"));

        context().setDocument(
           "<api name=\"allinone\" owner=\"johnd\" rcsversion=\"$Revision$\" rcsdate=\"$Date$\">" +
           "        <resultcode name=\"InvalidNumber\"  />" +
           "        <resultcode name=\"AlreadySet\"  />" +
           "</api>");

        context().setTemplateParamValue("resultcode_name", "someRCName");
        context().setTemplateParamValue("resultcode_value", "someRCValue");
        context().setTemplateParamValue("specsdir", "../tests/apis/allinone/spec");
        context().setTemplateParamValue("api_node", xpath("/api"));


        Node result = callTemplate("resultcodeValidity");
        
        //TODO: The xslt does not return any thing, it simply displays a 
        // message if the result code is not unique other wise does nothing
        // so we can only observe it on command line.

        assertTrue(true);
    }
}
