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
  <!-- Determine page height for various page sizes (US Letter portrait
  is the default) -->
  <!-- FIXME: JJP:would this be better of a file? -->
  <xsl:variable name="pageheight">
    <xsl:choose>
      <xsl:when test="$pageorientation = 'landscape'">
        <xsl:choose>
          <xsl:when test="$pagesize = 'a0'">841mm</xsl:when>
          <xsl:when test="$pagesize = 'a1'">594mm</xsl:when>
          <xsl:when test="$pagesize = 'a2'">420mm</xsl:when>
          <xsl:when test="$pagesize = 'a3'">297mm</xsl:when>
          <xsl:when test="$pagesize = 'a4'">210mm</xsl:when>
          <xsl:when test="$pagesize = 'a5'">148mm</xsl:when>
          <xsl:when test="$pagesize = 'executive'">7.25in</xsl:when>
          <xsl:when test="$pagesize = 'folio'">8.5in</xsl:when>
          <xsl:when test="$pagesize = 'ledger'">11in</xsl:when>
          <xsl:when test="$pagesize = 'legal'">8.5in</xsl:when>
          <xsl:when test="$pagesize = 'letter'">8.5in</xsl:when>
          <xsl:when test="$pagesize = 'quarto'">8.5in</xsl:when>
          <xsl:when test="$pagesize = 'tabloid'">11in</xsl:when>
          <xsl:otherwise>8.5in</xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:otherwise>
        <xsl:choose>
          <xsl:when test="$pagesize = 'a0'">1189mm</xsl:when>
          <xsl:when test="$pagesize = 'a1'">841mm</xsl:when>
          <xsl:when test="$pagesize = 'a2'">594mm</xsl:when>
          <xsl:when test="$pagesize = 'a3'">420mm</xsl:when>
          <xsl:when test="$pagesize = 'a4'">297mm</xsl:when>
          <xsl:when test="$pagesize = 'a5'">210mm</xsl:when>
          <xsl:when test="$pagesize = 'executive'">10.5in</xsl:when>
          <xsl:when test="$pagesize = 'folio'">13in</xsl:when>
          <xsl:when test="$pagesize = 'ledger'">17in</xsl:when>
          <xsl:when test="$pagesize = 'legal'">14in</xsl:when>
          <xsl:when test="$pagesize = 'quarto'">10.83in</xsl:when>
          <xsl:when test="$pagesize = 'tabloid'">17in</xsl:when>
          <xsl:otherwise>11in</xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <!-- Determine page width for various page sizes (US Letter portrait
  is the default) -->
  <xsl:variable name="pagewidth">
    <xsl:choose>
      <xsl:when test="$pageorientation = 'landscape'">
        <xsl:choose>
          <xsl:when test="$pagesize = 'a0'">1189mm</xsl:when>
          <xsl:when test="$pagesize = 'a1'">841mm</xsl:when>
          <xsl:when test="$pagesize = 'a2'">594mm</xsl:when>
          <xsl:when test="$pagesize = 'a3'">420mm</xsl:when>
          <xsl:when test="$pagesize = 'a4'">297mm</xsl:when>
          <xsl:when test="$pagesize = 'a5'">210mm</xsl:when>
          <xsl:when test="$pagesize = 'executive'">10.5in</xsl:when>
          <xsl:when test="$pagesize = 'folio'">13in</xsl:when>
          <xsl:when test="$pagesize = 'ledger'">17in</xsl:when>
          <xsl:when test="$pagesize = 'legal'">14in</xsl:when>
          <xsl:when test="$pagesize = 'quarto'">10.83in</xsl:when>
          <xsl:when test="$pagesize = 'tabloid'">17in</xsl:when>
          <xsl:otherwise>11in</xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:otherwise>
        <xsl:choose>
          <xsl:when test="$pagesize = 'a0'">841mm</xsl:when>
          <xsl:when test="$pagesize = 'a1'">594mm</xsl:when>
          <xsl:when test="$pagesize = 'a2'">420mm</xsl:when>
          <xsl:when test="$pagesize = 'a3'">297mm</xsl:when>
          <xsl:when test="$pagesize = 'a4'">210mm</xsl:when>
          <xsl:when test="$pagesize = 'a5'">148mm</xsl:when>
          <xsl:when test="$pagesize = 'executive'">7.25in</xsl:when>
          <xsl:when test="$pagesize = 'folio'">8.5in</xsl:when>
          <xsl:when test="$pagesize = 'ledger'">11in</xsl:when>
          <xsl:when test="$pagesize = 'legal'">8.5in</xsl:when>
          <xsl:when test="$pagesize = 'letter'">8.5in</xsl:when>
          <xsl:when test="$pagesize = 'quarto'">8.5in</xsl:when>
          <xsl:when test="$pagesize = 'tabloid'">11in</xsl:when>
          <xsl:otherwise>8.5in</xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
</xsl:stylesheet>
