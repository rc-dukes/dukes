#!/bin/sh
# install the dukes software
# WF 2019-09-18: check opencv.jar


#
# display the given error message
#
error() {
  local l_msg="$1"
  # echo to stderr
  echo "$l_msg" 1>&2
}

# now pom.xml has a workaround for this
mvn clean install -Dmaven.javadoc.skip=true
