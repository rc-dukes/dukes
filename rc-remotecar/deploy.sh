#!/bin/bash
# deploy the dukes software
# WF 2019-09-18: check opencv.jar

# target computer
targetUser=pi
targetHost=10.9.8.7
target=$targetUser@$targetHost
version=0.0.3

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
  echo "$(basename $0) [-a|-d|-h|-m|-s]"
  echo ""
  echo "  -a |--autostart         : configure the remotecar app to autostart on reboot"
  echo "  -d |--debug             : debug this script"
  echo "  -m |--maven             : run maven install"
  echo "  -h |--help              : show this usage"
  echo "  -s |--start             : deploy and start remotecar (duke) fat jar"
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
  jar="rc-remotecar-$version-jar-with-dependencies.jar"
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
# check that the target is available
#
pingCheck() {
  # ping intranet assuming 500 msecs max response time
  color_msg $blue "trying to ping $targetHost for 500 msecs"
  ping -i 0.5 -c 1 -W 500 $targetHost > /dev/null
  if [ $? -ne 0 ]
  then
    error "target host $targetHost is not reachable\nYou might want to check $ini or modify the deploy.sh script"
    exit 1
  fi
}

#
# deploy the Jar file
#
deployJar() {
  pingCheck
  copyToTarget "$ini" "$inipath"
}

#
# start the farm
#
startFarm() {
  deployJar
  color_msg $blue "starting rc-remote-car aka duke-farm"
  #ssh $target -t "killall -9 java; screen sh -c \"/usr/bin/java -jar $jar\""
  ssh $target -t "killall -9 java;/usr/bin/java -jar $jar"
}

#
# template for a desktop entry
#
desktopEntry() {
  cat << EOF
[Desktop Entry]
Encoding=UTF-8
Type=Application
Name=Dukes
Comment=Self Driving Car Client
Exec=lxterminal -e '/usr/local/lib/dukes/startRemoteCar.sh'
Type=Application
Categories=Utility
EOF
}

#
# configure the Raspberry to autostart the application
#
configureAutostart() {
  deployJar
  # create the target location
  lib="/usr/local/lib/dukes"
  color_msg $blue "creating $lib on $targetHost"
  ssh $target sudo mkdir -p $lib
  color_msg $blue "installing startRemoteCar.sh on $targetHost"
  # copy the startRemoteCar.sh script to the target location
  # see https://possiblelossofprecision.net/?p=444 for the ssh stdin handling
  script=startRemoteCar.sh
  cat << EOF | ssh $target "cat >/tmp/$script"
#!/bin/bash
# Autostart script for Raspberry see
# https://github.com/rc-dukes/dukes/issues/26
# WF 2019-06-26 - version 0.0.2
java -jar /home/$targetUser/$jar
EOF
  # move and make executable
  ssh $target sudo mv /tmp/$script $lib
  ssh $target chmod +x $lib/$script
  color_msg $blue "creating autostart desktop entry for $targetUser on $targetHost"
  # copy the autostart desktop entry
  autostart="~/.config/autostart"
  ssh $target mkdir -p $autostart
  desktopEntry | ssh $target "cat > $autostart/Dukes.desktop"
  echo "Shall I reboot $targetHost? y/n"
  read x
  case $x in
    y|Y) ssh $target sudo reboot
  esac
}

ini=.dukes/dukes.ini
inipath=$HOME/$ini
if [ -f $inipath  ]
then
  targetUser=$(getValue $inipath remotecar.user)
  targetHost=$(getValue $inipath remotecar.host)
  target=$targetUser@$targetHost
else
  color_msg $red "configuration file $inipath is missing - will use $target as target"
fi

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

    -a|--autostart)
       configureAutostart
       ;;
  esac
done
