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
<!--
Stylesheet for generating an aggregated feed from multple feeds.
-->
<xsl:stylesheet version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  
  <xsl:output method="xml" version="1.0" encoding="UTF-8"/>
  
  <xsl:param name="tag"/>
  
  <xsl:template match="taggedSources">
    <feedDescriptor>
      <xsl:apply-templates/>
    </feedDescriptor>
  </xsl:template>
  
  <xsl:template match="source">
    <feed id="{@id}">
      <url><xsl:value-of select="@href"/><xsl:value-of select="$tag"/></url>
    </feed>
  </xsl:template>
</xsl:stylesheet>
