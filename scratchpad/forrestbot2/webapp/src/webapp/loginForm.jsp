<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>


<html:errors/>

<html:form action="/summary" focus="username">
<table border="0">
    <tr>
        <th>
            <bean:message key="prompt.username"/>
        </th>
        <td>
            <html:text property="username" size="16"/>
        </td>
    </tr>
    <tr>
        <th>
            <bean:message key="prompt.password"/>
        </th>
        <td>
            <html:password property="password" size="16"/>
        </td>
    </tr>
    <tr>
        <td colspan="2">
        	<input type="hidden" name="submit"/>
            <html:submit>
                <bean:message key="button.submit"/>
            </html:submit>
        </td>
    </tr>
</table>

</html:form>