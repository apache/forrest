<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
 
     <xsl:output method = "xml"
                 version="1.0" 
                 encoding="ISO-8859-1" 
                 indent="yes"  
                 doctype-public="-//Netscape Communications//DTD RSS 0.91//EN"                   
                 doctype-system="http://my.netscape.com/publish/formats/rss-0.91.dtd"                   
                 />
                 
   <xsl:template match="status">

  
      <rss version="0.91">
         <channel>
            <title>Project Changes</title>

            <link>http://www.krysalis.org/centipede/changes.html</link>

            <description>Project Changes</description>

            <language>en-us</language>

            <xsl:for-each select="changes/release[1]/action">
               <item>
                  <title>
                     <xsl:value-of select="@type" />

                     <xsl:if test="@context">about 
                     <xsl:value-of select="@context" />
                     </xsl:if>

                     <xsl:if test="@type='fix'">(fixes bug 
                     <xsl:value-of select="@fixes-bug" />

                     )</xsl:if>
                  </title>

                  <link>http://www.krysalis.org/centipede/changes.html</link>

                  <description>
                  <xsl:value-of select="@type" />

                  by 
                  <xsl:value-of select="@dev" />

                  <xsl:if test="@context">about 
                  <xsl:value-of select="@context" />
                  </xsl:if>

                  . 
                  <xsl:if test="@type='fix'">It fixes bug 
                  <xsl:value-of select="@fixes-bug" />

                  .</xsl:if>

                  . 
                  <xsl:if test="@due-to">Thanks to 
                  <xsl:value-of select="@due-to" />

                  .</xsl:if> 
                  
                  Message: <xsl:value-of select="." />
                  
                  </description>
               </item>
            </xsl:for-each>
         </channel>
      </rss>
   </xsl:template>
</xsl:stylesheet>

