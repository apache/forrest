<%@page import="java.util.ArrayList"%>
<% 

// settings.properties
java.util.Properties settings = new java.util.Properties();
settings.load(
  getServletConfig().getServletContext().getResourceAsStream("/WEB-INF/settings.properties")
  );

//getServletConfig().getServletContext().getRealPath(xxx);
String commands_file = settings.getProperty("commands-file");
String sites_dir = settings.getProperty("build-dir");
String logs_dir = settings.getProperty("logs-dir");
String sites_url = settings.getProperty("build-url");
String logs_url = settings.getProperty("logs-url");

%>
<%-- vim: set ft=java: --%>

