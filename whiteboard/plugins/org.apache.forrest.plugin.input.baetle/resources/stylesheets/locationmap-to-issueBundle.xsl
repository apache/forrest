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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:lm="http://apache.org/forrest/locationmap/1.0"
                xmlns:cinclude="http://apache.org/cocoon/include/1.0"
                version ="1.0">

  <xsl:template match="lm:locationmap">
    <issues>
      <xsl:apply-templates/>
    </issues>
  </xsl:template>

  <xsl:template match="lm:locator/lm:match[starts-with(@pattern, 'baetle.bissues.')]">
    <xsl:variable name="href-noext" 
                  select="substring-after(@pattern, 'baetle.bissues.')"/>
    <issue href-noext="samples/bissues/{$href-noext}">
      <xsl:attribute name="id">
        <xsl:value-of select="@pattern"/>
      </xsl:attribute>
      <cinclude:include>
        <xsl:attribute name="src">
          <xsl:choose>
            <xsl:when test="starts-with(lm:location[1]/@src, 'http://')">
              <xsl:value-of select="lm:location[1]/@src"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="concat('cocoon:/samples/bissues/',
                                    $href-noext, '.source.xml')"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
      </cinclude:include>
    </issue>
  </xsl:template>
</xsl:stylesheet>
