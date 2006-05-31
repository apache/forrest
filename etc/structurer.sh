#!/bin/sh
# Licensed to the Apache Software Foundation (ASF) under one or more
# license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.


# set the current working dir as the PROJECT_HOME variable 
cygwin=false;
darwin=false;
case `uname` in
  CYGWIN*) cygwin=true ;;
  Darwin*) darwin=true
           if [ -z "$JAVA_HOME" ] ; then
             JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Home   
           fi
           ;;
esac

if [ "$cygwin" = "true" ] ; then
  PROJECT_HOME=`cygpath -w "$PWD"`
else
  PROJECT_HOME=`pwd`
fi

if [ -z "$FORREST_HOME" ] ; then
  # use the location of this script to infer $FORREST_HOME
  whoami=`basename $0`
  whereami=`echo $0 | sed -e "s#^[^/]#\`pwd\`/&#"`
  whereami=`dirname $whereami`

  # Resolve any symlinks of the now absolute path, $whereami
  realpath_listing=`ls -l $whereami/$whoami`
  case "$realpath_listing" in
    *-\>\ /*)
      realpath=`echo $realpath_listing | sed -e "s#^.*-> ##"`
      ;;
    *-\>*)
      realpath=`echo $realpath_listing | sed -e "s#^.*-> #$whereami/#"`
      ;;
    *)
      realpath=$whereami/$whoami
      ;;
  esac
  FORREST_HOME=`dirname "$realpath"`/..
  export FORREST_HOME
fi

cd $FORREST_HOME/whiteboard/plugins/org.apache.forrest.plugin.internal.structurer
$FORREST_HOME/tools/ant/bin/ant local-deploy
cd ../org.apache.forrest.plugin.output.themer
$FORREST_HOME/tools/ant/bin/ant local-deploy

