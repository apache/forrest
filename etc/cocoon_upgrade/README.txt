             Upgrading Forrest's Cocoon README
             ---------------------------------

This directory contains files to help upgrade Cocoon to whatever is the latest
version of Cocoon.

Instructions for use:

1) In the Forrest root directory, run:

  patch -p0 < etc/cocoon_upgrade/cocoon_upgradepatch*

Which will make any required patches to the Forrest sources.



2) Upgrade the lib/endorsed/* jars to those from Cocoon:

[xml-forrest ~]$ ls lib/endorsed 
lib/endorsed:
CVS  xalan-2.5.0.jar  xercesImpl-2.4.0.jar  xml-apis.jar



3) Download nekopull and nekodtd:

  http://www.apache.org/~andyc/neko/nekopull-latest.tar.gz
  http://www.apache.org/~andyc/neko/nekodtd-latest.tar.gz

and put the jars in lib/core/:

[xml-forrest ~]$ ls lib/core/neko*
lib/core/nekodtd-0.1.5.jar  lib/core/nekopull-0.2.1.jar



3) Run build.sh to build a regular Forrest distribution



4) In this directory (etc/cocoon_upgrade), run the 'upgrade_cocoon_jars.sh'
script, after editing the top-level variables in it.  This will upgrade jars in
the build/dist/shbat/WEB-INF/lib directory.


Now the Forrest distribution in build/dist/shbat/ should be using the new
Cocoon.

-- 
jefft@apache.org
$Revision: 1.4 $ $Date: 2003/05/18 07:14:11 $
