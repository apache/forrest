<?xml version="1.0" encoding="UTF-8"?>
<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:variable name="nav" select="document('/nav.xml')"/>
  <xsl:template match="/">
   <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="body">
   <div id="flite-content-wrapper">
  	    <div id="flite-header"></div>
  	   	   <div id="flite-content">
	   		 <xsl:apply-templates/>
	       </div>
	       <div id="flite-left">
	  	      <xsl:copy-of select="$nav"/>
	  	   </div>
	       <div id="flite-footer">Footer</div>
	    </div>
  </xsl:template>
  
  <xsl:template match="head"> 
  	<link rel="stylesheet" type="text/css" href="/resources/css/style.css" media="screen"/>
  	<xsl:apply-templates/>
  </xsl:template>

    <xsl:template match="node() | @*">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>