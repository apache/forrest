<!--
Named templates related to link handling.

Currently requires Xalan.  Yes, XSLT 2.0's xsl:redirect-document and Saxon's
saxon:output are no good because they don't allow doc appending.  This current
stylesheet-based method for recording protocol'ed links is therefore a
temporary hack.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:redirect="http://xml.apache.org/xalan/redirect"
  xmlns:saxon="http://icl.com/saxon"
  extension-element-prefixes="redirect saxon">

  <xsl:template name="record-link">
    <xsl:param name="href" />
    <xsl:param name="linkdir" select="'.'"/>
    <xsl:variable name="protocol" select="substring-before($href, ':')"/>
    <xsl:if test="not($protocol = '' or $protocol='http' or $protocol='https')">
      <xsl:message>###  Protocol is <xsl:value-of select="$protocol"/></xsl:message>
      <xsl:variable name="filename" select="concat(concat(concat($linkdir, '/unprocessed-'), $protocol), 's.txt')"/>

<!--
No good because there is no 'append' option :(

      <saxon:output href="{$filename}" method="text">
        <xsl:value-of select="substring-after($href, concat($protocol, ':'))"/>
        <xsl:text>
        </xsl:text>
        <xsl:fallback>
          <xsl:message>saxon:output not available!</xsl:message>
        </xsl:fallback>
      </saxon:output>
      -->

      <redirect:write file="{$filename}" append="true">
        <xsl:value-of select="substring-after($href, concat($protocol, ':'))"/>
        <xsl:text>
        </xsl:text>
        <!--
        <xsl:fallback>
          <xsl:message>Xalan's redirect:write not available!</xsl:message>
        </xsl:fallback>
        -->
      </redirect:write>
    </xsl:if>
  </xsl:template>

<!--
Uncomment this to test.
Usage: saxon linkutils.xsl linkutils.xsl link=file:hello.pdf

<xsl:param name="link" select="'file:hello.pdf'"/>
<xsl:param name="linkdir" select="'.'"/>
<xsl:template match="/">
  <xsl:message>
    link= <xsl:value-of select="$link"/>
    <xsl:call-template name="record-link">
      <xsl:with-param name="href" select="$link"/>
      <xsl:with-param name="linkdir" select="$linkdir"/>
    </xsl:call-template>
  </xsl:message>
</xsl:template>
-->


</xsl:stylesheet>
