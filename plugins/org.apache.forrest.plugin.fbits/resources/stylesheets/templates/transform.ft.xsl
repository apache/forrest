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
  xmlns:saxon="http://saxon.sf.net/"
  extension-element-prefixes="saxon"
  >
  
  <xsl:namespace-alias stylesheet-prefix="alias" result-prefix="xsl"/>
  <!--FIXME Need to make sure all variables are matched!!!-->
 	<xsl:param name="config-file"/>
  <xsl:param name="path"/>
  <xsl:variable name="config" select="document($config-file)/skinconfig"/>
  
	<!--<xsl:include href="cocoon:/prepare.include.{$request}"/>
  <xsl:include href="cocoon:/prepare.xhtml.{$request}"/>-->

  <xsl:template match="/">
    <html>
      <head>
    		<xsl:call-template name="getHead"/>
      </head>
      <body onload="init()">
        <h1><xsl:value-of select="$request"/> </h1>
        <xsl:call-template name="getBody"/>
      </body>
    </html>
  </xsl:template>

</xsl:stylesheet>
