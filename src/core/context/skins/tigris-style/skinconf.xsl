<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
        
    <xsl:template match="skinconfig">

  <xsl:if test="not(colors)">
  <colors>
  <!-- Collabnet --> 
    <color name="header"    value="#ffffff"/><!-- 003366 -->

    <color name="tab-selected" value="#dddddd"/>
    <color name="tab-unselected" value="#999999"/>
    <color name="subtab-selected" value="#cccccc"/>
    <color name="subtab-unselected" value="#cccccc"/>

    <color name="heading" value="#003366"/>
    <color name="subheading" value="#888888"/>
    
    <color name="navstrip" value="#dddddd"/>
    <color name="toolbox" value="#dddddd"/>
    <color name="border" value="#999999"/>
    
    <color name="menu" value="#ffffff"/>    
    <color name="dialog" value="#eeeeee"/>
            
    <color name="body"      value="#ffffff"/>
    
    <color name="table" value="#ccc"/>    
    <color name="table-cell" value="#ffffff"/>   
    <color name="highlight" value="#ffff00"/>
    <color name="fixme" value="#cc6060"/>
    <color name="note" value="#006609"/>
    <color name="warning" value="#990000"/>
    <color name="code" value="#003366"/>
        
    <color name="footer" value="#ffffff"/>
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
       <color name="header" value="#ffffff"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='tab-selected'])">
      <color name="tab-selected" value="#dddddd"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='tab-unselected'])">
      <color name="tab-unselected" value="#999999"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='subtab-selected'])">
      <color name="subtab-selected" value="#cccccc"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='subtab-unselected'])">
      <color name="subtab-unselected" value="#cccccc"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='heading'])">
      <color name="heading" value="#003366"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='subheading'])">
      <color name="subheading" value="#888888"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='navstrip'])">
      <color name="navstrip" value="#dddddd"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='toolbox'])">
       <color name="toolbox" value="#dddddd"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='border'])">
       <color name="border" value="#999999"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='menu'])">
       <color name="menu" value="#ffffff"/>    
     </xsl:if>  
     <xsl:if test="not(color[@name='dialog'])">
      <color name="dialog" value="#eeeeee"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='body'])">
      <color name="body" value="#ffffff"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='table'])">
      <color name="table" value="#cccccc"/>    
     </xsl:if>  
     <xsl:if test="not(color[@name='table-cell'])">
      <color name="table-cell" value="#ffffff"/>    
     </xsl:if>  
     <xsl:if test="not(color[@name='highlight'])">
       <color name="highlight" value="#ffff00"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='fixme'])">
       <color name="fixme" value="#cc6600"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='note'])">
       <color name="note" value="#006699"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='warning'])">
       <color name="warning" value="#990000"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='code'])">
       <color name="code" value="#003366"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='footer'])">
       <color name="footer" value="#ffffff"/>
     </xsl:if>  
     </xsl:copy> 

    </xsl:template>
    
</xsl:stylesheet>
