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
    <% } %>

    <% if (command != null) { %>
    Processing <b><%= command %></b> - <a target="_top" href="<%=request.getContextPath()+"/refresh/"+module%>">View progress</a>
    <% } %>

    <table>
      <tr><td width="40%">
    <p>
    <ul style="list-style-image: url(images/dir.gif)">
      <li><a target="_top" href="<%= sites_url %>">View sites</a>:
      <ul style="list-style-image: url(images/file.gif)">
        <%
        for (int i=0; i<sites.size(); i++) {
          String sitename = (String)sites.get(i);
          boolean exists = new File(sites_dir + "/"+sitename).exists();
          out.println("<li>");
          if (exists) out.println("<a target='_top' href='"+sites_url+"/"+sitename+"'>");
            out.println(sitename);
          if (exists) out.println("</a>");
        if (exists) out.println("&nbsp;&nbsp;<a href='" + logs_url + "/"+sitename+".log'><small>[log]</small></a>");
        if (exists) out.println("&nbsp;&nbsp;"+getTimestamp(new File(logs_dir + "/"+sitename+".log"), request.getLocale()));
        }
        %>
      </ul>
      <li><a target="_top" href="<%= logs_url %>">Logs</a>
    </ul>
    </p>
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
        <input type="submit" name="action" value="build">
      <% } %>
      <% if (privs.contains("deploy")) { %>
        <input type="submit" name="action" value="deploy"/>
      <% } %>
      <% } else { %>
        <input type="submit" value="login">
      <% } %>
  </form>
  </td></tr>
  </table>
  </body>
</html>
