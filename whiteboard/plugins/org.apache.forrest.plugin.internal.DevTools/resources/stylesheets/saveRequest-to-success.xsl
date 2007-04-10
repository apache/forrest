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
  xmlns:h="http://apache.org/cocoon/request/2.0">
<!-- The response from the save action -->
  <xsl:param name="response"/>
<!-- The Staging Dir -->
  <xsl:param name="staging-dir"/>
  <xsl:template match="h:request">
    <document>
      <header>
        <title>Site Build</title>
      </header>
      <body>
        <warning>
          Your site may not be fully generated.
        </warning>
        <p>
          You have sucesfully saved the file to your local staging site.
          However, no other versions of this page (i.e. PDF, text etc) have been
          saved, similarly no other pages have been saved.
        </p>
        <note>
          If you have changed any of the navigation or skin/theme files you must
          regenerate the whole site using the "forrest site" command.
        </note>
        <p>
          <link>
          <xsl:attribute name="href">/<xsl:value-of select="h:requestParameters/h:parameter[@name='URI']/h:value"/>
          </xsl:attribute>
          Return to the originating page</link>.
        </p>
        <section>
          <title>Details</title>
          <table>
            <tr>
              <th>Staging Directory</th>
              <td>
                <xsl:value-of select="$staging-dir"/>
              </td>
            </tr>
            <tr>
              <th>Staged URL</th>
              <td>/<xsl:value-of select="h:requestParameters/h:parameter[@name='URI']/h:value"/>
              </td>
            </tr>
          </table>
        </section>
      </body>
    </document>
  </xsl:template>
</xsl:stylesheet>
