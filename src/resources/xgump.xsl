<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method = "html" encoding="Windows-1252" />

  <xsl:template match="/">
    <html>
      <head>
        <title>Gump descriptor of module <xsl:value-of select="module/@name" /></title>
      </head>
      <body>

       <h1><xsl:value-of select="module/@name" /></h1>

       <p>website:<a><xsl:attribute  name = "href" >
              <xsl:value-of select="module/url/@href" />
             </xsl:attribute><xsl:value-of select="module/url/@href" /></a>
       <br/>cvs repository: <xsl:value-of select="module/cvs/@repository" />
       <xsl:for-each select = "module/mailing-lists/mailing-list">
       <br/><xsl:value-of select="@user" />&#160;mailing list:&#160;
            <a><xsl:attribute  name = "href" >mailto:<xsl:value-of select="@mail" /></xsl:attribute>
               <xsl:value-of select="@mail" /></a>
            <a><xsl:attribute  name = "href" >mailto:<xsl:value-of select="@subscribe" /></xsl:attribute>
               Subscribe</a>
                  <a><xsl:attribute  name = "href" >mailto:<xsl:value-of select="@unsubscribe" /></xsl:attribute>
                     Unsubscribe</a>

       </xsl:for-each>
             </p>

       <h2>Description</h2>
       <p><xsl:value-of select="module/description" /></p>
       <p><xsl:value-of select="module/detailed" /></p>

       <h2>Reasons</h2>
       <p><xsl:value-of select="module/why" /></p>

       <h2>Goals</h2>
       <ul>
       <xsl:for-each select = "module/what/goal">
       <li><xsl:value-of select="." /></li>
       </xsl:for-each>
       </ul>

       <h2>Developers</h2>
       <table>
       <xsl:for-each select = "module/who/person">
       <tr><td><xsl:value-of select="@name" /></td>
           <td><xsl:value-of select="@email" /></td>
           <td>[<xsl:value-of select="@id" />]</td></tr>
       </xsl:for-each>
       </table>


       <h2>To do</h2>
       <xsl:for-each select = "module/todo/actions">
       <h3><xsl:value-of select = "@priority"/>&#160;priority</h3>
       <table>
       <tr><th>context</th><th>what</th><th>assigned to</th></tr>
       <xsl:for-each select = "action">
       <tr><td><xsl:value-of select="@context" /></td>
           <td><xsl:value-of select="." /></td>
           <td>[<xsl:value-of select="@dev" />]</td></tr>
       </xsl:for-each>
       </table>
       </xsl:for-each>

       <h2>Changes</h2>
       <xsl:for-each select = "module/changes/release">
       <h3>release&#160;<xsl:value-of select = "@version"/>&#160;
           of date&#160;<xsl:value-of select = "@date"/></h3>
       <table>
       <tr><th>type</th><th>what</th><th>developer</th></tr>
       <xsl:for-each select = "action">
       <tr><td><xsl:value-of select="@type" /></td>
           <td><xsl:value-of select="." /></td>
           <td>[<xsl:value-of select="@dev" />]</td></tr>
       </xsl:for-each>
       </table>
       </xsl:for-each>

       <h2>License</h2>
       <p><b><xsl:value-of select="module/licence" /></b></p>

       <h2>Credits</h2>
       <ul>
       <xsl:for-each select = "module/credits/credit">
       <li><xsl:value-of select="." /></li>
       </xsl:for-each>
       </ul>

       </body>
    </html>
  </xsl:template>
</xsl:stylesheet>