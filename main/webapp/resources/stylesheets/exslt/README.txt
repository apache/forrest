This directory is meant to hold XSL extensions from exslt.org, cf https://issues.apache.org/jira/browse/FOR-1176

For each stylesheet that we want to use, add it "as-is" to this directory, using a sub-directory structure patterned on the directories from all-exslt.zip 
(Note that adding all exslt stylesheets to Forrest is too large.) 

In the various Forrest plugins, use our "locationmap" techniques to refer to the relevant stylesheet. 

See the "EXSLT" example in the forrest seed-sample site for documentation and demonstration.

Please note that these stylesheets that are distributed with Forrest must be
unmodified copies (see http://www.apache.org/legal/resolved.html#cc-sa).
If you need changes then please contribute to exslt.org and then follow on to
update our unmodified copies.
