<?xml version="1.0" encoding="UTF-8"?>
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
<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:dir="http://apache.org/cocoon/directory/2.0"
    xmlns:session="http://apache.org/cocoon/session/1.0"
    xmlns:forrest="http://apache.org/forrest/templates/1.0"
    >

  <!--
      Create row for each document.  Information about the document is
      extracted from the document itself using the document()
      function.
  -->
  <xsl:template match="/">
   <document>
    	<header>
        <title>ls.contracts</title>
    	</header>
    	<body>
        <xsl:apply-templates/>
    	</body>
  	</document>
  </xsl:template>
<xsl:template match="forrest:contracts">
  <xsl:apply-templates/>
</xsl:template>
<xsl:template match="forrest:contract">
  <section>
  <title><xsl:value-of select="@name" /></title>
<p class="file"><strong>file-name:</strong> <br/><xsl:value-of select="@file-name" /></p>
<p class="description"><strong>description:</strong> <br/><xsl:value-of select="./description" /></p>
<p class="usage"><strong>usage:</strong></p>
<source><xsl:value-of select="./usage" /></source>
<p class="path"><strong>realpath:</strong> <br/><xsl:value-of select="./realpath" /></p>
</section>
</xsl:template>

</xsl:stylesheet>

