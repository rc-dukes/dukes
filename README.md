Dukes of Hazard self driving car
================================

[![GitHub issues](https://img.shields.io/github/issues/rc-dukes/dukes.svg)](https://github.com/rc-dukes/dukes/issues)
[![GitHub issues](https://img.shields.io/github/issues-closed/rc-dukes/dukes.svg)](https://github.com/rc-dukes/dukes/issues/?q=is%3Aissue+is%3Aclosed)

The self driving car consists of modules with the following responsibilities:


| module        | type     | runs on | responsibility                    |  
|---------------|----------|---------|-----------------------------------|  
| Boars Nest    | Runner   | laptop  | Runner to start the cluster on the laptop |
| Bo            | Verticle | car     | Processing Car control inputs
| Boss Hogg     | Web      | laptop  | Application to provide manual inputs
| Cletus        |          |         | Region of Interest
| Cooter        |          |         | CameraMatrix and PerspectiveShift
| Daisy         |          |         | Detectors - e.g. Lane and StartLight
| Duke Farm     | Runner   | car     | Runner to start the cluster on the car
| Enos          |          |         | Geometry
| Flash         | Verticle | car     | Heartbeat controller watchdog     
| General Lee   | Library  |         | Car/Engine/Steering
| Hazard County | Library  |         | Lib containing common API classes
| Luke          | Verticle |         | Processing image analysis results into actions
| Roscoe        |          |         | lane detection debug image web server
| Uncle Jesse   | JavaFX   | laptop  | JavaFX app to monitor the car


Getting started
===
- run 'install.sh'
- modify deploy.sh in duke-farm to correct address of your Raspberry PI
- run 'deploy.sh' in module 'duke-farm'
- start 'boars nest' in IDE
