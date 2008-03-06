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
                version="1.0">

  <xsl:import href="utils/date.parse-date.template.xsl"/>
  <xsl:import href="utils/date.format-date.template.xsl"/>
  <xsl:include href="param.xsl"/>
  
  <xsl:template match="/">
    <rdf:RDF>
      <xsl:apply-templates select="rss"/>
    </rdf:RDF>
  </xsl:template>

  <xsl:template match="rss[@version='0.92']">
    <btl:Issue>
      <xsl:apply-templates select="*/item"/>
    </btl:Issue>
  </xsl:template>

  <xsl:template match="item">
    <xsl:apply-templates select="link" mode="rdf.about"/>
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="item/link" mode="rdf.about">
    <xsl:attribute name="rdf:about">
      <xsl:value-of select="concat(., $baetle.issue.suffix.uri)"/>
    </xsl:attribute>
  </xsl:template>
 
  <xsl:template match="title">
    <btl:title>
      <xsl:apply-templates/>
    </btl:title>
  </xsl:template>
 
  <xsl:template match="description">
    <btl:description>
      <xsl:apply-templates/>
    </btl:description>
  </xsl:template>
 
  <xsl:template match="summary">
    <btl:summary>
      <xsl:apply-templates/>
    </btl:summary>
  </xsl:template>
 
  <xsl:template match="key">
    <rdfs:label>
      <xsl:apply-templates/>
    </rdfs:label>
  </xsl:template>
 
  <xsl:template match="type">
    <rdf:type>
      <xsl:call-template name="baetle.issue.type">
        <xsl:with-param name="term" select="."/>
      </xsl:call-template>
    </rdf:type>
  </xsl:template>

  <xsl:template match="link">
  <!-- do nothing -->
  </xsl:template>
 
  <xsl:template match="priority">
    <btl:priority>
      <xsl:call-template name="baetle.issue.priority">
        <xsl:with-param name="term" select="."/>
      </xsl:call-template>
    </btl:priority>
  </xsl:template>

  <xsl:template match="status">
    <btl:status>
      <xsl:call-template name="baetle.issue.status">
        <xsl:with-param name="term" select="."/>
      </xsl:call-template>
    </btl:status>
  </xsl:template>

  <xsl:template match="resolution">
    <btl:resolution>
      <xsl:call-template name="baetle.issue.resolution">
        <xsl:with-param name="term" select="."/>
      </xsl:call-template>
    </btl:resolution>
  </xsl:template>

  <xsl:template match="assignee">
    <btl:assigned_to>
      <xsl:choose>
        <xsl:when test="@username = '-1'">
          <xsl:call-template name="baetle.issue.assigned_to">
            <xsl:with-param name="term" select="."/>
          </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
          <foaf:Person>
            <foaf:name>
              <xsl:apply-templates/>
            </foaf:name>
            <!-- Oops -->
            <sioc:name>
              <xsl:value-of select="@username"/>
            </sioc:name>
          </foaf:Person>
        </xsl:otherwise>
      </xsl:choose>
    </btl:assigned_to>
  </xsl:template>
 
  <xsl:template match="reporter">
    <btl:reporter>
      <foaf:Person>
        <foaf:name>
          <xsl:apply-templates/>
        </foaf:name>
        <!-- Oops -->
        <sioc:name>
          <xsl:value-of select="@username"/>
        </sioc:name>
      </foaf:Person>
    </btl:reporter>
  </xsl:template>

  <xsl:template match="created">
    <btl:raised rdf:datatype="{$rdf.datatype.xs.dateTime.uri}">
      <xsl:call-template name="baetle.date.rss2baetle">
        <xsl:with-param name="date" select="."/>
      </xsl:call-template>
    </btl:raised>
  </xsl:template>

  <xsl:template match="updated">
    <btl:updated rdf:datatype="{$rdf.datatype.xs.dateTime.uri}">
      <xsl:call-template name="baetle.date.rss2baetle">
        <xsl:with-param name="date" select="."/>
      </xsl:call-template>
    </btl:updated>
  </xsl:template>
 
  <xsl:template match="votes">
    <btl:votes rdf:datatype="http://www.w3.org/2001/XMLSchema#integer">
      <xsl:apply-templates/>
    </btl:votes>
  </xsl:template>
 
  <xsl:template match="component">
    <btl:about>
      <xsl:attribute name="rdf:resource">
        <xsl:value-of select="concat($baetle.component.prefix.uri, translate(., ' ', '_'))"/>
      </xsl:attribute>
    </btl:about>
  </xsl:template>
 
  <xsl:template match="version">
    <!-- FIXME: property? rdf:resouce -->
    <btl:affects>
      <xsl:apply-templates/>
    </btl:affects>
  </xsl:template>
 
  <xsl:template match="fixVersion">
    <!-- FIXME: property? rdf:resouce -->
    <btl:target_milestone>
      <xsl:apply-templates/>
    </btl:target_milestone>
  </xsl:template>

  <xsl:template match="subtasks">
    <xsl:for-each select="subtask">
      <btl:subtask>
        <xsl:attribute name="rdf:resource">
          <xsl:value-of select="concat($baetle.issue.prefix.uri, .)"/>
        </xsl:attribute>
      </btl:subtask>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="attachments">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="comments">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="comments/comment">
    <sioc:container_of>
      <sioc:Post>
        <xsl:attribute name="rdf:about">
          <xsl:call-template name="baetle.issue.post.uri"/>
        </xsl:attribute>
        <xsl:apply-templates select="@*" mode="sioc.post"/>
        <sioc:content>
          <xsl:apply-templates/>
        </sioc:content>
      </sioc:Post>
    </sioc:container_of>
  </xsl:template>

  <xsl:template match="attachments/attachment">
    <sioc:container_of>
      <sioc:Post>
        <xsl:attribute name="rdf:about">
          <xsl:call-template name="baetle.issue.post.uri"/>
        </xsl:attribute>
        <xsl:call-template name="baetle.issue.attachment.ref"/>
        <xsl:apply-templates select="@*" mode="sioc.post"/>
      </sioc:Post>
    </sioc:container_of>
  </xsl:template>

  <xsl:template name="baetle.issue.attachment.ref">
    <awol:content>
      <awol:Content>
        <awol:src>
          <xsl:attribute name="rdf:resource">
            <xsl:call-template name="baetle.issue.post.uri"/>
          </xsl:attribute>
        </awol:src>
      </awol:Content>
    </awol:content>
  </xsl:template>

  <xsl:template match="attachment/@author|comment/@author" mode="sioc.post">
    <dc:creator>
      <xsl:attribute name="rdf:resource">
        <xsl:value-of select="concat($sioc.user.prefix.uri, .)"/>
      </xsl:attribute>
    </dc:creator>
  </xsl:template>

  <xsl:template match="attachment/@id|attachment/@size" mode="sioc.post">
    <!-- Ignore -->
  </xsl:template>

  <xsl:template match="attachment/@name" mode="sioc.post">
    <dc:title>
      <xsl:value-of select="."/>
    </dc:title>
  </xsl:template>

  <xsl:template match="attachment/@created|comment/@created" mode="sioc.post">
    <dcterms:created>
      <xsl:call-template name="baetle.date.rss2baetle">
        <xsl:with-param name="date" select="."/>
      </xsl:call-template>
    </dcterms:created>
  </xsl:template>

  <xsl:template name="baetle.issue.post.uri">
    <!-- FIXME: URI scheme should be defined by a pattern -->
    <xsl:choose>
      <xsl:when test="parent::comments">
        <xsl:value-of 
           select="concat($baetle.issue.comment.prefix.uri, '#')"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of 
         select="concat($baetle.issue.attachment.prefix.uri, @id, '/', @name)"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="baetle.issue.type">
    <xsl:param name="term"/>
    <xsl:variable name="stdTerm">
      <xsl:choose>
        <xsl:when test="$term = 'Improvement'">Enhancement</xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$term"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:attribute name="rdf:resource">
      <xsl:value-of select="concat($baetle.ns.uri, $stdTerm)"/>
    </xsl:attribute>
  </xsl:template>

  <xsl:template name="baetle.issue.priority">
    <xsl:param name="term"/>
    <xsl:variable name="stdTerm">
      <xsl:choose>
        <xsl:when test="$term = 'Minor'">Minor</xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$term"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:attribute name="rdf:resource">
      <xsl:value-of select="concat($baetle.ns.uri, $stdTerm)"/>
    </xsl:attribute>
  </xsl:template>

  <xsl:template name="baetle.issue.status">
    <xsl:param name="term"/>
    <xsl:variable name="stdTerm">
      <xsl:choose>
        <xsl:when test="$term = 'OPEN'">Open</xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$term"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:attribute name="rdf:resource">
      <xsl:value-of select="concat($baetle.ns.uri, $stdTerm)"/>
    </xsl:attribute>
  </xsl:template>

  <xsl:template name="baetle.issue.resolution">
    <xsl:param name="term"/>
    <xsl:variable name="stdTerm">
      <xsl:choose>
        <xsl:when test="$term = 'FIXED'">Fixed</xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$term"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:attribute name="rdf:resource">
      <xsl:value-of select="concat($baetle.ns.uri, $stdTerm)"/>
    </xsl:attribute>
  </xsl:template>

  <xsl:template name="baetle.issue.assigned_to">
    <xsl:param name="term"/>
    <xsl:variable name="stdTerm">
      <xsl:choose>
        <xsl:when test="$term = 'UNASSIGNED'">Unassigned</xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$term"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:attribute name="rdf:resource">
      <xsl:value-of select="concat($baetle.ns.uri, $stdTerm)"/>
    </xsl:attribute>
  </xsl:template>

  <xsl:template name="baetle.date.rss2baetle">
    <xsl:param name="date"/>
    <!-- FIXME: debug the exsl date-time module to further parse 'Z' as well -->
    <xsl:variable name="iso-8601-dateTime">
      <xsl:call-template name="date:parse-date">
        <xsl:with-param name="date-time" select="substring($date, 1, 31)"/>
        <xsl:with-param name="format" select="'EEE, dd MMM yyyy HH:mm:ss'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:value-of select="$iso-8601-dateTime"/>
    <xsl:call-template name="date:format-date">
      <xsl:with-param name="date-time" select="$iso-8601-dateTime"/>
      <xsl:with-param name="format" select="'yyyy-MM-ddTHH:mm:ss'"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="customfields|issuelinks">
    <!-- Ignore -->
  </xsl:template>

</xsl:stylesheet>
