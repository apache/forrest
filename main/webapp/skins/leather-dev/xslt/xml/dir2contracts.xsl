<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:dir="http://apache.org/cocoon/directory/2.0"
    xmlns:session="http://apache.org/cocoon/session/1.0"
    >

<xsl:param name="contentDir" select="'default/path'"/>

  <!--
      Create row for each document.  Information about the document is
      extracted from the document itself using the document()
      function.
  -->
  <xsl:template match="/">
      <contracts xmlns:xhtml="http://www.w3.org/1999/xhtml">
        <xsl:apply-templates select="//dir:file"/>                    
      </contracts>
  </xsl:template>

<xsl:template match="dir:file[starts-with(@name,'c-')]">
    <xsl:variable name="fct-bit-file">
      <xsl:value-of select="$contentDir"/>
      <xsl:text>/</xsl:text>
      <xsl:value-of select="@name"/>
    </xsl:variable>
    <xsl:variable name="fct-bit-title">
      <xsl:value-of select="document($fct-bit-file)/contract/@name"/>
    </xsl:variable>
    <xsl:variable name="fct-bit-nc">
      <xsl:value-of select="document($fct-bit-file)/contract/@nc"/>
    </xsl:variable>
    <xsl:variable name="fct-bit-tlc">
      <xsl:value-of select="document($fct-bit-file)/contract/@tlc"/>
    </xsl:variable>
    <xsl:variable name="fct-bit-description">
      <xsl:value-of select="document($fct-bit-file)/contract/description"/>
    </xsl:variable>
    <xsl:if test="$fct-bit-tlc='content'">
      <content>
        <contract name="{$fct-bit-title}">
          <css-contract>
            <xsl:value-of select="$fct-bit-nc"/>
          </css-contract>
          <description>
            <xsl:value-of select="$fct-bit-description"/>
          </description>
          <realpath>
            <xsl:value-of select="$fct-bit-file"/>
          </realpath>
        </contract>
      </content>
    </xsl:if>
</xsl:template>

</xsl:stylesheet>

