#!/bin/sh


# ----- Test if ant is around ------------------------------------------------
# and bail out if it does not with a message that it is required

if [  "$ANT_HOME" = "" ] ; then
  echo "You must install Ant (http://jakarta.apache.org/ant)"
  echo "and set ANT_HOME to point at your Ant Installation directory."
  exit 1
fi


# set the current working dir as the PROJECT_HOME variable 
PROJECT_HOME=$PWD

# set the ant file to use
ANTFILE=$FORREST_HOME/forrest.build.xml

# call ant.
$ANT_HOME/bin/ant -buildfile $ANTFILE -Dproject.home=$PROJECT_HOME -Dforrest.home=$FORREST_HOME -emacs -logger org.apache.tools.ant.NoBannerLogger $@ 
