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
* Extract the war to a temporary directory
* Modify welcome.local.html
* Modify osuser.xml if you want to use an authenticator other than XML files
* Modify projects.xml, groups.xml, and users.xml according to your needs
* Modify settings.properties according to the server's specific setup
* Repackage the war file
* Deploy the war file in Tomcat or some other servlet container


TODO Wishlist
-------------
* seperate authorization of 'build' and 'deploy'
* log everything, not just the build
* view old log files
* ability to reload all config files on the fly
* put date at top of viewlog_body page


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

2. Can I do anything with this besides just running forrest?

Sure.  Create a wrapper script and specify it as forrest-exec in settings.properties.  Here's an example:

#!/bin/bash

# remove env values set from container 
PATH=/usr/bin
CLASSPATH=
# get env vars
. /home/user/.profile

# group writable for easier sharing of files with others who run forrestbot
umask g+w

# you can preprocess something
xmlfile=$2
if [ "${xmlfile##*.}" = "xml" ]                # everything after last .
then
    target=$3
    if [ "$target" != "deploy" ]                # don't run snap on a deploy
    then
        projectTarget=${xmlfile%.*}               # everything before last .
	# do something special for $projectTarget
    fi
else
        echo "Syntax: forrest_wrapper.sh -f myapp.xml [build|deploy|...]"
        exit
fi

# run forrest
forrest $*

# you can postprocess something