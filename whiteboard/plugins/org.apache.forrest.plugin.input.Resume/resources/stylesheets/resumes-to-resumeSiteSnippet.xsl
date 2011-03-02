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
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:dir="http://apache.org/cocoon/directory/2.0"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:resume="http://xmlresume.sourceforge.net/resume/0.0">
  <xsl:template match="resumes">
    <resumes label="Resumes" href="resume/">
      <ooFerdinand label=" Soethe, Ferdinand" href="soethe_ferdinand.html" description="Resume for Ferdinand Soethe"/>
      <xsl:apply-templates/>
    </resumes>
  </xsl:template>
  <xsl:template match="file">
    <xsl:apply-templates>
      <xsl:with-param name="filename" select="concat(@filename,'.html')"/>
    </xsl:apply-templates>
  </xsl:template>
  <xsl:template match="resume:resume">
    <xsl:param name="filename"/>
    <xsl:variable name="firstname" select="resume:header/resume:name/resume:firstname"/>
    <xsl:variable name="surname" select="resume:header/resume:name/resume:surname"/>
    <resume>
      <xsl:attribute name="label">
        <xsl:value-of select="$surname"/>, <xsl:value-of select="$firstname"/>
      </xsl:attribute>
      <xsl:attribute name="href">
        <xsl:value-of select="$filename"/>
      </xsl:attribute>
      <xsl:attribute name="description">Resume for <xsl:value-of select="$firstname"/>
        <xsl:value-of select="$surname"/>
      </xsl:attribute>
    </resume>
  </xsl:template>
</xsl:stylesheet>
