<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean-el" prefix="bean-el" %>

<html:errors/>

<logic:messagesNotPresent> 
	<bean-el:message key="execute.started" arg0="${project}"/>
</logic:messagesNotPresent>
<br/>
<html:link page="/summary.do"><bean:message key="back.to.main"/></html:link>
<br/>
<html:link page="/viewlog.do" paramid="project" paramname="project"><bean-el:message key="view.log" arg0="${project}"/></html:link>
