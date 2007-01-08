function update() {
importClass(Packages.org.apache.forrest.http.client.PostFile);
var destinationUrl=cocoon.parameters["destinationUrl"];
var srcUrlBase=cocoon.parameters["srcUrlBase"];
var srcId=cocoon.parameters["srcId"];
var srcUrl;
var action = cocoon.parameters["action"];
//print("action "+action);
if(action=='commit' || action=='optimize'){
 srcUrl=srcUrlBase+"solr."+action;
 //print("srcUrl "+srcUrl);
}else{
 srcUrl=srcUrlBase+srcId+"solr."+action;
 //print("srcUrl "+srcUrl);
}
var post = new PostFile(destinationUrl, srcUrl);
cocoon.sendPage("result.jx", {"action":action,"status" : post.statusCode(), "charSet" : post.getResponseCharSet(), "body" : post.getResponseBodyAsString()} );
}
