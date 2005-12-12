/*
* Copyright 2002-2005 The Apache Software Foundation or its licensors,
* as applicable.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

var dispatcherCall=false;

function setDispatcherCall(xBoolean) {
  dispatcherCall=(xBoolean)?true:false;
}

function startforrestbar()
{
  //alert("function init");
  if(document.getElementById("forrestbar").getAttribute("hidden") == "false")
  {
    removeEventListener ("load",startforrestbar, true); // Keep the event from firing a hundred more times.
  }
}

addEventListener("load", startforrestbar, true); // Run the startup function when the window loads

function navigate(url) {
  window._content.document.location.href = url;
}


/**************************/

function search(searchID)
{
  searchDev('aims', searchID);
}

function searchDev(engine, searchID)
{
  var searchItem = document.getElementById(searchID);
  if (engine == 'aims') {
    navigate('http://marc.theaimsgroup.com/?l=forrest-dev&w=2&r=1&q=b&s=' + searchItem.value);
  } else {
    navigate('http://www.mail-archive.com/cgi-bin/htsearch?method=and&format=short&config=dev_forrest_apache_org&restrict=&exclude=&words=' + searchItem.value);
  }
}

function searchUser(searchID)
{
  var searchItem = document.getElementById(searchID);
  navigate('http://www.mail-archive.com/cgi-bin/htsearch?method=and&format=short&config=user_forrest_apache_org&restrict=&exclude=&words=' + searchItem.value);
}

function searchSite(searchID)
{
  var searchItem = document.getElementById(searchID);
  navigate('http://www.google.com/search?sitesearch=forrest.apache.org&q=search&Search=' + searchItem.value);
}

function contract(subUrl,searchID)
{
  var searchItem = document.getElementById(searchID);
  navigate('http://localhost:8888/'+subUrl + searchItem.value);
}

function navProject(searchID) {
  var searchItem = document.getElementById(searchID);
  navigate(searchItem.selectedItem.value);
}

function viewXML(xmltype)
{
  var href = gBrowser.currentURI.spec;
  if( ! isLocalUrl())
  {
    alert("This action is only available on Local Forrest (jetty) site...");
    return(false);
  }
  (dispatcherCall)?navigate("http://localhost:8888/"+xmltype+href.substring(href.lastIndexOf('8888/')+5, href.lastIndexOf('.') )):navigate(href.substring(0, href.lastIndexOf('.') ) + xmltype);
}

function isLocalUrl ()
{
  var href = gBrowser.currentURI.spec;

  return( (typeof(href) != 'undefined') &&
          (href.substr) &&
          (startsWith(href, 'http://127.0.0.1:8888/') || startsWith(href, 'http://localhost:8888/'))
        );
}

function startsWith(st, pref)
{
  return( (pref.length > 0) && (st.substring(0, pref.length) == pref) );
}
