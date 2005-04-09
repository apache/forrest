<?xml version="1.0"?>
<!--
  Copyright 2002-2004 The Apache Software Foundation or its licensors,
  as applicable.

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

<!--+
    | Replace element for the value on the project descriptor 
    | xmlns:for has to be replaced for the final version
    +-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:for="http://apache.org/forrest" version="1.0">

  <xsl:import href="copyover.xsl"/>

  <xsl:variable name="config" select="//skinconfig"/>

  <xsl:template match="for:*">
    <xsl:param name="ancestorpath" select="''"/>
    <xsl:choose>
      <xsl:when test="*">
        <xsl:apply-templates>
          <xsl:with-param name="ancestorpath"
            select="concat($ancestorpath,'/',local-name())"/>
        </xsl:apply-templates>
      </xsl:when>
      <xsl:otherwise>
        <xsl:variable name="tmp"
          select="concat($ancestorpath, '/', local-name())"/>
        <xsl:value-of
          select="$config//*[concat($ancestorpath, '/', local-name()) = $tmp]"/>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>

</xsl:stylesheet>
