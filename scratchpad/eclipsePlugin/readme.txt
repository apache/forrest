This is a plugin for Eclipse. It provides some basic funtionality for Forrest.

Currently implemented features include:

- Start and Stop Forrest
- Build a Forrest site
- Seed a new Project

============
Installation
============

To use the plugin you need to install it within Eclipse V3.0 or later.

Although not required we recommend that you also install the Web Tools Platform
Project (http://www.eclipse.org/webtools/index.html) which will provide many
useful plugins such as XML and Schema editors, Browser View and much more.

For Development
===============

If you want to help develop this plugin you need to import it as a project in
Eclipse, the easiest way is to use the Subclipse  SVN Browser plugin from 
http://subclipse.tigris.org and do a "Check out as...")

Executing the Plugin
--------------------

- Select Run | Run ...

- Set up a new "Run Time Workbench" configration for the plugin

- Run this configuration, a new version of Eclipse will start and there will 
be a Forrest menu and a couple of toolbar buttons for starting and stoppng 
forrest (firt time you run it you need to set up some preferences, click 
Window | preferences there is an option for Forrest there.

For General Use
===============

If you want the plugin to start whenever you start Eclipse you need to 
build and deploy the plugin:

- Import as above

- Select File | Export

- Select Deployable Plugins and Fragments and follow the wizard (easiest 
to create directory structure directly in the eclipse installation 
directory).

- Restart Eclipse