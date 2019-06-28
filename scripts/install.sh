#!/bin/sh
# install the dukes software
# WF 2019-09-18: check opencv.jar

#ansi colors
#http://www.csc.uvic.ca/~sae/seng265/fall04/tips/s265s047-tips/bash-using-colors.html
blue='\033[0;34m'
red='\033[0;31m'
green='\033[0;32m' # '\e[1;32m' is too bright for white bg.
endColor='\033[0m'

#
# a colored message
#   params:
#     1: l_color - the color of the message
#     2: l_msg - the message to display
#
color_msg() {
  local l_color="$1"
  local l_msg="$2"
  echo -e "${l_color}$l_msg${endColor}"
}

# error
#
#   show an error message and exit
#
#   params:
#     1: l_msg - the message to display
error() {
  local l_msg="$1"
  # use ansi red for error
  color_msg $red "Error: $l_msg" 1>&2
}

#
# show usage
#
usage() {
  echo "usage: $(basename $0) [-d|-f|-j|-q]* [-h]?"
  echo ""
  echo "  -d |--debug        : debug this script"
  echo "  -f |--fatjar       : create a fat jar"
  echo "  -j |--javadoc      : with javadoc (default is without)"
  echo "  -q |--quick        : no tests, no javadoc"
  echo ""
  echo "  -h |--help         : show this usage"
  exit 1
}

mvnOptions="clean install"
javaDoc="-Dmaven.javadoc.skip=true"
# commandline option
while [  "$1" != ""  ]
do
  option=$1
  shift

  # optionally show usage
  case $option in
    -d|--debug)
      set -x
      debug=true;
      ;;

    -f|--fatjar)
      mvnOptions="$mvnOptions -D createAssembly=true"
      ;;
    -j|--javadoc)
      javaDoc=""
      ;;

    -h|--help)
      usage
      exit 0
      ;;

    -q)
      mvnOptions="$mvnOptions -D skipTests -D gpg.skip"
      ;;
  esac
done
mvn $mvnOptions $javaDoc
