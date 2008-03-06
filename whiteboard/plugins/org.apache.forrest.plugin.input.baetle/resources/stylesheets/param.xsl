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
                version="1.0">

  <xsl:param name="baetle.ns.uri" select="'http://xmlns.com/baetle/#'"/>
  <xsl:param name="baetle.issue.suffix.uri" select="'#issue'"/>
  <xsl:param name="rdf.datatype.xs.dateTime.uri" 
       select="'http://www.w3.org/2001/XMLSchema#'"/>
  <xsl:param name="baetle.issue.prefix.uri" 
       select="'https://issues.apache.org/jira/browse/'"/>
  <xsl:param name="baetle.issue.attachment.prefix.uri"
       select="'https://issues.apache.org/jira/secure/attachment/'"/>
  <xsl:param name="baetle.issue.comment.prefix.uri"
       select="'https://issues.apache.org/jira/browse/'"/>
  <xsl:param name="baetle.component.prefix.uri" 
       select="'https://issues.apache.org/jira/browse/FOR/component/'"/>
  <xsl:param name="sioc.user.prefix.uri" 
       select="'https://issues.apache.org/jira/secure/ViewProfile.jspa?name='"/>

  <xsl:param name="baetle.issue.person.link" select="'1'"/>
</xsl:stylesheet>
