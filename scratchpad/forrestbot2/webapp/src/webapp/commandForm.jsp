<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean-el" prefix="bean-el" %>

<bean-el:message key="login.as" arg0="${username}"/>
<br/>
<a href="logout.do"><bean:message key="login.out"/></a>
<br/>
<br/>