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

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<!--

A set of XSLT templates useful for parsing repository related commands.
A command for the repository is formated as follows:

http://domain.com/repositoryCommand/[command]/[parametersForCommand]/repositoryURI/[repositoryURI]

where [command] is one of:

* getSCO

(for parameters see below)

and [repositoryURI] is the URI of the repository we wish to access (no protocol)

====================
Command Descriptions
====================

getSCO
======

Retrieves a Sharable Content Object from the indicated repository. This command
takes the format .../getSCO/[SCOName]

where

[SCOName] is the name of the SCO we wish to retrieve

========
Examples
========

/repositoryCommand/getSCO/HowTo/repositoryURI/www.burrokeet.org/repositoryData

will retrive the SCO called HowTo from the repository located at http://www.burrokeet.org/repositoryData


-->

<xsl:param name="cmdRepositoryGetSCO">/repositoryCommand/getSCO/</xsl:param>

<xsl:param name="repositoryURILabel">/repositoryURI/</xsl:param>

<!-- Return the burrokeet command in a path that includes one -->
<xsl:template name="getRepositoryCommand">
  <xsl:param name="path"/>
  <xsl:if test="contains($path, $cmdRepositoryGetSCO)">getSCO</xsl:if>
</xsl:template>

<!-- return the name of the SCO too use -->
<xsl:template name="getSCOName">
  <xsl:param name="path"/>
  <xsl:variable name="fullCommand">
    <xsl:value-of select="substring-after($path, $cmdRepositoryGetSCO)"/>
  </xsl:variable>
  <xsl:value-of select="substring-before($fullCommand, $repositoryURILabel)"/>
</xsl:template>

<!-- return the URI of the repository to use -->
<xsl:template name="getRepositoryURI">
  <xsl:param name="path"/>
  <xsl:value-of select="substring-after($path, $repositoryURILabel)"/>
</xsl:template>


</xsl:stylesheet>
