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
<%@page import="java.io.*" contentType="text/plain"%>
<%!
private static final String LOG_FILE="/WEB-INF/refresh_log.txt";
/** Number of bytes to print on each refresh */
private static final int TAIL_SIZE=1000;
%>

<%
File f = new File(getServletContext().getRealPath(LOG_FILE));
if (f.exists()) {
try {
  String logs = forrestbot.IOUtil.toString(new FileReader(f));
  int len = logs.length();
  out.println( ((len < TAIL_SIZE) ? logs : logs.substring(logs.length()-TAIL_SIZE)) );
  } catch (Throwable t) { out.println(t); }
} else { %>

Log file <%=LOG_FILE%> not found.

Please start the forrestbot backend script (overseer) and ensure it is logging
to the right place.

<% } %>
