<?xml version='1.0'?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:key name="attlistbyname" match="attlist" use="@ename"/>
<xsl:key name="contentmodelbychildren" match="contentModel" use="descendant::element/@name"/>

<xsl:template match="dtd">
  <document>
    <header>
      <title>DTD documentation</title>
      <subtitle>
        <xsl:call-template name="trailingfilename">
          <xsl:with-param name="string" select="@sysid"/>
        </xsl:call-template>
      </subtitle>
    </header>
    <body>
      <section><title>Top-level element(s)</title>
        <ul>
          <xsl:for-each select="/dtd/contentModel[not(key('contentmodelbychildren', @ename))]">
            <li>
              <link href="#{@ename}"><xsl:value-of select="@ename"/></link>
            </li>
          </xsl:for-each>
        </ul>
      </section>
      <section><title>List of elements</title>
        <ul>
          <xsl:for-each select="contentModel">
            <xsl:sort select="@ename"/>
            <li>
              <link href="#{@ename}"><xsl:value-of select="@ename"/></link>
            </li>
          </xsl:for-each>
        </ul>
      </section>
      <section>
        <title>Element declarations</title>
        <xsl:apply-templates select="contentModel"/>
      </section>
    </body>
  </document>
</xsl:template>

<xsl:template match="contentModel">
  <section id="{@ename}">
    <title><xsl:value-of select="@ename"/></title>
    <section><title>Content Model</title>
      <p>
        <xsl:apply-templates/>
      </p>
    </section>
    <xsl:if test="key('attlistbyname',@ename)">
      <section><title>Attributes</title>
        <xsl:apply-templates select="key('attlistbyname',@ename)"/>
      </section>
    </xsl:if>
    <xsl:if test="key('contentmodelbychildren',@ename)">
      <section><title>Used inside</title>
        <p>
          <xsl:for-each select="key('contentmodelbychildren',@ename)">
            <link href="#{@ename}"><xsl:value-of select="@ename"/></link>
            <xsl:if test="not(position() = last())">, </xsl:if>
          </xsl:for-each>
        </p>
      </section>
    </xsl:if>
  </section>
</xsl:template>

<xsl:template match="empty">
  EMPTY
</xsl:template>

<xsl:template match="pcdata">
  #PCDATA
</xsl:template>

<xsl:template match="element">
  <link href="#{@name}"><xsl:value-of select="@name"/></link>
</xsl:template>

<xsl:template match="group">
  <xsl:text>( </xsl:text><xsl:apply-templates/><xsl:text> )</xsl:text>
</xsl:template>

<xsl:template match="separator">
  <xsl:text> </xsl:text><xsl:value-of select="@type"/><xsl:text> </xsl:text>
</xsl:template>

<xsl:template match="occurrence">
  <xsl:text> </xsl:text><xsl:value-of select="@type"/>
</xsl:template>

<xsl:template match="attlist">
  <dl>
    <xsl:apply-templates/>
  </dl>
</xsl:template>

<xsl:template match="attributeDecl">
  <dt>
    <strong><xsl:value-of select="@aname"/></strong>
  </dt>
  <dd>
    <xsl:if test="not(enumeration)">
      <em>type: </em><xsl:value-of select="@atype"/><br/>
    </xsl:if>
    <xsl:if test="@required">
      required attribute<br/>
    </xsl:if>
    <xsl:if test="@default">
      <em>default value: </em><xsl:value-of select="@default"/><br/>
    </xsl:if>
    <xsl:if test="@fixed">
      <em>fixed value: </em><xsl:value-of select="@default"/><br/>
    </xsl:if>
    <xsl:if test="enumeration">
      <em>possible values: </em><xsl:for-each select="enumeration">
        <xsl:value-of select="@value"/><xsl:text> </xsl:text>
      </xsl:for-each>
      <br/>
    </xsl:if>
  </dd>
</xsl:template>

<xsl:template name="trailingfilename">
  <xsl:param name="string"/>
  <xsl:choose>
    <xsl:when test="contains($string,'/')">
      <xsl:call-template name="trailingfilename">
        <xsl:with-param name="string" select="substring-after($string,'/')"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$string"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

</xsl:stylesheet>
