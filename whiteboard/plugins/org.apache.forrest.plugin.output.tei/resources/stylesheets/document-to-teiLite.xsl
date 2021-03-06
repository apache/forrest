<?xml version='1.0'?>
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
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:datetime="http://exslt.org/dates-and-times"
    xmlns:prop="http://apache.org/forrest/properties/1.0"
    version="1.0">

  <xsl:variable
        name="properties"
        select="//prop:properties" />

  <xsl:variable
        name="reference-section"
        select="$properties/*[@name='output.tei.reference-section']/@value" />

  <xsl:key name="references" match="*" use="concat(name(), '::', .)" />
  
  <xsl:template match='/'>
    <xsl:text disable-output-escaping="yes">
  <![CDATA[
  <!DOCTYPE TEI.2 PUBLIC "-//TEI//DTD TEI Lite 1.0//EN" "">
  ]]>
</xsl:text>
    
    <TEI.2>
      <xsl:apply-templates select="/site/document" />
    </TEI.2>
  </xsl:template>

  <xsl:template match="document">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="header">
    <teiHeader>
      <fileDesc>
        <titleStmt>
          <title>
            <xsl:value-of select="title" />
          </title>
        </titleStmt>
	<editionStmt>
          <edition status="live"/>
	</editionStmt>
        <publicationStmt>
          <publisher>
            <xsl:value-of select="$properties/*[@name='output.tei.publisher']/@value"/>
          </publisher>
          <authority>
            <xsl:value-of select="$properties/*[@name='output.tei.authority']/@value"/>
          </authority>
          <address>
            <email>
              <xsl:value-of select="$properties/*[@name='output.tei.email']/@value"/>
            </email>
          </address>
          <availability>
            <licence>
              <xsl:value-of select="$properties/*[@name='output.tei.licence']/@value"/>
            </licence>
          </availability>
          <date><xsl:value-of select="datetime:date()"/></date>
        </publicationStmt>

      </fileDesc>
      <revisionDesc
        vcdate="$LastChangedDate: 2006-10-02 17:10:52 +0100 (Mon, 02 Oct 2006) $"
        vcwho="$LastChangedBy: unknown $"
        vcrevision="$LastChangedRevision: 1 $">
        <change>
          <date><xsl:value-of select="datetime:date()"/></date>
          <respStmt>
            <resp>author</resp>
            <name>FIXME: Author</name>
          </respStmt>
          <reason>creation</reason>
        </change>
      </revisionDesc>
    </teiHeader>
  </xsl:template>

  <xsl:template match="body">
    <text>
      <body>
        <xsl:apply-templates />
        <xsl:choose>
          <xsl:when test="$reference-section = 'true'">
            <xsl:call-template name="references"/>
          </xsl:when>
        </xsl:choose>
      </body>
    </text>
  </xsl:template>

  <xsl:template match="section">
    <div>
      <xsl:apply-templates />
    </div>
  </xsl:template>

  <!-- lists -->
  <xsl:template match="ul">
    <list type='unordered'>
      <xsl:apply-templates />
    </list>
  </xsl:template>

  <xsl:template match="ol">
    <list type='ordered'>
      <xsl:apply-templates />
    </list>
  </xsl:template>
  <xsl:template match="li">
    <item>
      <xsl:apply-templates />
    </item>
  </xsl:template>

  <xsl:template match="em">
    <emph><xsl:apply-templates/></emph>
  </xsl:template>

  <xsl:template match="a|link">
    <xref>
      <xsl:attribute name="url"><xsl:call-template name="replace-substring">
            <xsl:with-param name="value"><xsl:value-of select="@href"/></xsl:with-param>
            <xsl:with-param name="from">.html</xsl:with-param>
            <xsl:with-param name="to">.xml</xsl:with-param>
          </xsl:call-template>
      </xsl:attribute>
      <xsl:value-of select="." />
    </xref>
  </xsl:template>

  <xsl:template match="title">
    <head>
      <xsl:value-of select="." />
    </head>
  </xsl:template>

  <xsl:template match="node()|@*" priority="-1">
    <xsl:copy>
      <xsl:apply-templates select="node()|@*" />
    </xsl:copy>
  </xsl:template>

  <xsl:template name="replace-substring">
    <xsl:param name="value" />
    <xsl:param name="from" />
    <xsl:param name="to" />
    <xsl:choose>
      <xsl:when test="contains($value,$from)">
        <xsl:value-of select="substring-before($value,$from)" />
        <xsl:value-of select="$to" />
        <xsl:call-template name="replace-substring">
          <xsl:with-param name="value"
            select="substring-after($value,$from)" />
          <xsl:with-param name="from" select="$from" />
          <xsl:with-param name="to" select="$to" />
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$value" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="references">
    <div>
      <head>Further Reading</head>
      <p>
	<hi>Links:</hi>
	<list type="unordered">
          <xsl:for-each select="//link[generate-id(.) =
            generate-id(key('references', concat(name(), '::', .))[1])
            and not (contains(@href, 'mailto:'))
            and not (starts-with(@href, 'http://www.oss-watch.ac.uk'))]">
            <xsl:sort select ="."/>
            <item>
              FIXME: Document Title
              [<xptr><xsl:attribute name="url">
                  <xsl:value-of select="@href"/>
                </xsl:attribute></xptr>]
            </item>
          </xsl:for-each>
        </list>
      </p>
      <p>
        <hi>Related information from OSS Watch</hi>
        <list rend="copytosidebar" type="unordered">
          <xsl:for-each select="//link[generate-id(.) =
            generate-id(key('references', concat(name(), '::', .))[1])
            and not (contains(@href, 'mailto:'))
            and (starts-with(@href, 'http://www.oss-watch.ac.uk'))]">
            <xsl:sort select ="."/>
            <item>
              <xref>
                <xsl:attribute name="url">
                  <xsl:value-of select="substring(@href,27)"/>
                </xsl:attribute>
		FIXME: Document Title
              </xref>
            </item>
          </xsl:for-each>
        </list>
      </p>
    </div>
  </xsl:template>
</xsl:stylesheet>
