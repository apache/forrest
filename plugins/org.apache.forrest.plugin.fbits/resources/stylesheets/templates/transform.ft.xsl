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
  xmlns:dyn="http://exslt.org/dynamic"
  extension-element-prefixes="dyn"
  >
  
  <xsl:namespace-alias stylesheet-prefix="alias" result-prefix="xsl"/>
  <!--FIXME 
    - Need to make sure all variables are matched!!!
    - Make sure that this variables get dynamically-->
 	<xsl:param name="config-file"/>
  <xsl:param name="path"/>
  <xsl:param name="request"/>
  <xsl:variable name="config" select="document($config-file)/skinconfig"/>
  <xsl:variable name="filename-noext">toBeImplemended</xsl:variable>
  <xsl:variable name="skin-img-dir">skin/images</xsl:variable>
  <xsl:variable name="root"></xsl:variable>

	<xsl:include href="cocoon:/prepare.include.dyn:evaluate($request)"/>
  <xsl:include href="cocoon:/prepare.xhtml.dyn:evaluate($request)"/>

  <xsl:template match="/">
    <xhtml>
      <head>
    		<xsl:call-template name="getHead"/>
<!--FIXME:
  Need to discuss how to insert default values-->
         <style type="text/css">
body {background-color: yellow}
h1 {font-size: 36pt}
h2 {color: blue}
p {margin-left: 50px}
<xsl:call-template name="getCss"/>
</style>

      </head>
      <body onload="init()">
        <xsl:call-template name="getBody"/>
      </body>
    </xhtml>
  </xsl:template>

</xsl:stylesheet>
