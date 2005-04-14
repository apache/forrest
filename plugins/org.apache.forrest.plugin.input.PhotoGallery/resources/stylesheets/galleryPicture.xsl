<?xml version="1.0"?>
<!--
    Copyright 1999-2004 The Apache Software Foundation or its licensors,
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

Author Jörg Werner
-->
<xsl:stylesheet version="1.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:dir="http://apache.org/cocoon/directory/2.0">
  <xsl:param name="pos" />
  <xsl:param name="big" />
  <xsl:template match="/dir:directory">
    <document>
      <xsl:variable name="all_hits" select="dir:file" />
      <xsl:for-each select="$all_hits[position() = $pos]">
	<header>
	  <title><xsl:value-of select="substring-before(@name,'.')"/></title>
	</header>
	<body>
	  <table>
	    <tr align="center" valign="middle">
        <td colspan="3">
          <xsl:variable name="indexRoundedNum"><xsl:value-of select="round($pos div 20)"/></xsl:variable>
          <xsl:variable name="indexModNum"><xsl:value-of select="$pos mod 20"/></xsl:variable>
          <xsl:variable name="indexNum">
            <xsl:choose>
              <xsl:when test="$indexModNum = 0 or $indexModNum >= 10">
                <xsl:value-of select="$indexRoundedNum"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="$indexRoundedNum + 1"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:variable>
          <a>
            <xsl:attribute name="href">index_<xsl:value-of select="$indexNum"/>.html</xsl:attribute>
            Index Page
          </a>
        </td>
      </tr>
	    <tr align="center" valign="middle">
	      <td>
		<xsl:if test="$pos &gt; 1">
		  <a>
        <xsl:choose>
          <xsl:when test="$big=0">
            <xsl:attribute name="href">pic_<xsl:value-of select="$pos - 1"/>.html</xsl:attribute>
          </xsl:when>
          <xsl:otherwise>
            <xsl:attribute name="href">fullPic_<xsl:value-of select="$pos - 1"/>.html</xsl:attribute>
          </xsl:otherwise>
        </xsl:choose>
		    <img src="images/leftarrow.png"/><br/>
        Previous Picture
		  </a> 
		</xsl:if>
	      </td>
	      <td>
		<xsl:choose>
		  <xsl:when test="$big = 0">
		    <a>
          <xsl:attribute name="href">fullPic_<xsl:value-of select="$pos"/>.html</xsl:attribute>
		      <img src="small/{@name}" /><br/>
          Click to View Largest Picture
		    </a>
		  </xsl:when>
		  <xsl:otherwise>
		    <a>
          <xsl:attribute name="href">pic_<xsl:value-of select="$pos"/>.html</xsl:attribute>
		      <img src="big/{@name}" /><br/>
          Click to View Smaller Picture
		    </a>
		  </xsl:otherwise>
		</xsl:choose> 
	      </td>
	      <td>
		<xsl:if test="$pos &lt; count($all_hits)">
		  <a>
        <xsl:choose>
          <xsl:when test="$big=0">
            <xsl:attribute name="href">pic_<xsl:value-of select="$pos + 1"/>.html</xsl:attribute>
          </xsl:when>
          <xsl:otherwise>
            <xsl:attribute name="href">fullPic_<xsl:value-of select="$pos + 1"/>.html</xsl:attribute>
          </xsl:otherwise>
        </xsl:choose>
        <img src="images/rightarrow.png"/><br/>
        Next Picture
      </a>
		</xsl:if>
	      </td>
	    </tr>
	  </table>
	</body>
      </xsl:for-each>
    </document>
  </xsl:template>
</xsl:stylesheet>
