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
    xmlns:st="http://chaperon.sourceforge.net/schema/syntaxtree/2.0"
    exclude-result-prefixes="st">
    <xsl:output indent="yes" method="html"
        doctype-public="-//APACHE//DTD Documentation V1.1//EN"
        doctype-system="document-v11.dtd"
        cdata-section-elements="source" />
    <xsl:param name="name" select="''" />
    <xsl:param name="spaceless-filenames" select="''" />
    <xsl:template name="splitString">
        <xsl:param name="restOfString" />
        <xsl:variable name="uppercase">
            (ABCDEFGHIJKLMNOPQRSTUVWXYZ
        </xsl:variable>
        <xsl:variable name="currentLetter"
            select="substring($restOfString,1,1)" />
        <xsl:choose>
            <xsl:when
                test="contains($restOfString, '(') or contains($restOfString,' ')">
                <xsl:value-of select="$restOfString" />
            </xsl:when>
            <xsl:when test="string-length($restOfString) &gt;= 2">
                <!-- there's a possibility it needs to be split -->
                <xsl:choose>
                    <xsl:when
                        test="contains($uppercase,$currentLetter)">
                        <xsl:variable name="followingLetter"
                            select="substring($restOfString,2,1)" />
                        <xsl:if
                            test="not(contains($uppercase,$followingLetter))">
                            <xsl:text></xsl:text>
                        </xsl:if>
                        <xsl:value-of select="$currentLetter" />
                        <xsl:call-template name="splitString">
                            <xsl:with-param name="restOfString"
                                select="substring($restOfString,2)" />
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:otherwise>
                        <!-- current letter is lower-case - just spit it out -->
                        <xsl:value-of select="$currentLetter" />
                        <xsl:call-template name="splitString">
                            <xsl:with-param name="restOfString"
                                select="substring($restOfString,2)" />
                        </xsl:call-template>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <!-- end of string - just write the remainder -->
                <xsl:value-of select="$restOfString" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template match="st:output">
        <document>
            <header>
                <title>
                    <xsl:variable name="title"
                        select="st:document/st:sections1/st:section/st:title/st:textsequence" />
                    <xsl:choose>
                        <xsl:when test="$title">
                            <xsl:value-of select="$title" />
                        </xsl:when>
                        <xsl:otherwise>
                            <!-- 	      <xsl:when test="$name"> -->
                            <xsl:call-template name="splitString">
                                <xsl:with-param name="restOfString"
                                    select="$name" />
                            </xsl:call-template>
                            <!-- 	      </xsl:when> -->
                        </xsl:otherwise>
                    </xsl:choose>
                </title>
            </header>
            <body>
                <xsl:apply-templates
                    select="st:document/st:paragraphs/st:paragraph/*"
                    mode="paragraph" />
                <xsl:apply-templates select="st:document/st:sections" />
            </body>
        </document>
    </xsl:template>
    <xsl:template match="st:sections">
        <xsl:apply-templates select="st:sections5/st:section5" />
        <xsl:apply-templates select="st:sections4/st:section4" />
        <xsl:apply-templates select="st:sections3/st:section3" />
        <xsl:apply-templates select="st:sections2/st:section2" />
        <xsl:apply-templates select="st:sections1/st:section1" />
    </xsl:template>
    <xsl:template match="st:section1">
        <section>
            <title>
                <xsl:value-of select="st:title1/st:textsequence" />
            </title>
            <xsl:apply-templates
                select="st:paragraphs/st:paragraph/*|st:sections2/st:section2" />
        </section>
    </xsl:template>
    <xsl:template match="st:section2">
        <section>
            <title>
                <xsl:value-of select="st:title2/st:textsequence" />
            </title>
            <xsl:apply-templates
                select="st:paragraphs/st:paragraph/*|st:sections3/st:section3" />
        </section>
    </xsl:template>
    <xsl:template match="st:section3">
        <section>
            <title>
                <xsl:value-of select="st:title3/st:textsequence" />
            </title>
            <xsl:apply-templates
                select="st:paragraphs/st:paragraph/*|st:sections4/st:section4" />
        </section>
    </xsl:template>
    <xsl:template match="st:section4">
        <section>
            <title>
                <xsl:value-of select="st:title4/st:textsequence" />
            </title>
            <xsl:apply-templates
                select="st:paragraphs/st:paragraph/*|st:sections5/st:section5" />
        </section>
    </xsl:template>
    <xsl:template match="st:section5">
        <section>
            <title>
                <xsl:value-of select="st:title5/st:textsequence" />
            </title>
            <xsl:apply-templates select="st:paragraphs/st:paragraph/*" />
        </section>
    </xsl:template>
    <xsl:template match="st:source">
        <source>
            <xsl:value-of select="substring(.,4,string-length(.)-6)" />
        </source>
    </xsl:template>
    <xsl:template match="st:textsequence">
        <p>
            <xsl:apply-templates select="st:textblock/*|st:break" />
        </p>
    </xsl:template>
    <xsl:template match="st:line" mode="paragraph">
        <p>
            --------------------------------------------------------------------------------
        </p>
    </xsl:template>
    <xsl:template match="st:table" mode="paragraph">
        <table>
            <xsl:apply-templates
                select="st:tablehead|st:tablerows/st:tablerow" />
        </table>
    </xsl:template>
    <xsl:template match="st:tablehead">
        <tr>
            <xsl:apply-templates select="st:tabletitle" />
        </tr>
    </xsl:template>
    <xsl:template match="st:tabletitle">
        <th>
            <xsl:apply-templates select="st:textblock/*" />
        </th>
    </xsl:template>
    <xsl:template match="st:tablerow">
        <tr>
            <xsl:apply-templates
                select="st:tablecolumns/st:tablecolumn" />
        </tr>
    </xsl:template>
    <xsl:template match="st:tablecolumn">
        <td>
            <xsl:apply-templates select="st:textblock/*" />
        </td>
    </xsl:template>
    <xsl:template match="st:etablecolumn">
        <td></td>
    </xsl:template>
    <xsl:template match="st:text">
        <xsl:value-of select="." />
        <xsl:text></xsl:text>
    </xsl:template>
    <xsl:template match="st:break">
        <br />
    </xsl:template>
    <xsl:template match="st:link">
        <xsl:choose>
            <xsl:when test="contains(.,' ')">
                <xsl:variable name="text"
                    select="substring-before(substring-after(.,' '),']')" />
                <xsl:variable name="href0"
                    select="substring-after(substring-before(.,' '),'[')" />
                <!-- 	just a temporary solution until wiki identifiers are available in forrest -->
                <xsl:variable name="href">
                    <xsl:choose>
                        <xsl:when
                            test="substring($href0,1,5) = 'wiki:'">
                            <xsl:value-of
                                select="concat('http://',substring($href0,6))" />
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="$href0" />
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <xsl:choose>
                    <xsl:when test="string(number($href)) != 'NaN'">
                        <link href="#{$href}">
                            <xsl:value-of select="$text" />
                        </link>
                    </xsl:when>
                    <xsl:when
                        test="contains($href,'.png') or contains($href,'.jpg') or contains($href,'.gif')">
                        <img src="{$href}" alt="{$text}" />
                    </xsl:when>
                    <xsl:when
                        test="contains($href,':') or contains($href,'.')">
                        <link href="{$href}">
                            <xsl:value-of select="$text" />
                        </link>
                    </xsl:when>
                    <xsl:otherwise>
                        <link>
                            <xsl:attribute name="href">
                                <xsl:choose>
                                    <xsl:when
                                        test="$spaceless-filenames">
                                        <xsl:value-of
                                            select="concat(translate($href,' ',''),'.html')" />
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of
                                            select="concat('view.do?page=',$href)" />
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:attribute>
                            <xsl:value-of select="$text" />
                        </link>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <xsl:variable name="href0"
                    select="substring(.,2,string-length(.)-2)" />
                <!-- 	just a temporary solution until wiki identifiers are available in forrest -->
                <xsl:variable name="href">
                    <xsl:choose>
                        <xsl:when
                            test="substring($href0,1,5) = 'wiki:'">
                            <xsl:value-of
                                select="concat('http://',substring($href0,6))" />
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="$href0" />
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <xsl:choose>
                    <xsl:when test="string(number($href)) != 'NaN'">
                        <link href="#{$href}">
                            [
                            <xsl:value-of select="$href" />
                            ]
                        </link>
                    </xsl:when>
                    <xsl:when
                        test="contains($href,'.png') or contains($href,'.jpg') or contains($href,'.gif')">
                        <img src="{$href}" alt="{$href}" />
                    </xsl:when>
                    <xsl:when
                        test="contains($href,':') or contains($href,'.')">
                        <link href="{$href}">
                            <xsl:value-of select="$href" />
                        </link>
                    </xsl:when>
                    <xsl:otherwise>
                        <link>
                            <xsl:attribute name="href">
                                <xsl:choose>
                                    <xsl:when
                                        test="$spaceless-filenames">
                                        <xsl:value-of
                                            select="concat(translate(translate($href, '&quot;', ''),' ',''),'.html')" />
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of
                                            select="concat('view.do?page=',$href)" />
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:attribute>
                            <xsl:value-of
                                select="translate($href, '&quot;','')" />
                        </link>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template match="st:anchor">
        <p>
            <xsl:choose>
                <xsl:when test="contains(.,' ')">
                    <anchor
                        id="substring-after(substring-before(.,' '),'[#')">
                        <xsl:value-of
                            select="substring-before(substring-after(.,' '),']')" />
                    </anchor>
                </xsl:when>
                <xsl:otherwise>
                    <em>
                        <xsl:value-of
                            select="substring(.,3,string-length(.)-3)" />
                        :
                    </em>
                    <anchor id="{substring(.,3,string-length(.)-3)}" />
                </xsl:otherwise>
            </xsl:choose>
        </p>
    </xsl:template>
    <xsl:template match="st:comment">
      <xsl:comment><xsl:apply-templates/></xsl:comment>
    </xsl:template>
    <xsl:template match="st:comment" mode="paragraph">
      <xsl:comment><xsl:apply-templates/></xsl:comment>
    </xsl:template>
    <xsl:template match="st:ipara">
        <strong>
            <xsl:value-of select="st:textblock/st:text" />
            <xsl:text></xsl:text>
        </strong>
    </xsl:template>
    <xsl:template match="st:ulblock">
        <xsl:value-of select="st:text" />
        <xsl:text></xsl:text>
    </xsl:template>
    <xsl:template match="st:emblock">
        <em>
            <xsl:value-of select="st:text" />
        </em>
        <xsl:text></xsl:text>
    </xsl:template>
    <xsl:template match="st:strongblock">
        <strong>
            <xsl:value-of select="st:text" />
        </strong>
        <xsl:text></xsl:text>
    </xsl:template>
    <xsl:template match="st:supblock">
        <sup>
            <xsl:value-of select="st:text" />
        </sup>
        <xsl:text></xsl:text>
    </xsl:template>
    <xsl:template match="st:subblock">
        <sub>
            <xsl:value-of select="st:text" />
        </sub>
        <xsl:text></xsl:text>
    </xsl:template>
    <xsl:template match="st:codeblock">
        <code>
            <xsl:value-of select="st:text" />
        </code>
        <xsl:text></xsl:text>
    </xsl:template>
    <xsl:template match="st:alist1">
        <xsl:apply-templates select="st:bulletedlist1" />
        <xsl:apply-templates select="st:letteredlist1" />
        <xsl:apply-templates select="st:numberedlist1" />
        <xsl:apply-templates select="st:romanlist1" />
        <xsl:apply-templates select="st:plainlist1" />
    </xsl:template>
    <xsl:template match="st:alist2">
        <xsl:apply-templates select="st:bulletedlist2" />
        <xsl:apply-templates select="st:letteredlist2" />
        <xsl:apply-templates select="st:numberedlist2" />
        <xsl:apply-templates select="st:romanlist2" />
        <xsl:apply-templates select="st:plainlist2" />
    </xsl:template>
    <xsl:template match="st:alist3">
        <xsl:apply-templates select="st:bulletedlist3" />
        <xsl:apply-templates select="st:letteredlist3" />
        <xsl:apply-templates select="st:numberedlist3" />
        <xsl:apply-templates select="st:romanlist3" />
        <xsl:apply-templates select="st:plainlist3" />
    </xsl:template>
    <xsl:template match="st:bulletedlist1">
        <ul>
            <xsl:apply-templates
                select="st:bulletedlistitem1|st:alist2" />
        </ul>
    </xsl:template>
    <xsl:template match="st:bulletedlistitem1">
        <li>
            <xsl:apply-templates
                select="st:textsequence/st:textblock/*" />
        </li>
    </xsl:template>
    <xsl:template match="st:bulletedlist2">
        <ul>
            <xsl:apply-templates
                select="st:bulletedlistitem2|st:alist3" />
        </ul>
    </xsl:template>
    <xsl:template match="st:bulletedlistitem2">
        <li>
            <xsl:apply-templates
                select="st:textsequence/st:textblock/*" />
        </li>
    </xsl:template>
    <xsl:template match="st:bulletedlist3">
        <ul>
            <xsl:apply-templates select="st:bulletedlistitem3" />
        </ul>
    </xsl:template>
    <xsl:template match="st:bulletedlistitem3">
        <li>
            <xsl:apply-templates
                select="st:textsequence/st:textblock/*" />
        </li>
    </xsl:template>
    <xsl:template match="st:numberedlist1">
        <ol type="1">
            <xsl:apply-templates
                select="st:numberedlistitem1|st:alist2" />
        </ol>
    </xsl:template>
    <xsl:template match="st:numberedlistitem1">
        <li>
            <xsl:apply-templates
                select="st:textsequence/st:textblock/*" />
        </li>
    </xsl:template>
    <xsl:template match="st:numberedlist2">
        <ol type="1">
            <xsl:apply-templates
                select="st:numberedlistitem2|st:alist3" />
        </ol>
    </xsl:template>
    <xsl:template match="st:numberedlistitem2">
        <li>
            <xsl:apply-templates
                select="st:textsequence/st:textblock/*" />
        </li>
    </xsl:template>
    <xsl:template match="st:numberedlist3">
        <ol type="1">
            <xsl:apply-templates select="st:numberedlistitem3" />
        </ol>
    </xsl:template>
    <xsl:template match="st:numberedlistitem3">
        <li>
            <xsl:apply-templates
                select="st:textsequence/st:textblock/*" />
        </li>
    </xsl:template>
    <xsl:template match="st:letteredlist1">
        <ol type="a">
            <xsl:apply-templates
                select="st:letteredlistitem1|st:alist2" />
        </ol>
    </xsl:template>
    <xsl:template match="st:letteredlistitem1">
        <li>
            <xsl:apply-templates
                select="st:textsequence/st:textblock/*" />
        </li>
    </xsl:template>
    <xsl:template match="st:letteredlist2">
        <ol type="a">
            <xsl:apply-templates
                select="st:letteredlistitem2|st:alist3" />
        </ol>
    </xsl:template>
    <xsl:template match="st:letteredlistitem2">
        <li>
            <xsl:apply-templates
                select="st:textsequence/st:textblock/*" />
        </li>
    </xsl:template>
    <xsl:template match="st:letteredlist3">
        <ol type="a">
            <xsl:apply-templates select="st:letteredlistitem3" />
        </ol>
    </xsl:template>
    <xsl:template match="st:letteredlistitem3">
        <li>
            <xsl:apply-templates
                select="st:textsequence/st:textblock/*" />
        </li>
    </xsl:template>
    <xsl:template match="st:romanlist1">
        <ol type="i">
            <xsl:apply-templates select="st:romanlistitem1|st:alist2" />
        </ol>
    </xsl:template>
    <xsl:template match="st:romanlistitem1">
        <li>
            <xsl:apply-templates
                select="st:textsequence/st:textblock/*" />
        </li>
    </xsl:template>
    <xsl:template match="st:romanlist2">
        <ol type="i">
            <xsl:apply-templates select="st:romanlistitem2|st:alist3" />
        </ol>
    </xsl:template>
    <xsl:template match="st:romanlistitem2">
        <li>
            <xsl:apply-templates
                select="st:textsequence/st:textblock/*" />
        </li>
    </xsl:template>
    <xsl:template match="st:romanlist3">
        <ol type="i">
            <xsl:apply-templates select="st:romanlistitem3" />
        </ol>
    </xsl:template>
    <xsl:template match="st:romanlistitem3">
        <li>
            <xsl:apply-templates
                select="st:textsequence/st:textblock/*" />
        </li>
    </xsl:template>
    <xsl:template match="st:plainlist1">
        <ul>
            <xsl:apply-templates select="st:plainlistitem1|st:alist2" />
        </ul>
    </xsl:template>
    <xsl:template match="st:plainlistitem1">
        <xsl:apply-templates select="st:textsequence/st:textblock/*" />
    </xsl:template>
    <xsl:template match="st:plainlist2">
        <ul>
            <xsl:apply-templates select="st:plainlistitem2|st:alist3" />
        </ul>
    </xsl:template>
    <xsl:template match="st:plainlistitem2">
        <xsl:apply-templates select="st:textsequence/st:textblock/*" />
    </xsl:template>
    <xsl:template match="st:plainlist3">
        <ul>
            <xsl:apply-templates select="st:plainlistitem3" />
        </ul>
    </xsl:template>
    <xsl:template match="st:plainlistitem3">
        <xsl:apply-templates select="st:textsequence/st:textblock/*" />
    </xsl:template>
    <xsl:template match="st:rulen">
        <p>
            <xsl:apply-templates select="*" />
        </p>
    </xsl:template>
    <xsl:template match="@*|*|text()|processing-instruction()"
        priority="-1">
        <xsl:copy>
            <xsl:apply-templates
                select="@*|*|text()|processing-instruction()" />
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>
