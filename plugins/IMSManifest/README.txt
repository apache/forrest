IMSManifest Plugin
===================

This plugin allows IMS SCORM Manifest files to be used as an alternative to the 
site.xml and tabs.xml files. If a file named imsmanifest.xml is found in 
the xdocs directory of a project this will be used to generate the reqruied
files for Forrest to run.

Also allows content from another Content Package defiend with an IMS Manifest 
to be included within a second Content Package.

Known Issues
============

- location of repository is hard coded in imsmanifest2site.xsl and sitemap.xsl

Version
=======

0.1-dev

Code, interfaces and functionality are likely to change. Use at your own risk.

ToDo
====

- remove hard coded references to the repository

- check there is not already a way of encoding embedded objects in an IMS Manifest

- provide and example

- documentation
  

