<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<html:html locale="true">
    <head>
        <title><tiles:insert attribute="title"/></title>
    </head>
    <body bgcolor="#FFFFFF"/>

    <tiles:insert attribute="header"/>
	<h2>
		<tiles:insert attribute="title"/>
	</h2>
    <tiles:insert attribute="body-content"/>
    <tiles:insert attribute="footer"/>

    </body>
</html:html>