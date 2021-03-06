# Dukes of Hazzard self driving RC car
[![Join the chat at https://gitter.im/rc-dukes/community](https://badges.gitter.im/rc-dukes/community.svg)](https://gitter.im/rc-dukes/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Travis (.org)](https://img.shields.io/travis/rc-dukes/dukes.svg)](https://travis-ci.org/rc-dukes/dukes)
[![codecov](https://codecov.io/gh/rc-dukes/dukes/branch/master/graph/badge.svg)](https://codecov.io/gh/rc-dukes/dukes)
[![GitHub issues](https://img.shields.io/github/issues/rc-dukes/dukes.svg)](https://github.com/rc-dukes/dukes/issues)
[![GitHub issues](https://img.shields.io/github/issues-closed/rc-dukes/dukes.svg)](https://github.com/rc-dukes/dukes/issues/?q=is%3Aissue+is%3Aclosed)
[![GitHub](https://img.shields.io/github/license/rc-dukes/dukes.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![jpoint](http://wiki.bitplan.com/images/wiki/thumb/7/78/JpointLogo.png/100px-JpointLogo.png)](https://www.jpoint.nl/)
[![openvalue](http://wiki.bitplan.com/images/wiki/thumb/a/a4/OpenValueLogo.png/113px-OpenValueLogo.png)](https://www.openvalue.nl)
[![BITPlan](http://wiki.bitplan.com/images/wiki/thumb/3/38/BITPlanLogoFontLessTransparent.png/120px-BITPlanLogoFontLessTransparent.png)](http://www.bitplan.com)

## Videos
[![Build a self driving RC car](http://img.youtube.com/vi/OL0vg1WmI6I/mqdefault.jpg)](http://www.youtube.com/watch?v=OL0vg1WmI6I "Building a self driving RC car")[![Build a self driving RC car](http://img.youtube.com/vi/YeUMtQyvZKM/mqdefault.jpg)](http://www.youtube.com/watch?v=YeUMtQyvZKM "Building a self driving RC car")
[![General Lee](http://img.youtube.com/vi/pUZtAK5jjyE/mqdefault.jpg)](https://www.youtube.com/watch?v=pUZtAK5jjyE&t=18 "General Lee")

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
| [server](https://rc-dukes.github.io/dukes/dukes/apidocs/org/rcdukes/server/package-summary.html)| [Boars Nest](https://www.thedukesofhazzard.nl/georgia-filming-locations/oxford-area/the-boars-nest/)    | Runner   | laptop  | Runner to start the cluster on the laptop |
| [car](https://rc-dukes.github.io/dukes/dukes/apidocs/org/rcdukes/car/package-summary.html)           | [Bo](https://en.wikipedia.org/wiki/The_Dukes_of_Hazzard#Bo)            | Verticle | car     | Processing Car control inputs
| [webcontrol](https://rc-dukes.github.io/dukes/dukes/apidocs/org/rcdukes/webcontrol/package-summary.html)    | [Boss Hogg](https://en.wikipedia.org/wiki/Boss_Hogg)     | Web      | laptop  | Application to provide manual inputs
| [roi](https://rc-dukes.github.io/dukes/dukes/apidocs/org/rcdukes/roi/package-summary.html)           | [Cletus](https://en.wikipedia.org/wiki/The_Dukes_of_Hazzard#Cletus)        | Library  |         | Region of Interest
| [camera](https://rc-dukes.github.io/dukes/dukes/apidocs/org/rcdukes/camera/package-summary.html) | [Cooter](https://en.wikipedia.org/wiki/The_Dukes_of_Hazzard#Cooter)        | Library  |         | CameraMatrix and PerspectiveShift
| [detect](https://rc-dukes.github.io/dukes/dukes/apidocs/org/rcdukes/detect/package-summary.html)        | [Daisy](https://en.wikipedia.org/wiki/The_Dukes_of_Hazzard#Daisy)         | Verticle |         | Detectors - e.g. Lane and StartLight
| [remotecar](https://rc-dukes.github.io/dukes/dukes/apidocs/org/rcdukes/remotecar/package-summary.html)     | [Duke Farm](https://www.thedukesofhazzard.nl/georgia-filming-locations/loganville-area/duke-farm/)     | Runner   | car     | Runner to start the cluster on the car
| [geometry](https://rc-dukes.github.io/dukes/dukes/apidocs/org/rcdukes/geometry/package-summary.html)      | [Enos](https://en.wikipedia.org/wiki/The_Dukes_of_Hazzard#Enos) | Library  |         | Geometry
| [watchdog](https://rc-dukes.github.io/dukes/dukes/apidocs/org/rcdukes/watchdog/package-summary.html)      | [Flash](https://en.wikipedia.org/wiki/The_Dukes_of_Hazzard#Flash)         | Verticle | car     | Heartbeat controller watchdog     
| [drivecontrol](https://rc-dukes.github.io/dukes/dukes/apidocs/org/rcdukes/drivecontrol/package-summary.html)  | [General Lee](https://en.wikipedia.org/wiki/General_Lee_(car))   | Library  |         | RC Car/Engine/Steering
| [common](https://rc-dukes.github.io/dukes/dukes/apidocs/org/rcdukes/common/package-summary.html)        | [Hazard County](https://en.wikipedia.org/wiki/Hazzard_County,_Georgia) | Library  |         | Lib containing common API classes
| [action](https://rc-dukes.github.io/dukes/dukes/apidocs/org/rcdukes/action/package-summary.html)        | [Luke ](https://en.wikipedia.org/wiki/The_Dukes_of_Hazzard#Luke)         | Verticle |         | Proc Image analysis results into actions
| [imageview](https://rc-dukes.github.io/dukes/dukes/apidocs/org/rcdukes/imageview/package-summary.html)     | [Rosco](https://en.wikipedia.org/wiki/Sheriff_Rosco_P._Coltrane)        | Runner   |         | lane detection debug image web server
| [app](https://rc-dukes.github.io/dukes/dukes/apidocs/org/rcdukes/app/package-summary.html)           | [Uncle Jesse](https://en.wikipedia.org/wiki/The_Dukes_of_Hazzard#Jesse)   | JavaFX   | laptop  | JavaFX app to monitor the car

## Getting started
see [Deployment](http://wiki.bitplan.com/index.php/Self_Driving_RC_Car#Deployment) in the Wiki
