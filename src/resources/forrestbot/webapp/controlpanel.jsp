<%@page import="java.io.*,java.util.*,java.text.*"%>

<%!
String[] sites = new String[]{"xml-forrest", "xml-forrest-krysalis", "xml-forrest-template", "xml-site", "incubator-site", "xml-xindice", "xml-fop", "avalon-site", "avalon-phoenix", "ws-site", "cocoon-site"};
%>
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
      <li><a target="_top" href="sites/">Preview sites</a>:
      <ul style="list-style-image: url(images/file.gif)">
        <%
        for (int i=0; i<sites.length; i++) {
          boolean exists = new File(getServletContext().getRealPath("sites/"+sites[i])).exists();
          out.println("<li>");
          if (exists) out.println("<a target='_top' href='site/"+sites[i]+"'>");
            out.println(sites[i]);
          if (exists) out.println("</a>");
        if (exists) out.println("&nbsp;&nbsp;<a href='logs/work."+sites[i]+".log'><small>[log]</small></a>");
        if (exists) out.println("&nbsp;&nbsp;"+getTimestamp(sites[i], request.getLocale()));
        }
        %>
      </ul>
      <li><a target="_top" href="logs/">Logs</a>
    </ul>
    </p>
  </td>
  <td width="10%">
  </td>
  <td>
  <div style="color: red"><% if (errMsg != null) { out.println(errMsg); } %></div>
  <form method="GET">
    Username: <input type="text" name="username" value="<%= (username!=null?username:"")%>"> <br>
    Password: <input type="password" name="passwd" value="<%=(password!=null?password:"")%>"> <br>
    <% if (privs != null) { %>
      Select a module:
      <select name="module">
        <%
        for (int i=0; i<sites.length; i++) {
          if (hasPriv(privs, sites[i])) {
            out.println("<option value='"+sites[i]+"'>"+sites[i]);
          }
        }
        %>
      </select>
      <br>
      Select an action:
      <% if (hasPriv(privs, "refresh")) { %>
        <input type="submit" name="action" value="refresh">
      <% } %>
      <% if (hasPriv(privs, "publish")) { %>
        <input type="submit" name="action" value="publish"/>
      <% } %>
      <% } else { %>
        <input type="submit" value="login">
      <% } %>
  </form>
  </td></tr>
  </table>
  </body>
</html>
