<?xml version="1.0"?>

<!--
Stylesheet for generating book.xml from a suitably hierarchical site.xml file.
The project info is currently hardcoded, but since it isn't used anyway that
isn't a major problem.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="http://apache.org/forrest/linkmap/1.0" exclude-result-prefixes="f">

  <xsl:param name="path"/>
  <xsl:output doctype-system="book-cocoon-v10.dtd" doctype-public="-//APACHE//DTD Cocoon Documentation Book V1.0//EN"/>

  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="f:site">
    <book software="Forrest"
      title="Apache Forrest"
      copyright="2002 The Apache Software Foundation">
      <xsl:apply-templates/>
    </book>
  </xsl:template>

  <xsl:template match="*/*">
    <xsl:choose>
      <!-- No label, abandon the whole subtree -->
      <xsl:when test="not(@label)">
      </xsl:when>
      <!-- Below here, everything has a label, and is therefore considered "for display" -->

      <!-- No children -> must be a menu item -->
      <!-- Has children, but they are not for display -> menu item -->
      <xsl:when test="count(*) = 0 or count(*) > 0 and (not(*/@label))">
        <menu-item label="{@label}" href="{@href}">
          <xsl:if test="@description">
            <xsl:attribute name="description">
              <xsl:value-of select="@description"/>
            </xsl:attribute>
          </xsl:if>
        </menu-item>
      </xsl:when>

      <!-- Anything else is considered a menu -->
      <xsl:otherwise>
        <menu label="{@label}">
          <xsl:if test="@href">
            <xsl:attribute name="href">
              <xsl:value-of select="@href"/>
            </xsl:attribute>
          </xsl:if>
          <xsl:if test="@description">
            <xsl:attribute name="description">
              <xsl:value-of select="@description"/>
            </xsl:attribute>
          </xsl:if>

          <xsl:apply-templates/>
        </menu>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
