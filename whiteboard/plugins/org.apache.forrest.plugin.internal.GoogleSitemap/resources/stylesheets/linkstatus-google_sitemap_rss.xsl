<?xml version="1.0" encoding="UTF-8"?>
<!--the input for this stylesheet is the Cocoon LinkStatusGenerator XML output-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:java="java" exclude-result-prefixes="java">
	<xsl:output method="xml" indent="yes" />
	<xsl:param name="request-root"/>
	<xsl:param name="site-root"/>
	<xsl:variable name="version.rss" select="'2.0'"/>
	
	<xsl:template match="/">
		<rss version="$version.rss">
			<channel>
				<!--TODO: these need to be configurable-->
				<title>Sitemap For a Site Built with Forrest</title>
				<link><xsl:value-of select="$site-root"/></link>
				<description>Sitemap file in RSS format for an Apache Forrest Site</description>
				<xsl:apply-templates select="//*[boolean(@href)]"/>
			</channel>
		</rss>
	</xsl:template>
	
	<xsl:template match="//*[boolean(@href)]">
		<item>
			<link>
				<xsl:variable name="relativeURI" select="substring-after(@href, $request-root)"/>
				<xsl:choose>
					<!--used when generating a static site and the site uri needs to replace the local uri-->
					<xsl:when test="string-length($site-root) &gt; 1 and $site-root != 'null'">
						<xsl:value-of select="concat($site-root, $relativeURI)"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="@href"/>
					</xsl:otherwise>
				</xsl:choose>
			</link>
			
			<pubDate>
			<!--
			TODO-Replace: this is obviously a HACK, hopefully one day this will be provided from the LinkStatusGenerator xml ouput
			which is the input for this stylsheet. We should then take that result and format it like 'Sun, 19 May 2002 15:21:36 GMT'
			per the RSS spec for the "pubDate" element
			-->
				<xsl:value-of select="java:util.Date.new()"/>
			</pubDate>
		</item>
	</xsl:template>
	
	<!--do nothing with text nodes-->
	<xsl:template match="*/text()"/>

</xsl:stylesheet>

