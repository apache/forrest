<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright 2002-2004 The Apache Software Foundation

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->
<xsl:stylesheet version="1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<contract name="logo" nc="logo" tlc="branding">
  <description>
    This nugget will create the logos.
  </description>
</contract>
<xsl:template match="logo" mode="xhtml">
<xsl:param name="contract">project</xsl:param>
<xsl:comment>+
    |start Logo
    +</xsl:comment> 
     <div class="{$contract}logo">
        <xsl:call-template name="renderlogo">
            <xsl:with-param name="name" select="$config/{$contract}-name"/>
            <xsl:with-param name="url" select="$config/{$contract}-url"/>
            <xsl:with-param name="logo" select="$config/{$contract}-logo"/>
            <xsl:with-param name="root" select="$root"/>
            <xsl:with-param name="description" select="$config/{$contract}-description"/>
        </xsl:call-template>
      </div>
<xsl:comment>+
    |end Logo
    +</xsl:comment> 
</xsl:template>
</xsl:stylesheet>
