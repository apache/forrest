This directory is meant to hold XSL extensions from exslt.org, cf https://issues.apache.org/jira/browse/FOR-1176

For each stylesheet that we want to use, add it "as-is" to this directory, using a sub-directory structure patterned on the directories from all-exslt.zip 
(Note that adding all exslt stylesheets to Forrest is too large.) 

In the various Forrest plugins, use our "locationmap" techniques to refer to the relevant stylesheet. 

Please note that these stylesheets should not be edited, rather contribute changes to exslt.org and update the copies found in Forrest.
