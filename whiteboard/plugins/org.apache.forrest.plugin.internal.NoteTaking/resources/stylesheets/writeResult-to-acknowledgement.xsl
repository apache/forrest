<?xml version='1.0'?>
<xsl:stylesheet version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:dyn="http://exslt.org/dynamic">
  
  <!-- the page for which the note was added -->
  <xsl:param name="path"/>
  
  <xsl:template match="sourceResult">
    <xsl:choose>
      <xsl:when test="execution='success'">
        <xsl:call-template name="success"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="failure"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template name="success">
    <document>
      <header>
        <title>Note Written</title>
      </header>
      <body>
        <p>Your note has been succesfully written. You can now continue to 
        work.</p>
        
        <p>
          <link>
            <xsl:attribute name="href">/<xsl:value-of select="$path"/>.html</xsl:attribute>
            Return to originating page.
          </link>
        </p>
      </body>
    </document>
  </xsl:template>
  
  <xsl:template name="failure">
    <document>
      <header>
        <title>Failed Note Written</title>
      </header>
      <body>
        <warning>There was an error recording your note.</warning>
        
        <p>
          <link>
            <xsl:attribute name="href">/<xsl:value-of select="$path"/>.html</xsl:attribute>
            Return to originating page.
          </link>
        </p>
      </body>
    </document>
  </xsl:template>
</xsl:stylesheet>
