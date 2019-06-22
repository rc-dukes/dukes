#!/bin/bash
# deploy the dukes software
# WF 2019-09-18: check opencv.jar

# target computer
targetUser=pi
targetHost=10.9.8.7
target=$targetUser@$targetHost

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
  echo "$(basename $0) [-d|-h|-m|-s]"
  echo ""
  echo "  -d |--debug             : debug this script"
  echo "  -m |--maven             : run maven install"
  echo "  -h |--help              : show this usage"
  echo "  -s |--start             : deploy and start duke-farm fat jar"
  exit 1
}

#
# copy the fat java file to the target machine
# 
#  param 1: the ini file to use
#  param 2: the iniPath to use
#
copyToTarget() {
  local l_ini="$1"
  local l_iniPath="$2"
  jar="duke-farm-1.0-SNAPSHOT-fat.jar"
  color_msg $blue "copying $jar to $target"
  if [ ! -f target/$jar ]
  then
    error "$jar is missing - you might want to run $0 -m"
    exit 1
  else
     # create the properties path on the remote computer
     rinipath=$(dirname $l_ini)
     ssh $target mkdir -p "$rinipath" 
     # copy the properties file
     scp "$l_iniPath" "$target:$l_ini"
     # sync the fat file
     rsync -avz --progress target/$jar $target:~
     color_msg $blue "starting duke-farm"
     #ssh $target -t "killall -9 java; screen sh -c \"/usr/bin/java -jar $jar\""
     ssh $target -t "killall -9 java;/usr/bin/java -jar $jar"
  fi
}

#
# from the given properties file get the given value
#   param 1: ini file
#   param 2: name of property
#   returns: value of property or fails with error message
#
getValue() {
  local l_ini="$1"
  local l_name="$2"
  cat $l_ini | egrep -v "^#" | grep "^$l_name=" | cut -f2 -d "="
}

#
# start the farm
#
startFarm() {
  ini=.dukes/dukes.ini
  inipath=$HOME/$ini 
  if [ -f $inipath  ]
  then
    targetUser=$(getValue $inipath targetUser)
    targetHost=$(getValue $inipath targetHost)
    target=$targetUser@$targetHost
  else
    color_msg $red "configuration file $inipath is missing - will use $target as target"
  fi

  # ping intranet assuming 500 msecs max response time
  color_msg $blue "trying to ping $targetHost for 500 msecs"
  ping -i 0.5 -c 1 -W 500 $targetHost > /dev/null
  if [ $? -ne 0 ]
  then
    error "target host $targetHost is not reachable\nYou might want to create $ini or modify the deploy.sh script"
    exit 1
  else
    copyToTarget "$ini" "$inipath"
  fi
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

    -m|--maven)
       color_msg $blue "starting maven install"
       ../scripts/install.sh
       ;;

    -s|--start)
       startFarm
       ;;
  esac
done
