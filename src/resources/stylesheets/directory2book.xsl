<?xml version="1.0"?>

<!-- Converts the output of the DirectoryGenerator to Forrest's book.xml
format.  Typically this would be used to define a book.xml pipeline for a
specific page.  Eg, in navigation.xml, define the DirectoryGenerator:

<map:generators default="file">
  <map:generator name="directory" src="org.apache.cocoon.generation.DirectoryGenerator" />
</map:generators>

And then define the book.xml matcher for a directory that you want to
automatically generate a menu for (here wiki/):

<map:match pattern="wiki/book-*">
  <map:generate type="directory" src="content/xdocs/wiki">
    <map:parameter name="dateFormat" value="yyyy-MM-dd hh:mm" />
    <map:parameter name="depth" value="10" />
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
    <menu label="{@name}">
      <xsl:apply-templates select="dir:file|dir:directory" />
    </menu>
  </xsl:template>

  <xsl:template match="dir:file[not(@name=concat('book', $ext))]">
    <menu-item label="{substring-before(@name, $ext)}"
    href="{concat(substring-before(@name, $ext), '.html')}"/>
  </xsl:template>

</xsl:stylesheet>
