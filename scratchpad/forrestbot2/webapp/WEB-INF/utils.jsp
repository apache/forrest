<%!
private String getTimestamp(File file, Locale loc) {
  if (!file.exists()) return "";
  long tstamp = file.lastModified();
  long t0 = System.currentTimeMillis();
  long dt = t0 - tstamp;
  long secs=dt/1000;
  long mins=secs/60;
  long hours=mins/60;
  long days=hours/24;

  StringBuffer ret = new StringBuffer();

  if (days != 0) ret.append(days+" day"+(days==1?" ":"s "));
  hours -= days*24;
  if (hours != 0) ret.append(hours+" hour"+(hours==1?" ":"s "));
  mins -= (days*24 + hours)*60;
  if (mins != 0) ret.append(mins+" min"+(mins==1?" ":"s "));
  secs -= ((days*24 + hours)*60 + mins)*60;
  if (secs != 0) ret.append(secs+" sec"+(secs==1?" ":"s "));
  ret.append(" ago");
  return ret.toString();
}

%>

<%-- vim: set ft=java: --%>
