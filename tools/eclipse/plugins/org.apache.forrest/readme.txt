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

To use the plugin you need to install it within Eclipse V3.0 
and Apache Forrest. You will also need to ensure that 
you have the required plugins for Eclipse.

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

- add the repository http://svn.apache.org/repos/asf/forrest/trunk
  (devs use https: instead)

- use the tree browser to find forrest

- right click on the forrest folder and do a "Check out as project" 

Setting Up Eclipse
-------------------

If you do not already have eclipse you can get started by downloading the WST "All in one" 
release from http://www.eclipse.org/webtools/index.html

If you already use Eclipse then you need to install the WST project as folloes:

Installing Required Plugins
...........................

Install WST and all its prerequisites from the Web Tools Project at Eclipse.
See http://www.eclipse.org/webtools/index.html.

Verifying the Installation
..........................

If you get into trouble at any point you can use the following steps to verify the plugins 
have installed correctly.

- Check the plugins are all installed correctly
  - Help -> Software Updates -> Manage Configuration
  - Ensure the configuration dialog is set to display disabled features
    - third button from left in the toolbar should be depressed
  - You should see all the features enabled (i.e. none have a little 
    red no-entry sign on their icon)
  - If any are disabled then enable them by selecting them and clicking "Enable"
    in the right pane
  
Getting the Plugin Sources
--------------------------

Pur sources are held in an SVN repository so you will need the Subclipse plugin. DO the following:

- Help -> software updates -> find and install

- Select "search for new features to install" and click next

- click the "new remote site" button

- Add the subclipse site: http://subclipse.tigris.org/update

- click Finish

- select subclips (the book is optional, but recomended)

- click next

- accept the license terms (after reading htem)

- click next

- review and click finish when happy

The plugin will now be installed

Now that you have subclipse you can get the Forrest Eclipse plugin sources 
from our SVN.

There are currently two plugins that make up the Forrest Eclipse feature:

tools/eclipse/plugins/org.apache.forrest - the main plugin
tools/eclipse/plugins/org.apache.forrest.eclipse.servletEngine - a helper plugin for starting and stopping Jetty

The feature itself is defined in tools/eclipse/features/org.apache.forrest.eclipse.feature

You need to check out all of these out using subclipse:

- Window -> Open Perspective -> SVN repository Exploer

- right click in the SVN Repository view, select New - Add a repository

- enter the following info:
  - Url: http://svn.apache.org/repos/asf/forrest/trunk/
  - User: blank
  - password: blank

- click finish

- expand the tree to tools/eclipse/plugins

- right click on org.apache.forrest, select Check out as project

- right click on org.apache.forrest.eclipse.servletEngine, select Check out as project

- expand the tree to tools/eclipse/features

- right click on org.apache.forrest.eclipse.servletEngine, select Check out as project

- right click on org.apache.forrest.eclipse.feature, select Check out as project

[FIXME: create a project set for the checkouts]

Running in the Development Environment
--------------------------------------

To run this plugins in a development environemnt:

- right click on te forrestplugin project and select Run...

- select Eclipse Application and click the New button

- give the new configuration a sensible name and click Run

A new instance of Eclipse will start.

There is currently no Forrest perspective, so you will need 
to tell the Eclipse app that you want to open the relevant views
(Window -> show view -> Other... Forrest -> ...)

[FIXME: add a forrest perspective to open these views automatically)

(after you have done this the first time you will not need to do it
again, just click the run button).

Exporting Forrest Plugins
-------------------------

If you want to have the plugins available in your normal instance of Eclipse
you will need to deploy the plugins. To do this:

  - File -> Export -> Deployable Features
  - click Next
  - ensure org.apache.forrest.eclipse.feature is checked in the "Available Features" List
  - in the "Export Destination" section enter the directory of your Eclipse instance
  - click Finish
  
- Restart Eclipse
  

===============
Getting Started
===============

See the online documentation at http://forrest.apache.org/tools/eclipse.html
