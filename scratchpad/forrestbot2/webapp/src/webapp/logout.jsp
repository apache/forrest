<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>

<br/>
<html:errors/>
<br/>
<bean:message key="login.out.done"/>
<br/>
<br/>
<html:link page="/summary.do"><bean:message key="back.to.main"/></html:link>
