<%@include file="utils.jsp"%>
<%@include file="settings.jsp"%>
<%@page import="java.util.ArrayList"%>
<% 

// build list of sites
File f = new File(sites_dir);
if (!f.exists()) {
	%> <h2> sites-dir in settings.properties is not valid </h2> <%
	out.println(sites_dir);
	return;
}
File[] possibleSites = f.listFiles();
ArrayList sites = new ArrayList();
for(int i=0; i<possibleSites.length; i++) {
	if (possibleSites[i].isDirectory()) {
		sites.add(possibleSites[i].getName());
	}
}

String username = request.getParameter("username");
if (username != null) { session.setAttribute("username", username); }
else { username = (String)session.getAttribute("username"); }
if (username != null && username.equals("")) username = null;

// Note: Moz 1.2 doesn't like password fields called 'password'
String password = request.getParameter("passwd");
if (password != null) { session.setAttribute("password", password); }
else { password = (String)session.getAttribute("password"); }
if (password != null && password.equals("")) password = null;

// Try to authenticate the user and determine what privs they have
ArrayList privs = null;
String errMsg = null;
if (username != null) {
  java.util.Properties users = new java.util.Properties();
  users.load(
      getServletConfig().getServletContext().getResourceAsStream("/WEB-INF/users.properties")
      );
  String reqPassword = users.getProperty(username+".password");
  if (reqPassword == null) {
    errMsg = "No user '"+username+"'. Please contact the admins to have your name added";
  } else {
    if (password != null && password.equals(reqPassword)) {
		privs = new ArrayList();
		StringTokenizer stPrivs = new StringTokenizer(users.getProperty(username), ",");
		while (stPrivs.hasMoreTokens()) {
			String currentPriv = stPrivs.nextToken();
			if (currentPriv.equals("*"))
				privs.addAll(sites);
			else
				privs.add(currentPriv);
		}
    } else {
      errMsg = "Incorrect password for user '"+username+"', please try again";
    }
  }
}

// Write commands to COMMANDS_FILE, where hopefully they will get picked up by
// a shell script

String action = request.getParameter("action");
String module = request.getParameter("module");
String command = null;
if (privs != null && action != null && module!= null) {
  if (privs.contains(module) && privs.contains(action)) {
    PrintStream ps = new PrintStream(new FileOutputStream(new File(commands_file)));
    command = action+" "+module;
    ps.println(command);
    ps.close();
  } else {
    errMsg = "User "+username+" cannot "+action+" module "+module;
  }
}
%>
<%-- vim: set ft=java: --%>

