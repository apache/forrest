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

// Initialize Install
var err = initInstall("ForrestBar", "forrestbar", "0.7");

// Get the JAR
addFile("forrestbar", "chrome/forrestbar.jar", getFolder("chrome"),"");

// Register the content
registerChrome(CONTENT | DELAYED_CHROME, getFolder("chrome","forrestbar.jar"), "content/");

// Install it
if (err == SUCCESS){
  performInstall();
  alert('Please restart your browser to finish the installation.');
} else {
  cancelInstall(err);
}
