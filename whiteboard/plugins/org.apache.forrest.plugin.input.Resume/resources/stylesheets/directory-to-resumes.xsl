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
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:dir="http://apache.org/cocoon/directory/2.0"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:resume="http://xmlresume.sourceforge.net/resume/0.0"
                xmlns:cinclude="http://apache.org/cocoon/include/1.0">
  <xsl:template match="dir:directory">
    <resumes>
      <xsl:apply-templates select="dir:file"/>
    </resumes>
  </xsl:template>
  <xsl:template match="dir:file">
    <xsl:variable name="name" select="substring-before(@name,'.xml')"/>
    <file>
      <xsl:attribute name="filename">
        <xsl:value-of select="$name"/>
      </xsl:attribute>
      <cinclude:include>
        <xsl:attribute name="src">
          <xsl:value-of select="concat('cocoon://team/resume/',$name, '.source.xml')"/>
        </xsl:attribute>
      </cinclude:include>
    </file>
  </xsl:template>
  <xsl:template match="@*|*|text()|processing-instruction()|comment()">
    <xsl:copy>
      <xsl:apply-templates select="@*|*|text()|processing-instruction()|comment()"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
