             Upgrading Forrest's Cocoon
             --------------------------

This directory contains files to help upgrade Cocoon to whatever is the latest
version of Cocoon.

Instructions for use:

0) Follow the instructions to prepare and build a recent Cocoon:
http://issues.apache.org/wiki/apachewiki.cgi?ForrestUpgradeLibs

1) In the Forrest root directory, run:

  patch -p0 < etc/cocoon_upgrade/cocoon_upgradepatch*

Which will make any required patches to the Forrest sources.

2) Edit upgrade_cocoon_jars.sh and modify the variables at the top of the file.
(Beware: This has not yet been updated for the recent Forrest.
 So it is probably better to use "testing" and do the rest manually.)
You'll need to specify locations for Cocoon, Nekopull and Nekodtd.  Also,
decide what 'type' of upgrade you wish to perform:
 - 'testing': Copies upgraded jars to build/dist/shbat/*.  This is for testing
   a Cocoon upgrade, and is the default
 - 'real': Copies upgraded jars to their official lib/* locations, and 'cvs
   add's them.

3) Run build.sh to build a regular Forrest distribution
