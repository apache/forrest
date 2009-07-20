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
<!--
A prototype Docbook-to-Forrest stylesheet.
Volunteers are needed to improve this!

Support for the range of Docbook tags is very patchy. If you need real
Docbook support, then use Norm Walsh's stylesheets - see Forrest FAQ.

Credit: original from the jakarta-avalon project

-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:template match="/">
    <xsl:apply-templates select="book|chapter|revhistory|article|refentry"/>
  </xsl:template>
  <xsl:template match="refentry">
    <document>
      <header>
        <title><xsl:value-of select="refmeta/refentrytitle"/>
          <xsl:apply-templates select="refmeta/manvolnum"/></title>
      </header>
      <body>
        <xsl:apply-templates select="refnamediv"/>
        <xsl:apply-templates select="refsynopsisdiv"/>
        <xsl:apply-templates select="refsect1"/>
      </body>
    </document>
  </xsl:template>
  <xsl:template match="manvolnum">
      (<xsl:value-of select="."/>)
    </xsl:template>
  <xsl:template match="refsect1">
    <section>
      <xsl:apply-templates/>
    </section>
  </xsl:template>
  <xsl:template match="refsynopsisdiv">
    <section>
      <title>SYNOPSIS</title>
      <xsl:apply-templates select="cmdsynopsis"/>
    </section>
  </xsl:template>
  <xsl:template match="refnamediv">
    <section>
      <title>NAME</title>
      <p>
        <xsl:value-of select="refname"/>
        <xsl:apply-templates select="refpurpose"/>
      </p>
      <xsl:apply-templates select="refdescriptor"/>
    </section>
  </xsl:template>
  <xsl:template match="refdescriptor">
    <p>
      <xsl:value-of select="."/>
    </p>
  </xsl:template>
  <xsl:template match="refpurpose">
      - <xsl:value-of select="."/>
  </xsl:template>
  <xsl:template match="book">
    <document>
      <header>
        <xsl:apply-templates select="bookinfo/title"/>
        <xsl:apply-templates select="bookinfo/subtitle"/>
        <authors>
          <xsl:apply-templates select="bookinfo/author"/>
        </authors>
<!--
                        <notice/>
                        <abstract/>
                        -->
      </header>
      <body>
        <xsl:apply-templates select="node()[ local-name() != 'bookinfo']"/>
        <xsl:call-template name="apply-footnotes"/>
      </body>
    </document>
  </xsl:template>
  <xsl:template match="chapter">
    <document>
      <header>
        <xsl:apply-templates select="title"/>
        <xsl:apply-templates select="subtitle"/>
        <authors>
          <xsl:apply-templates select="chapterinfo/authorgroup/author"/>
        </authors>
      </header>
      <body>
        <xsl:apply-templates select="node()[
                              local-name() != 'title' and
                              local-name() != 'subtitle' and
                              local-name() != 'chapterinfo' 
                              ]"/>
        <xsl:call-template name="apply-footnotes"/>
      </body>
    </document>
  </xsl:template>
  <xsl:template match="article">
    <document>
      <header>
        <xsl:apply-templates select="articleinfo/title|title"/>
        <xsl:apply-templates select="articleinfo/subtitle|subtitle"/>
        <authors>
          <xsl:apply-templates select="articleinfo/author"/>
          <xsl:apply-templates select="articleinfo/corpauthor"/>
        </authors>
      </header>
      <body>
        <xsl:call-template name="extract-articleinfo"/>
        <xsl:apply-templates select="node()[
                               local-name() != 'title' and
                               local-name() != 'subtitle' and
                               local-name() != 'articleinfo'
                               ]"/>
        <xsl:call-template name="apply-footnotes"/>
      </body>
    </document>
  </xsl:template>
  <xsl:template name="extract-articleinfo">
    <xsl:if test="articleinfo/copyright">
      <section id="pubinfo">
        <title>Publication Information</title>
        <xsl:if test="articleinfo/pubdate">
          <p>
            Date published:
            <xsl:value-of select="articleinfo/pubdate"/>
          </p>
        </xsl:if>
        <xsl:apply-templates select="articleinfo/copyright"/>
      </section>
    </xsl:if>
  </xsl:template>
  <xsl:template name="apply-footnotes">
    <xsl:if test="//footnote">
      <section>
        <title>Footnotes</title>
        <xsl:apply-templates select="//footnote" mode="base"/>
      </section>
    </xsl:if>
  </xsl:template>
  <xsl:template match="author">
    <xsl:element name="person">
      <xsl:if test="id">
        <xsl:attribute name="id">
          <xsl:value-of select="id"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:attribute name="name">
        <xsl:if test="honorific">
          <xsl:value-of select="honorific"/>. </xsl:if>
        <xsl:if test="firstname">
          <xsl:value-of select="firstname"/>
        </xsl:if>
