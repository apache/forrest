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
<!DOCTYPE xsl:stylesheet [
<!ENTITY content SYSTEM 'include_content.xml'>
<!ENTITY meta SYSTEM 'include_meta.xml'>
<!ENTITY settings SYSTEM 'include_settings.xml'>
<!ENTITY styles SYSTEM 'include_styles.xml'>
<!ENTITY manifest SYSTEM 'include_manifest.xml'>
]>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:zip="http://apache.org/cocoon/zip-archive/1.0"
                xmlns:text="http://openoffice.org/2000/text"
                xmlns:xlink="http://www.w3.org/1999/xlink"
                xmlns:draw="urn:oasis:names:tc:opendocument:xmlns:drawing:1.0"
                xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0"
                xmlns:datetime="http://exslt.org/dates-and-times"
                exclude-result-prefixes="datetime">

                <xsl:template match="document">
          <zip:archive>
            <zip:entry name="content.xml" serializer="xml">
               &content;
            </zip:entry>
            <zip:entry name="meta.xml" serializer="xml">
               &meta;
            </zip:entry>
            <zip:entry name="settings.xml" serializer="xml">
               &settings;
            </zip:entry>
            <zip:entry name="styles.xml" serializer="xml">
               &styles;
            </zip:entry>
            <zip:entry name="META-INF/manifest.xml" serializer="xml">
               &manifest;
            </zip:entry>
            <zip:entry name="mimetype" serializer="text">
              <text>application/vnd.oasis.opendocument.text</text>
            </zip:entry>
            <xsl:call-template name="createImageEntries"/>
          </zip:archive>
  </xsl:template>
        <xsl:template match="@*|node()">
          <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
          </xsl:copy>
        </xsl:template>
        <xsl:template match="header">
                <text:h text:outline-level="1" text:is-list-header="true"><xsl:value-of select="title"/></text:h>
                <text:h text:outline-level="3" text:is-list-header="true"><xsl:value-of select="subtitle"/></text:h>
                <text:p><xsl:value-of select="abstract"/></text:p>
        </xsl:template>
        <xsl:template match="body">
                <xsl:apply-templates/>
        </xsl:template>
        <!-- work on the TOC stuff here -->
        <!-- <xsl:variable name="config" select="//skinconfig"/>
  <xsl:variable name="minitoc-location" select="//skinconfig/toc/@location"/>
  <xsl:template match="div[@id='skinconf-toc-page']">
    <xsl:if test="$config/toc">
      <xsl:if test="contains($minitoc-location,'page')">
        <xsl:if test="(count(//tocitems/tocitem) >= $config/toc/@min-sections) or (//tocitems/@force = 'true')">
          <xsl:call-template name="minitoc">
            <xsl:with-param name="tocroot" select="//tocitems"/>
          </xsl:call-template>
        </xsl:if>
      </xsl:if>
    </xsl:if>
  </xsl:template>
  <xsl:template name="minitoc">
    <xsl:param name="tocroot"/>
    <xsl:if test="(count($tocroot/tocitem) >= $config/toc/@min-sections) or ($tocroot/@force = 'true')">
      <xsl:if test="contains($config/toc/@location,'page')">
        <ul class="minitoc">
          <xsl:for-each select="$tocroot/tocitem">
            <li><a href="{@href}">
              <xsl:value-of select="@title"/></a>
              <xsl:if test="@level&lt;//skinconfig/toc/@max-depth+1">
                <xsl:call-template name="minitoc">
                  <xsl:with-param name="tocroot" select="."/>
                </xsl:call-template>
              </xsl:if></li>
          </xsl:for-each>
        </ul>
      </xsl:if>
    </xsl:if>
  </xsl:template>-->
        <!-- work on the TOC stuff ends -->
        <xsl:template match="section">
                <xsl:apply-templates/>
        </xsl:template>
        <xsl:template match="title">
                <text:h text:outline-level="2" text:is-list-header="true"><xsl:value-of select="."/></text:h>
        </xsl:template>
        <xsl:template match="a|link">
          <text:a xlink:type="simple" xlink:href="{@href}"><xsl:value-of select="."/></text:a>
        </xsl:template>
        <xsl:template match="p">
                <text:p><xsl:apply-templates/></text:p>
        </xsl:template>
        <xsl:template match="ul">
                <text:list>
                  <xsl:apply-templates/>
                </text:list>
        </xsl:template>
        <xsl:template match="ol">
                <text:list text:style-name="L1">
                  <xsl:apply-templates/>
                </text:list>
        </xsl:template>
        <xsl:template match="li">
                <text:list-item>
                  <xsl:choose>
                          <xsl:when test="p|note|warning|fixme">
                      <xsl:apply-templates/>
                    </xsl:when>
                    <xsl:otherwise>
                      <text:p>
                        <xsl:apply-templates/>
                      </text:p>
                    </xsl:otherwise>
                  </xsl:choose>
                </text:list-item>
        </xsl:template>
        <xsl:template match="sub">
          <text:span text:style-name="T1">
            <xsl:value-of select="."/>
          </text:span>
        </xsl:template>
        <xsl:template match="sup">
          <text:span text:style-name="T2">
            <xsl:value-of select="."/>
          </text:span>
        </xsl:template>
        <xsl:template match="em">
          <text:span text:style-name="Emphasis">
            <xsl:value-of select="."/>
          </text:span>
        </xsl:template>
        <xsl:template match="code">
          <text:span text:style-name="Source_20_Text">
            <xsl:value-of select="."/>
          </text:span>
        </xsl:template>
        <xsl:template match="strong">
          <text:span text:style-name="Strong_20_Text">
            <xsl:value-of select="."/>
          </text:span>
        </xsl:template>

        <xsl:template match="note | warning | fixme">
          <xsl:choose>
            <xsl:when test="@label">
              <xsl:value-of select="@label"/>
            </xsl:when>
            <xsl:when test="local-name() = 'note'"><text:p text:style-name="P6">Note: <xsl:value-of select="."/></text:p></xsl:when>
            <xsl:when test="local-name() = 'warning'"><text:p text:style-name="P6">Warning: <xsl:value-of select="."/></text:p></xsl:when>
            <xsl:otherwise><text:p text:style-name="P6">Fixme (<xsl:value-of select="@author"/>) <xsl:value-of select="."/></text:p></xsl:otherwise>
          </xsl:choose>
        </xsl:template>
        <xsl:template match="img|figure|icon">
                <draw:frame>
                        <xsl:attribute name="draw:style-name">fr1</xsl:attribute>
                        <xsl:attribute name="draw:name"><xsl:value-of select="@alt"/></xsl:attribute>
                        <xsl:attribute name="text:anchor-type">paragraph</xsl:attribute>
                        <xsl:attribute name="draw:z-index">0</xsl:attribute>
                        <!-- FIXME: See FOR-1098 - The svg attributes below are being ignored. -->
                        <xsl:attribute name="svg:y">0cm</xsl:attribute>
                        <xsl:attribute name="svg:width"><xsl:value-of select="../width div 36"/>cm</xsl:attribute>
                        <xsl:attribute name="svg:height"><xsl:value-of select="../height div 36"/>cm</xsl:attribute>
                        <xsl:call-template name="drawImage"/>
            </draw:frame>
            <xsl:apply-templates/>
    </xsl:template>
    <xsl:template name="drawImage">
            <draw:image>
              <xsl:attribute name="xlink:href">Pictures/<xsl:call-template name="fileName">
              <xsl:with-param name="path" select="@src"/>
              </xsl:call-template>
              </xsl:attribute>
               <xsl:attribute name="xlink:type">simple</xsl:attribute>
               <xsl:attribute name="xlink:show">embed</xsl:attribute>
               <xsl:attribute name="xlink:actuate">onLoad</xsl:attribute>
       </draw:image>
       </xsl:template>
    <!-- Tables -->
    <xsl:template match="table">
    <xsl:param name="count" select="count(following-sibling::tr)"/>
      <table:table table:name="{//caption}">
      <table:table-column table:number-columns-repeated="3" />
      <!-- FIXME: That hard coded 3 needs to be replaced with a count of how <tr> there are
              need to test and apply the 'count' param above -->
        <xsl:apply-templates/>
      </table:table>
    </xsl:template>
    <xsl:template match="th">
      <xsl:value-of select="."/>
    </xsl:template>
    <xsl:template match="tr">
      <table:table-row>
        <xsl:apply-templates/>
      </table:table-row>
    </xsl:template>
    <xsl:template match="td">
      <table:table-cell>
        <text:p>
           <xsl:apply-templates/>
         </text:p>
       </table:table-cell>
    </xsl:template>
    <!-- /Tables -->
