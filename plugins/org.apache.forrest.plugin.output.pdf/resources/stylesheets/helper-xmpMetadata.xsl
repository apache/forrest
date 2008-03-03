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
<!-- $Id$ -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:fo="http://www.w3.org/1999/XSL/Format" version="1.0">
  <xsl:template name="createXMPMetadata">
    <xsl:variable name="authors" select="/site/document/header/authors/person"/>
    <fo:declarations>
      <x:xmpmeta xmlns:x="adobe:ns:meta/">
        <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
          <rdf:Description rdf:about="" xmlns:dc="http://purl.org/dc/elements/1.1/">
            <dc:title>
              <rdf:Alt>
                <rdf:li xml:lang="x-default">
                  <xsl:value-of select="/site/document/header/title"/>
                </rdf:li>
              </rdf:Alt>
            </dc:title>
            <dc:description>
              <rdf:Alt>
                <rdf:li xml:lang="x-default">
                  <xsl:value-of select="$config/project-name"/>
                </rdf:li>
              </rdf:Alt>
            </dc:description>
            <xsl:if test="count($authors) &gt; 0">
              <dc:creator>
                <rdf:Seq>
                  <xsl:for-each select="$authors">
                    <rdf:li><xsl:value-of select="@name"/></rdf:li>
                  </xsl:for-each>
                </rdf:Seq>
              </dc:creator>
            </xsl:if>
          </rdf:Description>
          <rdf:Description about="" xmlns="http://ns.adobe.com/pdf/1.3/" xmlns:pdf="http://ns.adobe.com/pdf/1.3/">
            <pdf:Creator>Apache Forrest - http://forrest.apache.org/</pdf:Creator>
          </rdf:Description>
        </rdf:RDF>
      </x:xmpmeta>
    </fo:declarations>
  </xsl:template>
</xsl:stylesheet>
