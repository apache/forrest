             Upgrading Forrest's Cocoon
             --------------------------

This directory contains files to help upgrade Cocoon to whatever is the latest
version of Cocoon.

Instructions for use:

NOTE:
Make sure that you have set $COCOON_HOME like e.g.:
#COCOON_HOME
export COCOON_HOME=/home/thorsten/apache/cocoon-trunk/

1. cd $FORREST_HOME/etc/cocoon_upgrade

Step 2 and 3 can be done with "./build.sh 0"
2. Copy all cocoon libraries to forrest.
    ant copy-core-libs
    ant copy-endorsed-libs
    ant copy-optional-libs

3. Verify that all there is not two versions of libraries within same directory:

   cd $FORREST_HOME/lib/endorsed
   svn st
   cd $FORREST_HOME/lib/core
   svn st

4. cd $FORREST_HOME/etc/cocoon_upgrade

Steps 5 and 6 can be done with "./build.sh 1" it will create an ant property file, so you do not need to edit the build.xml.
5. edit build.xml and modify new revision
   ant build-cocoon

6. cd $FORREST_HOME/lib

7a. For each cocoon-{name}-{cocoon.version}-{cocoon.revision}.jar

svn mv cocoon-{name}-{cocoon.version}-{cocoon.OLDrevision}.jar 
cocoon-{name}-{cocoon.version}-{cocoon.NEWrevision}.jar
 
svn ci -m "prework for upgrade to {cocoon.NEWrevision}" 

7b.  ant copy-cocoon

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
