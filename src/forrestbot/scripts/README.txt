                               Forrestbot Scripts

                 $Revision: 1.1 $ $Date: 2003/10/21 08:38:31 $
                            ------------------------

This directory contains shell scripts for using the Forrestbot from a webapp or
cron job to generate multiple sites.  Contents are as follows:

launch-overseer          # Convenience script for running 'overseer'.
overseer                 # A driver script which is used with the webapp.  Invokes 'refresh' and 'publish_livesite'
refresh                  # Main driver script. Refreshes a single site via a forrestbot script
refresh_all              # Convenience wrapper for 'refresh' which regens all sites. Suitable for cron use
local-vars               # Script defining local variables, with defaults. Frontend to local-vars-`uname -n`
local-vars-`uname -n`    # Script containing local variable definitions. MUST BE DEFINED FOR YOUR SITE
local-vars-sample        # Template local-vars-`uname -n` file, containing explanations of the variables
publish_livesite         # WIP script to commit files to the Apache xml-site/targets/forrest module
work/                    # Runtime-generated directory containing forrestbot files

Prerequisites
-------------
Apart from the xml-forrest module, the only thing you'll need to run these
scripts is 'cvsco' from www.red-bean.com/cvsutils. If this is not found in your
PATH then Forrest will not be updated from CVS, which may be what you want
anyway. See the REGEN_FORREST flag in 'refresh'.


Cron usage
----------
Create a crontab file ('crontab -e') with contents like the following

PATH=/bin:/usr/local/bin:/usr/local/sbin:/sbin:/usr/sbin:/usr/bin:/usr/serverlocal/bin
0 * * * * (/home/j/jeffturner/forrestbot/refresh_all)

This runs the 'refresh_all' script every hour, which regenerates sites listed
in ../samples/forrestbot.conf.xml

Webapp usage
------------
To use the forrestbot webapp:
1) Point your servlet container at the ../webapps directory, eg in server.xml:

<Context path="/forrestbot" docBase="/home/jeff/apache/xml/xml-forrest/src/resources/forrestbot/webapp" debug="0" reloadable="true"/>

Ensure that everything except the 'refresh' button works. Use
forrest-dev/forrest-dev for the username/password.

2) Run the 'launch-overseer' command.  The 'refresh' button in the webapp
should now trigger a regen of the selected site.

Note: You can customize the usernames/passwords in
../webapp/WEB-INF/users.properties

--Jeff
