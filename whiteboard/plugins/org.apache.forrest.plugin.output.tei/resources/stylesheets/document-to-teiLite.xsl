<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform'
  version='1.0'>

  <xsl:template match='document'>
    <xsl:text disable-output-escaping="yes">
  <![CDATA[
  <!DOCTYPE TEI.2 PUBLIC "-//TEI//DTD TEI Lite 1.0//EN" "">
  ]]>
</xsl:text>
    
    <TEI.2>
      <xsl:apply-templates />
    </TEI.2>
  </xsl:template>

  <xsl:template match="header">
    <teiHeader>
      <fileDesc>
        <titleStmt>
          <title>
            <xsl:value-of select="title" />
          </title>
        </titleStmt>

        <publicationStmt>
          <publisher>OSS Watch, Oxford University</publisher>
          <authority>OSS Watch</authority>
          <address>
            <email>info@oss-watch.ac.uk</email>
          </address>
          <availability>
            <licence>
              http://creativecommons.org/licenses/by-sa/2.0/uk/
            </licence>
          </availability>
          <date>FIXME: Date</date>
        </publicationStmt>

      </fileDesc>
      <revisionDesc
        vcdate="$LastChangedDate: 2006-10-02 17:10:52 +0100 (Mon, 02 Oct 2006) $"
        vcwho="$LastChangedBy: unklown $"
        vcrevision="$LastChangedRevision: 1 $">
        <change>
          <date>FIXME: Date</date>
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

</xsl:stylesheet>