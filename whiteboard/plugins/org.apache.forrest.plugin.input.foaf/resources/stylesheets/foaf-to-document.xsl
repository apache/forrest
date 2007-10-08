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
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:foaf="http://xmlns.com/foaf/0.1/"
    xmlns:wot="http://xmlns.com/wot/0.1/"
    xmlns:doap="http://usefulinc.com/ns/doap#"
    xmlns:pm="http://www.web-semantics.org/ns/pm#"
    exclude-result-prefixes="rdf dc foaf">

    <xsl:template match="/">
        <document>
            <xsl:call-template name="header" />
            <body>
                <xsl:apply-templates select="rdf:RDF/foaf:Person" />
            </body>
        </document>
    </xsl:template>

    <xsl:template match="foaf:Person">
        <section>
            <title>
                <xsl:value-of select="foaf:name" />
            </title>

            <xsl:apply-templates select="foaf:depiction" />

                <table>
                    <tr>
                        <th>Property</th>
                        <th>Value</th>
                    </tr>    
                    <tr>
                        <td>Name</td>
                        <td>
                            <xsl:choose>
                                <xsl:when test="foaf:name">
                                    <xsl:value-of select="foaf:name" />
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of
                                        select="foaf:firstName" />
                                    <xsl:text></xsl:text>
                                    <xsl:value-of select="foaf:surname" />
                                </xsl:otherwise>
                            </xsl:choose>
                        </td>
                    </tr>
                    
                    <xsl:apply-templates select="*[not(local-name(.) = 'depiction') and not(local-name(.) = 'knows') and not(local-name(.) = 'holdsAccount')  and not(local-name(.) = 'currentProject')]"/>
                </table>
                    
                <xsl:if test="foaf:currentProject">
                  <section>
                    <title>Current Project(s)</title>
                    <ul>
                      <xsl:apply-templates select="foaf:currentProject"/>
                    </ul>
                  </section>
                </xsl:if>
                    
                <xsl:if test="foaf:holdsAccount">
                  <section>
                    <title>Accounts</title>
                    <table>
                      <xsl:apply-templates select="foaf:holdsAccount"/>
                    </table>
                  </section>
                </xsl:if>
                    
                <xsl:if test="foaf:knows">
                  <section>
                    <title>Knows</title>
                    <table>
                      <xsl:apply-templates select="foaf:knows"/>
                    </table>
                  </section>
                </xsl:if>

        </section>
    </xsl:template>

    <xsl:template name="header">
        <header>
            <title>Person details</title>
        </header>
    </xsl:template>
    
    <xsl:template match="foaf:name">
    </xsl:template>
    
    <xsl:template match="foaf:firstname">
    </xsl:template>
    
    <xsl:template match="foaf:surname">
    </xsl:template>

    <xsl:template match="foaf:mbox_sha1sum">
        <tr>
            <td>Mbox SH1Sum</td>
            <td>
                <xsl:value-of select="." />
            </td>
        </tr>
    </xsl:template>

    <xsl:template match="foaf:mbox">
        <tr>
            <td>Mbox</td>
            <td>
              <xsl:apply-templates select="@rdf:resource"/>
            </td>
        </tr>
    </xsl:template>
    
    <xsl:template match="@rdf:resource">
      <xsl:choose>
          <xsl:when test="starts-with(., 'mailto:') or starts-with(., 'http:')">
            <link href="{.}">
                <xsl:value-of select="." />
            </link>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="." />
          </xsl:otherwise>
      </xsl:choose>
    </xsl:template>

    <!--  FIXME: This is a bit of a hack. Different people use different
          ways of capturing project details. We need to refactor this to
          make it work regardless of how they express this data. -->
    <xsl:template match="foaf:currentProject">
      <xsl:choose>
        <xsl:when test="@dc:title">
          <li>
              <link href="{@rdf:resource}">
                <xsl:value-of select="@dc:title" />
              </link>
              <xsl:value-of select="@rdfs:comment" />
          </li>
        </xsl:when>
        <xsl:when test="doap:Project">
          <li>
            <link href="{doap:Project/pm:homepage/@rdf:resource}">
              <xsl:value-of select="doap:Project/pm:name" />
            </link>
          </li>
        </xsl:when>
      </xsl:choose>
    </xsl:template>

    <xsl:template match="foaf:publications">
            <tr>
                <td>
                    <xsl:value-of select="./@dc:title" />
                </td>
                <td>
                    <link href="{./@rdf:resource}">
                        <xsl:value-of select="./@rdf:resource" />
                    </link>
                </td>
                <td>
                    <xsl:value-of select="./@dc:date" />
                </td>
            </tr>
    </xsl:template>

    <xsl:template match="foaf:knows">
        <xsl:for-each select="foaf:Person">
            <tr>
                <td>
                    <xsl:choose>
                        <xsl:when test="foaf:name">
                            <xsl:value-of select="foaf:name" />
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="foaf:firstName" />
                            <xsl:text></xsl:text>
                            <xsl:value-of select="foaf:surname" />
                        </xsl:otherwise>
                    </xsl:choose>
                </td>
                <td>
                    <xsl:choose>
                        <xsl:when test="foaf:mbox_sha1sum">
                            <xsl:value-of select="foaf:mbox_sha1sum" />
                        </xsl:when>
                        <xsl:when test="foaf:mbox">
                          <xsl:apply-templates select="foaf:mbox/@rdf:resource"/>
                        </xsl:when>
                        <xsl:otherwise>
                          No MBox specified
                        </xsl:otherwise>
                    </xsl:choose>
                </td>
            </tr>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="foaf:depiction">
        <p>
            <img src="{@rdf:resource}" height="120"
                border="0" alt="depiction"/>
        </p>
    </xsl:template>

    <xsl:template match="@dc:format"/>

    <xsl:template match="@rdf:ID">
        <tr>
            <td>Id</td>
            <td>
                <xsl:value-of select="." />
            </td>
        </tr>
    </xsl:template>

    <xsl:template match="foaf:nick">
        <tr>
            <td>Nick</td>
            <td>
                <xsl:value-of select="." />
            </td>
        </tr>
    </xsl:template>

    <xsl:template match="foaf:phone">
        <tr>
            <td>Phone</td>
            <td>
              <xsl:apply-templates select="@*"/>
            </td>
        </tr>
    </xsl:template>

    <xsl:template match="foaf:homepage">
        <tr>
            <td>Homepage</td>
            <td>
              <xsl:apply-templates select="@*"/>
            </td>
        </tr>
    </xsl:template>

    <xsl:template match="foaf:workplaceHomepage">
        <tr>
            <td>Work Place Homepage</td>
            <td>
              <xsl:apply-templates select="@*"/>
            </td>
        </tr>
    </xsl:template>

    <xsl:template match="foaf:holdsAccount">
      <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="foaf:OnlineAccount">
        <xsl:choose>
            <xsl:when test="foaf:accountServiceHomepage">
              <tr>
                <td>                  
                  <xsl:apply-templates select="foaf:homepage/@rdf:resource"/>
                </td>
                <td>
                  <xsl:if test="foaf:seeAlso">
                      <link href="{foaf:seeAlso/@rdf:resource}">
                        <xsl:value-of select="foaf:seeAlso/@dc:format" />
                      </link>
                  </xsl:if>
                </td>
              </tr>
            </xsl:when>
            <xsl:otherwise>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="foaf:schoolHomepage">
        <tr>
            <td>School Homepage</td>
            <td>
              <xsl:apply-templates select="@*"/>
            </td>
        </tr>
    </xsl:template>

    <xsl:template match="foaf:*">
            <tr>
                <td>
                    <xsl:value-of select="local-name()" />
                </td>
                <td>
                    <xsl:choose>
                      <xsl:when test="text()">
                        <xsl:value-of select="." />
                      </xsl:when>
                      <xsl:when test="@rdf:resource">
                        <xsl:apply-templates select="@rdf:resource"/>
                      </xsl:when>
                    </xsl:choose>
                </td>
            </tr>
    </xsl:template>
    
    <xsl:template match="*"/>
</xsl:stylesheet>
