<?xml version="1.0"?>
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
<!--+
    | Upgrade xconf from cocoon CVS
    |
    +-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">


  <xsl:template match="/">
<xsl:comment>
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
</xsl:comment>
<xsl:comment>
=======================================================================
File created by etc/cocoon_upgrade/upgrade-skinconf.xsl
Please do not Edit!!!!!
========================================================================
</xsl:comment>
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="cocoon">
    <xsl:copy>
    <xsl:copy-of select="@*"/>
    <xsl:apply-templates select="sitemap"/>
    <xsl:apply-templates select="input-modules"/>
    <xsl:apply-templates select="source-factories"/>
    <xsl:apply-templates select="entity-resolver"/>
    <xsl:apply-templates select="xml-parser"/>
    <xsl:apply-templates select="xslt-processor"/>
    <xsl:apply-templates select="component"/>
    <xsl:apply-templates select="xpath-processor"/>
    <xsl:apply-templates select="xmlizer"/>
    <xsl:apply-templates select="transient-store"/>
    <xsl:apply-templates select="store"/>
    <xsl:apply-templates select="persistent-store"/>
    <xsl:apply-templates select="store-janitor"/>
    <xsl:apply-templates select="classloader"/>
    <xsl:apply-templates select="xml-serializer"/>
    <xsl:apply-templates select="xml-deserializer"/>
    </xsl:copy>
  </xsl:template>

  <!-- Whole elements trees that need to be copied as is -->
  <xsl:template match="sitemap|component-instance|xml-parser|xslt-processor|xpath-processor|classloader|xml-serializer|xml-deserializer">
    <xsl:copy>
    <xsl:copy-of select="@*"/>
    <!-- FIXME: remove comment() elements -->
    <xsl:copy-of select="node()"/>
    </xsl:copy>
  </xsl:template>

  <!-- Trees with parameter elements so we can avoid comments -->
  <xsl:template match="component|transient-store|store|persistent-store|store-janitor">
    <xsl:copy>
    <xsl:copy-of select="@*"/>
    <xsl:copy-of select="parameter"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="input-modules">
    <xsl:comment>======= Input Modules needed by forrest input module =========</xsl:comment>
    <xsl:element name="input-modules">
    <xsl:apply-templates select="component-instance[@name='request']"/>
    <xsl:apply-templates select="component-instance[@name='request-param']"/>
    <xsl:apply-templates select="component-instance[@name='request-attr']"/>
    <xsl:apply-templates select="component-instance[@name='session-attr']"/>
    <xsl:apply-templates select="component-instance[@name='realpath']"/>
    <xsl:call-template name="forrest-input"/>
    </xsl:element>
  </xsl:template>

  <xsl:template name="forrest-input">
    <xsl:comment>======= Forrest Input =========</xsl:comment>
   
    <xsl:element name="component-instance">
      <xsl:copy-of select="component-instance[@name='chain']/@logger"/>
      <xsl:attribute name="name">forrest</xsl:attribute>
      <xsl:copy-of select="component-instance[@name='chain']/@class"/>
      <input-module name="request-param"/>
      <input-module name="request-attr"/>
      <input-module name="session-attr"/>
      <input-module name="defaults"/>
    </xsl:element>

    <xsl:comment>======= Defaults Model is replaced by ant using tokens =========</xsl:comment>
    <xsl:element name="component-instance">
      <xsl:copy-of select="component-instance[@name='defaults']/@logger"/>
      <xsl:attribute name="name">defaults</xsl:attribute>
      <xsl:copy-of select="component-instance[@name='defaults']/@class"/>
      <xsl:element name="values">
        <skin>@skin@</skin>
        <menu-scheme>@menu-scheme@</menu-scheme>
        <bugtracking-url>@bugtracking-url@</bugtracking-url>
        <i18n>@i18n@</i18n>
        <home>@forrest.home@/</home>
        <skins-dir>@forrest.home@/context/skins/</skins-dir>
        <stylesheets>@forrest.home@/context/resources/stylesheets</stylesheets>
      </xsl:element>
    </xsl:element>

    <xsl:comment>======= "Project" Defaults Model is replaced by ant using tokens =========</xsl:comment>
    <xsl:element name="component-instance">
      <xsl:copy-of select="component-instance[@name='defaults']/@logger"/>
      <xsl:attribute name="name">project</xsl:attribute>
      <xsl:copy-of select="component-instance[@name='defaults']/@class"/>
      <xsl:element name="values">
        <skin>@skin@</skin>
        <skinconf>@project.webapp@/skinconf.xml</skinconf>
        <doc>@project.home@/@project.content-dir@/</doc>
        <content>@project.home@/@project.raw-content-dir@/</content>
        <content.xdocs>@project.home@/@project.xdocs-dir@/</content.xdocs>
        <translations>@project.home@/@project.translations-dir@</translations>
        <resources.stylesheets>@project.home@/@project.stylesheets-dir@/</resources.stylesheets>
        <resources.images>@project.home@/@project.images-dir@/</resources.images>
        <skins-dir>@project.home@/@project.skins-dir@/</skins-dir>
      </xsl:element>
    </xsl:element>

    <xsl:comment>======= Skinconf Defaults Model is replaced by values on the skinconf.xml file =========</xsl:comment>
    <xsl:element name="component-instance">
      <xsl:copy-of select="component-instance[@name='simplemap']/@logger"/>
      <xsl:attribute name="name">conf</xsl:attribute>
      <xsl:copy-of select="component-instance[@name='simplemap']/@class"/>
      <input-module name="skinconf">
        <file src="skinconf.xml" reloadable="true" />
      </input-module>
      <prefix>/skinconfig/</prefix>
    </xsl:element>

    <xsl:element name="component-instance">
      <xsl:copy-of select="component-instance[@name='myxml']/@logger"/>
      <xsl:attribute name="name">linkmap</xsl:attribute>
      <xsl:copy-of select="component-instance[@name='myxml']/@class"/>
    </xsl:element>
    
    <xsl:element name="component-instance">
      <xsl:copy-of select="component-instance[@name='myxml']/@logger"/>
      <xsl:attribute name="name">skinconf</xsl:attribute>
      <xsl:copy-of select="component-instance[@name='myxml']/@class"/>
    </xsl:element>

    <xsl:element name="component-instance">
      <xsl:copy-of select="component-instance[@name='simplemap']/@logger"/>
      <xsl:attribute name="name">site</xsl:attribute>
      <xsl:copy-of select="component-instance[@name='simplemap']/@class"/>
    </xsl:element>

    <xsl:element name="component-instance">
      <xsl:copy-of select="component-instance[@name='simplemap']/@logger"/>
      <xsl:attribute name="name">ext</xsl:attribute>
      <xsl:copy-of select="component-instance[@name='simplemap']/@class"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="source-factories">
    <xsl:comment>======= This is needed by the Open Office Support =========</xsl:comment>
    <xsl:element name="source-factories">
      <!-- Needed for the OO source -->
      <component-instance class="org.apache.cocoon.components.source.impl.ZipSourceFactory" name="zip"/>
      <xsl:copy-of select="component-instance"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="entity-resolver">
    <xsl:comment>======= Entity Resolution built-in =========</xsl:comment>
    <xsl:element name="entity-resolver">
      <xsl:copy-of select="@logger"/>
      <parameter name="catalog" value="@forrest.home@/context/resources/schema/catalog.xcat"/>
      <parameter name="local-catalog" value="@local-catalog@"/>
      <parameter name="verbosity" value="@catalog-verbosity@"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="xmlizer">
    <xsl:element name="xmlizer">
      <xsl:copy-of select="@*"/>
      <xsl:copy-of select="parser"/>
      <parser mime-type="text/html" role="org.apache.excalibur.xml.sax.SAXParser/HTML"/>
    </xsl:element>
  </xsl:template>

</xsl:stylesheet>
