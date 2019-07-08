# Dukes of Hazzard self driving RC car

[![Travis (.org)](https://img.shields.io/travis/rc-dukes/dukes.svg)](https://travis-ci.org/rc-dukes/dukes)
[![Coverage Status](https://coveralls.io/repos/github/rc-dukes/dukes/badge.svg?branch=master)](https://coveralls.io/github/rc-dukes/dukes?branch=master)
[![GitHub issues](https://img.shields.io/github/issues/rc-dukes/dukes.svg)](https://github.com/rc-dukes/dukes/issues)
[![GitHub issues](https://img.shields.io/github/issues-closed/rc-dukes/dukes.svg)](https://github.com/rc-dukes/dukes/issues/?q=is%3Aissue+is%3Aclosed)
[![GitHub](https://img.shields.io/github/license/rc-dukes/dukes.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![jpoint](https://www.jpoint.nl/images/logo/jpoint-black-small.png)](https://www.jpoint.nl/)
[![openvalue](http://wiki.bitplan.com/images/wiki/thumb/a/a4/OpenValueLogo.png/225px-OpenValueLogo.png)](https://www.openvalue.nl)
[![BITPlan](http://wiki.bitplan.com/images/wiki/thumb/3/38/BITPlanLogoFontLessTransparent.png/198px-BITPlanLogoFontLessTransparent.png)](http://www.bitplan.com)

## Videos
[![Build a self driving RC car](http://img.youtube.com/vi/OL0vg1WmI6I/0.jpg)](http://www.youtube.com/watch?v=OL0vg1WmI6I "Building a self driving RC car")[![Build a self driving RC car](http://img.youtube.com/vi/YeUMtQyvZKM/0.jpg)](http://www.youtube.com/watch?v=YeUMtQyvZKM "Building a self driving RC car")
[![General Lee](http://img.youtube.com/vi/pUZtAK5jjyE/0.jpg)](https://www.youtube.com/watch?v=pUZtAK5jjyE&t=18 "General Lee")

## Documentation
* [Wiki](http://wiki.bitplan.com/index.php/Self_Driving_RC_Car)
* [dukes Project pages](https://rc-dukes.github.io/dukes)

## Modules
### Inspiration for naming
* [Dukes of Hazzard](https://en.wikipedia.org/wiki/The_Dukes_of_Hazzard)
![](https://upload.wikimedia.org/wikipedia/commons/thumb/6/6d/General_lee.jpg/420px-General_lee.jpg)

### Module Overview
The self driving car consists of modules with the following responsibilities:

| module        | nickname      | type     | runs on | responsibility                            |  
|---------------|---------------|----------|---------|-------------------------------------------|  
| [server](https://rc-dukes.github.io/dukes/dukes/apidocs/nl/vaneijndhoven/dukes/server/package-summary.html)        | [Boars Nest](https://www.thedukesofhazzard.nl/georgia-filming-locations/oxford-area/the-boars-nest/)    | Runner   | laptop  | Runner to start the cluster on the laptop |
| [car](https://rc-dukes.github.io/dukes/dukes/apidocs/nl/vaneijndhoven/dukes/car/package-summary.html)           | [Bo](https://en.wikipedia.org/wiki/The_Dukes_of_Hazzard#Bo)            | Verticle | car     | Processing Car control inputs
| [webcontrol](https://rc-dukes.github.io/dukes/dukes/apidocs/nl/vaneijndhoven/dukes/webcontrol/package-summary.html)    | [Boss Hogg](https://en.wikipedia.org/wiki/Boss_Hogg)     | Web      | laptop  | Application to provide manual inputs
| [roi](https://rc-dukes.github.io/dukes/dukes/apidocs/nl/vaneijndhoven/dukes/roi/package-summary.html)           | [Cletus](https://en.wikipedia.org/wiki/The_Dukes_of_Hazzard#Cletus)        | Library  |         | Region of Interest
| [camera-matrix](https://rc-dukes.github.io/dukes/dukes/apidocs/nl/vaneijndhoven/dukes/camera/matrix/package-summary.html) | [Cooter](https://en.wikipedia.org/wiki/The_Dukes_of_Hazzard#Cooter)        | Library  |         | CameraMatrix and PerspectiveShift
| [detect](https://rc-dukes.github.io/dukes/dukes/apidocs/nl/vaneijndhoven/detect/package-summary.html)        | [Daisy](https://en.wikipedia.org/wiki/The_Dukes_of_Hazzard#Daisy)         | Verticle |         | Detectors - e.g. Lane and StartLight
| [remotecar](https://rc-dukes.github.io/dukes/dukes/apidocs/nl/vaneijndhoven/dukes/remotecar/package-summary.html)     | [Duke Farm](https://www.thedukesofhazzard.nl/georgia-filming-locations/loganville-area/duke-farm/)     | Runner   | car     | Runner to start the cluster on the car
| [geometry](https://rc-dukes.github.io/dukes/dukes/apidocs/nl/vaneijndhoven/dukes/geometry/package-summary.html)      | [Enos](https://en.wikipedia.org/wiki/The_Dukes_of_Hazzard#Enos) | Library  |         | Geometry
| [watchdog](https://rc-dukes.github.io/dukes/dukes/apidocs/nl/vaneijndhoven/dukes/watchdog/package-summary.html)      | [Flash](https://en.wikipedia.org/wiki/The_Dukes_of_Hazzard#Flash)         | Verticle | car     | Heartbeat controller watchdog     
| [drivecontrol](https://rc-dukes.github.io/dukes/dukes/apidocs/nl/vaneijndhoven/dukes/drivecontrol/package-summary.html)  | [General Lee](https://en.wikipedia.org/wiki/General_Lee_(car))   | Library  |         | RC Car/Engine/Steering
| [common](https://rc-dukes.github.io/dukes/dukes/apidocs/nl/vaneijndhoven/dukes/common/package-summary.html)        | [Hazard County](https://en.wikipedia.org/wiki/Hazzard_County,_Georgia) | Library  |         | Lib containing common API classes
| [action](https://rc-dukes.github.io/dukes/dukes/apidocs/nl/vaneijndhoven/dukes/action/package-summary.html)        | [Luke ](https://en.wikipedia.org/wiki/The_Dukes_of_Hazzard#Luke)         | Verticle |         | Proc Image analysis results into actions
| [imageview](https://rc-dukes.github.io/dukes/dukes/apidocs/nl/vaneijndhoven/dukes/imageview/package-summary.html)     | [Roscoe](https://en.wikipedia.org/wiki/Sheriff_Rosco_P._Coltrane)        | Runner   |         | lane detection debug image web server
| [app](https://rc-dukes.github.io/dukes/dukes/apidocs/nl/vaneijndhoven/dukes/app/package-summary.html)           | [Uncle Jesse](https://en.wikipedia.org/wiki/The_Dukes_of_Hazzard#Jesse)   | JavaFX   | laptop  | JavaFX app to monitor the car


## Getting started
see [Deployment](http://wiki.bitplan.com/index.php/Self_Driving_RC_Car#Deployment) in the Wiki
