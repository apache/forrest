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
        <xsl:apply-templates select="faq|part" mode="index"/>
       </ul>
      </section>
      <section>
       <title>Answers</title>
        <xsl:apply-templates select="faq|part"/>
      </section>
    </body>
   </document>  
  </xsl:template>

  <xsl:template match="part" mode="index">
    <li>
      <link>
        <xsl:attribute name="href">
          <xsl:text>#</xsl:text><xsl:call-template name="generate-id"/>
        </xsl:attribute>
       <xsl:apply-templates select="title"/>
      </link>
       <ul>
        <xsl:apply-templates select="faq|part" mode="index"/>
       </ul>
    </li>
  </xsl:template>

  <xsl:template match="faq" mode="index">
    <li>
      <link>
        <xsl:attribute name="href">
          <xsl:text>#</xsl:text><xsl:call-template name="generate-id"/>
        </xsl:attribute>
        <xsl:value-of select="question"/>
      </link>
    </li>
  </xsl:template>

  <xsl:template match="part">
    <section>
      <xsl:attribute name="id">
        <xsl:call-template name="generate-id"/>
      </xsl:attribute>
      <title>
      <xsl:value-of select="title"/>
     </title>
      <xsl:apply-templates select="faq|part"/>
    </section>
  </xsl:template>

  <xsl:template match="faq">
    <section>
      <xsl:attribute name="id">
        <xsl:call-template name="generate-id"/>
      </xsl:attribute>
     <title>
      <xsl:value-of select="question"/>
     </title>
      <xsl:apply-templates/>
    </section>
  </xsl:template>

  <xsl:template name="generate-id">
    <xsl:choose>
      <xsl:when test="@id">
        <xsl:value-of select="@id"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="concat(concat(local-name(.), '-'), generate-id(.))"/>
      </xsl:otherwise>
  </xsl:choose>
  </xsl:template>

  <xsl:template match="question">
    <!-- ignored since already used -->
  </xsl:template>

  <xsl:template match="answer">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="title">
    <xsl:apply-templates/>
  </xsl:template>

</xsl:stylesheet>
