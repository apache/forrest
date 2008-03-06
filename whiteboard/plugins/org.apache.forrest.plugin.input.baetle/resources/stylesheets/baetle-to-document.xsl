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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" 
                xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:foaf="http://xmlns.com/foaf/0.1/"
                xmlns:btl="http://xmlns.com/baetle/#"
                xmlns:awol="http://bblfish.net/work/atom-owl/2006-06-06/#"
                xmlns:dcterms="http://purl.org/dc/terms/"
                xmlns:sioc="http://rdfs.org/sioc/ns#"
                xmlns:date="http://exslt.org/dates-and-times"
                extension-element-prefixes="date"
                exclude-result-prefixes="awol dcterms dc foaf btl date sioc
                                         rdf rdfs"
                version="1.0">

  <xsl:import href="utils/date.format-date.template.xsl"/>

  <xsl:include href="param.xsl"/>
 
  <xsl:template match="/">
    <xsl:apply-templates select="rdf:RDF/btl:Issue"/>
  </xsl:template>

  <xsl:template match="btl:Issue">
    <document>
      <xsl:call-template name="baetle.issue.header"/>
      <xsl:call-template name="baetle.issue.body"/>
    </document>
  </xsl:template>

  <xsl:template name="baetle.issue.header">
    <header>
      <xsl:call-template name="baetle.issue.title"/>
    </header> 
  </xsl:template>

  <xsl:template name="baetle.issue.title">
    <xsl:apply-templates select="btl:title|btl:subtitle"/>
  </xsl:template>

  <xsl:template match="btl:title">
    <title>
      <xsl:apply-templates/>
    </title>
  </xsl:template>

  <xsl:template match="btl:summary">
    <subtitle>
      <xsl:apply-templates/>
    </subtitle>
  </xsl:template>

  <xsl:template name="baetle.issue.body">
    <body>
      <xsl:apply-templates select="btl:description"/>
      <xsl:if test="sioc:container_of/sioc:Post/sioc:content">
        <section id="issue-comments">
          <!-- FIXME: il8n ? -->
          <title>Comments</title>
          <xsl:apply-templates select="sioc:container_of/sioc:Post[sioc:content]"/>
        </section>
      </xsl:if>
      <xsl:if test="sioc:container_of/sioc:Post/awol:content">
        <section id="issue-attachments">
          <!-- FIXME: il8n ? -->
          <title>Attachments</title>
          <xsl:apply-templates select="sioc:container_of/sioc:Post[awol:content]"/>
        </section>
      </xsl:if>
    </body>
  </xsl:template>

  <xsl:template name="baetle.issue.summary">
    <section id="issueSummary">
      <title>Summary</title>
      <table>
        <xsl:call-template name="baetle.issue.component"/>
        <xsl:call-template name="baetle.issue.affectsVersion"/>
        <xsl:call-template name="baetle.issue.fixVersion"/>
        <xsl:apply-templates mode="baetle.issue.summary"/>
      </table>
    </section>
  </xsl:template>

  <xsl:template match="rdfs:label" mode="baetle.issue.summary">
    <tr>
      <xsl:call-template name="baetle.issue.node.class.attrib"/>
      <xsl:call-template name="baetle.issue.summary.label"/>
      <td class="value"><p>
        <xsl:value-of select="."/>
      </p></td>
    </tr>
  </xsl:template>

  <xsl:template match="rdf:type" mode="baetle.issue.summary">
    <tr>
      <xsl:call-template name="baetle.issue.node.class.attrib"/>
      <xsl:call-template name="baetle.issue.summary.label"/>
      <td class="value"><p>
        <xsl:call-template name="baetle.issue.resource.resolve"/>
      </p></td>
    </tr>
  </xsl:template>

  <xsl:template match="btl:priority" mode="baetle.issue.summary">
    <tr>
      <xsl:call-template name="baetle.issue.node.class.attrib"/>
      <xsl:call-template name="baetle.issue.summary.label"/>
      <td class="value"><p>
        <xsl:call-template name="baetle.issue.resource.resolve"/>
      </p></td>
    </tr>
  </xsl:template>

  <xsl:template match="btl:status" mode="baetle.issue.summary">
    <tr>
      <xsl:call-template name="baetle.issue.node.class.attrib"/>
      <xsl:call-template name="baetle.issue.summary.label"/>
      <td class="value"><p>
        <xsl:call-template name="baetle.issue.resource.resolve"/>
      </p></td>
    </tr>
  </xsl:template>

  <xsl:template match="btl:resolution" mode="baetle.issue.summary">
    <tr>
      <xsl:call-template name="baetle.issue.node.class.attrib"/>
      <xsl:call-template name="baetle.issue.summary.label"/>
      <td class="value"><p>
        <xsl:call-template name="baetle.issue.resource.resolve"/>
      </p></td>
    </tr>
  </xsl:template>

  <xsl:template match="btl:votes" mode="baetle.issue.summary">
    <tr>
      <xsl:call-template name="baetle.issue.node.class.attrib"/>
      <xsl:call-template name="baetle.issue.summary.label"/>
      <td class="value"><p>
        <xsl:value-of select="."/>
      </p></td>
    </tr>
  </xsl:template>

  <xsl:template match="btl:reporter" mode="baetle.issue.summary">
    <tr>
      <xsl:call-template name="baetle.issue.node.class.attrib"/>
      <xsl:call-template name="baetle.issue.summary.label"/>
      <td class="value"><p>
        <xsl:choose>
          <xsl:when test="foaf:Person">
            <xsl:apply-templates mode="baetle.issue.summary"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:call-template name="baetle.issue.resource.resolve"/>
          </xsl:otherwise>
        </xsl:choose>
      </p></td>
    </tr>
  </xsl:template>

  <xsl:template match="btl:assigned_to" mode="baetle.issue.summary">
    <tr>
      <xsl:call-template name="baetle.issue.node.class.attrib"/>
      <xsl:call-template name="baetle.issue.summary.label"/>
      <td class="value"><p>
        <xsl:choose>
          <xsl:when test="foaf:Person">
            <xsl:apply-templates mode="baetle.issue.summary"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:call-template name="baetle.issue.resource.resolve"/>
          </xsl:otherwise>
        </xsl:choose>
      </p></td>
    </tr>
  </xsl:template>

  <xsl:template match="foaf:Person" mode="baetle.issue.summary">
    <xsl:if test="$baetle.issue.person.link != '0'">
      <a href="{concat($sioc.user.prefix.uri, sioc:name/text())}" 
         title="{foaf:name/text()}">
        <xsl:value-of select="foaf:name"/>
      </a>
    </xsl:if>
  </xsl:template>

  <xsl:template match="btl:raised" mode="baetle.issue.summary">
    <tr>
      <xsl:call-template name="baetle.issue.node.class.attrib"/>
      <xsl:call-template name="baetle.issue.summary.label"/>
      <td class="value"><p>
        <xsl:call-template name="baetle.issue.date.format">
          <xsl:with-param name="date" select="."/>
        </xsl:call-template>
      </p></td>
    </tr>
  </xsl:template>

  <xsl:template match="btl:updated" mode="baetle.issue.summary">
    <tr>
      <xsl:call-template name="baetle.issue.node.class.attrib"/>
      <xsl:call-template name="baetle.issue.summary.label"/>
      <td class="value"><p>
        <xsl:call-template name="baetle.issue.date.format">
          <xsl:with-param name="date" select="."/>
        </xsl:call-template>
      </p></td>
    </tr>
  </xsl:template>

  <xsl:template match="btl:summary" mode="baetle.issue.summary">
    <p>
      <xsl:apply-templates/>
    </p>
  </xsl:template>

  <xsl:template match="btl:description|sioc:container_of|btl:title|btl:about|btl:target_milestone|btl:affects" 
                mode="baetle.issue.summary">
    <!-- Ignore -->
  </xsl:template>

  <xsl:template match="btl:description">
    <section>
      <xsl:call-template name="baetle.issue.node.class.attrib"/>
      <!-- FIXME: il8n ? -->
      <title>Description</title>
      <p>
        <xsl:value-of select="." disable-output-escaping="yes"/>
      </p>
    </section>
  </xsl:template>

  <xsl:template match="sioc:content">
    <p>
      <xsl:call-template name="baetle.issue.node.class.attrib"/>
      <xsl:apply-templates/>
    </p>
  </xsl:template>

  <xsl:template match="sioc:Post[sioc:content]">
    <xsl:variable name="author" select="substring-after(dc:creator/@rdf:resource, $sioc.user.prefix.uri)"/>
    <dl>
      <xsl:call-template name="baetle.issue.node.class.attrib"/>
      <dt>
        <a href="{dc:creator/@rdf:resource}" title="{$author}">
          <xsl:value-of select="$author"/>
        </a>
        <xsl:text> - </xsl:text>
        <xsl:call-template name="baetle.issue.date.format">
          <xsl:with-param name="date" select="dcterms:created"/>
        </xsl:call-template>
      </dt>
      <dd>
        <xsl:apply-templates select="sioc:content"/>
      </dd>
    </dl>
  </xsl:template>

  <xsl:template name="baetle.issue.component">
    <xsl:if test="btl:about">
      <tr>
        <xsl:call-template name="baetle.issue.node.class.attrib">
          <xsl:with-param name="node" select="btl:about[1]"/>
        </xsl:call-template>
        <xsl:call-template name="baetle.issue.summary.label">
          <xsl:with-param name="node" select="btl:about[1]"/>
        </xsl:call-template>
        <td class="value"><p>
        <xsl:for-each select="btl:about">
          <xsl:value-of select="translate(substring-after(@rdf:resource, $baetle.component.prefix.uri), '_', ' ')"/>
          <xsl:if test="position() != last()">
            <xsl:text>, </xsl:text>
          </xsl:if>
        </xsl:for-each>
        </p></td>
      </tr>
    </xsl:if>
  </xsl:template>

  <xsl:template name="baetle.issue.fixVersion">
    <xsl:if test="btl:target_milestone">
      <tr>
        <xsl:call-template name="baetle.issue.node.class.attrib">
          <xsl:with-param name="node" select="btl:target_milestone[1]"/>
        </xsl:call-template>
        <xsl:call-template name="baetle.issue.summary.label">
          <xsl:with-param name="node" select="btl:target_milestone[1]"/>
        </xsl:call-template>
        <td class="value"><p>
          <xsl:for-each select="btl:target_milestone">
            <xsl:value-of select="."/>
            <xsl:if test="position() != last()">
              <xsl:text>, </xsl:text>
            </xsl:if>
          </xsl:for-each>
        </p></td>
      </tr>
    </xsl:if>
  </xsl:template>

  <xsl:template name="baetle.issue.affectsVersion">
    <xsl:if test="btl:affects">
      <tr>
        <xsl:call-template name="baetle.issue.node.class.attrib">
          <xsl:with-param name="node" select="btl:affects[1]"/>
        </xsl:call-template>
        <xsl:call-template name="baetle.issue.summary.label">
          <xsl:with-param name="node" select="btl:affects[1]"/>
        </xsl:call-template>
        <td class="value"><p>
        <xsl:for-each select="btl:affects">
          <xsl:value-of select="."/>
          <xsl:if test="position() != last()">
            <xsl:text>, </xsl:text>
          </xsl:if>
        </xsl:for-each>
        </p></td>
      </tr>
    </xsl:if>
  </xsl:template>

  <xsl:template name="baetle.issue.node.class.attrib">
    <xsl:param name="node" select="."/>
    <xsl:attribute name="class">
      <xsl:value-of select="local-name($node)"/>
    </xsl:attribute>
  </xsl:template>

  <!-- This template assigns an appropriate label to a given property.
       Will be replaced by il8n though. 
       Translation is preferred to be explicit -->
  <xsl:template name="baetle.issue.summary.label">
    <xsl:param name="node" select="."/>
    <xsl:variable name="name" select="local-name($node)"/>
    <td class="label" width="25%">
      <p><strong>
        <xsl:choose>
          <xsl:when test="$name = 'label'">Label</xsl:when>
          <xsl:when test="$name = 'type'">Type</xsl:when>
          <xsl:when test="$name = 'priority'">Priority</xsl:when>
          <xsl:when test="$name = 'status'">Status</xsl:when>
          <xsl:when test="$name = 'resolution'">Resolution</xsl:when>
          <xsl:when test="$name = 'assigned_to'">Assignee</xsl:when>
          <xsl:when test="$name = 'reporter'">Reporter</xsl:when>
          <xsl:when test="$name = 'raised'">Issue Raised</xsl:when>
          <xsl:when test="$name = 'updated'">Last Updated</xsl:when>
          <xsl:when test="$name = 'affects'">Affected Version(s)</xsl:when>
          <xsl:when test="$name = 'target_milestone'">Fix Version(s)</xsl:when>
          <xsl:when test="$name = 'about'">Component</xsl:when>
          <xsl:when test="$name = 'votes'">Votes</xsl:when>
          <xsl:when test="$name = 'summary'">Summary</xsl:when>
          <xsl:when test="$name = 'title'">Title</xsl:when>
          <xsl:otherwise>Unknown Property</xsl:otherwise>
        </xsl:choose>
        <xsl:text>: </xsl:text>
      </strong></p>
    </td>
  </xsl:template>

  <xsl:template name="baetle.issue.resource.resolve">
    <xsl:if test="@rdf:resource">
      <xsl:variable name="value" select="substring-after(@rdf:resource, 
                                         $baetle.ns.uri)"/>
      <xsl:choose>
        <xsl:when test="$value = 'Bug'">Bug</xsl:when>
        <xsl:when test="$value = 'Fixed'">Fixed</xsl:when>
        <xsl:when test="$value = 'Minor'">Minor</xsl:when>
        <xsl:when test="$value = 'Major'">Major</xsl:when>
        <xsl:when test="$value = 'Unresolved'">Unresolved</xsl:when>
        <xsl:when test="$value = 'Unassigned'">Unassigned</xsl:when>
        <xsl:when test="$value = 'Open'">Open</xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$value"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:if>
  </xsl:template>

  <xsl:template name="baetle.issue.date.format">
    <xsl:param name="date"/>
    <!--
    <xsl:call-template name="date:format-date">
      <xsl:with-param name="date-time" select="$date"/>
      <xsl:with-param name="format" select="'EEE, dd MMM yyyy hh:mm'"/>
    </xsl:call-template>
    -->
    <xsl:value-of select="$date"/>
  </xsl:template>

</xsl:stylesheet>
