<?xml version="1.0"?>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method = "xml"  
                version="1.0"
                omit-xml-declaration="no" 
                indent="yes"
                encoding="ISO-8859-1"
                doctype-system="book-cocoon-v10.dtd"
                doctype-public="-//APACHE//DTD Cocoon Documentation Book V1.0//EN" />

    <!-- book to project -->
    <xsl:template match="project">
        <book software="{@name}"
            copyright="{@name}"
            title="{title}">

        <xsl:for-each select = "//menu">
          <menu label="{@name}">
            <xsl:for-each select = "item">
             <menu-item  label="{@name}" href="{@href}"/>
            </xsl:for-each>
          </menu> 
        </xsl:for-each>

        </book>
    </xsl:template>

    <xsl:template match="menu"/>

    <xsl:template match="item"/>


</xsl:stylesheet>
