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

- download Eclipse 3.0 (you need the full SDK or, for a smaller 
  download the eclipse platform) 
  - NOTE: 3.1 Milestone releases are not currently supported
  - NOTE: the platform download does not include the java development plugins

- extract to your chosen directory (don't forget to make this different 
  to you normal working directory - we'll call this installation forrestEclipse)

- run forrestEclipse

- install some required plugins
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
      - Graphical Editing FrameworkXSD Sxhema Infoset Model
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
    - If any are disableed then enable them by selecting them and clicking "Enable"
      in the right pane

- compile the Forrest Eclipse plugin into your forrestEclipse
  - If you installed the full Eclipse SDK this can be done from within 
    forrestEclipse, otherwise you will need to do it from within your main 
    Eclipse installation
  - import the FORREST_HOME/tools/eclipse project into your workspace
    - File -> import -> Existing Project into workspace
    - browse to FORREST_HOME/tools/eclipse
    - click "OK"
    - click "finish"
  - Select File | Export
  - Select Deployable Plugins and Fragments and follow the wizard (easiest 
    to create directory structure directly in the forrestEclipse installation 
    directory, that is make the destination directory your forestEclipse install
    directory).
  
- install the IBM code drop for the WTP project (this is the pre-alpha stuff)
  - http://download.eclipse.org/webtools/downloads/initial-contributions/ibm.zip
  - extract the zip to a temp directory
  - extract tmp/com.ibm.wtp.sdk-I-200407201920.zip to the same temp directory
  - copy or move the contents of temp/eclipse to your forrestEclipse directory

- restart forrestEclipse

- ensure Forrest eclipse plugin is installed
  - Help -> About -> Plug-in Details
  - look for org.apache.forrest.eclipse in the list of installed plugins

===============
Getting Started
===============

Create a project
----------------
- File -> New -> Project
- Forrest -> Seed New Project

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

(NOTE: currently broken)

Build Site
----------
- right click on Forrest project
- select site -> build


============
Known Issues
============

- If a project name has a space in it then the seeding of the project fails (problem with Forrest core?)