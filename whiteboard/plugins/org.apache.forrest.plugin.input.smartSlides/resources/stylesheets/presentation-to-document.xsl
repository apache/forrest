<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" extension-element-prefixes="datetime redirect"
    xmlns:datetime="http://exslt.org/dates-and-times" xmlns:redirect="http://xml.apache.org/xalan/redirect">
    <xsl:output doctype-public="-//APACHE//DTD Documentation V1.3//EN" doctype-system="http://forrest.apache.org/dtd/document-v13.dtd" encoding="ISO-8859-1"
        indent="yes" method="xml" />
    <!-- 
        
        Parameters 
    
    -->
    <!--space separated list of all special elements to be visible in target (talk demo addlInfo comment todo) -->
    <xsl:param name="show"> </xsl:param>
    <!--Keep this set as long as DIVs are not legal in doc13-->
    <xsl:param name="noDIVs">1</xsl:param>
    <xsl:template match="/">
        <xsl:apply-templates mode="doc13" />
    </xsl:template>
    <!-- 
        
        Main processing template
    -->
    <xsl:template match="presentation" mode="doc13">
        <document>
            <header>
                <xsl:apply-templates select="metadata" mode="doc13" />
            </header>
            <xsl:apply-templates select="content" mode="doc13" />
        </document>
    </xsl:template>
    <!-- 
        
        Process metadata
    -->
    <xsl:template match="metadata" mode="doc13">
        <title>
            <xsl:value-of select="title" />
        </title>
        <xsl:if test="count(author)">
            <authors>
                <xsl:for-each select="author">
                    <person>
                        <xsl:attribute name="name">
                            <xsl:value-of select="fullName" />
                        </xsl:attribute>
                        <xsl:if test="email">
                            <xsl:attribute name="email">
                                <xsl:value-of select="email" />
                            </xsl:attribute>
                        </xsl:if>
                    </person>
                </xsl:for-each>
            </authors>
        </xsl:if>
        <abstract>
            <xsl:value-of select="abstract" />
        </abstract>
    </xsl:template>
    <!-- 
        
        Generate visible content
    -->
    <xsl:template match="content" mode="doc13">
        <body>
            <xsl:attribute name="class">
                <xsl:value-of select="normalize-space(concat('slidyPresentation ', @class))" />
            </xsl:attribute>
            <xsl:copy-of select="@id|@style" />
            <!--       <section id="main">
            <title>
                <xsl:value-of select="/presentation/metadata/title" />
            </title>-->
            <xsl:apply-templates mode="doc13" />
            <!--        </section>-->
        </body>
    </xsl:template>
    <!--
        
        Process chapter start
    -->
    <xsl:template match="chapter" mode="doc13">
        <xsl:if test="not(contains(@showOnlyFor,'slides'))">
            <section>
                <xsl:attribute name="class">
                    <xsl:value-of select="normalize-space(concat('chapter ', @class))" />
                </xsl:attribute>
                <xsl:copy-of select="@id|@style" />
                <title>
                    <xsl:value-of select="@title" />
                </title>
                <xsl:apply-templates mode="doc13" />
            </section>
        </xsl:if>
    </xsl:template>
    <!--
        
        Process module (no processing at all)
    -->
    <xsl:template match="module" mode="doc13">
        <xsl:if test="not(contains(@showOnlyFor,'slides'))">
            <xsl:apply-templates mode="doc13" />
        </xsl:if>
    </xsl:template>
    <!--
        
        Process page
    -->
    <xsl:template match="page" mode="doc13">
        <xsl:if test="not(contains(@showOnlyFor,'slides'))">
            <section>
                <xsl:attribute name="class">
                    <xsl:value-of select="normalize-space(concat('slide ', @type))" />
                </xsl:attribute>
                <xsl:copy-of select="@id|@style" />
                <xsl:choose>
                    <xsl:when test="heading">
                        <title>
                            <xsl:copy-of select="heading/@*" />
                            <xsl:apply-templates mode="doc13Heading" select="heading" />
                        </title>
                    </xsl:when>
                    <xsl:otherwise>
                        <title>
                            <xsl:apply-templates mode="doc13" select="@title" />
                        </title>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:apply-templates mode="doc13" />
            </section>
        </xsl:if>
    </xsl:template>
    <!--
        
        Special element TOC as a macro for a Table of content
    -->
    <xsl:template match="TOC" mode="doc13">
        <ul>
            <xsl:for-each select="//chapter[not (@hideInTOC)]|//page[@listIn='allTOCs' or  @listIn='globalTOC']">
                <li>
                    <p>
                        <xsl:attribute name="class">
                            <xsl:choose>
                                <xsl:when test="local-name()='chapter'">TOCchapterEntry</xsl:when>
                                <xsl:otherwise>TOCPageEntry</xsl:otherwise>
                            </xsl:choose>
                        </xsl:attribute>
                        <xsl:choose>
                            <xsl:when test="@title">
                                <xsl:value-of select="@title" />
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="heading" />
                            </xsl:otherwise>
                        </xsl:choose>
                    </p>
                </li>
            </xsl:for-each>
        </ul>
    </xsl:template>
    <!--
        
        Special element chapterTOC as a macro for a Table of content
    -->
    <xsl:template match="chapterTOC" mode="doc13">
        <!-- List of pages -->
        <xsl:variable name="list" select="ancestor::page/following-sibling::page[@listIn='allTOCs' or @listIn='chapterTOC']|ancestor::
            page/following-sibling::chapter" />
        <xsl:if test="$list">
            <ul>
                <xsl:for-each select="$list">
                    <li>
                        <xsl:value-of select="@title" />
                    </li>
                </xsl:for-each>
            </ul>
        </xsl:if>
    </xsl:template>
    <!--
        
        Special element PresentationTitle as a macro for a Table of content
    -->
    <xsl:template match="PresentationTitle" mode="doc13">
        <xsl:value-of select="/presentation/metadata/title" />
    </xsl:template>
    <!--
        
        Special element Presenters as a macro for a Table of content
    -->
    <xsl:template match="Presenters" mode="doc13">
        <xsl:for-each select="/presentation/metadata/presenter">
            <p class="Presenter">
                <xsl:value-of select="fullName" />
                <br />
                <link href="mailto:{email}">
                    <xsl:value-of select="email" />
                </link>
            </p>
        </xsl:for-each>
    </xsl:template>
    <!--
        
        Heading
        block processing
    -->
    <xsl:template match="heading" mode="doc13" />
    <!--
        
        Heading
        block processing
    -->
    <xsl:template match="heading" mode="doc13Heading">
        <xsl:apply-templates mode="doc13" />
    </xsl:template>
    <!--
        
        section
    -->
    <xsl:template match="section" mode="doc13">
        <xsl:if test="not(contains(@showOnlyFor,'slides'))">
            <section>
                <xsl:copy-of select="@class|@id|@style" />
                <title>
                    <xsl:value-of select="@title" />
                </title>
                <xsl:apply-templates mode="doc13" />
            </section>
        </xsl:if>
    </xsl:template>
    <!--
        
        XRef
    -->
    <xsl:template match="xref" mode="doc13">
        <xsl:if test="not(contains(@showOnlyFor,'slides'))">
            <link>
                <xsl:copy-of select="@class|@id|@style" />
                <!-- FIXME: Remove this as soon as we can have divs in docX -->
                <xsl:choose>
                    <xsl:when test="$noDIVs and ancestor::talk">
                        <xsl:attribute name="class">talk</xsl:attribute>
                    </xsl:when>
                    <xsl:when test="$noDIVs and ancestor::demo">
                        <xsl:attribute name="class">demo</xsl:attribute>
                    </xsl:when>
                    <xsl:when test="$noDIVs and ancestor::comment">
                        <xsl:attribute name="class">comment</xsl:attribute>
                    </xsl:when>
                </xsl:choose>
                <xsl:attribute name="href">
                    <xsl:value-of select="@url" />
                </xsl:attribute>
                <xsl:copy-of select="@*[name(.) != 'url']" />
                <xsl:apply-templates mode="doc13" />
            </link>
        </xsl:if>
    </xsl:template>
    <!--
        
        Image
    -->
    <xsl:template match="image" mode="doc13">
        <xsl:if test="not(contains(@showOnlyFor,'slides'))">
            <xsl:choose>
                <xsl:when test="parent::page|parent::section">
                    <figure>
                        <xsl:copy-of select="@class|@id|@style" />
                        <!-- FIXME: Remove this as soon as we can have divs in docX -->
                        <xsl:choose>
                            <xsl:when test="$noDIVs and ancestor::talk">
                                <xsl:attribute name="class">talk</xsl:attribute>
                            </xsl:when>
                            <xsl:when test="$noDIVs and ancestor::demo">
                                <xsl:attribute name="class">demo</xsl:attribute>
                            </xsl:when>
                            <xsl:when test="$noDIVs and ancestor::comment">
                                <xsl:attribute name="class">comment</xsl:attribute>
                            </xsl:when>
                        </xsl:choose>
                        <xsl:attribute name="src">
                            <xsl:value-of select="@url" />
                        </xsl:attribute>
                        <xsl:attribute name="alt">
                            <xsl:value-of select="@alternativeText" />
                        </xsl:attribute>
                    </figure>
                </xsl:when>
                <xsl:otherwise>
                    <icon>
                        <xsl:copy-of select="@class|@id|@style" />
                        <!-- FIXME: Remove this as soon as we can have divs in docX -->
                        <xsl:choose>
                            <xsl:when test="$noDIVs and ancestor::talk">
                                <xsl:attribute name="class">talk</xsl:attribute>
                            </xsl:when>
                            <xsl:when test="$noDIVs and ancestor::demo">
                                <xsl:attribute name="class">demo</xsl:attribute>
                            </xsl:when>
                            <xsl:when test="$noDIVs and ancestor::comment">
                                <xsl:attribute name="class">comment</xsl:attribute>
                            </xsl:when>
                        </xsl:choose>
                        <xsl:attribute name="src">
                            <xsl:value-of select="@url" />
                        </xsl:attribute>
                        <xsl:attribute name="alt">
                            <xsl:value-of select="@alternativeText" />
                        </xsl:attribute>
                    </icon>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:if>
    </xsl:template>
    <!--
        
        numberedList
    -->
    <xsl:template match="numberedList" mode="doc13">
        <xsl:if test="not(contains(@showOnlyFor,'slides'))">
            <ol>
                <xsl:copy-of select="@class|@id|@style" />
                <!-- FIXME: Remove this as soon as we can have divs in docX -->
                <xsl:choose>
                    <xsl:when test="$noDIVs and ancestor::talk">
                        <xsl:attribute name="class">talk</xsl:attribute>
                    </xsl:when>
                    <xsl:when test="$noDIVs and ancestor::demo">
                        <xsl:attribute name="class">demo</xsl:attribute>
                    </xsl:when>
                    <xsl:when test="$noDIVs and ancestor::comment">
                        <xsl:attribute name="class">comment</xsl:attribute>
                    </xsl:when>
                </xsl:choose>
                <xsl:apply-templates mode="doc13" />
            </ol>
        </xsl:if>
    </xsl:template>
    <!--
        
        bulletList
    -->
    <xsl:template match="bulletList" mode="doc13">
        <xsl:if test="not(contains(@showOnlyFor,'slides'))">
            <ul>
                <xsl:copy-of select="@class|@id|@style" />
                <!-- FIXME: Remove this as soon as we can have divs in docX -->
                <xsl:choose>
                    <xsl:when test="$noDIVs and ancestor::talk">
                        <xsl:attribute name="class">talk</xsl:attribute>
                    </xsl:when>
                    <xsl:when test="$noDIVs and ancestor::demo">
                        <xsl:attribute name="class">demo</xsl:attribute>
                    </xsl:when>
                    <xsl:when test="$noDIVs and ancestor::comment">
                        <xsl:attribute name="class">comment</xsl:attribute>
                    </xsl:when>
                </xsl:choose>
                <xsl:apply-templates mode="doc13" />
            </ul>
        </xsl:if>
    </xsl:template>
    <!--
        
        Macro for Abstract in body
    -->
    <xsl:template match="Abstract" mode="doc13">
        <abstract>
            <xsl:apply-templates select="/presentation/abstract" mode="doc13" />
        </abstract>
    </xsl:template>
    <!--
        
        list item
    -->
    <xsl:template match="item" mode="doc13">
        <xsl:if test="not(contains(@showOnlyFor,'slides'))">
            <li>
                <xsl:copy-of select="@class|@id|@style" />
                <!-- FIXME: Remove this as soon as we can have divs in docX -->
                <xsl:choose>
                    <xsl:when test="$noDIVs and ancestor::talk">
                        <xsl:attribute name="class">talk</xsl:attribute>
                    </xsl:when>
                    <xsl:when test="$noDIVs and ancestor::demo">
                        <xsl:attribute name="class">demo</xsl:attribute>
                    </xsl:when>
                    <xsl:when test="$noDIVs and ancestor::comment">
                        <xsl:attribute name="class">comment</xsl:attribute>
                    </xsl:when>
                </xsl:choose>
                <xsl:apply-templates mode="doc13" />
            </li>
        </xsl:if>
    </xsl:template>
    <!--
        
        para
    -->
    <xsl:template match="para" mode="doc13">
        <xsl:if test="not(contains(@showOnlyFor,'slides'))">
            <p>
                <xsl:copy-of select="@class|@id|@style" />
                <!-- FIXME: Remove this as soon as we can have divs in docX -->
                <xsl:choose>
                    <xsl:when test="$noDIVs and ancestor::talk">
                        <xsl:attribute name="class">talk</xsl:attribute>
                    </xsl:when>
                    <xsl:when test="$noDIVs and ancestor::demo">
                        <xsl:attribute name="class">demo</xsl:attribute>
                    </xsl:when>
                    <xsl:when test="$noDIVs and ancestor::comment">
                        <xsl:attribute name="class">comment</xsl:attribute>
                    </xsl:when>
                </xsl:choose>
                <xsl:apply-templates mode="doc13" />
            </p>
        </xsl:if>
    </xsl:template>
    <!--
        
        todo
    -->
    <xsl:template match="todo" mode="doc13">
        <xsl:if test="contains($show,local-name())">
            <xsl:choose>
                <!--wrap in divs when allowed, otherwise just pass content-->
                <xsl:when test="not($noDIVs)">
                    <div>
                        <xsl:attribute name="class">
                            <xsl:value-of select="local-name()" />
                        </xsl:attribute>
                        <xsl:copy-of select="@id|@style" />
                        <xsl:value-of select="assignedTo"/>:<xsl:value-of select="doUntil"/>:<xsl:value-of select="priority"/><br/>
                        <xsl:apply-templates mode="doc13" />
                    </div>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates mode="doc13" />
                </xsl:otherwise>
            </xsl:choose>
        </xsl:if>
    </xsl:template>
    <!--
        
        comment,demo,talk
    -->
    <xsl:template match="comment|talk|demo" mode="doc13">
        <xsl:if test="contains($show,local-name())">
            <xsl:choose>
                <!--wrap in divs when allowed, otherwise just pass content-->
                <xsl:when test="not($noDIVs)">
                    <div>
                        <xsl:attribute name="class">
                            <xsl:value-of select="local-name()" />
                        </xsl:attribute>
                        <xsl:copy-of select="@id|@style" />
                        <xsl:apply-templates mode="doc13" />
                    </div>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates mode="doc13" />
                </xsl:otherwise>
            </xsl:choose>
        </xsl:if>
    </xsl:template>
    <!--
        
        addlInfo
    -->
    <xsl:template match="addlInfo" mode="doc13">
        <xsl:if test="contains($show,local-name())">
            <section>
                <xsl:attribute name="class">
                    <xsl:value-of select="normalize-space(concat('addlInfo ', @class))" />
                </xsl:attribute>
                <title>
                    <xsl:choose>
                        <xsl:when test="@title">
                            <xsl:value-of select="@title" />
                        </xsl:when>
                        <xsl:otherwise> Additional Information </xsl:otherwise>
                    </xsl:choose>
                </title>
                <xsl:apply-templates mode="doc13" />
            </section>
        </xsl:if>
    </xsl:template>
    <!--
        
        Elements that can be passed on unchanged
    -->
    <xsl:template match="em|strong|kbd|quote|code|br" mode="doc13">
      
        <xsl:copy>
            <xsl:copy-of select="@class|@id|@style" />
            <xsl:apply-templates mode="doc13" />
        </xsl:copy>
    </xsl:template>
    <!--
        
        Process bare text in items
        and wrap it in paras
    -->
    <xsl:template match="item/text()|cell/text()|talk/text()|demo/text()|comment/text()" mode="doc13">
        <!-- Ignore completely empty pieces of text -->
        <xsl:if test="normalize-space(.)!=''">
            <p>
                <!-- FIXME: Remove this as soon as we can have divs in docX -->
                <xsl:choose>
                    <xsl:when test="$noDIVs and ancestor::talk">
                        <xsl:attribute name="class">talk</xsl:attribute>
                    </xsl:when>
                    <xsl:when test="$noDIVs and ancestor::demo">
                        <xsl:attribute name="class">demo</xsl:attribute>
                    </xsl:when>
                    <xsl:when test="$noDIVs and ancestor::comment">
                        <xsl:attribute name="class">comment</xsl:attribute>
                    </xsl:when>
                </xsl:choose>
                <xsl:value-of select="." />
            </p>
        </xsl:if>
    </xsl:template>
    <!--
        
        Catch unblocked elements
    -->
    <xsl:template match="node()|text()|comment()">
        <xsl:message>Ungeblocktes Elemente <xsl:value-of select="." /></xsl:message>
    </xsl:template>
    <xsl:template match="processing-instruction()" />
</xsl:stylesheet>
