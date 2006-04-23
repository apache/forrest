<?xml version="1.0"?>

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">

  <xsl:import href="copyover.xsl"/>

  <xsl:template match="references">
   <document>
    <header>
     <title>References</title>
    </header>
    <body>
      <xsl:apply-templates select="part"/>
    </body>
   </document>  
  </xsl:template>

  <xsl:template match="part">
    <section>
      <xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
      <title><xsl:value-of select="title"/></title>
      <table>
        <tr>
          <th>Citation</th>
          <th>Details</th>
        </tr>
        <xsl:apply-templates select="item"/>
      </table>
    </section>
  </xsl:template>

  <xsl:template match="item">
    <xsl:variable name="id">
      <xsl:call-template name="generate-id"/>
    </xsl:variable>
    <tr id="{$id}">
      <td>[<xsl:value-of select="$id"/>]</td>
      <td><xsl:apply-templates/></td>
    </tr>
  </xsl:template>
  
  <xsl:template name="generate-id">
    <xsl:choose>
      <xsl:when test="@id">
        <xsl:value-of select="@id"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="concat(concat(local-name(.), '-'), generate-id(.))"/>
      </xsl:otherwise>
  </xsl:choose>
  </xsl:template>

  
</xsl:stylesheet>
