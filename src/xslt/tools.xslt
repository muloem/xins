<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 XSLT that generates ANT targets that execute extra tools for your API code.

 $Id$

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Print the footer -->
	<xsl:template name="tools">
		<xsl:param name="xins_home" />
		<xsl:param name="project_home" />
		<xsl:param name="cvsweb" />

		<target name="-init-tools">
			<path id="tools-cp">
				<fileset dir="{$xins_home}/lib">
					<include name="**/*.jar"/>
				</fileset>
			</path>
			<input addproperty="api.name"
						 message="Please, enter the name of the api:" />
			<available property="api.source.dir" value="apis/${{api.name}}/impl" file="apis/${{api.name}}/impl" type="dir" />
			<fail message="No implementation directory found for API ${{api.name}}" unless="api.source.dir" />
		</target>

		<target name="download-tools" description="Download the dependencies JAR file used by the tools.">
			<get src="http://www.ibiblio.org/maven2/java2html/j2h/1.3.1/j2h-1.3.1.jar"
			     dest="{$xins_home}/lib/j2h.jar"
			     usetimestamp="true" ignoreerrors="true" />
			<get src="http://www.ibiblio.org/maven2/pmd/pmd/3.7/pmd-3.7.jar"
			     dest="{$xins_home}/lib/pmd.jar"
			     usetimestamp="true" ignoreerrors="true" />
			<get src="http://www.ibiblio.org/maven2/jaxen/jaxen/1.1-beta-11/jaxen-1.1-beta-11.jar"
			     dest="{$xins_home}/lib/jaxen.jar"
			     usetimestamp="true" ignoreerrors="true" />
			<get src="http://www.ibiblio.org/maven/checkstyle/jars/checkstyle-4.1.jar"
			     dest="{$xins_home}/lib/checkstyle.jar"
			     usetimestamp="true" ignoreerrors="true" />
			<get src="http://www.ibiblio.org/maven/commons-beanutils/jars/commons-beanutils-1.7.0.jar"
			     dest="{$xins_home}/lib/commons-beanutils.jar"
			     usetimestamp="true" ignoreerrors="true" />
			<get src="http://www.ibiblio.org/maven/antlr/jars/antlr-2.7.6.jar"
			     dest="{$xins_home}/lib/antlr.jar"
			     usetimestamp="true" ignoreerrors="true" />
			<get src="http://www.ibiblio.org/maven2/cobertura/cobertura/1.8/cobertura-1.8.jar"
			     dest="{$xins_home}/lib/cobertura.jar"
			     usetimestamp="true" ignoreerrors="true" />
			<get src="http://www.ibiblio.org/maven2/asm/asm/2.2.1/asm-2.2.1.jar"
			     dest="{$xins_home}/lib/asm.jar"
			     usetimestamp="true" ignoreerrors="true" />
			<get src="http://www.jutils.com/maven/lint4j/jars/lint4j-0.9.1.jar"
			     dest="{$xins_home}/lib/lint4j.jar"
			     usetimestamp="true" ignoreerrors="true" />
			<get src="http://www.ibiblio.org/maven/jdepend/jars/jdepend-2.9.1.jar"
			     dest="${{ant.home}}/lib/jdepend.jar"
			     usetimestamp="true" ignoreerrors="true" />
			<input addproperty="extra-tools.dir"
						 message="Where do you want to install FindBugs and JMeter?" />
			<get src="http://heanet.dl.sourceforge.net/sourceforge/findbugs/findbugs-1.1.2.tar.gz"
			     dest="${{extra-tools.dir}}/findbugs-1.1.2.tar.gz"
			     usetimestamp="true" ignoreerrors="true" />
			<gunzip src="${{extra-tools.dir}}/findbugs-1.1.2.tar.gz" />
			<untar src="${{extra-tools.dir}}/findbugs-1.1.2.tar" dest="${{extra-tools.dir}}" />
			<delete file="${{extra-tools.dir}}/findbugs-1.1.2.tar" />
			<get src="http://www.eu.apache.org/dist/jakarta/jmeter/binaries/jakarta-jmeter-2.2.tgz"
			     dest="${{extra-tools.dir}}/jakarta-jmeter-2.2.tgz"
			     usetimestamp="true" ignoreerrors="true" />
			<gunzip src="${{extra-tools.dir}}/jakarta-jmeter-2.2.tgz" />
			<untar src="${{extra-tools.dir}}/jakarta-jmeter-2.2.tar" dest="${{extra-tools.dir}}" />
			<delete file="${{extra-tools.dir}}/jakarta-jmeter-2.2.tgz" />
		</target>

		<target name="java2html" depends="-init-tools" description="Generates HTML pages which contains the API code.">
			<taskdef name="java2html" classname="com.java2html.Java2HTMLTask">
				<classpath refid="tools-cp" />
			</taskdef>
			<mkdir dir="{$project_home}/build/j2h/${{api.name}}" />
			<java2html
			title="Source X-ref for ${{api.name}}"
			destination="{$project_home}/build/j2h/${{api.name}}"
			footer="no">
				<fileset dir="${{api.source.dir}}">
					<include name="**/*.java" />
				</fileset>
			</java2html>
			<!--copy
			file="{$xins_home}/src/j2h/front.html"
			todir="{$project_home}/build/j2h/${{api.name}}"
			overwrite="true" /-->
			<copy file="{$xins_home}/src/css/j2h/style.css"
			tofile="{$project_home}/build/j2h/${{api.name}}/stylesheet.css"
			overwrite="true" />
		</target>

		<target name="pmd" depends="-init-tools" description="Generate the PMD report for an API.">
			<taskdef name="pmd" classname="net.sourceforge.pmd.ant.PMDTask">
				<classpath refid="tools-cp" />
			</taskdef>
			<property name="build.java.version" value="${{ant.java.version}}" />
			<property name="pmd.rules" value="rulesets/basic.xml,rulesets/unusedcode.xml" />
			<mkdir dir="{$project_home}/build/pmd/${{api.name}}" />
			<pmd rulesetfiles="${{pmd.rules}}" targetjdk="${{build.java.version}}">
				<formatter type="html" toFile="{$project_home}/build/pmd/${{api.name}}/index.html"/>
				<fileset dir="${{api.source.dir}}">
					<include name="**/*.java" />
				</fileset>
			</pmd>
		</target>

		<target name="checkstyle" depends="-init-tools" description="Generate the checkstyle report for an API.">
			<taskdef name="checkstyle" classname="com.puppycrawl.tools.checkstyle.CheckStyleTask">
				<classpath refid="tools-cp" />
			</taskdef>
			<mkdir dir="{$project_home}/build/checkstyle/${{api.name}}" />
			<checkstyle config="{$xins_home}/src/config/checkstyle/config.xml" failOnViolation="false">
				<formatter type="xml" tofile="{$project_home}/build/checkstyle/${{api.name}}/results.xml"/>
				<fileset dir="${{api.source.dir}}">
					<include name="**/*.java"/>
				</fileset>
			</checkstyle>
			<xslt
			in="{$project_home}/build/checkstyle/${{api.name}}/results.xml"
			out="{$project_home}/build/checkstyle/${{api.name}}/index.html"
			style="{$xins_home}/src/xslt/checkstyle/index.xslt" />
			<copy
			file="{$xins_home}/src/css/checkstyle/style.css"
			tofile="{$project_home}/build/checkstyle/${{api.name}}/stylesheet.css" />
		</target>

		<target name="coverage" depends="-init-tools" description="Generate the unit tests code coverage report for an API.">
			<taskdef name="cobertura-instrument" classname="net.sourceforge.cobertura.ant.InstrumentTask">
				<classpath refid="tools-cp" />
			</taskdef>
			<taskdef name="cobertura-report" classname="net.sourceforge.cobertura.ant.ReportTask">
				<classpath refid="tools-cp" />
			</taskdef>
			<delete dir="{$project_home}/build/coverage/${{api.name}}" />
			<mkdir dir="{$project_home}/build/coverage/${{api.name}}" />
			<antcall target="classes-api-${{api.name}}" />
			<cobertura-instrument todir="{$project_home}/build/coverage/${{api.name}}/instrumented-classes">
				<fileset dir="{$project_home}/build/classes-api/${{api.name}}">
					<include name="**/*.class" />
					<exclude name="**/*$Request.class" />
					<exclude name="**/*Result.class" />
				</fileset>
			</cobertura-instrument>
			<copy todir="{$project_home}/build/coverage/${{api.name}}/instrumented-classes" overwrite="false">
				<fileset dir="{$project_home}/build/classes-api/${{api.name}}" includes="**/*.class" />
			</copy>
			<antcall target="war-${{api.name}}">
				<param name="classes.api.dir" value="{$project_home}/build/coverage/${{api.name}}/instrumented-classes" />
			</antcall>
			<!-- unless explicitly set to false, the API will be started at the same time -->
			<property name="test.start.server" value="true" />
			<antcall target="test-${{api.name}}">
				<param name="test.start.server" value="${{test.start.server}}" />
				<param name="classes.api.dir" value="{$project_home}/build/coverage/${{api.name}}/instrumented-classes" />
			</antcall>
			<cobertura-report format="html"	destdir="{$project_home}/build/coverage/${{api.name}}"> <!-- datafile="{$project_home}/build/coverage/${{api.name}}/cobertura.ser" -->
				<fileset dir="${{api.source.dir}}" includes="**/*.java" />
				<fileset dir="{$project_home}/build/java-fundament/${{api.name}}" includes="**/*.java" />
			</cobertura-report>
			<delete file="{$project_home}/cobertura.ser" />
		</target>

		<target name="findbugs" depends="-prepare-classes, -init-tools" description="Generate the FindBugs report for an API.">
			<fail message="Please, specify the findbugs.home property" unless="findbugs.home" />
			<taskdef name="findbugs" classname="edu.umd.cs.findbugs.anttask.FindBugsTask" classpath="${{findbugs.home}}/lib/findbugs-ant.jar" />
			<mkdir dir="{$project_home}/build/findbugs/${{api.name}}" />
			<findbugs home="${{findbugs.home}}"
			output="html"
			outputFile="{$project_home}/build/findbugs/${{api.name}}/index.html" >
				<sourcePath path="${{api.source.dir}}" />
				<class location="{$project_home}/build/classes-api/${{api.name}}" />
				<auxClasspath>
					<path refid="xins.classpath" />
				</auxClasspath>
			</findbugs>
		</target>

		<target name="lint4j" depends="-prepare-classes, -init-tools" description="Generate the Lint4J report for an API.">
			<taskdef name="lint4j" classname="com.jutils.lint4j.ant.Lint4jAntTask">
				<classpath refid="tools-cp" />
			</taskdef>
			<mkdir dir="{$project_home}/build/lint4j/${{api.name}}" />
			<lint4j>
				<xsl:attribute name="packages">
					<xsl:choose>
						<xsl:when test="@domain">
							<xsl:value-of select="@domain" />
							<xsl:text>.*</xsl:text>
						</xsl:when>
						<xsl:otherwise>*</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
				<sourcePath path="${{api.source.dir}}" />
				<classpath>
					<path refid="xins.classpath" />
					<dirset dir="{$project_home}/build">
						<include name="java-fundament/${{api.name}}" />
						<include name="classes-types/${{api.name}}" />
						<include name="classes-api/${{api.name}}" />
					</dirset>
				</classpath>
				<formatters>
					<formatter type="text"/>
					<formatter type="text" toFile="{$project_home}/build/lint4j/${{api.name}}/lint4j-report.txt"/>
					<formatter type="xml" toFile="{$project_home}/build/lint4j/${{api.name}}/lint4j-report.xml"/>
				</formatters>
			</lint4j>
		</target>

		<target name="jdepend" depends="-prepare-classes, -init-tools" description="Generate the JDepend report for an API.">
			<mkdir dir="{$project_home}/build/jdepend/${{api.name}}" />
			<jdepend classpathref="xins.classpath" format="xml" outputfile="{$project_home}/build/jdepend/${{api.name}}/${{api.name}}-jdepend.xml">
				<classespath>
					<pathelement location="{$project_home}/build/classes-api/${{api.name}}"/>
				</classespath>
			</jdepend>
			<xslt in="{$project_home}/build/jdepend/${{api.name}}/${{api.name}}-jdepend.xml"
			out="{$project_home}/build/jdepend/${{api.name}}/index.html"
			style="${{ant.home}}/etc/jdepend.xsl" />
		</target>

		<target name="cvschangelog" depends="-init-tools" description="Generate the CVS change logs report for an API.">
			<mkdir dir="{$project_home}/build/cvschangelog/${{api.name}}" />
			<cvschangelog dir="{$project_home}/apis/${{api.name}}" destfile="{$project_home}/build/cvschangelog/${{api.name}}/changelog.xml" />
			<xslt in="{$project_home}/build/cvschangelog/${{api.name}}/changelog.xml"
			out="index.html"
			style="${{ant.home}}/etc/changelog.xsl">
				<param name="title" expression="Change Log for ${{api.name}} API"/>
				<param name="module" expression="${{api.name}}"/>
				<param name="cvsweb" expression="{$cvsweb}/apis/${{api.name}}"/>
			</xslt>
		</target>

		<target name="jmeter" depends="-init-tools" description="Generate JMeter tests from the function examples.">
			<mkdir dir="{$project_home}/build/jmeter/${{api.name}}" />
			<xslt
			in="apis/${{api.name}}/spec/api.xml"
			out="{$project_home}/build/jmeter/${{api.name}}/${{api.name}}.jmx"
			style="{$xins_home}/src/tools/jmeter/${{api.name}}/api_to_jmx.xslt">
				<xmlcatalog refid="all-dtds" />
				<param name="project_home" expression="{$project_home}" />
			</xslt>
		</target>

		<target name="run-jmeter" depends="-init-tools" description="Execute some JMeter tests.">
			<fail message="Please, specify the jmeter.home property" unless="jmeter.home" />
			<taskdef name="jmeter" classname="org.programmerplanet.ant.taskdefs.jmeter.JMeterTask" classpath="${{jmeter.home}}/extras/ant-jmeter.jar" />
			<property name="jmeter.test" value="{$project_home}/build/jmeter/${{api.name}}/${{api.name}}" />
			<jmeter jmeterhome="${{jmeter.home}}"
			testplan="${{jmeter.test}}.jmx"
			resultlog="${{jmeter.test}}.jlt">
				<property name="jmeter.save.saveservice.output_format" value="xml"/>
				<property name="jmeter.save.saveservice.assertion_results" value="all"/>
				<property name="file_format.testlog" value="2.0"/>
			</jmeter>
			<xslt force="true"
			in="${{jmeter.test}}.jlt"
			out="{$project_home}/build/jmeter/${{api.name}}/index.html"
			style="${{jmeter.home}}/extras/jmeter-results-detail-report.xsl">
			</xslt>
		</target>

		<target name="maven" depends="-init-tools" description="Generates a POM file.">
			<xslt
			in="apis/${{api.name}}/spec/api.xml"
			out="apis/${{api.name}}/pom.xml"
			style="{$xins_home}/src/tools/maven/api_to_pom.xslt">
				<xmlcatalog refid="all-dtds" />
				<param name="api" expression="${{api.name}}" />
				<param name="xins_home" expression="{$xins_home}" />
				<param name="project_home" expression="{$project_home}" />
			</xslt>
		</target>

		<target name="eclipse" depends="-init-tools" description="Generates Eclipse project files.">
			<!-- Create destination directories -->
			<mkdir dir="{$project_home}/build/java-fundament/${{api.name}}" />
			<mkdir dir="{$project_home}/build/java-types/${{api.name}}" />
			<mkdir dir="{$project_home}/build/classes-api/${{api.name}}" />
			<mkdir dir="{$project_home}/build/classes-types/${{api.name}}" />

			<!-- Copy the build file for the API -->
			<copy file="{$xins_home}/demo/xins-project/apis/petstore/nbbuild.xml"
			todir="apis/${{api.name}}" overwrite="false" />
			<replace file="apis/${{api.name}}/nbbuild.xml"
			token="value=&quot;petstore&quot;" value="value=&quot;${{api.name}}&quot;" />
			<replace file="apis/${{api.name}}/nbbuild.xml"
			token="name=&quot;petstore&quot;" value="name=&quot;${{api.name}}&quot;" />

			<!-- Create the xins user library if needed -->
			<replace file="{$xins_home}/src/tools/eclipse/xins-eclipse.userlibraries"
			token="%%XINS_HOME%%" value="{$xins_home}" />

			<!-- Create the project files -->
			<xslt
			in="apis/${{api.name}}/spec/api.xml"
			out="apis/${{api.name}}/.project"
			style="{$xins_home}/src/tools/eclipse/api_to_project.xslt">
				<xmlcatalog refid="all-dtds" />
				<param name="project_home" expression="{$project_home}" />
			</xslt>
			<xslt
			in="apis/${{api.name}}/spec/api.xml"
			out="apis/${{api.name}}/.classpath"
			style="{$xins_home}/src/tools/eclipse/api_to_classpath.xslt">
				<xmlcatalog refid="all-dtds" />
			</xslt>
			<mkdir dir="apis/${{api.name}}/.externalToolBuilders" />
			<xslt
			in="apis/${{api.name}}/spec/api.xml"
			out="apis/${{api.name}}/.externalToolBuilders/${{api.name}} Ant Builder.launch"
			style="{$xins_home}/src/tools/eclipse/api_to_antbuilder.xslt">
				<xmlcatalog refid="all-dtds" />
			</xslt>
			<xslt
			in="apis/${{api.name}}/spec/api.xml"
			out="apis/${{api.name}}/.tomcatplugin"
			style="{$xins_home}/src/tools/eclipse/api_to_tomcatplugin.xslt">
				<xmlcatalog refid="all-dtds" />
				<param name="project_home" expression="{$project_home}" />
			</xslt>
			<xslt
			in="apis/${{api.name}}/spec/api.xml"
			out="apis/${{api.name}}/tomcat-server.xml"
			style="{$xins_home}/src/tools/eclipse/api_to_tomcatserver.xslt">
				<xmlcatalog refid="all-dtds" />
				<param name="project_home" expression="{$project_home}" />
			</xslt>
		</target>

		<target name="xsd2types" depends="-init-tools" description="Generates type files from a set of xsd files.">
			<input addproperty="xsd.dir"
						 message="Please, enter the directory containing the XSD files:" />
			<available property="xsd.dir.exists" file="${{xsd.dir}}" type="dir" />
			<fail message="No directory &quot;${{xsd.dir}}&quot; found for XSD." unless="xsd.dir.exists" />
			<xslt
			basedir="${{xsd.dir}}"
			includes="*.xsd"
			destdir="apis/${{api.name}}/spec"
			extension=".typ"
			style="{$xins_home}/src/xslt/webapp/xsd_to_types.xslt">
				<param name="project_home" expression="{$project_home}" />
				<param name="specsdir" expression="{$project_home}/apis/${{api.name}}/spec" />
			</xslt>
		</target>

		<target name="wsdl2api" depends="-init-tools" description="Generates the XINS API files from the WSDL.">
			<input addproperty="wsdl.file"
						 message="Please, enter the location of the WSDL file:" />
			<available property="wsdl.file.exists" file="${{wsdl.file}}" type="file" />
			<fail message="No WSDL file &quot;${{wsdl.file}}&quot; found." unless="wsdl.file.exists" />
			<!-- TODO If the api exists, ask the user if he want to override the API -->
			<available property="api.exists" file="{$project_home}/apis/${{api.name}}/spec/api.xml" type="file" />
			<fail message="There is an already existing API named ${{api.name}}." if="api.exists" />
			<xslt
			in="${{wsdl.file}}"
			out="apis/${{api.name}}/spec/api.xml"
			style="{$xins_home}/src/xslt/webapp/wsdl_to_api.xslt">
				<param name="project_home" expression="{$project_home}" />
				<param name="specsdir" expression="{$project_home}/apis/${{api.name}}/spec" />
				<param name="api_name" expression="${{api.name}}" />
			</xslt>
		</target>
	</xsl:template>
</xsl:stylesheet>
