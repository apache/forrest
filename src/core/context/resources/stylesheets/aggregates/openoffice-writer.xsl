<?xml version="1.0" encoding="UTF-8"?>
<!-- OpenOffice Writer files are stored in .swx files which are 
     ZIP files. Content, style, meta, ... are stored
     n different files within these archives. In order to generate 
     *one* XML file containing all parts this aggregation using
     the CInclude-transformer is necessary.
     -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
  <xsl:param name="src"/>
  <xsl:template match="/">
    <office:document xmlns:c="http://apache.org/cocoon/include/1.0" xmlns:office="http://openoffice.org/2000/office" xmlns:style="http://openoffice.org/2000/style" xmlns:text="http://openoffice.org/2000/text" xmlns:table="http://openoffice.org/2000/table" xmlns:draw="http://openoffice.org/2000/drawing" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:meta="http://openoffice.org/2000/meta" xmlns:number="http://openoffice.org/2000/datastyle" xmlns:svg="http://www.w3.org/2000/svg" xmlns:chart="http://openoffice.org/2000/chart" xmlns:dr3d="http://openoffice.org/2000/dr3d" xmlns:math="http://www.w3.org/1998/Math/MathML" xmlns:form="http://openoffice.org/2000/form" xmlns:script="http://openoffice.org/2000/script" xmlns:config="http://openoffice.org/2001/config" office:class="text" office:version="1.0">
      <c:include select="/*/*">
        <xsl:attribute name="src">zip://meta.xml@<xsl:value-of select="$src"/></xsl:attribute>
      </c:include>
      <c:include select="/*/*">
        <xsl:attribute name="src">zip://content.xml@<xsl:value-of select="$src"/></xsl:attribute>
      </c:include>      
    </office:document>
  </xsl:template>
</xsl:stylesheet>
