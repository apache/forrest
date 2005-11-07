<?xml version="1.0"?>
<!--
  Copyright 2002-2005 The Apache Software Foundation or its licensors,
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

<xsl:stylesheet version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:forrest="http://apache.org/forrest/templates/1.0"  
  xmlns:c="http://apache.org/cocoon/include/1.0" >

	<xsl:param name="format"/>
  <xsl:key name="contracts" match="forrest:contract" use="@name" />

  <xsl:template match="/">
    <forrest:properties >
	    <xsl:for-each select="forrest:view[@type=$format]//forrest:contract[count(. | key('contracts', @name)[1]) = 1]">
        <c:include>
          <xsl:attribute name="src">
            <xsl:value-of 
              select="concat('cocoon://prepare.contract-property.', $format,'.', @name)"/>
          </xsl:attribute>
        </c:include>
	    </xsl:for-each>
    </forrest:properties>
  </xsl:template>
  
</xsl:stylesheet>
