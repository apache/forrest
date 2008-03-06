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
                xmlns:btl="http://xmlns.com/baetle/#"
                exclude-result-prefixes="btl"
                version="1.0">

  <xsl:include href="baetle-to-document.xsl"/>
 
  <xsl:template match="/*/btl:Issue">
    <section id="issueDetails">
      <title>Issue Details</title>
      <table>
        <xsl:apply-templates mode="baetle.issue.summary"/>
      </table>
    </section>
  </xsl:template>

</xsl:stylesheet>
