             Upgrading Forrest's Cocoon README
             ---------------------------------

This directory contains files to help upgrade Cocoon to whatever is the latest
version of Cocoon.

Instructions for use:

1) In the Forrest root directory, run:

  patch -p0 < etc/cocoon_upgrade/cocoon_upgradepatch*

Which will make any required patches to the Forrest sources.

2) Edit upgrade_cocoon_jars.sh and modify the variables at the top of the file.
You'll need to specify locations for Cocoon, Nekopull and Nekodtd.  Also,
decide what 'type' of upgrade you wish to perform:
 - 'testing': Copies upgraded jars to build/dist/shbat/*.  This is for testing
   a Cocoon upgrade, and is the default
 - 'real': Copies upgraded jars to their official lib/* locations, and 'cvs
   add's them.

3) Run build.sh to build a regular Forrest distribution

-- 
jefft@apache.org
$Revision: 1.5 $ $Date: 2003/05/29 09:01:55 $
