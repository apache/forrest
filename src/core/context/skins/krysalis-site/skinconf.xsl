<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
        
    <xsl:template match="skinconfig">

  <xsl:if test="not(colors)">
  <colors>
  <!-- Krysalis -->
    <color name="header"    value="#FFFFFF"/>

    <color name="tab-selected" value="#a5b6c6"/>
    <color name="tab-unselected" value="#F7F7F7"/>
    <color name="subtab-selected" value="#a5b6c6"/>
    <color name="subtab-unselected" value="#a5b6c6"/>

    <color name="heading" value="#a5b6c6"/>
    <color name="subheading" value="#CFDCED"/>
        
    <color name="navstrip" value="#CFDCED"/>
    <color name="toolbox" value="#a5b6c6"/>
    <color name="border" value="#a5b6c6"/>
    
    <color name="menu" value="#F7F7F7"/>    
    <color name="dialog" value="#F7F7F7"/>
            
    <color name="body"      value="#ffffff"/>
    
    <color name="table" value="#a5b6c6"/>    
    <color name="table-cell" value="#ffffff"/>    
    <color name="highlight" value="#yellow"/>
    <color name="fixme" value="#c60"/>
    <color name="note" value="#069"/>
    <color name="warning" value="#900"/>
    <color name="code" value="#a5b6c6"/>
        
    <color name="footer" value="#a5b6c6"/>
  </colors>
  </xsl:if>

     <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:copy-of select="node()[not(name(.)='colors')]"/>     
      <xsl:apply-templates select="colors"/>
     </xsl:copy> 

    </xsl:template>

    <xsl:template match="colors">
     <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:copy-of select="node()[name(.)='color']"/> 
      
     <xsl:if test="not(color[@name='header'])">
       <color name="header" value="#FFFFFF"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='tab-selected'])">
      <color name="tab-selected" value="#a5b6c6"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='tab-unselected'])">
      <color name="tab-unselected" value="#F7F7F7"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='subtab-selected'])">
      <color name="subtab-selected" value="#a5b6c6"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='subtab-unselected'])">
      <color name="subtab-unselected" value="#a5b6c6"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='heading'])">
      <color name="heading" value="#a5b6c6"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='subheading'])">
      <color name="subheading" value="#CFDCED"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='navstrip'])">
      <color name="navstrip" value="#CFDCED"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='toolbox'])">
       <color name="toolbox" value="#a5b6c6"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='border'])">
       <color name="border" value="#a5b6c6"/>
     </xsl:if>       
     <xsl:if test="not(color[@name='menu'])">
       <color name="menu" value="#F7F7F7"/>    
     </xsl:if>  
     <xsl:if test="not(color[@name='dialog'])">
      <color name="dialog" value="#F7F7F7"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='body'])">
      <color name="body" value="#ffffff"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='table'])">
      <color name="table" value="#a5b6c6"/>    
     </xsl:if>  
     <xsl:if test="not(color[@name='table-cell'])">
      <color name="table-cell" value="#ffffff"/>    
     </xsl:if>  
     <xsl:if test="not(color[@name='highlight'])">
       <color name="highlight" value="#ffff00"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='fixme'])">
       <color name="fixme" value="#c60"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='note'])">
       <color name="note" value="#069"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='warning'])">
       <color name="warning" value="#900"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='code'])">
       <color name="code" value="#a5b6c6"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='footer'])">
       <color name="footer" value="#a5b6c6"/>
     </xsl:if>  
    
     </xsl:copy> 

    </xsl:template>
    
</xsl:stylesheet>
