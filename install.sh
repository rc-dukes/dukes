#!/bin/sh
# deploy the dukes software
# WF 2019-09-18: check opencv.jar

# precompiled opencv library
opencv=opencv-330.jar

# target computer
targetUser=pi
targetHost=10.9.8.7
target=targetUser@targetHost

#
# display the given error message
#
error() {
  local l_msg="$1"
  # echo to stderr
  echo "$l_msg" 1>&2
}

#
# check that opencv is in place
#
checkopencv() {
  for module in daisy cletus roscoe cooter
  do
    jar=$module/src/main/resources/lib/osx-bj/$opencv
    if [ ! -f $jar ]
    then
      error "$jar is missing"
    fi
  done
}

checkopencv
mvn clean install

