This directory contains files to help upgrade Cocoon to whatever is the latest
version of Cocoon.

Current contents:

upgrade_cocoon_jars.sh
   Script to copy jars from cocoon-2.1/build/* to Forrest 
cocoon-upgradepatch-20030504.patch
   Patch that should be applied from the Forrest root, which updates the Java
   code to the latest Cocoon API.


In addition to applying these changes, you need to:
 - Upgrade the lib/endorsed/* jars
 - Upgrade lib/core/neko*.jar 

[xml-forrest ~]$ ls lib/endorsed lib/core/neko*
lib/core/nekodtd-0.1.5.jar  lib/core/nekopull-0.2.1.jar

lib/endorsed:
CVS  xalan-2.5.0.jar  xercesImpl-2.4.0.jar  xml-apis.jar
