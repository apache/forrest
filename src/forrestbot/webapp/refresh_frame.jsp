<!--
  Copyright 2003-2004 The Apache Software Foundation

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->
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
