<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0" xmlns:style="http://openoffice.org/2000/style" xmlns:text="http://openoffice.org/2000/text" xmlns:office="http://openoffice.org/2000/office" xmlns:table="http://openoffice.org/2000/table" xmlns:draw="http://openoffice.org/2000/drawing" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:meta="http://openoffice.org/2000/meta" xmlns:number="http://openoffice.org/2000/datastyle" xmlns:svg="http://www.w3.org/2000/svg" xmlns:chart="http://openoffice.org/2000/chart" xmlns:dr3d="http://openoffice.org/2000/dr3d" xmlns:math="http://www.w3.org/1998/Math/MathML" xmlns:form="http://openoffice.org/2000/form" xmlns:script="http://openoffice.org/2000/script" xmlns:config="http://openoffice.org/2001/config" office:class="text" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" exclude-result-prefixes="office meta  table number dc fo xlink chart math script xsl draw svg dr3d form config text style">
  
  <xsl:import href="openoffice-common2forrest.xsl"/>
  
  
  <!-- ================================================================== -->
  <!-- The templates below match special styles created in a special 
       Forrest template. However, there is no longer any need to use this
       template as support for Open Office defined styles is being 
       embedded into the stylesheets, that is, all style information should
       be copied into the HTML page.                                      -->
  <!-- ================================================================== -->     
  
  <!--+
      | strong emphasised text
      +-->
  <xsl:template match="text:span[@text:style-name='Strong Emphasis']">
    <strong>
      <xsl:apply-templates/>
    </strong>
  </xsl:template>  

  <!--+
      | emphasised text
      +-->
  <xsl:template match="text:span[@text:style-name='Emphasis']">
    <em>
      <xsl:apply-templates/>
    </em>
  </xsl:template>    

  <!--+
      | Code
      +-->
  <xsl:template match="text:span[@text:style-name='Forrest: Code']">
    <code>
      <xsl:value-of select="."/>
    </code>
  </xsl:template>
  
  <!--+
      | sup
      +-->
  <xsl:template match="text:span[@text:style-name='Forrest: Above']">
    <sup>
      <xsl:value-of select="."/>
    </sup>
  </xsl:template>
  <!--+
      | sub
      +-->
  <xsl:template match="text:span[@text:style-name='Forrest: Below']">
    <sub>
      <xsl:value-of select="."/>
    </sub>
  </xsl:template>    
  
  <!--+
      | Source
      +-->
  <xsl:template match="text:p[@text:style-name='Forrest: Source']">
    <source>
<xsl:text>
</xsl:text>
      <xsl:apply-templates/>
<xsl:text>
</xsl:text>
   
    </source>
  </xsl:template>      
  <xsl:template match="text:p[@text:style-name='Forrest: Source']/text:line-break">
    <br/>
  </xsl:template> 
  
  <!--+
      | Warning
      +-->
  <xsl:template match="text:p[@text:style-name='Forrest: Warning']">
    <warning>
      <xsl:apply-templates/>
    </warning>
  </xsl:template>
  
  <!--+
      | Fixme
      | - the author attribute is ignored but this has to change with
      |   XHTML2 anyway ... (RP)
      +-->
  <xsl:template match="text:p[@text:style-name='Forrest: Fixme']">
    <fixme>
      <xsl:apply-templates/>
    </fixme>
  </xsl:template>

  <!--+
      | Note
      +-->
  <xsl:template match="text:p[@text:style-name='Forrest: Note']">
    <note>
      <xsl:apply-templates/>
    </note>
  </xsl:template>    
</xsl:stylesheet>
