                               Forrestbot Scripts

                                jefft@apache.org
                 $Revision: 1.1 $ $Date: 2002/12/07 09:21:59 $
                            ------------------------

This directory contains shell scripts for using the Forrestbot to generate
multiple sites.  Contents are as follows:

overseer                 # A driver script which is used with the webapp. Invokes 'refresh' and 'update_livesite'
refresh                  # Main driver script. Refreshes a single site via a forrestbot script
refresh_all              # Convenience wrapper for 'refresh' which regens all sites. Suitable for cron use
local-vars               # Script defining local variables, with defaults. Frontend to local-vars-`uname -n`
local-vars-`uname -n`    # Script containing local variable definitions. MUST BE DEFINED FOR YOUR SITE
update_livesite          # WIP script to commit files to the Apache xml-site/targets/forrest module
work/                    # Runtime-generated directory containing forrestbot files


In summary, if you obtain this from CVS and wish to use it locally, you must:
 - Create a file defining local variables, called local-vars-<name>, where
   <name> is output from `uname -n`. See local-vars-cocoondev.org for an example.

In addition, to run the 'update_livesite' scripts you need to install the
cvsutils scripts (http://www.red-bean.com/cvsutils/) and have them in your
PATH.  This _may_ also be a requirement for the other scripts: check for
'cvsco' usage in 'common-scripts'.

--Jeff
