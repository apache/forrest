<?xml version="1.0"?>
<!--
  Copyright 2002-2005 The Apache Software Foundation or its licensors,
  as applicable.

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
<xsl:stylesheet  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:ss="urn:schemas-microsoft-com:office:spreadsheet"
  xmlns:o="urn:schemas-microsoft-com:office:office"
>

  <xsl:template match="ss:Workbook">
   <document>
    <header>
     <title><xsl:value-of select="*/o:Title"/></title>
    </header>
    <body>
      <p><xsl:value-of select="*/o:Description"/></p>
      <xsl:apply-templates/>
    </body>
   </document>
  </xsl:template>

  <xsl:template match="ss:Worksheet"> 
    <section>
      <xsl:attribute name="id"><xsl:value-of select="@ss:Name"/></xsl:attribute>
      <title><xsl:value-of select="@ss:Name"/></title>
      <xsl:apply-templates/>
    </section>
  </xsl:template>

  <xsl:template match="ss:Table">
    <xsl:apply-templates select="ss:Row[ss:Cell[position()='1']='text']"   mode="text"/>
    <xsl:apply-templates select="ss:Row[ss:Cell[position()='1']='figure']" mode="figure"/>
    <table>
      <caption><xsl:value-of select="parent::ss:Worksheet/@ss:Name"/></caption>
      <xsl:apply-templates select="ss:Row[position()=1]" mode="header"/>
      <xsl:apply-templates 
           select="ss:Row[ss:Cell!='' and position()!=1 
              and ss:Cell[position()='1']!='figure' 
              and ss:Cell[position()='1']!='text']"/>
    </table>
  </xsl:template>


  <xsl:template match="ss:Row" mode="header">
    <tr><xsl:apply-templates select="ss:Cell[.!='']" mode="header"/></tr>
  </xsl:template>
  <xsl:template match="ss:Cell" mode="header">
    <th><xsl:value-of select="ss:Data"/></th>
  </xsl:template>

  <xsl:template match="ss:Row">
    <tr>
      <xsl:apply-templates select="ss:Cell[.!='']"/>
    </tr>
  </xsl:template>
  <xsl:template match="ss:Cell">
    <td>
      <xsl:choose>
      <xsl:when test="contains(ss:Data, '#')">
        <xsl:attribute name="style">background-color:<xsl:value-of select="ss:Data"/>;
        <xsl:if test="contains(ss:Data, '#000000')">
          color:#ffffff
        </xsl:if>
        </xsl:attribute>
        <xsl:value-of select="ss:Data" />
      </xsl:when>
      <xsl:when test="contains(ss:Data, '.png') or contains(ss:Data, '.gif') or contains(ss:Data, '.jpg')">
        <img>
          <xsl:attribute name="src">images/<xsl:value-of select="ss:Data"/></xsl:attribute>
          <xsl:attribute name="alt">images/<xsl:value-of select="ss:Data"/></xsl:attribute>
        </img>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of disable-output-escaping="no" select="ss:Data" />
      </xsl:otherwise>
      </xsl:choose>
    </td>
  </xsl:template>

  <xsl:template match="ss:Row" mode="figure">
    <xsl:element name="img">
      <xsl:attribute name="src"><xsl:text>images/</xsl:text>
         <xsl:value-of select="ss:Cell[position()='2']"/>
      </xsl:attribute>
      <xsl:attribute name="alt">
         <xsl:value-of select="ss:Cell[position()='4' or @ss:Index='4']/ss:Data"/>
      </xsl:attribute>
    </xsl:element>
  </xsl:template>

  <xsl:template match="ss:Row" mode="text">
    <p>
      <xsl:value-of select="ss:Cell[position()='2']"/>
    </p>
  </xsl:template>

  <xsl:template match="node()"/>   <!-- remove anything else -->

</xsl:stylesheet>
