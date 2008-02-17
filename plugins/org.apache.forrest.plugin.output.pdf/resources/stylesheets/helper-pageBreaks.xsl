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
  xmlns:fo="http://www.w3.org/1999/XSL/Format" version="1.0">
  <xsl:template name="insertPageBreaks">
    <!-- if marked as a 'pageBreakBefore', and we're breaking on pages, and were not the first node -->
    <xsl:if
      test="contains(@class, 'pageBreakBefore') and preceding-sibling::node()">
      <xsl:attribute name="break-before">page</xsl:attribute>
    </xsl:if>
    
    <!-- if marked as a 'pageBreakAfter', and we're breaking on pages, and were not the last node -->
    <xsl:if
      test="contains(@class, 'pageBreakAfter') and following-sibling::node()">
      <xsl:attribute name="break-after">page</xsl:attribute>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>