<xsl:text> </xsl:text>
        <xsl:value-of select="surname"/>
        <xsl:if test="affiliation">
<xsl:text> (</xsl:text>
<!-- FIXME: horrid hack - there can be zero-or-more jobtitle -->
          <xsl:if test="affiliation/jobtitle">
            <xsl:value-of select="affiliation/jobtitle"/>
<xsl:text>, </xsl:text>
          </xsl:if>
          <xsl:if test="affiliation/orgname">
            <xsl:value-of select="affiliation/orgname"/>
          </xsl:if>
<xsl:text>)</xsl:text>
        </xsl:if>
      </xsl:attribute>
      <xsl:attribute name="email">
        <xsl:value-of select="address/email"/>
      </xsl:attribute>
    </xsl:element>
  </xsl:template>
  <xsl:template match="chapter">
    <section>
      <xsl:apply-templates/>
    </section>
  </xsl:template>
  <xsl:template match="cmdsynopsis">
<!--
            <cmdsynopsis>
                  <command>xindice add_collection</command>
                  <arg choice="req">-c <replaceable>context</replaceable></arg>
                  <arg choice="req">-n <replaceable>name</replaceable></arg>
                  <arg choice="opt">-v <replaceable></replaceable></arg>
            </cmdsynopsis>
            -->
    <p>
      <code class="cmdsynopsis">
      <xsl:value-of select="command"/>
      <xsl:apply-templates select="node()[ local-name() != 'command' ]"/>
      </code>
    </p>
  </xsl:template>
  <xsl:template match="arg">
    <xsl:choose>
      <xsl:when test="@choice = 'req' ">
        <xsl:apply-templates/>
      </xsl:when>
      <xsl:otherwise>
                        [<xsl:apply-templates/>]
                  </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template match="replaceable">
            (or <xsl:value-of select="."/>)
      </xsl:template>
  <xsl:template match="bridgehead">
    <section>
      <title><xsl:value-of select="."/></title>
    </section>
  </xsl:template>
  <xsl:template match="example">
