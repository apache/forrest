<?xml version="1.0"?>

<!--+
    | Transforms TextGenerator output to document format.
    |
    | CVS $Id: text2document.xsl,v 1.1 2003/07/18 00:17:23 cheche Exp $
    +-->

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output indent="yes" 
            method="xml"
	    doctype-public="-//APACHE//DTD Documentation V1.2//EN"
	    doctype-system="document-v12.dtd"
            cdata-section-elements="source"/>

  <xsl:param name="filename"/>

  <xsl:template match="/">
    <document>
      <header>
        <title>
          <xsl:value-of select="$filename"/>
        </title>
      </header>
      <body>
        <source>
          <xsl:value-of select="node()"/>
        </source>
      </body>
    </document>
  </xsl:template>
 
</xsl:stylesheet>
