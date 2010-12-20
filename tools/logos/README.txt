Adjust properties for existing/new entries in projects.xml and type 'ant'.
View generated/index.html and see some howto notes at the bottom.

---------------------------------------
Issues
------

* Not sure how to re-run after editing, but this works:
rm generated/my-logo.png; ant

* Unfortunately, even though it doesn't use Forrest, the Ant build file
uses batik*.jar and xercesImpl*.jar from Forrest's lib directory.

---------------------------------------
History
-------

This facility was created by 'nicolaken' in CVS committers/apachelogos
on 2003-11-13 and moved to Forrest on 2004-06-18.

---------------------------------------
