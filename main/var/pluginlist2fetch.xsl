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
   <xsl:output method="xml" indent="yes" />

   <xsl:param name="plugin-name" />
   <xsl:param name="plugin-dir"/>
   <xsl:param name="forrest-version" />

   <xsl:template match="plugins">
         <xsl:choose>
           <xsl:when test="plugin[@name=$plugin-name]">
      <project default="fetchplugin">
         <target name="fetchplugin" depends="fetch-versioned-plugin, fetch-unversioned-plugin, final-check"/>

         <target name="fetch-versioned-plugin">
            <echo>Trying to get "<xsl:value-of select="$plugin-name" />" plugin version 
                  <xsl:value-of select="$forrest-version" />...</echo>
            <get verbose="true" usetimestamp="true" ignoreerrors="true">
               <xsl:attribute name="src"><xsl:value-of select="plugin[@name=$plugin-name]/@url" />/<xsl:value-of select="$plugin-name" />-<xsl:value-of select="$forrest-version" />.zip</xsl:attribute>
               <xsl:attribute name="dest"><xsl:value-of select="$plugin-dir"/><xsl:value-of select="$plugin-name" />.zip</xsl:attribute>
            </get>
            <available property="versioned-plugin.present">
               <xsl:attribute name="file"><xsl:value-of select="$plugin-dir"/><xsl:value-of select="$plugin-name" />.zip</xsl:attribute>
            </available>
         </target>

         <target name="fetch-unversioned-plugin" unless="versioned-plugin.present">
            <echo>Versioned plugin unavailable, trying to get versionless plugin...</echo>
            <get verbose="true" usetimestamp="true" ignoreerrors="true">
               <xsl:attribute name="src"><xsl:value-of select="plugin[@name=$plugin-name]/@url" />/<xsl:value-of select="$plugin-name" />.zip</xsl:attribute>
               <xsl:attribute name="dest"><xsl:value-of select="$plugin-dir"/><xsl:value-of select="$plugin-name" />.zip</xsl:attribute>
            </get>
         </target>

         <target name="final-check">
            <available property="plugin.present">
               <xsl:attribute name="file"><xsl:value-of select="$plugin-dir"/><xsl:value-of select="$plugin-name" />.zip</xsl:attribute>
            </available>
            <fail unless="plugin.present">
              Unable to download the 
              "<xsl:value-of select="$plugin-name" />" plugin from 
              <xsl:value-of select="plugin[@name=$plugin-name]/@url" />
              There are a number of possible causes for this:
              
              One possible problem is that you do not have write access to 
              FORREST_HOME, in which case ask your system admin to install the 
              required Forrest plugin as described below.
              
              A further possibility is that Forrest may be unable to connect to 
              the plugin distribution server, again the solution is to maually 
              install the plugin.
              
              To manually install a plugin download the plugin zip file from
              <xsl:value-of select="plugin[@name=$plugin-name]/@url"/> and
              extract it into to <xsl:value-of select="$plugin-dir"/><xsl:value-of select="$plugin-name" /></fail>
            <echo>Plugin "<xsl:value-of select="$plugin-name" />" correctly installed.</echo>
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

