<?xml version="1.0"?>
<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

<!--
This is the main stylesheet for analyzing the Forrest sitemaps.  It calls
the others as needed.  

Status:  Currently just trying to get it working.  It's pretty ugly, lots
of duplication and no output style.  I'm limited by my serious xslt skill
deficiencies at the moment. 

-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
			xmlns:map="http://apache.org/cocoon/sitemap/1.0"
			xmlns:xalan="http://xml.apache.org">
      
  <xsl:import href="common-printer.xsl"/>
  <xsl:import href="generator-printer.xsl"/>
  <xsl:import href="transformer-printer.xsl"/>
  <xsl:import href="selector-printer.xsl"/>

  <xsl:variable name="aggregate" select="document('../../main/webapp/aggregate.xmap')"/>
  <xsl:variable name="faq" select="document('../../main/webapp/faq.xmap')"/>
  <xsl:variable name="forrest" select="document('../../main/webapp/forrest.xmap')"/>
  <xsl:variable name="i18n" select="document('../../main/webapp/i18n.xmap')"/>
  <xsl:variable name="issues" select="document('../../main/webapp/issues.xmap')"/>
  <xsl:variable name="linkmap" select="document('../../main/webapp/linkmap.xmap')"/>
  <xsl:variable name="menu" select="document('../../main/webapp/menu.xmap')"/>
  <xsl:variable name="profiler" select="document('../../main/webapp/profiler.xmap')"/>
  <xsl:variable name="raw" select="document('../../main/webapp/raw.xmap')"/> 
  <xsl:variable name="resources" select="document('../../main/webapp/resources.xmap')"/> 
  <xsl:variable name="revisions" select="document('../../main/webapp/revisions.xmap')"/>
  <xsl:variable name="search" select="document('../../main/webapp/search.xmap')"/>
	<xsl:variable name="sitemap" select="/"/>
  <xsl:variable name="tabs" select="document('../../main/webapp/tabs.xmap')"/> 
	  
<xsl:template match="/">
  <html>
    <head>
      <title>Sitemap Analysis</title>
    </head>
    <body>
  		<xsl:call-template name="print-transformers"/>
	    <xsl:call-template name="print-generators"/>
      <xsl:call-template name="print-selectors"/>  		
		</body>
  </html>
</xsl:template>
	

	
	
	

	
</xsl:stylesheet>
