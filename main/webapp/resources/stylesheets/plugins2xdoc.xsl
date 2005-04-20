<?xml version="1.0" encoding="iso-8859-1"?>
<!--
	Copyright 2002-2005 The Apache Software Foundation or its licensors,
	as applicable.
	
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
	http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">

	<xsl:template match="/plugins">
		<document>
			<header>
				<title>Plugins Index</title>
			</header>
			<body>
				<section>
					<title>Plugins Index</title>
					<p>
						Plugins are a way of extending Forrest to
						satisfy site-specific needs. For more
						information on plugins see
						<a href="usingPlugins.html">
							Extending Forrest with Plugins
						</a>
						.
					</p>
					<p>
						This page lists all plugins that have been
						recognised by the Apache Forrest project. Any
						plugins on this page have been verified as
						working in the versions of Forrest specified
						here.
					</p>
					<p class="note">
						If you have created a plugin that is not listed
						here please let us know.
					</p>

					<section>
						<title>Input Plugins</title>
						<p>
							Input plugins enable Forrest to work with
							source documents in different formats.
						</p>
						<xsl:apply-templates
							select="plugin[@type='input']">
							<xsl:sort select="@name" />
						</xsl:apply-templates>
					</section>

					<section>
						<title>Output Plugins</title>
						<p>
							Output plugins enable Forrest to produce
							documents in different formats.
						</p>
						<xsl:apply-templates
							select="plugin[@type='output']">
							<xsl:sort select="@name" />
						</xsl:apply-templates>
					</section>

					<section>
						<title>Internal Plugins</title>
						<p>
							Internal plugins change the core behaviour
							of Forrest.
						</p>
						<xsl:apply-templates
							select="plugin[@type='internal']">
							<xsl:sort select="@name" />
						</xsl:apply-templates>
					</section>
				</section>
			</body>
		</document>
	</xsl:template>

	<xsl:template match="plugin">
		<section>
			<title>
				<xsl:value-of select="@name" />
			</title>
			<table>
				<tr>
					<th width="25%">Description</th>
					<td>
						<xsl:choose>
							<xsl:when test="description/p">
								<xsl:apply-templates
									select="description" />
							</xsl:when>
							<xsl:otherwise>
								<p>
									<xsl:apply-templates
										select="description" />
								</p>
							</xsl:otherwise>
						</xsl:choose>
					</td>
				</tr>

				<tr>
					<th width="25%">Author</th>
					<td>
						<xsl:value-of select="@author" />
					</td>
				</tr>

				<tr>
					<th width="25%">Website</th>
					<td>
						<a>
							<xsl:attribute name="href">
								<xsl:value-of select="@website" />
							</xsl:attribute>
							<xsl:value-of select="@website" />
						</a>
					</td>
				</tr>

				<tr>
					<th width="25%">Download URL:</th>
					<td>
						<a>
							<xsl:attribute name="href">
								<xsl:value-of select="@url" />
							</xsl:attribute>
							<xsl:value-of select="@url" />
						</a>
					</td>
				</tr>

				<tr>
					<th width="25%">Plugin version</th>
					<td>
						<xsl:value-of select="@version" />
					</td>
				</tr>

				<tr>
					<th width="25%">
						Minimum Forrest version required
					</th>
					<td>
						<xsl:value-of select="forrestVersion" />
					</td>
				</tr>
			</table>
		</section>
	</xsl:template>
</xsl:stylesheet>