<!-- changed to note because section gives a hard time
                 to set the class: it's done in skins/common, pelt 
                 and then in site2xhtml, too :-(  -->
    <note class="example" label="{title}">
      <xsl:apply-templates/>
    </note>
  </xsl:template>
  <xsl:template match="note">
    <note>
      <xsl:if test="title">
        <xsl:attribute name="label">
          <xsl:value-of select="title"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:apply-templates/>
    </note>
  </xsl:template>
  <xsl:template match="informaltable">
    <table>
      <xsl:apply-templates/>
    </table>
  </xsl:template>
  <xsl:template match="anchor">
<!--
            <a name="{.}"/>
            -->
    <xsl:element name="link">
      <xsl:if test="@href">
        <xsl:attribute name="href">
          <xsl:value-of select="@href"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:attribute name="id">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </xsl:element>
  </xsl:template>
  <xsl:template match="a"><link href="{@href}">
    <xsl:value-of select="."/></link>
  </xsl:template>
  <xsl:template match="subtitle">
    <subtitle>
      <xsl:value-of select="."/>
    </subtitle>
  </xsl:template>
  <xsl:template match="title">
    <title><xsl:value-of select="."/></title>
  </xsl:template>
  <xsl:template match="affiliation">
    <li><xsl:text>[</xsl:text>
      <xsl:value-of select="shortaffil"/>
<xsl:text>] </xsl:text>
      <b>
        <xsl:value-of select="jobtitle"/>
      </b>
      <i>
        <xsl:value-of select="orgname"/>
        <xsl:if test="orgdiv">
<xsl:text>/</xsl:text>
          <xsl:value-of select="orgdiv"/>
        </xsl:if>
      </i></li>
  </xsl:template>
  <xsl:template match="authorblurb">
    <section title="Bio">
      <xsl:apply-templates/>
    </section>
  </xsl:template>
  <xsl:template match="honorific|firstname|surname|orgdiv|orgname|shortaffil|jobtitle"/>
  <xsl:template match="revhistory">
    <document>
      <header>
        <title>Revision History</title>
      </header>
      <body>
        <section>
          <title>Revision History</title>
          <table>
            <xsl:variable name="unique-revisions" select="revision[not(revnumber=preceding-sibling::revision/revnumber)]/revnumber"/>
            <xsl:variable name="base" select="."/>
            <xsl:for-each select="$unique-revisions">
              <tr>
                <td>
                  <b>Revision <xsl:value-of select="."/> 
                                                            (<xsl:value-of select="$base/revision[revnumber=current()]/date"/>)
                                                      </b>
                </td>
              </tr>
              <tr>
                <td>
                  <font color="#000000" face="arial,helvetica,sanserif">
                    <br/>
                    <ul>
                      <xsl:apply-templates select="$base/revision[revnumber=current()]"/>
                    </ul>
                  </font>
                </td>
              </tr>
            </xsl:for-each>
          </table>
        </section>
      </body>
    </document>
  </xsl:template>
  <xsl:template match="para">
    <xsl:if test="normalize-space(.)">
    <p>
      <xsl:apply-templates/>
    </p>
    </xsl:if>
  </xsl:template>
  <xsl:template match="emphasis">
    <xsl:choose>
      <xsl:when test="@role='bold'"><strong>
        <xsl:apply-templates/></strong>
      </xsl:when>
      <xsl:otherwise><em>
        <xsl:apply-templates/></em>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template match="firstterm"><em>
    <xsl:apply-templates/></em>
  </xsl:template>
  <xsl:template match="revision">
    <li><xsl:choose>
        <xsl:when test="@revisionflag='added'">
          <img align="absmiddle" alt="added" src="images/add.jpg"/>
        </xsl:when>
        <xsl:when test="@revisionflag='changed'">
          <img align="absmiddle" alt="changed" src="images/update.jpg"/>
        </xsl:when>
        <xsl:when test="@revisionflag='deleted'">
          <img align="absmiddle" alt="deleted" src="images/remove.jpg"/>
        </xsl:when>
        <xsl:when test="@revisionflag='off'">
          <img align="absmiddle" alt="off" src="images/fix.jpg"/>
        </xsl:when>
        <xsl:otherwise>
          <img align="absmiddle" alt="changed" src="images/update.jpg"/>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:value-of select="revremark"/>
<xsl:text> (</xsl:text>
      <xsl:value-of select="authorinitials"/>
<xsl:text>)</xsl:text></li>
  </xsl:template>
  <xsl:template match="revnumber|revremark|authorinitials|date"/>
  <xsl:template match="sect1|sect2|sect3|sect4|sect5|section">
    <xsl:param name="idval">
      <xsl:if test="@id">
        <xsl:value-of select="@id"/>
      </xsl:if>
    </xsl:param>
    <xsl:choose>
      <xsl:when test="@id">
        <xsl:element name="section">
          <xsl:attribute name="id">
            <xsl:value-of select="$idval"/>
          </xsl:attribute>
          <xsl:apply-templates/>
        </xsl:element>
      </xsl:when>
      <xsl:otherwise>
        <xsl:element name="section">
          <xsl:apply-templates/>
        </xsl:element>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template match="dedication">
    <table>
      <tr>
        <td>
          <b>Dedication</b>
        </td>
      </tr>
      <tr>
        <td>
          <br/>
          <xsl:apply-templates/>
        </td>
      </tr>
    </table>
  </xsl:template>
  <xsl:template match="edition|pubdate|year|holder"/>
  <xsl:template match="copyright">
    <p>
      Copyright &#x00A9;
      <xsl:value-of select="year"/>
      by
      <xsl:value-of select="holder"/>
<xsl:text> </xsl:text>
      <i>All rights reserved.</i>
    </p>
  </xsl:template>
  <xsl:template match="legalnotice">
    <table>
      <tr>
        <td>
          <xsl:apply-templates/>
        </td>
      </tr>
    </table>
  </xsl:template>
  <xsl:template match="programlisting">
    <source>
      <xsl:apply-templates/>
    </source>
  </xsl:template>
  <xsl:template match="screen">
    <source>
      <xsl:apply-templates/>
    </source>
  </xsl:template>
  <xsl:template match="variablelist">
    <dl>
      <xsl:apply-templates/>
    </dl>
  </xsl:template>
  <xsl:template match="varlistentry">
    <dt><xsl:apply-templates select="term"/></dt>
    <dd><xsl:apply-templates select="listitem/*"/></dd>
  </xsl:template>
  <xsl:template match="orderedlist">
    <ol>
      <xsl:apply-templates/>
    </ol>
  </xsl:template>
  <xsl:template match="listitem">
    <li><xsl:apply-templates/></li>
  </xsl:template>
  <xsl:template match="itemizedlist">
    <ul>
      <xsl:apply-templates/>
    </ul>
  </xsl:template>
  <xsl:template match="command"><code class="command">
    <xsl:value-of select="."/></code>
  </xsl:template>
  <xsl:template match="computeroutput"><code  class="computeroutput">
    <xsl:value-of select="."/></code>
  </xsl:template>
  <xsl:template match="userinput"><code class="userinput">
    <xsl:value-of select="."/></code>
  </xsl:template>
  <xsl:template match="varname"><code class="varname">
    <xsl:value-of select="."/></code>
  </xsl:template>
  <xsl:template match="literal">
<!-- @moreinfo ignored --><code class="literal">
    <xsl:value-of select="."/></code>
  </xsl:template>
  <xsl:template match="option"><code class="option">
    <xsl:value-of select="."/></code>
  </xsl:template>
  <xsl:template match="constant"><code class="constant">
    <xsl:value-of select="."/></code>
  </xsl:template>
  <xsl:template match="trademark">
    <xsl:apply-templates/>&#x2122;
      </xsl:template>
  <xsl:template match="filename"><code class="filename">
    <xsl:value-of select="."/></code>
  </xsl:template>
  <xsl:template match="classname|function|parameter"><code class="{local-name()}">
    <xsl:apply-templates/>
    <xsl:if test="name(.)='function'">
<xsl:text>()</xsl:text>
    </xsl:if></code>
  </xsl:template>
  <xsl:template match="quote">
<xsl:text>"</xsl:text>
    <xsl:apply-templates/>
<xsl:text>"</xsl:text>
  </xsl:template>
  <xsl:template match="blockquote">
    <table>
      <xsl:if test="title">
        <tr>
          <td>
            <xsl:value-of select="title"/>
          </td>
        </tr>
      </xsl:if>
      <tr>
        <td>
          <xsl:apply-templates/>
        </td>
      </tr>
    </table>
  </xsl:template>
  <xsl:template match="warning">
    <warning>
      <xsl:apply-templates/>
    </warning>
  </xsl:template>
  <xsl:template match="ulink">
    <xsl:element name="link">
      <xsl:attribute name="href">
        <xsl:choose>
          <xsl:when test="@uri">
            <xsl:value-of select="@uri"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="@url"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:choose>
        <xsl:when test="not(node()[not(self::comment())])">
          <xsl:choose>
            <xsl:when test="@uri">
              <xsl:value-of select="@uri"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="@url"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:element>
  </xsl:template>
  <xsl:template match="footnote">
    <xsl:variable name="footnote-id">
      <xsl:value-of select="count(preceding::footnote)+1"/>
    </xsl:variable>
    <anchor id="footnote-{$footnote-id}-ref"/><sup><link href="#footnote-{$footnote-id}">
    <xsl:value-of select="$footnote-id"/></link></sup>
  </xsl:template>
  <xsl:template match="footnote" mode="base">
    <p>
      <xsl:variable name="footnote-id">
        <xsl:value-of select="count(preceding::footnote)+1"/>
      </xsl:variable>
      <anchor id="footnote-{$footnote-id}"/>
      <link href="#footnote-{$footnote-id}-ref">
      <xsl:value-of select="$footnote-id"/>
      </link>
<xsl:text>) </xsl:text>
<!-- Most footnotes have a para nested; strip if there is only one-->
      <xsl:if test="not(para)">
        <xsl:apply-templates/>
      </xsl:if>
      <xsl:if test="count(para)=1">
        <xsl:apply-templates
                              select="para/node()"/>
      </xsl:if>
    </p>
    <xsl:if test="count(para)>1">
      <xsl:apply-templates/>
    </xsl:if>
  </xsl:template>
  <xsl:template match="figure">
    <table>
      <tr>
        <td>
          <xsl:value-of select="title"/>
        </td>
      </tr>
      <tr>
        <td>
          <xsl:apply-templates select="*[not(self::title)]"/>
        </td>
      </tr>
    </table>
  </xsl:template>
  <xsl:template match="mediaobject|imageobject">
    <xsl:apply-templates/>
  </xsl:template>
  <xsl:template match="inlinegraphic">
    <img alt="{@srccredit}" src="{@fileref}"/>
  </xsl:template>
  <xsl:template match="graphic|imagedata">
    <img alt="{@srccredit}" src="{@fileref}"/>
    <xsl:if test="@srccredit">
      <ul>
        <li><xsl:value-of select="@srccredit"/></li>
      </ul>
    </xsl:if>
  </xsl:template>
  <xsl:template match="simplelist">
    <ul>
      <xsl:apply-templates/>
    </ul>
  </xsl:template>
  <xsl:template match="member">
    <li><xsl:apply-templates/></li>
  </xsl:template>
  <xsl:template match="table">
    <table>
      <caption>
        <xsl:value-of select="title"/>
      </caption>
      <xsl:apply-templates/>
    </table>
  </xsl:template>
  <xsl:template match="tgroup">
    <xsl:apply-templates select="thead|tbody|tfoot"/>
  </xsl:template>
  <xsl:template match="thead">
    <xsl:apply-templates mode="thead"/>
  </xsl:template>
  <xsl:template match="row" mode="thead">
    <tr>
      <xsl:apply-templates mode="thead"/>
    </tr>
  </xsl:template>
  <xsl:template match="entry" mode="thead">
    <th>
      <xsl:call-template name="entry.spans"/>
      <xsl:apply-templates/>
    </th>
  </xsl:template>
  <xsl:template match="row">
    <tr>
      <xsl:apply-templates/>
    </tr>
  </xsl:template>
  <xsl:template match="tbody|tfoot">
    <xsl:apply-templates/>
  </xsl:template>
  <xsl:template match="entry">
    <td>
      <xsl:call-template name="entry.spans"/>
      <xsl:apply-templates/>
    </td>
  </xsl:template>
  <xsl:template name="entry.spans">
    <xsl:if test="@morerows">
      <xsl:attribute name="rowspan">
        <xsl:value-of select="number(@morerows)+1"/>
      </xsl:attribute>
    </xsl:if>
    <xsl:if test="@namest and @nameend">
      <xsl:attribute name="colspan">
        <xsl:variable name="start">
          <xsl:call-template name="colspec.index">
            <xsl:with-param name="olist" select="ancestor::tgroup/colspec"/>
            <xsl:with-param name="colname" select="@namest"/>
          </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="end">
          <xsl:call-template name="colspec.index">
            <xsl:with-param name="olist" select="ancestor::tgroup/colspec"/>
            <xsl:with-param name="colname" select="@nameend"/>
          </xsl:call-template>
        </xsl:variable>
        <xsl:value-of select="number($end)-number($start)+1"/>
      </xsl:attribute>
    </xsl:if>
  </xsl:template>
  <xsl:template name="colspec.index">
