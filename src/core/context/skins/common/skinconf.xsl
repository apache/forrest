<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
        
    <xsl:template match="skinconfig">

     <xsl:if test="not(disable-lucene)">     
       <disable-lucene>true</disable-lucene>
     </xsl:if>
     <xsl:if test="not(disable-search)">     
       <disable-search>false</disable-search>
     </xsl:if>
     <xsl:if test="not(disable-print-link)">     
       <disable-print-link>true</disable-print-link>
     </xsl:if>
     <xsl:if test="not(disable-pdf-link)">     
       <disable-pdf-link>false</disable-pdf-link>
     </xsl:if>
     <xsl:if test="not(disable-xml-link)">     
       <disable-xml-link>true</disable-xml-link>
     </xsl:if>
     <xsl:if test="not(disable-external-link-image)">     
       <disable-external-link-image>false</disable-external-link-image>
     </xsl:if>
     <xsl:if test="not(disable-compliance-links)">     
       <disable-compliance-links>false</disable-compliance-links>
     </xsl:if>
     <xsl:if test="not(obfuscate-mail-links)">     
       <obfuscate-mail-links>true</obfuscate-mail-links>
     </xsl:if>
     <!--
     <xsl:if test="not(searchsite-domain)">     
       <searchsite-domain>mydomain</searchsite-domain>
     </xsl:if>
     <xsl:if test="not(searchsite-name)">     
       <searchsite-name>MyProject</searchsite-name>
     </xsl:if>
     <xsl:if test="not(project-name)">     
       <project-name>MyProject</project-name>
     </xsl:if>
     <xsl:if test="not(project-description)">     
       <project-description>MyProject Description</project-description>
     </xsl:if>
     <xsl:if test="not(project-url)">     
       <project-url>http://myproj.mygroup.org/</project-url>
     </xsl:if>
     <xsl:if test="not(project-logo)">     
       <project-logo>images/project.png</project-logo>
     </xsl:if>
     <xsl:if test="not(group-name)">     
       <group-name>MyGroup</group-name>
     </xsl:if>
     <xsl:if test="not(group-description)">     
       <group-description>MyGroup Description</group-description>
     </xsl:if>
     <xsl:if test="not(group-url)">     
       <group-url>http://mygroup.org</group-url>
     </xsl:if>
     <xsl:if test="not(group-logo)">     
       <group-logo>images/group.png</group-logo>
     </xsl:if>
     <xsl:if test="not(host-url)">     
       <host-url/>
     </xsl:if>
     <xsl:if test="not(host-logo)">     
       <host-logo/>
     </xsl:if>
     <xsl:if test="not(year)">     
       <year>2004</year>
     </xsl:if>
     <xsl:if test="not(vendor)">     
       <vendor>The Acme Software Foundation.</vendor>
     </xsl:if>
     -->
     <xsl:if test="not(trail)">
       <trail>
         <link1 name="" href=""/>
         <link2 name="" href=""/>
         <link3 name="" href=""/>
       </trail>
       
     </xsl:if>
     <xsl:if test="not(trail)">
        <toc level="2" location="page"/>
     </xsl:if>
     
  <xsl:if test="not(colors)">
  <colors>
    <color name="header" value="#294563"/>

    <color name="tab-selected" value="#4a6d8c"/>
    <color name="tab-unselected" value="#b5c7e7"/>
    <color name="subtab-selected" value="#4a6d8c"/>
    <color name="subtab-unselected" value="#4a6d8c"/>

    <color name="heading" value="#294563"/>
    <color name="subheading" value="#4a6d8c"/>
        
    <color name="navstrip" value="#cedfef"/>
    <color name="toolbox" value="#294563"/>
    
    <color name="menu" value="#4a6d8c"/>    
    <color name="dialog" value="#4a6d8c"/>
            
    <color name="body" value="#ffffff"/>
    
    <color name="table" value="#7099C5"/>    
    <color name="table-cell" value="#f0f0ff"/>    
    <color name="highlight" value="#yellow"/>
    <color name="fixme" value="#c60"/>
    <color name="note" value="#069"/>

    <color name="warning" value="#900"/>
    <color name="code" value="#CFDCED"/>
        
    <color name="footer" value="#cedfef"/>
  </colors>
  </xsl:if>

  <xsl:if test="not(extra-css)">
    <extra-css>
    </extra-css>
  </xsl:if>
  <xsl:if test="not(credits)">
   <credits>
    <credit>
      <name>Built with Apache Forrest</name>
      <url>http://xml.apache.org/forrest/</url>
      <image>images/built-with-forrest-button.png</image>
      <width>88</width>
      <height>31</height>
    </credit>
    <!-- A credit with @role='pdf' will have its name and url displayed in the
    PDF page's footer. -->
  </credits>     
  </xsl:if>
 
     <xsl:copy>
      <xsl:copy-of select="*"/>
     </xsl:copy> 
    </xsl:template>

  
</xsl:stylesheet>
