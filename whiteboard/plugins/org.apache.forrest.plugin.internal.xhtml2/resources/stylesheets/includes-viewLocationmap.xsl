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
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:collection="http://apache.org/cocoon/collection/1.0"
  xmlns:xi="http://www.w3.org/2001/XInclude"
  xmlns:dyn="http://exslt.org/dynamic">
  
  <xsl:param name="defaultView" select="'default.fv'"/>
  <xsl:param name="rootElement" select="'/xdocs/'"/>
  <xsl:param name="viewExtension" select="'.fv'"/>
  <xsl:param name="path" select="'.'"/>
  <xsl:param name="root" select="'xdocs'"/>
  <xsl:param name="viewFallback" select="'resources/views/default.fv'"/>
  <xsl:variable name="lastFallback" select="boolean(/xdocs/*[local-name()=$defaultView])"/>
   <!--
    *viewSelector* project-xdocs
    will return which view is responsible for the requested path.
    If no view (choice|fallback) could be found the template will return the 
    viewFallback (resources/views/default.fv).
    
    ex.: 
    1.request: index 
    First choice: index.fv
    First/last fallback: default.fv
    
    2.request: sample/index 
    First choice: sample/index.fv
    First fallback: sample/default.fv
    Last fallback: default.fv
    
    3.request: sample/subdir/index 
    First choice: sample/subdir/index.fv
    First fallback: sample/subdir/default.fv
    Second fallback: sample/default.fv
    Last fallback: default.fv
    
    ...
    
    des.: 
    The parent view (root-view) inherits to its children until 
    a child is overriding this view. This override can be used for 
    directories (default.fv) and/or files (*.fv).
    That means that the root view is the default view as long no other
    view can be found in the requested child.
    -->
  <xsl:template match="/">
    <!--<xsl:comment>
      lastFallback: <xsl:value-of select="$lastFallback"/>
      root: <xsl:value-of select="$root"/>
      request: <xsl:value-of select="$path"/>
      default view: <xsl:value-of select="$defaultView"/>
      function-available('dyn:evaluate') <xsl:value-of select="function-available('dyn:evaluate')"/>
    </xsl:comment>-->
    <xsl:variable name="view2response">
      <xsl:call-template name="viewSelector">
	      <xsl:with-param name="request" select="concat($path,$viewExtension)"/>
	      <xsl:with-param name="rest" select="''"/>
	    </xsl:call-template>
    </xsl:variable>
    <!--Nothing can be found, returning the default.fv-->
    <xsl:if test="not($lastFallback) and $view2response=''">
      <xi:include href="{$viewFallback}"/>
    </xsl:if>
    <!--The lastFallback can be found, returning this-->
    <xsl:if test="$lastFallback and $view2response=''">
      <xi:include href="{concat('file://', $root,$defaultView)}"/>
    </xsl:if>
    <!--Some fallback can be found that is not the last one-->
    <xsl:if test="$view2response!=''">
      <xi:include href="{concat('file://', $root,$view2response)}"/>
    </xsl:if>
  </xsl:template>
 
  <xsl:template name="viewSelector">
    <xsl:param name="request" select="''"/>
    <xsl:param name="rest" select="''"/>
    <xsl:param name="view2response" select="''"/>
    <xsl:if test="contains($request,'/')">
      <xsl:variable name="nextRequest" select="substring-after($request,'/')"/>
      <xsl:variable name="currentDir" select="substring-before($request,'/')"/>
      <!--<xsl:comment>
        request: <xsl:value-of select="$request"/>
	      currentDir: <xsl:value-of select="$currentDir"/>
        currentDir has default view: <xsl:value-of select="boolean(dyn:evaluate(concat($rootElement,$rest,$currentDir,'/',$defaultView)))"/>
        next request: <xsl:value-of select="$nextRequest"/>
        rest: <xsl:value-of select="$rest"/>
        view2response: <xsl:value-of select="$view2response"/>
	    </xsl:comment>-->
      <!--In the path are still directories-->
      <xsl:if test="boolean(dyn:evaluate(concat($rootElement,$rest,$currentDir,'/',$defaultView)))">
        <!--<xsl:comment>view2response set - <xsl:value-of select="(concat($rest,$currentDir,'/',$defaultView)"/></xsl:comment>-->
        <xsl:variable name="viewTemp" select="concat($rest,$currentDir,'/',$defaultView)"/>
        <xsl:call-template name="viewSelector">
			    <xsl:with-param name="request" select="$nextRequest"/>
			    <xsl:with-param name="rest" select="concat($rest,$currentDir,'/')"/>
	        <xsl:with-param name="view2response" select="$viewTemp"/>
			  </xsl:call-template>
      </xsl:if>
      <xsl:if test="not(dyn:evaluate(concat($rootElement,$rest,$currentDir,'/',$defaultView)))">
        <!--<xsl:comment>no view2response set</xsl:comment>-->
        <xsl:if test="$view2response=''">
          <xsl:variable name="viewTemp" select="''"/>
          <xsl:call-template name="viewSelector">
				    <xsl:with-param name="request" select="$nextRequest"/>
				    <xsl:with-param name="rest" select="concat($rest,$currentDir,'/')"/>
		        <xsl:with-param name="view2response" select="$viewTemp"/>
				  </xsl:call-template>
        </xsl:if>
        <xsl:if test="$view2response!=''">
          <xsl:variable name="viewTemp" select="$view2response"/>
          <xsl:call-template name="viewSelector">
				    <xsl:with-param name="request" select="$nextRequest"/>
				    <xsl:with-param name="rest" select="concat($rest,$currentDir,'/')"/>
		        <xsl:with-param name="view2response" select="$viewTemp"/>
				  </xsl:call-template>
        </xsl:if>
      </xsl:if>
    </xsl:if>
    <xsl:if test="not(contains($request,'/'))">
      <!--<xsl:comment>
        request: <xsl:value-of select="$request"/>
        rest: <xsl:value-of select="$rest"/>
        iteration: <xsl:value-of select="number($iteration)"/>
        view2response: <xsl:value-of select="$view2response"/>
	    </xsl:comment>-->
      <xsl:if test="not(dyn:evaluate(concat($rootElement, $rest, $request)))">
        <!--<xsl:comment>no file specific override</xsl:comment>-->
        <xsl:if test="boolean(dyn:evaluate(concat($rootElement,$rest,$defaultView)))">
          <!--<xsl:comment>dir specific override</xsl:comment>-->
          <xsl:value-of select="concat($rest,$defaultView)"/>
        </xsl:if>
        <xsl:if test="not(dyn:evaluate(concat($rootElement,$rest,$defaultView)))">
          <!--<xsl:comment>no dir specific override</xsl:comment>-->
          <xsl:value-of select="$view2response"/>
        </xsl:if>
      </xsl:if>
      <xsl:if test="boolean(dyn:evaluate(concat($rootElement, $rest, $request)))">
        <!--<xsl:comment>file specific override</xsl:comment>-->
         <xsl:value-of select="concat($rest, $request)"/>
      </xsl:if>
    </xsl:if>
  </xsl:template>
  
</xsl:stylesheet>
