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
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:resume="http://xmlresume.sourceforge.net/resume/0.0"
    version="1.0">
  <xsl:template match="/">
    <xsl:apply-templates select="resume:resume"/>
  </xsl:template>
  <xsl:template match="resume:resume">
    <document>
      <xsl:apply-templates select="resume:header"/>
      <body>
        <xsl:apply-templates select="resume:header/resume:address"/>
        <xsl:apply-templates select="resume:header/resume:contact"/>
        <xsl:apply-templates select="*[name(.) != 'header']"/>
      </body>
    </document>
  </xsl:template>
  <xsl:template match="resume:address">
    <div id="resume-address">
      <section>
        <title>Address</title>
        <p>
          <xsl:value-of select="resume:street"/>
          <br/>
          <xsl:value-of select="resume:suburb"/>
          <br/>
          <xsl:value-of select="resume:city"/>
          <br/>
          <xsl:value-of select="resume:postalCode"/>
          <br/>
          <xsl:value-of select="resume:country"/>
          <br/>
        </p>
      </section>
    </div>
  </xsl:template>
  <xsl:template match="resume:contact">
    <div id="resume-contact">
      <section>
        <title>Contact Details</title>
        <p>
          <xsl:apply-templates select="resume:phone"/>
          <xsl:apply-templates select="resume:email"/>
          <xsl:apply-templates select="resume:instantMessage"/>
        </p>
      </section>
    </div>
  </xsl:template>
  <xsl:template match="resume:awards">
    <div id="resume-awards">
      <section>
        <xsl:apply-templates/>
      </section>
    </div>
  </xsl:template>
  <xsl:template match="resume:award">
    <div class="resume-award">
      <section>
        <title><xsl:value-of select="resume:title"/></title>
        <p>
          Awarding by
          <xsl:value-of select="resume:organization"/>
          (
          <xsl:value-of select="resume:period"/>
          ).
        </p>
        <p>
          Description:
          <xsl:value-of select="resume:description"/>
        </p>
      </section>
    </div>
  </xsl:template>
  <xsl:template match="resume:pubs">
    <div id="resume-pubs">
      <section>
        <title>Publications</title>
        <xsl:apply-templates/>
      </section>
    </div>
  </xsl:template>
  <xsl:template match="resume:pub">
    <li class="resume-pub"><xsl:value-of select="resume:artTitle"/>
      <xsl:value-of select="resume:bookTitle"/>,
        <xsl:value-of select="resume:publisher"/>.
        <xsl:value-of select="resume:date"/></li>
  </xsl:template>
  <xsl:template match="resume:memberships">
    <div id="resume-memberships">
      <section>
        <xsl:apply-templates/>
      </section>
    </div>
  </xsl:template>
  <xsl:template match="resume:membership">
    <section class="resume-membership">
      <title><xsl:value-of select="resume:organization"/></title>
      <xsl:apply-templates/>
    </section>
  </xsl:template>
  <xsl:template match="resume:misc">
    <div class="resume-misc">
      <section>
        <xsl:apply-templates/>
      </section>
    </div>
  </xsl:template>
  <xsl:template match="resume:phone">
    Phone <xsl:value-of select="@location"/>: <xsl:value-of select="."/>
    <br/>
  </xsl:template>
  <xsl:template match="resume:email">
    EMail: 
    <a>
    <xsl:attribute name="href">mailto:<xsl:value-of select="."/>
    </xsl:attribute>
    <xsl:value-of select="."/></a>
    <br/>
  </xsl:template>
  <xsl:template match="resume:instantMessage">
    Instant Message (<xsl:value-of select="@service"/>): <xsl:value-of select="."/>
    <br/>
  </xsl:template>
  <xsl:template match="resume:header">
    <header>
      <title><xsl:value-of select="resume:name/resume:title"/>
        <xsl:value-of select="resume:name/resume:firstname"/>