<!-- calculate the index for a given colname, e.g. from entry/@nameend -->
<!-- inspired from the original docbook stylesheets -->
    <xsl:param name="olist" select="//table/tgroup/colspec"/>
    <xsl:param name="colname" select="c1"/>
    <xsl:for-each select="$olist">
      <xsl:if test="@colname=$colname">
        <xsl:choose>
          <xsl:when test="@colnum">
            <xsl:value-of select="@colnum"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="position()"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>
  <xsl:template match="trademark">
    <xsl:apply-templates/><sup>TM</sup>
  </xsl:template>
  <xsl:template match="email">
        &lt;<link>
    <xsl:attribute name="href">
      <xsl:value-of select="concat('mailto:',.)"/>
    </xsl:attribute>
    <xsl:apply-templates/></link>&gt;
      </xsl:template>
<!-- Filched from Norm Walsh's inline.xsl -->
  <xsl:template match="sgmltag">
    <xsl:call-template name="format.sgmltag"/>
  </xsl:template>
  <xsl:template name="format.sgmltag">
    <xsl:param name="class">
      <xsl:choose>
        <xsl:when test="@class">
          <xsl:value-of select="@class"/>
        </xsl:when>
        <xsl:otherwise>element</xsl:otherwise>
      </xsl:choose>
    </xsl:param><tt class="sgmltag-{$class}">
    <xsl:choose>
      <xsl:when test="$class='attribute'">
        <xsl:apply-templates/>
      </xsl:when>
      <xsl:when test="$class='attvalue'">
        <xsl:apply-templates/>
      </xsl:when>
      <xsl:when test="$class='element'">
        <xsl:apply-templates/>
      </xsl:when>
      <xsl:when test="$class='endtag'">
