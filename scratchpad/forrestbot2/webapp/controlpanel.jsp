<%@page import="java.io.*,java.util.*,java.text.*"%>

<%@include file="WEB-INF/logic.jsp"%>

<html>
<head>
	<title>Forrestbot Web Interface</title>
</head>

<body>
	<% if (request.getParameter("intro") != null) { %>
	<jsp:include page="welcome.html" flush="true"/>
	<hr>
	<jsp:include page="welcome.local.html" flush="true"/>
	<hr>
	<% } %>

	<% if (command != null) { %>
	Processing <b><%= command %></b> - <a target="_top" href="<%=request.getContextPath()+"/refresh/"+module%>">View progress</a>
	<% } %>

	<table>
	<tr><td width="40%">
	<!--<p>
	<a target="_top" href="<%= sites_url %>">View all sites</a>-->
		<table><tr>
			<th>Site</th>
			<th>Status</th>
			<th>Log</th>
			<th>Date</th>
		</tr>
		<%
		for (int i=0; i<sites.size(); i++) {
			String sitename = (String)sites.get(i);
			//boolean exists = new File(sites_dir + "/"+sitename).exists();
			out.println("<tr>");
			out.println("<td>");
			out.println("<a target='_top' href='"+sites_url+"/"+sitename+"'>" + sitename + "</a>");
			out.println("</td>");
			out.println("<td>");
			out.println("</td>");
			out.println("<td>");
			out.println("<a href='" + logs_url + "/"+sitename+".log'>log</a>");
			out.println("</td>");
			out.println("<td>");
			//out.println("&nbsp;&nbsp;"+getTimestamp(new File(logs_dir + "/"+sitename+".log"), request.getLocale()));
			DateFormat formatter = new SimpleDateFormat("HH:mm:ss, dd MMM yyyy", request.getLocale());
			Date date = new Date((new File(logs_dir + "/"+sitename+".log")).lastModified());
			out.println(formatter.format(date));
			out.println("</td>");
			out.println("</tr>");
		}
		%>
		</table>
<!--	</ul>
	<li><a target="_top" href="<%= logs_url %>">Logs</a>
	</ul>
	</p>-->
</td>
<td width="10%">
</td>
<td>
<div style="color: red"><% if (errMsg != null) { out.println(errMsg); } %></div>
<form method="POST">
	Username: <input type="text" name="username" value="<%= (username!=null?username:"")%>"> <br>
	Password: <input type="password" name="passwd" value="<%=(password!=null?password:"")%>"> <br>
	<% if (privs != null) { %>
		Select a module:
		<select name="module">
			<%
			for (int i=0; i<sites.size(); i++) {
				String sitename = (String)sites.get(i);
			if (privs.contains(sitename)) {
				out.println("<option value='"+sitename+"'>"+sitename);
			}
			}
			%>
		</select>
		<br>
		Select an action:
		<% if (privs.contains("build")) { %>
			<input type="submit" name="action" value="build"/>
		<% } %>
		<% if (privs.contains("deploy")) { %>
			<input type="submit" name="action" value="deploy"/>
		<% } %>
	<% } else if (privs == null) { %>
		<input type="submit" value="login">
	<% } %>
</form>
</td></tr>
</table>
</body>
</html>
