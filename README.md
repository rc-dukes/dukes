# Dukes of Hazard self driving car

[![Travis (.org)](https://img.shields.io/travis/rc-dukes/dukes.svg)](https://travis-ci.org/rc-dukes/dukes)
[![Coverage](https://coveralls.io/repos/github/rc-dukes/dukes/badge.svg?branch=master)](https://coveralls.io/github/rc-dukes/dukes?branch=master)
[![GitHub issues](https://img.shields.io/github/issues/rc-dukes/dukes.svg)](https://github.com/rc-dukes/dukes/issues)
[![GitHub issues](https://img.shields.io/github/issues-closed/rc-dukes/dukes.svg)](https://github.com/rc-dukes/dukes/issues/?q=is%3Aissue+is%3Aclosed)
[![GitHub](https://img.shields.io/github/license/rc-dukes/dukes.svg)](https://www.apache.org/licenses/LICENSE-2.0)

## Videos
[![Build a self driving RC car](http://img.youtube.com/vi/OL0vg1WmI6I/0.jpg)](http://www.youtube.com/watch?v=OL0vg1WmI6I "Building a self driving RC car")
[![Build a self driving RC car](http://img.youtube.com/vi/YeUMtQyvZKM/0.jpg)](http://www.youtube.com/watch?v=YeUMtQyvZKM "Building a self driving RC car")

## Documentation
* [Wiki](http://wiki.bitplan.com/index.php/Self_Driving_RC_Car)
* [dukes Project pages](https://rc-dukes.github.io/dukes)

## Modules
![](https://upload.wikimedia.org/wikipedia/commons/thumb/6/6d/General_lee.jpg/420px-General_lee.jpg)

The self driving car consists of modules with the following responsibilities:


| module        | nickname      | type     | runs on | responsibility                            |  
|---------------|---------------|----------|---------|-------------------------------------------|  
| server        | [Boars Nest](https://www.thedukesofhazzard.nl/georgia-filming-locations/oxford-area/the-boars-nest/)    | Runner   | laptop  | Runner to start the cluster on the laptop |
| car           | [Bo](https://en.wikipedia.org/wiki/The_Dukes_of_Hazzard#Bo)            | Verticle | car     | Processing Car control inputs
| webcontrol    | [Boss Hogg](https://en.wikipedia.org/wiki/Boss_Hogg)     | Web      | laptop  | Application to provide manual inputs
| roi           | [Cletus](https://en.wikipedia.org/wiki/The_Dukes_of_Hazzard#Cletus)        | Library  |         | Region of Interest
| camera-matrix | [Cooter](https://en.wikipedia.org/wiki/The_Dukes_of_Hazzard#Cooter)        | Library  |         | CameraMatrix and PerspectiveShift
| detect        | [Daisy](https://en.wikipedia.org/wiki/The_Dukes_of_Hazzard#Daisy)         | Verticle |         | Detectors - e.g. Lane and StartLight
| remotecar     | [Duke Farm](https://www.thedukesofhazzard.nl/georgia-filming-locations/loganville-area/duke-farm/)     | Runner   | car     | Runner to start the cluster on the car
| geometry      | [Enos](https://en.wikipedia.org/wiki/The_Dukes_of_Hazzard#Enos) | Library  |         | Geometry
| watchdog      | [Flash](https://en.wikipedia.org/wiki/The_Dukes_of_Hazzard#Flash)         | Verticle | car     | Heartbeat controller watchdog     
| drivecontrol  | [General Lee](https://en.wikipedia.org/wiki/General_Lee_(car))   | Library  |         | RC Car/Engine/Steering
| common        | [Hazard County](https://en.wikipedia.org/wiki/Hazzard_County,_Georgia) | Library  |         | Lib containing common API classes
| action        | [Luke ](https://en.wikipedia.org/wiki/The_Dukes_of_Hazzard#Luke)         | Verticle |         | Proc Image analysis results into actions
| imageview     | [Roscoe](https://en.wikipedia.org/wiki/Sheriff_Rosco_P._Coltrane)        | Runner   |         | lane detection debug image web server
| app           | [Uncle Jesse](https://en.wikipedia.org/wiki/The_Dukes_of_Hazzard#Jesse)   | JavaFX   | laptop  | JavaFX app to monitor the car

### Inspiration for naming
* [Dukes of Hazzard](https://en.wikipedia.org/wiki/The_Dukes_of_Hazzard)


## Getting started
- run 'install.sh'
- create a "~/dukes/dukes.ini" file with your settings, example:
```
#
# rc-dukes configuration file
# see https://github.com/rc-dukes/dukes
#
targetHost=10.9.8.7
targetUser=pi
```
- run './deploy.sh -m -s' in module 'rc-remotecar'
- start 'CarServer' in module 'rc-server' in IDE
- point your browser to http://localhost:8080
