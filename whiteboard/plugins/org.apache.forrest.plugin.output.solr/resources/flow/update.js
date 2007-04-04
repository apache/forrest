/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
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
