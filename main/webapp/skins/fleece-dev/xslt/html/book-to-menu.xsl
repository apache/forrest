<?xml version="1.0"?>
<!--
book-to-menu.xsl generates the HTML menu.    See the imported book-to-menu.xsl for
details.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:import href="lm://transform.skin.common.html.book-to-menu"/>

    <xsl:template match="book">
        <div id="menu">
            <ul>
                <xsl:apply-templates select="menu"/>
            </ul>
        </div>
    </xsl:template>

    <xsl:template match="menu">
        <li>
            <h1>
                <xsl:value-of select="@label"/>
            </h1>
            <ul>
                <xsl:apply-templates/>
            </ul>
        </li>
    </xsl:template>

    <xsl:template match="menu-item[@type='hidden']"/>

    <xsl:template match="menu-item">
        <li><xsl:apply-imports/></li>
    </xsl:template>

    <xsl:template name="selected">
        <div class="current">
            <xsl:value-of select="@label"/>
        </div>
    </xsl:template>

    <xsl:template name="print-external">
        <font color="#ffcc00">
            <xsl:apply-imports/>
        </font>
    </xsl:template>

</xsl:stylesheet>
