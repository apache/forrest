<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xsl:output method="xml" indent="yes" />

   <xsl:param name="skin-name" />
   <xsl:param name="forrest-version" />

   <xsl:template match="skins">
      <project default="fetchskin">
      
         <target name="fetchskin" depends="fetch-versioned-skin, fetch-unversioned-skin, final-check"/>

         <target name="fetch-versioned-skin">
            <echo>Trying to get "<xsl:value-of select="$skin-name" />" skin version 
                  <xsl:value-of select="$forrest-version" />...</echo>
            <get verbose="true" usetimestamp="true" ignoreerrors="true">
               <xsl:attribute name="src"><xsl:value-of select="skin[@name=$skin-name]/@url" /><xsl:value-of select="$skin-name" />-<xsl:value-of select="$forrest-version" />.fsj</xsl:attribute>
               <xsl:attribute name="dest">${forrest.home}/context/skins/<xsl:value-of select="$skin-name" />.fsj</xsl:attribute>
            </get>
         </target>

         <target name="fetch-unversioned-skin" unless="versioned-skin.present">
            <echo>Versioned skin unavailable, trying to get versionless skin...</echo>

            <get verbose="true" usetimestamp="true" ignoreerrors="true">
               <xsl:attribute name="src"><xsl:value-of select="skin[@name=$skin-name]/@url" /><xsl:value-of select="$skin-name" />.fsj</xsl:attribute>
               <xsl:attribute name="dest">${forrest.home}/context/skins/<xsl:value-of select="$skin-name" />.fsj</xsl:attribute>
            </get>
         </target>

         <target name="final-check">
            <available property="skin.present">
               <xsl:attribute name="file">${forrest.home}/context/skins/<xsl:value-of select="$skin-name" />.fsj</xsl:attribute>
            </available>

            <fail unless="skin.present">
              Unable to download the 
              "<xsl:value-of select="$skin-name" />" skin from 
              <xsl:value-of select="skin[@name=$skin-name]/@url" />. 
              In case the reason is the network connection, you can try 
              installing the package manually by placing the file in the 
              skins directory.</fail>

            <echo>Skin "<xsl:value-of select="$skin-name" />" correctly installed.</echo>
         </target>
      </project>
   </xsl:template>

   <xsl:template match="skin">
   </xsl:template>
   
</xsl:stylesheet>

