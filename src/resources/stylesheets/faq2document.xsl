<?xml version="1.0"?>

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">

 <xsl:import href="copyover.xsl"/>

  <xsl:template match="faqs">
   <document>
    <header>
     <title><xsl:value-of select="@title"/></title>
    </header>
    <body>
      <section>
       <title>Questions</title>
       <ul>
        <xsl:apply-templates select="faq" mode="index"/>
       </ul>
      </section>
      <section>
       <title>Answers</title>
        <xsl:apply-templates select="faq"/>
      </section>
    </body>
   </document>  
  </xsl:template>

  <xsl:template match="faq" mode="index">
    <li>
      <link href="#faq-{position()}">
        <xsl:value-of select="question"/>
      </link>
    </li>
  </xsl:template>

  <xsl:template match="faq">
    <anchor id="faq-{position()}"/>
    <section>
     <title>
      <xsl:value-of select="question"/>
     </title>
      <xsl:apply-templates/>
    </section>
  </xsl:template>

  <xsl:template match="question">
    <!-- ignored since already used -->
  </xsl:template>

  <xsl:template match="answer">
    <xsl:apply-templates/>
  </xsl:template>

</xsl:stylesheet>
