<?xml version="1.0"?>
<!--
  Copyright 1999-2004 The Apache Software Foundation or its licensors,
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

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:param name="filename"></xsl:param>

  <xsl:template match="/">
    <html xmlns="http://www.w3.org/1999/xhtml">
      <xsl:call-template name="head"/>
      <xsl:call-template name="body"/>
    </html>
  </xsl:template>
  
  <xsl:template name="head">
    <head>
      <!-- Configure the path to the editor.  We make it relative now, so that the
      example ZIP file will work anywhere, but please NOTE THAT it's better to
      have it an absolute path, such as '/htmlarea/'. -->
      <script type="text/javascript">
        _editor_url = "/edit_htmlArea";
        _editor_lang = "en";
      </script>
      
      <!-- load the main HTMLArea file, this will take care of loading the CSS and
      other required core scripts. -->
      <script type="text/javascript">
        <xsl:attribute name="src">/edit_htmlArea/htmlarea.js</xsl:attribute>
      </script>  
      
      <!-- load the plugins -->
      <script type="text/javascript">
        // WARNING: using this interface to load plugin
        // will _NOT_ work if plugins do not have the language
        // loaded by HTMLArea.
  
        // In other words, this function generates SCRIPT tags
        // that load the plugin and the language file, based on the
        // global variable HTMLArea.I18N.lang (defined in the lang file,
        // in our case "lang/en.js" loaded above).
  
        // If this lang file is not found the plugin will fail to
        // load correctly and NOTHING WILL WORK.
  
        //HTMLArea.loadPlugin("TableOperations");
        //HTMLArea.loadPlugin("SpellChecker");
        //HTMLArea.loadPlugin("FullPage");
        //HTMLArea.loadPlugin("CSS");
        //HTMLArea.loadPlugin("ContextMenu");
        //HTMLArea.loadPlugin("HtmlTidy");
        //HTMLArea.loadPlugin("ListType");
        //HTMLArea.loadPlugin("CharacterMap");
        //HTMLArea.loadPlugin("DynamicCSS");
      </script>
      
      <script type="text/javascript">
      var editor = null;
      
      function initEditor() {
      
        // create an editor for the "editor" textbox
        editor = new HTMLArea("editor");
      
        // register the FullPage plugin
        //editor.registerPlugin(FullPage);
      
        // register the SpellChecker plugin
        //editor.registerPlugin(TableOperations);
      
        // register the SpellChecker plugin
        //editor.registerPlugin(SpellChecker);
        
        // register the HtmlTidy plugin
        //editor.registerPlugin(HtmlTidy);
      
        // register the ListType plugin
        //editor.registerPlugin(ListType);
      
        //editor.registerPlugin(CharacterMap);
        //editor.registerPlugin(DynamicCSS);
      
        // register the CSS plugin
        /*editor.registerPlugin(CSS, {
          combos : [
            { label: "Syntax:",
                         // menu text       // CSS class
              options: { "None"           : "",
                         "Code" : "code",
                         "String" : "string",
                         "Comment" : "comment",
                         "Variable name" : "variable-name",
                         "Type" : "type",
                         "Reference" : "reference",
                         "Preprocessor" : "preprocessor",
                         "Keyword" : "keyword",
                         "Function name" : "function-name",
                         "Html tag" : "html-tag",
                         "Html italic" : "html-helper-italic",
                         "Warning" : "warning",
                         "Html bold" : "html-helper-bold"
                       },
              context: "pre"
            },
            { label: "Info:",
              options: { "None"           : "",
                         "Quote"          : "quote",
                         "Highlight"      : "highlight",
                         "Deprecated"     : "deprecated"
                       }
            }
          ]
        });
      */
        // add a contextual menu
        //editor.registerPlugin("ContextMenu");
      
        // load the stylesheet used by our CSS plugin configuration
        //editor.config.pageStyle = "@import url(custom.css);";
      
        editor.generate();
        return false;
      }
      
      HTMLArea.onload = initEditor;
      
      function insertHTML() {
        var html = prompt("Enter some HTML code here");
        if (html) {
          editor.insertHTML(html);
        }
      }
      function highlight() {
        editor.surroundHTML('<span style="background-color: yellow">', '</span>');
      }
      </script>
    </head>
  </xsl:template>
  
  <xsl:template name="body">
    <body onload="initEditor()">
      <form method="post" action="write.do">
        <label>Title:
          <input type="text" name="title" size="70">
            <xsl:attribute name="value"><xsl:value-of select="/html/head/title"/></xsl:attribute>
          </input>
        </label>
        
        <hr/>
        
        <textarea id="editor" name="editor" cols="80" rows="30">
          <!--xsl:value-of select="normalize-space(.)"/-->
          <xsl:apply-templates/>
        </textarea>
        
        <input type="hidden" name="filename">
          <xsl:attribute name="value"><xsl:value-of select="$filename"/></xsl:attribute>
        </input>
        
        <input type="submit" value="Submit" />
        <input type="reset" value="Reset" />
      </form>
    </body>
  </xsl:template>
  
  <xsl:template match="body">
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="node()|@*" priority="-1">
        <xsl:copy>
              <xsl:apply-templates select="node()|@*"/>
        </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
