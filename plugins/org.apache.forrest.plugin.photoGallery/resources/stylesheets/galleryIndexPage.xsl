<?xml version="1.0"?>
<!--
    Copyright 1999-2004 The Apache Software Foundation

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Author: Jörg Werner
-->
<xsl:stylesheet version="1.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:dir="http://apache.org/cocoon/directory/2.0">
  
  <xsl:param name="page" />
  <xsl:param name="cols"/>
  <xsl:param name="rows"/>
  
  <xsl:template match="/pics/dir:directory">
    <document>
      <header>
        <title>Gallery</title>
      </header>
      <body>
        <section>
          <title>Gallery</title>
          <xsl:variable name="all_hits" select="dir:file" />
          <xsl:variable name="count" select="count(dir:file)"/>
          <table>
            <!-- start a new data row for every 1st, 5th, 9th, etc. 'hit' element -->
            <xsl:for-each
          select="$all_hits[position() mod $cols = 1  and position() &gt; $rows*$cols*($page - 1) and position() &lt;=$rows*$cols*$page]">
              <xsl:variable name="this_hit_pos"
                select="position()" />
              <xsl:variable name="current_row_hits"
                select="$all_hits[position() &gt;= ($this_hit_pos - 1) * $cols + 1 + $rows*$cols*($page -1) and position() &lt; $this_hit_pos * $cols + 1 + $rows*$cols*($page -1)]" />
              <!-- go generate the 3 table rows for this one data row -->
              <xsl:call-template name="make_table_rows">
          <xsl:with-param name="cols" select="$cols" />
          <xsl:with-param name="current_row_hits" select="$current_row_hits" />
          <xsl:with-param name="offset" select="($this_hit_pos - 1)* $cols + $rows*$cols*($page - 1)"/>
              </xsl:call-template>
            </xsl:for-each>
      
            <!-- now the navigation -->
            <tr>
              <td width="100">&#160;</td>
              <td width="100" align="center">
          <xsl:choose>
            <xsl:when test="$page &gt; 1">
              <a href="index_{$page - 1}.html">
                <img border="0" src="button/Previous"/>
              </a>
            </xsl:when>
            <xsl:otherwise>&#160;</xsl:otherwise>
          </xsl:choose>
              </td>
              <td width="100" align="center">
          <xsl:choose>
            <xsl:when test="($page * $rows * $cols) &lt; count($all_hits)">
              <a href="index_{$page + 1}.html">
                <img border="0" src="button/Next"/>
              </a>
            </xsl:when>
            <xsl:otherwise>&#160;</xsl:otherwise>
          </xsl:choose>
              </td>
              <td width="100">&#160;</td>
            </tr>
          </table>
        </section>
      </body>
    </document>
  </xsl:template>
  
  <xsl:template name="make_table_rows">
    <xsl:param name="cols" select="1" />
    <xsl:param name="current_row_hits" select="/.." />
    <xsl:param name="offset" select="/.." />
<!-- selects above are defaults in case nothing was passed in -->
    <xsl:if test="$current_row_hits">
      <xsl:variable name="num_empty_cols"
		    select="$cols - count($current_row_hits)" />
      <tr>
	<xsl:for-each select="$current_row_hits">
	  <td width="100" height="100" align="center">
	    <a>
        <xsl:attribute name="href">pic_<xsl:value-of select="position()+$offset"/>.html</xsl:attribute>
	      <img src="preview/{@name}" />
	    </a>
	  </td>
	</xsl:for-each>
	<xsl:if test="$num_empty_cols">
	  <!-- true if not zero -->
	  <xsl:call-template name="make_empty_cells">
	    <xsl:with-param name="num" select="$num_empty_cols" />
	  </xsl:call-template>
	</xsl:if>
      </tr>
      <tr>
	<xsl:for-each select="$current_row_hits">
	  <td width="100" align="center">
	    <img border="0" src="button/{substring-before(@name, '.')}" />
	  </td>
	</xsl:for-each>
	<xsl:if test="$num_empty_cols">
	  <!-- true if not zero -->
	  <xsl:call-template name="make_empty_cells">
	    <xsl:with-param name="num" select="$num_empty_cols" />
	  </xsl:call-template>
	</xsl:if>
      </tr>
    </xsl:if>
  </xsl:template>
  <xsl:template name="make_empty_cells">
    <xsl:param name="num" select="0" />
    <xsl:if test="$num">
      <td>&#160;</td>
      <xsl:call-template name="make_empty_cells">
	<xsl:with-param name="num" select="$num - 1" />
      </xsl:call-template>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>

