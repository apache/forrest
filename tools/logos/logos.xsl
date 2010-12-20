<?xml version="1.0" encoding="utf-8"?>
<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->
<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output indent="yes" method="xml"/>
  <xsl:template match="projects">
    <project default="logo" basedir="../.." name="tmpbuild">
      <target name="logo">
        <xsl:attribute  name = "depends" >init<xsl:for-each select = "project">,<xsl:value-of select="@id" />
          </xsl:for-each>
        </xsl:attribute>
      </target>
      <target name="init">
        <mkdir dir="build"/>
        <mkdir dir="generated"/>
        <xslt in="projects.xml" out="generated/index.html" style="logopage.xsl"/>
        <path id="classpath">
          <fileset dir="../../lib/core">
            <include name="batik*.jar"/>
          </fileset>
          <fileset dir=".">
            <include name="batik*.jar"/>
          </fileset>
          <fileset dir="../../lib/endorsed">
            <include name="*.jar"/>
          </fileset>
        </path>
        <xsl:for-each select = "project">
          <available file="generated/apache-{@id}.svg" property="{@id}.svg.present"/>
        </xsl:for-each>
      </target>
      <xsl:apply-templates select="project"/>
    </project>
  </xsl:template>
  <xsl:template match="project">
    <target>
      <xsl:attribute name = "name" >
        <xsl:value-of select="@id" />
      </xsl:attribute>
      <xsl:attribute name = "unless" >
        <xsl:value-of select="@id" />.svg.present</xsl:attribute>
      <xsl:variable name="bgcolor">
        <xsl:choose>
          <xsl:when test="@bgcolor='$forrest'">#294563</xsl:when>
          <xsl:when test="@bgcolor='$maven'">#003063</xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="@bgcolor"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <xsl:variable name="scale">
        <xsl:choose>
          <xsl:when test="@scale='small'">0.8</xsl:when>
          <xsl:when test="@scale='big'">1.4</xsl:when>
          <xsl:otherwise>1</xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
<!--
  <xsl:variable name="width">520*number($scale)*14</xsl:variable>
  <xsl:variable name="height">51*number($scale)*14</xsl:variable>

apache-jakarta - 350,68
-->
      <xsl:variable name="width">
        <xsl:choose>
          <xsl:when test="@logo='apache-httpd'">
            <xsl:value-of select="572*number($scale)" />
          </xsl:when>
          <xsl:when test="@logo='apache-jakarta'">
            <xsl:value-of select="(300+(string-length(@name)*13))*number($scale)" />
          </xsl:when>
          <xsl:when test="@logo='apachecon'">
            <xsl:value-of select="125*number($scale)" />
          </xsl:when>
          <xsl:when test="@logo='apachecon-wide'">
            <xsl:value-of select="234*number($scale)" />
          </xsl:when>
          <xsl:when test="@logo='apache-asf'">
            <xsl:value-of select="125*number($scale)" />
          </xsl:when>
          <xsl:when test="@logo='apache-thanks'">
            <xsl:value-of select="88*number($scale)" />
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="(450+(string-length(@name)*13))*number($scale)" />
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <xsl:variable name="height">
        <xsl:choose>
          <xsl:when test="@logo='apache-httpd'">
            <xsl:value-of select="53*number($scale)" />
          </xsl:when>
          <xsl:when test="@logo='apache-jakarta'">
            <xsl:value-of select="68*number($scale)" />
          </xsl:when>
          <xsl:when test="@logo='apachecon'">
            <xsl:value-of select="125*number($scale)" />
          </xsl:when>
          <xsl:when test="@logo='apachecon-wide'">
            <xsl:value-of select="60*number($scale)" />
          </xsl:when>
          <xsl:when test="@logo='apache-asf'">
            <xsl:value-of select="125*number($scale)" />
          </xsl:when>
          <xsl:when test="@logo='apache-thanks'">
            <xsl:value-of select="31*number($scale)" />
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="51*number($scale)" />
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <xsl:variable name="svgfile">generated/apache-<xsl:value-of select="@id" />.svg</xsl:variable>
      <copy overwrite="true">
        <xsl:attribute name="file">templates/<xsl:value-of select="@logo" />.svg</xsl:attribute>
        <xsl:attribute name="tofile">
          <xsl:value-of select="$svgfile" />
        </xsl:attribute>
        <filterset>
          <filter token="NAME">
            <xsl:attribute name="value">
              <xsl:value-of select="@name" />
            </xsl:attribute>
          </filter>
          <filter token="NAME-2">
            <xsl:attribute name="value">
              <xsl:value-of select="@name-2" />
            </xsl:attribute>
          </filter>
          <filter token="NAME-3">
            <xsl:attribute name="value">
              <xsl:value-of select="@name-3" />
            </xsl:attribute>
          </filter>
          <filter token="NAME-4">
            <xsl:attribute name="value">
              <xsl:value-of select="@name-4" />
            </xsl:attribute>
          </filter>
          <filter token="NAME-5">
            <xsl:attribute name="value">
              <xsl:value-of select="@name-5" />
            </xsl:attribute>
          </filter>
          <filter token="ID">
            <xsl:attribute name="value">
              <xsl:value-of select="@id" />
            </xsl:attribute>
          </filter>
          <filter token="URL">
            <xsl:attribute name="value">
              <xsl:value-of select="@url" />
            </xsl:attribute>
          </filter>
          <filter token="BGCOLOR">
            <xsl:attribute name="value">
              <xsl:value-of select="$bgcolor" />
            </xsl:attribute>
          </filter>
          <filter token="COLOR">
            <xsl:attribute name="value">
              <xsl:value-of select="@color" />
            </xsl:attribute>
          </filter>
          <filter token="COLOR-2">
            <xsl:attribute name="value">
              <xsl:value-of select="@color-2" />
            </xsl:attribute>
          </filter>
          <filter token="SCALE">
            <xsl:attribute name="value">
              <xsl:value-of select="$scale" />
            </xsl:attribute>
          </filter>
          <filter token="WIDTH">
            <xsl:attribute name="value">
              <xsl:value-of select="$width" />
            </xsl:attribute>
          </filter>
          <filter token="HEIGHT">
            <xsl:attribute name="value">
              <xsl:value-of select="$height" />
            </xsl:attribute>
          </filter>
        </filterset>
      </copy>
      <java fork="yes" classname="org.apache.batik.apps.rasterizer.Main">
        <arg value="{$svgfile}"/>
<!--<arg value="-d"/><arg value="{$pngfile}"/>-->
        <arg value="-scriptSecurityOff"/>
        <classpath refid="classpath" />
      </java>
    </target>
  </xsl:template>
</xsl:stylesheet>
