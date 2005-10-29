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

/*
* Note: this script depends on prototype.js
*
* cssStyleSwitcher.js
*/
function switchTheme(title){
  for(var i = 0; i <document.getElementsByTagName("link").length; i++) {
    var a = document.getElementsByTagName("link")[i];
     //deactivate all screen css
    if (a.getAttribute("media") == "screen") {
      a.disabled=true;
    }
  }
  //activate the selected theme 
  for(var i = 0; i <document.getElementsByTagName("link").length; i++) {
    var a = document.getElementsByTagName("link")[i];
    if (a.getAttribute("media") == "screen" ) {
      a.disabled = (a.getAttribute("title") == title)?false:true;
    }
  }
}
/* change the active (preferred) stylesheet to the selected one and save it */
function switchThemeSelect(selBox){
   var selIndex= selBox.selectedIndex; // get the selected index
   var title= selBox.options[selIndex].value; // get the value of this index
   if (title == "-1") return false;
     switchTheme(title); // do the actual switch
} // end method switchThemeSelect()