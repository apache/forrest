<html>
  <head>
    <title>Forrestbot Web Interface</title>
  </head>
  <% String ctx = request.getContextPath();%>
  <frameset ROWS="20%,*">
    <frame SRC="<%=ctx%>/controlpanel.jsp" NAME="index">
    <frame SRC="<%=ctx%>/sites/<%=request.getPathInfo()%>/index.html" NAME="frame2">
    <noframes>
      <body>
        Sorry, you need frames to view this site.
      </body>
    </noframes>
  </frameset>
</html>
