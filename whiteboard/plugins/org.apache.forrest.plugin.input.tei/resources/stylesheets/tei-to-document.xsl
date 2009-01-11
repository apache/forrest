<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform' version='1.0'
                xmlns:fn="http://www.w3.org/2006/xpath-functions">

<xsl:output method="xml" version="1.0" indent="yes" cdata-section-elements="Program"/>

<!--

  tei-to-document.xsl
-->
	<xsl:template match='TEI.2'>
		<document>
		
			<header>
                                <title><xsl:value-of select="teiHeader/fileDesc/titleStmt/title"/></title> 
			</header>
			
				<xsl:apply-templates/>
			</document>
	</xsl:template>
	
    <xsl:template match="body">
      <body>
        <xsl:apply-templates/>
      </body>
    </xsl:template>
    
	<xsl:template match="div">
      <section>
        <title><xsl:value-of select="head"/></title>
		<xsl:apply-templates />
      </section>
	</xsl:template>
	
	<!-- lists -->
	<xsl:template match="list[@type='gloss']">
	  <xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="list[@type='ordered']">
	  <ol><xsl:apply-templates/></ol>
	</xsl:template>
	
	<xsl:template match="list[@type='unordered']">
      <ul><xsl:apply-templates/></ul>
    </xsl:template>
    
    <xsl:template match="list[@type='bulleted']">
	  <ul><xsl:apply-templates/></ul>
	</xsl:template>
	
	<xsl:template match="list[@type='simple']">
	  <ul><xsl:apply-templates/></ul>
	</xsl:template>
	
	<xsl:template match="label[parent::list[@type='gloss']]">
        <p><strong><xsl:apply-templates/></strong></p>
    </xsl:template>
    
    <xsl:template match="item[parent::list[@type='gloss']]">
      <p><xsl:apply-templates/></p>
    </xsl:template>
    
    <xsl:template match="item">
	  <li><xsl:apply-templates/></li>
	</xsl:template>
        
    <xsl:template match="xref">
      <link>
        <xsl:attribute name="href"><xsl:value-of select="translate(@url,'.xml','.html')"/></xsl:attribute>
        <xsl:value-of select="."/>
      </link>
    </xsl:template>
    
    <xsl:template match="xptr">
      <link>
        <xsl:attribute name="href"><xsl:value-of select="@url"/></xsl:attribute>
        <xsl:value-of select="@url"/>
      </link>
    </xsl:template>
    
    <xsl:template match="Program">
      <source>
        <xsl:apply-templates/>
      </source>
    </xsl:template>

    <xsl:template match="emph">
      <em>
        <xsl:apply-templates/>
      </em>
    </xsl:template>
    
    <xsl:template match="hi">
      <strong><xsl:apply-templates/></strong>
    </xsl:template>
    
    <xsl:template match="text">
      <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="figure">
      <img>
        <xsl:attribute name="src"><xsl:value-of select="@url"/></xsl:attribute>
        <!-- <xsl:attribute name="alt"><xsl:value-of select="@rend"/></xsl:attribute> -->
        <!-- Uncomment above line once we have images being displayed -->
      </img>
    </xsl:template>
    
    <xsl:template match="@url">
      <xsl:attribute name="href"><xsl:apply-templates/></xsl:attribute>
    </xsl:template>
    
    <!-- do nothing templates -->   

	<!-- teiheader (do nothing) -->
	<xsl:template match="teiHeader" />
	
	<!-- teiheader (do nothing) -->
	<xsl:template match="front/titlePage" />
		
	<!-- head (head) -->
	<xsl:template match="head" />

    <xsl:template match="node()|@*" priority="-1">
      <xsl:copy>
        <xsl:apply-templates select="node()|@*"/>
      </xsl:copy>
    </xsl:template>
</xsl:stylesheet>
