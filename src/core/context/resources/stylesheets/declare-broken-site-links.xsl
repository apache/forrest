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

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
        <!-- Change the site: and ext: links in the output so that Forrest
             understands that they are broken -->
             
<!-- nicolaken: commenting out because it breaks the linkmap stuff.
                Just including even the same link at this point makes
                Cocoon use that link as a relative link.
                For example, if in xdocs/samples I have ext:dtd-docs, the below
                template with concat('', .) will make Cocoon search for
                samples/ext:dtd-docs, which of course does not exist.
                
  <xsl:template match="@*">
    <xsl:attribute name="{name(.)}">
      <xsl:choose>
        <xsl:when test="contains(., 'site:') or contains(., 'ext:')">
          <xsl:value-of select="concat('error:', .)"/>
        </xsl:when>
        <xsl:otherwise>
         <xsl:value-of select="."/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:attribute>
  </xsl:template>
-->
	<!-- Identity transformation template -->			
	<xsl:template match="/ | * | comment() | processing-instruction() | text()"> 
		<xsl:copy> 
			<xsl:apply-templates select="@* | * | comment() | processing-instruction() | text()"/> 
		</xsl:copy> 
	</xsl:template> 
    
  
</xsl:stylesheet>
