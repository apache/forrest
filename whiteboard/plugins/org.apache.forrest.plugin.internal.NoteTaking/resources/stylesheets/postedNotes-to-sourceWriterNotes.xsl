<?xml version='1.0'?>
<xsl:stylesheet version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:h="http://apache.org/cocoon/request/2.0"
  xmlns:source="http://apache.org/cocoon/source/1.0">
  
  <!-- the name of the file to append the notes to -->
  <xsl:param name="notesFile"/>
  
  <xsl:template match="h:request">
   <source:insert>
      <source:path>notes/<xsl:value-of select="h:requestParameters/h:parameter[@name='path']"/></source:path>
      <source:source>
        <xsl:value-of select="$notesFile"/>
      </source:source>
      <source:replace>note</source:replace>
      <source:fragment>
        <note>
          <xsl:value-of select="h:requestParameters/h:parameter[@name='note']"/>
        </note>
      </source:fragment>
    </source:insert>
  </xsl:template>
</xsl:stylesheet>
