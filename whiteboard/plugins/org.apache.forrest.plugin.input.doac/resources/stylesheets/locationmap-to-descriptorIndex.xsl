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
<xsl:stylesheet version = "1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:lm="http://apache.org/forrest/locationmap/1.0"
  xmlns:cinclude="http://apache.org/cocoon/include/1.0">
  <xsl:template match="lm:locationmap">
    <descriptors>
      <categories>        
          <cinclude:include>
            <xsl:attribute name="src">cocoon:/person/categoryDefinitions.xml</xsl:attribute>
          </cinclude:include>
      </categories>
      <xsl:apply-templates/>
    </descriptors>
  </xsl:template>
  <xsl:template match="lm:locator/lm:match[starts-with(@pattern, 'doac.descriptor')]">
    <xsl:variable name="href-noext">
       <xsl:value-of select="substring-after(@pattern, 'doac.descriptor.')"/>
    </xsl:variable>
    <descriptor href-noext="/person/{$href-noext}">
      <xsl:attribute name="id">
        <xsl:value-of select="@pattern"/>
      </xsl:attribute>
      <cinclude:include>
        <xsl:attribute name="src">cocoon:/person/<xsl:value-of select="$href-noext"/>.source.xml</xsl:attribute>
      </cinclude:include>
    </descriptor>
  </xsl:template>
</xsl:stylesheet>
