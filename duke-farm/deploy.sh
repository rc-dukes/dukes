#!/bin/sh
# deploy the dukes software
# WF 2019-09-18: check opencv.jar

# target computer
targetUser=pi
targetHost=10.9.8.7
target=targetUser@targetHost

#
# display the given error message
#
error() {
  local l_msg="$1"
  echo "$l_msg" 1>&2
}

copyToTarget() {
  ls target/
  scp target/duke-farm-1.0-SNAPSHOT-fat.jar pi@10.9.8.7:~
  ssh pi@10.9.8.7 -t "killall -9 java; screen sh -c \"java -jar duke-farm-1.0-SNAPSHOT-fat.jar\""
}

#../install.sh

# ping intranet assuming 500 msecs max response time
echo "trying to ping $targetHost for 500 msecs"
ping -i 0.5 -c 1 -W 500 $targetHost > /dev/null
if [ $? -ne 0 ]
then
  error "target host $targetHost is not reachable\nYou might want to modify the deploy.sh script"
  exit 1
else
   copyToTarget
fi
