<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles" %>
<html:html locale="true">
    <head>
        <title><tiles:insert attribute="title"/></title>
    </head>
    <frameset rows="40,*">
        <html:frame framename="header" page="/viewlog_header.do" paramid="project" paramname="project"/>
        <html:frame framename="body" page="/viewlog_body.do" paramid="project" paramname="project"/>
    </frameset>
    <noframes>
      <html:link page="/viewlog_header.do" paramid="project" paramname="project">header</html:link>
      <html:link page="/viewlog_body.do" paramid="project" paramname="project">body</html:link>
    </noframes>
  </html:html>