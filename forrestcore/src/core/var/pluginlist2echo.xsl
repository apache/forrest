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

	<xsl:template match="plugins">
      <project default="echoplugins">
      	<target name="echoplugins">
      	<echo>Available plugins:
Forrest provides basic functionlaity for creating documentation in various
formats froma range of source formats. However, additional functionlaity
can be provided through plugins.

Plugins may be maintained by other people and be available from
outside the Forrest distribution. The list below details all known plugins.
</echo>
  	    <xsl:apply-templates select="plugin" />
	    </target>
	  </project>
	</xsl:template>
	
	<xsl:template match="plugin">
<echo>
* <xsl:value-of select="@name"/> - <xsl:value-of select="normalize-space(description)"/>
  - author: <xsl:value-of select="@author"/>
  - website: <xsl:value-of select="@website"/> 	    
</echo>
	</xsl:template>	
	
</xsl:stylesheet>
