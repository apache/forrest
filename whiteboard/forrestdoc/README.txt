See http://issues.apache.org/jira/browse/FOR-820
for explanation and links to some of the email discussion.

Note that forrestdoc is not yet used at all by 'forrest'.
It was simply copied from the now defunct "Apache Jakarta Alexandria".

------------------------------------------------------------------------

  build project
  
and see Forrestdoc generate documentation for itself in build/project

  build ws
  
and see Forrestdoc generate documentation for the workspace it's put in
(workspace/xml-forrest/scratchpad/forrestdoc -> generate for all projects in workspace/)

to set a workspace dir manually:

 -Dws.dir=
 
to set a project dir manually:
 
 -Dproject.dir=
