<?xml version="1.0"?>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method = "xml"  
                version="1.0"
                omit-xml-declaration="no" 
                indent="no"
                encoding="ISO-8859-1"
                doctype-system="document-v11.dtd"
                doctype-public="-//APACHE//DTD Documentation V1.1//EN" />

    <xsl:template match="/">
     <xsl:choose>
   	   <xsl:when test="name(child::node())='document'">
         <xsl:apply-templates/>
	   </xsl:when>
  
	   <xsl:otherwise>
	     <document>
	      <header><title>Error in conversion</title></header>
	      <body>
	       <warning>This file is not in anakia format, please convert manually.</warning>
	      </body>
	     </document>
	   </xsl:otherwise>
     </xsl:choose>
    </xsl:template>
           
    <xsl:template match="document">
        <document>
            <xsl:apply-templates/>
        </document>
    </xsl:template>
    
    <!-- properties to header -->
    <xsl:template match="properties">
        <header>
            <xsl:apply-templates/>
            <authors>
              <xsl:for-each select = "author">
                <person email="{@email}" name="{.}"/>
              </xsl:for-each>
            </authors>
        </header>
    </xsl:template>

    <xsl:template match="figure">
        <figure alt="{title}" src= "{graphic/@fileref}" />
    </xsl:template>

    <xsl:template match="img">
       <xsl:choose>
    	<xsl:when test="name(..)='section'">
          <figure alt="{@alt}" src= "{@src}"/>
    	</xsl:when>
    	<xsl:otherwise>
          <img alt="{@alt}" src= "{@src}"/>
    	</xsl:otherwise>
       </xsl:choose>
       
       
    </xsl:template>
    
    <xsl:template match="source">
      <xsl:choose>
    	<xsl:when test="name(..)='p'">
    	  <code>
    	    <xsl:apply-templates/>
    	  </code> 
    	</xsl:when>
      
    	<xsl:otherwise>
    	  <source>
    	    <xsl:apply-templates/>
    	  </source> 
    	</xsl:otherwise>
       </xsl:choose>
    </xsl:template>

    
    <!-- person to author -->
    <xsl:template match="author"/>
    
    <xsl:template match="section|s1|s2|s3|s4|s5|s6">
        <section>
          <title><xsl:value-of select="@name" /></title>
            <xsl:apply-templates/>
        </section>
    </xsl:template>

  
    <xsl:template match="subsection">
        <section>
          <title><xsl:value-of select="@name" /></title>
            <xsl:apply-templates/>
        </section>
    </xsl:template>
    
    <xsl:template match="a">
        <link href="{@href}"><xsl:value-of select="." /></link>
    </xsl:template>
    
    <xsl:template match="center">
      <xsl:choose>
    	<xsl:when test="name(..)='p'">
    	    <xsl:apply-templates/>
    	</xsl:when>
      
    	<xsl:otherwise>
    	  <p>
    	    <xsl:apply-templates/>
    	  </p> 
    	</xsl:otherwise>
       </xsl:choose>
    </xsl:template>

    <xsl:template match="ol">
      <xsl:choose>
    	<xsl:when test="name(..)='p'">
    	   <xsl:text disable-output-escaping="yes"><![CDATA[</p>]]></xsl:text>
    	    <ol>
    	     <xsl:apply-templates/>
    	    </ol>
    	   <xsl:text disable-output-escaping="yes"><![CDATA[<p>]]></xsl:text>
    	</xsl:when>
      	<xsl:otherwise>
    	    <ol>
    	     <xsl:apply-templates/>
    	    </ol>
    	</xsl:otherwise>
       </xsl:choose>
    </xsl:template>
    
    <xsl:template match="ul">
      <xsl:choose>
    	<xsl:when test="name(..)='p'">
    	   <xsl:text disable-output-escaping="yes"><![CDATA[</p>]]></xsl:text>
    	    <ul>
    	     <xsl:apply-templates/>
    	    </ul>
    	   <xsl:text disable-output-escaping="yes"><![CDATA[<p>]]></xsl:text>
    	</xsl:when>
      	<xsl:otherwise>
    	    <ul>
    	     <xsl:apply-templates/>
    	    </ul>
    	</xsl:otherwise>
       </xsl:choose>
    </xsl:template>
        
    <xsl:template match="b">
      <strong>
        <xsl:apply-templates/>
      </strong>
    </xsl:template>
    
    <xsl:template match="i">
      <em>
        <xsl:apply-templates/>
      </em>
    </xsl:template>
        
    <xsl:template match="node()|@*" priority="-1">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*"/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
