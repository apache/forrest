<% String site = request.getParameter("site"); %>
<html>
  <head>
    <title>Forrestbot Web Interface - Refreshing site <%= site %></title>
  </head>
  <body>
    <h1>Refreshing site <%= site %></h1>
    <p>
    The lower pane prints the last page or so of the build log file,
    refreshed every 5 seconds. When it looks like stuff has finished
    happening, you can view the regenerated site.
    </p>
    <ul>
      <li><a target="_top" href="<%= request.getContextPath()%>">Return to menu</a>
      <li><a target="_top" href="<%= request.getContextPath()%>/site<%=request.getParameter("site")%>">View site <%= site %></a>
    </ul>
  </body>
</html>