<xsl:template name="createImageEntries">
  <xsl:for-each select="//img|//figure|//icon">
    <zip:entry>
      <xsl:attribute name="name">Pictures/<xsl:call-template name="fileName">
          <xsl:with-param name="path" select="@src"/>
        </xsl:call-template>
      </xsl:attribute>
      <xsl:attribute name="src">cocoon://<xsl:value-of select="@src"/>
      </xsl:attribute>
    </zip:entry>
  </xsl:for-each>
  <!-- Add default background image -->
  <zip:entry>
          <xsl:attribute name="name">Pictures/osswatch_background.png</xsl:attribute>
          <xsl:attribute name="src">resources/images/osswatch_background.png</xsl:attribute>
          <!-- FIXME: Would like to use project images dir, how to configure for that ? -->
  </zip:entry>
</xsl:template>

<!-- 'filename' template returns just file.txt from a path such
as /foo/bar/baz/file.txt -->

<xsl:template name="fileName">
  <xsl:param name="path" />
  <xsl:choose>
    <xsl:when test="contains($path,'\')">
      <xsl:call-template name="fileName">
        <xsl:with-param name="path" select="substring-after($path,'\')" />
      </xsl:call-template>
    </xsl:when>
    <xsl:when test="contains($path,'/')">
      <xsl:call-template name="fileName">
        <xsl:with-param name="path" select="substring-after($path,'/')" />
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$path" />
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

</xsl:stylesheet>



