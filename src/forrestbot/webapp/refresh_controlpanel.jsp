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
