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
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  xmlns:foaf="http://xmlns.com/foaf/0.1/"
  exclude-result-prefixes="rdf dc foaf">

 <xsl:template match="/">
  <xsl:apply-templates select="rdf:RDF/foaf:Person"/>
 </xsl:template>

<xsl:template match="foaf:Person">
     <xsl:call-template name="person" />
</xsl:template>

<xsl:template name="person">
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
  <div class="content">  
 
    <h1>FOAF Metadata of <xsl:value-of select="foaf:name"/> </h1>
    <p><img src="{./foaf:depiction/@rdf:resource}" alt="Photograph" height="120" border="0" /></p>
    <p>
    <table>
      <thead align="left">
      <tr><th>Property</th><th>Value</th></tr>
		</thead>
      <tbody>
      <tr><td>Name</td><td>
      <xsl:choose >
      	<xsl:when test="foaf:name">
      		<xsl:value-of select="foaf:name"/>
      	</xsl:when>
         <xsl:otherwise>
         	<xsl:value-of select="foaf:firstName"/> <xsl:text>  </xsl:text><xsl:value-of select="foaf:surname"/> 
      	</xsl:otherwise>   
      </xsl:choose></td></tr>
      <tr><td>Id</td><td><a href="{./@rdf:ID}"><xsl:value-of select="./@rdf:ID"/></a></td></tr>
      <tr><td>Nick</td><td><xsl:value-of select="foaf:nick"/></td></tr>
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
      <tr><td>Phone</td><td><a href="{foaf:phone/@rdf:resource}"><xsl:value-of select="foaf:phone/@rdf:resource"/></a></td></tr>
      <tr><td>Homepage</td><td><a href="{foaf:homepage/@rdf:resource}"><xsl:value-of select="foaf:homepage/@rdf:resource"/></a></td></tr>
     <tr><td>Work Place Homepage</td><td><a href="{foaf:workplaceHomepage/@rdf:resource}"><xsl:value-of select="foaf:workplaceHomepage/@rdf:resource"/></a></td></tr>
     <tr><td>School Homepage</td><td><a href="{foaf:schoolHomepage/@rdf:resource}"><xsl:value-of select="foaf:schoolHomepage/@rdf:resource"/></a></td></tr>
     </tbody>
    </table>

    </p>
    
    <br /><br /><br />

    <h2>Knows</h2>
    <p>
    <table border="1">
     <thead align="left">
      <tr><th>ID</th><th>Name</th><th>Project Page</th><th>Mail</th><th>Link to FOAF</th></tr>
     </thead>
     <tbody>
      <xsl:apply-templates select="foaf:knows"/>
     </tbody>
    </table>
    </p>

    <br /><br /><br />

    <p>
    </p>
    </div>
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
	<td><a href="{foaf:id/@rdf:resource}"><xsl:value-of select="foaf:id/@rdf:resource" /></a></td>
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
   <td><a href="rdfs:seeAlso/@rdf:resource"><xsl:value-of select="rdfs:seeAlso/@rdf:resource" /></a></td>

   </tr>
  </xsl:for-each>
</xsl:template> 

</xsl:stylesheet>    
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
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  xmlns:foaf="http://xmlns.com/foaf/0.1/"
  exclude-result-prefixes="rdf dc foaf">

 <xsl:template match="/">
  <xsl:apply-templates select="rdf:RDF/foaf:Person"/>
 </xsl:template>

<xsl:template match="foaf:Person">
     <xsl:call-template name="person" />
</xsl:template>

<xsl:template name="person">
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
  <div class="content">  
 
    <h1>FOAF Metadata of <xsl:value-of select="foaf:name"/> </h1>
    <p><img src="{./foaf:depiction/@rdf:resource}" alt="Photograph" height="120" border="0" /></p>
    <p>
    <table>
      <thead align="left">
      <tr><th>Property</th><th>Value</th></tr>
		</thead>
      <tbody>
      <tr><td>Name</td><td>
      <xsl:choose >
      	<xsl:when test="foaf:name">
      		<xsl:value-of select="foaf:name"/>
      	</xsl:when>
         <xsl:otherwise>
         	<xsl:value-of select="foaf:firstName"/> <xsl:text>  </xsl:text><xsl:value-of select="foaf:surname"/> 
      	</xsl:otherwise>   
      </xsl:choose></td></tr>
      <tr><td>Id</td><td><a href="{./@rdf:ID}"><xsl:value-of select="./@rdf:ID"/></a></td></tr>
      <tr><td>Nick</td><td><xsl:value-of select="foaf:nick"/></td></tr>
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
      <tr><td>Phone</td><td><a href="{foaf:phone/@rdf:resource}"><xsl:value-of select="foaf:phone/@rdf:resource"/></a></td></tr>
      <tr><td>Homepage</td><td><a href="{foaf:homepage/@rdf:resource}"><xsl:value-of select="foaf:homepage/@rdf:resource"/></a></td></tr>
     <tr><td>Work Place Homepage</td><td><a href="{foaf:workplaceHomepage/@rdf:resource}"><xsl:value-of select="foaf:workplaceHomepage/@rdf:resource"/></a></td></tr>
     <tr><td>School Homepage</td><td><a href="{foaf:schoolHomepage/@rdf:resource}"><xsl:value-of select="foaf:schoolHomepage/@rdf:resource"/></a></td></tr>
     </tbody>
    </table>

    </p>
    
    <br /><br /><br />

    <h2>Knows</h2>
    <p>
    <table border="1">
     <thead align="left">
      <tr><th>ID</th><th>Name</th><th>Project Page</th><th>Mail</th><th>Link to FOAF</th></tr>
     </thead>
     <tbody>
      <xsl:apply-templates select="foaf:knows"/>
     </tbody>
    </table>
    </p>

    <br /><br /><br />

    <p>
    </p>
    </div>
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
	<td><a href="{foaf:id/@rdf:resource}"><xsl:value-of select="foaf:id/@rdf:resource" /></a></td>
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
   <td><a href="rdfs:seeAlso/@rdf:resource"><xsl:value-of select="rdfs:seeAlso/@rdf:resource" /></a></td>

   </tr>
  </xsl:for-each>
</xsl:template> 

</xsl:stylesheet>    