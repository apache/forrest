<html>
  <head>
    <title>Forrestbot Web Interface</title>
    <meta http-equiv="Refresh" CONTENT="5">
  </head>
  <% String ctx = request.getContextPath();%>
  <frameset ROWS="20%,*">
    <frame SRC="<%=ctx%>/refresh_controlpanel.jsp?site=<%=request.getPathInfo()%>" NAME="frame2">
    <frame SRC="<%=ctx%>/logs/refresh" NAME="frame2">
    <noframes>
      <body>
        Sorry, you need frames to view this site.
      </body>
    </noframes>
  </frameset>
</html>
