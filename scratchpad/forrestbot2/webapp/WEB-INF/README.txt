                            Forrestbot web interface

                                jefft@apache.org
                               dave@brondsema.net
                 $Revision: 1.1 $ $Date: 2003/11/20 20:25:13 $
                            ------------------------

This webapp implements a website staging application for sites built with
Apache Forrest. The webapp hosts HTML for a number of managed websites.  The
user can regenerate these sites at will.  Once the user is satisfied with the
site, they may 'deploy' the site, making it live.

This webapp has the loosest of couplings with the backend engine which is
expected to do the real build/deploy work: instructions are simply written to
the WEB-INF/commands file.  It is expected that an external app will be polling
this file for input.  See ../../scripts/README.txt for more info.

Installation
------------

Set up WEB-INF/settings.properties and WEB-INF/users.properties.  These files are self-documented.

Simply point Tomcat (or another servlet container) at this directory. Eg in
server.xml:

<Context path="/forrestbot" docBase="/home/jeff/apache/xml/xml-forrest/src/resources/forrestbot/webapp" debug="0" reloadable="true"/>

Note: this webapp will not work with a standard Apache/Tomcat mod_jk
connection.  If you look at web.xml you will see why: a number of virtual paths
(/site/*, /refresh/*, /logs/refresh) are used, and mod_jk does not forwards
requests for these paths on to Tomcat.  These need to be added by hand to
httpd.conf.  Alternatively, use a more intelligent connector like mod_webapp.
