             Upgrading Forrest's Cocoon
             --------------------------

This directory contains files to help upgrade Cocoon to whatever is the latest
stable version of Cocoon-2.1 branch.

NOTE:
Make sure that you have set $COCOON_HOME like e.g.:
export COCOON_HOME=/home/me/svn/asf/cocoon-branch-2_1_X
and do a 'build clean' there.

-------------------------
0. cd $FORREST_HOME/etc/cocoon_upgrade

1. Keep our local.*.properties files sychronised with Cocoon's.

2. Perhaps need to update version number for Cocoon in ./build.xml file.

Step 3 and 4 can be done with "./build.sh 0"

3. Copy the cocoon supporting libraries to forrest.

   See ./build.xml where some are excluded. You might need to add/exclude
   others via this build.xml file.
   Remember that things will change with Cocoon and we must keep in sync.

    ant copy-core-libs
    ant copy-endorsed-libs
    ant copy-optional-libs

4. Verify that there are not two versions of libraries within the same directory:

   cd $FORREST_HOME/lib/endorsed
   svn st
   cd $FORREST_HOME/lib/core
   svn st

5. cd $FORREST_HOME/etc/cocoon_upgrade

Steps 6 and 7 can be done with "./build.sh 1"

6. Build Cocoon core and the blocks that we need.

   ant build-cocoon

7. Copy the built Cocoon jars to Forrest.

   ant copy-cocoon

8.  We need to make sure there is a license.txt file for each of the
    jars that we have in the lib/* directories.

    svn status | grep '^!' | grep 'license.txt'

    If the removed license.txt file listed above matches a jar
    that we have, then revert the deletetion by doing an

      svn revert some.jar.license.txt

    Otherwise, copy the relevant license.txt file from $COCOON_HOME/legal.

9.  Keep our Cocoon config files and sitemaps synchronised at main/webapp/WEB-INF/

10. cd $FORREST_HOME/main

11. Build a regular forrest distribution and test, test test.

    At least do a 'build test'.

    The testing should consist of doing a "forrest site", "forrest run"
    and "forrest war" against existing forrest projects and also against
    new "forrest seed" sites.

12. Now do 'svn commit' for the changed/new files in forrest/lib
    and use the Cocoon SVN revision number in your log message.

------------------------------------------------------------------------
Cleanup

* There will be a new local.blocks.properties over in your $COCOON_HOME.
Remove it to continue developing with Cocoon.

