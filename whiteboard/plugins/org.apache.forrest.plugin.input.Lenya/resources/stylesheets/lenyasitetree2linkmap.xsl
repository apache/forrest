<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:s="http://apache.org/cocoon/lenya/sitetree/1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="http://apache.org/forrest/linkmap/1.0"
    version="1.0">
    <xsl:output method="xml"/>
    
    <xsl:template match="/">
        <xsl:apply-templates select="s:site"/>
    </xsl:template>
    
    <xsl:template match="s:site">
        <site label="MyProj">
            <about label="About">
                <index label="Index" href="index.html" description="Welcome to MyProj"/>
            </about>
            
            <lenya label="Lenya Site" href="lenya/">
	            <xsl:apply-templates select="*"/>
            </lenya>
        </site>
    </xsl:template>
    
    <xsl:template match="s:node">
        <xsl:element name="{@id}">
            <xsl:attribute name="label">
                <xsl:value-of select="s:label[1]"/>
            </xsl:attribute>
            <xsl:attribute name="href">
                <xsl:value-of select="@id"/>
                <xsl:choose>
                    <xsl:when test="s:node">
                        <xsl:text>/</xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text>.html</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <xsl:apply-templates select="*"/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="s:label"/>
</xsl:stylesheet>