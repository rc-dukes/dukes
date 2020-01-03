package nl.vaneijndhoven.dukes.common;

/**
 * nicknames used for DukesVerticles
 */
public enum Characters {

  BO("car","Processing Car control inputs","Lost sheep Bo","main character, driver, act first think later"), 
  LUKE("action","Proc Image analysis results into actions","Lost sheep Luke","main character, sidekick, planner"), 
  DAISY("detect","Detectors - e.g. Lane and StartLight", "Bo Peep","sister of main characters, pretty, gathers intel"), 
  UNCLE_JESSE("app","JavaFX app to monitor the car","Shepherd"," guardian of main characters, wise man"), 
  ROSCOE("imageview","lane detection debug image web server","Red Dog","comical sheriff, chasing, illegal speed traps, “hot pursuit“"), 
  FLASH("watchdog","Heartbeat controller watchdog","Velvet ears","Roscoes dog"), 
  BOSS_HOGG("webcontrol","Application to provide manual inputs","Little fat buddy","villain, rich, fat, food lover “get them duke boys“"), 
  COOTER("camera-matrix","CameraMatrix and PerspectiveShift","Crazy Cooter","Mechanic, supports main characters, ”Breaker one, Breaker one, I might be crazy but I ain't dumb, Craaaazy Cooter comin' atcha, come on.”"), 
  ENOS("geometry","Dipstick","Geometry","roscoes sidekick, loves daisy, helps the dukes"), 
  CLETUS("roi","Region of Interest","Cletus","roscoes sidekick, loves daisy");

  private String module;
  private String purpose;
  private String callsign;
  private String comment;

  Characters(String module,String purpose,String callsign,String comment) {
    this.module=module;
    this.purpose=purpose;
    this.callsign = callsign;
    this.comment=comment;
  }

  public String getCallsign() {
    return callsign;
  }
}
