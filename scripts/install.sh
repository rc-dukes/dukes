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
  echo "$(basename $0) [-q|-h]"
  echo ""
  echo "  -d |--debug        : debug this script"
  echo "  -q |--quick        : no tests, no javadoc"
  echo "  -h |--help         : show this usage"
  exit 1
}

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

    -h|--help)
      usage
      ;;

    -q)
      mvn clean install -D skipTests -Dmaven.javadoc.skip=true -D gpg.skip
      ;;

    *)
      mvn clean install -Dmaven.javadoc.skip=true
      ;;
  esac
done
