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
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:collection="http://apache.org/cocoon/collection/1.0">
  <xsl:variable name="includePattern" select="'.fv'"/>
  
  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>
  <xsl:template match="collection:collection">
    <dir name="{@name}">
    	<xsl:apply-templates/>
    </dir>
  </xsl:template>
  <xsl:template match="collection:resource[contains(@name,$includePattern)]">
    <view name="{@name}">
    	<xsl:apply-templates/>
    </view>
  </xsl:template>
  <!--/collection:resource[contains(@name,$includePattern)]-->
 <!-- <xsl:template match="//*/*[contains(@name,$includePattern)]">
    <xsl:variable name="parent" select="../."/>
    <node id="{$parent/@name}"/>
   <!-#-<xsl:copy-of select="$parent"/>-#->
  </xsl:template>-->
  
</xsl:stylesheet>
