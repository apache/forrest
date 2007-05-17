<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform' version='1.0'>

    <xsl:template match='document'>
        <TEI.2>
            <xsl:apply-templates/>
        </TEI.2>
    </xsl:template>
    
    <xsl:template match="header">
        <teiHeader>
           <fileDesc>
             <titleStmt>
               <title><xsl:value-of select="title"/></title> 
             </titleStmt>
             
                <publicationStmt>
                    <publisher>OSS Watch, Oxford University</publisher>
                    <authority>OSS Watch</authority>
                    <address>
                     <email>info@oss-watch.ac.uk</email>
                    </address>
                    <availability>
                     <licence>http://creativecommons.org/licenses/by-sa/2.0/uk/</licence>
                    </availability>
                    <date>2006-04-28</date>
                   </publicationStmt>
             
           </fileDesc>
           <revisionDesc vcdate="$LastChangedDate: 2006-10-02 17:10:52 +0100 (Mon, 02 Oct 2006) $"
            vcwho="$LastChangedBy: rgardler $" vcrevision="$LastChangedRevision: 10309 $">
            <change>
                <date>2006-02-20</date>
                <respStmt>
                    <resp>author</resp>
                    <name>Ross Gardler</name>
                </respStmt>
                <reason>creation</reason>
            </change>
          </revisionDesc>
        </teiHeader>
    </xsl:template>
    
    <xsl:template match="body">
      <text>
        <body>
          <xsl:apply-templates/>
        </body>
      </text>    
    </xsl:template>
    
    <xsl:template match="section">
      <div>
        <xsl:apply-templates />
      </div>
    </xsl:template>
    
    <!-- lists -->
    <xsl:template match="ul">
        <list type='unordered'>
          <xsl:apply-templates/>
        </list>
    </xsl:template>
    
    <xsl:template match="ol">
        <list type='ordered'>
          <xsl:apply-templates/>
        </list>
    </xsl:template>
    <xsl:template match="li">
      <item><xsl:apply-templates/></item>
    </xsl:template>
        
    <xsl:template match="a|link">
      <xref>
        <xsl:attribute name="url"><xsl:value-of select="translate(@href,'.html','.xml')"/></xsl:attribute>
        <xsl:value-of select="."/>
      </xref>
    </xsl:template>
    
    <xsl:template match="title">
      <head><xsl:value-of select="."/></head>
    </xsl:template>
    
    <xsl:template match="node()|@*" priority="-1">
      <xsl:copy>
        <xsl:apply-templates select="node()|@*"/>
      </xsl:copy>
    </xsl:template>
</xsl:stylesheet>