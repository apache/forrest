<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <!-- document-v10.dtd to document-v11.dtd transformation -->
  
  <!-- normally, I would include something like this:
  <xsl:output doctype-public="-//APACHE//DTD Documentation V1.1//EN" doctype-system="document-v11.dtd"/>
  We should something similar, i.e. make sure the result of this transformation is validated against the v11 DTD
  -->

  <!-- fixes sections -->
  <xsl:template match="s1 | s2 | s3 | s4">
    <section>
      <xsl:for-each select="@*">
        <xsl:copy-of select="."/>
      </xsl:for-each>
      <xsl:apply-templates/>
    </section>
  </xsl:template>

  <!-- dunnow what to do with connect - maybe just evaporize it? -->
  <xsl:template match="connect">
    <xsl:message terminate="no">The connect element isn't supported anymore in the document-v11.dtd, please fix your document.</xsl:message>
    [[connect: <xsl:value-of select="."/> ]]
  </xsl:template>

  <xsl:template match="link/@type | link/@actuate | link/@show |
                       jump/@type | jump/@actuate | jump/@show |
                       fork/@type | fork/@actuate | fork/@show"/>

  <!-- 'simple lists' become unordered lists -->
  <xsl:template match="sl">
    <ul>
      <xsl:for-each select="@*">
        <xsl:copy-of select="."/>
      </xsl:for-each>
      <xsl:apply-templates/>
    </ul>
  </xsl:template>

  <!-- the obligatory copy-everything -->
  <xsl:template match="node() | @*">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
