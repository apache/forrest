<%@page import="java.io.*" contentType="text/plain"%>
<%! private static final String LOG_FILE="/WEB-INF/refresh_log.txt"; %>
<%
File f = new File(getServletContext().getRealPath(LOG_FILE));
if (f.exists()) {
	String logs = forrestbot.IOUtil.toString(new FileReader(f));
	out.println(logs.substring(logs.length()-1000));
} else { %>

Log file <%=LOG_FILE%> not found.

Please start the forrestbot backend script (overseer) and ensure it is logging
to the right place.

<% } %>
