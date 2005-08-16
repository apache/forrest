#!/bin/sh

cd ../whiteboard/plugins/org.apache.forrest.plugin.internal.view/
ant local-deploy
cd ../org.apache.forrest.plugin.output.viewHelper.xhtml/
ant local-deploy
cd ../org.apache.forrest.plugin.input.viewHelper.xhtml.ls
ant local-deploy