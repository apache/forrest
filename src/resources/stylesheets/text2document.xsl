<?xml version="1.0"?>

<!--+
    | Transforms TextGenerator output to document format.
    |
    | CVS $Id: text2document.xsl,v 1.3 2003/07/30 08:37:54 cheche Exp $
    +-->

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

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
          <!-- &#13; is System.getProperty("line.separator") -->
	  <xsl:value-of select="translate(node(),'&#13;',' ')"/>
        </source>
      </body>
    </document>
  </xsl:template>
 
</xsl:stylesheet>
