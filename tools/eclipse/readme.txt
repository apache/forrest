This is a plugin for Eclipse. It provides some basic funtionality for Forrest.

Currently implemented features include:

- Start Forrest and launch the index page in an internal browser 
  svn del plu (Stop currently broken)
- Build a Forrest site
- Seed a new Project
- Add a new XDoc to the project


============
Installation
============

To use the plugin you need to install it within Eclipse V3.0 or later.

You will also need to install IBM Web Tools Platform contribution
(see http://www.eclipse.org/webtools/index.html).


For Development
===============

If you want to help develop this plugin you need to import it as a project in
Eclipse, the easiest way is to use the Subclipse  SVN Browser plugin from 
http://subclipse.tigris.org and do a "Check out as...")

Download Subclipse Plugin
-------------------------

- Select Help | Software updates | Find and Install | Search for new features

- Add http://subclipse.tigris.org/update as URL

Executing the Plugin
--------------------
On the context menu within resouce perspective:

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
directory otherwise you will need to manually install the generated
files as a plugin for eclipse).

- Restart Eclipse

============
Known Issues
============

- If a project name has a space in it then the seeding of the project fails (problem with Forrest core?)