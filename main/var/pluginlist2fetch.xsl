<?xml version="1.0"?>
<!--
  Copyright 2002-2006 The Apache Software Foundation or its licensors,
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
   <xsl:output method="xml" indent="yes" />

   <xsl:param name="plugin-name" />
   <xsl:param name="plugin-version" />
   <xsl:param name="plugin-dir"/>
   <xsl:param name="plugin-src-dir"/>
   <xsl:param name="forrest-version" />

   <xsl:template match="plugins">
         <xsl:choose>
           <xsl:when test="plugin[@name=$plugin-name]">
      <project default="fetchplugin">
         <target name="fetchplugin" depends="fetch-versioned-plugin, fetch-unversioned-plugin, final-check"/>

         <target name="fetch-versioned-plugin">
            <xsl:if test="$plugin-version">
              <echo>Trying to get "<xsl:value-of select="$plugin-name" />" plugin, version <xsl:value-of select="$plugin-version" />
for Forrest version <xsl:value-of select="$forrest-version" />...</echo>
              <get verbose="true" usetimestamp="true" ignoreerrors="true">
                 <xsl:attribute name="src"><xsl:value-of select="plugin[@name=$plugin-name]/@url" />/<xsl:value-of select="$forrest-version" />/<xsl:value-of select="$plugin-name" />-<xsl:value-of select="$plugin-version" />.zip</xsl:attribute>
                 <xsl:attribute name="dest"><xsl:value-of select="$plugin-dir"/><xsl:value-of select="$plugin-name" />-<xsl:value-of select="$plugin-version" />.zip</xsl:attribute>
              </get>
              <available property="versioned-plugin.present">
                 <xsl:attribute name="file"><xsl:value-of select="$plugin-dir"/><xsl:value-of select="$plugin-name" />-<xsl:value-of select="$plugin-version" />.zip</xsl:attribute>
              </available>
            </xsl:if>
         </target>

         <target name="fetch-unversioned-plugin"
           unless="versioned-plugin.present">
            <echo>Versioned plugin unavailable, trying to get versionless plugin...</echo>
            <trycatch property="plugin-found">
              <try>
                <for param="plugin-src-dir">
                  <xsl:attribute name="list"><xsl:value-of select="$plugin-src-dir" /></xsl:attribute>
                  <sequential>
                    <echo>Looking in local @{plugin-src-dir}</echo>
                    <if>
                      <available property="plugin.src.present" type="dir">
                        <xsl:attribute name="file">@{plugin-src-dir}/<xsl:value-of select="$plugin-name" /></xsl:attribute>
                      </available>
                      <then>
                        <ant target="local-deploy">
                          <xsl:attribute name="antfile">@{plugin-src-dir}/<xsl:value-of select="$plugin-name" />/build.xml</xsl:attribute>
                          <xsl:attribute name="dir">@{plugin-src-dir}/<xsl:value-of select="$plugin-name" /></xsl:attribute>
                        </ant>
                        <fail/>
                      </then>
                    </if>
                  </sequential>
                </for>
              </try>
              <catch>
                <echo>Plugin <xsl:value-of select="$plugin-name" /> deployed !</echo>
              </catch>
            </trycatch>
            <if>
              <not>
                <isset property="plugin-found"/>
              </not>
              <then>
                <echo>Tying to download from the distribution site ...</echo>
                <get verbose="true" usetimestamp="true" ignoreerrors="true">
                  <xsl:attribute name="src"><xsl:value-of select="plugin[@name=$plugin-name]/@url" />/<xsl:value-of select="$plugin-name" />.zip</xsl:attribute>
                  <xsl:attribute name="dest"><xsl:value-of select="$plugin-dir"/><xsl:value-of select="$plugin-name" />.zip</xsl:attribute>
                </get>
              </then>
            </if>
         </target>

         <target name="final-check">
            <available property="desired.plugin.zip.present">
              <xsl:choose>
                <xsl:when test="$plugin-version">
                  <xsl:attribute name="file"><xsl:value-of select="$plugin-dir"/><xsl:value-of select="$plugin-name" />-<xsl:value-of select="$plugin-version" />.zip</xsl:attribute>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:attribute name="file"><xsl:value-of select="$plugin-dir"/><xsl:value-of select="$plugin-name" />.zip</xsl:attribute>
                </xsl:otherwise>
              </xsl:choose>
            </available>

            <if>
              <isset property="desired.plugin.zip.present"/>
              <then>
                <echo><xsl:value-of select="$plugin-name" /> downloaded, ready to install</echo>
              </then>
              <else>
                <available property="unversioned.plugin.present" type="dir">
                  <xsl:attribute name="file"><xsl:value-of select="$plugin-dir"/><xsl:value-of select="$plugin-name" /></xsl:attribute>
                </available>
                <fail unless="unversioned.plugin.present">
  Unable to download the
  "<xsl:value-of select="$plugin-name" />" plugin
  <xsl:if test="$plugin-version">version <xsl:value-of select="$plugin-version"/></xsl:if>
  or an equivalent unversioned plugin
  from <xsl:value-of select="plugin[@name=$plugin-name]/@url" />
  There are a number of possible causes for this:

  One possible problem is that you do not have write access to
  FORREST_HOME, in which case ask your system admin to install the
  required Forrest plugin as described below.

  A further possibility is that Forrest may be unable to connect to
  the plugin distribution server. Again the solution is to manually
  install the plugin.

  To manually install a plugin, download the plugin zip file from
  <xsl:value-of select="plugin[@name=$plugin-name]/@url"/> and
  extract it into
  <xsl:value-of select="$plugin-dir"/><xsl:value-of select="$plugin-name" /></fail>
              </else>
            </if>
         </target>
      </project>
         </xsl:when>
         <xsl:otherwise>
            <project default="findPlugin">
              <target name="findPlugin"/>
            </project>
         </xsl:otherwise>
       </xsl:choose>
   </xsl:template>

   <xsl:template match="plugin">
   </xsl:template>

</xsl:stylesheet>

