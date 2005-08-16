#!/bin/sh
echo "\$PWD: $PWD needs to be equal to \$FORREST_HOME: $FORREST_HOME"
cd $PWD/whiteboard/plugins/org.apache.forrest.plugin.internal.view/
ant local-deploy
cd ../org.apache.forrest.plugin.output.viewHelper.xhtml/
ant local-deploy
cd ../org.apache.forrest.plugin.input.viewHelper.xhtml.ls
ant local-deploy