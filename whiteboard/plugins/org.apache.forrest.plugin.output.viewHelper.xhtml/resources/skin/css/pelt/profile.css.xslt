<?xml version="1.0"?>
<!--
  Copyright 2002-2004 The Apache Software Foundation or its licensors,
  as applicable.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:import href="forrest.css.xslt"/>

  <!-- xsl:output is not used by Forrest but makes it possible to debug the 
       stylesheet in standalone editors -->
  <xsl:output method = "text"  omit-xml-declaration="yes"  />
  
<!-- ==================== main block colors ============================ -->

<xsl:template match="color[@name='header']">
#container          { background-color: <xsl:value-of select="@value"/>;}  
</xsl:template>

<xsl:template match="color[@name='tab-selected']"> 
#nav-main .current { background-color: <xsl:value-of select="@value"/>;} 
#nav-main .current a:link {  color: <xsl:value-of select="@link"/>;  }
#nav-main .current a:visited { color: <xsl:value-of select="@vlink"/>; }
#nav-main .current a:hover { color: <xsl:value-of select="@hlink"/>; }
</xsl:template> 

<xsl:template match="color[@name='tab-unselected']"> 
#nav-main li      { background-color: <xsl:value-of select="@value"/> ;} 
#nav-main li a:link {  color: <xsl:value-of select="@link"/>;  }
#nav-main li a:visited { color: <xsl:value-of select="@vlink"/>; }
#nav-main li a:hover { color: <xsl:value-of select="@hlink"/>; }
</xsl:template> 

<xsl:template match="color[@name='subtab-selected']">
#level2tabs       { background-color: <xsl:value-of select="@value"/> ;} 
#level2tabs a:link {  color: <xsl:value-of select="@link"/>;  }
#level2tabs a:visited { color: <xsl:value-of select="@vlink"/>; }
#level2tabs a:hover { color: <xsl:value-of select="@hlink"/>; }
</xsl:template> 

<!--xsl:template match="color[@name='subtab-unselected']">
.level2tabstrip { background-color: <xsl:value-of select="@value"/>;}
.datenote { background-color: <xsl:value-of select="@value"/>;} 
.level2tabstrip.unselected a:link {  color: <xsl:value-of select="@link"/>;  }
.level2tabstrip.unselected a:visited { color: <xsl:value-of select="@vlink"/>; }
.level2tabstrip.unselected a:hover { color: <xsl:value-of select="@hlink"/>; }
</xsl:template--> 

<xsl:template match="color[@name='heading']">
.heading { background-color: <xsl:value-of select="@value"/>;} 
</xsl:template> 

<xsl:template match="color[@name='subheading']">
.boxed { background-color: <xsl:value-of select="@value"/>;} 
.underlined_5 	{border-bottom: solid 5px <xsl:value-of select="@value"/>;}
.underlined_10 	{border-bottom: solid 10px <xsl:value-of select="@value"/>;}
table caption { 
background-color: <xsl:value-of select="@value"/>; 
color: <xsl:value-of select="@font"/>;
}
</xsl:template> 
<xsl:template match="color[@name='feedback']">    
#feedback {
color: <xsl:value-of select="@font"/>;
background: <xsl:value-of select="@value"/>;
text-align: <xsl:value-of select="@align"/>;
}
#feedback #feedbackto {
color: <xsl:value-of select="@font"/>;
}   
</xsl:template>

<xsl:template match="color[@name='breadtrail']">
#page .breadtrail {
background: <xsl:value-of select="@value"/>; 
color: <xsl:value-of select="@font"/>;
}
#page .breadtrail a:link {  color: <xsl:value-of select="@link"/>;  }
#page .breadtrail a:visited { color: <xsl:value-of select="@vlink"/>; }
#page .breadtrail a:hover { color: <xsl:value-of select="@hlink"/>; }
#container .breadtrail {
background: <xsl:value-of select="@value"/>; 
color: <xsl:value-of select="@font"/>;
}
#container .breadtrail a:link {  color: <xsl:value-of select="@link"/>;  }
#container .breadtrail a:visited { color: <xsl:value-of select="@vlink"/>; }
#container .breadtrail a:hover { color: <xsl:value-of select="@hlink"/>; }    
</xsl:template>
<!--Fix for other (old) profiles-->
<xsl:template match="color[@name='navstrip']">
#publishedStrip { 
color: <xsl:value-of select="@font"/>;
background: <xsl:value-of select="@value"/>; 
}
</xsl:template> 
<!--has to go after the nav-strip (no 'navstrip')-->
<xsl:template match="color[@name='published']">
#publishedStrip { 
color: <xsl:value-of select="@font"/>;
background: <xsl:value-of select="@value"/>; 
}
</xsl:template> 
<xsl:template match="color[@name='toolbox']">
#leftbar .menupagetitle  { background-color: <xsl:value-of select="@value"/>}
</xsl:template> 

