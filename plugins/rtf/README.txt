STATUS
------

This plugin is pre-alpha, as such it may change considerably without warning.

KNOWN PROBLEMS
--------------

Generating the samples/minimal.rtf file in the enclosed documentation project
works fine. This file is provided as an fo source file. Generating index.rtf, 
which uses the skin to create an fo file from the DocumentV* source currently
fails.

INSTALLATION
------------

Installation of this plugin is not automatic at this time. This is because there
is no release of the plugin as yet. In order to install it follow these simple 
steps:

- copy the RTF plugin directory to forrest/build/plugins
- copy the WEB-INF directory to forrest/main/webapp
- add "rtf" (without quotes) to your projects "project.required.plgins"
  property
