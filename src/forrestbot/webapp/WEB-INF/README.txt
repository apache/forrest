                            Forrestbot web interface

                                jefft@apache.org
                 $Revision: 1.1 $ $Date: 2003/10/21 08:38:31 $
                            ------------------------

This webapp implements a website staging application for sites built with
Apache Forrest. The webapp hosts HTML for a number of managed websites.  The
user can regenerate these sites at will.  Once the user is satisfied with the
site, they may 'publish' the site, making it live.

This webapp has the loosest of couplings with the backend engine which is
expected to do the real update/publish work: instructions are simply written to
the WEB-INF/commands file.  It is expected that an external app will be polling
this file for input.  See ../../scripts/README.txt for more info.

Installation
------------

Simply point Tomcat (or another servlet container) at this directory. Eg in
server.xml:

<Context path="/forrestbot" docBase="/home/jeff/apache/xml/xml-forrest/src/resources/forrestbot/webapp" debug="0" reloadable="true"/>

Note: this webapp will not work with a standard Apache/Tomcat mod_jk
connection.  If you look at web.xml you will see why: a number of virtual paths
(/site/*, /refresh/*, /logs/refresh) are used, and mod_jk does not forwards
requests for these paths on to Tomcat.  These need to be added by hand to
httpd.conf.  Alternatively, use a more intelligent connector like mod_webapp.
