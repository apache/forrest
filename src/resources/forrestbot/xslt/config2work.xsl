<?xml version='1.0'?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                              xmlns:xalan="http://xml.apache.org/xalan"
                              exclude-result-prefixes="xalan">

<!-- this stylesheet generates an Ant build file (work.build.xml) from the forrestbot
     configuration (forrestbot.conf.xml) -->

<xsl:output indent="yes"/>

<!-- retrieving the list of stages from stages.conf.xml -->
<xsl:variable name="workstages" select="xalan:nodeset(document('../stages.conf.xml'))"/>



<xsl:template match="forrest-config">
  <project basedir="." default="work" name="generated forrestbot build file">
    <xsl:call-template name="listprojects"/>
    <xsl:call-template name="startproject"/>
    <xsl:call-template name="liststagesperproject"/>
   </project>
</xsl:template>



<xsl:template name="listprojects">

<!--
  per-project invocation of the chain of workstages:
  (for each project element in the input there is a child to the <parallel>
   element in the output)

  <target name="work">
    <parallel>
      <antipede-trycatch>
        <try>
          <ant antfile="[this-file]" target="work.[project-name]" .../>
        </try>
        <catch>
          <...>
        </catch>
        <finally>
          <mail .../>
        </finally>
    </parallel>
  </target>
-->

   <target name="work">
     <parallel>
       <xsl:for-each select="project">
         <!-- todo: wrap it in try catch to make it send in case of fail as well -->
         <antipede-trycatch>
           <try>
             <ant antfile="${{bot.work.build.xml}}" inheritRefs="true"
                  target="work.{@name}" output="${{bot.build.dir}}/work.{@name}.log"/>
             <property name="completion-status.{@name}" value="SUCCESS" />
           </try>
           <catch>
             <echo message="Failed to complete workstages for project {@name}" />
             <property name="completion-status.{@name}" value="FAIL" />
           </catch>
           <finally>
             <xsl:if test="@sendlogto">
               <mail from="forrest-bot@xml.apache.org"
                     mailhost="${{mailhost}}"
                     tolist="{@sendlogto}"
                     subject="[DO NOT REPLY] ForrestBot site builder for '{@name}'"
                     files="${{bot.build.dir}}/work.{@name}.log"
                     failonerror="false">
                 <message>
Another hard day at the BOT factory, and quite happy to have worked for you.
Our completion status for your project [<xsl:value-of select="@name" />] was ${completion-status.<xsl:value-of select="@name"/>}
Please find the details in the log attached.

The Forrest-Bot team. (http://xml.apache.org/forrest)
                 </message>
               </mail>
             </xsl:if>
           </finally>
         </antipede-trycatch>
       </xsl:for-each>
     </parallel>
   </target>
</xsl:template>



<xsl:template name="startproject">

<!--
  list dependency chain of worker task:
  <target name="work.xml-forrest"
          depends="prepare.xml-forrest,
            get-src.xml-forrest,generate.xml-forrest,
            deploy.xml-forrest,cleanup.xml-forrest" />
-->

  <xsl:for-each select="project">
    <xsl:variable name="project.name" select="@name"/>
    <target name="work.{@name}">
      <xsl:attribute name="depends">
         <xsl:for-each select="$workstages/stages/workstage">
           <xsl:value-of select="concat(@name, '.', $project.name)"/>
           <xsl:if test="not(position() = last())">,</xsl:if>
         </xsl:for-each>
      </xsl:attribute>
    </target>
  </xsl:for-each>
</xsl:template>



<xsl:template name="liststagesperproject">

<!--
  for each project, construct the different workstages
  and add correct dependencies:

  <target name="prepare.xml-forrest">
    <property ... />
    ...
    <xmlproperty ... (defaults, to be overloaded eventually) />
    <ant ... target=" (project/workstage) " />
  </target>
  <target name="get-src.xml-forrest" depends="prepare.xml-forrest">
    ...
  </target>
-->

  <xsl:for-each select="/forrest-config/project">
    <xsl:variable name="projectnode" select="."/>
    <xsl:for-each select="$workstages/stages/workstage">
      <xsl:variable name="workstagenode" select="."/>
      <target name="{$workstagenode/@name}.{$projectnode/@name}">
        <xsl:if test="preceding::workstage[1]">
          <xsl:attribute name="depends">
            <xsl:value-of select="concat(preceding::workstage[1]/@name,'.',$projectnode/@name)"/>
          </xsl:attribute>
        </xsl:if>
        <xsl:call-template name="insertprojectnodeprops">
          <xsl:with-param name="projectnode" select="$projectnode"/>
        </xsl:call-template>
        <xmlproperty file="${{bot.default.parameters.xml}}" keepRoot="false"/>
        <xsl:call-template name="insertantcalltotemplate">
          <xsl:with-param name="projectnode" select="$projectnode"/>
          <xsl:with-param name="workstagenode" select="$workstagenode"/>
        </xsl:call-template>
      </target>
    </xsl:for-each>
  </xsl:for-each>
</xsl:template>

<xsl:template name="insertprojectnodeprops">
  <xsl:param name="projectnode"/>
  <xsl:for-each select="$projectnode/*/*">
    <property>
      <xsl:attribute name="name">
        <!-- watching out for <generate skin=""/> -->
        <xsl:value-of select="name(..)"/>
        <xsl:if test="../@type">.<xsl:value-of select="../@type"/></xsl:if>
        <xsl:text>.</xsl:text>
        <xsl:value-of select="name()"/>
      </xsl:attribute>
      <xsl:attribute name="value">
        <xsl:value-of select="@name"/>
      </xsl:attribute>
    </property>
  </xsl:for-each>
</xsl:template>

<xsl:template name="insertantcalltotemplate">
  <xsl:param name="workstagenode"/>
  <xsl:param name="projectnode"/>
  <ant antfile="${{bot.templates.build.xml}}" inheritRefs="true">
    <xsl:attribute name="target">
      <xsl:text>template.</xsl:text>
      <xsl:value-of select="$workstagenode/@name"/>
      <xsl:if test="$projectnode/*[name()=$workstagenode/@name][@type]">
        <xsl:text>.</xsl:text>
        <xsl:value-of select="$projectnode/*[name()=$workstagenode/@name]/@type"/>
      </xsl:if>
    </xsl:attribute>
    <property name="project.name" value="{$projectnode/@name}"/>
  </ant>
</xsl:template>

</xsl:stylesheet>
