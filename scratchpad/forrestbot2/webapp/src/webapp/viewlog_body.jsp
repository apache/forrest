<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>

<%@ page import="java.io.BufferedReader" %>
<%@ page import="java.io.FileReader" %>
<%@ page import="org.apache.log4j.Logger" %>

<%
Logger log = Logger.getLogger(request.getRequestURI());
%>

<html:errors/>

<pre>
<%--
I don't know of a way to put a bean variable into a jsp:include page attribute
nor how to use tiles to include an absolute file
so we include it the old fashioned way
--%>
<%
String logfile = (String)request.getAttribute("logfile");
if (logfile != null)
{
	log.debug(logfile);
	BufferedReader in = new BufferedReader(new FileReader(logfile));
	String line;
	while ((line = in.readLine()) != null) {
		out.println(line);
	}
}
%>
</pre>