<xsl:template match="color[@name='border']">
#leftbar           { border-color: <xsl:value-of select="@value"/>;}
#leftbar .menupagetitle  { border-color: <xsl:value-of select="@value"/>;}
#leftbar .menupageitemgroup  { border-color: <xsl:value-of select="@value"/>;}
</xsl:template> 

<xsl:template match="color[@name='menu']">
#leftbar      { background-color: <xsl:value-of select="@value"/>;} 
#leftbar  {  color: <xsl:value-of select="@font"/>;} 
#leftbar a:link {  color: <xsl:value-of select="@link"/>;} 
#leftbar a:visited {  color: <xsl:value-of select="@vlink"/>;} 
#leftbar a:hover {  
background-color: <xsl:value-of select="@value"/>;
color: <xsl:value-of select="@hlink"/>;} 
</xsl:template> 
<xsl:template match="color[@name='dialog']"> 
#leftbar .menupagetitle  { color: <xsl:value-of select="@font"/>;}    
#leftbar .menupageitemgroup     { 
background-color: <xsl:value-of select="@value"/>;
}
#leftbar .menupageitem {
color: <xsl:value-of select="@font"/>;
} 
#leftbar .menupageitem a:link {  color: <xsl:value-of select="@link"/>;} 
#leftbar .menupageitem a:visited {  color: <xsl:value-of select="@vlink"/>;} 
#leftbar .menupageitem a:hover {  
background-color: <xsl:value-of select="@value"/>;
color: <xsl:value-of select="@hlink"/>;
}
</xsl:template> 
<xsl:template match="color[@name='menuheading']">
#leftbar h1 {
color: <xsl:value-of select="@font"/>;
background-color: <xsl:value-of select="@value"/>;
}   
</xsl:template> 
<xsl:template match="color[@name='searchbox']"> 
#header .searchbox { 
background-color: <xsl:value-of select="@value"/> ;
color: <xsl:value-of select="@font"/>; 
} 
</xsl:template>

<xsl:template match="color[@name='body']">
body{ 
background-color: <xsl:value-of select="@value"/>;
color: <xsl:value-of select="@font"/>;
} 
a:link { color:<xsl:value-of select="@link"/>} 
a:visited { color:<xsl:value-of select="@vlink"/>} 
a:hover { color:<xsl:value-of select="@hlink"/>} 

</xsl:template>

<xsl:template match="color[@name='footer']"> 
#footer       { background-color: <xsl:value-of select="@value"/>;} 
</xsl:template> 


<!-- ==================== other colors ============================ -->
<xsl:template match="color[@name='highlight']"> 
.highlight        { background-color: <xsl:value-of select="@value"/>;} 
</xsl:template> 

<xsl:template match="color[@name='fixme']"> 
.fixme        { border-color: <xsl:value-of select="@value"/>;} 
</xsl:template> 

<xsl:template match="color[@name='note']"> 
.note         { border-color: <xsl:value-of select="@value"/>;} 
</xsl:template> 

<xsl:template match="color[@name='warning']"> 
.warning         { border-color: <xsl:value-of select="@value"/>;} 
</xsl:template>

<xsl:template match="color[@name='code']"> 
.code         { border-color: <xsl:value-of select="@value"/>;} 
</xsl:template> 

<xsl:template match="color[@name='table']"> 
.ForrestTable      { background-color: <xsl:value-of select="@value"/>;} 
</xsl:template> 

<xsl:template match="color[@name='table-cell']"> 
.ForrestTable td   { background-color: <xsl:value-of select="@value"/>;} 
</xsl:template> 


</xsl:stylesheet>
