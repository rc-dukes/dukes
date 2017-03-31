Dukes of Hazard self driving car
================================

The self driving car consists of modules with the following responsibilities:


| module        | type     | responsibility                    |
|---------------|----------|-------------------------------|
| Bo            | Verticle | Processing Car control inputs |
| Boss Hogg     | Web      | Application to provide manual inputs
| Cletus        |          |
| Cooter        |          |
| Daisy         |          |
| Duke Farm     | Runner   | Runner to start the cluster (needs to be split into part on the car and part on the laptop)
| Enos          |          |
| Flash         | Verticle | Heartbeat controller watchdog     |
| General Lee   | Library  | 
| Hazard County | Library  | Lib containing common API classes |
| Luke          | Verticle | Processing image analysis results into actions
| Roscoe        |          |
| Uncle Jesse   | JavaFX   | JavaFX app to monitor the car


 
- 
- 