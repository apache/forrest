<?xml version="1.0"?>
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

<!--+
    | Transforms last section of meeting memos into iCal todo lists
    +-->

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:date="http://exslt.org/dates-and-times"
                extension-element-prefixes="date">

  <xsl:import href="lm://transform-exslt/date/functions/add/date.add.template.xsl" />
  <!--xsl:import href="date.add.template.xsl" /-->

  <xsl:output method="text"/>
  <xsl:param name="person"/>
  <xsl:param name="date"/>
  <xsl:param name="defaultTimeSpan"/>

  <xsl:variable name="timeSpanString" >
    <xsl:value-of select='concat("P", $defaultTimeSpan)'/>
  </xsl:variable>

  <xsl:variable name="tdate" >
    <xsl:call-template name="date:add">
      <xsl:with-param name="date-time" select="$date" />
      <xsl:with-param name="duration" select="$timeSpanString" />
    </xsl:call-template>
  </xsl:variable>

  <xsl:variable name="bgndate" select="translate($date,'-','')"/>
  <xsl:variable name="duedate" select="translate($tdate,'-','')"/>

  <xsl:template match="/document"
>BEGIN:VCALENDAR
VERSION:2.0
PRODID:-//Divvun//Forrest Wiki iCal//EN
CALSCALE:GREGORIAN
<xsl:apply-templates select="body/section[last()]"/>END:VCALENDAR
</xsl:template>

  <xsl:template match="body/section[last()]">
    <xsl:apply-templates select="section[contains(title,$person)]"/>
  </xsl:template>

  <xsl:template match="section[contains(title,$person)]">
    <xsl:apply-templates select="ul/li"/>
  </xsl:template>

  <xsl:template match="section/ul/li"
>BEGIN:VTODO
DTSTAMP;VALUE=DATE:<xsl:value-of select="$bgndate"/>
DUE;VALUE=DATE:<xsl:value-of select="$duedate"/>
SUMMARY:<xsl:choose>
<xsl:when test="./ul">
<xsl:value-of select="text()"/>
</xsl:when>
<xsl:otherwise>
<xsl:value-of select="string(.)"/>
</xsl:otherwise>
</xsl:choose>
<xsl:apply-templates select="ul"/>
<xsl:apply-templates select="link"/>
END:VTODO
</xsl:template>

  <xsl:template match="ul/li/ul">
DESCRIPTION:<xsl:apply-templates select="li"/></xsl:template>

  <xsl:template match="ul/li/ul/li">• <xsl:apply-templates/>\n</xsl:template>

  <xsl:template match="link">
URL;VALUE=URI:<xsl:value-of select="@href"/>
</xsl:template>

</xsl:stylesheet>
