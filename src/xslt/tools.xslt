<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 XSLT that generates ANT targets that execute extra tools for your API code.

 $Id$

 Copyright 2003-2006 Orange Nederland Breedband B.V.
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

		<target name="java2html" depends="-init-tools" description="Generates HTML pages which contains the API code.">
			<taskdef name="java2html" classname="com.java2html.Java2HTMLTask">
				<classpath refid="tools-cp" />
			</taskdef>
			<mkdir dir="build/j2h/${{api.name}}" />
			<java2html
			title="Source X-ref for ${{api.name}}"
			destination="build/j2h/${{api.name}}"
			footer="no">
				<fileset dir="${{api.source.dir}}">
					<include name="**/*.java" />
				</fileset>
			</java2html>
			<!--copy
			file="{$xins_home}/src/j2h/front.html"
			todir="build/j2h/${{api.name}}"
			overwrite="true" /-->
			<copy file="{$xins_home}/src/css/j2h/style.css"
			tofile="build/j2h/${{api.name}}/stylesheet.css"
			overwrite="true" />
		</target>

		<target name="pmd" depends="-init-tools" description="Generate the PMD report for an API.">
			<taskdef name="pmd" classname="net.sourceforge.pmd.ant.PMDTask">
				<classpath refid="tools-cp" />
			</taskdef>
			<property name="pmd.rule" value="basic" />
			<mkdir dir="build/pmd/${{api.name}}" />
			<pmd>
				<ruleset>${{pmd.rule}}</ruleset>
				<formatter type="html" toFile="build/pmd/${{api.name}}/index.html" linkPrefix="../../j2h/${{api.name}}/"/>
				<fileset dir="${{api.source.dir}}">
					<include name="**/*.java"/>
				</fileset>
			</pmd>
		</target>

		<target name="checkstyle" depends="-init-tools" description="Generate the checkstyle report for an API.">
			<taskdef name="checkstyle" classname="com.puppycrawl.tools.checkstyle.CheckStyleTask">
				<classpath refid="tools-cp" />
			</taskdef>
			<mkdir dir="build/checkstyle/${{api.name}}" />
			<checkstyle config="{$xins_home}/src/config/checkstyle/config.xml" failOnViolation="false">
				<formatter type="xml" tofile="build/checkstyle/${{api.name}}/results.xml"/>
				<fileset dir="${{api.source.dir}}">
					<include name="**/*.java"/>
				</fileset>
			</checkstyle>
			<style
			in="build/checkstyle/${{api.name}}/results.xml"
			out="build/checkstyle/${{api.name}}/index.html"
			style="{$xins_home}/src/xslt/checkstyle/index.xslt" />
			<copy
			file="{$xins_home}/src/css/checkstyle/style.css"
			tofile="build/checkstyle/${{api.name}}/stylesheet.css" />
		</target>

		<target name="coverage" depends="-init-tools" description="Generate the unit tests code coverage report for an API.">
			<taskdef name="cobertura-instrument" classname="net.sourceforge.cobertura.ant.InstrumentTask">
				<classpath refid="tools-cp" />
			</taskdef>
			<taskdef name="cobertura-report" classname="net.sourceforge.cobertura.ant.ReportTask">
				<classpath refid="tools-cp" />
			</taskdef>
			<delete dir="build/coverage/${{api.name}}" />
			<mkdir dir="build/coverage/${{api.name}}" />
			<antcall target="classes-api-${{api.name}}" />
			<cobertura-instrument todir="build/coverage/${{api.name}}/instrumented-classes">
				<fileset dir="build/classes-api/${{api.name}}" includes="**/*.class" />
			</cobertura-instrument>
			<copy todir="build/coverage/${{api.name}}/instrumented-classes" overwrite="false">
				<fileset dir="build/classes-api/${{api.name}}" includes="**/*.class" />
			</copy>
			<antcall target="war-${{api.name}}">
				<param name="classes.api.dir" value="build/coverage/${{api.name}}/instrumented-classes" />
			</antcall>
			<antcall target="test-${{api.name}}">
				<param name="test.start.server" value="true" />
				<param name="classes.api.dir" value="build/coverage/${{api.name}}/instrumented-classes" />
			</antcall>
			<cobertura-report format="html"	destdir="build/coverage/${{api.name}}"> <!-- datafile="build/coverage/${{api.name}}/cobertura.ser" -->
				<fileset dir="${{api.source.dir}}" includes="**/*.java" />
				<fileset dir="build/java-fundament/${{api.name}}" includes="**/*.java" />
			</cobertura-report>
		</target>

		<target name="findbugs" depends="-prepare-classes, -init-tools" description="Generate the FindBugs report for an API.">
			<taskdef name="findbugs" classname="edu.umd.cs.findbugs.anttask.FindBugsTask">
				<classpath refid="tools-cp" />
			</taskdef>
			<mkdir dir="build/findbugs/${{api.name}}" />
			<findbugs home="${{findbugs.home}}"
			output="text"
			outputFile="build/findbugs/${{api.name}}/${{api.name}}-fb.txt" >
				<sourcePath path="${{api.source.dir}}" />
				<class location="build/classes-api/${{api.name}}" />
				<auxClasspath>
					<path refid="xins.classpath" />
				</auxClasspath>
			</findbugs>
		</target>

		<target name="jdepend" depends="-prepare-classes, -init-tools" description="Generate the JDepend report for an API.">
			<mkdir dir="build/jdepend/${{api.name}}" />
			<jdepend classpathref="xins.classpath" format="xml" outputfile="build/jdepend/${{api.name}}/${{api.name}}-jdepend.xml">
				<classespath>
					<pathelement location="build/classes-api/${{api.name}}"/>
				</classespath>
			</jdepend>
			<style in="build/jdepend/${{api.name}}/${{api.name}}-jdepend.xml"
			out="build/jdepend/${{api.name}}/index.html"
			style="${{ant.home}}/etc/jdepend.xsl" />
		</target>

		<target name="cvschangelog" depends="-init-tools" description="Generate the CVS change logs report an API.">
			<mkdir dir="build/cvschangelog/${{api.name}}" />
			<cvschangelog dir="${{api.source.dir}}/.." destfile="build/cvschangelog/${{api.name}}/changelog.xml" />
			<style in="build/cvschangelog/${{api.name}}/changelog.xml"
			out="index.html"
			style="${{ant.home}}/etc/changelog.xsl">
				<param name="title" expression="Change Log for ${{api.name}} API"/>
				<param name="module" expression="${{api.name}}"/>
				<param name="cvsweb" expression="{$cvsweb}/apis/${{api.name}}"/>
			</style>
		</target>

		<target name="jmeter" depends="-init-tools" description="Execute some JMeter tests.">
			<mkdir dir="build/jmeter/${{api.name}}" />
			<taskdef name="jmeter" classname="org.programmerplanet.ant.taskdefs.jmeter.JMeterTask" classpath="${{jmeter.home}}/extras/ant-jmeter.jar" />
			<jmeter jmeterhome="${{jmeter.home}}"
			testplan="${{jmeter.test}}.jmx"
			resultlog="${{jmeter.test}}.jlt">
				<property name="jmeter.save.saveservice.output_format" value="xml"/>
				<property name="jmeter.save.saveservice.assertion_results" value="all"/>
				<property name="file_format.testlog" value="2.0"/>
			</jmeter>
			<style force="true"
			in="${{jmeter.test}}.jlt"
			out="build/jmeter/${{api.name}}/index.html"
			style="${{jmeter.home}}/extras/jmeter-results-detail-report.xsl">
			</style>
		</target>

		<target name="maven" depends="-init-tools" description="Generates a POM file.">
			<style
			in="apis/${{api.name}}/spec/api.xml"
			out="apis/${{api.name}}/pom.xml"
			style="{$xins_home}/src/tools/maven/api_to_pom.xslt">
				<xmlcatalog refid="all-dtds" />
				<param name="api" expression="${{api.name}}" />
				<param name="xins_home" expression="{$xins_home}" />
				<param name="project_home" expression="{$project_home}" />
			</style>
		</target>
	</xsl:template>
</xsl:stylesheet>
