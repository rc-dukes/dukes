#!/bin/sh
workdir=`pwd`

cd $workdir && cd ../bo && mvn clean install
cd $workdir && cd ../flash && mvn clean install

cd $workdir

mvn clean package
ls target/
scp target/duke-farm-1.0-SNAPSHOT-fat.jar pi@10.9.8.7:~
ssh pi@10.9.8.7 -t "killall -9 java; screen sh -c \"java -jar duke-farm-1.0-SNAPSHOT-fat.jar\""


