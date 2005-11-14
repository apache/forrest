<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" extension-element-prefixes="datetime
    redirect" xmlns:datetime="http://exslt.org/dates-and-times" xmlns:redirect="http://xml.apache.org/xalan/redirect">
    <xsl:output doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
        doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" encoding="ISO-8859-1" indent="yes"
        method="html" />
    <!-- 
        
        Parameters 
    
    -->
    <!--space separated list of all special elements to be visible in target (talk demo addlInfo comment todo) -->
    <xsl:param name="show"> </xsl:param>
    <xsl:template match="/">
        <xsl:apply-templates mode="slidy" />
    </xsl:template>
    <!-- 
        
        Main processing template
    -->
    <xsl:template match="presentation" mode="slidy">
        <html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
            <head>
                <xsl:apply-templates select="metadata" mode="slidy" />
            </head>
            <xsl:apply-templates select="content" mode="slidy" />
        </html>
    </xsl:template>
    <!-- 
        
        Process metadata
    -->
    <xsl:template match="metadata" mode="slidy">
        <title>
            <xsl:value-of select="title" />
        </title>
        <link rel="stylesheet" type="text/css" media="screen, projection" href="ui/show.css" />
        <script src="ui/slidy.js" type="text/javascript" />
        <style type="text/css">
            <!-- your custom style rules -->
        </style>
        <link rel="stylesheet" type="text/css" media="print" href="ui/print.css" />
        <meta name="copyright">
            <xsl:attribute name="content">
                <xsl:value-of select="author" />
                <xsl:text> </xsl:text>
                <xsl:value-of select="datetime:formatDate(created,'yyyy')" />
            </xsl:attribute>
        </meta>
    </xsl:template>
    <!-- 
        
        Generate visible content
    -->
    <xsl:template match="content" mode="slidy">
        <body>
            <xsl:attribute name="class">
                <xsl:value-of select="normalize-space(concat('slidyPresentation ', @class))" />
            </xsl:attribute>
            <xsl:copy-of select="@id|@style" />
            <!-- Background for all pages -->
            <div class="background">
                <img id="head-logo" alt="graphic with four colored squares" style="float:right"
                    src="resources/ssplogo.png" />
            </div>
            <!-- Background for cover page -->
            <div class="background cover" style="background-color: white"> </div>
            <div class="background chapterTitle" style="background-color: yellow"> </div>
            <xsl:apply-templates mode="slidy" />
        </body>
    </xsl:template>
    <!--
        
        Process chapter start
    -->
    <xsl:template match="chapter" mode="slidy">
        <xsl:if test="not(contains(@showOnlyFor,'print'))">
            <xsl:apply-templates mode="slidy" />
        </xsl:if>
    </xsl:template>
    <!--
        
        Process module (no processing at all)
    -->
    <xsl:template match="module" mode="slidy">
        <xsl:if test="not(contains(@showOnlyFor,'print'))">
            <xsl:apply-templates mode="slidy" />
        </xsl:if>
    </xsl:template>
    <!--
        
        Process page
    -->
    <xsl:template match="page" mode="slidy">
        <xsl:if test="not(contains(@showOnlyFor,'print'))">
            <div>
                <xsl:attribute name="class">
                    <xsl:value-of select="normalize-space(concat('slide ', @type))" />
                </xsl:attribute>
                <xsl:choose>
                    <xsl:when test="heading">
                        <h1>
                            <xsl:copy-of select="heading/@*" />
                            <xsl:apply-templates mode="slidyHeading" select="heading" />
                        </h1>
                    </xsl:when>
                    <xsl:otherwise>
                        <h1>
                            <xsl:copy-of select="@*" />
                            <xsl:apply-templates mode="slidy" select="@title" />
                        </h1>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:apply-templates mode="slidy" />
            </div>
        </xsl:if>
    </xsl:template>
    <!--
        
        Special element TOC as a macro for a Table of content
    -->
    <xsl:template match="TOC" mode="slidy">
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
    <xsl:template match="chapterTOC" mode="slidy">
        <!-- List of pages -->
        <ul>
            <xsl:for-each select="ancestor::                 page/following-sibling::page[@listIn='allTOCs' or
                @listIn='chapterTOC']|ancestor::                 page/following-sibling::chapter">
                <li>
                    <xsl:value-of select="@title" />
                </li>
            </xsl:for-each>
        </ul>
    </xsl:template>
    <!--
        
        Special element PresentationTitle as a macro for a Table of content
    -->
    <xsl:template match="PresentationTitle" mode="slidy">
        <xsl:value-of select="/presentation/metadata/title" />
    </xsl:template>
    <!--
        
        Special element Presenters as a macro for a Table of content
    -->
    <xsl:template match="Presenters" mode="slidy">
        <xsl:for-each select="/presentation/metadata/presenter">
            <p class="Presenter">
                <xsl:value-of select="fullName" />
                <br />
                <a href="mailto:{email}">
                    <xsl:value-of select="email" />
                </a>
            </p>
        </xsl:for-each>
    </xsl:template>
    <!--
        
        Heading
        block processing
    -->
    <xsl:template match="heading" mode="slidyHeading">
        <xsl:apply-templates mode="slidy" />
    </xsl:template>
    <!--
        
        Heading
        block processing
    -->
    <xsl:template match="heading" mode="slidy" />
    <!--
        
        section
    -->
    <xsl:template match="section" mode="slidy">
        <xsl:if test="not(contains(@showOnlyFor,'print'))">
            <xsl:element name="{concat('h',count(ancestor::section)+1)}">
                <xsl:copy-of select="@class|@id|@style" />
                <xsl:value-of select="@title" />
            </xsl:element>
            <xsl:apply-templates mode="slidy" />
        </xsl:if>
    </xsl:template>
    <!--
        
        XRef
    -->
    <xsl:template match="xref" mode="slidy">
        <xsl:if test="not(contains(@showOnlyFor,'print'))">
            <a>
                <xsl:copy-of select="@class|@id|@style" />
                <xsl:attribute name="href">
                    <xsl:value-of select="@url" />
                </xsl:attribute>
                <xsl:apply-templates mode="slidy" />
            </a>
        </xsl:if>
    </xsl:template>
    <!--
        
        Image
    -->
    <xsl:template match="image" mode="slidy">
        <xsl:if test="not(contains(@showOnlyFor,'print'))">
            <img>
                <xsl:copy-of select="@class|@id|@style" />
                <xsl:attribute name="src">
                    <xsl:value-of select="@url" />
                </xsl:attribute>
                <xsl:attribute name="alt">
                    <xsl:value-of select="@alternativeText" />
                </xsl:attribute>
            </img>
        </xsl:if>
    </xsl:template>
    <!--
        
        numberedList
    -->
    <xsl:template match="numberedList" mode="slidy">
        <xsl:if test="not(contains(@showOnlyFor,'print'))">
            <ol>
                <xsl:copy-of select="@class|@id|@style" />
                <xsl:apply-templates mode="slidy" />
            </ol>
        </xsl:if>
    </xsl:template>
    <!--
        
        bulletList
    -->
    <xsl:template match="bulletList" mode="slidy">
        <xsl:if test="not(contains(@showOnlyFor,'print'))">
            <ul>
                <xsl:copy-of select="@class|@id|@style" />
                <xsl:apply-templates mode="slidy" />
            </ul>
        </xsl:if>
    </xsl:template>
    <!--
        
        list item
    -->
    <xsl:template match="item" mode="slidy">
        <xsl:if test="not(contains(@showOnlyFor,'print'))">
            <li>
                <xsl:copy-of select="@class|@id|@style" />
                <xsl:apply-templates mode="slidy" />
            </li>
        </xsl:if>
    </xsl:template>
    <!--
        
        para
    -->
    <xsl:template match="para" mode="slidy">
        <xsl:if test="not(contains(@showOnlyFor,'print'))">
            <p>
                <xsl:copy-of select="@class|@id|@style" />
                <xsl:apply-templates mode="slidy" />
            </p>
        </xsl:if>
    </xsl:template>
    <!--
        
        todo
    -->
    <xsl:template match="todo" mode="slidy">
        <xsl:if test="contains($show,local-name())">
            <div>
                <xsl:attribute name="class">
                    <xsl:value-of select="local-name()" />
                </xsl:attribute>
                <xsl:copy-of select="@id|@style" />
                <xsl:value-of select="assignedTo" />:<xsl:value-of select="doUntil" />:<xsl:value-of select="priority" /><br />
                <xsl:apply-templates mode="doc13" />
            </div>
        </xsl:if>
    </xsl:template>
    <!--
        
        comment, talk, demo
    -->
    <xsl:template match="comment|talk|demo" mode="slidy">
        <xsl:if test="contains($show,local-name())">
            <div>
                <xsl:copy-of select="@id|@style" />
                <xsl:attribute name="class">
                    <xsl:value-of select="local-name()" />
                </xsl:attribute>
                <xsl:apply-templates mode="slidy" />
            </div>
        </xsl:if>
    </xsl:template>
    <!--
        
        addlInfo
    -->
    <xsl:template match="addlInfo" mode="slidy">
        <xsl:if test="contains($show,local-name())">
            <xsl:variable name="NoteFileName" select="concat('addlInfo/',generate-id(..),'.html')" />
            <a href="{$NoteFileName}">
                <xsl:attribute name="class">
                    <xsl:value-of select="normalize-space(concat('additionalInfoRef ', @class))" />
                </xsl:attribute>
                <img src="resources\notes.png" alt="Click here to open additional info ..." />
            </a>
            <redirect:write select="$NoteFileName">
                <html>
                    <body>
                        <xsl:apply-templates mode="slidy" />
                    </body>
                </html>
            </redirect:write>
        </xsl:if>
    </xsl:template>
    <!--

        Elements that can be passed on unchanged
    -->
    <xsl:template match="em|strong|kbd|quote|code|br" mode="slidy">
        <xsl:copy>
            <xsl:copy-of select="@class|@id|@style" />
            <xsl:apply-templates mode="slidy" />
        </xsl:copy>
    </xsl:template>
    <!--
        
        Process bare text in items
        and wrap it in paras
    -->
    <xsl:template match="item/text()|cell/text()" mode="slidy">
        <!-- Ignore completely empty pieces of text -->
        <xsl:if test="normalize-space(.)!=''">
            <p>
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
