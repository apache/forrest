  Copyright 2002-2004 The Apache Software Foundation or its licensors,
  as applicable.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.


XXE Forrest Config
==================

Works with:
===========
* XXE V2.5p3+
* Forrest 0.5+


Upgrading
=========
* Delete the 'forrest' directory from the XXE application config directory. (e.g. D:\Program Files\XMLmind_XML_Editor\config)
  * if you have installed the config in your XXE user directory (see below), this step is not necessary
* Install normally


Installing
==========
* For XXEv2.5p3+
  * Extract into the XXE application config directory (e.g. D:\Program Files\XMLmind_XML_Editor\config)
    OR
  * Extract into your XXE user directory (e.g. ~/.xxe/addon/config) (only in version 1.3+ of this config)
    From version 1.3, this is the recommended installation location, as it permits upgrading XXE without having to reinstall this config
* For XXEv2.5p2 or prior you also need to do
  * Open XXE
  * select Options > Options > Schema > Add File
  * Specify the XML Catalog file:
  * "[FORREST_HOME]/main/webapp/resources/schema/catalog.xcat" (where [FORREST_HOME] is the value of your FORREST_HOME environment variable)

Building
========
To build the configuration:
* set the FORREST_HOME environment variable
* run 'ant' in this directory

History
=======
1.3:
----
- Fixed a bug that prevented this config to work with XXE 3.x (FOR-720)
- Fixed a bug that prevented correct loading of the common css stylesheet (FOR-581)
- Changed icon references to be installation independent (FOR-581)
- Added a Forrest menu, with more robust table manipulation, and for v2 docs some link traversals (both taken from the XXE XHTML config)
- Added more entries to the Table button (menu) in the Forrest toolbar, replicating the entries in the Forrest menu
- Added a History section to the README and documentation

References
==========
XMLmind XML Editor
    http://www.xmlmind.com/xmleditor/
Apache Forrest
    http://forrest.apache.org/
Instructions for other Forrest XML Editors
    http://forrest.apache.org/docs/catalog.html
XXE Custom Configuration Info
    http://www.xmlmind.com/xmleditor/_distrib/docs/configure/index.html
