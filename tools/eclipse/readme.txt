This is a plugin for Eclipse. It provides some basic funtionality for Forrest.

Currently implemented features include:

- Start Forrest and launch the index page in an internal browser 
  (Stop currently broken)
- Build a Forrest site
- Seed a new Project
- Add a new XDoc to the project
- WYSIWYG html editing

============
Installation
============

To use the plugin you need to install it within Eclipse V3.0 (V3.1M* not 
currently supported) and Apache Forrest. You will also need to ensure that 
you have the reqruied plugins for Eclipse.

Getting Forrest
---------------

In order to use the Forrest Eclipse Plugin you will need to install Forrest. Furthermore,
since there is no official release of the Forrest Eclipse plugin yet you will need the
SVN version of Forrest. If you do not already have it, now is a good time to get it (see 
below). If you do already have it then import it into your workspace in Eclipse (see 
below).

Forrest installation instructions: http://forrest.apache.org/build.html

You may want to use the Eclipse SVN plugin, subclipse:

- Select Help | Software updates | Find and Install | Search for new features

- Add http://subclipse.tigris.org/update as URL

Now you need to checkout the Forrest Project:

- open the SVN Repository Exploring perspective

- add the repository https://svn.apache.org/repos/asf/forrest/trunk

- use the tree browser to find forrest

- right click on the forrest folder and do a "Check out as project" 

Getting Eclipse
---------------

If you use Eclipse and an XML plugin as your usual editing environemnt I 
recomend that you have a completely separate installation for this work. 
The reason being there is a pre-alpha XML editor in there and it may not be 
as good as your existing environement yet (I'd love to know what is good/bad 
about it so I can feed it back to the WTP project at Eclipse). If you don't 
use eclipse for XML editing at present then just use your normal installation.

- download Eclipse 3.0 (you need the full SDK) 
  - NOTE: 3.1 Milestone releases are not currently supported

- Extract the complete tree it into a new directory
  (we named it forrestEclipse)
  
- Run forrestEclipse/eclipse.exe

Installing Required Plugins
---------------------------

- Help -> Software Updates... -> Find and Install
  - select "Search for new features to install"
  - check the selection box for "Eclipse.org update site"
  - expand the "Eclipse.org update site" tree node
  - ensure that the following  options are checked
    - EMF SDK 2.0.1
    - GEF SDK 3.0.1
    - JEM
  - click "Next"
  - wait while eclipse checks for updates
  - check (at least) the following updates
    - Eclipse Modelling Framework
    - EMF SDK
    - EMF Service Data Objects
    - Graphical Editing Framework
    - XSD Sxhema Infoset Model
  - click "Next"
  - accept the license aggreement (after reading it of course)
  - click "Next"
  - click "Finish"
  - you will be asked if you want to install various plugins, just 
    click "Install"
  - this will take a while, especially if you are on a slow connection
  - once all plugins are installed you will need to restart forrestEclipse
    
- Check the plugins are all installed correctly
  - Help -> Software Updates -> Manage Configuration
  - Ensure the configuration dialog is set to display disabled features
    - third button from left in the toolbar should be depressed
  - You should see the following features enabled (i.e. they do not have a little 
    red no-entry sign on their icon
  - If any are disabled then enable them by selecting them and clicking "Enable"
    in the right pane
  
- install the IBM code drop for the WTP project (this is the pre-alpha stuff)
  - download http://download.eclipse.org/webtools/downloads/initial-contributions/ibm.zip
  - extract the zip to a temp directory (we named it tmp)
  - In the extracted tree you will find another archive
    tmp/com.ibm.wtp.sdk-I-200407201920.zip extract its to the tmp directory
  - copy or move the contents of tmp/eclipse to your forrestEclipse directory
  
- restart forrestEclipse using the command "eclipse.exe -clean" (this will 
  reload all the plugin descriptors, you do not need the "-clean" flag in 
  subsequent restarts
  
- verify that the IBM plugins have been installed
  - Help -> Software Updates -> Manage Configuration
  - ensure com.ibm.wtp.sdk.6.0.0 is listed and does not have a little red "disabled" flag on the icon

Export Forrest Eclipse Plugin
-----------------------------

To work with the Forrest Eclipse plugin you need to first need to make it
available in your wokspace. Here's how:

Import the Forrest Eclipse plugin code into you workspace
  - File -> Import -> Existing Project into Workspace
  - Click Next
  - Click Browse and point to the "tools/eclipse" directory of your Forrest installation
  - Click Finish

If you found the right directory, "forrestplugin" will show as "Project Name"

Now export the plugin
  - File -> Export -> Deployable Plugins and Fragments
  - click Next
  - ensure org.apache.forrest.eclipse is checked in the "Available Plugins and Fragments List"
  - in the "Export Options" section select "Deploy as a directory structure"
  - in the destination section browse to your forrestEclipse installation directory
  - click Finish
  
- Restart Eclipse
  
Ensure Forrest eclipse plugin is installed
  - Help -> About -> Plug-in Details
  - look for org.apache.forrest.eclipse in the list of installed plugins
  
If you cannot see the Forrest plugin in the list you many need to start
forrestEclipse with the "-clean" command line flag. This forces Eclipse to
re-initialise its plugin database.
  
NOTE - if you intend to help develop the Forrest Eclipse plugin (please do) 
then you should be familar with using Eclipse for Plugin Development. A good
startiung point is http://www.eclipse.org/articles/index.html

===============
Getting Started
===============

Create a project
----------------
- File -> New -> Project
- Forrest -> Seed New Project
  Currently you cannot use whitespace in the project directory name.
  This should change in a future version, see http://issues.cocoondev.org/browse/FOR-398

Create new XDoc
---------------
- File -> New -> XDoc

Start Forrest
-------------
- right click on Forrest project
- select site -> start

(index page will open in embedded browser automatically)

Start Forrest
-------------
- right click on Forrest project
- select site -> stop

(NOTE: currently broken - you will have to manually kill the java process I'm afraid)

Build Site
----------
- right click on Forrest project
- select site -> build
