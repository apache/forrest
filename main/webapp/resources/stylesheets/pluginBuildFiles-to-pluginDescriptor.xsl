<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:dir="http://apache.org/cocoon/directory/2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    
    <xsl:param name="type" select="FIXME"/>
    
    <xsl:template match="/">
      <plugins>
        <xsl:attribute name="type"><xsl:value-of select="$type"/></xsl:attribute>
        <xsl:apply-templates select="/dir:directory/dir:directory/dir:file[@name='build.xml']/dir:xpath"/>
      </plugins>
    </xsl:template>
    
    <xsl:template match="project">
      <xsl:if test="property[@name='publish']/@value='true'">
        <plugin>
            <xsl:attribute name="name"><xsl:value-of select="property[@name='plugin-name']/@value"/></xsl:attribute>
            <xsl:attribute name="type"><xsl:value-of select="property[@name='type']/@value"/></xsl:attribute>
            <xsl:attribute name="author"><xsl:value-of select="property[@name='author']/@value"/></xsl:attribute>
            <xsl:attribute name="website"><xsl:value-of select="property[@name='websiteURL']/@value"/></xsl:attribute>
            <xsl:attribute name="url"><xsl:value-of select="property[@name='downloadURL']/@value"/></xsl:attribute>
            <xsl:attribute name="version"><xsl:value-of select="property[@name='plugin-version']/@value"/></xsl:attribute>
            <forrestVersion><xsl:value-of select="property[@name='forrest.version']/@value"/></forrestVersion>
                   
            <description>
              <xsl:value-of select="property[@name='description']/@value"/>
            </description>
        </plugin>       
      </xsl:if>
    </xsl:template>
</xsl:stylesheet>