<xsl:text> </xsl:text>
        <xsl:value-of select="resume:name/resume:surname"/></title>
    </header>
  </xsl:template>
  <xsl:template match="resume:objective">
    <div id="resume-objective">
      <section>
        <title>Objective</title>
        <xsl:apply-templates/>
      </section>
    </div>
  </xsl:template>
  <xsl:template match="resume:availability">
    <div id="resume-availability">
      <section>
        <title>Availability</title>
        <xsl:apply-templates/>
      </section>
    </div>
  </xsl:template>
  <xsl:template match="resume:history">
    <div id="resume-history">
      <section>
        <title>Employment History</title>
        <xsl:apply-templates/>
      </section>
    </div>
  </xsl:template>
  <xsl:template match="resume:academics">
    <div id="resume-academics">
      <section>
        <title>Academic History</title>
        <xsl:apply-templates/>
      </section>
    </div>
  </xsl:template>
  <xsl:template match="resume:interests">
    <div id="resume-interests">
      <section>
        <title>Interests</title>
        <xsl:apply-templates/>
      </section>
    </div>
  </xsl:template>
  <xsl:template match="resume:interest">
    <div class="resume-interest">
      <section>
        <title><xsl:value-of select="resume:title"/></title>
        <xsl:apply-templates/>
      </section>
    </div>
  </xsl:template>
  <xsl:template match="resume:degrees">
    <xsl:apply-templates/>
  </xsl:template>
  <xsl:template match="resume:degree">
    <div class="resume-academic">
      <section>
        <title><xsl:value-of select="resume:major"/> (<xsl:value-of select="resume:level"/>)</title>
        <p>
          <xsl:value-of select="resume:institution"/>
          <xsl:if test="resume:date"> (<xsl:value-of select="resume:date"/>)</xsl:if>
          <xsl:if test="resume:annotation">
<xsl:text> </xsl:text>
            <xsl:value-of select="resume:annotation"/>
          </xsl:if>
        </p>
      </section>
    </div>
  </xsl:template>
  <xsl:template match="resume:job">
    <section>
      <title><xsl:value-of select="resume:jobtitle"/>, <xsl:value-of select="resume:employer"/>, <xsl:apply-templates select="resume:period"/></title>
      <div class="resume-job">
        <section>.
          <title>Job Description</title>
          <p>
            <xsl:apply-templates select="resume:description"/>
          </p>
        </section>
        <xsl:apply-templates select="resume:achievements"/>
        <xsl:apply-templates select="resume:projects"/>
      </div>
    </section>
  </xsl:template>
  <xsl:template match="resume:achievements">
    <section>
      <title>Achievements</title>
      <xsl:apply-templates select="resume:achievement"/>
    </section>
  </xsl:template>
  <xsl:template match="resume:achievement">
    <p>
      <xsl:apply-templates select="text()"/>
    </p>
  </xsl:template>
  <xsl:template match="resume:projects">
    <section>
      <title>Significant Projects</title>>
      <xsl:apply-templates select="resume:project"/>
    </section>
  </xsl:template>
  <xsl:template match="resume:project">
    <section>
      <title><xsl:value-of select="@title"/></title>
      <p>
        <xsl:value-of select="text()"/>
      </p>
      <xsl:if test="resume:url">
        <div class="resume-project-moreInformationLink">
          <p>
            More information is available from <a>
            <xsl:attribute name="href">
              <xsl:value-of select="resume:url"/>
            </xsl:attribute>
            <xsl:value-of select="resume:url"/>
            </a>
          </p>
        </div>
      </xsl:if>
    </section>
  </xsl:template>
  <xsl:template match="resume:period">
    <xsl:value-of select="resume:from"/>
    -
    <xsl:choose>
      <xsl:when test="resume:to/resume:present">
        present
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="resume:to"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template match="resume:skillareas">
    <div id="resume-skillareas">
      <section>
        <title>Skills Summary</title>
        <xsl:apply-templates/>
      </section>
    </div>
  </xsl:template>
  <xsl:template match="resume:referees">
    <div id="resume-referees">
      <section>
        <title>Referees</title>
        <xsl:apply-templates/>
      </section>
    </div>
  </xsl:template>
  <xsl:template match="resume:referee">
    <div class="resume-referee">
      <section>
        <title><xsl:apply-templates select="resume:name"/></title>
        <p>
          <xsl:apply-templates select="resume:title"/>
          <xsl:apply-templates select="resume:organization"/>
          <em> (Contact details provided upon request)</em>
        </p>
      </section>
    </div>
  </xsl:template>
  <xsl:template match="resume:skillarea">
    <div class="resume-skillArea">
      <xsl:apply-templates/>
    </div>
  </xsl:template>
  <xsl:template match="resume:title">
    <title><xsl:value-of select="."/></title>
  </xsl:template>
  <xsl:template match="resume:skillset">
    <div class="resume-skillset">
      <section>
        <xsl:apply-templates select="resume:title"/>
        <ul>
          <xsl:apply-templates select="resume:skill"/>
        </ul>
      </section>
    </div>
  </xsl:template>
  <xsl:template match="resume:skill">
    <li class="resume-skill">
      <xsl:value-of select="."/>
      <xsl:if test="@level"> (<xsl:value-of select="@level"/>)</xsl:if>
    </li>
  </xsl:template>
  <xsl:template match="resume:para">
    <p>
      <xsl:apply-templates/>
    </p>
  </xsl:template>
</xsl:stylesheet>
