<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

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
<!--
RepositoryUtils.xsl

A set of XSLT templates useful for parsing Burrokeet URI's

-->

<xsl:param name="cmdRepositoryGetSCO">http://repository.burrokeet.org/getSCO/</xsl:param>

<!-- Return the burrokeet command in a path that includes one -->
<xsl:template name="getRepositoryCommand">
  <xsl:param name="path"/>
  <xsl:if test="contains($path, $cmdRepositoryGetSCO)">getSCO</xsl:if>
</xsl:template>

<!-- return the name of the SCO too use -->
<xsl:template name="getSCOName">
  <xsl:param name="path"/>
  <xsl:value-of select="substring-after($path, $cmdRepositoryGetSCO)"/>
</xsl:template>


</xsl:stylesheet>
