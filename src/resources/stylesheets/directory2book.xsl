<?xml version="1.0"?>

<!-- Converts the output of the DirectoryGenerator to Forrest's book.xml
format.  Typically this would be used to define a book.xml pipeline for a
specific page.  Eg, in menu.xmap, define the DirectoryGenerator:

<map:generators default="file">
  <map:generator name="directory" src="org.apache.cocoon.generation.DirectoryGenerator" />
</map:generators>

And then define the book.xml matcher for a directory that you want to
automatically generate a menu for (here wiki/):

<map:match pattern="wiki/**book-*">
  <map:generate type="directory" src="content/xdocs/wiki/{1}">
    <map:parameter name="dateFormat" value="yyyy-MM-dd hh:mm" />
    <map:parameter name="depth" value="5" />
    <map:parameter name="exclude" value="[.][^x[^m][^l]|~$|^my-images$" />
  </map:generate>
  <map:transform src="resources/stylesheets/directory2book.xsl" />
  <map:serialize type="xml"/>
</map:match>


-->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:dir="http://apache.org/cocoon/directory/2.0" exclude-result-prefixes="dir">

  <xsl:output doctype-system="book-cocoon-v10.dtd" doctype-public="-//APACHE//DTD Cocoon Documentation Book V1.0//EN"/>

  <xsl:output indent="yes"/>

  <xsl:param name="expected-extension" select="'xml'"/>

  <xsl:variable name="ext" select="concat('.', $expected-extension)"/>

  <xsl:template match="/">
    <book software="" title="" copyright="">
      <!--
      <menu label="Aggregates">
        <menu-item label="Combined content" href="combined.html"/>
      </menu>
      -->
      <xsl:apply-templates/>
    </book>
  </xsl:template>

  <xsl:template match="dir:directory">
    <menu label="{translate(@name,'-_',' ')}">
      <xsl:apply-templates select="dir:file" />
    </menu>
      <!-- [descendant::dir:file] is to remove empty menu nodes -->
      <xsl:apply-templates select="dir:directory [descendant::dir:file]" />
  </xsl:template>

  <xsl:template match="dir:file">
    <menu-item label="{translate(substring-before(@name, $ext),'-_',' ')}">
      <xsl:attribute name="href">
      <xsl:variable name="path" />
        <!-- [not (position()=last())] is to ignore the root node -->
        <xsl:for-each select="ancestor::dir:directory [not (position()=last())]">
          <xsl:variable name="path" select="concat($path, @name, '/')" />
          <xsl:value-of select="$path"/>
        </xsl:for-each>
        <xsl:value-of select="concat(substring-before(@name, $ext), '.html')"/>
      </xsl:attribute>
    </menu-item>
  </xsl:template>

</xsl:stylesheet>
