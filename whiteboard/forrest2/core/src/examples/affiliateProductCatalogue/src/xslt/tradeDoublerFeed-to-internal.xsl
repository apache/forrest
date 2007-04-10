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
  <xsl:template match="products">
    <html xmlns=" http://www.w3.org/2002/06/xhtml2" 
		      xml:lang="en" 
		      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
		      xsi:schemaLocation="http://www.w3.org/2002/06/xhtml2/ http://www.w3.org/MarkUp/SCHEMA/xhtml2.xsd">
      <head>
        <title>Trade Doubler Product Feed</title>
      </head>
      <body>
        <xsl:apply-templates select="product"/>
      </body>
    </html>
  </xsl:template>
  <xsl:template match="product">
    <h>
      <xsl:value-of select="name"/>
    </h>
    <p>
      <xsl:value-of select="description"/>
    </p>
    <img>
      <xsl:attribute name="src">
        <xsl:value-of select="imageUrl"/>
      </xsl:attribute>
    </img>
    <p>
      Price:
      <xsl:value-of select="price"/>
    </p>
    <p>
      <a>
      <xsl:attribute name="href">
        <xsl:value-of select="productUrl"/>
      </xsl:attribute>
      Buy Now </a>
    </p>
  </xsl:template>
</xsl:stylesheet>