<xsl:text>&lt;/</xsl:text>
        <xsl:apply-templates/>
<xsl:text>&gt;</xsl:text>
      </xsl:when>
      <xsl:when test="$class='genentity'">
<xsl:text>&amp;</xsl:text>
        <xsl:apply-templates/>
<xsl:text>;</xsl:text>
      </xsl:when>
      <xsl:when test="$class='numcharref'">
<xsl:text>&amp;#</xsl:text>
        <xsl:apply-templates/>
<xsl:text>;</xsl:text>
      </xsl:when>
      <xsl:when test="$class='paramentity'">
<xsl:text>%</xsl:text>
        <xsl:apply-templates/>
<xsl:text>;</xsl:text>
      </xsl:when>
      <xsl:when test="$class='pi'">
<xsl:text>&lt;?</xsl:text>
        <xsl:apply-templates/>
<xsl:text>&gt;</xsl:text>
      </xsl:when>
      <xsl:when test="$class='xmlpi'">
<xsl:text>&lt;?</xsl:text>
        <xsl:apply-templates/>
<xsl:text>?&gt;</xsl:text>
      </xsl:when>
      <xsl:when test="$class='starttag'">
<xsl:text>&lt;</xsl:text>
        <xsl:apply-templates/>
<xsl:text>&gt;</xsl:text>
      </xsl:when>
      <xsl:when test="$class='emptytag'">
