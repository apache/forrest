<?xml version="1.0"?>

<!--
Stylesheet for generating site.xml from a IMS Manifest file.
-->

<xsl:stylesheet version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:ims="http://www.imsproject.org/xsd/imscp_rootv1p1p2"
  xmlns:adlcp="http://www.adlnet.org/xsd/adlcp_rootv1p2">
  
  <xsl:import href="pathutils.xsl"/>
  <xsl:import href="repositoryUtils.xsl"/>

  <xsl:output doctype-system="http://apache.org/forrest/dtd/tab-cocoon-v11.dtd"
    doctype-public="-//APACHE//DTD Cocoon Documentation Tab V1.1//EN"/>

  <xsl:template match="/">
    <tabs software="MyProj"
      title="MyProj"
      copyright="foo">

      <xsl:apply-templates select="ims:manifest"/>
    </tabs>
  </xsl:template>
  
  <xsl:template match="ims:manifest">
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="ims:organizations">
    <xsl:apply-templates select="ims:organization"/>
  </xsl:template>
  
  <xsl:template match="ims:organization">
    <xsl:variable name="title"><xsl:value-of select="./ims:title"/></xsl:variable>
    <xsl:variable name="default_file_id"><xsl:value-of select=".//ims:item[@identifierref][1]/@identifierref"/></xsl:variable>
    <xsl:variable name="repositoryCommand">
      <xsl:call-template name="getRepositoryCommand">
        <xsl:with-param name="path"><xsl:value-of select="//ims:resources/ims:resource[@identifier=$default_file_id]/@href"/></xsl:with-param>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="scoName">
      <xsl:call-template name="getSCOName">
        <xsl:with-param name="path"><xsl:value-of select="//ims:resources/ims:resource[@identifier=$default_file_id]/@href"/></xsl:with-param>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="full_path">
      <xsl:choose>
        <xsl:when test="$repositoryCommand='getSCO'">getSCO/<xsl:call-template name="getSCOName"><xsl:with-param name="path"><xsl:value-of select="//ims:resources/ims:resource[@identifier=$default_file_id]/@href"/></xsl:with-param></xsl:call-template></xsl:when>
        <xsl:otherwise><xsl:value-of select="//ims:resources/ims:resource[@identifier=$default_file_id]/@href"/></xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="href">
      <xsl:call-template name="dirname-nz">
        <xsl:with-param name="path"><xsl:value-of select="$full_path"/></xsl:with-param>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="filename_noext">
      <xsl:choose>
        <xsl:when test="$repositoryCommand='getSCO'"><xsl:value-of select="$scoName"/>/index</xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="filename-noext">
            <xsl:with-param name="path"><xsl:value-of select="$full_path"/></xsl:with-param>
          </xsl:call-template>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    
    <tab>
      <xsl:attribute name="id"><xsl:value-of select="$title"/></xsl:attribute>
      <xsl:attribute name="label"><xsl:value-of select="$title"/></xsl:attribute>
      <xsl:attribute name="href"><xsl:value-of select="$href"/></xsl:attribute>
      <xsl:attribute name="indexfile">
        <xsl:choose>
          <xsl:when test="not($filename_noext='')"><xsl:value-of select="$filename_noext"/>.html</xsl:when>
          <xsl:otherwise>index.html</xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:apply-templates select="ims:item"/>
    </tab>
  </xsl:template>
  
</xsl:stylesheet>
