             Upgrading Forrest's Cocoon
             --------------------------

This directory contains files to help upgrade Cocoon to whatever is the latest
version of Cocoon.

Instructions for use:

1. cd $FORREST_HOME/etc/cocoon_upgrade

2. Copy all cocoon libraries.
    ant copy-core-libs
    ant copy-endorsed-libs
    ant copy-optional-libs

3. Verify that all there is not two versions of libraries within same directory:

   cd $FORREST_HOME/lib/endorsed
   svn st
   cd $FORREST_HOME/lib/core
   svn st

4. cd $FORREST_HOME/etc/cocoon_upgrade

5. ant build-cocoon 

6. ant copy-cocoon

7.  cd $FORREST_HOME/lib

8.  We need to make sure there is a license.txt file for each of the
    jars that we have in the lib/* directories.

    svn status | grep '^!' | grep 'license.txt'

    If the removed license.txt file listed above matches a jar
    that we have, then revert the deletetion by doing an

      svn revert some.jar.license.txt

    Otherwise, copy the relavent license.txt file from $COCOON_HOME/legal.

9.  Keep our Cocoon config files synchronised at main/webapp/WEB-INF/

10. cd $FORREST_HOME/main

11. Build a regular forrest distribution and test, test test.

    The testing should consist of doing a "forrest site", "forrest run"
    and "forrest war" against existing forrest projects and also against
    new "forrest seed" sites.

12. Update the cocoon "svn info" at the top of this README.txt
