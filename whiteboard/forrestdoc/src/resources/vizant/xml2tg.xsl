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
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method = "xml" />
  <xsl:template match="graph">
    <TOUCHGRAPH_LB version="1.20">
      <NODESET>
        <xsl:apply-templates select="node"/>
      </NODESET>
      <EDGESET>
        <xsl:apply-templates select="edge" />
      </EDGESET>
      <PARAMETERS>
        <PARAM name="offsetX" value="627"/>
        <PARAM name="rotateSB" value="0"/>
        <PARAM name="zoomSB" value="-7"/>
        <PARAM name="offsetY" value="19"/>
      </PARAMETERS>
    </TOUCHGRAPH_LB>
  </xsl:template>
<!--
	<xsl:template match="attributes">
	 <h3><xsl:value-of select="@type" /></h3> 
       <xsl:apply-templates select="attribute"/>
	</xsl:template>
	
	<xsl:template match="attribute">
	  <xsl:value-of select="@name" />"="<xsl:value-of select="@value" /><br />
	</xsl:template>
-->
  <xsl:template match="node">
    <NODE>
      <xsl:attribute name="nodeID">
        <xsl:value-of select="@id"/>
      </xsl:attribute>
      <NODE_LOCATION x="534" y="87" visible="true"/>
      <NODE_LABEL shape="2" backColor="00A0F0" textColor="FFFFFF" fontSize="18">
        <xsl:attribute name="label">
          <xsl:value-of select="@id"/>
        </xsl:attribute>
      </NODE_LABEL>
      <NODE_URL url="" urlIsLocal="false" urlIsXML="false"/>
      <NODE_HINT width="150" height="-1" isHTML="false">
<!-- the description -->
        <xsl:attribute name="hint">
          <xsl:value-of select="@id"/>
        </xsl:attribute>
      </NODE_HINT>
    </NODE>
  </xsl:template>
  <xsl:template match="edge">
    <EDGE type="1" length="80" visible="true" color="A0A0A0">
      <xsl:attribute name="fromID">
        <xsl:value-of select="@from"/>
      </xsl:attribute>
      <xsl:attribute name="toID">
        <xsl:value-of select="@to"/>
      </xsl:attribute>
    </EDGE>
  </xsl:template>
  <xsl:template match="subgraph">
    <NODE>
      <xsl:attribute name="nodeID">
        <xsl:value-of select="@label"/>
      </xsl:attribute>
      <NODE_LOCATION x="534" y="87" visible="true"/>
      <NODE_LABEL shape="2" backColor="00A0F0" textColor="FFFFFF" fontSize="18">
        <xsl:attribute name="label">
          <xsl:value-of select="@label"/>
        </xsl:attribute>
      </NODE_LABEL>
      <NODE_URL url="" urlIsLocal="false" urlIsXML="false"/>
      <NODE_HINT width="150" height="-1" isHTML="false">
        <xsl:attribute name="hint">subgraph:cluster:<xsl:value-of select="@numcluster"/>:<xsl:value-of select="@label"/>
        </xsl:attribute>
      </NODE_HINT>
    </NODE>
    <xsl:apply-templates />
  </xsl:template>
</xsl:stylesheet>
