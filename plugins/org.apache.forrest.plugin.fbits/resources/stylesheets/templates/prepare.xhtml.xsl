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

<xsl:stylesheet version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:forrest="http://apache.org/forrest/templates/1.0"  
  xmlns:xi="http://www.w3.org/2001/XInclude"
  xmlns:alias="http://www.w3.org/1999/XSL/TransformAlias"
  >
  <xsl:namespace-alias stylesheet-prefix="alias" result-prefix="xsl"/>
  
  <xsl:template match="/">
    <alias:stylesheet version="1.0">
      <xsl:comment>All xhtml head elements requested by the forrest:template</xsl:comment>
			<alias:template name="getHead">
			  <xsl:for-each select="/*/forrest:properties/*[@head='true']">
		      <alias:call-template name="{@name}-head" />
			  </xsl:for-each>
		   </alias:template>
	    <xsl:comment>All xhtml css elements requested by the forrest:template</xsl:comment>
			<alias:template name="getCss">
			  <xsl:for-each select="/*/forrest:properties/*[@css='true']">
		      <alias:call-template name="{@name}-css" />
			  </xsl:for-each>
		   </alias:template>
		   	   
      <xsl:comment>All xhtml body elements requested by the forrest:template</xsl:comment>
			<alias:template name="getBody">
          <xsl:apply-templates select="/*/forrest:view"/>
		   </alias:template>
		</alias:stylesheet>
	</xsl:template>
	
  <xsl:template match="forrest:view">
    <xsl:apply-templates/>
  </xsl:template>
	
	<xsl:template match="forrest:hook">
    <div id="{@name}">
      <xsl:apply-templates/>
    </div>
  </xsl:template>
  
  <xsl:template match="forrest:contract">
    <xsl:variable name="name" select="@name"/>
    <!--Test whether there is a body template needed-->
    <xsl:if test="/*/forrest:properties/*[@body='true' and @name=$name]">
	    <alias:call-template name="{@name}-body" />
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
