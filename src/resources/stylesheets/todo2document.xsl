<?xml version="1.0"?>

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">

 <xsl:template match="/">
  <xsl:apply-templates select="//todo"/>
 </xsl:template>
 
 <xsl:template match="todo">
  <document>
   <header>
    <title>Todo List</title>
   </header>
   <body>
    <xsl:apply-templates/>
   </body>
  </document>
 </xsl:template>

 <xsl:template match="actions">
  <section title="{@priority}">
   <ul>
    <xsl:for-each select="action">
     <li>
      <strong><xsl:text>[</xsl:text><xsl:value-of select="@context"/><xsl:text>]</xsl:text></strong><xsl:text> </xsl:text>
      <xsl:apply-templates mode="copy"/>
     </li>
    </xsl:for-each>
   </ul>
  </section>
 </xsl:template>
 
</xsl:stylesheet>
