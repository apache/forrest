<?xml version="1.0" encoding="iso-8859-1"?>
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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
				xmlns:req="http://apache.org/cocoon/request/2.0"
				version="1.0">

  <xsl:param name="documentID"><xsl:value-of select="req:request/req:requestParameters/req:parameter[@name='documentID']/req:value"/></xsl:param>
  <xsl:param name="repositoryURL"><xsl:value-of select="req:request/req:requestParameters/req:parameter[@name='repositoryURL']/req:value"/></xsl:param>
	<xsl:param name="repositoryPort"><xsl:value-of select="req:request/req:requestParameters/req:parameter[@name='repositoryPort']/req:value"/></xsl:param>
	<xsl:param name="repositoryType"><xsl:value-of select="req:request/req:requestParameters/req:parameter[@name='repositoryType']/req:value"/></xsl:param>
  <xsl:param name="repositoryCollection"><xsl:value-of select="req:request/req:requestParameters/req:parameter[@name='repositoryCollection']/req:value"/></xsl:param>
	
  <xsl:template match="req:request">      
			<xsl:choose>
				<xsl:when test="$repositoryPort">
					<xsl:variable name="document">cocoon:/do/getRepositoryData/<xsl:value-of select="$repositoryType"/>/<xsl:value-of select="$repositoryURL"/>/port/<xsl:value-of select="$repositoryPort"/>/collection/<xsl:value-of select="$repositoryCollection"/>/doc/<xsl:value-of select="$documentID"/>.xml</xsl:variable>
		      <xsl:apply-templates select="document($document)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:variable name="document">cocoon:/do/getRepositoryData/<xsl:value-of select="$repositoryType"/>/<xsl:value-of select="$repositoryURL"/>/collection/<xsl:value-of select="$repositoryCollection"/>/doc/<xsl:value-of select="$documentID"/>.xml</xsl:variable>
		      <xsl:apply-templates select="document($document)"/>
				</xsl:otherwise>
		</xsl:choose>

  </xsl:template>  
    
  <xsl:template match="link">
    <a>
      <xsl:attribute name="href">
        <xsl:choose>
          <xsl:when test="starts-with(@href, 'http://') or starts-with(@href, 'mailto:')">
            <xsl:value-of select="@href"/>
          </xsl:when>
          <xsl:otherwise><xsl:value-of select="@href"/>?repositoryURL=<xsl:value-of select="$repositoryURL"/>&amp;repositoryCollection=<xsl:value-of select="$repositoryCollection"/>&amp;repositoryPort=<xsl:value-of select="$repositoryPort"/>&amp;repositoryType=<xsl:value-of select="$repositoryType"/>&amp;documentID=<xsl:value-of select="@href"/></xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:apply-templates select="node()[not(@href)]"/>
    </a>
  </xsl:template>

  <xsl:template match="@*|*|text()|processing-instruction()|comment()">
    <xsl:copy>
      <xsl:apply-templates select="@*|*|text()|processing-instruction()|comment()"/>
    </xsl:copy>
  </xsl:template>
      
</xsl:stylesheet>