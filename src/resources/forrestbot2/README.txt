                                 Forrestbot v2
                                 =============

Introduction
------------

This directory contains an attempt at rewriting the Forrestbot using vanilla
Ant 1.6, making extensive use of the <import> task.

Motivation
----------

The old Forrestbot works by generating an Ant script with XSLT.  This script
calls targets in templates.build.xml, which in turn calls targets in
forrest.build.xml.  This scheme has two problems:

 - With three layers of indirection, this gets very hard to debug

 - It's inflexible.  We cannot define project-specific tasks.  We very much
   need this missing flexibility, because a lot of projects (notably Cocoon)
   call Forrest after customising the xdocs in their own Ant scripts.


Design
------

Instead, Forrestbot v2 uses Ant 1.6's <import> to define a hierarchy of
scripts:

                               forrest.build.xml
                                       |
                                       V
                                 forrestbot.xml
                                       |
                                       V
      xml-forrest.xml, xml-fop.xml, cocoon-site.xml, jakarta-poi.xml, ...


forrestbot.xml runs <import file="forrest.build.xml"/>, making it a logical
superset of forrest.build.xml.  Scripts for building each Forrest-using
projects are likewise supersets of forrestbot.xml

The nice thing about <import> is that overriding scripts can:

 - Redefine properties.  Our project scripts can all redefine details like the
   CVS location, where confirmation mail should go to, etc.

 - Override targets.  Two uses:
 
    - Customize the forrestbot workflow.  Want to get a project's contents
      locally instead of from CVS?  Just override the 'getsrc' method:

       <target name="getsrc" depends="local.getsrc"/>

      Similarly for 'deploy' (local, ftp, scp) and 'notify' (email or just
      <echo>), 

    - Project-specific stuff.  Cocoon needs to copy lib/jars.xml to the xdocs
      directory, xml-cocoon.xml would override the 'get-src' target from
      forrestbot.xml, and do the copy (or invoke a Cocoon script to do it).


Status
------

*Very* alpha.  Requires the latest Ant, which I don't think Forrest's is.  Only
a few project scripts implemented.


-- 
$Date: 2003/10/01 12:16:54 $ 
