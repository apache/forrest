<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

   <xsl:output method = "text" />

	<xsl:template match="graph">
      digraph "<xsl:value-of select="@name" />" {
      
      <xsl:text>
      ranksep=1.0; nodesep=0.5;
      node [color="grey90", style="filled"]
      edge [color="grey70"]
      <!-- edge .antcall [label="antcall", fontcolor="gray70", fontsize" value="9"]
      node .default [color="pink"] -->
      </xsl:text>
         
       <xsl:apply-templates />
      }
	</xsl:template>

	<xsl:template match="attributes">
	 <xsl:value-of select="@type" /> [  
       <xsl:apply-templates select="attribute"/>
      ];
	</xsl:template>
	
	<xsl:template match="attribute">
	 "<xsl:value-of select="@name" />"="<xsl:value-of select="@value" />",
	</xsl:template>
		
	<xsl:template match="node">
	   "<xsl:value-of select="@id" />";
	</xsl:template>
	
	<xsl:template match="edge">
	  "<xsl:value-of select="@from" />" -> "<xsl:value-of select="@to" />";
	</xsl:template>

	<xsl:template match="subgraph">
  	 subgraph 
  	   <if select="@numcluster">
  	             "cluster:<xsl:value-of select="@numcluster" />"
  	          </if>
  	   {
  	      style="filled";
  	      color="grey95";
  	      label="<xsl:value-of select="@label" />";
         <xsl:apply-templates />  	     
       }
	</xsl:template>
   
</xsl:stylesheet>