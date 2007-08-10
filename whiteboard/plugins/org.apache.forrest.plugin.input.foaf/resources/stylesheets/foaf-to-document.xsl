<?xml version="1.0"?>
<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
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
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
  xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  xmlns:foaf="http://xmlns.com/foaf/0.1/"
  exclude-result-prefixes="rdf dc foaf">

 <xsl:template match="/">
  <xsl:apply-templates select="rdf:RDF/foaf:Person"/>
 </xsl:template>

<xsl:template match="foaf:Person">
    <document>
      <xsl:call-template name="header" />
      <xsl:call-template name="body" />
    </document>
</xsl:template>

<xsl:template name="header">
    <header>
      <title>Information about <xsl:value-of select="foaf:name"/></title>
    </header>
</xsl:template>

<xsl:template name="body">
<body>
  <section>
   <title>FOAF Metadata of <xsl:value-of select="foaf:name"/> </title>
   <xsl:choose>
   <xsl:when test="foaf:depiction/@rdf:resource">
    <p><img src="{foaf:depiction/@rdf:resource}" height="120" border="0" /></p>
    </xsl:when>
    </xsl:choose>
    <p>
    <table>
      <tr><th>Property</th><th>Value</th></tr>
	  <tr><td>Name</td><td>
      <xsl:choose >
      	<xsl:when test="foaf:name">
      		<xsl:value-of select="foaf:name"/>
      	</xsl:when>
         <xsl:otherwise>
         	<xsl:value-of select="foaf:firstName"/> <xsl:text>  </xsl:text><xsl:value-of select="foaf:surname"/> 
      	</xsl:otherwise>   
      </xsl:choose></td></tr>
      <xsl:choose>
      <xsl:when test="./@rdf:ID">
      	<tr><td>Id</td><td><a href="{./@rdf:ID}"><xsl:value-of select="./@rdf:ID"/></a></td></tr>
      </xsl:when>
      </xsl:choose>
      <xsl:choose>
      <xsl:when test="foaf:nick">
      	<tr><td>Nick</td><td><xsl:value-of select="foaf:nick"/></td></tr>
      </xsl:when>
      </xsl:choose>
      <tr><td>Mbox</td>
      <xsl:choose>
      	<xsl:when test="foaf:mbox_sha1sum">
      		<td><xsl:value-of select="foaf:mbox_sha1sum"/></td>
      	</xsl:when>
      	<xsl:otherwise>
      		<td><a href="mailto:{foaf:mbox/@rdf:resource}"><xsl:value-of select="foaf:mbox/@rdf:resource"/></a></td>
      	</xsl:otherwise>
      	</xsl:choose>
      	</tr>
      <xsl:choose>
      <xsl:when test="foaf:phone/@rdf:resource">
      	<tr><td>Phone</td><td><a href="{foaf:phone/@rdf:resource}"><xsl:value-of select="foaf:phone/@rdf:resource"/></a></td></tr>
      </xsl:when>
      </xsl:choose>
      <xsl:choose>
      <xsl:when test="foaf:homepage/@rdf:resource">
      	<tr><td>Homepage</td><td><a href="{foaf:homepage/@rdf:resource}"><xsl:value-of select="foaf:homepage/@rdf:resource"/></a></td></tr>
     </xsl:when>
     </xsl:choose>
     <xsl:choose>
     <xsl:when test="foaf:workplaceHomepage/@rdf:resource">
     	<tr><td>Work Place Homepage</td><td><a href="{foaf:workplaceHomepage/@rdf:resource}"><xsl:value-of select="foaf:workplaceHomepage/@rdf:resource"/></a></td></tr>
     </xsl:when>
     </xsl:choose>
     <xsl:choose>
     <xsl:when test="foaf:schoolHomepage/@rdf:resource">
     	<tr><td>School Homepage</td><td><a href="{foaf:schoolHomepage/@rdf:resource}"><xsl:value-of select="foaf:schoolHomepage/@rdf:resource"/></a></td></tr>
    </xsl:when>
    </xsl:choose>
    </table>
    </p>
    </section>
    
    <br/>
    
    <section>
    <title>Friends</title>
    <p>
    <table>
      <tr><th>ID</th><th>Name</th><th>Mail</th></tr>
      <xsl:apply-templates select="foaf:knows"/>
     </table>
    </p>
    </section>

    </body>
 </xsl:template>

 <xsl:template match="foaf:*">
 <section>
  <tr><td><xsl:value-of select="local-name()"/></td><td><xsl:value-of select="."/></td></tr>
 </section>
 </xsl:template>

 <xsl:template match="foaf:currentProject">
  <section>
  <tr>	<td><xsl:value-of select="./@dc:title"/></td>
  			<td><a href="{./@rdf:resource}"><xsl:value-of select="./@rdf:resource" /></a></td>
  			<td><xsl:value-of select="./@rdfs:comment" /></td>
  </tr>
 </section></xsl:template> 

 <xsl:template match="foaf:publications">
  <section>
  <tr>
		<td><xsl:value-of select="./@dc:title"/></td>
  		<td><a href="{./@rdf:resource}"><xsl:value-of select="./@rdf:resource" /></a></td>
  		<td><xsl:value-of select="./@dc:date" /></td>
  </tr>
 </section>
 </xsl:template> 

 <xsl:template match="foaf:knows">
  <xsl:for-each select="foaf:Person">
   <tr>
   <td><xsl:value-of select="./@rdf:ID" /></td>
	<td>
	<xsl:choose >
     	<xsl:when test="foaf:name">
     		<xsl:value-of select="foaf:name"/>
     	</xsl:when>
        <xsl:otherwise>
        	<xsl:value-of select="foaf:firstName"/><xsl:text>  </xsl:text> <xsl:value-of select="foaf:surname"/> 
     	</xsl:otherwise>   
     </xsl:choose>
     </td>
   <td>
   <xsl:choose>
   	<xsl:when test="foaf:mbox_sha1sum">
   		<xsl:value-of select="foaf:mbox_sha1sum"/>
   	</xsl:when>
   	<xsl:otherwise>
   		<a href="{foaf:mbox/@rdf:resource}"><xsl:value-of select="foaf:mbox/@rdf:resource" /></a>
   	</xsl:otherwise>
   </xsl:choose>
   </td>
   </tr>
  </xsl:for-each>
</xsl:template> 

</xsl:stylesheet>        