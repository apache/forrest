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
<!-- $Id$ -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:key name="month" match="logentry" use="substring(date, 1, 7)"/>
  <xsl:template match="/">
    <status>
      <xsl:apply-templates select="log"/>
    </status>
  </xsl:template>
  <xsl:template match="log">
    <changes>
      <xsl:for-each 
        select="//logentry[generate-id(.)=generate-id(key('month',substring(date, 1, 7)))]">
        <xsl:sort select="date" order="descending"/>
        <release version="{substring(date, 1, 7)}">
          <xsl:for-each select="key('month',substring(date, 1, 7))">
            <xsl:sort select="@revision"/>
            <revision id="{@revision}"/>
          </xsl:for-each>
        </release>
      </xsl:for-each>
    </changes>
  </xsl:template>
</xsl:stylesheet>
