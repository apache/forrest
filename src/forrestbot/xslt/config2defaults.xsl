<?xml version='1.0'?>
<!--
  Copyright 2002-2004 The Apache Software Foundation

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <!--
    this stylesheet generates a configuration file containing the default
    properties to be used during the forrestbot process (read using the 
    xmlproperty task) from the forrestbot configuration file: forrestbot.conf.xml
  -->

<xsl:output indent="yes"/>

<xsl:template match="defaults">
  <wortel>
    <xsl:for-each select="*">
      <xsl:variable name="elementname">
        <xsl:value-of select="name()"/>
        <xsl:if test="@type">
          <xsl:text>.</xsl:text>
          <xsl:value-of select="@type"/>
        </xsl:if>
      </xsl:variable>
      <xsl:element name="{$elementname}">
        <xsl:for-each select="*">
          <xsl:element name="{name()}">
            <xsl:value-of select="@name"/>
          </xsl:element>
        </xsl:for-each> 
      </xsl:element>
    </xsl:for-each>
  </wortel>
</xsl:template>



</xsl:stylesheet>
