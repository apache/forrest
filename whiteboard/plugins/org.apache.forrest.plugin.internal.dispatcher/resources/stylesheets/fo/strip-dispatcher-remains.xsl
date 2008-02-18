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
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:fo="http://www.w3.org/1999/XSL/Format">
  <xsl:key name="static-content" match="fo:static-content" use="@flow-name"/>
  <xsl:template match="/">
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
      <fo:layout-master-set>
        <xsl:copy-of select="/fo/layout-master-set/*"/>
      </fo:layout-master-set>
      <fo:bookmark-tree>
        <xsl:copy-of select="/fo/bookmark-tree/*"/>
      </fo:bookmark-tree>
      <fo:page-sequence master-reference="book">
        <xsl:for-each select="//fo:static-content[generate-id()=generate-id(key('static-content', @flow-name))]">
          <xsl:sort select="@flow-name"/>
          <xsl:variable name="flow-name" select="@flow-name"/>
          <fo:static-content flow-name="{@flow-name}">
            <xsl:copy-of select="//fo:static-content[@flow-name=$flow-name]/*"/>
          </fo:static-content>
        </xsl:for-each>
        <fo:flow flow-name="xsl-region-body">
          <xsl:copy-of select="/fo/xsl-region-body/title/*"/>
<!-- FIXME : left was {$text-align} -->
          <fo:block text-align="left" padding-before="18pt" padding-after="18pt">
            <xsl:copy-of select="/fo/xsl-region-body/body/*"/>
          </fo:block>
          <fo:block id="term" />
        </fo:flow>
      </fo:page-sequence>
    </fo:root>
  </xsl:template>
</xsl:stylesheet>
