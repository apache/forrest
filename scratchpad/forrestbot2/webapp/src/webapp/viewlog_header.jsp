<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean-el" prefix="bean-el" %>
<html:html locale="true">
    <head>
    </head>
    <body bgcolor="#FFFFFF"/>
    <table border="0" width="100%" cellpadding="0" cellspacing="0">
    <tr><td>
        <html:link target="_top" page="/"><bean:message key="log.back"/></html:link>
    </td><td align="center">
        <bean-el:message key="log.refresh" arg0="${refreshrate}"/>
    </td><td align="right">
        <html:link target="body" page="/viewlog_body.do" paramid="project" paramname="project"><bean:message key="log.force.refresh"/></html:link>
    </td></tr>
    </table>
    </body>
</html:html>