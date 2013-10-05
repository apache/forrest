             Upgrading Forrest's Cocoon
             --------------------------

This directory contains files to help upgrade Cocoon to whatever is the latest
stable version of Cocoon-2.1 branch.

NOTE:
Make sure that you have set $COCOON_HOME like e.g.:
export COCOON_HOME=/home/me/svn/asf/cocoon-branch-2_1_X
and do a 'build clean' there.

Set the required Java version to what is specifed
as Forrest's minimum requirement in $FORREST_HOME/README.txt

-------------------------
0. cd $FORREST_HOME/etc/cocoon_upgrade

1. Keep our local.*.properties files synchronised with Cocoon's.

2. Perhaps need to update version number for Cocoon in ./build.xml file.

Step 3 and 4 can be done with "./build.sh 0"

3. Copy the cocoon supporting libraries to forrest.

   See ./build.xml where some are excluded. You will probably need to
   add/exclude others via this build.xml file.
   Remember that things will change with Cocoon and we must keep our
   build system up-to-date. Also, some of ours are newer.

    ant copy-core-libs
    ant copy-endorsed-libs
    ant copy-optional-libs
    ant copy-plugins-libs

  We use a newer Jing. Cocoon cannot permanently upgrade to that because
  they are limited to Java-1.4
  https://issues.apache.org/jira/browse/FOR-1215
  Just do this temporarily:
    cp $FORREST_HOME/lib/core/jing-20091111.jar $COCOON_HOME/lib/optional/
    rm $COCOON_HOME/lib/optional/jing-20030619.jar
    cp $FORREST_HOME/lib/core/jing-20091111.jar $COCOON_HOME/tools/lib/
    rm $COCOON_HOME/tools/lib/jing-20030619.jar
    edit $COCOON_HOME/lib/jars.xml (fix the jing entry)

4. Verify that there are not two versions of libraries within the same directory:

   cd $FORREST_HOME/lib
   svn st

5. cd $FORREST_HOME/etc/cocoon_upgrade

Steps 6 and 7 can be done with "./build.sh 1"

6. Build Cocoon core and the blocks that we need.

   $FORREST_HOME/tools/ant/bin/ant build-cocoon

7. Copy the built Cocoon jars to Forrest.

   $FORREST_HOME/tools/ant/bin/ant copy-cocoon

8.  We need to make sure there is a license.txt file for each of the
    jars that we have in the lib/* directories.

    svn status | grep '^!' | grep 'license.txt'

    If the removed license.txt file listed above matches a jar
    that we have, then revert the deletion by doing an

      svn revert some.jar.license.txt

    Otherwise, copy the relevant license.txt file from $COCOON_HOME/legal.

    Ensure that licenses are properly listed in Forrest LICENSE.txt

9.  Keep our Cocoon config files and sitemaps synchronised at main/webapp/WEB-INF/

10. cd $FORREST_HOME/main
    ./build.sh clean; ./build.sh

11. Build a regular forrest distribution and test, test test.

    At least do a 'build test'.

    The testing should consist of doing a "forrest site", "forrest run"
    and "forrest war" against existing forrest projects and also against
    new "forrest seed" sites.

12. Now do 'svn commit' for the changed/new files in forrest/lib

    NOTE: Use the Cocoon SVN revision number in your log message.
    If you forget, then follow up with 'svn propedit svn:log --revprop -r ...'

------------------------------------------------------------------------
Cleanup

* There will be a new local.blocks.properties over in your $COCOON_HOME.
Remove it to continue developing with Cocoon.

