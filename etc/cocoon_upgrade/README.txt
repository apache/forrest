             Upgrading Forrest's Cocoon
             --------------------------

This directory contains files to help upgrade Cocoon to whatever is the latest
version of Cocoon.

Latest Release of Cocoon that has been incorporated (svn info):

URL: https://svn.apache.org/repos/asf/cocoon/trunk
Repository UUID: 13f79535-47bb-0310-9956-ffa450edef68
Revision: 125082
Node Kind: directory
Schedule: normal
Last Changed Author: cziegeler
Last Changed Rev: 125082
Last Changed Date: 2005-01-13 16:00:06 +0000 (Thu, 13 Jan 2005)
Properties Last Updated: 2005-01-12 20:34:35 +0000 (Wed, 12 Jan 2005)


Instructions for use:

1.  cd $FORREST_HOME/etc/cocoon_upgrade

2.  Two choices here to deal with the local.*.properties files that
    cocoon uses:

    (a) cp local.build.properties $COCOON_HOME/
        cp local.blocks.properties $COCOON_HOME/

    or it may be easier to apply a patch of the differences between
    the local.*.properties files and the original properties files
    they were based on. Which brings us to option:

    (b) diff -u build.properties local.build.properties > ~/x
        cd $COCOON_HOME
        cp build.properties local.build.properties
        patch -p0 $COCOON_HOME/local.build.properties < ~/x

        cd $FORREST_HOME/etc/cocoon_upgrade
        diff -u blocks.properties local.blocks.properties > ~/x
        cd $COCOON_HOME
        cp blocks.properties local.blocks.properties
        patch -p0 $COCOON_HOME/local.blocks.properties < ~/x

3.  cd $COCOON_HOME

    Build cocoon in the usual way.

4.  cd $FORREST_HOME/etc/cocoon_upgrade

5.  Edit the upgrade_cocoon_jars.sh

    Set the environment variables in the "MUST BE OVERRIDDEN" and the
    "CAN BE OVERRIDDEN" sections.

6.  ./upgrade_cocoon_jars.sh

7.  cd $FORREST_HOME/lib

8.  We need to make sure there is a license.txt file for each of the
    jars that we have in the lib/* directories.

    svn status | grep '^!' | grep 'license.txt'

    If the removed license.txt file listed above matches a jar
    that we have, then revert the deletetion by doing an

      svn revert some.jar.license.txt

    Otherwise, copy the relavent license.txt file from $COCOON_HOME/legal.

9.  cd $FORREST_HOME/main

10. Build a regular forrest distribution and test, test test.

    The testing should consist of doing a "forrest site", "forrest run"
    and "forrest war" against existing forrest projects and also against
    new "forrest seed" sites.

11. Update the cocoon "svn info" at the top of this README.txt
