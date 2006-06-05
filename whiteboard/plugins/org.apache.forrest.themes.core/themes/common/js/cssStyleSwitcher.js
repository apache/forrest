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

/*
* Note: this script depends on prototype.js
*
* cssStyleSwitcher.js
*/
var THEME_SWITCHER_PROFILING=false;
var THEME_SWITCHER_DEFAULT_THEME="";
function switchTheme(title){
  var linkElements= document.getElementsByTagName("link"); // get all linked objects
  for(var i = 0; i <linkElements.length; i++) {
    var a = linkElements[i];  // get linked object
     //deactivate all screen css
    if (a.getAttribute("media") == "screen") {
      a.disabled=true;
    }
  }
  //activate the selected theme 
  for(var i = 0; i <linkElements.length; i++) {
    var a = linkElements[i]; // get all linked objects
    if (a.getAttribute("media") == "screen" ) {
/* theme profiling will only change a couple of stylesheets so we need to include
    the default theme on which the profiling is based on*/
      if (THEME_SWITCHER_PROFILING){
        if(title.lastIndexOf(THEME_SWITCHER_DEFAULT_THEME)>-1){
          var profiled = (a.getAttribute("title") == THEME_SWITCHER_DEFAULT_THEME || a.getAttribute("title") == title)?true:false;
          a.disabled = (profiled)?false:true;
        }else{
          a.disabled = (a.getAttribute("title") == title)?false:true;
        }
      }else{
        a.disabled = (a.getAttribute("title") == title)?false:true;
      }
    }
  }
  var styleElements= document.getElementsByTagName("style");// get all inline style objects
  for(var i = 0; i <styleElements.length; i++) {
    var a = styleElements[i];
     //deactivate all screen css
    if (a.getAttribute("media") == "screen") {
      a.disabled=true;
    }
  }
  //activate the selected theme 
  for(var i = 0; i <styleElements.length; i++) {
    var a = styleElements[i]; // get inline style object
    if (a.getAttribute("media") == "screen" ) {
/* theme profiling will only change a couple of stylesheets so we need to include
    the default theme on which the profiling is based on*/
      if (THEME_SWITCHER_PROFILING){
        if(title.lastIndexOf(THEME_SWITCHER_DEFAULT_THEME)>-1){
          var profiled = (a.getAttribute("title") == THEME_SWITCHER_DEFAULT_THEME || a.getAttribute("title") == title)?true:false;
          a.disabled = (profiled)?false:true;
        }else{
          a.disabled = (a.getAttribute("title") == title)?false:true;
        }
      }else{
        a.disabled = (a.getAttribute("title") == title)?false:true;
      }
    }
  }
  createCookie("style", title, 365);
} // end method switchTheme(title)
/* change the active (preferred) stylesheet to the selected one and save it */
function switchThemeSelect(selBox){
   var selIndex= selBox.selectedIndex; // get the selected index
   var title= selBox.options[selIndex].value; // get the value of this index
   if (title == "-1") return false;
     switchTheme(title); // do the actual switch
} // end method switchThemeSelect()
function initSelectSwitcher(themeSwitcherSelect){
  var select = $(themeSwitcherSelect);
  var themes=aviableThemes();
  var tempTheme = themes.split(';');
  for(var xi=0;xi < tempTheme.length;xi++) {
    if (tempTheme[xi].length>1){
      select.options[select.options.length] = new Option(tempTheme[xi]);
    }
  }
}
function initASwitcher(themeSwitcherA,seperator){
  var select = $(themeSwitcherA); // get the object where we want to insert the response
  var output = "";// initialize output object
  var themes = aviableThemes(); // get all aviable themes
  var tempTheme = themes.split(';'); // get an array out of the themes
  for(var xi=0;xi < tempTheme.length;xi++) {
    if (tempTheme[xi].length>1){
      output=output+"<a href=\"javascript:switchTheme('"+tempTheme[xi]+"')\">"+tempTheme[xi]+"</a>"+seperator; // add the theme to an a object
    }
  }
  var response = output.substring(0, output.lastIndexOf(seperator)); // clean the output trailing seperator
  $(select).innerHTML=response; // set the response
}
function aviableThemes(){
  var currentTheme;
  var themes="";
  var linkElements= document.getElementsByTagName("link");
  for(var i = 0; i <linkElements.length; i++) {
    var a = linkElements[i];
    if (a.getAttribute("media") == "screen" && a.getAttribute("title")) {
      currentTheme=a.getAttribute("title");
      var tempTheme = themes.split(';');
      var contained = false;
      for(var xi=0;xi < tempTheme.length;xi++) {
        var theme = tempTheme[xi];
        if (theme==currentTheme){
          contained=true;
        }
      }
      if (contained==false){
        themes=themes+currentTheme+";";
      }
    }
  }
  var styleElements= document.getElementsByTagName("style");
  for(var i = 0; i <styleElements.length; i++) {
    var a = styleElements[i];
    if (a.getAttribute("media") == "screen" && a.getAttribute("title")) {
      currentTheme=a.getAttribute("title");
      var tempTheme = themes.split(';');
      var contained = false;
      for(var xi=0;xi < tempTheme.length;xi++) {
        var theme = tempTheme[xi];
        if (theme==currentTheme){
          contained=true;
        }
      }
      if (contained==false){
        themes=themes+currentTheme+";";
      }
    }
  }
  return themes;
}

/*CookieStuff*/
function createCookie(name, value, days) {
	var date = new Date();
	date.setTime(date.getTime() + (days*24*60*60*1000));
	var expires = "; expires="+date.toGMTString();
	document.cookie = name + "=" + value + expires + "; path=/";
}
function readCookie(name) {
	var nameEQ = name + "=";
	var ca = document.cookie.split(';');
	for(var i=0;i < ca.length;i++) {
		var c = ca[i];
		while (c.charAt(0)==' ') c = c.substring(1,c.length);
		if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
	}
	return null;
}
function initBrandingThemeSwitcher(defaultTheme,profilerActivated){
  THEME_SWITCHER_PROFILING=profilerActivated;
  THEME_SWITCHER_DEFAULT_THEME=defaultTheme;
  var cookie = readCookie("style");
  switchTheme((cookie)?cookie:defaultTheme);
}
//FIXME: Extract the following method to a helper class
function startsWith(st, pref)
{
  return( (pref.length > 0) && (st.substring(0, pref.length) == pref) );
}
