                            Forrestbot Web Interface

                            ------------------------

This webapp implements a website staging application for sites built with
Apache Forrest. The webapp hosts HTML for a number of managed websites.  The
user can regenerate these sites at will.  Once the user is satisfied with the
site, they may 'deploy' the site, making it live.

Build
-----
To build the webapp from source, you need maven installed.  From a command prompt,
enter this directory and execute "maven war".


Installation
------------



FAQ
---------------
1. The build and/or deploy commands don't seem to work

Currently forrestbot only logs the forrest part of it's execution, not the whole
thing.  And the web interface starts a forrestbot process and doesn't watch it's 
progress directly.  So look at the debugging output for the command executed and
the working directory used.  Log into the server using the login of the Tomcat server
and cd to the working directory and then execute the command.  This will fairly
accurately simulate what the web interface does.

Also, set debug-exec=true in settings.properties and make sure log4j.properties
logs DEBUG.  This will log all the thread output.