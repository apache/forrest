<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:param name="path"/>
  <xsl:include href="dotdots.xsl"/>
  <xsl:include href="pathutils.xsl"/>

  <xsl:variable name="filename">
    <xsl:call-template name="filename">
      <xsl:with-param name="path" select="$path"/>
    </xsl:call-template>
  </xsl:variable>
  
  <xsl:template name="spacer">
    <td> | </td>
  </xsl:template>
  
  <xsl:template name="not-selected">
      <td>
         <a>
           <xsl:attribute name="href">
             <xsl:call-template name="dotdots">
               <xsl:with-param name="path" select="$filename"/>
             </xsl:call-template>
             <xsl:if test="@dir != ''">
               <xsl:value-of select="concat(translate(normalize-space(translate(@dir, ' /', '/ ')), ' /', '/ '), '/')"/>
             </xsl:if>
           </xsl:attribute>
           <xsl:value-of select="@label"/></a>
      </td>
  </xsl:template>
  
  <xsl:template name="selected">
      <td> <b><xsl:value-of select="@label"/></b> </td>
  </xsl:template>
  
  <xsl:template match="tabs">
    <div class="tab">
      <table cellspacing="0" cellpadding="2" border="0" summary="tab bar">
        <tr>
          <xsl:apply-templates/>
        </tr>
      </table>
    </div>
  </xsl:template>
  
  <xsl:template match="tab">
    <xsl:call-template name="spacer"/>
    <xsl:choose>
      <xsl:when test="$filename!='' and @dir=''">
        <xsl:call-template name="not-selected"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:choose>
          <xsl:when test="starts-with($filename,@dir)">
           <xsl:call-template name="selected"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:call-template name="not-selected"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
