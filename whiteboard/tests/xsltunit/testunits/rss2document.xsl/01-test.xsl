<?xml version="1.0" ?>
<!--
  Copyright 2002-2005 The Apache Software Foundation or its licensors,
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
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsltu="http://xsltunit.org/0/"
>

  <xsl:import href="cocoon://code/xsltunit.xsl"/>

  <xsl:strip-space elements="*"/>

  <xsl:template match="/">
    <xsl:call-template name="xsltu:assertEqual">
      <xsl:with-param name="id" select="'Title for the channel'"/>
      <xsl:with-param name="nodes1" select="."/>
      <xsl:with-param name="nodes2"  select="document('01-output.xml')"/>
    </xsl:call-template>
  </xsl:template>

</xsl:stylesheet>
