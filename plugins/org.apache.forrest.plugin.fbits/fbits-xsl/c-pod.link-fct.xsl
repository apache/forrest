<?xml version="1.0" encoding="UTF-8"?>
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
<forrest:contract name="pod-fct" nc="pod" tlc="content"
  xmlns:forrest="http://apache.org/forrest/templates/1.0">
  <description>
    This functions will output the POD link with image.
  </description>

	<xsl:stylesheet version="1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	  <xsl:template name="xml" mode="xhtml-head">
	    <head/>
	  </xsl:template>
	  
	  <xsl:template name="xml" mode="xhtml-body">
	    <body>
	      <div class="podlink" title="Plain Old Documentation"><a href="{$filename-noext}.pod" class="dida">
	        <img class="skin" src="{$skin-img-dir}/poddoc.png" alt="POD - icon" /><br/>
	        POD</a>
	      </div>
	    </body>
	  </xsl:template>
	
	</xsl:stylesheet>
</forrest:contract>