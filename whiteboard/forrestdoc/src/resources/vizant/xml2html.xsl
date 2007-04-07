<?xml version="1.0"?>
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
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method = "html" />
  <xsl:template match="graph">
    <html>
      <head>
        <meta http-equiv="content-type" content="text/html; charset=ISO8859-1" />
        <title>Vizant - Ant task to visualize buildfiles</title><link rel="stylesheet" href="main.css" media="all" />
      </head>
      <body>
        <h1>
          <xsl:value-of select="@name" />
        </h1>
        <xsl:apply-templates />
      </body>
    </html>
  </xsl:template>
<!--
	<xsl:template match="attributes">
	 <h3><xsl:value-of select="@type" /></h3> 
       <xsl:apply-templates select="attribute"/>
	</xsl:template>
	
	<xsl:template match="attribute">
	  <xsl:value-of select="@name" />=<xsl:value-of select="@value" /><br />
	</xsl:template>
-->
  <xsl:template match="node">
    <h2><a name="{@id}">
      <xsl:value-of select="@id" /></a>
    </h2>
  </xsl:template>
  <xsl:template match="edge">
    <xsl:value-of select="@from" /> -> <a href="#{@to}">
    <xsl:value-of select="@to" /></a>
    <br />
  </xsl:template>
  <xsl:template match="subgraph">
    <h2>subgraph  
  	   <if select="@numcluster">
  	             "cluster:<xsl:value-of select="@numcluster" />"
  	          </if>
    </h2>
    <h3>label"="<xsl:value-of select="@label" />
    </h3>
    <xsl:apply-templates />
  </xsl:template>
</xsl:stylesheet>
