<%! public static final String COMMANDS_FILE="/WEB-INF/commands"; %>
<%@include file="utils.jsp"%>
<% 
String username = request.getParameter("username");
if (username != null) { session.setAttribute("username", username); }
else { username = (String)session.getAttribute("username"); }
if (username != null && username.equals("")) username = null;

// Note: Moz 1.2 doesn't like password fields called 'password'
String password = request.getParameter("passwd");
if (password != null) { session.setAttribute("password", password); }
else { password = (String)session.getAttribute("password"); }
if (password != null && password.equals("")) password = null;

//out.println(username+"/"+password);
// Try to authenticate the user and determine what privs they have
String privs = null;
String errMsg = null;
if (username != null) {
  java.util.Properties users = new java.util.Properties();
  users.load(
      getServletContext().getResourceAsStream("/WEB-INF/users.properties")
      );
  String reqPassword = users.getProperty(username+".password");
  if (reqPassword == null) {
    errMsg = "No user '"+username+"'. Please contact the admins to have your name added";
  } else {
    if (password != null && password.equals(reqPassword)) {
      privs=users.getProperty(username);
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
  if (hasPriv(privs, module) && hasPriv(privs, action)) {
    PrintStream ps = new PrintStream(new FileOutputStream(new File(getServletContext().getRealPath(COMMANDS_FILE))));
    command = action+" "+module;
    ps.println(command);
    ps.close();
  } else {
    errMsg = "User "+username+" cannot "+action+" module "+module;
  }
} %>
<%-- vim: set ft=java: --%>

