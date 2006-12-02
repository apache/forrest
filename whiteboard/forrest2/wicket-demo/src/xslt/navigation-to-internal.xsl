<?xml version="1.0" encoding="utf-8"?>
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
<xsl:stylesheet version="1.0" xmlns="http://www.w3.org/2002/06/xhtml2"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:html="http://www.w3.org/2002/06/xhtml2">

	<xsl:template match="navigation">
		<html xmlns=" http://www.w3.org/2002/06/xhtml2" 
		      xml:lang="en" 
		      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
		      xsi:schemaLocation="http://www.w3.org/2002/06/xhtml2/ http://www.w3.org/MarkUp/SCHEMA/xhtml2.xsd">
			<head>
				<title>Navigation</title>
			</head>
			<body>
			  <ul>
				<xsl:apply-templates/>
			  </ul>
			</body>
		</html>
	</xsl:template>
	
	<!-- The item element provides a link to some otehr item in the
	content object. -->
	<xsl:template match="item">
	  <li class="menuitem">
	    <xsl:choose>
	    <xsl:when test="@href">
	    <a>
	      <xsl:attribute name="href"><xsl:value-of select="@href"/></xsl:attribute>
	      <xsl:value-of select="@label"/>
	    </a>
	    </xsl:when>
	    <xsl:otherwise>
	      <xsl:value-of select="@label"/>
	    </xsl:otherwise>
	    </xsl:choose>
	    <xsl:if test="item">
	      <ul>
	        <xsl:apply-templates/>
	      </ul>
	    </xsl:if>
	  </li>
	</xsl:template>
	
	<!--  The parent element provides a link back to the
	      parent directory. -->
	<xsl:template match="parent">
	  <li>
	    <a href="..">
	      <xsl:value-of select="@label"/>
	    </a>
	  </li>
	    
	</xsl:template>

</xsl:stylesheet>