<xsl:text>&lt;</xsl:text>
        <xsl:apply-templates/>
<xsl:text>/&gt;</xsl:text>
      </xsl:when>
      <xsl:when test="$class='sgmlcomment'">
<xsl:text>&lt;!--</xsl:text>
        <xsl:apply-templates/>
<xsl:text>--&gt;</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates/>
      </xsl:otherwise>
    </xsl:choose></tt>
  </xsl:template>
  <xsl:template match="xref">
    <xsl:param name="linkend">
      <xsl:value-of select="@linkend"/>
    </xsl:param>
    <xsl:param name="endterm">
      <xsl:value-of select="@endterm"/>
    </xsl:param>
    <xsl:param name="xreflabelvalue">
      <xsl:value-of select="@xreflabel"/>
    </xsl:param>
    <xsl:param name="linkendvalue">
      <xsl:if test="$linkend">
        <xsl:value-of select="//*[@id=$linkend]/title"/>
      </xsl:if>
    </xsl:param>
    <xsl:param name="endtermvalue">
      <xsl:if test="$endterm">
        <xsl:value-of select="//*[@id=$endterm]"/>
      </xsl:if>
    </xsl:param>
    <xsl:choose>
      <xsl:when test="//xref[not(@endterm)]">
        <xsl:element name="link">
          <xsl:attribute name="href">
<xsl:text>#</xsl:text>
            <xsl:value-of select="$linkend"/>
          </xsl:attribute>
          <xsl:choose>
            <xsl:when test="@xreflabel">
              <xsl:value-of select="$xreflabelvalue"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="$linkendvalue"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:element>
        <xsl:apply-templates/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:element name="link">
          <xsl:attribute name="href">
<xsl:text>#</xsl:text>
            <xsl:value-of select="$linkend"/>
          </xsl:attribute>
          <xsl:value-of select="$endtermvalue"/>
        </xsl:element>
        <xsl:apply-templates/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template match="node()|@*" priority="-1">
    <xsl:copy>
      <xsl:apply-templates select="node()|@*"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
<!-- vim: set ft=xml sw=6: -->
