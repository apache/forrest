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
<xsl:stylesheet version="1.0" 
  xmlns:style="http://openoffice.org/2000/style" 
  xmlns:text="http://openoffice.org/2000/text" 
  xmlns:office="http://openoffice.org/2000/office" 
  xmlns:table="http://openoffice.org/2000/table" 
  xmlns:draw="http://openoffice.org/2000/drawing" 
  xmlns:fo="http://www.w3.org/1999/XSL/Format" 
  xmlns:xlink="http://www.w3.org/1999/xlink" 
  xmlns:dc="http://purl.org/dc/elements/1.1/" 
  xmlns:meta="http://openoffice.org/2000/meta" 
  xmlns:number="http://openoffice.org/2000/datastyle" 
  xmlns:svg="http://www.w3.org/2000/svg" 
  xmlns:chart="http://openoffice.org/2000/chart"
  xmlns:dr3d="http://openoffice.org/2000/dr3d" 
  xmlns:math="http://www.w3.org/1998/Math/MathML" 
  xmlns:form="http://openoffice.org/2000/form" 
  xmlns:script="http://openoffice.org/2000/script" 
  xmlns:config="http://openoffice.org/2001/config" 
  office:class="text" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:resume="http://xmlresume.sourceforge.net/resume/0.0"
  exclude-result-prefixes="office meta  table number dc fo xlink chart math script xsl draw svg dr3d form config text style">
  <xsl:output method="xml" indent="yes" omit-xml-declaration="no"/>
  <xsl:output method="xml" version="1.0" encoding="UTF-8" 
    doctype-public="-//Sean Kelly//DTD Resume 1.5.1//EN" 
    doctype-system="http://xmlresume.sourceforge.net/dtd/resume.dtd"/>
<!--+
      | keys to generate the structure
      +-->
  <xsl:key name="skillsets" match="text:h[@text:level='3']" use="generate-id((preceding-sibling::text:h[@text:level='2'])[last()])"/>
  <xsl:key name="skilltables" match="table:table" use="generate-id((preceding-sibling::text:h[@text:level='3'])[last()])"/>
<!--+
      | start transforming 
      +-->
  <xsl:template match="office:document">
    <resume:resume>
      <resume:header>
        <resume:name>
          <resume:firstname>
            <xsl:value-of select="office:document-content/office:body/text:user-field-decls/text:user-field-decl[@text:name='firstname']/@text:string-value"/>
          </resume:firstname>
          <resume:surname>
            <xsl:value-of select="office:document-content/office:body/text:user-field-decls/text:user-field-decl[@text:name='lastname']/@text:string-value"/>
          </resume:surname>
        </resume:name>
      </resume:header>
      <xsl:apply-templates select="office:document-content/office:body[1]/text:h[@text:level='2']"/>
    </resume:resume>
  </xsl:template>
<!--+
      | 
      +-->
<!--+
      | process the skillareas ( level 2 headings)
      +-->
  <xsl:template match="text:h[@text:level='2']">
    <resume:skillarea>
      <resume:title>
        <xsl:value-of select="."/>
      </resume:title>
      <xsl:apply-templates select="key('skillsets', generate-id())"/>
    </resume:skillarea>
  </xsl:template>
<!--+
      | process the skillsets ( level 3 headings)
      +-->
  <xsl:template match="text:h[@text:level='3']">
    <resume:skillset>
      <resume:title>
        <xsl:value-of select="."/>
      </resume:title>
      <xsl:apply-templates select="key('skilltables', generate-id())/table:table-row"/>
    </resume:skillset>
  </xsl:template>
<!--+
      | process the Skills ( table rows)
      +-->
  <xsl:template match="table:table-row">
    <xsl:element name="resume:skill">
      <xsl:attribute name="level">
        <xsl:value-of select="table:table-cell[2]/text:p"/>
      </xsl:attribute>
      <xsl:value-of select="table:table-cell[1]/text:p"/>
    </xsl:element>
  </xsl:template>
</xsl:stylesheet>
