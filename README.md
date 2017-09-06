Dukes of Hazard self driving car
================================

The self driving car consists of modules with the following responsibilities:


| module        | type     | runs on | responsibility                    |  
|---------------|----------|---------|-----------------------------------|  
| Boars Nest    | Runner   | laptop  | Runner to start the cluster on the laptop | 
| Bo            | Verticle | car | Processing Car control inputs 
| Boss Hogg     | Web      | laptop | Application to provide manual inputs
| Cletus        |          | |
| Cooter        |          | |
| Daisy         |          | |
| Duke Farm     | Runner   | car | Runner to start the cluster on the car
| Enos          |          | |
| Flash         | Verticle | car | Heartbeat controller watchdog     
| General Lee   | Library  |  |
| Hazard County | Library  | | Lib containing common API classes 
| Luke          | Verticle |  |Processing image analysis results into actions
| Roscoe        |          | |
| Uncle Jesse   | JavaFX   | laptop |JavaFX app to monitor the car


 
- 
- 