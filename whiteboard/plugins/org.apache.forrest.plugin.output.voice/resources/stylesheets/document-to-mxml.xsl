<?xml version="1.0"?>
<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  license agreements.  See the NOTICE file distributed with
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
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:vxml="http://www.w3.org/2001/vxml" xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xv="http://www.voicexml.org/2002/xhtml+voice" xml:lang="en-US">

  <xsl:template name="voiceNavigation">
    <vxml:form id="main">
      <vxml:var name="activeSection"/>
      <vxml:var name="navigateDir"/>

      <vxml:block>
        <xsl:for-each select="//section[position() > 1]">
          <vxml:assign expr="true">
            <xsl:attribute name="name">
              <xsl:value-of select="@id"/>
            </xsl:attribute>
          </vxml:assign>
        </xsl:for-each>
      </vxml:block>

      <xsl:for-each select="//section">
        <vxml:field>
          <xsl:attribute name="name">
            <xsl:value-of select="@id"/>
          </xsl:attribute>
          <vxml:grammar src="simpleNav.jsgf"/>
          <vxml:prompt timeout="2s">
            Section <xsl:value-of select="position()"/>: <xsl:value-of select="title"/>.
            <vxml:break/>
            <xsl:apply-templates/>
          </vxml:prompt>

          <vxml:filled>
            <vxml:assign name="activeSection">
              <xsl:attribute name="expr">
                <xsl:value-of select="position()"/>
              </xsl:attribute>
            </vxml:assign>
            <vxml:assign name="navigateDir">
              <xsl:attribute name="expr">
                <xsl:value-of select="@id"/>
              </xsl:attribute>
            </vxml:assign>
            <vxml:throw event="navigate.simple"/>
          </vxml:filled>

          <vxml:catch event="noinput">
            <vxml:assign expr="true">
              <xsl:attribute name="name">
                <xsl:value-of select="@id"/>
              </xsl:attribute>
            </vxml:assign>
            <vxml:assign name="activeSection">
              <xsl:attribute name="expr">
                <xsl:value-of select="position()"/>
              </xsl:attribute>
            </vxml:assign>
            <vxml:assign name="navigateDir" expr="'next'"/>
            <vxml:throw event="navigate.simple"/>
          </vxml:catch>
        </vxml:field>
      </xsl:for-each>

      <vxml:field name="navigate_wait_for_good_index">
        <vxml:grammar type="application/srgs">
          #ABNF 1.0;
          language en-us;
          mode voice;
          root $command;
          private $number = 1<xsl:for-each select="//section[position() > 1]"> | <xsl:value-of select="position()+1"/></xsl:for-each>;
          public $command = $number {$ = $$};
        </vxml:grammar>
        <vxml:prompt timeout="5s">
          Say correct section index.
        </vxml:prompt>
        <vxml:filled>
          <vxml:assign name="navigateDir" expr="navigate_wait_for_good_index"/>
          <vxml:throw event="navigate.simple"/>
        </vxml:filled>
        <vxml:catch event="help nomatch noinput" count="1">
          You can say index from 1 to <xsl:value-of select="count(//section)"/>
        </vxml:catch>
        <vxml:catch event="help nomatch noinput" count="2"/>
      </vxml:field>

      <vxml:catch event="navigate.simple">
        <vxml:if cond="navigateDir == 'next'">
          <vxml:assign name="activeSection" expr="activeSection + 1"/>
        <vxml:elseif cond="navigateDir == 'back'"/>
          <vxml:assign name="activeSection" expr="activeSection - 1"/>
        <vxml:else/>
          <vxml:assign name="activeSection" expr="navigateDir"/>
        </vxml:if>

        <vxml:if>
          <xsl:attribute name="cond">activeSection &lt;= 0 || activeSection &gt;<xsl:value-of select="count(//section)"/></xsl:attribute>
          Index <vxml:value expr="activeSection"/> is out of range.
          <vxml:clear namelist="navigate_wait_for_good_index"/>
          <xsl:for-each select="//section">
            <vxml:elseif>
              <xsl:attribute name="cond">activeSection == <xsl:value-of select="position()"/></xsl:attribute>
            </vxml:elseif>
              <vxml:clear>
                <xsl:attribute name="namelist">
                  <xsl:value-of select="@id"/>
                </xsl:attribute>
              </vxml:clear>
          </xsl:for-each>
        </vxml:if>
      </vxml:catch>
    </vxml:form>
  </xsl:template>

  <xsl:template match="title"/>

  <xsl:template match="p">
    <xsl:value-of select="."/>
    <vxml:break/>
  </xsl:template>

  <xsl:template match="/">
    <xsl:apply-templates select="//document"/>
  </xsl:template>

  <xsl:template match="document">
    <html>
      <head>
        <xsl:apply-templates select="//header"/>
        <xsl:apply-templates select="//body"/>
      </head>
      <body ev:event="load" ev:handler="#main">

      </body>
    </html>
  </xsl:template>

  <xsl:template match="header">
    <title><xsl:value-of select="title"/></title>
  </xsl:template>

  <xsl:template match="body">
    <xsl:call-template name="voiceNavigation"/>
  </xsl:template>

</xsl:stylesheet>
